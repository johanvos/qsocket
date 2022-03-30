package org.redfx.explore.qsocket;
        
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author johan
 */
public class QSocketTest {
    
    private static final int TEST_PORT=1902;
    
    private Thread createServerSocketThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    QServerSocket qss = new QServerSocket(TEST_PORT);
                    QSocket accept = qss.accept();
                } catch (IOException ex) {
                    Logger.getLogger(QSocketTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return t;
    }
    
    @Test
    public void testTest() {
        assertTrue(1==1);
    }
    
    @Test
    public void testBit() {
       
    }
}
