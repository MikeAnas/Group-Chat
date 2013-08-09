


import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerText extends JFrame implements ActionListener{

	
	/**
	 * 
	 */
	
	public static  JTextArea text = new JTextArea(20,20);
	public int port;
	 BufferedReader inFromClient;
	static ServerSocket welcomeSocket;
	 DataOutputStream outToClient;
	static Semaphore sem = new Semaphore(1,true);
	private static final long serialVersionUID = 1L;
	public static ArrayList<ClientThread> x ;
	public static boolean stop = false;
	static JPanel p;
	public ServerText(int port) throws HeadlessException, IOException {
		super();
		this.port=port;
		JPanel p = new JPanel();
		
		
		//Ftiaxnw to button.
		
	 
		this.setContentPane(p);
		JButton button = new JButton("Stop the Server");
		button.addActionListener(this);
		p.add(button);
		//JTextArea text = new JTextArea(5,20);
		text.setEditable(false);
		
		p.add(text);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
		pack();
	
	
	

	}
	
	public static void sendtoall(String tosend) {
		int i=0;
		//System.out.println("I am the server and ready to send this to everyone:");
		//System.out.println(tosend+'\n');
		try {
		
			sem.acquire();
			for(i=0;i<	x.size(); i++)
			{
				ClientThread curclient = x.get(i);
				curclient.ClientReceive(tosend);
			}
			sem.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

	public  void stopServer() throws IOException{

		
		stop=true;
		int i=0;
		//System.out.println("I am the server and ready to send this to everyone:");
		//System.out.println(tosend+'\n');

	
		sendtoall("Server Closed\n");
		try {
			
			sem.acquire();
			for(i=0;i<	x.size(); i++)
			{
				ClientThread curclient = x.get(i);
				curclient.connectionSocket.close();
				curclient.keepgoingbro=false;
			}
			sem.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//outToClient.writeBytes("Server Closed\n");
		Socket clientSocket = new Socket("localhost", 6786); 
		clientSocket.close();

	}
	
	public void startServer(){
		try {
		
			welcomeSocket = new ServerSocket(port);
			System.out.println("Created the server and waiting for connections \n");
			while(!stop)       {  
				Socket connectionSocket = welcomeSocket.accept(); 
				if(stop==true)
				{
					System.out.println("Bika edw pou prepei\n");
					break;
				}
				

				System.out.println("New client to server\n");
				  inFromClient  = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));    
				  outToClient = new DataOutputStream(connectionSocket.getOutputStream());     
				outToClient.writeBytes("You are connected"+'\n'); 
				//System.out.println(outToClient);
				//System.out.println(connectionSocket);
				//System.out.println(welcomeSocket);
				
				ClientThread client = new ClientThread(inFromClient,outToClient,connectionSocket);
				x.add(client);
				client.start();

	           }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Problem creating the socket\n");
			
		}
		System.out.println("Closing the server bye bye  \n");
		System.out.println(" (╯°□°）╯︵ ┻━┻\n");
		try {
			welcomeSocket.close();
		
		} catch (IOException e) {
			System.out.println("Problem closing this \n");
			e.printStackTrace();
		}
	
	}
	

	public static void main(String [] args) throws HeadlessException, IOException{
	
		
		x = new ArrayList<ClientThread>();
		int port = 6786;
		ServerText server = new ServerText(port);
		server.startServer();
		
		
	}
	
	class ClientThread extends Thread {
		public  Socket connectionSocket ;
		public  BufferedReader inFromClient;
		public  DataOutputStream outToClient;
		public boolean keepgoingbro=true;
		String clientSentence; 
		String username;
		
		public ClientThread(BufferedReader inFromClient, DataOutputStream outToClient2, Socket mine) {
			this.connectionSocket = mine;
			this.inFromClient=inFromClient;
			this.outToClient=outToClient2;
		}
		
		public void ClientReceive(String tobroadcast) {
			try {
				//System.out.println("I am a client thread and going to send to my client this :");
				//System.out.println(tobroadcast);
				//System.out.println(outToClient);
				outToClient.writeBytes(tobroadcast+'\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Couldn't sent to the user "+connectionSocket+"\n");
				e.printStackTrace();
			}
		}
		
		public void run() {
			if(!connectionSocket.isClosed()) {
			try {
				username = inFromClient.readLine(); // pira to username 
			
			
				while(keepgoingbro) {
					
					if(connectionSocket.isClosed()){break;}
					//System.out.println(username);
					//System.out.println("Eimai etoimos na kollhsw sto readline\n");
					clientSentence = inFromClient.readLine(); 
					if(connectionSocket.isClosed()){break;}
					//System.out.println("T perasa t readline\n");
					if(clientSentence==null) {
						break;
						
					}
					
					//System.out.println(username + "said : " + clientSentence + "\n"); 
					String tosend = username + " said : " + clientSentence  ;
				
					
					text.append(tosend);
					text.append("\n");
					//outToClient.writeBytes(tosend);  // edw tha prepei na steilw se olous ts users
					sendtoall(tosend);
				    
				}
			} 
			
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Caught an expection probably from the server being closed.");
			
			}
			
			
			System.out.println("Removed him\n");
			x.remove(this); // dn eimai sgros gia auto	
			
		
		}
		}
		
		
		
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {
		if( arg0.getSource() instanceof JButton) {
			try {
				System.out.println("Pressed\n");
				this.stopServer();
			} catch (IOException e) {
				System.out.println("The server could not be stopped It's op!\n");
				e.printStackTrace();
			}
		}
		
	}


}
