package com.sh.teethdetect;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DatabaseRequest extends StringRequest {
//서버 URL 설정(php 파일 연동)
final static private String URL = "http://lsh2952.dothome.co.kr/Database.php";
private Map<String, String> map;

public DatabaseRequest(String userEmail,String setimg, String cariesnumber,String visittext, Response.Listener<String> listener) {
    super(Method.POST, URL, listener, null);

    map = new HashMap<>();
    map.put("UserEmail", userEmail);
    map.put("UserImage", setimg);
    map.put("UserCaries", cariesnumber);
    map.put("UserText", visittext);

}

@Override
protected Map<String, String>getParams() throws AuthFailureError {
    return map;
}

}
