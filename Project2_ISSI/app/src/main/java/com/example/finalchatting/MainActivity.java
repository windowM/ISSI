package com.example.finalchatting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    boolean isConnect=false;
    boolean isRunning =false;

    EditText edit1;
    Button btn1;
    LinearLayout container;
    TextView ID;
    TextView userCount;
    LinearLayout title;
    ScrollView scroll;
    ProgressDialog pro;     //실시간 진행상태를 알려주는 클래스
    Socket member_socket;       //서버와 연결될 소켓객체
    String user_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //접속버튼을 누르면 스레드가 동작하는 방식
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        ID=findViewById(R.id.ID);
        edit1=findViewById(R.id.editText);
        btn1=findViewById(R.id.button);
        userCount=findViewById(R.id.userCount);
        container=findViewById(R.id.container);
        title=findViewById(R.id.title);
        scroll=findViewById(R.id.scroll);
    }

    public void btnMethod(View v) {
        if (!isConnect) {       //연결이 되어있지 않다면 이름을 받고 연결 스레드 실행
            String nickName = edit1.getText().toString();
            if (nickName.length() > 0 ) {
                pro = ProgressDialog.show(this, null, "접속중입니다.");

                ConnectionThread thread = new ConnectionThread();
                thread.start();
            } else {       //닉네임이 입력되지 않았을경우
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("닉네임을 입력해주세요.");
                builder.setPositiveButton("확인", null);
                builder.show();
            }
        } else {                    //연결되었다면 문자 송수신 스레드 실행
            String msg = edit1.getText().toString();
            if (msg.trim().length() > 0  ) {
                SendToServerThread thread = new SendToServerThread(member_socket, msg);
                thread.start();
            } else {
                edit1.setText("");
                Toast.makeText(this, "TEXT를 입력하시오.", Toast.LENGTH_SHORT).show();

            }

        }
    }
    class ConnectionThread extends Thread {     //서버와 연결하고 스트림을 구성을 담당
        @Override
        public void run() {
            try{
                Socket socket=new Socket("192.168.0.118",8888);         //집 : 192.168.0.9, 연구실 : 192.168.0.118
                member_socket=socket;
                String nickName=edit1.getText().toString();
                user_nickname=nickName;

                BufferedWriter bOut=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bOut.write(nickName+"\n");
                bOut.flush();

                runOnUiThread(new Runnable() {      //서브스레드에서 UI를 다루기 위해 사용.Runnable 인터페이스를 구현한 클래스를 통해 뷰를 수정해야 비동기식 문제를 처리할 수 있다.>>
                    @Override
                    public void run() {
                        pro.dismiss();
                        edit1.setText("");
                        edit1.setHint("메시지 입력");
                        btn1.setText("전송");
                        ID.setText(user_nickname);
                        title.setVisibility(View.VISIBLE);
                        isConnect=true;
                        isRunning=true;
                        System.out.println("클라이언트//"+"연결완료");
                        MessageThread thread=new MessageThread(socket); //접속한 클라이언트 소켓
                        thread.start();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
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
                    String nickName=bIn.readLine();
                    String msg=bIn.readLine();

                    runOnUiThread(new Runnable() {//현재 스레드가 메인 스레드인지 여부를 검사하여 메인 스레드가 아니라면 post() 메서드를 실행하고, 메인 스레드라면 Runnable의 run() 메서드를 직접 실행합니다.
                        @Override
                        public void run() {
                            //텍스트뷰 동적할당
                            TextView showName=new TextView(MainActivity.this);
                            TextView showText=new TextView(MainActivity.this);
                            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                            showName.setText(nickName);
                            showName.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                            showName.setTypeface(null, Typeface.BOLD);

                            showText.setText(msg);
                            showText.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);


                            if(user_nickname.equals(showName.getText().toString())){
                                showText.setBackgroundResource(R.drawable.me);
                                showText.setTextColor(Color.BLACK);
                                lp.gravity= Gravity.RIGHT;
                                lp.setMargins(20,0,0,20);

                            }
                            else if(showName.getText().toString().equals("SERVER")){
                                Toast.makeText(MainActivity.this, "서버에서 보낸 메시지", Toast.LENGTH_SHORT).show();
                                showText.setBackgroundColor(Color.RED);
                                showText.setTextColor(Color.BLACK);
                                userCount.setText(""+msg.charAt(msg.length()-2));
                                lp.gravity= Gravity.CENTER;
                                lp.setMargins(20,0,20,20);
                            }
                            else{
                                showText.setBackgroundResource(R.drawable.your);
                                showText.setTextColor(Color.WHITE);
                                lp.gravity=Gravity.LEFT;
                                lp.setMargins(0,0,20,20);
                            }

                            showName.setLayoutParams(lp);
                            showText.setLayoutParams(lp);
                            container.addView(showName);
                            container.addView(showText);      //대화메세지를 계속 추가
                            scroll.fullScroll(ScrollView.FOCUS_DOWN); //제일 하단으로 스크롤
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();

            }finally{
                try {
                    bIn.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendToServerThread extends Thread{
        Socket socket;
        String msg;
        BufferedWriter bOut;

        public SendToServerThread(Socket socket,String msg){
            try{
                this.socket=socket;
                this.msg=msg;
                bOut=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                bOut.write(msg+"\n");        //서버로 데이터 보내기.
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            member_socket.close();
            isRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}