package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieContentProvider extends ContentProvider
{
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = BuildUriMatcher();

    public static UriMatcher BuildUriMatcher()
    {
        UriMatcher Matcher = new UriMatcher(UriMatcher.NO_MATCH);

        Matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        Matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return Matcher;
    }

    private MovieDBHelper mMovieDBHelper;

    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        mMovieDBHelper = new MovieDBHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri UriValue, ContentValues Values)
    {
        final SQLiteDatabase Database = mMovieDBHelper.getWritableDatabase();

        int Match = sUriMatcher.match(UriValue);
        Uri Result;

        switch (Match)
        {
            case MOVIES:
                long ID = Database.insert(MovieContract.MovieEntry.TABLE_NAME, null, Values);
                if ( ID > 0 )
                {
                    Result = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, ID);
                } else
                    {
                    throw new android.database.SQLException("Failed to insert row into " + UriValue);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + UriValue);
        }

        getContext().getContentResolver().notifyChange(UriValue, null);

        return Result;
    }


    @Override
    public Cursor query(@NonNull Uri UriValue, String[] Projection, String Selection,
                        String[] SelectionArgs, String SortOrder)
    {
        final SQLiteDatabase Database = mMovieDBHelper.getReadableDatabase();

        int Match = sUriMatcher.match(UriValue);
        Cursor Result;

        switch (Match)
        {
            case MOVIES:
                Result =  Database.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        Projection,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        SortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + UriValue);
        }

        Result.setNotificationUri(getContext().getContentResolver(), UriValue);

        return Result;
    }


    @Override
    public int delete(@NonNull Uri UriValue, String Selection, String[] SelectionArgs)
    {
        final SQLiteDatabase Database = mMovieDBHelper.getWritableDatabase();

        int Match = sUriMatcher.match(UriValue);
        int MoviesDeleted;

        switch (Match)
        {
            case MOVIES_WITH_ID:
                String ID = UriValue.getPathSegments().get(1);

                MoviesDeleted = Database.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        "_id=?", new String[]{ID});
                break;

            case MOVIES:
                MoviesDeleted = Database.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        Selection,
                        SelectionArgs
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + UriValue);
        }

        if (MoviesDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(UriValue, null);
        }

        return MoviesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
