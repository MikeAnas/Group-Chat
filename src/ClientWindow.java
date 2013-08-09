
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;


public class ClientText extends JFrame implements ActionListener,WindowListener,WindowFocusListener,
WindowStateListener {
	
	/**
	 * 
	 */
	
	private static final String TEXT_SUBMIT = "text-submit";
	private static final String INSERT_BREAK = "insert-break";
	
	
	private static final long serialVersionUID = 1L;
	static DataOutputStream outToServer;
	static BufferedReader inFromUser;
	static Socket clientSocket;
	static boolean serveropen = true;
	static  BufferedReader inFromServer;
	public static  JTextArea text1 = new JTextArea(20,20);
	public static  JTextArea text2 = new JTextArea(20,20);
	public static ArrayList<Socket> x ;
	public static boolean stop = false;
	static JPanel p;
	public ClientText() throws HeadlessException, IOException {
		super();
		 JScrollPane scrollPane1 = new JScrollPane(text1);
		 JScrollPane scrollPane2 = new JScrollPane(text2);
		this.setSize(500, 200);
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.HORIZONTAL;
		 c.gridx=0;
		 c.gridy=0;
		c.gridwidth=200;
		c.gridheight=100;
	
	text1.setBackground((Color.white));
	text2.setBackground((Color.LIGHT_GRAY));
		this.setContentPane(p);
		
		
		//JTextArea text = new JTextArea(5,20);
		text1.setEditable(false);
		text2.setEditable(true);
		
		p.add(scrollPane1,c);
		//c.fill = GridBagConstraints.HORIZONTAL;
		// c.fill = GridBagConstraints.HORIZONTAL;
		   //make this component tall
		 //c.weightx = 0.0;
		 c.gridheight=50;
		 c.gridwidth = 200;
		 c.gridx = 0;
		 c.gridy = 150;
		 p.add(scrollPane2,c);
		
		
		
		JButton button = new JButton("Send");
		button.addActionListener(this);
        addWindowListener(this);
        addWindowFocusListener(this);
        addWindowStateListener(this);
	
		//c.fill = GridBagConstraints.HORIZONTAL;
		//c.ipady = 0;       //reset to default
		//c.weighty = 1.0;   //request any extra vertical space
		
		
		c.gridx = 0;       //aligned with button 2
		c.gridwidth = 2;   //2 columns wide
		c.gridy = 250;       //third row
		p.add(button, c);
		
		//MyKey mykey = new MyKey();
		//text2.addKeyListener(mykey);

			setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
		pack();
	

	

	}
	
	
	

	
	

	public static  void main(String [] args) throws HeadlessException, IOException, InterruptedException {
		
		 new ClientText();
		 boolean alreadyclosed = false;
		 initialize();
		 int j=0;
		try 
			{
			 while(j<10) {
		 	  clientSocket = new Socket("snf-22636.vm.okeanos.grnet.gr", 6786);
		 	  		 	  System.out.println(clientSocket);
		 	  if(clientSocket.isConnected()) {break;}
		 	  Thread.sleep(1000);
		 	  j++;
			 }
		 	  //clientSocket = new Socket("localhost", 6786);
			} 
			catch (UnknownHostException e) 
			{
		    	text1.append("Server is not known\n");
		    	  System.out.println("Skataa\n");
			} 
			catch (IOException e)
			{
				text1.append("Server is Open.\n"+"Try again Later\n");
				
			    
			} 
		 try {
			 
			 
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
			text1.append("Waiting to connect\n");
			String text1append = inFromServer.readLine();
			if(clientSocket.isClosed()) 
			{
				System.out.println("To pianw\n");
			}
			text1.append(text1append);
			while(serveropen) {
					//System.out.println("Ready to receive things for my text1");
					text1append = inFromServer.readLine();
					if(clientSocket.isClosed())
							{
					 		alreadyclosed=true;
					 		break;
							}
					if(text1append==null) break;
				    
				   // System.out.println("I am the client and received this"+text1append+"\n");
				    while(text1append==null) 
				    		{
							  text1append = inFromServer.readLine();
							  System.out.println("Next one null\n");
							  
						    }
				   // System.out.println("I am the client and received this"+text1append+"\n");
					text1.append("\n");
					text1.append(text1append);	
			}
				
		  }
		 catch (ConnectException  ex)
		 {
			// System.out.println("Geia sas\n");
			 text1.append("Could not create the input / output streams\n");
		 }
		 catch (IOException e) 
		 {
			
			//e.printStackTrace();
	     }  

		
		 if(!alreadyclosed) { clientSocket.close(); }
		// text1.append("\nServer Closed\n");
		 text2.setEditable(false);


	}

	


	private static void initialize() {
	    InputMap input = text2.getInputMap();
	    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
	    KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
	    input.put(shiftEnter, INSERT_BREAK);  // input.get(enter)) = "insert-break"
	    input.put(enter, TEXT_SUBMIT);

	    ActionMap actions = text2.getActionMap();
	    actions.put(TEXT_SUBMIT, new AbstractAction() {
	        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public void actionPerformed(ActionEvent e) {
	        	String toserver = ClientText.text2.getText();
	        	
				ClientText.text2.moveCaretPosition(ClientText.text2.getSelectionStart());
				ClientText.text2.setCaretPosition(0);
				ClientText.text2.setText("");
				try {
					ClientText.outToServer.writeBytes(toserver+'\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
	    });
	}

	
	


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if( arg0.getSource() instanceof JButton) {
			String toserver = text2.getText();
			//if(toserver.equals("CloseWindow")) 
			try {
				
				text2.setText("");
				text2.setCaretPosition(0);
				text2.moveCaretPosition(text2.getSelectionStart());
				//outToServer.writeChars(toserver+'\n');
				//outToServer.writeUTF(toserver+'\n');
				outToServer.writeBytes(toserver+'\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}








	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowClosing(WindowEvent arg0) {
		
		try {
			
			if(clientSocket==null) {}
			else clientSocket.close();
			if(outToServer==null) {}
			else outToServer.close();
			if(inFromServer==null){}
			else inFromServer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't close socket\n");
			this.dispose();
			//e.printStackTrace();
		}

		this.dispose();
	}








	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowStateChanged(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void windowLostFocus(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
