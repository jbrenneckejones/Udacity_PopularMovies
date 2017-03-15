package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies.databinding.ActivityMovieInfoBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieInfoActivity extends AppCompatActivity implements View.OnClickListener
{

    ActivityMovieInfoBinding mMovieBinding;

    private MovieInfo mMovieInfo;

    public static final String MOVIE_VIDEO_URL = "/videos";
    public static final String MOVIE_REVIEW_URL = "/reviews";

    public static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch";
    public static final String YOUTUBE_VIDEO_KEY_QUERY = "v";

    private String mMovieTrailerKey;
    private MovieReviewInfo[] mReviews;

    public Uri GetVideoUrl()
    {
        return Uri.parse(YOUTUBE_VIDEO_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_KEY_QUERY, mMovieTrailerKey)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mMovieBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_info);

        mMovieBinding.moviePlayId.setOnClickListener(this);

        mMovieBinding.favoriteButton.setOnClickListener(this);

        mMovieBinding.movieShareId.setOnClickListener(this);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME) != null)
            {
                mMovieInfo = intentThatStartedThisActivity.getParcelableExtra(MainActivity.PARCELABLE_NAME);
                mMovieBinding.tvMovieBasicDisplay.setText(mMovieInfo.mMovieTitle);
                mMovieBinding.tvMovieDetailDisplay.setText(mMovieInfo.GetMovieDetailInfo());

                String ImageUri = "https://image.tmdb.org/t/p/w500" + mMovieInfo.mMoviePosterPath;
                Picasso.with(getApplicationContext()).load(ImageUri).into(mMovieBinding.tvMoviePicture);
            }
        }

        SetFavorite(CheckIfMovieFavorite());

        new FetchVideosTask().execute(mMovieInfo.mMovieID);
        new FetchReviewTask().execute(mMovieInfo.mMovieID);
    }

    @Override
    public void onClick(View view)
    {
        int ViewID = view.getId();

        if(ViewID == R.id.movie_play_id && mMovieTrailerKey != null)
        {
            Uri VideoUri = GetVideoUrl();
            startActivity(new Intent(Intent.ACTION_VIEW, VideoUri));
        }

        if(ViewID == R.id.movie_share_id)
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Hey everyone check out this trailer: " + GetVideoUrl();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Movie Share");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if(ViewID == R.id.favoriteButton)
        {
            FavoriteMovie();
        }
    }

    public void SetFavorite(boolean IsFavorite)
    {
        if(IsFavorite)
        {
            mMovieBinding.favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.img_star_yellow));
        }
        else
        {
            mMovieBinding.favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.img_star_grey));
        }
    }

    public boolean CheckIfMovieFavorite()
    {
        boolean ContainsMovie;
        String Selection = MovieContract.MovieEntry.COLUMN_ID + " = ?";
        String SelectArgs[] = { String.valueOf(mMovieInfo.mMovieID) };
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                MovieDBHelper.MOVIE_DB_PROJECTIONS, Selection, SelectArgs, null);

        if(cursor == null || cursor.getCount() <= 0)
        {
            ContainsMovie = false;
        }
        else
        {
            ContainsMovie = true;
        }

        if(cursor != null)
        {
            cursor.close();
        }

        return ContainsMovie;
    }

    public void FavoriteMovie()
    {
        boolean ContainsMovie = CheckIfMovieFavorite();

        if(ContainsMovie)
        {
            String Deletion = MovieContract.MovieEntry.COLUMN_ID + " = ?";
            String DeletionArgs[] = { String.valueOf(mMovieInfo.mMovieID) };
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, Deletion, DeletionArgs);

        }
        else
        {
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, mMovieInfo.ToContentValue());
        }

        SetFavorite(!ContainsMovie);
    }

    public static URL buildReviewUrl(int MovieId)
    {

        String MovieUrl = MainActivity.MOVIE_BASE_URL + "/" + String.valueOf(MovieId) + MOVIE_REVIEW_URL;

        Uri builtUri = Uri.parse(MovieUrl).buildUpon()
                .appendQueryParameter(MainActivity.MOVIE_API_QUERY, MainActivity.MOVIE_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailerUrl(int MovieId) {

        String MovieUrl = MainActivity.MOVIE_BASE_URL + "/" + String.valueOf(MovieId) + MOVIE_VIDEO_URL;

        Uri builtUri = Uri.parse(MovieUrl).buildUpon()
                .appendQueryParameter(MainActivity.MOVIE_API_QUERY, MainActivity.MOVIE_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String[] getSimpleTrailerStringsFromJson(Context context, String movieJsonStr)
            throws JSONException
    {

        final String MOVIE_LIST = "results";

        final String MOVIE_VIDEO_KEY = "key";

        final String ERROR_MESSAGE_CODE = "errors";

        String parsedVideoData[];

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(ERROR_MESSAGE_CODE)) {
            String errorMessage = movieJson.getString(ERROR_MESSAGE_CODE);

            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.show();

            return null;
        }

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        parsedVideoData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++)
        {

            JSONObject movieObject = movieArray.getJSONObject(i);

            String MovieKey = movieObject.getString(MOVIE_VIDEO_KEY);

            if(MovieKey != null)
            {
                final String YoutubeVideoQuery = "v=";
                if(MovieKey.contains(YoutubeVideoQuery))
                {
                    int Index = MovieKey.indexOf(YoutubeVideoQuery);
                    MovieKey = MovieKey.substring(Index + YoutubeVideoQuery.length());
                }
            }

            parsedVideoData[i] = MovieKey;
        }

        return parsedVideoData;
    }

    public static MovieReviewInfo[] getSimpleReviewsStringsFromJson(Context context, String movieJsonStr)
            throws JSONException
    {

        final String MOVIE_LIST = "results";

        final String MOVIE_REVIEW_CONTENT = "content";
        final String MOVIE_REVIEW_AUTHOR = "author";

        final String ERROR_MESSAGE_CODE = "errors";

        MovieReviewInfo parsedReviewData[];

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(ERROR_MESSAGE_CODE)) {
            String errorMessage = movieJson.getString(ERROR_MESSAGE_CODE);

            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.show();

            return null;
        }

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        parsedReviewData = new MovieReviewInfo[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++)
        {

            JSONObject movieObject = movieArray.getJSONObject(i);

            MovieReviewInfo Review = new MovieReviewInfo();
            Review.mAuthor = movieObject.getString(MOVIE_REVIEW_AUTHOR);
            Review.mContent = movieObject.getString(MOVIE_REVIEW_CONTENT);

            parsedReviewData[i] = Review;
        }

        return parsedReviewData;
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
            URL movieRequestUrl = buildReviewUrl(MovieID);

            try
            {
                String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                MovieReviewInfo[] MovieReviews = getSimpleReviewsStringsFromJson(MovieInfoActivity.this, jsonMovieResponse);

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
                mMovieBinding.movieReviewText.setText("No reviews available.");

                return;
            }

            mReviews = movieReviews;

            mMovieBinding.movieReviewText.setText("");
            for(int Index = 0; Index < mReviews.length; ++Index)
            {
                MovieReviewInfo Review = mReviews[Index];

                mMovieBinding.movieReviewText.append(Review.mAuthor + " says : " + Review.mContent + "\n\n");
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
            URL movieRequestUrl = buildTrailerUrl(MovieID);

            try
            {
                String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                String[] MovieVideoKeys = getSimpleTrailerStringsFromJson(MovieInfoActivity.this, jsonMovieResponse);

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
                mMovieBinding.moviePlayId.setText("No Trailers Found");
            }
            else
            {
                mMovieTrailerKey = movieTrailers[0];
            }
        }
    }
}
