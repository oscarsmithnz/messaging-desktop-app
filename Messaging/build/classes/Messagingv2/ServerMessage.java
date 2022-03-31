/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

/**
 *
 * @author Oscar
 */
public class ServerMessage extends Message {
    private String[] onlineUsers;
    private boolean newAccount;
    private boolean loggingOff;
    
    public ServerMessage(String c){
        this.content = c;
    }
    
    public ServerMessage(String c, boolean acc){
        this.content = c;
        this.newAccount = acc;
    }
    
    public boolean getLoggingOff(){
        return loggingOff;
    }
    public void setLoggingOff(boolean status){
        this.loggingOff = status;
    }
    
    public boolean getNewAccount(){
        return newAccount;
    }
    public void setContent(String c){
        this.content = c;
    }
    
    public void setOnlineUsers(String[] u){
        onlineUsers = u;
    }
    public String[] getOnlineUsers(){
        return onlineUsers;
    }
    
    public void setSendTo(String u){
        this.sendTo = u;
    }
}
