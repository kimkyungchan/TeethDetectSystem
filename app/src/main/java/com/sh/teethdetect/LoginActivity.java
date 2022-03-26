package com.sh.teethdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

private EditText et_id, et_pass;
private Button btn_login, btn_register;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    et_id = findViewById( R.id.login_email );
    et_pass = findViewById( R.id.login_password );

    btn_register = findViewById( R.id.join_button );
    btn_register.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
            startActivity( intent );
        }
    });

    btn_login = findViewById( R.id.login_button );

    btn_login.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userEmail = et_id.getText().toString();
            String userPass = et_pass.getText().toString();


            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject( response );
                        String success = jsonObject.getString( "success" );
                        if(success.equals("true")) {//로그인 성공시
                            Toast.makeText( getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent( LoginActivity.this, HomeActivity.class );
                            intent.putExtra("userEmail",userEmail);
                            startActivity( intent );

                        } else {//로그인 실패시
                            Toast.makeText( getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this,"예외 (에러처리)",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();

                    }
                }
            };
            LoginRequest loginRequest = new LoginRequest( userEmail, userPass, responseListener );
            RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
            queue.add( loginRequest );

        }
    });
}
}