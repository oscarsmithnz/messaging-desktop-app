/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Oscar
 */
public class Client implements Runnable {

    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 232;
    private Socket socket;
    private boolean finished;
    private MainGUI gui;
    private String userName;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Client() {
        finished = false;
    }

    public void run() {
        do {
            String s = readMessage();
            gui.receiveMessage(s);
        } while (!finished);
    }

    public void startClient() {
        socket = null;
        try {
            socket = new Socket(HOST_NAME, HOST_PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            ClientConnectionTester connectionTester = new ClientConnectionTester(this);
            Thread thr = new Thread(connectionTester);
            thr.start();
        } catch (IOException e) {
            System.err.println("Client unable to connect: " + e);
            System.exit(-1);
        }
    }

    public void openMainGUI() {
        gui = new MainGUI("Messaging", this);
        Thread receive_messages = new Thread(this);
        receive_messages.start();
        gui.setVisible(true);
    }

    public boolean sendMessage(Message m) {
        try {
            oos.writeObject(m);
            return true;
        } catch (IOException e) {
            System.out.println("Error sending message: " + e);
            return false;
        }
    }

    public boolean sendUserMessage(String input, File imageFile) {
        if (input.length() < 3 || !input.contains(" ") || input.charAt(0) != '@' || input.startsWith("SERVER:")) {
            return false;
        }
        
        int i = input.indexOf(' ');
        String sendTo = input.substring(1, i).trim();
        input = input.substring(i).trim();
        UserMessage m = new UserMessage(input, sendTo, userName);
        try{
        if (imageFile != null){
            BufferedImage image = ImageIO.read(imageFile);
            m.setImage(image);
            System.out.println(image.hashCode());
        }
        }catch(IOException e){
            System.out.println("Error with image reading: " + e);
            return false;
        }
        return sendMessage(m);
    }

    public String readMessage() {
        try {
            Message m;
            m = (Message) ois.readObject();
            if (m instanceof ServerMessage) {
                if (((ServerMessage) m).getOnlineUsers() != null) {
                    String[] users = ((ServerMessage) m).getOnlineUsers();
                    for (int i = 0; i < users.length; i++) {
                        gui.userStatusChange(users[i], true);
                    }
                }
                return ((ServerMessage) m).getContent();
            } else if (m instanceof UserMessage) {
                String message = "FROM: " + ((UserMessage) m).getSender() + ": " + ((UserMessage) m).getContent();
                if (((UserMessage)m).getHasImage()){
                    gui.showImage(((UserMessage)m).getImage());
                }
                return message;
            }

            return null;
        } catch (IOException e) {
            System.out.println("Error reading message: " + e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Error with receiveMessage, class not found: " + e);
            return null;
        }
    }

    public boolean login(String u, String p) {
        String content = u + " " + p;
        sendMessage(new ServerMessage(content));
        String line = readMessage();
        if (line.startsWith("SERVER: SUCCESS, LOGGED IN AS: ")) {
            userName = line.substring(31, line.length()).trim();
            return true;
        }
        return false;
    }

    public boolean createAccount(String u, String p) {
        Message acc = new ServerMessage(u + " " + p, true);

        sendMessage(acc);

        return readMessage().compareTo("SERVER: Account made") == 0;
    }

    public static void main(String[] args) {
        Client client = new Client();
        LoginGUI login = new LoginGUI("Login", client);
        client.startClient();
        login.setVisible(true);
    }

    public void exit() {
        ServerMessage message = new ServerMessage("SERVER: " + userName + " has disconnected.");
        message.setLoggingOff(true);
        sendMessage(message);
        try{
        oos.close();
        ois.close();
        socket.close();
        }catch(IOException e){
            
        }
        finished = true;
    }

    public boolean getFinished() {
        return finished;
    }
}
