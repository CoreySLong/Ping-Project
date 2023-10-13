import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

class PINGClient {
    public static void main(String argv[]) throws Exception{
    	// socket variables
    	//DatagramSocket clientSocket;
    	DatagramPacket sendPacket;
    	DatagramPacket receivePacket;
    	byte[] receiveData = new byte[1024];
    	byte[] sendData = new byte[1024];   // Holds how many bytes the packet string will be		
    	int sendDataSize;					// Holds packet string byte length
    	InetAddress IPAddress; // address = InetAddress.getByName("localhost");
    	int seq_NO = 0;
    	byte version_num = (byte) 1;
    	Random random = new Random();
    	char paddingChar = '1';

    	// client variables
    	String packetString, serverSentence;
    	int ping_requests, ping_responses, i = 0;   // holds number of ping requests and responses for summary. i = iteration for the rtts and payload_sizes arrays
    	double[] rtts;  // holds all of the RTTs for summary
    	double min_rtt = 0, max_rtt = 0, avg_rtt = 0;
    	int packet_loss; // percentage of packets lost
    	int[] payload_sizes;  // holds all of the payload sizes for summary
    	int avg_payload = 0;

    	// command-line arguments
    	int port, clientID, num_packets, wait_time;
    	String host;

    	// process command-line arguments
    	if (argv.length < 5) {
    		System.out.println ("Usage: java PINGClient IP_Address port_num clientID num_packets wait_sec\n");
    		System.exit (-1);
    	}
    	host = argv[0];
    	port = Integer.parseInt(argv[1]);
    	clientID = Integer.parseInt(argv[2]);
    	num_packets = Integer.parseInt(argv[3]);
    	wait_time = Integer.parseInt(argv[4]);
    	
    	rtts = new double[num_packets];
    	payload_sizes = new int[num_packets];
      
        // Create client socket to destination
        try(DatagramSocket clientSocket = new DatagramSocket()){
        	IPAddress = InetAddress.getByName (host);
	        while(seq_NO < num_packets) {  // sending the packets
	            long currentTimeMillis = System.currentTimeMillis(); // Current time in milliseconds
	            
	            int size = random.nextInt(300 - 150 + 1) + 150;      // random size for the payload
	            payload_sizes[i] = size;
	            
	            // Create string that goes in packet
	            packetString = "---------- Ping Request Packet Header ----------"
	            			 + "\nVersion: " + version_num
	            			 + "\nClient ID: " + clientID
	            			 + "\nSequence NO.: " + seq_NO
	            			 + "\nTime: " + currentTimeMillis  // professor said the format for time is fine
	            			 + "\nPayload Size: " + size
	            			 + "\n--------- Ping Request Packet Payload ------------"
	            			 + "\nHost: " + host
	            			 + "\nClass-name: VCU-CMSC440-SPRING-2023"
	            			 + "\nUser-name: Long, Corey"
	            			 + "\nRest: ";     
	            
	            sendData = packetString.getBytes(); // calculates byte size of the string
	            sendDataSize = sendData.length;     // stores how many bytes the packet is
	            
	            if(sendDataSize < size) {   // if there is still space in the payload to be filled
	                while (sendDataSize < size) { // Appends padding character to the encoded string until it reaches the desired byte size
	                    sendData = Arrays.copyOf(sendData, sendData.length + 1);
	                    sendData[sendData.length - 1] = (byte) paddingChar;
	                    sendDataSize++;
	                }
	            }
	            
	            // adding ------ below "Rest" in the packetString
	            String str = new String(sendData);
	            str += "\n------------------------------------------------------------";
	            sendData = str.getBytes();
	            
	            // Create packet and send to server
	            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	            clientSocket.send(sendPacket);
	            clientSocket.setSoTimeout(wait_time * 1000);   // setting wait time for catch. multiplying it by 1000 since user gives seconds and function takes milliseconds
	            System.out.println (packetString + "\n");
	            
	            seq_NO++;
	            
	            // Create receiving packet and receive from server
	            receivePacket = new DatagramPacket(receiveData, receiveData.length); 
	            clientSocket.receive(receivePacket);
	            serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
	            
	            // calculating RTT
	            long secondCurrentTimeMillis = System.currentTimeMillis(); // Current time in milliseconds that packet is received
	            currentTimeMillis = secondCurrentTimeMillis - currentTimeMillis;
	            rtts[i] = currentTimeMillis/1000.0; 
	            	
	            System.out.println(serverSentence + "\nRTT: " + currentTimeMillis/1000.0 + " seconds"); 
	        }
	        clientSocket.close();
	        i++;
        }
        catch(SocketTimeoutException e) { // Handles the timeout
            System.out.println("--------------- Ping Response Packet Timed-Out ------------------");
        }
        
        // getting min, max, and avg rtt and avg payload size
        min_rtt = Arrays.stream(rtts).min().getAsDouble();
        max_rtt = Arrays.stream(rtts).max().getAsDouble();
        for (double number : rtts) {
            avg_rtt += number;
        }
        for (int number : payload_sizes) {
        	avg_payload += number;
        }   
        
        System.out.println("\nSummary: " + num_packets + " :: " + num_packets + " :: <packet loss rate> :: " + min_rtt + " :: " + max_rtt + " :: " + avg_rtt + " :: " + avg_payload);
    } // end main
} // end class
