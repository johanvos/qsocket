/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.redfx.explore.qsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.redfx.strange.Program;
import org.redfx.strange.QuantumExecutionEnvironment;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

/**
 *
 * @author johan
 */
public class Entangler {
    
    static final int EPORT = 2022;
    private static HashMap<Long, ProgramStatus> pairs = new HashMap<>();
 
    final byte CMD_REQUEST_A = 0x1;
    final byte CMD_REQUEST_B = 0x2;
    final byte CMD_MEASURE   = 0x3;
    
    private String localHost;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    
    private AtomicLong counter = new AtomicLong(0);
    private EntangledPair myPair;
    private final Queue<EntangledPair> readyQueue = new LinkedList();
    
    public Entangler() {
        
    }
    public Entangler (String localHost, int localPort, String remoteHost, int remotePort) {
        
    }
    
    public void start() throws IOException {
        Thread t = new Thread() {
            @Override public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(EPORT);
                    boolean go = true;
                    while (go) {
                        Socket socket = serverSocket.accept();
                        processSocket(socket);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Entangler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }
    
    
    private void processSocket(Socket s) {
        Thread t = new Thread() {
            @Override public void run() {
                try {
                    InputStream is = s.getInputStream();
                    OutputStream os = s.getOutputStream();
                    boolean go = true;
                    while (go) {
                        int cmd = is.read();
                        System.err.println("COMMAND = "+cmd);
                        if (cmd == CMD_REQUEST_A) {
                            myPair = createPair();
                            synchronized (readyQueue) {
                                readyQueue.add(myPair);
                                readyQueue.notifyAll();
                            }
                            long id = myPair.getId();
                            os.write(longToBytes(id));
                            os.flush();
                        }
                        if (cmd == CMD_REQUEST_B) {
                            EntangledPair poll = readyQueue.poll();
                            while (poll == null) {
                                try {
                                    synchronized (readyQueue) {
                                        readyQueue.wait();
                                    }
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Entangler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                poll = readyQueue.poll();
                            }
                            long id = myPair.getId();
                            os.write(longToBytes(id));
                            os.flush();
                        }
                        if (cmd == CMD_MEASURE) {
                            byte[] idb = new byte[8];
                            is.read(idb);
                            long id = bytesToLong(idb);
                            int answer = measureProgram(id);
                            os.write(answer);
                            os.flush();
                           
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Entangler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }
    
    int measureProgram(long id) {
        System.err.println("measureProgram with id = "+id);
        ProgramStatus ps = pairs.get(id);
        if (ps.measurement > -1) return ps.measurement;
        QuantumExecutionEnvironment qee = new SimpleQuantumExecutionEnvironment();
        Result result = qee.runProgram(ps.program);
        result.measureSystem();
        Qubit[] qubits = result.getQubits();
        ps.measurement = qubits[0].measure();
        System.err.println("MEASURED program, return "+ps.measurement);
        return ps.measurement;
    }
    
    
    public EntangledPair createPair() {
        long idx = counter.getAndIncrement();
        Qubit a = new Qubit();
        Qubit b = new Qubit();
        EntangledPair answer = new EntangledPair(idx, a, b);
        Program p = new Program(2);
        Step step = new Step(new Hadamard(0));
        Step step2 = new Step(new Cnot(0,1));
        p.addSteps(step, step2);
        ProgramStatus ps = new ProgramStatus(p);
        pairs.put(idx, ps);
        return answer;
    }

    public byte[] oldlongToBytes(long v) {
        long left = v;
        byte[] answer = new byte[8];

        for (int i = 7; i > -1; i--) {
            answer[i] = (byte) (left & 0xFF);
            left >>= 8;
        }
        return answer;
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    public long bytesToLong(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(b.length);
        bb.put(b, 0, b.length);
        bb.flip();
        return bb.getLong();
    }
    
    class ProgramStatus {
        Program program;
        int measurement = -1;
        
        ProgramStatus(Program p) {
            this.program = p;
        }
    }
}
