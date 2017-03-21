package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jbren on 3/20/2017.
 */

public class MovieBasicFragment extends Fragment
{
    private MovieInfo mMovieInfo;

    private ImageView mMoviePoster;
    private Button mFavoriteMovie;
    private TextView mMovieTitle;

    public MovieBasicFragment()
    {
    }

    public static MovieBasicFragment newInstance(MovieInfo MovieDetails)
    {
        MovieBasicFragment fragment = new MovieBasicFragment();

        Bundle args = new Bundle();
        args.putParcelable(MovieInfoActivity.MOVIE_BASIC_ARG, MovieDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moviebasic, container, false);
        mMovieInfo = getArguments().getParcelable(MovieInfoActivity.MOVIE_BASIC_ARG);

        mMoviePoster = (ImageView)rootView.findViewById(R.id.basic_movie_image);
        mFavoriteMovie = (Button)rootView.findViewById(R.id.basic_movie_favorite);
        mMovieTitle = (TextView)rootView.findViewById(R.id.basic_movie_title);

        mFavoriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FavoriteMovie();
            }
        });

        mMovieTitle.setText(mMovieInfo.mMovieTitle);

        String ImageUri = "https://image.tmdb.org/t/p/w500" + mMovieInfo.mMoviePosterPath;
        Picasso.with(getActivity()).load(ImageUri).into(mMoviePoster);

        SetFavorite(CheckIfMovieFavorite());

        return rootView;
    }

    public void SetFavorite(boolean IsFavorite)
    {
        if(IsFavorite)
        {
            mFavoriteMovie.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.img_star_yellow));
        }
        else
        {
            mFavoriteMovie.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.img_star_grey));
        }
    }

    public boolean CheckIfMovieFavorite()
    {
        boolean ContainsMovie;
        String Selection = MovieContract.MovieEntry.COLUMN_ID + " = ?";
        String SelectArgs[] = { String.valueOf(mMovieInfo.mMovieID) };
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
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
            getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, Deletion, DeletionArgs);

        }
        else
        {
            getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, mMovieInfo.ToContentValue());
        }

        SetFavorite(!ContainsMovie);
    }
}
