/*
package com.sh.teethdetect;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NonLoginActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_login);

        bottomNavigationView = findViewById(R.id.navi_bar);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.item_fragment1:
                        Intent intent = new Intent(NonLoginActivity.this, CariesActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.item_fragment2:
                        Intent intent1 = new Intent(NonLoginActivity.this, VideodetectActivity.class);
                        startActivity(intent1);
                        break;

                }
                return;
            }
        });
    }

*/
/* 뒤로가기 버튼 메소드*//*

public void onBackPressed(){
    //super.onBackPressed();
    //기존의 뒤로가기 버튼 기능 막기
    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
        backKeyPressedTime = System.currentTimeMillis();
        toast = Toast.makeText(this, "뒤로 버튼 한번더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
        toast.show();
        return;
    }// 뒤로가기버튼을 한번누르면 현재시간값에 현재버튼누른시간 저장
    if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
        moveTaskToBack(true);						// 태스크를 백그라운드로 이동
        finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());
        // finish();
        // toast.cancel();
    }//위에서 저장한 현재시간값에 2초안에 버튼을 한번 더 누르면 앱을 종료함.
}

}*/
