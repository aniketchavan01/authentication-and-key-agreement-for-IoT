import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class cloudServer {

        public static void main(String[] args){
            try{
                //socket for node1
                ServerSocket ss = new ServerSocket(6656);
                ServerSocket ss2 = new ServerSocket(6657);


                Socket s1=ss.accept();//establishes connection
                Socket s2= ss2.accept();
                Socket s3 = userPort.accept();
                //calculates start time
                double start = System.currentTimeMillis();

                //generating random value(Nonce) for Gid

                Random random = new Random();

                long SID = random.nextLong();
                long d = random.nextLong();

                System.out.println(" SID =>"+SID);
                System.out.println(" d =>"+d);


                //sending Gid to the nodes
                DataOutputStream din = new DataOutputStream(s1.getOutputStream());
                din.writeUTF(String.valueOf(SID));
                System.out.println("SID sent to node1");

                din.writeUTF(String.valueOf(d));
                System.out.println("d sent to node1");

                DataOutputStream din2 = new DataOutputStream(s2.getOutputStream());
                din2.writeUTF(String.valueOf(SID));
                System.out.println("SID sent to node2");

                //receiving from control server

                DataInputStream dout=new DataInputStream(s1.getInputStream());
                String  ID_ctrl = dout.readUTF();
                System.out.println("ID received from control server = "+ID_ctrl);
                String  Bsm_value = dout.readUTF();
                System.out.println("Bsm received from control server = "+Bsm_value);

                //recieving from user
                DataInputStream user_in=new DataInputStream(s3.getInputStream());

                String  Gi=user_in.readUTF();
                System.out.println("Gi received from cloud server => "+Gi);
                String  Fi=user_in.readUTF();
                System.out.println("Fi received from cloud server => "+Fi);

                String  Zi=user_in.readUTF();
                System.out.println("Zi received from cloud server => "+Zi);
                String  PIDi=user_in.readUTF();
                System.out.println("PIDi received from cloud server => "+PIDi);
                String  Ts=user_in.readUTF();
                System.out.println("Ts received from cloud server => "+Ts);

                //generate and check timestamp
                long TS = TimeStamp.getTimeStampStatus();
                System.out.println("TimeStamp "+TS);
                long TSi = 0;
                Boolean TsStatus = TimeStamp.isValidTimestamp(TS,TSi);
                if(TsStatus == true)
                    System.out.println("Generating a random number..");
                else
                    System.out.println("Connection Rejected");


                //receiving from node2
                DataInputStream dout2=new DataInputStream(s2.getInputStream());
                String  secret2=dout2.readUTF();
                System.out.println("secret s2 received from node2 = "+secret2);

                //decrypting the secret received from node2

                int decr_s2 = Integer.parseInt(secret2);
                System.out.println("Original Secret S2 => "+decr_s2);
               int decr_s1 =0;
                //calculating MSK
                long msk = Integer.valueOf(decr_s2) * Integer.valueOf(decr_s1);
                System.out.println("MSK = "+msk);
                long calc_partSecret01 =  (msk/decr_s1);
                long calc_partSecret02 =  (msk/decr_s2);



                System.out.println("Encrypted Partial secret Sent to node 1"+" => "+(calc_partSecret01));
                System.out.println("Encrypted Partial secret Sent to node 2"+" => "+(calc_partSecret02));

                //sending partial secrets
                    //Partial secret for node 1
                DataOutputStream partSecret1 = new DataOutputStream(s1.getOutputStream());
                partSecret1.writeUTF(String.valueOf(calc_partSecret01));

                // partial secret for node2
                DataOutputStream partSecret2 = new DataOutputStream(s2.getOutputStream());
                partSecret2.writeUTF(String.valueOf(calc_partSecret02));




                din.flush();
                din2.flush();
                dout.close();
                dout2.close();
                partSecret1.flush();
                partSecret2.flush();
                din.close();
                din2.close();
                partSecret1.close();
                partSecret2.close();
                ss.close(); //closing port of node1
                ss2.close();


                Runtime runtime = Runtime.getRuntime();
                // Run the garbage collector
                runtime.gc();

                // Calculate the used memory
                long memory = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Memory used => "+memory/1024+" KB");

                long end = System.currentTimeMillis();
                System.out.println("Total time =>  "+((end-start))+"ms");
                System.out.println("Total time =>  "+((end-start)/1000)+"sec");

            }catch(Exception e){System.out.println(e);}
        }
    }

