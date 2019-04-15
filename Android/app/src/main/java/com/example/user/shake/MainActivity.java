package com.example.user.shake;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import android.widget.Button;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    ListView listView = null;
    private String userName,userID;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Save User Information
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        Toast.makeText(getApplicationContext(),userName,Toast.LENGTH_SHORT).show();
        mContext=this;

        // Navigation Bar implementation
        Toast.makeText(getApplicationContext(),"화면을 스와이프하시면 메뉴가 보입니다.",Toast.LENGTH_SHORT).show();


        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        final String[] items = {userID+"님","Rent", "항목2", "항목3", "항목4"} ;

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items) ;

        listView = (ListView) findViewById(R.id.drawer_menulist) ;
        listView.setAdapter(adapter) ;

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0 : //List1
                        Toast.makeText(getApplicationContext(),"Welcome!",Toast.LENGTH_SHORT).show();
                        break ;
                    case 1 : //List2
                        Intent intent = new Intent(MainActivity.this, RentActivity.class);
                        MainActivity.this.startActivity(intent);
                        break ;
                    case 2 : //List3
                        Toast.makeText(getApplicationContext(),"List3 Clicked",Toast.LENGTH_SHORT).show();
                        break ;
                    case 3 : //List4
                        Toast.makeText(getApplicationContext(),"List4 Clicked",Toast.LENGTH_SHORT).show();
                        break ;
                    case 4 : //List5
                        Toast.makeText(getApplicationContext(),"List5 Clicked",Toast.LENGTH_SHORT).show();
                        break ;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;
                drawer.closeDrawer(Gravity.LEFT) ;
            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap map) {

        PhpConnect task = new PhpConnect();
        ArrayList<String> bikeLatLng = new ArrayList<>();
        ArrayList<BikeInfo> bikeList = new ArrayList<>();
        MarkerOptions markerOptions = new MarkerOptions();
        int bikeCost = 0;
        float bikeLatitude = 0, bikeLongitude = 0;
        String bikeOwner = "", bikeType = "", bikeImgUrl = "", bikeCode = "";
        String bikeLockId = "", bikeModelName = "";

        try {
            bikeLatLng = task.execute("http://13.125.229.179/getBikeInfo.php").get();
        }catch (InterruptedException e){
            //bikeLatLng = "fail connect";
            e.printStackTrace();
        }catch (ExecutionException e){
            //bikeLatLng = "fail connect";
            e.printStackTrace();
        }

        for (int i = 0; i < bikeLatLng.size(); i += 9){
            bikeOwner = bikeLatLng.get(i);
            bikeCode = bikeLatLng.get(i + 1);
            bikeLatitude = Float.parseFloat(bikeLatLng.get(i + 2));
            bikeLongitude = Float.parseFloat(bikeLatLng.get(i + 3));
            bikeCost = Integer.parseInt(bikeLatLng.get(i + 4));
            bikeImgUrl = bikeLatLng.get(i + 5);
            bikeLockId = bikeLatLng.get(i + 6);
            bikeModelName = bikeLatLng.get(i + 7);
            bikeType = bikeLatLng.get(i + 8);

            BikeInfo bike = new BikeInfo(bikeOwner, bikeCode, bikeLatitude, bikeLongitude, bikeCost, bikeImgUrl, bikeLockId, bikeModelName, bikeType);
            bikeList.add(bike);
            LatLng bikeLocation = new LatLng(bikeLatitude, bikeLongitude);
            simpleAddMarker(map, markerOptions, bikeLocation, "공유자: " + bikeOwner, "자전거 종류: " + bikeType);
        }

        LatLng SEOUL = new LatLng(37.56, 126.97);;
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    private void simpleAddMarker(final GoogleMap map, MarkerOptions markerOptions, LatLng pos, String title, String context) {
        markerOptions.position(pos);
        markerOptions.title(title);
        markerOptions.snippet(context);
        map.addMarker(markerOptions);

    }

    public String[] getInfo(){
        String[] temp = new String[2];
        temp[0]=userID;
        temp[1]=userName;
        return temp;

    }
}
