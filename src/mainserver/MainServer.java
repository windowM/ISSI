package mainserver;

import socket.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainClass {
    private ServerSocket server;                                // 서버소켓 생성
    private int userCount=0;
    ArrayList<UserClass> user_list;                             //사용자들 관리
    public static void main(String[] args) {                             //메인은 호출만 담당.
        new MainClass();
    }

    public MainClass() {  //생성자
        try {
            user_list = new ArrayList<UserClass>();                 //유저리트 생성
            server = new ServerSocket(8888);                    //서버 소켓 생성
            System.out.println("서버 생성...");
            ConnectionThread thread = new ConnectionThread();       //연결담당 스레드 생성
            thread.start();                                          //스레드 병렬처리를 위한 start()
        } catch (IOException e) {
            e.printStackTrace();                                             //오류메시지 출력
        }
    }

    class ConnectionThread extends Thread {                                     //연결담당 스레드(상시대기) 연결이 없으면 대기

        @Override
        public void run() {
            try {
                while (true) {                                              //언제나 연결을 상시대기를 위해 while(ture)
                    Socket socket= server.accept();                                    //접속이 없으면 멈춰있음
                    NickNameThread thread = new NickNameThread(socket);      //닉네임을 받아 처리하는 스레드, 클라이언트 소캣을 파라미터로
                    thread.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class NickNameThread extends Thread {                                                                    //전달받은 소켓을 통해 정보를 주고받음
        Socket socket;                                                                                            //소켓생성
        public NickNameThread(Socket socket) {
            this.socket = socket;                                                                                //받아온 소켓을 저장
        }
        @Override
        public void run() {
            try {
                BufferedReader bIn=new BufferedReader(new InputStreamReader(socket.getInputStream()));      //문자단위로 읽어오기 위해 inputstreamreader(inputStram객체), 문자열 단위로 읽어오기 위해 bufferedReader
                String nickName = bIn.readLine();

                System.out.println(nickName+"님 입장하셨습니다.");


                UserClass user = new UserClass(nickName, socket);                                                   //유저정보를 관리하는 객체 생성성
                user.start();
                user_list.add(user);                                                                                      // 유저 추가
                sendToClient( nickName + "님이 참여하셨습니다. <"+userCount+">","SERVER");                                       //동기화, 다른 클라이언트들에게 전송

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class UserClass extends Thread {                                                                        //사용자정보를 관리하는 클래스(메시지 뿌려주는 기능)
        String nickName;
        Socket socket;
        BufferedReader bIn;
        PrintWriter bOut;

        public UserClass(String nickName, Socket socket) {
            try {
                this.nickName = nickName;
                this.socket = socket;
                bIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bOut = new PrintWriter(socket.getOutputStream());
                userCount++;
                System.out.println("현재 참여 인원 : "+userCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg;
                    if((msg= bIn.readLine())!=null) {                    //클라이언트에게 메세지를 수신받는다.
                        sendToClient(msg, nickName);        //사용자들에게 메시지 전송송
                        System.out.println(nickName + ":" + msg);
                    }
                    else{
                        userCount--;
                        System.out.println(nickName+"님이 퇴장하셨습니다.");
                        System.out.println("현재 참여 인원 : "+userCount);
                        sendToClient(nickName+"님이 퇴장하셨습니다. <" +userCount+">","SERVER");

                        break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    bIn.close();
                    bOut.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void sendToClient(String msg, String nickName) {             //스레드간 동기화,현재 데이터를 사용하고 있는 해당 스레드를 제외하고 나머지 스레드들은 데이터에 접근 할 수 없도록 막는 개념입니다.

        try {
            for (UserClass user : user_list) {
                user.bOut.println(nickName);
                user.bOut.println(msg);
                user.bOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

