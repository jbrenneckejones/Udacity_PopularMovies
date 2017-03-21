package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import java.net.URL;

public class MovieInfoActivity extends AppCompatActivity
{
    private MoviePagerAdapter mMoviePagerAdapter;
    private ViewPager mMoviePager;

    private MovieInfo mMovieInfo;
    private String[] mMovieTrailerKeys;
    private MovieReviewInfo[] mReviews;

    public static final String MOVIE_BASIC_ARG = "basic-arg";
    public static final String MOVIE_DETAIL_ARG = "detail-arg";
    public static final String MOVIE_VIDEO_ARG = "video-arg";
    public static final String MOVIE_REVIEW_ARG = "review-arg";
    public static final int MOVIE_BASIC_POSITION = 0;
    public static final int MOVIE_DETAIL_POSITION = 1;
    public static final int MOVIE_VIDEO_POSITION = 2;
    public static final int MOVIE_REVIEW_POSITION = 3;

    public MovieInfo GetMovie()
    {
        return mMovieInfo;
    }

    public String[] GetTrailerKeys()
    {
        return mMovieTrailerKeys;
    }

    public MovieReviewInfo[] GetReviews()
    {
        return mReviews;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME) != null)
            {
                mMovieInfo = intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME);
                String ImageUri = "https://image.tmdb.org/t/p/w500" + mMovieInfo.mMoviePosterPath;
            }
        }

        new FetchVideosTask().execute(mMovieInfo.mMovieID);
        new FetchReviewTask().execute(mMovieInfo.mMovieID);

        setContentView(R.layout.activity_movie_info);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMoviePagerAdapter = new MoviePagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mMoviePager = (ViewPager) findViewById(R.id.container);
        mMoviePager.setAdapter(mMoviePagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mMoviePager);
    }

    public class FetchReviewTask extends AsyncTask<Integer, Void, MovieReviewInfo[]>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected MovieReviewInfo[] doInBackground(Integer... params) {

            if (params.length == 0)
            {
                return null;
            }

            int MovieID = params[0];
            URL movieRequestUrl = MovieHelperUtility.buildReviewUrl(MovieID);

            try
            {
                String jsonMovieResponse = MovieHelperUtility.getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                MovieReviewInfo[] MovieReviews = MovieHelperUtility.getSimpleReviewsStringsFromJson(MovieInfoActivity.this, jsonMovieResponse);

                return MovieReviews;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieReviewInfo[] movieReviews)
        {
            super.onPostExecute(movieReviews);
            if(movieReviews == null || movieReviews.length == 0)
            {
                // mMovieBinding.movieReviewText.setText("No reviews available.");

                return;
            }

            mReviews = movieReviews;

            // mMovieBinding.movieReviewText.setText("");
            for(int Index = 0; Index < mReviews.length; ++Index)
            {
                MovieReviewInfo Review = mReviews[Index];

                // mMovieBinding.movieReviewText.append(Review.mAuthor + " says : " + Review.mContent + "\n\n");
            }
        }
    }

    public class FetchVideosTask extends AsyncTask<Integer, Void, String[]>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Integer... params) {

            if (params.length == 0)
            {
                return null;
            }

            int MovieID = params[0];
            URL movieRequestUrl = MovieHelperUtility.buildTrailerUrl(MovieID);

            try
            {
                String jsonMovieResponse = MovieHelperUtility.getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                String[] MovieVideoKeys = MovieHelperUtility.getSimpleTrailerStringsFromJson(MovieInfoActivity.this, jsonMovieResponse);

                return MovieVideoKeys;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieTrailers)
        {
            super.onPostExecute(movieTrailers);
            if(movieTrailers == null)
            {
                return;
            }

            if(movieTrailers.length == 0)
            {
               // mMovieBinding.moviePlayId.setText("No Trailers Found");
            }
            else
            {
                mMovieTrailerKeys = movieTrailers;
            }
        }
    }
}
