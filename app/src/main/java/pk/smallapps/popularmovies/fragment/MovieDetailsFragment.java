package pk.smallapps.popularmovies.fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.MovieDbOpenHelper;
import pk.smallapps.popularmovies.MoviesDbContract.MovieEntry;
import pk.smallapps.popularmovies.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {
    public static final String ARG_MOVIE_ID = "movie_id";
    private String movieId;

    public MovieDetailsFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        MovieDbOpenHelper movieDbOpenHelper = new MovieDbOpenHelper(getContext());
        SQLiteDatabase moviesDb = movieDbOpenHelper.getReadableDatabase();
        Cursor cursor = moviesDb.query(MovieEntry.TABLE_NAME, null, MovieEntry.COLUMN_MOVIE_ID + "=" + movieId, null, null, null, null);
        String movieTitle = null;
        String movieReleaseDate = null;
        String movieRating = null;
        String movieOverview = null;
        String movieThumbnailRelativeUrl = null;
        if (cursor.moveToFirst()) {
            movieTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
            movieReleaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
            movieRating = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_USER_RATING));
            movieOverview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_OVERVIEW));
            movieThumbnailRelativeUrl = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER));
        }
        cursor.close();

        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ((TextView) view.findViewById(R.id.details_title_text_view)).setText(movieTitle);
        ((TextView) view.findViewById(R.id.details_year_text_view)).setText(movieReleaseDate.substring(0,4));
        ((TextView) view.findViewById(R.id.details_rating_text_view)).setText(movieRating+"/10");
        ((TextView) view.findViewById(R.id.details_overview_text_view)).setText(movieOverview);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.details_thumbnail_image_view);
        Picasso.with(getContext()).load(Constants.IMAGE_BASE_URL + movieThumbnailRelativeUrl).into(thumbnail);

        return view;
    }

}
