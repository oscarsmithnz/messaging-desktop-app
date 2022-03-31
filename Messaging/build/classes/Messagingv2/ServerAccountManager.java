/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

/**
 * this class maintains the accounts, holding createAccount and logIn methods,
 * as well as a list of all account objects.
 *
 * @author Oscar
 */
public class ServerAccountManager {

    private static ServerAccountManager manager_instance = null;
    private File file = new File("not logins.txt");
    private HashMap<String, String> allUsers;
    private Scanner scan;

    private ServerAccountManager() {
        allUsers = new HashMap<>();
        startUp();
    }

    public static ServerAccountManager getInstance() {
        if (manager_instance == null) {
            manager_instance = new ServerAccountManager();
        }
        return manager_instance;
    }

    private void startUp() {
        try {
            scan = new Scanner(file);
            
            while (scan.hasNextLine()) {
                String curLine = scan.nextLine();
                allUsers.put(curLine.split(" ")[0].toLowerCase(), curLine.split(" ")[1]);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e);
        }
    }
    //takes a string, returns a username if the user can be logged in, otherwise null.
    public synchronized String logIn(String line) {
        String user = line.split(" ")[0];
        String passw = line.split(" ")[1];
        
        String s = allUsers.get(user);
        if (s == null)
            return null;
        if (s.compareTo(passw) == 0)
            return user;
        
        return null;
    }

    public synchronized boolean createAccount(String line) {
        String u = line.split(" ")[0];
        String p = line.split(" ")[1];
        if (allUsers.containsKey(u) || u.length() > 15 || u.length() < 3 || p.length() > 15 || p.length() < 3 || u.contains(" ") || p.contains(" ")){
            return false;
        }
        try {
            String append = (u + " " + p + "\n");
            Files.write(file.toPath(), append.getBytes(), StandardOpenOption.APPEND);
            allUsers.put(u, p);
        } catch (IOException e) {
            System.out.println("File not found: " + e);
            return false;
        }
        return true;
    }
}
