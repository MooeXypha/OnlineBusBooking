package com.xypha.onlineBus.booking.services;

import java.net.Socket;

public class TestSMTPConnection {
    public static void main (String[] args) throws Exception{
        try (
            Socket socket = new Socket("smtp.gmail.com", 587)
        ){
            System.out.println("Connection successful");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
