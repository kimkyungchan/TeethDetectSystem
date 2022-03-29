package com.sh.teethdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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
}