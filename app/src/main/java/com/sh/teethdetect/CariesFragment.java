package com.sh.teethdetect;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CariesFragment extends Fragment {

    private androidx.recyclerview.widget.RecyclerView RecyclerView; // 이미지를 보여줄 리사이클러뷰

    ArrayList<ItemData> datalist = new ArrayList<>();

    MultiImageAdapter adapter = new MultiImageAdapter(datalist);
    int indlength=0;
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;

    private static final int CARIES_REQUEST = 0;
    private ImageView imageView2;
    private Button pictureGet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_caries, container, false);

        adapter.notifyDataSetChanged();

        pictureGet = v.findViewById(R.id.pictureGet);
        RecyclerView = v.findViewById(R.id.Recyclerview);

        pictureGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.ACTION_GET_CONTENT, true);
                startActivityForResult(intent, CARIES_REQUEST);
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(getActivity()) {
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

        return v;
    } //oncreateview


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Bundle bundle = getArguments();
        //로그인한 아이디 값 저장 변수
        String userEmail = bundle.getString("userEmail");


        String visittext = "정상 치아일 확률이 높습니다. 주기적으로 치아검진은 필수입니다";
        String visittext2= "충치로 의심되는 부분이 있습니다. 치과 방문을 권장합니다.";
        String visittext3= "충치로 의심되는 부분이 너무 많습니다. 빠른 시일내에 치과 방문을 권장합니다.";

        try {
            if (requestCode == CARIES_REQUEST) {
                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                    Toast.makeText(getActivity().getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {   // 이미지를 하나라도 선택한 경우
                    if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();

                        Mat image1 = new Mat();

                        Bitmap copyimg= img.copy(Bitmap.Config.ARGB_8888, true);
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

                                Imgproc.putText(image1,  intConf + "%", box.tl(), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0, 255, 0), 3);
                                Imgproc.rectangle(image1, box.tl(), box.br(), new Scalar(255, 0, 0), 5);

                            }
                        }
                        Bitmap setimg = Bitmap.createBitmap(image1.cols(),image1.rows(),null);
                         Utils.matToBitmap(image1,setimg);

                        // 데이터베이스 주기 위한 서버 동작
                        String cariesnumber = String.valueOf(indlength);
                        String bitstring = BitmapToString(setimg);
                        String setimgstringutf = URLEncoder.encode(bitstring,"utf-8");

                      //  byte[] a = bitmapToByteArray(setimg);
                      //  String aa = new String(a);


                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {

                                try {
                                    JSONObject jsonObject = new JSONObject( result );
                                    //JSONArray jsonArray = jsonObject.getJSONArray("result");
                                    String success = jsonObject.getString( "success" );

                                    try {
                                        Thread.sleep(5000);
                                        Bitmap a=StringToBitmap(jsonObject.getString("UserImage"));
                                        String b=jsonObject.getString("UserCaries");
                                        Toast.makeText(getActivity().getApplicationContext(),b,Toast.LENGTH_SHORT).show();
                                        String c=jsonObject.getString("UserText");
                                        if(success.equals("true")) {
                                            datalist.add(new ItemData(setimg,b,c));
                                            RecyclerView.setAdapter(adapter);
                                            RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                        } else {
                                            Toast.makeText(getActivity().getApplicationContext(),"success구문에 들어가지못함",Toast.LENGTH_SHORT).show();
                                        }

                                        //  adapter.notifyDataSetChanged();
                                        //  Toast.makeText(getContext(), "dddd", Toast.LENGTH_SHORT).show();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();

                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getActivity().getApplicationContext(),"예외 (에러처리)",Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        };

                        if(indlength<=0){
                            DatabaseRequest databaseRequest = new DatabaseRequest(userEmail,setimgstringutf, cariesnumber,visittext, responseListener );
                            RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
                            queue.add( databaseRequest );
                        }
                        else if(indlength>0&&indlength<3){
                            DatabaseRequest databaseRequest = new DatabaseRequest(userEmail,setimgstringutf, cariesnumber,visittext2, responseListener );
                            RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext() );
                            queue.add( databaseRequest );
                            indlength=0;
                        }
                        else
                        {
                            DatabaseRequest databaseRequest = new DatabaseRequest(userEmail,setimgstringutf, cariesnumber,visittext3, responseListener );
                            RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
                            queue.add( databaseRequest );
                            indlength=0;
                        }
                        datalist.add(new ItemData(setimg,String.valueOf(1),"aa"));
                        RecyclerView.setAdapter(adapter);
                        RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    }
                }
            }
        } catch (Exception e) {}
    }
    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getActivity().getApplicationContext(), "There's a problem, yo!", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }


    }

public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,10 , baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT); return temp;
    }

public byte[] BitmapToByteArray( Bitmap $bitmap ) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
    $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
    byte[] byteArray = stream.toByteArray() ;
    return byteArray ;
}
public static Bitmap StringToBitmap(String encodedString) {
    try {
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }
    catch (Exception e) {
        e.getMessage();
        return null;
    }
}
// Byte를 Bitmap으로 변환
public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length );
        return bitmap ;
    }




}