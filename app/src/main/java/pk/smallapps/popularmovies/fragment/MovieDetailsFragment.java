package pk.smallapps.popularmovies.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.MovieDbOpenHelper;
import pk.smallapps.popularmovies.MoviesDbContract.MovieEntry;
import pk.smallapps.popularmovies.R;


public class MovieDetailsFragment extends Fragment {
    private static final String TITLE_KEY = "title";
    private static final String DATE_KEY = "release_date";
    private static final String RATING_KEY = "rating";
    private static final String OVERVIEW_KEY = "overview";
    public static final String ARG_MOVIE_ID = "movie_id";
    private LinearLayout videosContainerLinearLayout;
    private Button loadReviewsButton;
    private TextView reviewsTextView;
    private String movieId;
    private RequestQueue requestQueue;

    public MovieDetailsFragment() {
    }

    public static MovieDetailsFragment newInstance(String movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        final Button markFavButton = (Button) view.findViewById(R.id.details_mark_fav_button);
        loadReviewsButton = (Button) view.findViewById(R.id.details_load_reviews_button);
        reviewsTextView = (TextView) view.findViewById(R.id.reviews_text_view);
        final TextView favTagTextView = (TextView) view.findViewById(R.id.details_fav_tag_text_view);
        videosContainerLinearLayout = (LinearLayout) view.findViewById(R.id.videos_container_linear_layout);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.details_thumbnail_image_view);

        String movieTitle;
        String movieReleaseDate;
        String movieRating;
        String movieOverview;
        String movieThumbnailRelativeUrl;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        requestVideos(movieId);

        MovieDbOpenHelper movieDbOpenHelper = new MovieDbOpenHelper(getContext());
        SQLiteDatabase moviesDb = movieDbOpenHelper.getWritableDatabase();
        final Cursor cursor = moviesDb.query(MovieEntry.TABLE_NAME, null, MovieEntry.COLUMN_MOVIE_ID + "=" + movieId, null, null, null, null);

        if (cursor.moveToFirst()) {
            movieTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
            movieReleaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
            movieRating = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_USER_RATING));
            movieOverview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW));
            movieThumbnailRelativeUrl = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER));
        } else {
            movieTitle = sharedPreferences.getString(movieId + TITLE_KEY, "");
            movieReleaseDate = sharedPreferences.getString(movieId + DATE_KEY, "");
            movieRating = sharedPreferences.getString(movieId + RATING_KEY, "");
            movieOverview = sharedPreferences.getString(movieId + OVERVIEW_KEY, "");
            movieThumbnailRelativeUrl = sharedPreferences.getString(movieId, "");
        }

        ((TextView) view.findViewById(R.id.details_title_text_view)).setText(movieTitle);
        ((TextView) view.findViewById(R.id.details_year_text_view)).setText(movieReleaseDate.substring(0, 4));
        ((TextView) view.findViewById(R.id.details_rating_text_view)).setText(movieRating + "/10");
        ((TextView) view.findViewById(R.id.details_overview_text_view)).setText(movieOverview);
        Picasso.with(getContext()).load(Constants.IMAGE_BASE_URL + movieThumbnailRelativeUrl).into(thumbnail);


        if (sharedPreferences.contains(movieId)) {
            markFavButton.setText(R.string.remove_from_fav);
            favTagTextView.setVisibility(View.VISIBLE);
        }
        final String finalMovieThumbnailRelativeUrl = movieThumbnailRelativeUrl;
        final String finalMovieTitle = movieTitle;
        final String finalMovieReleaseDate = movieReleaseDate;
        final String finalMovieRating = movieRating;
        final String finalMovieOverview = movieOverview;

        markFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (sharedPreferences.contains(movieId)) {
                    sharedPreferences.edit().remove(movieId).commit();
                    sharedPreferences.edit().remove(movieId + TITLE_KEY).commit();
                    sharedPreferences.edit().remove(movieId + DATE_KEY).commit();
                    sharedPreferences.edit().remove(movieId + RATING_KEY).commit();
                    sharedPreferences.edit().remove(movieId + OVERVIEW_KEY).commit();
                    markFavButton.setText(R.string.mark_as_fav);
                    favTagTextView.setVisibility(View.GONE);
                } else {
                    sharedPreferences.edit().putString(movieId + TITLE_KEY, finalMovieTitle).commit();
                    sharedPreferences.edit().putString(movieId + DATE_KEY, finalMovieReleaseDate).commit();
                    sharedPreferences.edit().putString(movieId + RATING_KEY, finalMovieRating).commit();
                    sharedPreferences.edit().putString(movieId + OVERVIEW_KEY, finalMovieOverview).commit();
                    sharedPreferences.edit().putString(movieId, finalMovieThumbnailRelativeUrl).commit();
                    markFavButton.setText(R.string.remove_from_fav);
                    favTagTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        loadReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReviews(movieId);
            }
        });
        return view;
    }

    public void requestVideos(String movieId) {
        String url = Constants.API_BASE_URL + movieId + Constants.MOVIE_VIDEOS_URL + Constants.API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray results = response.optJSONArray("results");
                int count = results.length();
                for (int i = 0; i < count; i++) {
                    JSONObject result = results.optJSONObject(i);
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.video_item, null);
                    TextView videoTitletextView = (TextView) view.findViewById(R.id.video_title);
                    videoTitletextView.setText(result.optString(Constants.VIDEOS_TITLE_JSON_NAME));
                    final String youtubeVideoId = result.optString(Constants.VIDEOS_YOUTUBE_ID_JSON_NAME);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_BASE_URL + youtubeVideoId));
                            startActivity(intent);
                        }
                    });
                    videosContainerLinearLayout.addView(view);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void requestReviews(String movieId) {
        loadReviewsButton.setEnabled(false);
        String url = Constants.API_BASE_URL + movieId + Constants.MOVIE_REVIEWS_URL + Constants.API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadReviewsButton.setVisibility(View.GONE);
                JSONArray results = response.optJSONArray("results");
                int count = results.length();
                StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i < count; i++) {
                    JSONObject result = results.optJSONObject(i);
                    String author = result.optString(Constants.REVIEWS_AUTHOR_JSON_NAME);
                    String review = result.optString(Constants.REVIEWS_CONTENT_JSON_NAME);
                    stringBuilder.append(author).append(":\n\t").append(review).append("\n\n");
                }
                if (count == 0) {
                    reviewsTextView.setText(R.string.no_review_found);
                } else {
                    reviewsTextView.setText(stringBuilder.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadReviewsButton.setText(R.string.retry);
                loadReviewsButton.setEnabled(true);
                reviewsTextView.setText(R.string.error_loading_reviews);

            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
