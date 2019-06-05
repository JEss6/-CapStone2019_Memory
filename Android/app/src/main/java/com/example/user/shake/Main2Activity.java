package com.example.user.shake;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.shake.Request.UpdateTokenRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;


    ListView listView = null;
    private String userName,userID;
    TextView navTitle;
    TextView navContext;
    public static Context mContext;
    private ArrayList<BikeInfo> bikeList;
    private ArrayList<BikeInfo> rankerList;
    private int markerClickFlag = -1;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("TOKEN = "+token);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        mContext=this;

        navTitle = findViewById(R.id.textNavTitle);
        navContext = findViewById(R.id.textNavContext);
        Toast.makeText(getApplicationContext(),"화면을 스와이프하시면 메뉴가 보입니다.",Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.mapMain);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println(jsonResponse.getString("token") + "    "+jsonResponse.getString("userid"));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        UpdateTokenRequest updateTokenRequest = new UpdateTokenRequest(userID, token,responseListener);
        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this);
        queue.add(updateTokenRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        TextView main_title = (TextView)findViewById(R.id.textNavTitle);
        main_title.setText(userID+" 님");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.itemRent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
            builder.setMessage("신고할 항목을 선택하세요")
                    .setNegativeButton("대여 기록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent2 = new Intent(Main2Activity.this, ReportActivity.class);
                            intent2.putExtra("userId", userID);
                            Main2Activity.this.startActivity(intent2);
                        }
                    })
                    .setPositiveButton("공유 기록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent2 = new Intent(Main2Activity.this, ReportOwnerActivity.class);
                            intent2.putExtra("userId", userID);
                            Main2Activity.this.startActivity(intent2);
                        }
                    })
                    .create()
                    .show();
            //Main2Activity.this.startActivity(intent2);
        } else if (id == R.id.itemRegister) {
            Intent intent2 = new Intent(Main2Activity.this, BikeRegisterActivity.class);
            intent2.putExtra("userId", userID);
            startActivityForResult(intent2, 2);
        }
        else if (id == R.id.itemInfo) {
            Intent intent2 = new Intent(Main2Activity.this, InfoActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }
        else if (id == R.id.itemcamera) {
            Intent intent2 = new Intent(Main2Activity.this, CheckAllowActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }
        else if (id == R.id.itemReviewList){
            Intent intent2 = new Intent(Main2Activity.this, ReviewListActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }
        else if (id == R.id.itemPoint){
            Intent intent2 = new Intent(Main2Activity.this, PointActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }
        else if (id == R.id.itemTerms){
            Intent intent2 = new Intent(Main2Activity.this, TermsActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }
        else if (id == R.id.itemCategory){
            Intent intent2 = new Intent(Main2Activity.this, CategoryActivity.class);
            intent2.putExtra("userId", userID);
            Main2Activity.this.startActivity(intent2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == 2){
                BikeInfo newBike = (BikeInfo) data.getSerializableExtra("newBike");
                bikeList.add(newBike);
                LatLng bikeLocation = new LatLng(newBike.getBikeLatitude(), newBike.getBikeLongitude());
                simpleAddMarker(mMap, bikeLocation, newBike.getBikeOwner(), "자전거 종류: " + newBike.getBikeType());
            }
        }
    }

    private void findRanker(){

        for (int i = 0; i < bikeList.size(); ++i){
            float myRating =  bikeList.get(i).getBikeRating();
            int upRatingCount = 0;
            int myReviewCount = bikeList.get(i).getBike_review_count();

            if (myReviewCount == 0){
                continue;
            }
            for (int j = 0; j < bikeList.size(); ++j){
                float rating = bikeList.get(j).getBikeRating();
                double gapLatitude, gapLongitude, distance;
                int reviewCount = bikeList.get(j).getBike_review_count();
                Log.d("tag", bikeList.get(i).getBikeCode()+"/"+Integer.toString(bikeList.get(i).getBike_review_count())+"  /  "+bikeList.get(j).getBikeCode()+"/"+bikeList.get(j).getBike_review_count());
                if (i == j || reviewCount == 0 || rating == 0)
                    continue;

                gapLatitude = bikeList.get(i).getBikeLatitude() - bikeList.get(j).getBikeLatitude();
                gapLongitude = bikeList.get(i).getBikeLongitude() - bikeList.get(j).getBikeLongitude();
                //위도 경도 km로 단위 변경
                gapLatitude *= 110;
                gapLongitude *= 88.74;
                distance = Math.sqrt(Math.pow(gapLatitude, 2) + Math.pow(gapLongitude, 2));

                if (distance <= 3 && reviewCount >= 5){
                    if (myRating < rating){
                        upRatingCount += 1;
                    }
                }
                if (upRatingCount >= 1)
                    break;
            }
            if (upRatingCount == 0 && myReviewCount >= 5){
                rankerList.add(bikeList.get(i));
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        PhpConnect task = new PhpConnect();
        ArrayList<String> bikeLatLng = new ArrayList<>();
        bikeList = new ArrayList<>();
        MarkerOptions markerOptions = new MarkerOptions();
        int bikeCost = 0, bike_review_count = 0;
        double bikeLatitude = 0, bikeLongitude = 0;
        String bikeOwner = "", bikeType = "", bikeImgUrl = "", bikeCode = "";
        String bikeLockId = "", bikeModelName = "", bikeAddInfo = "";
        float bikeRating = 0;

        mMap = map;

        try {
            bikeLatLng = task.execute("http://13.125.229.179/getBikeInfo.php").get();
        }catch (InterruptedException e){
            //bikeLatLng = "fail connect";
            e.printStackTrace();
        }catch (ExecutionException e){
            //bikeLatLng = "fail connect";
            e.printStackTrace();
        }

        for (int i = 0; i < bikeLatLng.size(); i += 12){
            bikeOwner = bikeLatLng.get(i);
            bikeCode = bikeLatLng.get(i + 1);
            bikeLatitude = Double.parseDouble(bikeLatLng.get(i + 2));
            bikeLongitude = Double.parseDouble(bikeLatLng.get(i + 3));
            bikeCost = Integer.parseInt(bikeLatLng.get(i + 4));
            bikeImgUrl = bikeLatLng.get(i + 5);
            bikeLockId = bikeLatLng.get(i + 6);
            bikeModelName = bikeLatLng.get(i + 7);
            bikeType = bikeLatLng.get(i + 8);
            bikeAddInfo = bikeLatLng.get(i + 9);
            bikeRating = Float.parseFloat(bikeLatLng.get(i + 10));
            bike_review_count = Integer.parseInt(bikeLatLng.get(i + 11));

            BikeInfo bike = new BikeInfo(bikeOwner, bikeCode, bikeLatitude, bikeLongitude, bikeCost, bikeImgUrl, bikeLockId, bikeModelName, bikeType, bikeAddInfo);
            bike.setBikeRating(bikeRating);
            bike.setBikeReviewCount(bike_review_count);
            bikeList.add(bike);
        }
        rankerList = new ArrayList<>();
        findRanker();

        for (int i = 0; i < bikeList.size(); ++i){
            LatLng bikeLocation = new LatLng(bikeList.get(i).getBikeLatitude(), bikeList.get(i).getBikeLongitude());
            int rankerFlag = -1;
            for (int j = 0; j < rankerList.size(); ++j) {
                if (bikeList.get(i).getBikeCode().equals(rankerList.get(j).getBikeCode())){
                    rankerFlag = j;
                    break;
                }
            }
            if (rankerFlag == -1){
                simpleAddMarker(map, bikeLocation, bikeList.get(i).getBikeOwner(), "자전거 종류: " + bikeList.get(i).getBikeType());
            }else{
                rankerAddMarker(map, bikeLocation, rankerList.get(rankerFlag).getBikeOwner(), "자전거 종류: " + rankerList.get(rankerFlag).getBikeType());
            }
        }

        GpsInfo gpsInfo = new GpsInfo(getApplicationContext());

        LatLng myLocation = new LatLng(gpsInfo.getLatitude(), gpsInfo.getLongitude());
        userAddMarker(map, myLocation);

        LatLng SEOUL = new LatLng(37.506, 126.958);
        if (myLocation.latitude > 30 && myLocation.latitude < 50 && myLocation.longitude < 150 && myLocation.longitude > 100)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


        mMap.setOnMarkerClickListener(this);
    }

    private void simpleAddMarker(final GoogleMap map, LatLng pos, String title, String context) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.title(title);
        markerOptions.snippet(context);
        map.addMarker(markerOptions);

    }

    private void rankerAddMarker(final GoogleMap map, LatLng pos, String title, String context){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.title(title);
        markerOptions.snippet(context);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ranker_star);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        map.addMarker(markerOptions);
    }

    private void userAddMarker(final GoogleMap map, LatLng pos){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.zIndex(1);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.userpos_icon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        map.addMarker(markerOptions);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String mOwner = marker.getTitle();
        for (int i = 0; i < bikeList.size(); ++i){
            if ((bikeList.get(i).getBikeOwner().equals(mOwner)) && (marker.getPosition().latitude == bikeList.get(i).getBikeLatitude())){
                if (markerClickFlag == i){
                    Intent intent = new Intent(Main2Activity.this, RentActivity.class);
                    intent.putExtra("borrower", userID);
                    intent.putExtra("bikecode", bikeList.get(i).getBikeCode());
                    startActivity(intent);
                }else{
                    markerClickFlag = i;
                }
                break;
            }
        }
        return false;
    }
    public String[] getInfo(){
        String[] temp = new String[2];
        temp[0]=userID;
        temp[1]=userName;
        return temp;
    }
}
