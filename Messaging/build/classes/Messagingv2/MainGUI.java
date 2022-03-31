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
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Oscar
 */
public class MainGUI extends JFrame {

    private JTextArea chatHistory;
    private JScrollPane chatScroll;
    private JList<String> onlineUsers;
    private DefaultListModel<String> model;
    private Font font = new Font("Times Roman", Font.PLAIN, 14);
    private static final String defaultPath = System.getProperty("user.home") + "/Desktop";
    private File imageFile;

    public MainGUI(String title, Client client) {
        super(title);
        getContentPane().setLayout(null);

        JTextField typingBox = new JTextField();
        chatHistory = new JTextArea();
        typingBox.setLocation(20, 480);
        typingBox.setSize(500, 30);
        getContentPane().add(typingBox);

        chatHistory.setEditable(false);
        chatHistory.setFont(font);
        chatHistory.setWrapStyleWord(true);
        chatHistory.setLineWrap(true);

        chatScroll = new JScrollPane(chatHistory, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroll.setLocation(20, 20);
        chatScroll.setSize(500, 450);
        getContentPane().add(chatScroll);

        model = new DefaultListModel<>();
        onlineUsers = new JList<>(model);

        JScrollPane userScroll = new JScrollPane(onlineUsers, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        userScroll.setLocation(530, 20);
        userScroll.setSize(150, 410);
        getContentPane().add(userScroll);

        typingBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (client.sendUserMessage(typingBox.getText(), imageFile)) {
                    chatHistory.append(typingBox.getText() + "\n");
                    imageFile = null;
                    chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());

                } else {
                    chatHistory.append(typingBox.getText() + "\n");
                    chatHistory.append("Message failed to send." + "\n");
                    chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
                }
                typingBox.setText("");
            }
        });

        JButton attachImageButton = new JButton("Attach Image");
        attachImageButton.setLocation(530, 440);
        attachImageButton.setSize(150, 30);
        getContentPane().add(attachImageButton);
        attachImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseImage();
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setLocation(530, 480);
        quitButton.setSize(150, 30);
        getContentPane().add(quitButton);
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.exit();
                System.exit(-1);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.exit();
                System.exit(-1);
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 550);
        setResizable(false);
    }

    public void userStatusChange(String u, boolean status) {
        if (status) {
            model.addElement(u);
        } else {
            model.removeElement(u);
        }
        onlineUsers.repaint();
    }

    public void receiveUserMessage(String m) {
        chatHistory.append(m + "\n");
        chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
    }

    public void chooseImage() {
        JFileChooser choose = new JFileChooser(defaultPath);
        choose.setFileFilter(new ImageFileFilter());

        if (choose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            imageFile = choose.getSelectedFile();
        }
    }
    //https://stackoverflow.com/questions/15258467/java-how-can-i-popup-a-dialog-box-as-only-an-image
    public void showImage(BufferedImage image) {
        JDialog dialog = new JDialog(this, "image", true);
        JLabel label = new JLabel(new ImageIcon(image));
        dialog.add(label);
        dialog.pack();
        dialog.setVisible(true);
    }


    public void receiveMessage(String m) {
        if (m.startsWith("SERVER:")) {
            if (m.endsWith(" disconnected.")) {
                String user = m.substring(8, m.length() - 18);
                userStatusChange(user, false);
            } else if (m.endsWith(" connected.")) {
                String user = m.substring(8, m.length() - 15);
                userStatusChange(user, true);
            }
        }
        chatHistory.append(m + "\n");
        chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum());
    }

    private class ImageFileFilter extends FileFilter implements java.io.FileFilter {

        public boolean accept(File f) {
            if (f.getName().toLowerCase().endsWith(".jpeg")) {
                return true;
            }
            if (f.getName().toLowerCase().endsWith(".jpg")) {
                return true;
            }
            if (f.getName().toLowerCase().endsWith(".png")) {
                return true;
            }
            if (f.isDirectory()) {
                return true;
            }
            return false;
        }

        public String getDescription() {
            return "Image files";
        }
    }
}
