package com.bulbulproject.bulbul.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.android.ApolloCall;
import com.apollographql.android.api.graphql.Response;
import com.bulbulproject.TrackQuery;
import com.bulbulproject.bulbul.App;
import com.bulbulproject.bulbul.R;
import com.bulbulproject.bulbul.model.MySong;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class AccuracyTest extends AppCompatActivity {

    int currentOrder = 0;
    int songsSize = 10;

    TextView textViewSongCounter;
    TextView textViewArtistName;
    TextView textViewAlbumName;
    TextView textViewSongName;
    ImageView imageViewAlbumImage;

    ArrayList<MySong> mSongs;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accuracy_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressView = findViewById(R.id.login_progress);
        mProgressView.setVisibility(View.VISIBLE);

        mSongs = new ArrayList<>();

        textViewArtistName = (TextView) findViewById(R.id.artist_name);
        textViewAlbumName = (TextView) findViewById(R.id.album_name);
        textViewSongName = (TextView) findViewById(R.id.song_name);
        textViewSongCounter = (TextView) findViewById(R.id.text_song_counter);
        imageViewAlbumImage = (ImageView) findViewById(R.id.album_img);

        ((App) getApplication()).apolloClient().newCall(
                TrackQuery.builder()
                        .limit(songsSize)
                        .skip(30)
                        .build())
                .enqueue(
                        new ApolloCall.Callback<TrackQuery.Data>() {
                            @Override
                            public void onResponse(@Nonnull Response<TrackQuery.Data> response) {
                                if (response.data() != null) {
                                    List<TrackQuery.Data.Track> trackList = response.data().tracks();
                                    for (TrackQuery.Data.Track track : trackList) {
                                        //Mapping api's track model to existing Song model
                                        MySong mSong = new MySong(track.id(),
                                                track.name(),
                                                "Album",
                                                (track.artists().size() > 0)?track.artists().get(0).name():"Unknown Artist",
//                                                "Artist",
                                                0,
                                                track.spotify_album_img()
                                        );
                                        mSongs.add(mSong);
                                    }
                                    //Update ui for adding new elements to list
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressView.setVisibility(View.GONE);
                                            updateUI();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@Nonnull Throwable t) {
                                final String text = t.getMessage();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
    }
    public void clicked_icon(View v){


        if(v.getId() == R.id.icon_bad || v.getId() == R.id.icon_neutral || v.getId() == R.id.icon_good){
            if(v.getId() == R.id.icon_bad){
                mSongs.get(currentOrder).setTestResult(-1);
            }
            else if(v.getId() == R.id.icon_neutral){
                mSongs.get(currentOrder).setTestResult(0);
            }
            else if(v.getId() == R.id.icon_good){
                mSongs.get(currentOrder).setTestResult(1);
            }
            if(currentOrder == songsSize -1) {
                Intent intent = new Intent(getApplicationContext(), AccuracyResult.class);
                intent.putExtra("accuracy_score", 5);
                startActivity(intent);
                return;
            }
            currentOrder++;
            updateUI();
        }
    }
    void updateUI(){
        textViewSongCounter.setText("Song: " + (currentOrder+1) + "/" + songsSize);

        textViewArtistName.setText(mSongs.get(currentOrder).getArtistName());
        textViewAlbumName.setText(mSongs.get(currentOrder).getAlbumName());
        textViewSongName.setText(mSongs.get(currentOrder).getName());


        Picasso.with(getApplicationContext())
                .load(mSongs.get(currentOrder).getImageUrl())
                .placeholder(R.drawable.cover_picture)
                .error(R.drawable.album)
                .into(imageViewAlbumImage);
    }
}