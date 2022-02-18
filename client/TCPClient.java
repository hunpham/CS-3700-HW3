/*
 * Client App upon TCP
 * Created by: Weiying Zhu
 * Worked on by: Hung Pham, Rigoberto Hinojos
 *
 */ 

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {

    // private static final String[] HTTPVER = {"HTTP/0.9", "HTTP/1.0", "HTTP/1.1", "HTTP/2", "HTTP/2.0", "HTTP/3"};
    static ArrayList<String> tempOutput = new ArrayList<String>();


    public static void main(String[] args) throws IOException {

        // if (args.length != 1) {
        //      System.out.println("Usage: java TCPClient <hostname>");
        //      return;
        // }
        
        // Get user input

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Enter IP address or hostname to connect (suggessted 147.153.10.87): ");
        
        Socket tcpSocket = null;
        // PrintWriter socketOut = null;
        // BufferedReader socketIn = null;
        String addressInput = sysIn.readLine();

        if(!"".equals(addressInput.trim())) {
            try {
                tcpSocket = new Socket(addressInput, 5260);
            } 
            catch (java.net.UnknownHostException e) {
                System.out.println("Don't know about host: " + addressInput);
            }
        }

        PrintWriter socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

        System.out.println("Connecting to " + tcpSocket);

        String fromServer;
        String fromUser;
        String httpMethod = null;
        String fileName = null;
        String httpVer = null;
        String host = null;
        String userAgent = null;

        while (true) {
            System.out.print("Enter HTTP method (suggested GET): ");
            httpMethod = sysIn.readLine();
            System.out.print("Enter requested file name (suggested /CS3700.htm): ");
            fileName = sysIn.readLine();
            System.out.print("Enter HTTP version (suggested HTTP/1.1): ");
            httpVer = sysIn.readLine();
            System.out.print("Enter Host (suggested cs3700a.msudenver.edu): ");
            host = sysIn.readLine();
            System.out.print("Enter User Agent: ");
            userAgent = sysIn.readLine();
            System.out.print("\r\n");
            
            String getReq = httpMethod + " " + fileName + " " + httpVer + "\r\n" + "Host: " + host + "\r\n" + "User-Agent: " + userAgent + "\r\n";
            // System.out.println("Going to send: " + getReq);
            long timeSend = System.currentTimeMillis();
            socketOut.println(getReq);

            // Get server response and put to an peopary arraylist

            while (!(fromServer = socketIn.readLine()).equals("null")) {     
                tempOutput.add(fromServer);
            }

            // Get time & HTTP status
            long rtt = System.currentTimeMillis() - timeSend;
            String status = tempOutput.get(0);

            if (!(status.contains("200 OK"))) {
                for (String str: tempOutput) {
                    System.out.println(str);
                }
                System.out.println("RTT: " + rtt + "ms");
                tempOutput.clear();
            }
            else {
                for (int i = 0; i < 4; i++) {
                    String temp = tempOutput.get(0);
                    System.out.println(temp);
                    tempOutput.remove(0);
                }
                System.out.println("RTT: " + rtt + "ms");
                System.out.println("200 OK - Writing to file...");
                String writerFile = fileName.substring(1);
                writeToFile(writerFile);
                System.out.println("Done.");
            }

            System.out.print("\nPress enter to continue or type quit to exit.\n> ");
            String option = sysIn.readLine();
            if (option.equals("quit")) {
                System.out.println("See ya!");
                socketOut.close();
                socketIn.close();
                sysIn.close();
                tcpSocket.close();
                break;
            }
        }
        // socketOut.close();
        // socketIn.close();
        // tcpSocket.close();
        // sysIn.close();
    }

    //create and file and write to it
    static void writeToFile(String writerFile) throws IOException {
        FileWriter fileWriter = new FileWriter(writerFile);
        for(String str: tempOutput) {
            fileWriter.write(str + System.lineSeparator());
        }
        fileWriter.close();
        tempOutput.clear();
    }

    // static String inputDefaultChoice(String defaultChoice) throws IOException {
    //     BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
    //     String inputVar = sysIn.readLine();

    //     if(!"".equals(inputVar.trim())) {
    //         try {
    //             defaultChoice = inputVar;
    //         }
    //         catch(Exception e) {
    //             System.out.println("Wrong! Cause " + e);
    //         }
    //     }
    //     return defaultChoice;
    // }

}
