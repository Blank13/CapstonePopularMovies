package com.mes.udacity.capstonepopularmovies.moviepostersactivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mes.udacity.capstonepopularmovies.database.MovieContract;
import com.mes.udacity.capstonepopularmovies.database.MovieDBHelper;
import com.mes.udacity.capstonepopularmovies.models.Movie;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.response.MovieResponse;
import com.mes.udacity.capstonepopularmovies.utils.Constants;
import com.mes.udacity.capstonepopularmovies.utils.ListItemClickListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mes.udacity.capstonepopularmovies.utils.StaticMethods.getBodyString;
import static com.mes.udacity.capstonepopularmovies.utils.StaticMethods.haveNetworkConnection;

/**
 * Created by moham on 2/18/2018.
 */

public class MoviePostersFragment extends Fragment implements MoviePostersListListener, ListItemClickListener {

    private static final String TAG = MoviePostersFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MoviePostersGridRecyclerAdapter posterGridRecyclerAdapter;
    private int pages = 1;
    private String sortType = "popular";
    private boolean firstTime = true;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(String movieStr);
        void onChangeSort();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(haveNetworkConnection(getActivity())){
            if(sortType != "show"){
                updateMovies(sortType);
            }
            else{
                if(posterGridRecyclerAdapter != null &&
                        posterGridRecyclerAdapter.getItemCount() != getDatabaseCount()){
                    getStoredMovies();
                }
            }
        }
        else {
            if(firstTime){
                firstTime = false;
                Toast.makeText(getActivity(),"No Internet Connection Opening the Favourites",
                        Toast.LENGTH_SHORT).show();
            }
            sortType = "show";
            getStoredMovies();
        }
    }

    private int getDatabaseCount() {
        MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        Cursor cursor = db.query(MovieContract.MovieEntry.MOVIES_TABLE,
                null, null, null, null, null, null);
        int i= cursor.getCount();
        cursor.close();
        return i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_posters,container,false);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView = view.findViewById(R.id.posters_gridview);
        if(posterGridRecyclerAdapter == null){
            posterGridRecyclerAdapter = new MoviePostersGridRecyclerAdapter(new ArrayList<Movie>(),this);
        }
        if (posterGridRecyclerAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }
        int numberOfRows = getResources().getInteger(R.integer.col_num);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfRows);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(posterGridRecyclerAdapter);
        return view;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = posterGridRecyclerAdapter.getItem(clickedItemIndex);
        Gson gson = new Gson();
        String movieStr = gson.toJson(movie);
        ((Callback)getActivity()).onItemSelected(movieStr);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.posters_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_popular_sort:
                sortType = "popular";
                progressBar.setVisibility(View.VISIBLE);
                posterGridRecyclerAdapter = new MoviePostersGridRecyclerAdapter(new ArrayList<Movie>(), this);
                recyclerView.setAdapter(posterGridRecyclerAdapter);
                pages = 1;
                ((Callback)getActivity()).onChangeSort();
                updateMovies(sortType);
                return true;
            case R.id.action_rating_sort:
                sortType = "top_rated";
                progressBar.setVisibility(View.VISIBLE);
                posterGridRecyclerAdapter = new MoviePostersGridRecyclerAdapter(new ArrayList<Movie>(), this);
                recyclerView.setAdapter(posterGridRecyclerAdapter);
                pages = 1;
                ((Callback)getActivity()).onChangeSort();
                updateMovies(sortType);
                return true;
            case R.id.action_show_favourite:
                sortType = "show";
                progressBar.setVisibility(View.VISIBLE);
                posterGridRecyclerAdapter = new MoviePostersGridRecyclerAdapter(new ArrayList<Movie>(), this);
                recyclerView.setAdapter(posterGridRecyclerAdapter);
                ((Callback)getActivity()).onChangeSort();
                getStoredMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPosterListReady(List<Movie> movieList) {
        progressBar.setVisibility(View.GONE);
        if(movieList == null){
            Toast.makeText(getActivity(),"No Internet connection/nLoading the Favourites"
                    , Toast.LENGTH_SHORT).show();
            sortType = "show";
            getStoredMovies();
        }
        else if(sortType.equals("show")){
            posterGridRecyclerAdapter.updatePosters(movieList);
        }
        else if (pages <= 15 && !sortType.equals("show")) {
            posterGridRecyclerAdapter.updatePosters(movieList);
            updateMovies(sortType);
        }
    }

    @Override
    public void onPosterError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),message , Toast.LENGTH_LONG).show();
    }

    private void updateMovies(String sort){
        FetchMoviesData fetchMoviesData = new FetchMoviesData();
        if(sort == null){
            sort = "popular";
        }
        if(pages < 15){
            fetchMoviesData.execute(sort);
        }
    }

    private class FetchMoviesData extends AsyncTask<String, Void, List<Movie>> {

        private String currentSort;

        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            MovieResponse movieResponse;
            currentSort = params[0];

            final String PAGE_NUM = "page";
            final String APPID_PARAM = "api_key";

            Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter(PAGE_NUM,Integer.toString(pages))
                    .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Gson gson = new Gson();
                movieResponse = gson.fromJson(getBodyString(urlConnection.getInputStream())
                        ,MovieResponse.class);
                return movieResponse.getMovies();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(currentSort.equalsIgnoreCase(sortType)){
                onPosterListReady(movies);
                pages++;
            }
        }
    }

    public void onFavouriteChange(){
        if(sortType.equalsIgnoreCase("show")){
            getStoredMovies();
            ((Callback)getActivity()).onChangeSort();
        }
    }

    private void getStoredMovies(){
        FetchStoredMoviesData fetchStoredMoviesData = new FetchStoredMoviesData();
        fetchStoredMoviesData.execute();
    }

    private class FetchStoredMoviesData extends AsyncTask<Void, Void, List<Movie>>{

        @Override
        protected List<Movie> doInBackground(Void... params) {
            List<Movie> movies = new ArrayList<>();
            Cursor cursor = getContext().getContentResolver()
                    .query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                            null, null, null, null);
            while (cursor.moveToNext()){
                Movie movie = new Movie();
                movie.setId(Long.parseLong(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry._ID))));
                movie.setTitle(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)));
                movie.setPosterPath(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)));
                movie.setReleaseDate(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE))));
                movie.setOverView(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_OVER_VIEW)));
                movies.add(movie);
            }
            cursor.close();
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            posterGridRecyclerAdapter.clear();
            if(movies != null){
                if(movies.size() == 0){
                    Toast.makeText(getActivity(),"There is no Favourites",Toast.LENGTH_LONG).show();
                }
                onPosterListReady(movies);
            }
        }
    }
}
