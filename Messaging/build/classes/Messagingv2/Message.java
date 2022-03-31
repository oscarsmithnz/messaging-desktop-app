/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messagingv2;

import java.io.Serializable;

/**
 *
 * @author Oscar
 */
public abstract class Message implements Serializable{
    protected String content;
    protected String sendTo;
    
    public String getContent(){
        return content;
    }
    
    public String getSendTo(){
        return sendTo;
    }
}
