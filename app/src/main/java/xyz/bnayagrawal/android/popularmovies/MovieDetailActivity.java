package xyz.bnayagrawal.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import xyz.bnayagrawal.android.popularmovies.data.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (null != savedInstanceState) {
            mMovie = savedInstanceState.getParcelable("MOVIE");
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra("MOVIE"))
                mMovie = intent.getParcelableExtra("MOVIE");
        }

        ((TextView) findViewById(R.id.tv_title)).setText(mMovie.getTitle());
        ((TextView) findViewById(R.id.tv_overview)).setText(mMovie.getOverview());
        ((TextView) findViewById(R.id.tv_release_date)).setText(
                getResources().getString(R.string.released_on).concat(mMovie.getReleaseDate()));
        ((TextView) findViewById(R.id.tv_vote_average)).setText(
                getResources().getString(R.string.ratings).concat(String.valueOf(mMovie.getVoteAverage())));

        final ImageView imageView = findViewById(R.id.img_thumbnail);
        Picasso.with(this).load(mMovie.getPosterPath()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                imageView.startAnimation(animation);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("MOVIE", mMovie);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
