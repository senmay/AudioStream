package server;

import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ArrayList<DataInputStream> listeners;
    ServerSocket serverSocket;
    Socket listener;
    DataInputStream dos;

    //Socket socket;
    //DataInputStream dis;
    AudioFormat format;
    DataLine.Info info;
    SourceDataLine speakers;

    Server() {
        listeners = new ArrayList<>();
    }

    public void start() {

        try {
            serverSocket = new ServerSocket(10001);
            System.out.println("Server Started");
            new broadCast().start();

            while (true) {
                listener = serverSocket.accept();
                dos = new DataInputStream(listener.getInputStream());
                listeners.add(dos);
                System.out.println("Connected from [" + listener.getPort() + " : " + listener.getInetAddress() + "]");
                System.out.println("Current listener : " + listeners.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }//main()

    class broadCast extends Thread {
        AudioFormat format = new AudioFormat(192000.0f, 16, 2, true, false);
        TargetDataLine microphone;
        DataOutputStream lstn;

        @Override
        public void run() {
            try {
                format = new AudioFormat(192000.0f, 16, 2, true, false);
                info = new DataLine.Info(SourceDataLine.class, format);

                int dsize = 0;
                byte[] data = new byte[1024];

                speakers = (SourceDataLine) AudioSystem.getLine(info);
                speakers.open(format);
                speakers.start();

                while(true) {
                    for (int i = 0; i<listeners.size(); i++) {
                        dsize = listeners.get(i).read(data);
                        speakers.write(data, 0, dsize);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                speakers.drain();
                speakers.close();
            }
        }
    }

}// server.Server class