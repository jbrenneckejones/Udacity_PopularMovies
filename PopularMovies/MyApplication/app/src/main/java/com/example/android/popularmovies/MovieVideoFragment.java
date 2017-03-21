package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class MovieVideoFragment extends Fragment implements View.OnClickListener
{
    private String[] VideoKeys;
    private ImageButton[] Buttons;

    public MovieVideoFragment()
    {
    }

    public static MovieVideoFragment newInstance(String[] MovieYoutubeKeys)
    {
        MovieVideoFragment fragment = new MovieVideoFragment();
        Bundle args = new Bundle();
        args.putStringArray(MovieInfoActivity.MOVIE_VIDEO_ARG, MovieYoutubeKeys);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_movievideo, container, false);
        VideoKeys = getArguments().getStringArray(MovieInfoActivity.MOVIE_VIDEO_ARG);

        LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.movie_video_linearlayout);

        if(layout == null)
        {
            return rootView;
        }

        if(VideoKeys == null)
        {

            TextView ReviewText = new TextView(rootView.getContext());
            ReviewText.setText("No video available.");

            ReviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.fab_margin));

            layout.addView(ReviewText);

            return rootView;
        }

        Buttons = new ImageButton[VideoKeys.length];
        for(int Index = 0; Index < VideoKeys.length; ++Index)
        {
            final ImageButton VideoButton = new ImageButton(getActivity());
            VideoButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

            VideoButton.setOnClickListener(this);

            String YoutubeImage = "http://img.youtube.com/vi/" + VideoKeys[Index] + "/0.jpg";

            Picasso.with(rootView.getContext())
                    .load(YoutubeImage)
                    .placeholder(R.drawable.common_full_open_on_phone)
                    .into(VideoButton);

            layout.addView(VideoButton);

            Buttons[Index] = VideoButton;
        }

        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        for(int Index = 0; Index < Buttons.length; ++Index)
        {
            if(Buttons[Index].getId() == view.getId())
            {

                Uri MovieLink = MovieHelperUtility.GetVideoUrl(VideoKeys[Index]);

                startActivity(new Intent(Intent.ACTION_VIEW, MovieLink));
            }
        }
    }
}
