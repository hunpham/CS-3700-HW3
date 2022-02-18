/*
 * Server App upon TCP
 * A thread is started to handle every client TCP connection to this server
 * Created by: Weiying Zhu
 * Worked on by: Hung Pham, Rigoberto Hinojos
 * 2/11/2021
 * 
 */ 

import java.net.*;
import java.util.*;
import java.io.*;

public class TCPMultiServerThread extends Thread {

    //creates Socket
    private Socket clientTCPSocket = null;
    //Creates date
    static Date date = new Date();
    static String empty = null;

    public TCPMultiServerThread(Socket socket) {
        super("TCPMultiServerThread");
        clientTCPSocket = socket;
        
    }

    public void run() {
        try {
            BufferedReader cSocketIn = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
            PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);

            String host;
            String userAgent;
            String emptyLine;

            StringTokenizer sTokenizer;
            String method;
            String file;

            String fromClient;

            while ((fromClient = cSocketIn.readLine()) != null) {
                //print client requests
                System.out.println(fromClient);
                host = cSocketIn.readLine();
                System.out.println(host);
                userAgent = cSocketIn.readLine();
                System.out.println(userAgent);
                emptyLine = cSocketIn.readLine();
                System.out.println("\\r\\n");
                

                sTokenizer = new StringTokenizer(fromClient);
                method = sTokenizer.nextToken();
                file = sTokenizer.nextToken();
                
                if (!method.equals("GET")) {
                    System.out.println("Sending 400...");
                    cSocketOut.println("HTTP/1.1 400 Bad Request\r\n" + "Date: " + date + "\r\n" + "Server: cs3700a.msudenver.edu\r\n" + "\r\n" + empty);
                } else {
                    // Send file
                    sendFile(cSocketOut, file);
                }
            }
            cSocketOut.close();
            cSocketIn.close();
            clientTCPSocket.close();
   
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Sends output of connection status. Sends Header
     *  If file not found sends appropriate errors. */
    void sendFile(PrintWriter cSocketOut, String file) 
    {
        File reqFile;
        try {
            reqFile = new File(file.substring(1,file.length()));
            FileInputStream fis = new FileInputStream(reqFile);
            byte[] bufData = new byte[(int) reqFile.length()];
            fis.read(bufData);
            fis.close();
            // Send response (header + data + 4 spaces)
            System.out.println("Sending 200...");
            cSocketOut.println("HTTP/1.1 200 OK \r\n" + "Date: " + date + "\r\n" + "Server: cs3700a.msudenver.edu\r\n" + "\r\n" + new String(bufData) + "\r\n\r\n\r\n\r\n" + empty);
            // Send fileData
            // cSocketOut.write(new String(bufData));
            //4 Empty lines for 200 ok case
            // cSocketOut.write("\r\n\r\n\r\n\r\n");
            // cSocketOut.flush();
            // cSocketOut.close();
            // clientTCPSocket.close();
        } 
        // If File not found
        catch (IOException e) 
        {
            System.out.println("Sending 404...");
            cSocketOut.println("HTTP/1.1 Error 404 Not Found\r\n" + "Date: " + date + "\r\n" + "Server: cs3700a.msudenver.edu\r\n" + "\r\n" + empty); 
        }
    }
}
