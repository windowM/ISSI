package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import static socket.Server.message_outputList;

public class ClientManagerThread extends Thread{
    private Socket m_socket;
    private String m_ID;

    @Override
    public void run() {
        try{
            BufferedReader in=new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
            String text;
            while(true){
                text=in.readLine();
                if(text!=null){
                    for(int i=0;i<message_outputList.size();i++){
                        message_outputList.get(i).println(text);
                        message_outputList.get(i).flush();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void setSocket(Socket m_socket){
        this.m_socket=m_socket;
    }
}
