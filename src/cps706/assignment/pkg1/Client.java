/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cps706.assignment.pkg1;

import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Rahul Bilimoria 500569144
 *         Tenzin Nyendak  500573810
 *         Brian Turner    500565237 
 */
public class Client {

    private final JFrame frame;
    private final JTextArea text;
    private final JLabel connected;
    private final JTextField input;
    private final JScrollPane scroll;
    private final JButton connect;

    private boolean isConnected;

    private Socket clientSocket;

    private DataInputStream dIn;
    private DataOutputStream dOut;
    
    /**
     * Creates GUI for the client object.
     */
    public Client() {
        frame = new JFrame("Client");

        text = new JTextArea(1, 50);
        text.setLineWrap(true);
        text.setEditable(false);

        scroll = new JScrollPane(text);
        connect = new JButton("Connect");
        input = new JTextField("Send something to server...");
        connected = new JLabel("Status: Not Connected.");

        isConnected = false;

        frame.setSize(400, 336);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        frame.add(scroll).setBounds(0, 0, 395, 250);
        frame.add(input).setBounds(0, 248, 395, 30);
        frame.add(connect).setBounds(290, 277, 104, 30);
        frame.add(connected).setBounds(0, 273, 130, 40);

        ActionHandler h = new ActionHandler();
        connect.addActionListener(h);
        input.addActionListener(h);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates a new client object.
     * @param args 
     */
    public static void main(String args[]) {
        Client client = new Client();
    }

    /**
     * Connects to the server localhost using the clientSocket at port 4444.
     */
    public void connectToServer() {
        try {
            text.append("Client: Connecting to server...\n");
            clientSocket = new Socket("localhost", 4444);
            dIn = new DataInputStream(clientSocket.getInputStream());
            dOut = new DataOutputStream(clientSocket.getOutputStream());
            connect.setText("Disconnect");
        } catch (Exception e) {
            text.append("Client: Could not connect to server.\n");
            text.append("Reason: Server not online.\n");
            return;
        }
        text.append("Client: Connected to server.\n");
        isConnected = true;
        connected.setText("Status: Connected.");
    }

    /**
     * Disconnects from the server.
     */
    public void disconnectFromServer() {
        try {
            text.append("Client: Disconnecting from server...\n");
            clientSocket.close();
            connect.setText("Connect");
        } catch (Exception e) {
            text.append("Client: Could not disconnect from server.\n");
            text.append("Reason: Not connected to the server.\n");
            return;
        }
        text.append("Client: Disconnected from server.\n");
        isConnected = false;
        connected.setText("Status: Not Connected.");
    }

    /**
     * Sends a request to the server depending on the byte
     * @param type a byte to send to the server
     */
    public void getDataFromServer(byte type) {
        if (!isConnected) {
            text.append("Client: Not connected to server.\n");
            return;
        }
        try {
            dOut.writeByte(type);
            dOut.flush();
        } catch (IOException e) {
            text.append("Client: Not connected to the server.\n");
        }
        try {
            switch (dIn.readByte()) {
                case 1: //date
                    text.append("Server: Todays date is " + dIn.readUTF() + "\n");
                    break;
                case 2: //time
                    text.append("Server: The current time is " + dIn.readUTF() + "\n");
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        } catch (IOException e) {}
    }
    /**
     * Class to handle the actions of the GUI objects.
     */
    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == connect) { //Connects to the server
                if (!isConnected) {
                    connectToServer();
                } else {
                    getDataFromServer((byte) 3); //Tells server to close the connection
                    disconnectFromServer();
                }
            }
            if (e.getSource() == input) {
                text.append("Client: " + input.getText() + "\n");
                if (input.getText().toLowerCase().contains("date")) {
                    getDataFromServer((byte) 1); // Requests for date
                }
                if (input.getText().toLowerCase().contains("time")) {
                    getDataFromServer((byte) 2); // Requests for time
                }
                if (!(input.getText().toLowerCase().contains("date")) && !(input.getText().toLowerCase().contains("time"))) {
                    text.append("Ask for the date or time.\n"); //If neither is asked
                }
            }
        }
    }
}
