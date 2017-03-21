package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
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

/**
 * Created by jbren on 3/20/2017.
 */

public class MovieHelperUtility
{
    public static final String MOVIE_VIDEO_URL = "/videos";
    public static final String MOVIE_REVIEW_URL = "/reviews";

    public static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch";
    public static final String YOUTUBE_VIDEO_KEY_QUERY = "v";

    public static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";

    public static String MOVIE_API_QUERY = "api_key";
    // TODO: Change this to your api key
    public static String MOVIE_API_KEY = "API_KEY";

    public static String MOVIE_LANGUAGE_QUERY = "language";
    public static String MOVIE_DEFAULT_LANGUAGE = "en-US";

    public static String MOVIE_SORT_BY_QUERY = "sort_by";

    public static String MOVIE_TOP_RATED = "/top_rated";
    public static String MOVIE_POPULAR = "/popular";
    public static String MOVIE_FAVORITES = "favorites";

    public static String MOVIE_SORT_BY_POPULARITY_DESC = "popularity.desc";
    public static String MOVIE_SORT_BY_POPULARITY_ASC = "popularity.asc";
    public static String MOVIE_SORT_BY_VOTE_AVERAGE_DESC = "vote_average.desc";
    public static String MOVIE_SORT_BY_VOTE_AVERAGE_ASC = "vote_average.asc";

    public static String MOVIE_INCLUDE_ADULT_QUERY = "include_adult";
    public static String MOVIE_DEFAULT_ADULT = "false";

    public static String MOVIE_INCLUDE_VIDEO_QUERY = "include_video";
    public static String MOVIE_DEFAULT_VIDEO = "true";

    public static String JSON_MOVIE_PAGE_QUERY = "page";
    public static final String JSON_MOVIE_LIST = "results";
    public static final String JSON_MOVIE_ID = "id";
    public static final String JSON_MOVIE_POSTER_PATH = "poster_path";
    public static final String JSON_MOVIE_SYNOPSES = "overview";
    public static final String JSON_MOVIE_RELEASE_DATE = "release_date";
    public static final String JSON_MOVIE_TITLE = "title";
    public static final String JSON_MOVIE_POPULARITY = "popularity";
    public static final String JSON_MOVIE_VOTES = "vote_count";
    public static final String JSON_MOVIE_VOTE_AVERAGE = "vote_average";
    public static final String JSON_ERROR_MESSAGE_CODE = "errors";

    public static int GetNumberOfColumns(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int NumberOfColumns = (int) (dpWidth / 180);
        return NumberOfColumns;
    }

    public static Uri GetVideoUrl(String YoutubeKey)
    {
        return Uri.parse(YOUTUBE_VIDEO_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_KEY_QUERY, YoutubeKey)
                .build();
    }

    public static URL buildMoviePageUrl(int PageNumber, String SortByQuery)
    {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL + SortByQuery).buildUpon()
                .appendQueryParameter(MOVIE_API_QUERY, MOVIE_API_KEY)
                // .appendQueryParameter(MOVIE_SORT_BY_QUERY, SortByQuery)
                // .appendQueryParameter(MOVIE_LANGUAGE_QUERY, MOVIE_DEFAULT_LANGUAGE)
                // .appendQueryParameter(MOVIE_INCLUDE_ADULT_QUERY, MOVIE_DEFAULT_ADULT)
                // .appendQueryParameter(MOVIE_INCLUDE_VIDEO_QUERY, MOVIE_DEFAULT_VIDEO)
                .appendQueryParameter(JSON_MOVIE_PAGE_QUERY, Integer.toString(PageNumber))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static MovieInfo[] JsonArrayToMovieInfo(JSONArray JSONData)
    {
        MovieInfo[] MovieData = new MovieInfo[JSONData.length()];

        for (int i = 0; i < JSONData.length(); i++) {
            MovieInfo Movie = new MovieInfo();

            JSONObject movieObject = null;
            try {
                movieObject = JSONData.getJSONObject(i);

                // Movie.mMoviePage = PageNumber;
                Movie.mMovieID = movieObject.getInt(JSON_MOVIE_ID);

                Movie.mMoviePosterPath = movieObject.getString(JSON_MOVIE_POSTER_PATH);
                Movie.mMovieOverview = movieObject.getString(JSON_MOVIE_SYNOPSES);
                Movie.mMovieReleaseDate = movieObject.getString(JSON_MOVIE_RELEASE_DATE);

                Movie.mMovieTitle = movieObject.getString(JSON_MOVIE_TITLE);
                Movie.mMoviePopularity = (float) movieObject.getDouble(JSON_MOVIE_POPULARITY);

                Movie.mMovieVotes = movieObject.getInt(JSON_MOVIE_VOTES);
                Movie.mMovieAverage = (float) movieObject.getDouble(JSON_MOVIE_VOTE_AVERAGE);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            MovieData[i] = Movie;
        }

        return MovieData;
    }

    public static MovieInfo[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        MovieInfo[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        MainActivity.sMaxPages = movieJson.getInt("total_pages");

        /* Is there an error? */
        if (movieJson.has(JSON_ERROR_MESSAGE_CODE)) {
            String errorMessage = movieJson.getString(JSON_ERROR_MESSAGE_CODE);

            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.show();

            return null;
        }

        JSONArray JsonData = movieJson.getJSONArray(JSON_MOVIE_LIST);
        parsedMovieData = JsonArrayToMovieInfo(JsonData);

        return parsedMovieData;
    }

    public static URL buildReviewUrl(int MovieId)
    {

        String MovieUrl = MOVIE_BASE_URL + "/" + String.valueOf(MovieId) + MOVIE_REVIEW_URL;

        Uri builtUri = Uri.parse(MovieUrl).buildUpon()
                .appendQueryParameter(MOVIE_API_QUERY, MOVIE_API_KEY)
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

        String MovieUrl = MOVIE_BASE_URL + "/" + String.valueOf(MovieId) + MOVIE_VIDEO_URL;

        Uri builtUri = Uri.parse(MovieUrl).buildUpon()
                .appendQueryParameter(MOVIE_API_QUERY, MOVIE_API_KEY)
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
}
