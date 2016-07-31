package pk.smallapps.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.R;
import pk.smallapps.popularmovies.fragment.MovieListFragment;


public class MainActivity extends AppCompatActivity implements MovieListFragment.OnListFragmentInteractionListener {
    MovieListFragment movieListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.movies_list_fragment);
        Spinner spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_order_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        spinner.setOnTouchListener(listener);
        spinner.setOnItemSelectedListener(listener);

    }

    @Override
    public void onListFragmentInteraction(String movieId) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(Constants.MOVIE_ID_EXTRA, movieId);
        startActivity(intent);
    }
//To stop automated call to onItemSelected without click event
    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                switch (position) {
                    case 0:
                        movieListFragment.requestMovies(Constants.POPULAR_MOVIES_URL + Constants.API_KEY);
                        break;
                    case 1:
                        movieListFragment.requestMovies(Constants.TOP_RATED_MOVIES_URL + Constants.API_KEY);
                        break;

                    default:
                }
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }
}
