package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.Space;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jbren on 3/19/2017.
 */

public class MovieReviewFragment extends Fragment
{
    public MovieReviewFragment()
    {
    }

    public static MovieReviewFragment newInstance(MovieReviewInfo[] MovieReviews)
    {
        MovieReviewFragment fragment = new MovieReviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(MovieInfoActivity.MOVIE_REVIEW_ARG, MovieReviews);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moviereviews, container, false);
        MovieReviewInfo[] Reviews = (MovieReviewInfo[])getArguments().getParcelableArray(MovieInfoActivity.MOVIE_REVIEW_ARG);

        LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.movie_review_linearlayout);

        if(layout == null)
        {
            return rootView;
        }

        if(Reviews == null)
        {
            TextView ReviewText = new TextView(rootView.getContext());
            ReviewText.setText("No reviews available.");

            ReviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.fab_margin));

            layout.addView(ReviewText);

            return rootView;
        }

        TextView[] ReviewsTexts = new TextView[Reviews.length];
        for(int Index = 0; Index < Reviews.length; ++Index)
        {
            TextView ReviewText = new TextView(rootView.getContext());
            ReviewText.setText(Reviews[Index].GetReviewNeat());

            ReviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.fab_margin));

            layout.addView(ReviewText);

            ReviewsTexts[Index] = ReviewText;

            View Spacer = new View(rootView.getContext());

            Spacer.setBackgroundColor(ContextCompat.getColor(rootView.getContext(), R.color.colorAccent));
            DrawerLayout.LayoutParams SpacerLayout = new DrawerLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    10);
            Spacer.setLayoutParams(SpacerLayout);

            layout.addView(Spacer);
        }

        return rootView;
    }

}
