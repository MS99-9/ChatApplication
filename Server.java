package chat;

//Java implementation of  Server side 
//It contains two classes : Server and ClientHandler 
//Save file as Server.java 

import java.io.*; 
import java.util.*; 
import java.net.*; 

//Server class 
public class Server  
{ 

 // Vector to store active clients 
 static Vector<ClientHandler> ar = new Vector<>();
 static Vector<String> names = new Vector<>(); 

   
 // counter for clients 
 static int i = 0; 

 public static void main(String[] args) throws IOException  
 { 
     // server is listening on port 1234 
     ServerSocket ss = new ServerSocket(6000); 
       
     Socket s; 
       
     // running infinite loop for getting 
     // client request 
     while (true)  
     { 
    	 boolean exists=false;
         s = ss.accept();
         DataInputStream dname = new DataInputStream(s.getInputStream()); 
         String x= dname.readUTF();
         System.out.print(x+" is ");
    	 for(int i=0;i<names.size();i++) {
    		 if(x.equalsIgnoreCase(names.get(i))) {
    			 exists=true;
    		 }
    	 }
    	 
       if(!exists){
    	   
    	 System.out.println("Accepted "+"("+s+")");
    	 DataOutputStream drej = new DataOutputStream(s.getOutputStream());
	     drej.writeUTF("accepted");
	     //drej.close();
         // Accept the incoming request 

         //System.out.println("New client request received : " + s); 
           
         // obtain input and output streams 
         DataInputStream dis = new DataInputStream(s.getInputStream()); 
         DataOutputStream dos = new DataOutputStream(s.getOutputStream());
         String clientName=x;
           
         //System.out.println("Creating a new handler for this client..."); 

         // Create a new handler object for handling this request. 
         ClientHandler mtch = new ClientHandler(s,clientName, dis, dos); 

         // Create a new Thread with this object. 
         Thread t = new Thread(mtch); 
           
         //System.out.println("Adding this client to active client list"); 

         // add this client to active clients list 
         ar.add(mtch);
         names.add(clientName);

         // start the thread. 
         t.start(); 

         // increment i for new client. 
         // i is used for naming only, and can be replaced 
         // by any naming scheme 
         i++;
         
     }
       else {
    	   System.out.println("Rejected "+"("+s+")");
    	   DataOutputStream drej = new DataOutputStream(s.getOutputStream());
    	     drej.writeUTF("rejected");
    	     drej.close();
       }

     } 
 } 
} 

//ClientHandler class 
class ClientHandler implements Runnable  
{ 
 Scanner scn = new Scanner(System.in); 
 private String name; 
 final DataInputStream dis; 
 final DataOutputStream dos; 
 Socket s; 
 boolean isloggedin; 
   
 // constructor 
 public ClientHandler(Socket s, String name, 
                         DataInputStream dis, DataOutputStream dos) { 
     this.dis = dis; 
     this.dos = dos; 
     this.name = name; 
     this.s = s; 
     this.isloggedin=true; 
 } 

