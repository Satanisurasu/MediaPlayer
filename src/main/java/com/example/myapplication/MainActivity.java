package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int[] tracks = {R.raw.track_one, R.raw.track_two, R.raw.track_three}; // Убедись, что треки находятся в res/raw
    private int currentTrackIndex = 0;

    private Button btnPlay, btnPrev, btnNext;
    private TextView tvTrackName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvTrackName = findViewById(R.id.tvTrackName);

        // Устанавливаем начальное название трека
        tvTrackName.setText("Track: " + (currentTrackIndex + 1));

        btnPlay.setOnClickListener(v -> togglePlayPause());
        btnPrev.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            playTrack();
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setText("Play");
        } else {
            mediaPlayer.start();
            btnPlay.setText("Pause");
        }
    }

    private void playTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex]);
        mediaPlayer.setOnCompletionListener(mp -> playNextTrack());
        mediaPlayer.start();
        tvTrackName.setText("Playing Track: " + (currentTrackIndex + 1));
        btnPlay.setText("Pause");
    }

    private void playPreviousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + tracks.length) % tracks.length;
        playTrack();
    }

    private void playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % tracks.length;
        playTrack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
