import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.text.*;


class slaveSocketDetails{
Socket 	slaveSocket;
String 	regDate;
String 	slaveHostIP;
String 	slaveAddressName;
int	    slaveportNumber;

  slaveSocketDetails() {
	Date currDate = new Date();
	regDate= new SimpleDateFormat("yyyy-MM-dd").format(currDate);
  }
}

public class MasterBot {
    public static String userinputLine;
    public static ArrayList<slaveSocketDetails> slaveList = new ArrayList<slaveSocketDetails>();
    
    //Function to list all slaves connected to the Master
    
    public static void listSlaves(){
        for(int i=0;i<slaveList.size(); i++){
            System.out.println(slaveList.get(i).slaveAddressName+" "+slaveList.get(i).slaveHostIP + " "+ slaveList.get(i).slaveportNumber + " "+ slaveList.get(i).regDate);
        }
    }
    
    
     
    // Function to connect slave to Target
    
    public static void connect(String [] connectCommand, int conn){
        if(connectCommand.length<4 ){
            System.out.println("For Connect atleast 3 arguments are needed");
        }
        else if (connectCommand[1].equals("all")){
            Iterator<slaveSocketDetails> i = slaveList.iterator();
            while(i.hasNext()){
                slaveSocketDetails currSocket = i.next(); 
                try{
                    PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.slaveSocket.getOutputStream()));
                    slaveconnOutput.println("connect "+ connectCommand[2] + " "+ connectCommand[3] + " "+conn);
                    slaveconnOutput.flush();
                }
                catch(Exception e){
                    System.err.println("Error connecting "+ connectCommand[2] +" to port" + connectCommand[3]);
                }
            }
        }
        else{
            Iterator<slaveSocketDetails> i = slaveList.iterator();
            while(i.hasNext()){
                slaveSocketDetails currSocket = i.next(); 
                if(currSocket.slaveHostIP.equals(connectCommand[1]) || currSocket.slaveAddressName.equals(connectCommand[1])){
                    try{
                        PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.slaveSocket.getOutputStream()));
                        slaveconnOutput.println("connect "+ connectCommand[2] + " "+ connectCommand[3] + " "+conn);
                        slaveconnOutput.flush();
                    }
                    catch(Exception e){
                        System.err.println("Error connecting "+ connectCommand[2] +" to port" + connectCommand[3]);
                    }
                }
                
            }
        }
    }
    
    
    //Function to disconnect slave from Target
    
    public static void disconnect(String [] disConnectCommand){
        if (disConnectCommand.length<3) {
            System.out.println("For disconnect atleast 2 arguments are needed");
        }
        else if(disConnectCommand[1].equals("all")){
            Iterator<slaveSocketDetails> i = slaveList.iterator();
            while(i.hasNext()){
                slaveSocketDetails currSocket = i.next();
                try{
                    PrintWriter  slavedisconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.slaveSocket.getOutputStream()));
                    slavedisconnOutput.println("disconnect "+ disConnectCommand[2] + " "+ disConnectCommand[3]);
                    slavedisconnOutput.flush();
                }
                catch(Exception e){
                    System.err.println("Error connecting "+ disConnectCommand[2] +" " + disConnectCommand[3]); 
                }
            }
        }
        else {
            Iterator<slaveSocketDetails> i = slaveList.iterator();
            while(i.hasNext()){
                slaveSocketDetails currSocket = i.next();
                if (currSocket.slaveHostIP.equals(disConnectCommand[1]) || currSocket.slaveAddressName.equals(disConnectCommand[1]) ){
                    try{
                        PrintWriter  slavedisconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.slaveSocket.getOutputStream()));
                        slavedisconnOutput.println("disconnect "+ disConnectCommand[2] + " "+ disConnectCommand[3]);
                        slavedisconnOutput.flush();
                    }
                    catch(Exception e){
                        System.err.println("Error connecting "+ disConnectCommand[2] +" " + disConnectCommand[3]); 
                    }
                }
            }
        }
    }
    
    //Main
    public static void main(String[] args) throws Exception {
    if (args.length<2){
         System.err.println("A port number for master should be provided");
         System.exit(1);	
	}
    else if (!args[0].equals("-p"))
    {
        System.err.println("A parameter for port number with -p should be specified");
	    System.exit(1);
    }
   else
    {
         try {
            String portno="";
            portno=args[1];
            SocketThread slaveThread = new SocketThread(Integer.parseInt(portno));
            slaveThread.start();   
            while(true){
                System.out.print(">");
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                userinputLine= userInput.readLine();
                String[] masterCommand = userinputLine.split(" ");
                
                
                if (masterCommand[0].equals("list")){
					MasterBot.listSlaves();
                }
                
                else if(masterCommand[0].equals("connect")) {
                    int noofConn=1;
                    if(masterCommand.length>4){
				        noofConn= Integer.parseInt(masterCommand[4]);
                    }
                    MasterBot.connect(masterCommand,noofConn);
				}
                
                else if(masterCommand[0].equals("disconnect")){
    				if(masterCommand[1] != null && masterCommand[2]!= null){    	
				        int portnum=0;
				        if(masterCommand.length>3){
				            portnum= Integer.parseInt(masterCommand[3]);
				        }
                        MasterBot.disconnect(masterCommand);
                       
                    }
                    else
                        System.err.println("Provide valid arguments for disconnect");         
                }
                else
                    System.err.println("Provide a valid command");     
            }
        }
        catch (Exception e) {
	      	e.printStackTrace();
            System.exit(-1);	    
        }
     }
   }
}

                                     
class SocketThread extends Thread {
  int portnum;
  slaveSocketDetails slavesocketlist;
  SocketThread(int port) {
    portnum = port;
  }
  public void run() {
      try {
          ServerSocket masterSocket = new ServerSocket(portnum);
		  while (true) {
              Socket slaveSocket = masterSocket.accept();
              slavesocketlist=new slaveSocketDetails();
              slavesocketlist.slaveHostIP=slaveSocket.getInetAddress().getHostAddress();
              slavesocketlist.slaveAddressName=slaveSocket.getInetAddress().getHostName();
              slavesocketlist.slaveportNumber=slaveSocket.getPort();
              slavesocketlist.slaveSocket=slaveSocket;
              MasterBot.slaveList.add(slavesocketlist);
		  }
	    } 
      catch (Exception e) {
	      e.printStackTrace();
      }
  }
}


