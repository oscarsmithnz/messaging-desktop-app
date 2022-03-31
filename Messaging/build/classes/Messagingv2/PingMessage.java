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
public class PingMessage extends Message {
    public PingMessage(){
        this.content = "SERVER: ping";
    }
}
