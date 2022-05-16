package com.sh.teethdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListClickView extends AppCompatActivity {
    ImageView uriimage;
    Button delete;
    TextView text1,text2,text3,text4;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;
    int indlength=0;
    String userEmail;
    //text1: 날짜 (db) , text2:충치개수 , text3:인덱스 text4:방문텍스트

    private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_click_view);

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

    if (startYolo == false) {

        startYolo = true;

        if (firstTimeYolo == false) {

            firstTimeYolo = true;
            String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/teeth-train-yolo.cfg";
            String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/6000.weights";

            tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
        }
    } else {
        startYolo = false;
    }

    delete = findViewById(R.id.delete);
    uriimage = findViewById(R.id.image1);
    text1 = findViewById(R.id.text1);
    text2 = findViewById(R.id.text2);
    text3 = findViewById(R.id.text3);
    text4 = findViewById(R.id.text4);

    String t1,t2,t3,t4,uri;

    Intent intent = getIntent();

    uri= intent.getStringExtra("Uri");
    t1= intent.getStringExtra("Date");
    t2 = intent.getStringExtra("Num");
    t3 = intent.getStringExtra("Index");
    t4 = intent.getStringExtra("Visit");
    userEmail = intent.getStringExtra("UserEmail");

    if(userEmail==null){
        delete.setEnabled(false);
    }

    File imgFile = new  File(uri);

    if(imgFile.exists()){
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        Mat image1 = new Mat();

        Bitmap copyimg= myBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(copyimg, image1);
        Imgproc.cvtColor(image1, image1, Imgproc.COLOR_RGBA2RGB);


        Mat imageBlob = Dnn.blobFromImage(image1, 0.00392, new Size(416, 416), new Scalar(0, 0, 0));

        tinyYolo.setInput(imageBlob);

        java.util.List<Mat> result = new java.util.ArrayList<Mat>(3);

        List<String> outBlobNames = new java.util.ArrayList<>();
        outBlobNames.add(0, "yolo_82");
        outBlobNames.add(1, "yolo_94");
        outBlobNames.add(2, "yolo_106");

        tinyYolo.forward(result, outBlobNames);


        float confThreshold = 0.3f;

        List<Integer> clsIds = new ArrayList<>();
        List<Float> confs = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();
        for (int i = 0; i < result.size(); ++i) {

            Mat level = result.get(i);

            for (int j = 0; j < level.rows(); ++j) {
                Mat row = level.row(j);
                Mat scores = row.colRange(5, level.cols());

                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);

                float confidence = (float) mm.maxVal;

                Point classIdPoint = mm.maxLoc;

                if (confidence > confThreshold) {
                    int centerX = (int) (row.get(0, 0)[0] * image1.cols());
                    int centerY = (int) (row.get(0, 1)[0] * image1.rows());
                    int width = (int) (row.get(0, 2)[0] * image1.cols());
                    int height = (int) (row.get(0, 3)[0] * image1.rows());

                    int left = centerX - width / 2;
                    int top = centerY - height / 2;

                    clsIds.add((int) classIdPoint.x);
                    confs.add((float) confidence);

                    rects.add(new Rect(left, top, width, height));

                }
            }
        }

        int ArrayLength = confs.size();

        if (ArrayLength >= 1) {
            // Apply non-maximum suppression procedure.
            float nmsThresh = 0.2f;


            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));

            Rect[] boxesArray = rects.toArray(new Rect[0]);
            MatOfRect boxes = new MatOfRect(boxesArray);
            MatOfInt indices = new MatOfInt();

            Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);

            // Draw result boxes:
            int[] ind = indices.toArray();

            for (int i = 0; i < ind.length; ++i) {

                indlength=(i+1);

                int idx = ind[i];
                Rect box = boxesArray[idx];

                int idGuy = clsIds.get(idx);

                float conf = confs.get(idx);

                List<String> cocoNames = Arrays.asList("caries");

                int intConf = (int) (conf * 100);

                Imgproc.putText(image1,"No."+ (i+1), box.tl(), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0, 0, 255), 3);
                Imgproc.rectangle(image1, box.tl(), box.br(), new Scalar(255, 0, 0), 5);

            }
        }
        Bitmap setimg = Bitmap.createBitmap(image1.cols(),image1.rows(),null);
        Utils.matToBitmap(image1,setimg);

        uriimage.setImageBitmap(setimg);
    }

    else{
        Toast.makeText(this,"기기에서 파일이 삭제되어서 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
    }

    text1.setText("검진시각\n"+t1);
    text2.setText("충치개수:"+t2);
    text3.setText("충치인덱스\n"+t3);
    text4.setText("권고사항\n"+t4);


    delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(ListClickView.this);
            dialog.setTitle("삭제하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject( response );
                                String success = jsonObject.getString( "success" );

                                if(success.equals("true")) {//서버에서 true를 받으면
                                    Toast.makeText( getApplicationContext(), "리스트 삭제 성공", Toast.LENGTH_SHORT ).show();
                                    Intent gohome = new Intent(ListClickView.this,HomeActivity.class);
                                    gohome.putExtra("userEmail",userEmail);
                                    startActivity(gohome);
                                    finish();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(ListClickView.this,"리스트삭제 실패(서버오류)",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();

                            }
                        }
                    };

                    //volley에 userEmail과 passcheck를 넣어서 MyInfoChangeRequest 통해 php서버로 보내줌.
                    DeletelistRequest deletelistRequest = new DeletelistRequest( userEmail,uri, responseListener );
                    RequestQueue queue = Volley.newRequestQueue( ListClickView.this );
                    queue.add( deletelistRequest );

                }
            }).setNegativeButton("취소",null);
            dialog.create();
            dialog.show();
        }
    });
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}