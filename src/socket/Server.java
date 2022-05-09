package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    public static ArrayList<PrintWriter> message_outputList;
    public static void main(String[] args) {
        message_outputList=new ArrayList<PrintWriter>();        //보낼 메시지 배열(printWriter클래스는 스트림에 있는 개체의 형식화된 표현을 텍스트 출력으로 인쇄.
        try{
            ServerSocket serverSocket=new ServerSocket(8888);   //서버소켓 생성, 포트번호=8888
            System.out.println(getTime()+" : 서버 실행중...");
            while(true) {
                //자바에서는 socket.accept() 메서드만 호출하면 Socket클래스 내부에서 socket() -> bind() -> listen()을 호출해준다.
                Socket c_socket = serverSocket.accept();  //서버소켓 클래스의 객체인 serverSocket으로 accept()메소드 호출     accept()=클라이언트가 들어오는 것을 대기
                //클라이언트가 8888포트로 연결을 시도하면 accept는 대기를 풀고 클라이언트와 연결시키는 socket클래스를 (c_socket 객체)생성하여 반환.
                ClientManagerThread c_thread=new ClientManagerThread();
                c_thread.setSocket(c_socket);

                message_outputList.add(new PrintWriter(c_socket.getOutputStream()));
                System.out.println(message_outputList.size());
                c_thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    static String getTime() {
        SimpleDateFormat f = new SimpleDateFormat("[hh시 mm분 ss초]");
        return f.format(new Date());
    }
}
