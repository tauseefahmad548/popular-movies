package pk.smallapps.popularmovies.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pk.smallapps.popularmovies.Constants;
import pk.smallapps.popularmovies.R;
import pk.smallapps.popularmovies.fragment.MovieDetailsFragment;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);


        if (savedInstanceState == null) {
            Fragment movieDetailsFragment = MovieDetailsFragment.newInstance(getIntent().getStringExtra(Constants.MOVIE_ID_EXTRA));
            getSupportFragmentManager().beginTransaction().add(R.id.details_container,movieDetailsFragment).commit();
        }
    }
}
