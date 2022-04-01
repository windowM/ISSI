package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;                  //하드코딩으로 인해 발생하는 경고-> 안뜨게하기위해 만듬
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.DecimalFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity {
    EditText eShow,eResult;

    Button bPlus,bSub,bMulti,bDivide;                                 //뷰 선언
    Button bPM,bDelete,bResult,bCE,bClear;
    Button bDiveX,bPower,bRoute,bPercent;

    Boolean checkStart=true;
    Boolean checkDot=false;
    Boolean checkOperator=false;
    Boolean checkResult=false;
    Boolean checkTrans=false;

    String number1="0";
    String number2="0";
    String history="";

    String format="#,###.################";         //3자리마다 "," , 소수점 16자리까지 뒤 0 생략
    static final String MAX="9999999999999999";         //정수 최대 16자리
    static final String MIN="0.0000000000000001";       //소수점 16자리까지

    int ADD=43;
    int SUB=45;
    int MULTI=42;
    int DIV=47;

    int DIVX=1;
    int POWER=2;
    int ROUTE=3;

    int TYPE=-1;
    int transTYPE=-1;
    int CYCLE=0;

    DecimalFormat decFormat = new DecimalFormat(format);      //소수점 16자리까지 나타내줌.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                             //레이아웃과 클래스를 연결해주는 메소드

        eShow=findViewById(R.id.show);
        eResult=findViewById(R.id.result);                                  //인스턴스 생성 후 레이아웃에서 캡처  ,  id값을 이용해 특정 뷰를 받아와주는 메소드로

        bPlus=findViewById(R.id.Button_plus);
        bSub=findViewById(R.id.Button_sub);
        bMulti=findViewById(R.id.Button_multi);
        bDivide=findViewById(R.id.Button_divide);
        bPercent =findViewById(R.id.Button_percent);
        bDiveX=findViewById(R.id.Button_divideX);
        bPower=findViewById(R.id.Button_power);
        bRoute=findViewById(R.id.Button_root);

        bResult=findViewById(R.id.Button_result);
        bCE=findViewById(R.id.Button_CE);
        bClear=findViewById(R.id.Button_clear);
        bDelete=findViewById(R.id.Button_delete);

        bPM=findViewById(R.id.Button_PM);

        bPlus.setOnClickListener(operator);
        bSub.setOnClickListener(operator);
        bMulti.setOnClickListener(operator);
        bDivide.setOnClickListener(operator);

        bDiveX.setOnClickListener(trans);
        bPower.setOnClickListener(trans);
        bRoute.setOnClickListener(trans);

        bResult.setOnClickListener(operator);
        bCE.setOnClickListener(operator);
        bClear.setOnClickListener(operator);

        bPercent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                number2=eResult.getText().toString();
                if(TYPE==MULTI  || TYPE==DIV){
                    eShow.setText(history + decFormat.format(Double.parseDouble(eResult.getText().toString()) * 0.01));
                    eResult.setText(decFormat.format(Double.parseDouble(eResult.getText().toString()) * 0.01));
                }
                else if(TYPE==ADD || TYPE==SUB){
                    eShow.setText(history + decFormat.format(Double.parseDouble(number1) *Double.parseDouble(number2)* 0.01));
                    eResult.setText(decFormat.format(Double.parseDouble(number1) *Double.parseDouble(number2)* 0.01));
                }
                else{
                    eResult.setText("0");
                }
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String del_number = eResult.getText().toString();
                if(del_number.length()==1) {
                    eResult.setText("0");
                    checkStart = true;
                    Toast.makeText(MainActivity.this, "다지웠다..", Toast.LENGTH_SHORT).show();     //화면에 잠깐 보여지고 사라지는 메시지를 보여주고 싶을때 사용
                }
                else {
                    del_number=del_number.substring(0, del_number.length()-1);
                    eResult.setText(del_number);     //.substring(a,b): a~b 전 까지 잘라내서 표시
                    if(!del_number.contains(".")){      //"."을 지우면 재사용 가능하게 구현
                        checkDot=false;
                    }
                }
            }
        });

        bPM.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")                         //@SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 합니다.
            @Override
            public void onClick(View view) {
                // 정수인지 판별 + "." 이 불리지 않았다면
                if(Double.parseDouble(eResult.getText().toString().replace(",",""))>=0){
                    eResult.setText("-"+eResult.getText().toString());

                }
                else{
                    eResult.setText(eResult.getText().toString().substring(1));
                }

            }
        });                 //음,양수 확인 후 부호 반전
    }

    Button.OnClickListener trans= new Button.OnClickListener() {          //trans별 동작 구현
        @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
        public void onClick(View v) {
            checkTrans=true;
            switch (v.getId()) {
                case R.id.Button_divideX:
                    transTYPE=DIVX;
                    if(CYCLE==0) {
                        eShow.setText("1/"+eResult.getText().toString());
                    }
                    else {
                        eShow.setText(history+"1/"+eResult.getText().toString());
                    }
                    transCalc(transTYPE);
                    break;

                case R.id.Button_power:
                    transTYPE=POWER;
                    if(CYCLE==0) {
                        eShow.setText("sqr("+eResult.getText().toString()+")");
                    }
                    else{
                        eShow.setText(history+"sqr("+eResult.getText().toString()+")");
                    }
                    transCalc(transTYPE);
                    break;

                case R.id.Button_root:
                    transTYPE=ROUTE;
                    if(CYCLE==0) {
                        eShow.setText("sqrt("+eResult.getText().toString()+")");
                    }
                    else {
                        eShow.setText(history + "sqrt(" + eResult.getText().toString() + ")");
                    }
                    transCalc(transTYPE);
                    break;

            }

        }
    };


    Button.OnClickListener operator= new Button.OnClickListener(){          //연산자별 동작 구현
        @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
        public void onClick(View v){
            checkDot=false;
            checkOperator=true;

            switch(v.getId()){
                case R.id.Button_plus:
                    if(CYCLE==0) {
                        TYPE=ADD;
                        number1 = eResult.getText().toString().replace(",","");
                        history = number1 + (char)TYPE;
                        eShow.setText(history);
                        CYCLE++;
                        break;
                    }
                    number2=eResult.getText().toString().replace(",","");
                    operatorCalc(TYPE);
                    TYPE=ADD;
                    history=number2+(char)TYPE;
                    eShow.setText(history);
                    break;


                case R.id.Button_sub:
                    if(CYCLE==0) {
                        TYPE=SUB;
                        number1 = eResult.getText().toString().replace(",","");
                        history = number1 + (char)TYPE;
                        eShow.setText(history);
                        CYCLE++;
                        break;
                    }
                    number2=eResult.getText().toString().replace(",","");
                    operatorCalc(TYPE);
                    TYPE=SUB;
                    history=number2+(char)TYPE;
                    eShow.setText(history);
                    break;
                case R.id.Button_multi:
                    if(CYCLE==0) {
                        TYPE=MULTI;
                        number1 = eResult.getText().toString().replace(",","");
                        history = number1 + (char)TYPE;
                        eShow.setText(history);
                        CYCLE++;
                        break;
                    }
                    number2=eResult.getText().toString().replace(",","");
                    operatorCalc(TYPE);
                    TYPE=MULTI;
                    history=number2+(char)TYPE;
                    eShow.setText(history);
                    break;

                case R.id.Button_divide:
                    if(CYCLE==0) {
                        TYPE=DIV;
                        number1 = eResult.getText().toString().replace(",","");
                        history = number1 + (char)TYPE;
                        eShow.setText(history);
                        CYCLE++;
                        break;
                    }
                    number2=eResult.getText().toString().replace(",","");
                    operatorCalc(TYPE);
                    TYPE=DIV;
                    history=number2+(char)TYPE;
                    eShow.setText(history);
                    break;

                case R.id.Button_CE:
                    if(checkResult){
                        allReset();
                    }
                    else {
                        eResult.setText("0");
                    }
                    break;

                case R.id.Button_clear:
                    allReset();
                    break;

                case R.id.Button_result:
                    checkResult=true;
                    checkOperator=false;
                    CYCLE=0;
                    number2=eResult.getText().toString().replace(",","");
                    eShow.setText(history+number2+"=");
                    operatorCalc(TYPE);
                    break;
            }
        }
    };

    public void transCalc(int transTYPE){
        BigDecimal bignum;
        if(CYCLE==0) {
            number1 = eResult.getText().toString().replace(",","");
            bignum= new BigDecimal(number1);
            if (transTYPE == DIVX) {
                if(bignum.equals(BigDecimal.ZERO)){
                    Toast.makeText(this, "0으로 나눌 수 없음", Toast.LENGTH_SHORT).show();
                    allReset();
                    return;
                }
                number1=BigDecimal.ONE.divide(bignum,15,RoundingMode.HALF_EVEN).toString();
            } else if (transTYPE == POWER) {
                number1=bignum.multiply(bignum).toString();
            } else if (transTYPE == ROUTE) {
                number1 =""+Math.sqrt(Double.parseDouble(number1));
            }
            if(checkOverflow(number1)){
                Toast.makeText(this, "오버플로우!!!", Toast.LENGTH_SHORT).show();
                allReset();
                return;
            }
            setText(number1);
        }
        else{
            number2=eResult.getText().toString().replace(",","");
            bignum=new BigDecimal(number2);
            if (transTYPE == DIVX) {
                if(bignum.equals(BigDecimal.ZERO)){
                    Toast.makeText(this, "0으로 나눌 수 없음", Toast.LENGTH_SHORT).show();
                    allReset();
                    return;
                }
                number2=BigDecimal.ONE.divide(bignum,15,RoundingMode.HALF_EVEN).toString();
            } else if (transTYPE == POWER) {
                number2=bignum.multiply(bignum).toString();
            } else if (transTYPE == ROUTE) {
                number2 =""+Math.sqrt(Double.parseDouble(number2));
            }
            if(checkOverflow(number2)){
                Toast.makeText(this, "오버플로우!!!", Toast.LENGTH_SHORT).show();
                allReset();
                return ;
            }
            setText(number2);
        }
    }

    @SuppressLint("DefaultLocale")
    public void operatorCalc(int TYPE) {

        BigDecimal b1=new BigDecimal(number1);
        BigDecimal b2=new BigDecimal(number2);


        if (TYPE == ADD) {
            number2 = b1.add(b2).toString();
            Toast.makeText(this, "더하기를 합니다.", Toast.LENGTH_SHORT).show();
        } else if (TYPE == SUB) {
            number2 = b1.subtract(b2).toString();
            Toast.makeText(this, "빼기를 합니다.", Toast.LENGTH_SHORT).show();
        } else if (TYPE == MULTI) {
            number2 = b1.multiply(b2).toString();
            Toast.makeText(this, "곱하기를 합니다.", Toast.LENGTH_SHORT).show();
        } else if (TYPE == DIV)  {
            try {
                number2=b1.divide(b2,15,RoundingMode.HALF_EVEN).stripTrailingZeros().toString();
                Toast.makeText(this, "나누기를 합니다.", Toast.LENGTH_SHORT).show();
            }catch (ArithmeticException e){
                Toast.makeText(this, "0으로 나눌 수 없다.", Toast.LENGTH_SHORT).show();
                allReset();
                return;
            }
        }
        if(checkOverflow(number2)){
            Toast.makeText(this, "오버플로우", Toast.LENGTH_SHORT).show();
            allReset();
            return;
        }
        setText(number2);
        number1=number2;
    }

    public void setText(String num){
        if((Math.abs(Double.parseDouble(num))<=Double.parseDouble(MAX) && Math.abs(Double.parseDouble(num))>=Double.parseDouble(MIN))) {
            eResult.setText(decFormat.format(Double.parseDouble(num)));
        }
        else if(Double.parseDouble(num)==0.0){
            eResult.setText(decFormat.format(Double.parseDouble(num)));
        }
        else{
            eResult.setText(String.format("%e",Double.parseDouble(num)));
        }
    }

    public Boolean checkOverflow(String num){
        if(Double.isNaN(Double.parseDouble(num))||Double.isInfinite(Double.parseDouble(num))){
            return true;
        }
        else
            return false;
    }       //overflow 확인 true,false

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    public void onClick(View v){        //0~9 , . 까지 button 구현
        checkOpReTr();            //버튼클릭 전에 어떤 의도인지 체크 후 editText에 띄울 값 처리.
        if(eResult.getText().toString().length()>20){ return;}      //숫자가 21자리 초과한다면 더이상 입력 금지
        switch (v.getId()){
            case R.id.Button_0:
                if(eResult.getText().toString().equals("0") || eResult.getText().toString().equals("")){    // 처음 0 2번이상 눌리는거 방지
                    eResult.setText("0");
                    checkStart=true;
                }else{
                    eResult.setText(eResult.getText().toString()+0);
                }break;
            case R.id.Button_1:eResult.setText(eResult.getText().toString()+1); break;
            case R.id.Button_2:eResult.setText(eResult.getText().toString()+2); break;
            case R.id.Button_3:eResult.setText(eResult.getText().toString()+3); break;
            case R.id.Button_4:eResult.setText(eResult.getText().toString()+4); break;
            case R.id.Button_5:eResult.setText(eResult.getText().toString()+5); break;
            case R.id.Button_6:eResult.setText(eResult.getText().toString()+6); break;
            case R.id.Button_7:eResult.setText(eResult.getText().toString()+7); break;
            case R.id.Button_8:eResult.setText(eResult.getText().toString()+8); break;
            case R.id.Button_9:eResult.setText(eResult.getText().toString()+9); break;
            case R.id.Button_dot:
                if(checkDot){
                    Toast.makeText(this, "이미 사용 하셨습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    if(eResult.getText().toString().equals("")) {               //처음 . 입력시
                        eResult.setText("0.");
                    }
                    else {
                        eResult.setText(eResult.getText().toString() + ".");
                    }
                    checkDot=true;
                    break;
                }
        }
    }

    public void checkOpReTr(){              // 어떤 목적의 버튼을 누른 후 숫자 버튼을 누른건지
        if(checkStart){
            eResult.setText("");
            checkStart=false;
        }

        if(checkResult) {
            if (!checkOperator) {
                allReset();
            }
            eResult.setText("");
            checkResult = false;
        }

        if(checkOperator) {
            eResult.setText("");
            checkOperator=false;
        }
        if(checkTrans){
            eResult.setText("");
            checkDot=false;
            checkTrans=false;
        }
        if(checkDot){
            checkStart=false;
        }
    }

    public void allReset(){
        Toast.makeText(this, "모두지웁니다.", Toast.LENGTH_SHORT).show();
        eShow.setText("");
        number1=number2="0";
        history="";
        eResult.setText("0");
        CYCLE=0;
        TYPE=transTYPE=-1;
        checkStart=true;
    }
}