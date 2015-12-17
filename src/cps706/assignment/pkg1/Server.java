/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cps706.assignment.pkg1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Calendar;

/**
 *
 * @author Rahul Bilimoria 500569144
 *         Tenzin Nyendak  500573810
 *         Brian Turner    500565237 
 */
public class Server {

    private ServerSocket serverSocket;

    /**
     * Creates a new serverSocket object on port 4444
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(4444);
        } catch (Exception e) {
            System.out.println("Can't connect to port number: 4444.");
            System.exit(1);
        }
    }
    
    /**
     * Creates a new Server object and handles the connections
     * @param args
     * @throws IOException 
     */
    public static void main(String args[]) throws IOException {
        Server myServer = new Server();
        while (true) {
            Socket clientSocket = myServer.serverSocket.accept();
            myServer.newThread(clientSocket);
        }
    }

    /**
     * Creates and runs a new thread for the connected socket
     * @param clientSocket the socket connected to the client
     */
    private void newThread(Socket clientSocket){
        Thread t = new Thread(new RequestThread(clientSocket));
        t.start();
    }
    
    /**
     * Gets the current date
     * @return a String of the date
     */
    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        return ((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR));
    }

    /**
     * Gets the current time
     * @return a String of the time
     */
    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        String hour = "", minute = "", second = "";
        if (calendar.get(Calendar.HOUR) < 9) {
            hour = "0";
        }
        if (calendar.get(Calendar.MINUTE) < 9) {
            minute = "0";
        }
        if (calendar.get(Calendar.SECOND) < 9) {
            second = "0";
        }
        hour = hour + calendar.get(Calendar.HOUR);
        minute = minute + calendar.get(Calendar.MINUTE);
        second = second + calendar.get(Calendar.SECOND);
        return (hour + ":" + minute + ":" + second);
    }

    /**
     * Class to handle threads 
     */
    private class RequestThread implements Runnable {

        Socket s;
        DataInputStream dIn;
        DataOutputStream dOut;
        boolean finished;
        
        /**
         * Initializes variables for the thread
         * @param s socket that is connected to the client
         */
        public RequestThread(Socket s) {
            this.s = s;
            finished = false;
        }
        
        @Override
        /**
         * Handles the requests of the client 
         */
        public void run() {
            try {
                dIn = new DataInputStream(s.getInputStream());
                dOut = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!finished) {
                byte type;
                try {
                    type = dIn.readByte();
                    switch (type) {
                        case 1:
                            dOut.writeByte(type);
                            dOut.writeUTF(getDate());
                            dOut.flush();
                            break;
                        case 2:
                            dOut.writeByte(type);
                            dOut.writeUTF(getTime());
                            dOut.flush();
                            break;
                        case 3:
                            dOut.writeByte(type);
                            dOut.flush();
                            s.close();
                            finished = true;
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    try{
                        s.close();
                        finished=true;
                    }
                    catch(IOException ex){} 
                }
            }
        }
    }
}
