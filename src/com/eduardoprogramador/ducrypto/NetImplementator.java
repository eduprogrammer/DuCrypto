/*
 * Copyright 2022. Eduardo Programador
 * www.eduardoprogramador.com
 * consultoria@eduardoprogramador.com
 *
 * Todos os direitos reservados
 * */

package com.eduardoprogramador.ducrypto;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class NetImplementator extends Socket {
    //declare
    public static final int SERVER_MODE = 0x41a;
    public static final int CLIENT_MODE = 0x41b;
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean isServer, isClient;
    private String host;
    private int port;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public static boolean clientAttached = false;

    //constructor
    private NetImplementator(int mode) throws Exception {
        switch (mode) {
            case SERVER_MODE:
                isServer = true;
                isClient = false;
                break;

            case CLIENT_MODE:
                isServer = false;
                isClient = true;
                break;

            default:
                throw new Exception("NetImplenentation Not Found");
        }
    }

    //methods
    public static NetImplementator getInstance(int mode) {
        try {
            return new NetImplementator(mode);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String host) {
        this.host = host;
    }

    public void initialize() throws Exception  {

            if(isServer) {
                serverSocket = new ServerSocket();
                SocketAddress socketAddress = null;
                if(host == null)
                    socketAddress = new InetSocketAddress(port);
                else
                    socketAddress = new InetSocketAddress(host,port);
                serverSocket.bind(socketAddress);
                getClient();
                clientAttached = true;
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

            } else if(isClient) {
                SocketAddress socketAddress = new InetSocketAddress(host,port);
                connect(socketAddress);
                dataInputStream = new DataInputStream(getInputStream());
                dataOutputStream = new DataOutputStream(getOutputStream());
            }
    }

    private void getClient() throws Exception {
        socket = serverSocket.accept();
    }


    public void sendBytes(byte[] bytes) throws Exception {
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);
        dataOutputStream.flush();
    }

    public void sendBytesByPieces(byte[] src) throws Exception {

        for (int i = 0; i < src.length; i++) {
            dataOutputStream.write(src[i]);
            dataOutputStream.flush();
        }
    }

    public byte[] receiveBytes() {

        try {
            int c = dataInputStream.readInt();
            byte[] bytes = new byte[c];
            if(c > 0) {
                dataInputStream.read(bytes);
                return bytes;
            } else {
                return null;
            }

        } catch (Exception ex) {
            return null;
        }
    }

    public byte[] receiveBytesByPieces() throws Exception {
        byte[] bytes = new byte[1];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (dataInputStream.read(bytes) > 0) {
            byteArrayOutputStream.write(bytes);
            byteArrayOutputStream.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }


    public void endOfConnection() throws Exception {
        dataInputStream.close();
        dataOutputStream.close();
        if(isClient)
            close();
        else if(isServer) {
            socket.close();
            serverSocket.close();
        }
    }

    public String getClientInfo() {
        return socket.getRemoteSocketAddress().toString();
    }

    public String getServerInfo() {
        return getRemoteSocketAddress().toString();
    }


    public void setOk(int mode) throws Exception {
        if(mode == CLIENT_MODE) {
            dataOutputStream.close();
            close();
        } else if(mode == SERVER_MODE) {
            socket.close();
            serverSocket.close();
        }

    }
}
