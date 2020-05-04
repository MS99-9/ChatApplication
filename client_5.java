package chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Java implementation for multithreaded chat client 
//Save file as Client.java 

import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField; 

public class client_5  
{ 
	static String ClientName;


	
	
 public static void main(String args[]) throws UnknownHostException, IOException  
 { 
		JLabel send ;
		 JLabel playerName ;
		 JLabel mypanel ;
		 JTextArea print;
		
	 JFrame gui = new JFrame();
	 gui.validate();
		gui.setExtendedState(gui.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		gui.setTitle("Chat");
		//gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
		playerName = new  JLabel("Please enter your name") ;
		playerName.setBounds(30, 670, 420, 100);
		playerName.setFont(new Font("Arial", Font.BOLD, 30));
		playerName.setForeground(Color.black);
		
		JLabel messages = new  JLabel("messages") ;
		messages.setBounds(10, 10, 200, 100);
		messages.setFont(new Font("Arial", Font.BOLD, 30));
		messages.setForeground(Color.black);
		messages.setBackground(Color.red);
		
		print = new  JTextArea() ;
		print.setBounds(50, 90, 1400, 570);
		print.setFont(new Font("Arial", Font.BOLD, 20));
		print.setForeground(Color.black);
		print.setBackground(Color.GRAY);

		
		send = new JLabel();
		send.setText("Press Enter To Send");
		send.setBounds(1050 ,670 ,420 ,100);
		send.setForeground(Color.black);
		send.setFont(new Font("Arial", Font.ITALIC, 45));
		send.setOpaque(false);
		
		mypanel = new JLabel();
		mypanel.setLayout(null);
		mypanel.setBounds(0, 0 , 1500 , 900);
		mypanel.add(playerName);
		mypanel.add(send);
		mypanel.add(print);
		mypanel.add(messages);

		
		ImageIcon iconstart = new ImageIcon("bg1.jpg");
		Image img2 = iconstart.getImage();  
	    Image resizedImage2 = img2.getScaledInstance(1600, 800,  java.awt.Image.SCALE_SMOOTH);  
	    ImageIcon icon2 = new ImageIcon(resizedImage2);
		mypanel.setIcon(icon2);
		
		gui.add(mypanel); 
		gui.setVisible(true);
		
		//String ClientName="";
	 
     //Scanner scn = new Scanner(System.in); 
	 //System.out.print("Join (Enter you name ): ");
	 JTextField clientname = new JTextField();
	 clientname.setToolTipText("please enter your name");
	 clientname.setBounds(400 , 670, 580 , 90);
	 clientname.setFont(new Font("Arial", Font.BOLD, 25));
		mypanel.add(clientname);
		clientname.setVisible(true);

		while(ClientName==null) {
			 
		 
	 clientname.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	ClientName=clientname.getText();
		    	playerName.setText("Chat Here");
            
		    }
		});
	 //String ClientName = scn.next();
		}
		
     Socket s = new Socket("localhost",5000); 

     DataOutputStream dname = new DataOutputStream(s.getOutputStream());
     dname.writeUTF(ClientName);
	 
     
     DataInputStream drej = new DataInputStream(s.getInputStream()); 
     String x= drej.readUTF();
     
     if (x.equalsIgnoreCase("rejected")){
    	 System.exit(0); 
     }
     else {
    	 mypanel.remove(clientname);
     
     
     BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 

	 
     // establish the connection 
       
     // obtaining input and out streams 
     DataInputStream dis = new DataInputStream(s.getInputStream()); 
     DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

     // sendMessage thread 
     Thread sendMessage = new Thread(new Runnable()  
     { 
         String msg="";
         @Override
         public void run() { 
             while (true) {
            	 
            	 JTextField name = new JTextField();
         		name.setBounds(400 , 670, 580 , 90);
         		name.setFont(new Font("Arial", Font.BOLD, 25));
         		name.addActionListener(new ActionListener() {

         		    @Override
         		    public void actionPerformed(ActionEvent e) {
         		       msg=name.getText();
                       try {
						dos.writeUTF(2+msg);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                       name.setText("");
         		    }
         		});
        		mypanel.add(name);


                 // read the message to deliver. 
//				if (send) {
//					msg=name.getText();
//				send =false;
//             }
                 try {
					msg=br.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//msg=msg+2; 
                   
                 try { 
                     // write on the output stream 
                     dos.writeUTF(2+msg);
                     if(msg.equalsIgnoreCase("Quit")) {
                    	 //s.close();
                    	 System.exit(0);
                     }
                     else if(!(msg.equalsIgnoreCase("GetMemberList"))){
                     StringTokenizer st = new StringTokenizer(msg, "#"); 
                     String source = st.nextToken();
                     String Destination = st.nextToken(); 
                     //int TTL = Integer.parseInt(st.nextToken());
                     String MsgToSend = st.nextToken();
                     System.out.println("you ---> "+Destination+" : "+MsgToSend);
                     print.setText(print.getText()+"          "+"\n"+"you ---> "+Destination+" : "+MsgToSend);
                     }
                 } catch (IOException e) { 
                     e.printStackTrace(); 
                 } 
             } 
         } 
     }); 
       
     // readMessage thread 
     Thread readMessage = new Thread(new Runnable()  
     { 
         @Override
         public void run() { 

             while (true) { 
                 try { 
                     // read the message sent to this client 
                     String msg = dis.readUTF(); 
                     System.out.println(msg);
                     print.setText(print.getText()+"          "+"\n"+msg);
                     print.validate();
                     mypanel.validate();
                 } catch (IOException e) { 

                	 System.exit(0);
                	 break;
                 } 
             } 
         } 
     }); 

     sendMessage.start(); 
     readMessage.start(); 
     
     }
     


 } 
} 