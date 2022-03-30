package org.redfx.explore.qsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author johan
 */
public class EntanglerService {

    private OutputStream os;
    private InputStream is;
    final byte CMD_REQUEST_A = 0x1;
    final byte CMD_REQUEST_B = 0x2;
    final byte CMD_MEASURE = 0x3;
    
    public EntanglerService (String host, int port) throws IOException {
        System.err.println("Creating entanglerservice to "+host+":"+port);
        Socket socket = new Socket(host, port);
        os = socket.getOutputStream();
        is = socket.getInputStream();
        System.err.println("Created entanglerservice to "+host+":"+port);
    }
    
    public ComQubit requestEntangledQubitA() throws IOException {
        os.write(CMD_REQUEST_A);
        os.flush();
        byte[] idb = new byte[8];
        int len = is.read(idb);
        if (len != 8)throw new IOException ("couldn't read 8 bytes");
        long id = bytesToLong(idb);
        ComQubit answer = new ComQubit(this);
        answer.setId(id);
        return answer;
    }
        
    public ComQubit requestEntangledQubitB() throws IOException {
        os.write(CMD_REQUEST_B);
        os.flush();
        byte[] idb = new byte[8];
        int len = is.read(idb);
        if (len != 8)throw new IOException ("couldn't read 8 bytes");
        long id = bytesToLong(idb);
        ComQubit answer = new ComQubit(this);
        answer.setId(id);
        return answer;
    }
    
    int measureComQubit(ComQubit cq) throws IOException {
        os.write(CMD_MEASURE);
        os.write(longToBytes(cq.id));
        os.flush();
        int answer = is.read();
        System.err.println("did read measurement from entangler: "+answer);
        return answer;
    }

    public long bytesToLong(byte[] b) {
        System.err.println("len = "+b.length+" and long = "+Long.BYTES);
        ByteBuffer bb = ByteBuffer.allocate(b.length);
        bb.put(b, 0, b.length);
        bb.flip();
        return bb.getLong();
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
