package com.example.user.shake.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertPointRequest extends StringRequest {
    final static private String URL = "http://13.125.229.179/insertPoint.php";
    private Map<String, String> parameters;

    public InsertPointRequest(String userID,int point,String time,Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPoint",point+"");
        parameters.put("time",time);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
