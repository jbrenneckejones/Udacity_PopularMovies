package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int VERSION = 1;

    public static final String[] MOVIE_DB_PROJECTIONS =
            {
                    MovieContract.MovieEntry.COLUMN_ID,
                    MovieContract.MovieEntry.COLUMN_AVERAGE,
                    MovieContract.MovieEntry.COLUMN_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_PAGE,
                    MovieContract.MovieEntry.COLUMN_POPULARITY,
                    MovieContract.MovieEntry.COLUMN_POSTER,
                    MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                    MovieContract.MovieEntry.COLUMN_VOTES,
                    MovieContract.MovieEntry.COLUMN_TITLE,
            };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_AVERAGE = 1;
    public static final int INDEX_MOVIE_OVERVIEW = 2;
    public static final int INDEX_MOVIE_PAGE = 3;
    public static final int INDEX_MOVIE_POPULARITY = 4;
    public static final int INDEX_MOVIE_POSTER = 5;
    public static final int INDEX_MOVIE_RELEASE_DATE = 6;
    public static final int INDEX_MOVIE_VOTES = 7;
    public static final int INDEX_MOVIE_TITLE = 8;

    // Constructor
    MovieDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase Database) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID                    + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_ID              + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_AVERAGE         + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW        + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_PAGE            + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY      + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER          + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE    + " DATE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTES           + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE           + " TEXT NOT NULL);";

        Database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase Database, int OldVersion, int NewVersion)
    {
        Database.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(Database);
    }
}
