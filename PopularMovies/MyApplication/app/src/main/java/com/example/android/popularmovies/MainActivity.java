package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;

    public static JSONArray sCurrentJsonResponse;
    public static int sCurrentJsonTopIndex;
    public static int sCurrentJsonBottomIndex;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String KEY_ADAPTER_STATE = "adapter_state";
    private static Bundle mBundleRecyclerViewState;

    private int mCurrentPage = 1;
    private String mCurrentSortQuery = MovieHelperUtility.MOVIE_TOP_RATED;

    public static int sMaxPages = 0;
    public static final String PARCELABLE_NAME = "MovieInfoData";

    public static final String SHARED_PREF_SORTQUERY = "sort-query";

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

        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_SORTQUERY, MODE_PRIVATE);
        mCurrentSortQuery = preferences.getString(SHARED_PREF_SORTQUERY, MovieHelperUtility.MOVIE_TOP_RATED);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(mBundleRecyclerViewState != null)
        {
            SetupRecycler();
            GetBundleState();
        }
        else
        {
            SetupRecycler();
            LoadData();
        }
    }

    private void SetupRecycler()
    {
        GridLayoutManager GridLayout = new GridLayoutManager(this, MovieHelperUtility.GetNumberOfColumns(this));

        mRecyclerView.setLayoutManager(GridLayout);

        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        RecyclerView.OnScrollListener ScrollListener = new RecyclerView.OnScrollListener()
        {
            private boolean Loading = true;
            int FirstVisibleItems, VisibleItemCount, TotalItemsCount, PreviousTotal, VisibleThreshold;

            private void ListCheck(RecyclerView recyclerView)
            {
                GridLayoutManager layoutmanager = (GridLayoutManager) recyclerView.getLayoutManager();

                VisibleItemCount = mRecyclerView.getChildCount();
                TotalItemsCount = layoutmanager.getItemCount();
                FirstVisibleItems = layoutmanager.findFirstVisibleItemPosition();
                VisibleThreshold = MovieAdapter.MOVIE_VIEW_MAX;

                /*
                if(FirstVisibleItems <= 3)
                {
                    if(mCurrentPage == 1 || mMovieAdapter.GetElement(0).mMoviePage == 1)
                    {
                        return;
                    }

                    if(ChangePageNumber(-1))
                    {
                        LoadMovieData(LIST_COMMAND.ADD_BEFORE);
                    }
                }
                */

                if (Loading)
                {
                    if (TotalItemsCount > PreviousTotal)
                    {
                        Loading = false;
                        PreviousTotal = TotalItemsCount;

                        // MovieAdapter Adapter = (MovieAdapter)recyclerView.getAdapter();
                        // Adapter.RemoveOldData(FirstVisibleItems + 5, 10);
                    }
                }

                if (!Loading && (TotalItemsCount - VisibleItemCount)
                        <= (FirstVisibleItems + VisibleThreshold))
                {
                    // Do something
                    if(ChangePageNumber(1))
                    {
                        LoadMovieData(LIST_COMMAND.ADD_AFTER);
                    }
                    Loading = true;
                }
            }

            private void EndCheck(RecyclerView recyclerView)
            {
                /*
                if (!recyclerView.canScrollVertically(-1))
                {

                }
                else if (!recyclerView.canScrollVertically(1))
                {
                    if(ChangePageNumber(1))
                    {
                        LoadMovieData(LIST_COMMAND.ADD_AFTER);
                    }
                }
                */
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                ListCheck(recyclerView);
                EndCheck(recyclerView);
            }
        };

        mRecyclerView.addOnScrollListener(ScrollListener);
    }

    private void LoadData()
    {
        if(mCurrentSortQuery.equals(MovieHelperUtility.MOVIE_FAVORITES))
        {
            mCurrentPage = 1;
            LoadFavoriteData();
        }
        else
        {
            LoadMovieData(LIST_COMMAND.INITIALIZE);
        }
    }

    private void SetBundleState()
    {
        mBundleRecyclerViewState = new Bundle();
        Parcelable ListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, ListState);

        MovieAdapter Adapter = (MovieAdapter)mRecyclerView.getAdapter();

        MovieInfo[] Movies = new MovieInfo[Adapter.GetMovies().size()];
        Movies = Adapter.GetMovies().toArray(Movies);
        mBundleRecyclerViewState.putParcelableArray(KEY_ADAPTER_STATE, Movies);
    }

    private void GetBundleState()
    {
        if(mBundleRecyclerViewState != null)
        {
            Parcelable ListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(ListState);

            MovieAdapter Adapter = (MovieAdapter)mRecyclerView.getAdapter();

            MovieInfo[] Movies = (MovieInfo[])mBundleRecyclerViewState.getParcelableArray(KEY_ADAPTER_STATE);

            if(Movies == null)
            {
                LoadData();
            }
            else
            {
                Adapter.SetMovieData(Movies);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SetBundleState();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        GetBundleState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_SORTQUERY, MODE_APPEND);
        preferences.edit().putString(SHARED_PREF_SORTQUERY, mCurrentSortQuery).apply();
    }

    private void LoadMovieData(LIST_COMMAND Command)
    {
        if(mCurrentSortQuery.equals(MovieHelperUtility.MOVIE_FAVORITES))
        {
            return;
        }

        new FetchMoviesTask().execute(String.valueOf(mCurrentPage), mCurrentSortQuery, Command.toString());
    }

    private void LoadFavoriteData()
    {
        new FetchFavoritesTask().execute();
    }

    @Override
    public void OnClick(MovieInfo ClickedMovieInfo)
    {
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

        if (id == R.id.menuTopRated)
        {
            mCurrentPage = 1;
            mCurrentSortQuery = MovieHelperUtility.MOVIE_TOP_RATED;

            SharedPreferences preferences = getSharedPreferences(SHARED_PREF_SORTQUERY, MODE_APPEND);
            preferences.edit().putString(SHARED_PREF_SORTQUERY, mCurrentSortQuery).apply();

            LoadData();
            return true;
        }

        if (id == R.id.menuPopularity)
        {
            mCurrentPage = 1;
            mCurrentSortQuery = MovieHelperUtility.MOVIE_POPULAR;

            SharedPreferences preferences = getSharedPreferences(SHARED_PREF_SORTQUERY, MODE_APPEND);
            preferences.edit().putString(SHARED_PREF_SORTQUERY, mCurrentSortQuery).apply();

            LoadData();
            return true;
        }

        if(id == R.id.menuFavorites)
        {
            mCurrentPage = 1;
            mCurrentSortQuery = MovieHelperUtility.MOVIE_FAVORITES;

            SharedPreferences preferences = getSharedPreferences(SHARED_PREF_SORTQUERY, MODE_APPEND);
            preferences.edit().putString(SHARED_PREF_SORTQUERY, mCurrentSortQuery).apply();

            LoadData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean ChangePageNumber(int Amount)
    {
        if(Amount > 0)
        {
            if(mCurrentPage < sMaxPages)
            {
                mCurrentPage += Amount;
                return true;
            }
        }

        if(Amount < 0)
        {
            if (mCurrentPage > 1)
            {
                mCurrentPage += Amount;

                return true;
            }
        }

        return false;
    }

    public class FetchFavoritesTask extends AsyncTask<Void, Void, MovieInfo[]>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieInfo[] doInBackground(Void... params)
        {
            Cursor cursor = getContentResolver()
                    .query(MovieContract.MovieEntry.CONTENT_URI,
                            MovieDBHelper.MOVIE_DB_PROJECTIONS, null, null, null);

            MovieInfo[] Movies = new MovieInfo[0];

            if(cursor != null)
            {
                cursor.moveToFirst();
                Movies = new MovieInfo[cursor.getCount()];
                for(int Index = 0; Index < cursor.getCount(); ++Index)
                {
                    MovieInfo Movie = new MovieInfo();

                    Movie.mMovieTitle = cursor.getString(MovieDBHelper.INDEX_MOVIE_TITLE);
                    Movie.mMovieID = cursor.getInt(MovieDBHelper.INDEX_MOVIE_ID);
                    Movie.mMovieAverage = cursor.getFloat(MovieDBHelper.INDEX_MOVIE_AVERAGE);
                    Movie.mMovieOverview = cursor.getString(MovieDBHelper.INDEX_MOVIE_OVERVIEW);
                    Movie.mMoviePage = cursor.getInt(MovieDBHelper.INDEX_MOVIE_PAGE);
                    Movie.mMoviePopularity = cursor.getFloat(MovieDBHelper.INDEX_MOVIE_POPULARITY);
                    Movie.mMoviePosterPath = cursor.getString(MovieDBHelper.INDEX_MOVIE_POSTER);
                    Movie.mMovieReleaseDate = cursor.getString(MovieDBHelper.INDEX_MOVIE_RELEASE_DATE);
                    Movie.mMovieVotes = cursor.getInt(MovieDBHelper.INDEX_MOVIE_VOTES);

                    Movies[Index] = Movie;
                    cursor.moveToNext();
                }
            }

            return Movies;
        }

        @Override
        protected void onPostExecute(MovieInfo[] movieData)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null)
            {
                mRecyclerView.setVisibility(View.VISIBLE);

                mMovieAdapter.SetMovieData(movieData);
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

            if (params.length == 0)
            {
                return null;
            }

            int pageNumber = Integer.parseInt(params[0]);
            String SortByQuery = params[1];
            LIST_COMMAND Command = Enum.valueOf(LIST_COMMAND.class, params[2]);
            mCommand = Command;
            URL movieRequestUrl = MovieHelperUtility.buildMoviePageUrl(pageNumber, SortByQuery);

            try
            {
                String jsonMovieResponse = MovieHelperUtility.getResponseFromHttpUrl(movieRequestUrl);

                Log.d("Movie Response: ", jsonMovieResponse);

                MovieInfo[] MovieInfoData = MovieHelperUtility.getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return MovieInfoData;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieInfo[] movieData)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null)
            {
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
