package com.example.fabio.mobileappscoursework;

import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlayMusicActivity extends AppCompatActivity {
    private ImageButton playBtn, nextBtn, previousBtn;
    private Button repeatBtn, shuffleBtn;
    private int numberOfSongs, songIndex, sound_id, repeatMode = 1;
    private final int TENSECONDS = 10000;
    private Boolean paused = false, shuffle = false;
    private SeekBar seekBar;
    private HashMap<Integer, String> hashedSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        InitialiseVariables();
        Play();
        AddActionListeners();
    }

    // set the variables
    public void InitialiseVariables(){
        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        seekBar = findViewById(R.id.seekBar);
        shuffleBtn = findViewById(R.id.ShuffleBtn);
        repeatBtn = findViewById(R.id.repeat);
        songIndex = getIntent().getIntExtra("Song Index", 0);
        hashedSongs = (HashMap<Integer, String>) getIntent().getSerializableExtra("songsHashMap");
        numberOfSongs = hashedSongs.size()-1;
    }


    public HashMap<Integer, String> Shuffle() {
        if (shuffle) {
            HashMap<Integer, String> ShuffledList = new HashMap<>();
            int index = 0;
            ShuffledList.put(index, hashedSongs.get(songIndex));
            index++;
            hashedSongs.remove(songIndex);
            ArrayList SongIndexes = new ArrayList(hashedSongs.keySet());
            Collections.shuffle(SongIndexes);
            for (Object i : SongIndexes) {
                ShuffledList.put(index, hashedSongs.get(i));
                index++;
            }
            hashedSongs = ShuffledList;
            shuffleBtn.setTextColor(ContextCompat.getColor(this, R.color.red));
            shuffleBtn.setText(getResources().getString(R.string.shuffleOn));
        } else {
            shuffleBtn.setTextColor(ContextCompat.getColor(this, R.color.Gray));
            hashedSongs = (HashMap<Integer, String>) getIntent().getSerializableExtra("songsHashMap");
            shuffleBtn.setText(getResources().getString(R.string.shuffleOff));
        }
        return hashedSongs;
    }
    public void Repeat(){
        if(repeatMode == 1){ // REPEAT OFF VALUE
            repeatBtn.setText(getResources().getString(R.string.repeatOff));
            repeatBtn.setTextColor(ContextCompat.getColor(this,R.color.Gray));

        }else if(repeatMode == 2) { // SINGLE SONG REPEAT VALUE
            repeatBtn.setTextColor(ContextCompat.getColor(this,R.color.red));
            repeatBtn.setText(getResources().getString(R.string.repeatSong));

        }else if(repeatMode == 3){ // ALL SONGS REPEAT VALUE
            repeatBtn.setText(getResources().getString(R.string.repeatAll));
        }
    }
    public void Seek(){
        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setMax(MainActivity.mediaPlayer.getDuration());
                        if(MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()){
                            seekBar.setProgress(MainActivity.mediaPlayer.getCurrentPosition());
                            seekBarProgress.setText(getTimeString(MainActivity.mediaPlayer.getCurrentPosition()));
                        }
                    }
                });
            }
        },0,200);
    }
    public void Play() {
        TextView titleText = findViewById(R.id.titleText);
        TextView artistText = findViewById(R.id.artistText);
        TextView seekBarDuration = findViewById(R.id.seekBarDuration);

        if(MainActivity.mediaPlayer != null){
            MainActivity.mediaPlayer.release();
        }
        sound_id = getApplicationContext().getResources().getIdentifier(hashedSongs.get(songIndex), "raw",
                getApplicationContext().getPackageName());
        MainActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), sound_id);
        seekBarDuration.setText(getTimeString(MainActivity.mediaPlayer.getDuration()));
        if(!paused) {
            MainActivity.mediaPlayer.start();
        }
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + sound_id);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, mediaPath);
        String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        titleText.setText(songTitle);
        artistText.setText("Artist: " + songArtist);
        Seek();
    }
    public void NextSong() {
        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer.release();
        if (repeatMode == 3 && songIndex == numberOfSongs) {
            songIndex = 0;
        } else if (repeatMode == 3 || repeatMode == 1) {
            songIndex++;
        }
          Play();
    }
    public void PreviousSong() {
        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer.release();
        if (repeatMode == 3 && songIndex == numberOfSongs) {
            songIndex = 0;
        } else if (repeatMode == 3 || repeatMode == 1) {
            songIndex--;
        }
        Play();
    }
    public void PlayPauseMethod() {
        if (MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
            playBtn.setBackgroundResource(R.drawable.ic_play);
            paused = true;
        } else {
            MainActivity.mediaPlayer.start();
            playBtn.setBackgroundResource(R.drawable.ic_pause);
            paused = false;
        }
    }
    public String getTimeString(long millis){
        String progress;
        int minutes = (int) (millis / (60 * 1000));
        int seconds = (int) (millis / 1000) % 60;
        progress = String.format("%d:%02d",minutes,seconds);
        return progress;
    }
    public void AddActionListeners() {
        MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                    if (repeatMode == 1 && songIndex == numberOfSongs) {
                    } else {
                        NextSong();
                    }
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayPauseMethod();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatMode == 1 && songIndex == numberOfSongs) {
                }
                else {
                    NextSong();
                }
            }
        });
        nextBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(MainActivity.mediaPlayer.getCurrentPosition()+TENSECONDS != MainActivity.mediaPlayer.getDuration()) {
                    MainActivity.mediaPlayer.seekTo(MainActivity.mediaPlayer.getCurrentPosition() + TENSECONDS);
                }
                return true;
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatMode == 1 && songIndex == 0) {
                }
                else {
                    PreviousSong();
                }
            }
        });
        previousBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(MainActivity.mediaPlayer.getCurrentPosition()-TENSECONDS != 0) {
                    MainActivity.mediaPlayer.seekTo(MainActivity.mediaPlayer.getCurrentPosition()-TENSECONDS);
                }
                return true;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                        MainActivity.mediaPlayer.seekTo(i-1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle =! shuffle;
                hashedSongs = Shuffle();
                songIndex = 0;

                }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatMode == 1){
                    repeatMode = 2;
                }
                else if(repeatMode == 2){
                    repeatMode = 3;
                }else{
                    repeatMode = 1;
                }
                Repeat();
            }
        });
    }
}