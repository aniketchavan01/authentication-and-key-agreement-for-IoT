
import java.io.*;
import java.net.*;
import java.util.Random;
public class controlServer {
    public static void main(String[] args) {
        try{


            //calculates start time
            double start = System.currentTimeMillis();

            //creating socket for communication with port
            Socket s=new Socket("localhost",6656);
            Socket userPort=new Socket("localhost",6658);


            //generating secret s1
            Random random = new Random();

            long ID = random.nextInt();
            String part_ID = String.valueOf(ID);
            System.out.println("complete ID  => "+part_ID);

            long Bi = random.nextInt();
            String part_Bi = String.valueOf(Bi);
            System.out.println("complete Biometric value  => "+part_Bi);

            long x = random.nextInt();
            String part_x = String.valueOf(x);
            System.out.println("complete x value  => "+part_x);

            long y = random.nextInt();
            String part_y = String.valueOf(y);
            System.out.println("complete y value  => "+part_y);

            long Pi = random.nextInt();
            String part_Pi = String.valueOf(Pi);
            String Pi_2= part_Pi.substring(0,5);

            System.out.println("Complete Pi  => "+part_Pi);
            System.out.println("Partial Pi  => "+Pi_2);

            System.out.println("Sending ID, Bio, Password information =>");

            // Sending s1 to server
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());
            dout.writeUTF(part_ID);
            System.out.println(" Sending ID to cloud server => "+part_ID);


            //recieve
            DataInputStream dis=new DataInputStream(s.getInputStream());
            DataInputStream userIn=new DataInputStream(userPort.getInputStream());

            String  SID=userIn.readUTF();
            System.out.println("SID received from cloud server => "+SID);

            String  d=userIn.readUTF();
            System.out.println("d received from cloud server => "+d);

            System.out.println("concatenation of SID and d => "+(d+SID+y));
            String  Ai = userIn.readUTF();

            System.out.println("Ai received from User = "+Ai);
            String  PIDi = userIn.readUTF();

            System.out.println("PIDi received from User = "+PIDi);

           String hash_value = Hash.toHexString(Hash.getSHA(d+SID));
           System.out.println("hash of d : " +hash_value);

           String Bsm_value  = Hash.toHexString(Hash.getSHA(d+hash_value+y));
           System.out.println("hash of Bsm : " +Bsm_value);

           dout.writeUTF(String.valueOf(Bsm_value));

            String Ci  = Hash.toHexString(Hash.getSHA(PIDi+Ai));
            System.out.println("hash of Ci : " +Ci);
            String Di  = Hash.toHexString(Hash.getSHA(PIDi+x));
            System.out.println("hash of Di : " +Di);
            Long Ei = Long.parseLong((Di.substring(0,5)),16) ^ (Long.parseLong((Ai.substring(0,5)),16));
            System.out.println("Ei  => "+Ei);

            //sending data to sc of user
            System.out.println("Sending . .");

            DataOutputStream userOut=new DataOutputStream(userPort.getOutputStream());

            userOut.writeUTF(Ci);
            userOut.writeUTF(String.valueOf(Ei));
            userOut.writeUTF(Hash.toHexString(Hash.getSHA(Ci.substring(0,5)+Di.substring(0,5))));
            System.out.println("Sent");

            //generate and check timestamp
            long TS = TimeStamp.getTimeStampStatus();
            System.out.println("TimeStamp "+TS);
            long TSi = 0;
            Boolean TsStatus = TimeStamp.isValidTimestamp(TS,TSi);
            if(TsStatus == true)
                System.out.println("Generating a random number..");
            else
                System.out.println("Connection Rejected");



            dout.flush();
            dout.close();
            s.close();

            Runtime runtime = Runtime.getRuntime();

            // Run the garbage collector
            runtime.gc();

            // Calculate the used memory
            long memory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory used => "+memory/1024+" KB");

            long end = System.currentTimeMillis();
            System.out.println("Total time =>  "+(end-start)+"ms");
            System.out.println("Total time =>  "+((end-start)/1000)+"sec");

        }catch(Exception e){System.out.println(e);}
    }
}
