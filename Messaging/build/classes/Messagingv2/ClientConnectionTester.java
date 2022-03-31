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
public class ClientConnectionTester implements Runnable{
    private Client client;
    private PingMessage message;
    
    public ClientConnectionTester(Client c){
        client = c;
        message = new PingMessage();
    }
    
    public void run(){
      do{
        if (!client.sendMessage(message)){
            System.out.println("Problem with delivering message to server.");
            client.exit();
        }
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){
            System.out.println("This shouldn't happen? " + e);
        }
        }while(!client.getFinished());
    }
}
