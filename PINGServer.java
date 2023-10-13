import java.io.*;
import java.net.*;
import java.util.Random;

class PINGServer {
    public static void main(String argv[]) throws Exception{
    	Random random = new Random();
    	// socket variables
    	DatagramSocket serverSocket;
    	byte[] receiveData = new byte[1024];
    	byte[] sendData = new byte[1024];
    	InetAddress IPAddress;
    	int clientPort;

    	// server variables
    	String serverSentence;

    	// command-line arguments
    	int port, loss;
    	String host;

    	// process command-line arguments
    	if (argv.length < 2) {
    		System.out.println ("Usage: java PINGServer port_num loss");
    		System.exit (-1);
    	}
    	if (Integer.parseInt(argv[0]) < 10000 || Integer.parseInt(argv[0]) > 11000) {
    		System.out.println ("ERR: arg 0, must be 10000-11000");
    		System.exit (-1);
    	}
    	if (Integer.parseInt(argv[1]) < 0) {
    		System.out.println ("ERR: arg 1, must be > 0");
    		System.exit (-1);
    	}
    	
    	port = Integer.parseInt(argv[0]);
    	loss = Integer.parseInt(argv[1]);
    	host = "127.0.0.1";

    	try { // The port is not being used
        	// Create welcoming socket using given port
            serverSocket = new DatagramSocket(port);
            System.out.println("PINGServer started with server IP: " + host + " port:" + port + "\n");
            serverSocket.close();
    	} catch (SocketException e) { // The port is being used
          System.err.println("ERR: cannot create PINGServer socket using port number" + port);
          System.exit (-1);
    	}
        // have to do again since there's a may not be initialized error if initialized in try
        serverSocket = new DatagramSocket(port);

    	// While loop to handle arbitrary sequence of clients making requests
    	while (true) {        
    		// Waits for some client to send a packet
    		DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
    		serverSocket.receive(receivePacket);
    	    String clientSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
    	    
    	    IPAddress = receivePacket.getAddress();
		    clientPort = receivePacket.getPort();
		    
		    String delim = "[:\n]";   // delimiters to parse the string with
		    String[] parsedString  = clientSentence.split(delim);      // parsing string
		    
            int randNum = random.nextInt(100) + 1;
            if (randNum <= loss)  // if true, simulates a packet loss
            	System.out.println("IP:" + IPAddress + " :: Port:" + clientPort + " :: ClientID:" + parsedString[4] + " :: Seq#:" + parsedString[6] + " :: DROPPED");
            else 
            	System.out.println("IP:" + IPAddress + " :: Port:" + clientPort + " :: ClientID:" + parsedString[4] + " :: Seq#:" + parsedString[6] + " :: RECEIVED");
            
            // Changing client sentence to show server received it
       	    String replacement = "Received Ping Request";
    	    int startIndex = 11; // Index of the substring to be replaced
    	    int endIndex = 23;   // Index of the character after the substring to be replaced
    	    String changedClientSentence = clientSentence;
    	    changedClientSentence = changedClientSentence.substring(0, startIndex) + replacement + changedClientSentence.substring(endIndex);
    	    startIndex = 147; 
    	    endIndex = 159;   
    	    changedClientSentence = changedClientSentence.substring(0, startIndex) + replacement + changedClientSentence.substring(endIndex);
    	    System.out.println(changedClientSentence + "\n"); 

    	    // Convert to all caps
    	    serverSentence = clientSentence.toUpperCase();
    	    
    	    // Changing client sentence to server sentence
    	    replacement = "Received Ping Response";
    	    startIndex = 11; // Index of the substring to be replaced
    	    endIndex = 23;   // Index of the character after the substring to be replaced
    	    serverSentence = serverSentence.substring(0, startIndex) + replacement + serverSentence.substring(endIndex);
    	    startIndex = 148; 
    	    endIndex = 160;   
    	    serverSentence = serverSentence.substring(0, startIndex) + replacement + serverSentence.substring(endIndex);

    	    // Write output line to socket
		    sendData = serverSentence.getBytes();
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
		    serverSocket.send(sendPacket);
		    System.out.println (serverSentence + "\n");
    	} //  end while; loop back to accept a new client connection
    } // end main
} // end class
