package com.example.user.shake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.shake.Request.GetRentLogInfo;

import org.json.JSONObject;

import java.util.ArrayList;

public class ReportOwnerActivity extends AppCompatActivity {
    private TextView explain;
    public String json_rentnumber,json_bikecode,json_rent_time;
    Intent intent_main;

    static String imgUrl = "http://13.125.229.179/JPEG_20190512_201100.jpg";
    String owner;

    ArrayList<String> rentnumber,bikecode,rent_time;
    ArrayList<String> Title,Context,img_url;
    ArrayList<Integer> img;

    //Test
    ArrayList<ListVO> list_itemArrayList;
    private ListView listview ;
    private ReportViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_owner);
        Toast.makeText(getApplication(),"신고하고자 하는 항목을 선택하세요",Toast.LENGTH_SHORT).show();
        listview=(ListView)findViewById(R.id.listview_owner_report);
        list_itemArrayList=new ArrayList<>();
        intent_main = getIntent();
        owner=intent_main.getStringExtra("userId");
        //System.out.println("Owner = "+owner);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    json_rentnumber=jsonResponse.getString("rentnumber");
                    json_bikecode = jsonResponse.getString("bikecode");
                    json_rent_time = jsonResponse.getString("rent_time");
                    System.out.println(json_rentnumber+"   "+json_bikecode+"   "+json_rent_time);
                    rentnumber=new ArrayList<>(); img_url=new ArrayList<>();
                    // bikecode=new ArrayList<>(); rent_time=new ArrayList<>();
                    //Title=new ArrayList<>(); Context=new ArrayList<>(); img=new ArrayList<>();

                    int len = json_rentnumber.split(",").length;
                    for(int i=0;i<len;i++){
                        rentnumber.add(json_rentnumber.split("\"")[2*i+1]);
                        list_itemArrayList.add(new ListVO("http://13.125.229.179/JPEG_20190512_201100.jpg",json_bikecode.split("\"")[2*i+1],json_rent_time.split("\"")[2*i+1]));
                    }
                    final Intent intent = new Intent(ReportOwnerActivity.this,ReportMainAcitivity.class);

                    //변수 초기화
                    adapter = new ReportViewAdapter(ReportOwnerActivity.this,list_itemArrayList);
                    listview = (ListView) findViewById(R.id.listview_owner_report);

                    //어뎁터 할당
                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(ReportOwnerActivity.this,list_itemArrayList.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                            intent.putExtra("img_url","http://13.125.229.179/test_18.jpg");
                            intent.putExtra("borrower",intent_main.getStringExtra("userId"));
                            intent.putExtra("bikecode",list_itemArrayList.get(position).getTitle());
                            intent.putExtra("renttime",list_itemArrayList.get(position).getContext());
                            startActivity(intent);
                        }
                    });

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        GetRentLogInfo getRentLogInfo = new GetRentLogInfo(owner, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ReportOwnerActivity.this);
        queue.add(getRentLogInfo);
    }
}

