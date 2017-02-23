package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MovieInfoActivity extends AppCompatActivity {

    private MovieInfo mMovieInfo;
    private TextView mMovieDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);


        mMovieDisplay = (TextView) findViewById(R.id.tv_movie_display);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME) != null) {
                mMovieInfo = intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME);
                mMovieDisplay.setText(mMovieInfo.GetMovieInfo());
            }
        }

    }
}
