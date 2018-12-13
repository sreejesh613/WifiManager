package com.qiwoo.wifimanager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class BroadcastHelper  extends Thread {

    private SocketAddress socketAddress =null;
    private  DatagramSocket datagramSocket = null;
    private   DatagramPacket   p;

    public BroadcastHelper(){

    }

    @Override
    public void run() {
        try{
            String messageStr="Water bottles have wings!";
            int server_port = 6789;
            InetAddress server = InetAddress.getByName("192.168.43.1");
            socketAddress = new InetSocketAddress(server,6789);

            datagramSocket = new DatagramSocket(socketAddress);

            //  datagramSocket.setReuseAddress(true);
            datagramSocket.setBroadcast(true);
            //datagramSocket.bind(socketAddress);

            InetAddress local = InetAddress.getByName("255.255.255.255");
            int msg_length=messageStr.length();
            byte[] message = messageStr.getBytes();
                p = new DatagramPacket(message, msg_length,local,server_port);
            new Timer().schedule(
                    new TimerTask(){

                        @Override
                        public void run(){
                            try{
                                datagramSocket.send(p);

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    }, 100,2000);
//            while (true) {
//                datagramSocket.send(p);
//            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}