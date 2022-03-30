package org.redfx.explore.qsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author johan
 */
public class QSocket {

    private final Socket classicSocket;
    private EntanglerService entanglerService = new EntanglerService("localhost", 2022);
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public QSocket(String host, int port) throws IOException {
        this (new Socket(host, port));
    }

    public QSocket(Socket classic) throws IOException {
        this (classic, null);
    }

    QSocket(Socket classic, Entangler entangler) throws IOException {
        this.classicSocket = classic;
      //  this.entangler = entangler;
        this.inputStream = new QInputStream(this);
        this.outputStream = new QOutputStream(this);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    Socket getClassicSocket() {
        return this.classicSocket;
    }

    class QInputStream extends InputStream {
        private QSocket qSocket;
        private Socket classicSocket;
        private InputStream cis;
        
        QInputStream (QSocket qSocket) throws IOException {
            this.qSocket = qSocket;
            this.classicSocket = qSocket.getClassicSocket();
            this.cis = this.classicSocket.getInputStream();
        }
        
        @Override
        public int read() throws IOException {
            System.err.println("Reading byte, get entangled first");
            ComQubit b = entanglerService.requestEntangledQubitB();
            int measurement = b.measure();
            System.err.println("entangled measurement = "+measurement);
            int cmsg = cis.read();
            System.err.println("Got answer over wire "+cmsg);
            int answer =  measurement ^ cmsg;
            System.err.println("Originally read "+cmsg+", measurement et = "+measurement+", answer = "+answer);
            return answer;
        }
        
    }
    
    class QOutputStream extends OutputStream {
        
        private QSocket qSocket;
        private Socket classicSocket;
        private OutputStream cos;
        
        QOutputStream(QSocket qSocket) throws IOException {
            this.qSocket = qSocket;
            this.classicSocket = qSocket.getClassicSocket();
            this.cos = this.classicSocket.getOutputStream();
        }

        @Override
        public void write(int b) throws IOException {
            int e = getSenderMeasurement();
            int corr = b ^ e;
            cos.write(corr);
            cos.flush();
            System.err.println("Originally, we had " + b+" and now we have "+ corr);
        }
        
        private int getSenderMeasurement() throws IOException {
            ComQubit a = entanglerService.requestEntangledQubitA();
//            ComQubit qubit = new ComQubit();
//            if (bit == 1) qubit.applyXGate();
//            a.entangleWith(qubit);
            int answer = a.measure();
            return answer;
        }
    }
}
