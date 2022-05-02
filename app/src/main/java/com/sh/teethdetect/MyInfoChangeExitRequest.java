package com.sh.teethdetect;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyInfoChangeExitRequest extends StringRequest {
//서버 URL 설정(php 파일 연동)
final static private String URL = "http://lsh2952.dothome.co.kr/Delete.php";
private Map<String, String> map;

public MyInfoChangeExitRequest(String userEmail,Response.Listener<String> listener) {
    super(Method.POST, URL, listener, null);

    map = new HashMap<>();
    map.put("UserEmail", userEmail);

}

@Override
protected Map<String, String>getParams() throws AuthFailureError {
    return map;
}

}
