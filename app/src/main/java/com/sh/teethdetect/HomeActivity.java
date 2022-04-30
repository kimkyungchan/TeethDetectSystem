package com.sh.teethdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

private androidx.recyclerview.widget.RecyclerView RecyclerView; // 이미지를 보여줄 리사이클러뷰
BaseLoaderCallback baseLoaderCallback;
CameraBridgeViewBase cameraBridgeViewBase;
BottomNavigationView bottomNavigationView;
public static final int CARIES_REQUEST = 0;
Net tinyYolo;
int indlength=0;
ArrayList<ItemData> datalist = new ArrayList<>();
MultiImageAdapter adapter = new MultiImageAdapter(datalist);
CariesFragment cariesFragment;
HomeFragment homeFragment;
MyPageFragment mypagefragment;
MyPageNoFragment mypagenofragment;
private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수
static final int PERMISSIONS_REQUEST = 0x00000001;

private String[] PERMISSIONS = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
};

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    bottomNavigationView = findViewById(R.id.navi_bar);

    homeFragment = new HomeFragment();
    setDefaultFragment();

    Intent intent = getIntent();
    String userEmail = intent.getStringExtra("userEmail");

    baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);

            switch (status) {

                case BaseLoaderCallback.SUCCESS:
                   // cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.item_fragment1:
                    cariesFragment = new CariesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,cariesFragment).commit();

                    Bundle bundle =new Bundle();
                    bundle.putString("userEmail",userEmail);
                    cariesFragment.setArguments(bundle);
                    break;

                case R.id.item_fragment2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new VideoWindowsActivityFragment()).commit();
                    break;
                case R.id.item_fragment3:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new MapsFragment()).commit();
                    break;
                case R.id.item_fragment4:

                    if(userEmail==null){
                        mypagenofragment = new MyPageNoFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,mypagenofragment).commit();
                    }

                    else{

                        mypagefragment = new MyPageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,mypagefragment).commit();
                        Bundle bundle1 =new Bundle();
                        bundle1.putString("userEmail",userEmail);
                        mypagefragment.setArguments(bundle1);

                    }

                    break;
            }
            return;
        }
    });


}

    public void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_frame, homeFragment);
        transaction.commit();

    }

@Override
protected void onResume() {
    super.onResume();

    if (!OpenCVLoader.initDebug()) {
        Toast.makeText(getApplicationContext(), "There's a problem, yo!", Toast.LENGTH_SHORT).show();
    } else {
        baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
    }

}

@Override
protected void onPause() {
    super.onPause();
    if (cameraBridgeViewBase != null) {
        cameraBridgeViewBase.disableView();
    }
}


@Override
protected void onDestroy() {
    super.onDestroy();
    if (cameraBridgeViewBase != null) {
        cameraBridgeViewBase.disableView();
    }
}
/* 뒤로가기 버튼 메소드*/
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
}