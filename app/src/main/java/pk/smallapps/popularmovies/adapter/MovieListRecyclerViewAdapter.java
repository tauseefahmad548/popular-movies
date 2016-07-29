package pk.smallapps.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.MoviesDbContract;
import pk.smallapps.popularmovies.R;
import pk.smallapps.popularmovies.fragment.MovieListFragment;


public class MovieListRecyclerViewAdapter extends RecyclerView.Adapter<MovieListRecyclerViewAdapter.ViewHolder> {

    private final Cursor moviesCursor;
    private final MovieListFragment.OnListFragmentInteractionListener mListener;

    public MovieListRecyclerViewAdapter(Cursor movies, MovieListFragment.OnListFragmentInteractionListener listener) {
        moviesCursor = movies;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        moviesCursor.moveToPosition(position);
        holder.movieId = moviesCursor.getString(moviesCursor.getColumnIndex(MoviesDbContract.MovieEntry.COLUMN_MOVIE_ID));
        String posterRelativeUrl = moviesCursor.getString(moviesCursor.getColumnIndex(MoviesDbContract.MovieEntry.COLUMN_MOVIE_POSTER));

        Picasso.with((Context) mListener).load(Constants.IMAGE_BASE_URL + posterRelativeUrl).placeholder(R.mipmap.ic_launcher).into(holder.posterImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.movieId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView posterImageView;
        public String movieId;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            posterImageView = (ImageView) view.findViewById(R.id.movie_poster_image_view);
        }

    }
}
