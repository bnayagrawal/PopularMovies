package xyz.bnayagrawal.android.popularmovies.util;

import android.net.Uri;

/**
 * Created by binay on 27/1/18.
 */

public class NetworkUtils {

    //TMDB API_KEY of your account
    private static final String API_KEY = "your_api_key_here";

    //Addresses
    private static final String TMDB_BASE_API_URL = "https://api.themoviedb.org/3/";

    //Paths
    private static final String TMDB_CONFIGURATION_PATH = "configuration";
    private static final String TMDB_POPULAR_MOVIES_PATH = "movie/popular";
    private static final String TMDB_TOP_RATED_MOVIES_PATH = "movie/top_rated";

    //Params
    private static final String PARAM_API_KEY = "api_key";

    /**
     * Builds url used to retrieve configuration.
     *
     * @return returns string URL which will be used to retrieve configuration
     */
    public static String buildRequestConfigUrl() {
        Uri uri = Uri.parse(TMDB_BASE_API_URL + TMDB_CONFIGURATION_PATH)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        return uri.toString();
    }

    public static String buildPopularMoviesUrl() {
        Uri uri = Uri.parse(TMDB_BASE_API_URL + TMDB_POPULAR_MOVIES_PATH)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        return uri.toString();
    }

    public static String buildTopRatedMoviesUrl() {
        Uri uri = Uri.parse(TMDB_BASE_API_URL + TMDB_TOP_RATED_MOVIES_PATH)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        return uri.toString();
    }
}
