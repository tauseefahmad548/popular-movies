package pk.smallapps.popularmovies.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.MovieDbOpenHelper;
import pk.smallapps.popularmovies.MoviesDbContract;
import pk.smallapps.popularmovies.R;
import pk.smallapps.popularmovies.adapter.MovieListRecyclerViewAdapter;

/**
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieListFragment extends Fragment {

    private SQLiteDatabase movieDatabase;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    private View view;

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        MovieDbOpenHelper movieDbOpenHelper = new MovieDbOpenHelper(getContext());
        movieDatabase = movieDbOpenHelper.getWritableDatabase();

        int mColumnCount = 2;
        Context context = view.getContext();
        recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        requestQueue = Volley.newRequestQueue(getContext());
        String url = Constants.POPULAR_MOVIES_URL + Constants.API_KEY;
        if (savedInstanceState == null) {
            requestMovies(url);
        }else
        {
            Cursor cursor = movieDatabase.query(MoviesDbContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
            recyclerView.setAdapter(new MovieListRecyclerViewAdapter(cursor, mListener));
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * Reference:
     * Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a>.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String movieId);
    }

    public void requestMovies(final String url) {

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                movieDatabase.delete(MoviesDbContract.MovieEntry.TABLE_NAME, null, null);
                JSONArray results = response.optJSONArray("results");
                int resultCount = results.length();

                for (int i = 0; i < resultCount; i++) {

                    JSONObject result = results.optJSONObject(i);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_ID, result.optString(Constants.MOVIE_ID_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_TITLE, result.optString(Constants.MOVIE_TITLE_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, result.optString(Constants.MOVIE_RELEASE_DATE_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, result.optString(Constants.MOVIE_OVERVIEW_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_POSTER, result.optString(Constants.MOVIE_POSTER_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_USER_RATING, result.optString(Constants.MOVIE_USER_RATING_JSON_NAME));
                    contentValues.put(MoviesDbContract.MovieEntry.COLUMN_MOVIE_POPULARITY, result.optString(Constants.MOVIE_POPULARITY_JSON_NAME));

                    movieDatabase.insert(MoviesDbContract.MovieEntry.TABLE_NAME, null, contentValues);
                }

                Cursor cursor = movieDatabase.query(MoviesDbContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
                recyclerView.setAdapter(new MovieListRecyclerViewAdapter(cursor, mListener));
                recyclerView.getAdapter().notifyDataSetChanged();
                Toast.makeText(getContext(), R.string.list_updated, Toast.LENGTH_SHORT).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, R.string.netwrok_error, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestMovies(url);
                    }
                }).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

}
