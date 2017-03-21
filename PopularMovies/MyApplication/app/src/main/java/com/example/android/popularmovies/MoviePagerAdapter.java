package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jbren on 3/20/2017.
 */

public class MoviePagerAdapter extends FragmentPagerAdapter
{
    private MovieInfoActivity mMovieInfoActivity;
    private static final int MOVIE_PAGE_COUNT = 4;

    public MoviePagerAdapter(FragmentManager fragmentManager, MovieInfoActivity movieInfoActivity)
    {
        super(fragmentManager);

        mMovieInfoActivity = movieInfoActivity;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case MovieInfoActivity.MOVIE_BASIC_POSITION:
                return MovieBasicFragment.newInstance(mMovieInfoActivity.GetMovie());
            case MovieInfoActivity.MOVIE_DETAIL_POSITION:
                return MovieDetailFragment.newInstance(mMovieInfoActivity.GetMovie());
            case MovieInfoActivity.MOVIE_VIDEO_POSITION:
                return MovieVideoFragment.newInstance(mMovieInfoActivity.GetTrailerKeys());
            case MovieInfoActivity.MOVIE_REVIEW_POSITION:
                return MovieReviewFragment.newInstance(mMovieInfoActivity.GetReviews());
            default:
                return MovieDetailFragment.newInstance(mMovieInfoActivity.GetMovie());
        }
    }

    @Override
    public int getCount()
    {
        return MOVIE_PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position) {
            case MovieInfoActivity.MOVIE_BASIC_POSITION:
                return "TITLE";
            case MovieInfoActivity.MOVIE_DETAIL_POSITION:
                return "DETAIL";
            case MovieInfoActivity.MOVIE_VIDEO_POSITION:
                return "VIDEOS";
            case MovieInfoActivity.MOVIE_REVIEW_POSITION:
                return "REVIEWS";
        }
        return null;
    }
}
