package sample;

import com.jcraft.jsch.*;
import java.io.*;
import com.jcraft.jsch.*;
import java.util.*;

public class Konsep {
    String status;
    static String username;
    static String hostname;
    String inputcommand;
    String output;
    static Session session;
    private Session sesConnection;

    JSch jsch = new JSch();

    public String status(String stringstatus) {
        stringstatus = status;
        return stringstatus;
    }

    public String InputCommand(String inputcommandstatus) {
        inputcommandstatus = inputcommand;
        return inputcommandstatus;
    }

    public void connect(String usernamelokal, String hostnamelokal,
                        String password, int port) {
                //JSch jsch=new JSch();
                jsch.setConfig("StrictHostKeyChecking", "no");
        try {
            Session sessionlokal = jsch.getSession(usernamelokal,hostnamelokal, port);
            sessionlokal.setPassword(password);
            //UserInfo ui = new UserInfoku.Infoku();
            //sessionlokal.setUserInfo(ui);
            sessionlokal.setTimeout(0);
            sessionlokal.connect();
            status = "tersambung \n";
            username = usernamelokal;
            hostname = hostnamelokal;
            session = sessionlokal;
            System.out.println(username + " " + hostname);
        } catch (Exception e) {
            System.out.println(e);
            status = "Exception = \n " + e + "\n";

        }
    }

    public void disconnect() {
        //        JSch jsch=new JSch();
        try {
            Session sessionlokal = jsch.getSession(username, hostname);
            //            System.out.println(username +" "+ hostname);
            sessionlokal.disconnect();
            status = "wes pedhoott \n";
        } catch (Exception e) {
            System.out.println(e);
            status = "Exception = \n " + e + "\n";
        }

    }

    public String sendCommand(String command)
    {
        StringBuilder outputBuffer = new StringBuilder();

        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while(readByte != 0xffffffff)
            {
                outputBuffer.append((char)readByte);
                readByte = commandOutput.read();
            }

            channel.disconnect();
        }
        catch(IOException ioX)
        {
            //logWarning(ioX.getMessage());
            return null;
        }
        catch(JSchException jschX)
        {
            //logWarning(jschX.getMessage());
            return null;
        }

        return outputBuffer.toString();
    }

    public void addRoute() {
        //        JSch jsch=new JSch();
        System.out.println(username + " " + hostname);
        try {
            Session sessionlokal = session; // =jsch.getSession(username, hostname);
            Channel channel = sessionlokal.openChannel("exec");
            ((ChannelExec) channel).setCommand(inputcommand);
            channel.setInputStream(null);
            channel.connect();
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: "
                            + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channel.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}