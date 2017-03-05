package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;

    private static int mMaxPages = 0;
    private int mCurrentPage = 1;
    private String mCurrentSortQuery = MOVIE_TOP_RATED;

    public static final String PARCELABLE_NAME = "MovieInfoData";

    public enum LIST_COMMAND
    {
        ADD_BEFORE,
        ADD_AFTER,
        INITIALIZE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                LinearLayoutManager layoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(layoutmanager != null)
                {
                    if(layoutmanager.findFirstCompletelyVisibleItemPosition() == 0)
                    {
                        if(mCurrentPage == 1 || mMovieAdapter.GetElement(0).mMoviePage == 1)
                        {
                            return;
                        }

                        ChangePageNumber(-1);
                        LoadMovieData(LIST_COMMAND.ADD_BEFORE);
                        mRecyclerView.scrollToPosition(80);
                    }
                    else if(layoutmanager.findLastCompletelyVisibleItemPosition() == mMovieAdapter.getItemCount() - 1)
                    {
                        ChangePageNumber(1);
                        LoadMovieData(LIST_COMMAND.ADD_AFTER);
                        mRecyclerView.scrollToPosition(20);
                    }
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        LoadMovieData(LIST_COMMAND.INITIALIZE);
    }

    private void LoadMovieData(LIST_COMMAND Command)
    {
        new FetchMoviesTask().execute(String.valueOf(mCurrentPage), mCurrentSortQuery, Command.toString());
    }

    private static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";


    private static String MOVIE_API_QUERY = "api_key";
    // TODO: Change this to your api key
    private static String MOVIE_API_KEY = "API_KEY";

    private static String MOVIE_LANGUAGE_QUERY = "language";
    private static String MOVIE_DEFAULT_LANGUAGE = "en-US";

    private static String MOVIE_SORT_BY_QUERY = "sort_by";

    private static String MOVIE_TOP_RATED = "/top_rated";
    private static String MOVIE_POPULAR = "/popular";

    private static String MOVIE_SORT_BY_POPULARITY_DESC = "popularity.desc";
    private static String MOVIE_SORT_BY_POPULARITY_ASC = "popularity.asc";
    private static String MOVIE_SORT_BY_VOTE_AVERAGE_DESC = "vote_average.desc";
    private static String MOVIE_SORT_BY_VOTE_AVERAGE_ASC = "vote_average.asc";

    private static String MOVIE_INCLUDE_ADULT_QUERY = "include_adult";
    private static String MOVIE_DEFAULT_ADULT = "false";

    private static String MOVIE_INCLUDE_VIDEO_QUERY = "include_video";
    private static String MOVIE_DEFAULT_VIDEO = "true";

    private static String MOVIE_PAGE_QUERY = "page";

    public static URL buildUrl(int PageNumber, String SortByQuery) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL + SortByQuery).buildUpon()
                .appendQueryParameter(MOVIE_API_QUERY, MOVIE_API_KEY)
                // .appendQueryParameter(MOVIE_SORT_BY_QUERY, SortByQuery)
                // .appendQueryParameter(MOVIE_LANGUAGE_QUERY, MOVIE_DEFAULT_LANGUAGE)
                // .appendQueryParameter(MOVIE_INCLUDE_ADULT_QUERY, MOVIE_DEFAULT_ADULT)
                // .appendQueryParameter(MOVIE_INCLUDE_VIDEO_QUERY, MOVIE_DEFAULT_VIDEO)
                .appendQueryParameter(MOVIE_PAGE_QUERY, Integer.toString(PageNumber))
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

    public static MovieInfo[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        final String MOVIE_LIST = "results";

        final String MOVIE_POSTER_PATH = "poster_path";

        final String MOVIE_SYNOPSES = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";

        final String MOVIE_TITLE = "title";
        final String MOVIE_POPULARITY = "popularity";

        final String MOVIE_VOTES = "vote_count";
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        final String ERROR_MESSAGE_CODE = "errors";

        MovieInfo[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        MainActivity.mMaxPages = movieJson.getInt("total_pages");

        int PageNumber = movieJson.getInt("page");

        /* Is there an error? */
        if (movieJson.has(ERROR_MESSAGE_CODE)) {
            String errorMessage = movieJson.getString(ERROR_MESSAGE_CODE);

            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.show();

            return null;
        }

        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        parsedMovieData = new MovieInfo[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            MovieInfo Movie = new MovieInfo();

            JSONObject movieObject = movieArray.getJSONObject(i);

            Movie.mMoviePage = PageNumber;

            Movie.mMoviePosterPath = movieObject.getString(MOVIE_POSTER_PATH);
            Movie.mMovieOverview = movieObject.getString(MOVIE_SYNOPSES);
            Movie.mMovieReleaseDate = movieObject.getString(MOVIE_RELEASE_DATE);

            Movie.mMovieTitle = movieObject.getString(MOVIE_TITLE);
            Movie.mMoviePopularity = (float) movieObject.getDouble(MOVIE_POPULARITY);

            Movie.mMovieVotes = movieObject.getInt(MOVIE_VOTES);
            Movie.mMovieAverage = (float) movieObject.getDouble(MOVIE_VOTE_AVERAGE);

            parsedMovieData[i] = Movie;
        }

        return parsedMovieData;
    }

    @Override
    public void OnClick(MovieInfo ClickedMovieInfo) {
        Context context = this;
        Class destinationClass = MovieInfoActivity.class;
        Intent intentToStartMovieInfoActivity = new Intent(context, destinationClass);
        intentToStartMovieInfoActivity.putExtra(PARCELABLE_NAME, ClickedMovieInfo);
        startActivity(intentToStartMovieInfoActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuTopRated) {
            mCurrentSortQuery = MOVIE_TOP_RATED;
            LoadMovieData(LIST_COMMAND.INITIALIZE);
            return true;
        }

        if (id == R.id.menuPopularity) {
            mCurrentSortQuery = MOVIE_POPULAR;
            LoadMovieData(LIST_COMMAND.INITIALIZE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ChangePageNumber(int Amount)
    {
        if(Amount > 0)
        {
            if(mCurrentPage < mMaxPages)
            {
                mCurrentPage += Amount;
            }
        }

        if(Amount < 0)
        {
            if(mCurrentPage > 1)
            {
                mCurrentPage += Amount;
            }
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieInfo[]> {

        private LIST_COMMAND mCommand;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieInfo[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            int pageNumber = Integer.parseInt(params[0]);
            String SortByQuery = params[1];
            LIST_COMMAND Command = Enum.valueOf(LIST_COMMAND.class, params[2]);
            mCommand = Command;
            URL movieRequestUrl = buildUrl(pageNumber, SortByQuery);

            try {
                String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                MovieInfo[] MovieInfoData = getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return MovieInfoData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieInfo[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                mRecyclerView.setVisibility(View.VISIBLE);

                switch(mCommand)
                {
                    case ADD_BEFORE:
                        mMovieAdapter.AddMovieDataBefore(movieData);
                        break;
                    case ADD_AFTER:
                        mMovieAdapter.AddMovieDataAfter(movieData);
                        break;
                    case INITIALIZE:
                        mMovieAdapter.SetMovieData(movieData);
                        break;
                }

            }
        }
    }
}
