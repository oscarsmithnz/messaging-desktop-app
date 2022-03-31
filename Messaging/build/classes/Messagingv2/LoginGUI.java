/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 *
 * @author Oscar
 */
public class LoginGUI extends JFrame{
    public LoginGUI(String title, Client client){
        super(title);
        getContentPane().setLayout(null);
        
        JTextField userText;
        JTextField passwordText;
        JButton loginButton;
        JButton createAccountButton;
        JLabel header;
        JLabel usernameLabel;
        JLabel passwordLabel;

        header = new JLabel("Messaging");
        header.setFont(new Font(header.getFont().getFontName(), Font.PLAIN, 40));
        header.setLocation(50, 10);
        header.setSize(400, 200);
        getContentPane().add(header);
        
        usernameLabel = new JLabel("Username: ");
        usernameLabel.setLocation(60, 150);
        usernameLabel.setSize(100, 30);
        getContentPane().add(usernameLabel);

        passwordLabel = new JLabel("Password: ");
        passwordLabel.setLocation(60, 185);
        passwordLabel.setSize(100, 30);
        getContentPane().add(passwordLabel);

        userText = new JTextField();
        userText.setSize(200, 30);
        userText.setLocation(130, 150);
        getContentPane().add(userText);

        passwordText = new JPasswordField();
        passwordText.setSize(200, 30);
        passwordText.setLocation(130, 185);
        getContentPane().add(passwordText);
        
        createAccountButton = new JButton("Create Account");
        createAccountButton.setSize(150, 60);
        createAccountButton.setLocation(200, 250);
        getContentPane().add(createAccountButton);
        createAccountButton.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
               if (client.createAccount(userText.getText(), passwordText.getText())){
                   JOptionPane.showMessageDialog(null, "Account created.");
                   userText.setText("");
                   passwordText.setText("");
               }else{
                   JOptionPane.showMessageDialog(null, "Account creation failed.");
               }
           } 
        });
        
        loginButton = new JButton("Login");
        loginButton.setSize(150, 60);
        loginButton.setLocation(30, 250);
        getContentPane().add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (client.login(userText.getText(), passwordText.getText())){
                    LoginGUI.this.dispose();
                    client.openMainGUI();
                }else{
                    JOptionPane.showMessageDialog(null, "You could not be logged in.");
                }
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(400, 400);
        setResizable(false);
        
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                client.exit();
                System.exit(-1);
            }
        });
    }
}
