package com.sh.teethdetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MyInfoChangeActivity extends AppCompatActivity {

EditText mypage_pass,mypage_passcheck;
Button mypage_changebt;

protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_myinfochange);


    mypage_changebt = findViewById(R.id.mypage_changebt);
    mypage_pass = findViewById(R.id.mypage_pass);
    mypage_passcheck = findViewById(R.id.mypage_passcheck);

    mypage_changebt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent gologin = new Intent(MyInfoChangeActivity.this,LoginActivity.class);

            Intent intent= getIntent();
            //프래그먼트에서 넘어온 userEmail값.
            String userEmail = intent.getStringExtra("passEmailIntent");

            String pass =  mypage_pass.getText().toString();
            String passcheck = mypage_passcheck.getText().toString();

            //바꿀비밀번호가 일치할때
            if(pass.equals(passcheck)){

                //db에 있는 값을 받아옴
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            String success = jsonObject.getString( "success" );

                            if(success.equals("true")) {//서버에서 true를 받으면
                                Toast.makeText( getApplicationContext(), "비밀번호 변경 성공", Toast.LENGTH_SHORT ).show();
                                startActivity(gologin);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(MyInfoChangeActivity.this,"비밀번호 변경실패(서버오류)",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                };

                //volley에 userEmail과 passcheck를 넣어서 MyInfoChangeRequest 통해 php서버로 보내줌.
                MyInfoChangeRequest myInfoChangeRequest = new MyInfoChangeRequest( userEmail, passcheck, responseListener );
                RequestQueue queue = Volley.newRequestQueue( MyInfoChangeActivity.this );
                queue.add( myInfoChangeRequest );

            }

            else{
                Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
            }

        }
    });

    }
}
