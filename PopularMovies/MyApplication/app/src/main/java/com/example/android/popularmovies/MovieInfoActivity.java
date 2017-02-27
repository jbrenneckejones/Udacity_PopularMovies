package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieInfoActivity extends AppCompatActivity {

    private MovieInfo mMovieInfo;
    private TextView mMovieDisplay;
    private ImageView mMoviePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);


        mMovieDisplay = (TextView) findViewById(R.id.tv_movie_display);

        mMoviePicture = (ImageView) findViewById(R.id.tv_movie_picture);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME) != null) {
                mMovieInfo = intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME);
                mMovieDisplay.setText(mMovieInfo.GetMovieInfo());

                String imageUri = "https://image.tmdb.org/t/p/w500" + mMovieInfo.mMoviePosterPath;
                Picasso.with(getApplicationContext()).load(imageUri).into(mMoviePicture);

            }
        }

    }
}
