package com.example.fabio.mobileappscoursework;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> songListArray;
    int songIndex;
    Button shuffleBtn, playBtn;
    HashMap<Integer, String> hashedSongs;
    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shuffleBtn = findViewById(R.id.Shuffle);
        playBtn = findViewById(R.id.Play);
        songListArray = getSongNames(R.raw.class.getFields());
        CreateListView();
        AddActionListener();
    }

    public void CreateListView(){
        listView = findViewById(R.id.list);
        listView.setNestedScrollingEnabled(true);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                songListArray){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);
    }
    public ArrayList<String> getSongNames(Field[] songList){
        songListArray = new ArrayList<>();
        for (int i = 0; i < songList.length; i++) {
            String name = songList[i].getName();
            songListArray.add(name);
        }
        return songListArray;
    }
    public HashMap<Integer, String> ToHashMap(ArrayList<String> songListArray){
        hashedSongs = new HashMap<>();
        int i = 0;
        for(String song: songListArray){
            hashedSongs.put(i, song);
            i++;
        }
        return hashedSongs;
    }
    public void ShowMusicPlayer(){
        Intent intent = new Intent(this, PlayMusicActivity.class);
        intent.putExtra("Song Index", songIndex);
        intent.putExtra("songsHashMap", hashedSongs);
        startActivity(intent);
    }
    public void AddActionListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                hashedSongs = ToHashMap(songListArray);
                songIndex = position;
                ShowMusicPlayer();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashedSongs = ToHashMap(songListArray);
                songIndex = 1;
                ShowMusicPlayer();
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.shuffle(songListArray);
                hashedSongs = ToHashMap(songListArray);
                songIndex = 1;
                ShowMusicPlayer();
            }
        });
    }
}
