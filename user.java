
import java.io.*;
import java.net.*;
import java.util.Random;
public class user {
    public static void main(String[] args) {
        try{



            //calculates start time
            double start = System.currentTimeMillis();

            //creating socket for communication with port
            Socket s=new Socket("localhost",6657);
            ServerSocket userPort=new ServerSocket(6658);

//*********************************************************************************REGI. PHASE**************************************************************************************************************************
/* Registration phase
* Generating parameters required for communication
* Sending parameters (ID,PID,Ai) to Control server
* receiving from control server (Ci, Di, H(.))
*  */
            // Generating parameters required for communication
            Random random = new Random();

            long b = random.nextInt();
            String part_b = String.valueOf(b);
            System.out.println("complete b  => "+part_b);

            long ID = random.nextInt();
            String part_ID = String.valueOf(ID);
            System.out.println("complete ID  => "+part_ID);

            long Bi = random.nextInt();
            String part_Bi = String.valueOf(Bi);
            String Bi_2= part_Bi.substring(0,5);
            System.out.println("complete Biometric value  => "+Bi_2);
            String Hash_Bi = Hash.toHexString(Hash.getSHA(Bi_2));

            long Pi = random.nextInt();
            String part_Pi = String.valueOf(Pi);
            String Pi_2= part_Pi.substring(0,5);
            System.out.println("Complete Pi  => "+part_Pi);
            System.out.println("Partial Pi  => "+Pi_2);

            String PIDi = Hash.toHexString(Hash.getSHA(part_ID+part_Bi));
            System.out.println("PIDi  => "+PIDi);

            Long Ai = (Long.parseLong(Pi_2,16) ^ Long.parseLong((Hash_Bi.substring(0,5)),16));
            System.out.println("Ai  => "+Ai);
            Long omega = (Long.parseLong(part_b) ^ Ai);
            System.out.println("omega  => "+omega);


            // Sending parameters (ID,PID,Ai) to Control server
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());
            DataOutputStream userout=new DataOutputStream(userPort.getOutputStream());

            userout.writeUTF(part_ID);
            userout.writeUTF(String.valueOf(Ai));
            userout.writeUTF(PIDi);

            //receiving from control server (Ci, Di, H(.))
            DataInputStream userIn=new DataInputStream(userPort.getInputStream());
            String  Ci=userIn.readUTF();
            System.out.println("Ci : "+Ci);
            String  Ei=userIn.readUTF();
            System.out.println("Ei : "+Ei);
            String  H_dot=userIn.readUTF();
            System.out.println("H_dot : "+H_dot);
            /* sending to cloud server*/

//*******************************************************************************************LOG IN PHASE***************************************************************************************************************
            /*
            Log-In phase
            Computes parameters required for Log-in
            Sending it to cloud server(Gi, Fi, Zi, PIDi,TSi)
            Receiving response from cloud server(Pcs, Qcs)
             */

            //Compute parameters required for Log-in
            long Ai_dash = (Pi ^ Long.parseLong(Hash_Bi.substring(0,5),16));
            System.out.println("Ai` : "+Ai_dash);

            String Ci_dash = Hash.toHexString(Hash.getSHA(String.valueOf(ID+Ai_dash)));
            System.out.println("Ci` : "+Ci_dash);


            //generate and check timestamp
            long TS = TimeStamp.getTimeStampStatus();
            System.out.println("TimeStamp "+TS);
            long TSi = 0;
            Boolean TsStatus = TimeStamp.isValidTimestamp(TS,TSi);

            /*if(TsStatus == true)
                System.out.println("Generating a random number..");
            else
                System.out.println("Connection Rejected");
            */

            //Generate and send Di, Gi, Fi, Zi
            long n_i = random.nextInt();
            long SIDm = random.nextInt();

             b = omega ^ Ai;
             System.out.println("b : "+b);

            long Di = Long.parseLong(Ei) ^ Ai;
            System.out.println("Di : "+Di);

             String Gi = Hash.toHexString(Hash.getSHA(String.valueOf(PIDi+SIDm+n_i+TS+Di)));
             System.out.println("Gi  : "+Gi);

            long Fi = Di ^ n_i;
            System.out.println("Fi : "+Fi);

            String z1 = (Hash.toHexString(Hash.getSHA(String.valueOf(Di+n_i))));

            long Zi = SIDm ^ Integer.parseInt(z1.substring(0,5),16);
            System.out.println("Zi : "+Zi);

            /* sending to cloud server*/
            userout.writeUTF(Gi);
            userout.writeUTF(String.valueOf(Fi));
            userout.writeUTF(String.valueOf(Zi));
            userout.writeUTF(PIDi);
            userout.writeUTF(String.valueOf(TS));

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
