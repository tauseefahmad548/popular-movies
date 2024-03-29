package pk.smallapps.popularmovies.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.R;
import pk.smallapps.popularmovies.fragment.MovieListFragment;


public class FavMoviesRecyclerViewAdapter extends RecyclerView.Adapter<FavMoviesRecyclerViewAdapter.ViewHolder> {
    HashMap<String, String> hashMap;
//    String[] movieIdsArray;
    List<String> lst;
    private final MovieListFragment.OnListFragmentInteractionListener mListener;

    public FavMoviesRecyclerViewAdapter(MovieListFragment.OnListFragmentInteractionListener listener) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) listener);
        hashMap = (HashMap<String, String>) sharedPreferences.getAll();
        Set<String> keySet = hashMap.keySet();
        String [] tempArray = new String[hashMap.size()];
        keySet.toArray(tempArray);

        Pattern p = Pattern.compile("\\d*");
        lst= new ArrayList<>();
        for (int i=0; i<tempArray.length; i++) {
            if(p.matcher(tempArray[i]).matches()){
                lst.add(tempArray[i]);
            }
        }
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
//        String movieId = movieIdsArray[position];
        String movieId = lst.get(position);
        holder.movieId = movieId;

        Picasso.with((Context) mListener).load(Constants.IMAGE_BASE_URL + hashMap.get(movieId)).into(holder.posterImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.movieId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return hashMap.size();
        return lst.size();
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

