package com.udacity.gradle.builditbigger.paid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.androidlibrary.AndroidJokeActivity;
import com.example.javajokelibrary.JavaJoke;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String JOKE_EXTRA_KEY = "passed_joke";
    String joke;
    private ProgressBar jokeProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
        jokeProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jokeProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        jokeProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        jokeProgressBar.setVisibility(View.VISIBLE);
        new com.udacity.gradle.builditbigger.paid.MainActivity.EndpointsAsyncTask().execute(new Pair<Context, String>(this, new JavaJoke().getBadJokes()));
    }

    public void startActivity(String sentJoke) {
        Intent intent = new Intent(this, AndroidJokeActivity.class);
        intent.putExtra(JOKE_EXTRA_KEY, sentJoke);
        startActivity(intent);
    }

    class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {

        MyApi myApiService = null;
        Context context;

        @Override
        protected String doInBackground(Pair<Context, String>[] pairs) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null).setRootUrl("https://finalproject-189010.appspot.com/_ah/api/");
                myApiService = builder.build();
            }
            context = pairs[0].first;
            try {
                return myApiService.sayHi(new JavaJoke().getBadJokes()).execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            joke = result;
            startActivity(result);
            jokeProgressBar.setVisibility(View.GONE);
        }
    }
}