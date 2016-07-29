package pk.smallapps.popularmovies;

import android.provider.BaseColumns;

/**
 * Created by Tauseef Ahmad on 7/28/2016.
 */
public final class MoviesDbContract {

        public MoviesDbContract() {}

        public static abstract class MovieEntry implements BaseColumns {
            public static final String TABLE_NAME = "movies";
            public static final String COLUMN_MOVIE_ID = "movieid";
            public static final String COLUMN_MOVIE_TITLE = "title";
            public static final String COLUMN_MOVIE_THUMBNAIL = "thumbnail";
            public static final String COLUMN_MOVIE_POSTER = "poster";
            public static final String COLUMN_MOVIE_OVERVIEW = "overview";
            public static final String COLUMN_MOVIE_RELEASE_DATE = "date";
            public static final String COLUMN_MOVIE_USER_RATING = "rating";
            public static final String COLUMN_MOVIE_POPULARITY = "popularity";
        }

}
