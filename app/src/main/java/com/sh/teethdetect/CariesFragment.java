package com.sh.teethdetect;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CariesFragment extends Fragment {

    private androidx.recyclerview.widget.RecyclerView RecyclerView; // 이미지를 보여줄 리사이클러뷰

    ArrayList<ItemData> datalist = new ArrayList<>();

    MultiImageAdapter adapter = new MultiImageAdapter(datalist);
    int indlength=0;
    String cariesnumber ="";
    Bitmap myBitmap,bitmap;
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;

    int max = 0;
    String detect1;

    private static final int CARIES_REQUEST = 0, CARIES_REQUEST1=1; //회원일때 응답코드는 0, 비회원 1

    private ImageView imageView2;
    private Button pictureGet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_caries, container, false);

        pictureGet = v.findViewById(R.id.pictureGet);
        RecyclerView = v.findViewById(R.id.Recyclerview);

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

        Bundle bundle = getArguments();
        //로그인한 아이디 값 저장 변수
        String userEmail = bundle.getString("userEmail");

        //bunble로 가져온 글자가 존재안하면 비회원처리
        if(userEmail==null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("비회원이므로 검진결과는 저장되지 않습니다.").setPositiveButton("확인",null);
            dialog.create();
            dialog.show();

            pictureGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, CARIES_REQUEST1);
                }
            });

        }

        //bundle로 가져온 글자가 존재하면 회원처리
        else{

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("이전충치검진결과 불러오는 중.\n없으면 확인 눌러주세요").setPositiveButton("확인",null);
            dialog.create();
            dialog.show();

            //caries프래그먼트가 실행이되면 서버에서 이미지경로를 가져와서 set해줘야함
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("res");

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject c = jsonArray.getJSONObject(i);
                            String a = c.getString("UserImage");

                            Log.e("받아지냐 시발 받아졌다",a);
                            File imgFile = new  File(a);

                            if(imgFile.exists()){

                                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                getOpenCv(myBitmap);
                            }
                            else{
                                Toast.makeText(getActivity().getApplicationContext(),"기기에서 파일이 삭제되어서 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }

                        }


                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(),"예외 (에러처리)",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                }
            };

            DatabaseDownRequest databaseDownRequest = new DatabaseDownRequest(userEmail, responseListener );
            RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
            queue.add( databaseDownRequest );

            pictureGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, CARIES_REQUEST);
                }
            });
        }

        return v;
    } //oncreateview


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Bundle bundle = getArguments();
        //로그인한 아이디 값 저장 변수
        String userEmail = bundle.getString("userEmail");


        try {
            //회원
            if (requestCode == CARIES_REQUEST) {
                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                    Toast.makeText(getActivity().getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {   // 이미지를 하나라도 선택한 경우
                    if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                        Log.e("single choice: ", String.valueOf(data.getData()));


                        /*InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();*/

                        Uri ImageUri = data.getData();
                        String realuri = getRealPathFromURI(ImageUri);

                       // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                       // String ImageUritoString = BitmapToString(bitmap);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("이전 검진결과를 불러오는 중 입니다.\n");
                        dialog.create();
                        dialog.show();


                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject( response );
                                    //JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    String success = jsonObject.getString( "success" );

                                    if(success.equals("true")) {

                                        String a = jsonObject.getString("UserImage");

                                        Uri uri = Uri.parse(a);
                                        Thread.sleep(1000);
                                        //String b=jsonObject.getString("UserCaries");
                                        //Toast.makeText(getActivity().getApplicationContext(),b,Toast.LENGTH_SHORT).show();
                                        //String c=jsonObject.getString("UserText");
                                        //cv 처리

                                        File imgFile = new  File(a);

                                        if(imgFile.exists()){
                                            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                                        }

                                        //bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                        // Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(a));

                                        // --

                                        getOpenCv(myBitmap);


                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(),"success구문에 들어가지못함",Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getActivity().getApplicationContext(),"예외 (에러처리)",Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                            }
                        };

                            DatabaseRequest databaseRequest = new DatabaseRequest(userEmail, realuri, responseListener );
                            RequestQueue queue = Volley.newRequestQueue( getActivity().getApplicationContext());
                            queue.add( databaseRequest );

                    }
                }
            }

            //비회원코드
            if (requestCode == CARIES_REQUEST1){
                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                    Toast.makeText(getActivity().getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {   // 이미지를 하나라도 선택한 경우
                    if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();

                        getOpenCv(img);
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
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);

        return temp;
    }

public byte[] BitmapToByteArray( Bitmap $bitmap ) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
    $bitmap.compress( Bitmap.CompressFormat.JPEG, 10, stream) ;
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

private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage"))
        {
            return contentUri.getPath();
        } String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try { int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) { return cursor.getString(columnIndex); } }
        finally { cursor.close(); } return null; }


        private void getOpenCv(Bitmap bitmap){
            Toast.makeText(getActivity().getApplicationContext(),"과거 검진 리스트를 불러오는 중입니다(10초가량소요) 과거 검진이력이 없으면 불러오지않습니다.",Toast.LENGTH_SHORT).show();
            String visittext = "검진결과 충치확률이 50% 이상입니다. 치과방문을 권장합니다. ";
            String visittext2= "충치 확률이 절반 이하이지만, 정확한 검진을 위해 치과 방문을 권장합니다.";
            String visittext3= "인공지능이 충치를 발견하지 못하였습니다.(정상치아)";

            Mat image1 = new Mat();

            Bitmap copyimg= bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(copyimg, image1);
            Imgproc.cvtColor(image1, image1, Imgproc.COLOR_RGBA2RGB);


            Mat imageBlob = Dnn.blobFromImage(image1, 0.00392, new Size(416, 416), new Scalar(0, 0, 0));

            tinyYolo.setInput(imageBlob);

            List<Mat> result = new ArrayList<Mat>(3);

            List<String> outBlobNames = new ArrayList<>();
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

                String detect[];
                detect = new String[ind.length];
                int[] intConf = new int[ind.length];

                for (int i = 0; i < ind.length; ++i) {

                    indlength=(i+1);

                    int idx = ind[i];
                    Rect box = boxesArray[idx];

                    int idGuy = clsIds.get(idx);

                    float conf = confs.get(idx);

                    List<String> cocoNames = Arrays.asList("caries");

                    intConf[i] = (int) (conf * 100);

                    Imgproc.putText(image1,"No."+ (i+1), box.tl(), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0, 0, 255), 3);
                    Imgproc.rectangle(image1, box.tl(), box.br(), new Scalar(255, 0, 0), 5);

                    detect[i]=(i+1)+"번째 치아의 충치 확률 : "+intConf[i]+"%";
                    Log.e("sssss",detect[i]);
                    detect1 = Arrays.toString(detect);

                    if(intConf[i]>max){
                        max = intConf[i];
                    }

                }
            }

            //cv처리된 비트맵 이미지
            Bitmap setimg = Bitmap.createBitmap(image1.cols(),image1.rows(),null);
            Utils.matToBitmap(image1,setimg);

            Toast.makeText(getActivity().getApplicationContext(),"충치 검진 결과를 확인하세요.",Toast.LENGTH_SHORT).show();
            // 데이터베이스 주기 위한 서버 동작
            cariesnumber = String.valueOf(indlength);
            if(indlength > 0 && max >= 50){
                datalist.add(new ItemData(setimg,cariesnumber,visittext,detect1));
                Log.e("씨발",detect1);
                indlength = 0;
                max = 0;

            } else if(indlength > 0 && max < 50 && max > 0){
                datalist.add(new ItemData(setimg,cariesnumber,visittext2,detect1));
                Log.e("씨발",detect1);
                indlength = 0;
                max = 0;
            } else if(indlength <= 0){
                datalist.add(new ItemData(setimg,cariesnumber,visittext3,"　"));
                indlength=0;
            }

            RecyclerView.setAdapter(adapter);
            RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            adapter.notifyDataSetChanged();
        }



}