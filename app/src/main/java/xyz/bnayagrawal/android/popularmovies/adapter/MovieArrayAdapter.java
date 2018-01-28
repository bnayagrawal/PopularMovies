package xyz.bnayagrawal.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.bnayagrawal.android.popularmovies.MovieDetailActivity;
import xyz.bnayagrawal.android.popularmovies.R;
import xyz.bnayagrawal.android.popularmovies.data.Movie;

/**
 * Created by binay on 27/1/18.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String DEBUG_TAG = MovieArrayAdapter.class.getSimpleName();

    private String thumbnailUrl;
    private Activity mActivity;

    public MovieArrayAdapter(Activity activity, List<Movie> movies) {
        super(activity.getApplicationContext(), 0, movies);
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item, parent, false);

        final ImageView imageView = (ImageView) convertView;
        thumbnailUrl = getItem(position).getPosterPath();

        Picasso.with(getContext())
                .load(thumbnailUrl)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.e(DEBUG_TAG, thumbnailUrl);
                    }
                });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra("MOVIE", getItem(position));
                getContext().startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        return convertView;
    }
}
