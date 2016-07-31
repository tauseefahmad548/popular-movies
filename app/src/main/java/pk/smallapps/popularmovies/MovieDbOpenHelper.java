package pk.smallapps.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tauseef Ahmad on 7/28/2016.
 */
public class MovieDbOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MoviesDbContract.MovieEntry.TABLE_NAME + " (" +
                    MoviesDbContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_USER_RATING + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_POSTER + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_POPULARITY + TEXT_TYPE +
                    MoviesDbContract.MovieEntry.COLUMN_MOVIE_FAVORITE + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MoviesDbContract.MovieEntry.TABLE_NAME;

    public MovieDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }
}
