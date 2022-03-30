/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.redfx.explore.qsocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johan
 */
public class Main {

    static final int PORT = 4573;
    
    QSocket alice;
    QSocket bob;
    
    CountDownLatch cdl;
    
    public static void main(String[] args) throws Exception {
        System.err.println("Hello, main");
        Main main = new Main();
        main.testSending();
    }

    public void testSending() throws IOException, InterruptedException {
        Entangler et = new Entangler();
        et.start();
        cdl = new CountDownLatch(2);
        startBob();
        startAlice();
        System.err.println("Alice and Bob started");
        cdl.await();
        System.err.println("Alice and Bob created");
        byte[] b = new byte[]{0,1,0,0,1,1,1,0};
        alice.getOutputStream().write(b);
    }

    public void startAlice() {
        Thread t = new Thread() {
            @Override
            public void run() {
                System.err.println("Starting Alice");
                try {
                    alice = new QSocket("localhost", PORT);
                    cdl.countDown();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.err.println("Started Alice");
            }
        };
        t.start();
    }

    public void startBob() {
        CountDownLatch serverStarted = new CountDownLatch(1);
        Thread t = new Thread() {
            @Override
            public void run() {
                System.err.println("Starting Bob");
                try {
                    QServerSocket qServerSocket = new QServerSocket(PORT);
                    serverStarted.countDown();
                    bob = qServerSocket.accept();
                    cdl.countDown();
                    InputStream inputStream = bob.getInputStream();
                    byte[] b = new byte[8];
                    System.err.println("will read at most 8 bytes");
                    int len = inputStream.read(b);
                    System.err.println("did read "+len+" bytes");
                    while (len > -1) {
                        System.err.println("Bob got bytes: " + Arrays.toString(b));
                        len = inputStream.read(b);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.err.println("Done Bob");
            }
        };
        t.start();
        try {
            serverStarted.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
