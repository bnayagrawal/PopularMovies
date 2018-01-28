package xyz.bnayagrawal.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.bnayagrawal.android.popularmovies.adapter.MovieArrayAdapter;
import xyz.bnayagrawal.android.popularmovies.data.Movie;
import xyz.bnayagrawal.android.popularmovies.util.Configuration;
import xyz.bnayagrawal.android.popularmovies.util.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private static final String INSTANCE_STATE_TAG_MOVIES = "MOVIES";
    private static final String INSTANCE_STATE_TAG_CONFIGURATION = "CONFIGURATION";
    private static final String INSTANCE_STATE_TAG_CHECKED_RADIO_BUTTON = "CHECKED_RADIO_BUTTON";

    private ArrayList<Movie> mMovies;
    private Configuration mConfiguration;

    private MovieArrayAdapter mMovieArrayAdapter;
    private LinearLayout mLinearLayoutProgressView;
    private LinearLayout mLinearLayoutErrorView;
    private GridView mGridViewRoot;

    private TextView mTvErrorMessage;
    private Button mBtnRetry;

    private RequestQueue mQueue;
    private JsonObjectRequest mJsObjRequest;

    private int mCheckedRadioButton;
    private Movie.SortOrder mSortOrder;
    private boolean mLoading = false;
    private Animation mFadeInAnimation, mFadeOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinearLayoutProgressView = findViewById(R.id.linear_layout_progress_view);
        mLinearLayoutErrorView = findViewById(R.id.linear_layout_error_view);
        mTvErrorMessage = findViewById(R.id.tv_error_message);
        mBtnRetry = findViewById(R.id.btn_retry);
        mGridViewRoot = findViewById(R.id.grid_view_popular_movies);
        mQueue = Volley.newRequestQueue(this);
        mSortOrder = Movie.SortOrder.POPULAR_MOVIES;

        //animation
        mFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);

        if (null != savedInstanceState) {
            mMovies = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TAG_MOVIES);
            mConfiguration = savedInstanceState.getParcelable(INSTANCE_STATE_TAG_CONFIGURATION);
            mCheckedRadioButton = savedInstanceState.getInt(INSTANCE_STATE_TAG_CHECKED_RADIO_BUTTON);
        } else {
            mMovies = new ArrayList<>();
            mCheckedRadioButton = R.id.popular_movies;
            fetchMovieList();
        }

        mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
        mGridViewRoot.setAdapter(mMovieArrayAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(INSTANCE_STATE_TAG_MOVIES, mMovies);
        savedInstanceState.putParcelable(INSTANCE_STATE_TAG_CONFIGURATION, mConfiguration);
        savedInstanceState.putInt(INSTANCE_STATE_TAG_CHECKED_RADIO_BUTTON, mCheckedRadioButton);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(mCheckedRadioButton).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCheckedRadioButton = item.getItemId();
        switch (item.getItemId()) {
            case R.id.popular_movies:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    if (!mLoading) {
                        mSortOrder = Movie.SortOrder.POPULAR_MOVIES;
                        fetchMovieList();
                    }
                }
            case R.id.top_rated_movies:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    if (!mLoading) {
                        mSortOrder = Movie.SortOrder.TOP_RATED_MOVIES;
                        fetchMovieList();
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void fetchConfiguration() {
        if (!isOnline()) {
            showErrorView(getResources().getString(R.string.network_error));
            mBtnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideErrorView();
                    fetchConfiguration();
                    fetchMovieList();
                }
            });
            return;
        }

        String configUrl = NetworkUtils.buildRequestConfigUrl();

        mJsObjRequest = new JsonObjectRequest
                (Request.Method.GET, configUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Prepare Configuration
                        mConfiguration = null;
                        try {
                            String base_url, secure_base_url;
                            HashMap<String, String[]> size_map = new HashMap<>();
                            String[] size_names = {"backdrop_sizes", "logo_sizes", "poster_sizes", "profile_sizes", "still_sizes"};

                            JSONObject images = response.getJSONObject("images");
                            base_url = images.getString("base_url");
                            secure_base_url = images.getString("secure_base_url");

                            String[] sizes;
                            JSONArray array;
                            for (String sizeName : size_names) {
                                array = images.getJSONArray(sizeName);
                                sizes = new String[array.length()];
                                for (int j = 0; j < array.length(); j++)
                                    sizes[j] = array.getString(j);
                                size_map.put(sizeName, sizes);
                            }

                            mConfiguration = new Configuration(base_url,
                                    secure_base_url,
                                    size_map.get("backdrop_sizes"),
                                    size_map.get("logo_sizes"),
                                    size_map.get("poster_sizes"),
                                    size_map.get("profile_sizes"),
                                    size_map.get("still_sizes")
                            );

                        } catch (JSONException jsonException) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            mConfiguration = null;
                            jsonException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorView(getResources().getString(R.string.config_fetch_error));
                        mBtnRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hideErrorView();
                                fetchConfiguration();
                                fetchMovieList();
                            }
                        });
                        error.printStackTrace();
                    }
                });
        mQueue.add(mJsObjRequest);
    }

    private void fetchMovieList() {
        toggleProgressView(true);

        if (mConfiguration == null)
            fetchConfiguration();

        if (!isOnline()) {
            showErrorView(getResources().getString(R.string.network_error));
            mBtnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideErrorView();
                    fetchMovieList();
                }
            });
            return;
        }

        String url = (mSortOrder == Movie.SortOrder.POPULAR_MOVIES) ? NetworkUtils.buildPopularMoviesUrl() :
                NetworkUtils.buildTopRatedMoviesUrl();

        mLoading = true;
        mJsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mMovies.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            int id;
                            double vote_average;
                            String title, poster_path, release_date, overview;
                            JSONObject item;

                            for (int i = 0; i < results.length(); i++) {
                                item = results.getJSONObject(i);
                                id = item.getInt("id");
                                vote_average = item.getDouble("vote_average");
                                title = item.getString("title");

                                //build poster path
                                poster_path = mConfiguration.getBase_url()
                                        + "w185"
                                        + item.getString("poster_path");

                                release_date = item.getString("release_date");
                                overview = item.getString("overview");
                                mMovies.add(new Movie(
                                        id, title, poster_path, overview, vote_average, release_date
                                ));
                            }

                            mLoading = false;
                            mMovieArrayAdapter.notifyDataSetChanged();
                            toggleProgressView(false);
                        } catch (JSONException jsonException) {
                            mLoading = false;
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            jsonException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorView(getResources().getString(R.string.something_went_wrong));
                        mBtnRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hideErrorView();
                                fetchMovieList();
                            }
                        });
                        error.printStackTrace();
                    }
                });
        mQueue.add(mJsObjRequest);
    }

    private void toggleProgressView(boolean show) {
        if (show) {
            mGridViewRoot.setVisibility(View.GONE);
            mGridViewRoot.startAnimation(mFadeOutAnimation);
            mLinearLayoutProgressView.setVisibility(View.VISIBLE);
            mLinearLayoutProgressView.startAnimation(mFadeInAnimation);
        } else {
            mLinearLayoutProgressView.setVisibility(View.GONE);
            mLinearLayoutProgressView.startAnimation(mFadeOutAnimation);
            mGridViewRoot.setVisibility(View.VISIBLE);
            mGridViewRoot.startAnimation(mFadeInAnimation);
        }
    }

    private void showErrorView(String message) {
        toggleProgressView(false);
        mTvErrorMessage.setText(message);
        mLinearLayoutProgressView.setVisibility(View.GONE);
        mLinearLayoutProgressView.startAnimation(mFadeOutAnimation);
        mLinearLayoutErrorView.setVisibility(View.VISIBLE);
        mLinearLayoutErrorView.startAnimation(mFadeInAnimation);
    }

    private void hideErrorView() {
        mLinearLayoutErrorView.setVisibility(View.GONE);
        mLinearLayoutErrorView.startAnimation(mFadeOutAnimation);
        mLinearLayoutProgressView.setVisibility(View.VISIBLE);
        mLinearLayoutProgressView.startAnimation(mFadeInAnimation);
    }
}
