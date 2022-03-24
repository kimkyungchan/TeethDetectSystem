package com.sh.teethdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    bottomNavigationView = findViewById(R.id.navi_bar);

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new CariesFragment()).commit();
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

 @Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {

    try {
        if (requestCode == CARIES_REQUEST) {

            if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {   // 이미지를 하나라도 선택한 경우
                if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                }
            }
        }
    } catch (Exception e) {}
    super.onActivityResult(requestCode, resultCode, data);
}
}