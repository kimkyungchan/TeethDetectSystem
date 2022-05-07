package com.sh.teethdetect;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeletelistRequest extends StringRequest {
//서버 URL 설정(php 파일 연동)
final static private String URL = "http://lsh2952.dothome.co.kr/DeleteList.php";
private Map<String, String> map;

public DeletelistRequest(String userEmail,String imageurl, Response.Listener<String> listener) {
    super(Method.POST, URL, listener, null);

    map = new HashMap<>();
    map.put("UserEmail", userEmail);
    map.put("UserImage",imageurl);

}

@Override
protected Map<String, String>getParams() throws AuthFailureError {
    return map;
}

}
