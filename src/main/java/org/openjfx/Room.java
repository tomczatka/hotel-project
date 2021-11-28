package org.openjfx;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Room {
    int number;

    public int getPort() {
        return port;
    }

    int size;
    String status;
    int key;
    Boolean isEmpty = true;
    int port = 0;

    @Override
    public String toString() {
        return "numer=" + number +
                ", rozmiar=" + size +
                ", status=" + status ;
    }

    public Room(int number, int size, String status, int key) throws IOException {
        this.number = number;
        this.size = size;
        this.status = status;
        this.key = key;
        createsocket();
    }

    private void createsocket() {
            Thread roomthread = new Thread(()->{
                try {
                    Socket socket = new Socket("localhost",1500);
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String inputString;
                    pw.println("room started"+"|"+number);
                    while((inputString = br.readLine()) != null){
                        if(inputString.charAt(0) == 'P'){
                            port = Integer.parseInt(inputString.substring(1));
                            createserver();

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            roomthread.start();
    }

    private void createserver(){
        Thread roomthread2 = new Thread(() -> {
           try{
               ServerSocket serverSocket = new ServerSocket(port);
               Thread roomthread3 = new Thread(()->{
                   while(true){
                       try {
                           Socket socket = serverSocket.accept();
                           serverperations(socket);

                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               });
               roomthread3.start();
           }catch (IOException e){
               e.printStackTrace();
           }
        });
        roomthread2.start();
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void serverperations(Socket socket){
        Thread thread = new Thread(() -> {
            try{
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputStream;
                while((inputStream = br.readLine()) != null){
                    if(inputStream.startsWith("wejscie")){
                        String[] list = inputStream.split("\\|");
                        if(Integer.parseInt(list[1])==number&&Integer.parseInt(list[2])==key){
                            pw.println("dane poprawne");
                            isEmpty = false;
                        }else {
                            pw.println("bledne dane");
                        }
                    }
                    if(inputStream.equals("wyjscie")){
                        isEmpty = true;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        });
        thread.start();
    }
}
