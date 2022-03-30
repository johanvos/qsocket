/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.redfx.explore.qsocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author johan
 */
public class QServerSocket {
    
    private ServerSocket classicServerSocket;
    
    private Socket classicSocket;
    private Entangler entangler;
    private final InetAddress localhost;
    private final int port;
    
    public QServerSocket(int port) throws IOException {
        this.port = port;
        this.localhost = InetAddress.getLocalHost();
        this.classicServerSocket = new ServerSocket(port);
    }
    
    public QSocket accept() throws IOException {
        if (classicSocket != null) {
            throw new IOException("QServerSocket can accept one connection only");
        }
        this.classicSocket = this.classicServerSocket.accept();
        String localname = localhost.getHostName();
        System.err.println("port = " + this.classicSocket.getPort()+", lp= "+this.classicSocket.getLocalPort());
        int remotePort = this.classicSocket.getPort();
        this.entangler = new Entangler(localname, port, this.classicSocket.getLocalAddress().getHostName(), remotePort);
        return new QSocket(this.classicSocket, entangler);
    }
}
