package com.example.finalchatting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    boolean isConnect=false;
    EditText edit1;
    Button btn1;
    LinearLayout container;
    ScrollView scroll;
    ProgressDialog pro;     //실시간 진행상태를 알려주는 클래스
    boolean isRunning =false;
    Socket member_socket;       //서버와 연결된 소켓객체
    String user_nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {        //접속버튼을 누르면 스레드가 동작하는 방식
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit1=findViewById(R.id.editText);
        btn1=findViewById(R.id.button);
        container=findViewById(R.id.container);
        scroll=findViewById(R.id.scroll);
    }

    public void btnMethod(View v){
        if(isConnect==false){
            String nickname=edit1.getText().toString();
            if(nickname.length()>0 && nickname!=null){
                pro= ProgressDialog.show(this,null,"접속중입니다.");

                ConnectionThread thread=new ConnectionThread();
                thread.start();
            }

            else{       //닉네임이 입력되지 않았을경우
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage("닉네임을 입력해주세요.");
                builder.setPositiveButton("확인",null);
                builder.show();
            }
        }else{
            String msg=edit1.getText().toString();
            System.out.println("서버에 전송 : "+msg);
            SendToServerThread thread=new SendToServerThread(member_socket,msg);
            thread.start();
        }
    }

    class ConnectionThread extends Thread {     //서버와 연결하고 스트림을 구성을 담당
        @Override
        public void run() {
            try{
                Socket socket=new Socket("192.168.0.118",8888);
                member_socket=socket;
                String nickName=edit1.getText().toString();
                user_nickname=nickName;

                PrintWriter bOut=new PrintWriter(socket.getOutputStream());
                bOut.println(nickName);   //닉네임 송신
                bOut.flush();
                runOnUiThread(new Runnable() {      //Runnable 인터페이스를 구현한 클래스를 통해 뷰를 수정해야 비동기식 문제를 처리할 수 있다.>>
                    @Override
                    public void run() {
                        pro.dismiss();
                        edit1.setText("");
                        edit1.setHint("메시지 입력");
                        btn1.setText("전송");
                        isConnect=true;
                        isRunning=true;
                        System.out.println("클라이언트//"+"연결완료");

                        MessageThread thread=new MessageThread(socket);
                        thread.start();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }finally{
               // if(!=null)
            }
        }
    }

    class MessageThread extends Thread{     //전달받은 메시지 객체를 불러들여 textEdit에 추가해주는 역활
        Socket socket;
        BufferedReader bIn=null;

        public MessageThread(Socket socket){
            try{
                this.socket=socket;
                bIn=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                while(isRunning){
                    System.out.println("msg문자//"+"대기중");
                    String msg=bIn.readLine();
                    System.out.println("msg에 문자담김");
                    runOnUiThread(new Runnable() {//현재 스레드가 메인 스레드인지 여부를 검사하여 메인 스레드가 아니라면 post() 메서드를 실행하고, 메인 스레드라면 Runnable의 run() 메서드를 직접 실행합니다.
                        @Override
                        public void run() {
                            //텍스트뷰 객체를 생성
                            TextView tv=new TextView(MainActivity.this);
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                            /*
                            //메시지의 시작 이름이 내 닉네임과 일치한다면
                            if(msg.startsWith(user_nickname)){
                                tv.setBackgroundResource(R.drawable.me);
                            }
                            else{
                                tv.setBackgroundResource(R.drawable.you);
                            }

                             */
                            tv.setText(msg);
                            container.addView(tv);      //대화메세지를 계속 추가
                            scroll.fullScroll(View.FOCUS_DOWN); //제일 하단으로 스크롤
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SendToServerThread extends Thread{
        Socket socket;
        String msg;
        PrintWriter bOut;

        public SendToServerThread(Socket socket,String msg){
            try{
                this.socket=socket;
                this.msg=msg;
                bOut=new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                bOut.println(msg);        //서버로 데이터 보내기.
                System.out.println("sendToServerThread//"+msg);
                bOut.flush();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edit1.setText("");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        try{
            member_socket.close();
            isRunning=false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}