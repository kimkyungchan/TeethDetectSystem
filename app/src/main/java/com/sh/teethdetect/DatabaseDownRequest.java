package com.sh.teethdetect;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DatabaseDownRequest extends StringRequest {
final static private String URL = "http://lsh2952.dothome.co.kr/DataSend.php";
private Map<String, String> map;

public DatabaseDownRequest(String user,Response.Listener<String> listener) {
    super(Method.POST, URL, listener, null);

    map = new HashMap<>();
    map.put("UserEmail", user);

}

@Override
protected Map<String, String>getParams() throws AuthFailureError {
    return map;
}

}
