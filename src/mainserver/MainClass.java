package mainserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainClass {
    private ServerSocket server;                                // 서버소켓 생성
    ArrayList<UserClass> user_list;                             //사용자들 관리

    public static void main(String[] args) {                             //메인은 호출만 담당.
        new MainClass();
    }

    public MainClass() {  //생성자
        try {
            user_list = new ArrayList<UserClass>();                 //유저리트 생성
            server = new ServerSocket(8888);                    //서버 소켓 생성
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
                    System.out.println("접속 대기중...");
                    Socket socket = server.accept();                                    //접속이 없으면 멈춰있음
                    System.out.println("접속완료");

                    NickNameThread thread = new NickNameThread(socket);      //닉네임을 받아 처리하는 스레드
                    thread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     /*
                InputStream is = socket.getInputStream();   //인풋 통로(byte단위)
                OutputStream os = socket.getOutputStream();  //아웃풋 통로
                DataInputStream dis = new DataInputStream(is);      //인간이쓰는 문자(!byte)로 변환을 위해 사용, DataInputStream은 필터 클래스이기에 파라미터가 inputstream 이여야함
                */


    class NickNameThread extends Thread {                                                                    //전달받은 소켓을 통해 정보를 주고받음
        Socket socket;                                                                                            //소켓생성
        public NickNameThread(Socket socket) {
            this.socket = socket;                                                                                //받아온 소켓을 저장
        }
        @Override
        public void run() {
            try {
                BufferedReader bIn=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter bOut=new PrintWriter(socket.getOutputStream());


                String nickName = bIn.readLine();                                                               //클라이언트 이름 기록
                bOut.println(nickName + "님 입장");                                                               //출력스트림으로 전달.
                bOut.flush();
                System.out.println(nickName+"님 입장하셨습니다.");

                sendToClient("서버 : " + nickName + "님이 접속하였습니다.");                                       //동기화

                UserClass user = new UserClass(nickName, socket);                                                   //유저정보를 관리하는 객체 생성성
                user.start();
                user_list.add(user);                                                                                      // 유저 추가

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

        public UserClass(String nickName, Socket socket) throws IOException {
            try {
                this.nickName = nickName;
                this.socket = socket;
                bIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bOut = new PrintWriter(socket.getOutputStream());
                //System.out.println("userClass//"+bIn.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = bIn.readLine();                    //클라이언트에게 메세지를 수신받는다.
                    sendToClient(nickName + " : " + msg);        //사용자들에게 메시지 전송송
                    System.out.println("nickname : //"+msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendToClient(String msg) {             //스레드간 동기화,현재 데이터를 사용하고 있는 해당 스레드를 제외하고 나머지 스레드들은 데이터에 접근 할 수 없도록 막는 개념입니다.

        try {
            for (UserClass user : user_list) {
                user.bOut.println(msg);
                user.bOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

