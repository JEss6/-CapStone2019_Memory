package com.example.user.shake;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.shake.Request.GetRentrequestRequest;
import com.example.user.shake.Request.GetUserTokenRequest;
import com.example.user.shake.Request.ReportCountReqeust;
import com.example.user.shake.Request.SendAllowRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CheckAllowActivity extends AppCompatActivity {
    private TextView explain;
    public String json_borrower,json_renttime,json_bikecode,json_returntime;
    Intent intent_main;

    static String imgUrl = "http://13.125.229.179/JPEG_20190512_201100.jpg";
    String borrower,Owner;

    ArrayList<String> rentnumber,bikecode,rent_time;
    ArrayList<String> Title,Context,img_url;

    //Test
    ArrayList<ListVI> list_itemArrayList;
    private ListView listview ;
    private PointViewAdapter adapter;
    String fcm_title,fcm_body,token;
    sendPost send_post=new sendPost();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_allow);
        list_itemArrayList=new ArrayList<>();
        intent_main=getIntent();
        Owner=intent_main.getStringExtra("userId");
        System.out.println("OWNER = "+Owner);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    json_borrower=jsonResponse.getString("borrower");
                    json_renttime = jsonResponse.getString("renttime");
                    json_bikecode = jsonResponse.getString("bikecode");
                    json_returntime=jsonResponse.getString("returntime");
                    int len = json_borrower.split(",").length;

                    if(json_borrower.equals("[]")) {
                        len=0;
                    }
                    //System.out.println(json_borrower+"FOR TEST    "+len);
                    for(int i=0;i<len;i++){
                        list_itemArrayList.add(new ListVI("http://13.125.229.179/white.png",json_bikecode.split("\"")[2*i+1],json_renttime.split("\"")[2*i+1]+"~\n"+json_returntime.split("\"")[2*i+1],json_borrower.split("\"")[2*i+1]+" 님"));
                    }
                    final Intent intent = new Intent(CheckAllowActivity.this,ReportMainAcitivity.class);
                    //System.out.println(list_itemArrayList.get(0).getFinish());
                    //변수 초기화
                    adapter = new PointViewAdapter(CheckAllowActivity.this,list_itemArrayList);
                    listview = (ListView) findViewById(R.id.listview_report);

                    //어뎁터 할당
                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        int report_count=0;
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            Response.Listener<String> responseListener8 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try{
                                        JSONObject jsonResponse = new JSONObject(response);
                                        token=jsonResponse.getString("token");
                                        System.out.println("GET TOKEN = "+token);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest(list_itemArrayList.get(position).getFinish().replace(" 님",""),responseListener8);
                            RequestQueue queue8 = Volley.newRequestQueue(CheckAllowActivity.this);
                            queue8.add(getUserTokenRequest);

                            Response.Listener<String> responseListener3 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        report_count=jsonResponse.getInt("reportcount");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckAllowActivity.this);
                                        builder.setMessage(list_itemArrayList.get(position).getFinish()+"의 신고 횟수 : "+report_count+"회\n"+"대여를 승인하시겠습니까?")
                                                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Response.Listener<String> responseListener6 = new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonResponse = new JSONObject(response);
                                                                    System.out.println(jsonResponse.getString("request")+"REQUEST CODE");
                                                                }
                                                                catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };
                                                        SendAllowRequest sendAllowRequest = new SendAllowRequest(list_itemArrayList.get(position).getFinish().replace(" 님",""),"ok", responseListener6);
                                                        RequestQueue queue6 = Volley.newRequestQueue(CheckAllowActivity.this);
                                                        queue6.add(sendAllowRequest);
                                                        fcm_body="대여가 승인 되었습니다";
                                                        fcm_title="대여 승인 알림";
                                                        send_post.execute(20);
                                                        list_itemArrayList.remove(list_itemArrayList.get(position));
                                                        listview.setAdapter(adapter);
                                                    }
                                                })
                                                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Response.Listener<String> responseListener6 = new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jsonResponse = new JSONObject(response);
                                                                }
                                                                catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };
                                                        SendAllowRequest sendAllowRequest = new SendAllowRequest(list_itemArrayList.get(position).getFinish().replace(" 님",""),"no", responseListener6);
                                                        RequestQueue queue6 = Volley.newRequestQueue(CheckAllowActivity.this);
                                                        queue6.add(sendAllowRequest);
                                                        fcm_body="대여가 거부 되었습니다";
                                                        fcm_title="대여 거부 알림";
                                                        send_post.execute(20);
                                                        list_itemArrayList.remove(list_itemArrayList.get(position));
                                                        listview.setAdapter(adapter);
                                                    }
                                                })
                                                .create()
                                                .show();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ReportCountReqeust reportCountReqeust = new ReportCountReqeust(list_itemArrayList.get(position).getFinish().replace(" 님",""), responseListener3);
                            RequestQueue queue3 = Volley.newRequestQueue(CheckAllowActivity.this);
                            queue3.add(reportCountReqeust);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        GetRentrequestRequest getRentrequestRequest = new GetRentrequestRequest(Owner,responseListener);
        RequestQueue queue = Volley.newRequestQueue(CheckAllowActivity.this);
        queue.add(getRentrequestRequest);
    }

    public class sendPost extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers){
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            //fcm_title="대여 요청";
            //fcm_body=borrower+" 님의 자전거 대여 요청이 있습니다.";
            RequestBody body = RequestBody.create(mediaType, "{ \"to\" : \""+token+"\",\r\n\"priority\" : \"high\", \r\n\"notification\" : { \r\n\"body\" : \""+fcm_body+"\", \"title\" : \""+fcm_title+"\" }, \"data\" : { \"title\" : \""+fcm_title+"\", \"message\" : \""+fcm_body+"\" } }");
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .addHeader("authorization", "key=AAAAte8fpPE:APA91bF-RJ5YY5uL6IIHbOu41KizVrkwMsAotezRUn_JfZyt06-0TGr7_kusw2fomtf3PkuqDktkRY9rpwZNKZpOCyzYV8lEsQZk8LQKLtf2hFQvvgH2dugpBHkMt_LITayTJy_OgSdl")
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "e9e95045-6675-5a29-1cb6-fb3a2ffcf7ee")
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
            }catch (IOException e){
                ;
            }
            return 0;
        }
        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }
}