 @Override
 public void run() { 

     String received; 
     while (true)  
     { 
         try
         { 
             // receive the string 
             received = dis.readUTF(); 
             //received=2+received;
               
             System.out.println(received); 
               
             if(received.substring(1).equals("Quit")){ 
                 this.isloggedin=false; 
                 for(int i=0;i<Server.ar.size();i++) {
                	 if(this.equals(Server.ar.get(i))) {
                		 Server.names.remove(i);
                		 Server.ar.remove(i);
                	 }
                 }
                 this.s.close(); 
                 break; 
             }
             
             else if(received.substring(1).equals("GetMemberList")){ 
                 String x="[";
            	 for(int i=0;i<Server.names.size();i++) {
                	 if(Server.names.get(i).length()<10) {
                		 x=x+Server.names.get(i)+", ";
                	 }
                 }
            	 x=x+"]";
            	 dos.writeUTF(x);
             } 
             else {
             // break the string into message and recipient part 
             StringTokenizer st = new StringTokenizer(received, "#"); 
             String source = st.nextToken();
             String Destination = st.nextToken(); 
             //int TTL = Integer.parseInt(st.nextToken());
             String MsgToSend = st.nextToken(); 

             // search for the recipient in the connected devices list. 
             // ar is the vector storing client of active users 
             
             
             if (Destination.equalsIgnoreCase(("all")))  
             {
            	 for (ClientHandler mc : Server.ar){
            		 mc.dos.writeUTF(this.name+" : "+MsgToSend);
        			 //Server.ar.get(i).dos.writeUTF(this.name+" : "+MsgToSend);
        			
        			 //DataOutputStream dOut = new DataOutputStream(Server.ar.get(i).s.getOutputStream());
						//dOut.writeUTF(this.name+" : "+MsgToSend);
        		 }
             } 
             else {
            	 boolean exists=false;
             for (int j=0;j<Server.names.size();j++)  
             { 
                 // if the recipient is found, write on its 
                 // output stream 
            	 
                 if (Server.names.get(j).equals(Destination) && Server.ar.get(j).isloggedin==true)  
                 { 
                	 if(this.name.length()>10) {
                		 StringTokenizer b = new StringTokenizer(this.name, "#"); 
                         String b1 = b.nextToken();
                         Server.ar.get(j).dos.writeUTF(b1.substring(1)+" : "+MsgToSend);
                	 }
                	 else
                	 Server.ar.get(j).dos.writeUTF(this.name+" : "+MsgToSend);
                     exists=true;
                     break; 
                 } 
                 
             }
             for (int j=0;j<Server.names.size();j++) {
            	 if (Server.names.get(j).equals(source.substring(1))) {
                	 Server.ar.get(j).dos.writeUTF(source.substring(1)+" : "+MsgToSend);
            	 }
            	 if (Server.names.get(j).equals(source.substring(2))) {
                	 Server.ar.get(j).dos.writeUTF(source.substring(2)+" : "+MsgToSend);
            	 }
            	 if (Server.names.get(j).equals(source)) {
                	 Server.ar.get(j).dos.writeUTF(source+" : "+MsgToSend);
            	 }
             }
             if(!exists) {
            	 //System.out.println("does not exist");
            	 if(received.charAt(0)=='2') {
            		 received=received.substring(1);
            		 received=1+received;
            		 Socket sc = new Socket("localhost", 5000); 
                     DataOutputStream dosc = new DataOutputStream(sc.getOutputStream());
                     dosc.writeUTF(received);
                     dosc.writeUTF(received);
            		 
            	 }
            	 else if(received.charAt(0)=='1') {
            		 received=received.substring(1);
            		 received=0+received;
            		 Socket sc = new Socket("localhost", 5000); 
                     DataOutputStream dosc = new DataOutputStream(sc.getOutputStream());
                     dosc.writeUTF(received);
                     dosc.writeUTF(received);

            	 }
            	 else if(received.charAt(0)=='0') {
            		 System.out.println("deleted");
            		 for (int j=0;j<Server.names.size();j++)  
                     { 
                         // if the recipient is found, write on its 
                         // output stream 
                    	 
                         if (Server.names.get(j).equals(source.substring(1)) && Server.ar.get(j).isloggedin==true)  
                         { 
                        	 if(this.name.length()>10) {
                        		 StringTokenizer b = new StringTokenizer(this.name, "#"); 
                                 String b1 = b.nextToken();
                                 Server.ar.get(j).dos.writeUTF("("+b1.substring(1)+" : "+MsgToSend+") was not sent due to not existence of destination");
                        	 }
                        	 else
                        	 Server.ar.get(j).dos.writeUTF(this.name+" : "+MsgToSend);
                             exists=true;
                             break; 
                         } 
                         
                     }
            	 }
            	 
             }
         }
         }
         } catch (IOException e) { 
               
             e.printStackTrace(); 
         } 
           
     } 
     try
     { 
         // closing resources 
         this.dis.close(); 
         this.dos.close(); 
           
     }catch(IOException e){ 
         e.printStackTrace(); 
     } 
 } 
} 
