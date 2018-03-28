package sample;

import com.jcraft.jsch.*;
import java.io.*;
import java.sql.*;

public class JschExecutor2
{
    Session session=null;
    Session secondSession=null;

    public void dc()
    {
        //secondSession.disconnect();
        session.disconnect();
    }
    public void go4pascal() throws Exception
    {
        JSch jsch=new JSch();
        /*Session*/ session=jsch.getSession("4bolba", "149.156.109.180", 22);
        session.setPassword("marcelino5");
        localUserInfo lui=new localUserInfo();
        session.setUserInfo(lui);
        session.setConfig("StrictHostKeyChecking", "no");
        // create port from 2233 on local system to port 22 on tunnelRemoteHost
        session.setPortForwardingL(5432, "127.0.0.1", 5432);
        session.connect();
    }
    public void go() throws Exception{

        StringBuilder outputBuffer = new StringBuilder();

        String host="taurus.fis.agh.edu.pl"; // First level target
        String user="4bolba";
        String password="marcelino5";
        String tunnelRemoteHost="pascal"; // The host of the second target
        String secondPassword="marcelino5";
        int port=22;


        JSch jsch=new JSch();
        /*Session*/ session=jsch.getSession(user, host, port);
        session.setPassword(password);
        localUserInfo lui=new localUserInfo();
        session.setUserInfo(lui);
        session.setConfig("StrictHostKeyChecking", "no");
        // create port from 2233 on local system to port 22 on tunnelRemoteHost
        session.setPortForwardingL(5432, tunnelRemoteHost, 22);
        session.connect();
        session.openChannel("direct-tcpip");


        // create a session connected to port 2233 on the local host.
        /*Session*/ secondSession = jsch.getSession(user, "localhost", 5432);
        secondSession.setPassword(secondPassword);
        secondSession.setUserInfo(lui);
        secondSession.setConfig("StrictHostKeyChecking", "no");

        secondSession.connect(); // now we're connected to the secondary system
        Channel channel=secondSession.openChannel("exec");
        ((ChannelExec)channel).setCommand("pwd");

        channel.setInputStream(null);

        InputStream stdout=channel.getInputStream();

        channel.connect();

        while (true) {
            byte[] tmpArray=new byte[1024];
            while(stdout.available() > 0){
                int i=stdout.read(tmpArray, 0, 1024);
                if(i<0)break;
                outputBuffer.append(new String(tmpArray, 0, i));
            }
            if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
        }
        stdout.close();

        channel.disconnect();



        //secondSession.disconnect();
        //session.disconnect();

        System.out.print(outputBuffer.toString());
    }

    class localUserInfo implements UserInfo{
        String passwd;
        public String getPassword(){ return passwd; }
        public boolean promptYesNo(String str){return true;}
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){return true; }
        public boolean promptPassword(String message){return true;}
        public void showMessage(String message){}
    }

}