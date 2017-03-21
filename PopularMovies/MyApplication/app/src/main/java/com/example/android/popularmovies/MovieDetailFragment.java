package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jbren on 3/19/2017.
 */

public class MovieDetailFragment extends Fragment
{

    public MovieDetailFragment()
    {
    }

    public static MovieDetailFragment newInstance(MovieInfo MovieDetails)
    {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MovieInfoActivity.MOVIE_DETAIL_ARG, MovieDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moviedetail, container, false);
        TextView OverviewText = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        TextView IntrinsicsText = (TextView) rootView.findViewById(R.id.detail_movie_intrinsics);

        MovieInfo Movie = getArguments().getParcelable(MovieInfoActivity.MOVIE_DETAIL_ARG);

        OverviewText.setText(Movie.mMovieOverview);
        IntrinsicsText.setText("Release Date: " + Movie.mMovieReleaseDate + "\n\n" +Movie.GetMovieIntrinsics());
        return rootView;
    }
}
