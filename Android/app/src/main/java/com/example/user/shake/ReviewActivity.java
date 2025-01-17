package com.example.user.shake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.shake.Request.PhpRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ReviewActivity extends AppCompatActivity {

    RatingBar ratingBar;
    EditText review;
    Button submitButton;
    ImageView reviewImage;

    float rating;
    float totalRating = 0;
    String userId;
    String imageUrl;
    int rentnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        rentnumber = intent.getIntExtra("rentnumber", -1);
        ratingBar = findViewById(R.id.reviewRatingBar);
        review = findViewById(R.id.reviewEdit);
        submitButton = findViewById(R.id.reviewSubmitButton);
        reviewImage = findViewById(R.id.reviewImage);

        if (rentnumber == -1)
            finish();

        rating = (float) 3.5;

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating_val, boolean b) {
                rating = rating_val;
            }
        });

        imageUrl = "not implemented yet";
        PhpConnect task = new PhpConnect();
        imageUrl = "http://13.125.229.179/"+userId+"_"+Integer.toString(rentnumber)+".jpg";
        if (imageUrl.contains("http")) {
            try{
                Thread.sleep(300);
                Glide.with(this).load(imageUrl).into(reviewImage);
            }catch (InterruptedException e){

            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rentnumber == -1){
                    Toast.makeText(getApplicationContext(),"rentnumber error!",Toast.LENGTH_SHORT).show();
                }
                else if (review.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"내용을 입력해주세요!",Toast.LENGTH_SHORT).show();
                }
                else{
                    String bikecode = PhpRequest.getBikecodeByRentnumber(rentnumber);
                    int reviewCount = PhpRequest.getReviewCount(bikecode);
                    totalRating = (PhpRequest.getBikeRating(bikecode) * reviewCount + rating) / (reviewCount + 1);
                    PhpRequest.updateBikeRating(bikecode, totalRating);
                    PhpRequest.updateBikeReviewCount(bikecode);

                    PhpConnect task2 = new PhpConnect();
                    try {
                        task2.execute("http://13.125.229.179/insertReview.php?rentnumber="+Integer.toString(rentnumber)+"&contents="+review.getText().toString()+"&imageUrl="+imageUrl+"&rating="+Float.toString(rating)).get();
                        Toast.makeText(getApplicationContext(),"리뷰가 등록되었습니다!",Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });

    }
}
