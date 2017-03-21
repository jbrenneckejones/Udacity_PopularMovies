package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by jbren on 2/22/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>
{

    private List<MovieInfo> mMovieData;

    // public static int MOVIE_COUNT_MAX = 40;
    public static int MOVIE_VIEW_MAX = 20;

    private final MovieAdapterOnClickHandler mClickHandler;

    public List<MovieInfo> GetMovies()
    {
        return mMovieData;
    }

    public interface MovieAdapterOnClickHandler {
        void OnClick(MovieInfo ClickedMovieInfo);
    }

    public MovieAdapter(MovieAdapterOnClickHandler ClickHandler) {
        mClickHandler = ClickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMoviePicture;

        public MovieAdapterViewHolder(View NewView) {
            super(NewView);
            mMoviePicture = (ImageView) NewView.findViewById(R.id.tv_movie_picture);
            NewView.setOnClickListener(this);
        }

        @Override
        public void onClick(View NewView) {
            int AdapterPosition = getAdapterPosition();
            MovieInfo MovieInfo = mMovieData.get(AdapterPosition);
            mClickHandler.OnClick(MovieInfo);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup NewViewGroup, int ViewType)
    {
        Context context = NewViewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_picture;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, NewViewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        MovieInfo movieData = mMovieData.get(position);
        String imageUri = "https://image.tmdb.org/t/p/w500" + movieData.mMoviePosterPath;
        Picasso.with(movieAdapterViewHolder.itemView.getContext()).load(imageUri).into(movieAdapterViewHolder.mMoviePicture);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    public void RemoveOldData(int IndexStart, int Count)
    {
        for(int Index = Count; Index > 0; Index--)
        {
            int RemovalIndex = mMovieData.size() - IndexStart;

            if(RemovalIndex >= mMovieData.size())
            {
                RemovalIndex = mMovieData.size() - 1;
            }
            else if(RemovalIndex < 0)
            {
                RemovalIndex = 0;
            }

            mMovieData.remove(RemovalIndex);
        }

        notifyDataSetChanged();
    }

    public void AddMovieDataBefore(MovieInfo[] MovieData)
    {
        for(int i = MovieData.length - 1; i >= 0; --i)
        {
            mMovieData.add(0, MovieData[i]);
        }


        // while(getItemCount() > MOVIE_COUNT_MAX)
        {
            mMovieData.remove(getItemCount() - 1);
        }

        notifyDataSetChanged();
    }

    public void AddMovieDataAfter(MovieInfo[] MovieData)
    {
        int Size = mMovieData.size();
        for(MovieInfo Movie : MovieData)
        {
            mMovieData.add(Movie);
        }

        int Index = Size;
        while(Size != mMovieData.size())
        {
            // mMovieData.remove(0);
            notifyItemChanged(Size - 1);
            Size++;
        }
    }

    public void SetMovieData(MovieInfo[] MovieData)
    {
        mMovieData = new ArrayList<MovieInfo>(MovieData.length);

        for(MovieInfo Movie : MovieData)
        {
            mMovieData.add(Movie);
        }

        notifyDataSetChanged();
    }

    public MovieInfo GetElement(int Index)
    {
        return mMovieData.get(Index);
    }
}
