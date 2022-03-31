/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 *
 * @author Oscar
 */
public class Server {

    private static Server server_instance = null;
    public static final int PORT = 232;
    private HashMap<String, ClientConnection> connectedUsers;

    private Server() {
        connectedUsers = new HashMap<>();
    }

    public static Server getInstance() {
        if (server_instance == null) {
            server_instance = new Server();
        }
        return server_instance;
    }

    public void start() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at: " + InetAddress.getLocalHost() + " on port: " + PORT);
        } catch (IOException e) {
            System.out.println("Server can't connect on port: " + e);
            System.exit(-1);
        }

        try {
            while (!serverSocket.isClosed()) {
                Socket s = serverSocket.accept();
                System.out.println("Connection made with: " + s.getInetAddress());
                ClientConnection cc = new ClientConnection(s);
                Thread thr = new Thread(cc);
                thr.start();
            }
        } catch (IOException e) {
            System.err.println("Can't accept the client connection: " + e);
        }
    }

    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.start();
    }

    private class ClientConnection implements Runnable {

        private Socket socket;
        private String userName;
        private boolean loggedIn;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public ClientConnection(Socket s) {
            socket = s;
            loggedIn = false;
        }

        public void run() {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                socket.setSoTimeout(30000);
            } catch (IOException e) {
                System.out.println("Error with oos & ois: " + e);
            }
            while (!loggedIn && socket.isConnected()) {
                logIn();
            }
            //trying to use scope so these variables go away?
            {
                ServerMessage welcomeMessage = new ServerMessage("SERVER: To send someone a message, type @username and then your message.");
                String[] onlineUsers = connectedUsers.keySet().toArray(new String[connectedUsers.size()]);
                welcomeMessage.setOnlineUsers(onlineUsers);
                this.sendMessage(welcomeMessage);
            }
            do {
                Message message = this.receiveMessage();
                if (message instanceof ServerMessage) {
                    
                } else {
                    if (((UserMessage)message).getHasImage()){
//                        System.out.println(((UserMessage)message).getImage().hashCode());
                    }
                    String sendTo = ((UserMessage) message).getSendTo();
                    ClientConnection receiver = connectedUsers.get(sendTo);
                    if (receiver == null) {
                        this.sendMessage(new ServerMessage("Recipient does not exist."));
                    } else {
                        connectedUsers.get(sendTo).sendMessage(message);
                    }
                }
            } while (loggedIn);
        }

        private void sendMessage(Message m) {

            try {
                oos.writeObject(m);
            } catch (IOException e) {
                System.out.println("Error with sendMessage: " + e);
            }
        }

        private Message receiveMessage() {
            try {
                Message receive;
                do {
                    receive = (Message) ois.readObject();
                } while (receive instanceof PingMessage);
                return receive;
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof EOFException) {
                    ServerMessage message = new ServerMessage("SERVER: " + userName + " has disconnected.");
                    message.setLoggingOff(true);
                    closeConnection();
                    return message;
                }
                System.out.println("Error with receiveMessage: " + e);
                return null;
            } catch (ClassNotFoundException e) {
                System.out.println("Error with receiveMessage, class not found: " + e);
                return null;
            }
        }

        private void closeConnection() {
            System.out.println(userName + " has disconnected.");
            try {
                if (connectedUsers.remove(userName) != null) {
                    connectedUsers.keySet().forEach((s) -> {
                        connectedUsers.get(s).sendMessage(new ServerMessage("SERVER: " + userName + " has disconnected."));
                    });
                }
                loggedIn = false;

                oos.close();
                ois.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error disconnecting: " + e);
            }
        }

        private void logIn() {
            Message details = receiveMessage();
            String messageContent = details.getContent();
            if (((ServerMessage) details).getNewAccount()) {
                if (ServerAccountManager.getInstance().createAccount(messageContent)){
                    sendMessage(new ServerMessage("SERVER: Account made"));
                    details = receiveMessage();
                    messageContent = details.getContent();
                }
            }

            String user = ServerAccountManager.getInstance().logIn(messageContent);

            if (!(user == null || connectedUsers.containsKey(user))) {
                userName = user;
                connectedUsers.put(user, this);
                loggedIn = true;
                System.out.println(userName + " has connected.");
                ServerMessage m = new ServerMessage("SERVER: SUCCESS, LOGGED IN AS: " + userName);
                this.sendMessage(m);

                connectedUsers.keySet().forEach((s) -> {
                    if (!(s.compareTo(userName) == 0)) {
                        connectedUsers.get(s).sendMessage(new ServerMessage("SERVER: " + userName + " has connected."));
                    }
                });
            }else{
                this.sendMessage(new ServerMessage("SERVER: Failed to log in."));
            }
        }
    }
}
