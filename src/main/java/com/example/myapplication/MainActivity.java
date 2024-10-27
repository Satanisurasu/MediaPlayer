package com.example.myapplication;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int[] tracks = {R.raw.track_one, R.raw.track_two, R.raw.track_three};  // Список треков
    private int[] images = {R.drawable.background_one, R.drawable.background_two, R.drawable.background_three}; // Список фонов
    private int currentTrackIndex = 0;
    private int currentImageIndex = 0;
    private boolean isPlaying = false;
    private int currentPosition = 0;

    private Button btnPlay, btnPrev, btnNext, btnNextBackground, btnPrevBackground;
    private TextView tvTrackName, tvCurrentTime, tvTotalTime;
    private ImageView backgroundImage;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов управления
        btnPlay = findViewById(R.id.btnPlay);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnNextBackground = findViewById(R.id.btnNextBackground);
        btnPrevBackground = findViewById(R.id.btnPrevBackground);
        tvTrackName = findViewById(R.id.tvTrackName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        backgroundImage = findViewById(R.id.backgroundImage);
        seekBar = findViewById(R.id.seekBar);

        // Установка начального названия трека
        tvTrackName.setText("Track: " + (currentTrackIndex + 1));

        // Установка обработчиков для кнопок
        btnPlay.setOnClickListener(v -> togglePlayPause());
        btnPrev.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
        btnNextBackground.setOnClickListener(v -> showNextBackground());
        btnPrevBackground.setOnClickListener(v -> showPreviousBackground());

        // Настройка SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (savedInstanceState != null) {
            // Восстановление состояния
            currentTrackIndex = savedInstanceState.getInt("trackIndex");
            currentImageIndex = savedInstanceState.getInt("imageIndex");
            currentPosition = savedInstanceState.getInt("currentPosition");
            isPlaying = savedInstanceState.getBoolean("isPlaying");

            // Восстановление фонового изображения
            backgroundImage.setImageResource(images[currentImageIndex]);

            // Воспроизведение трека с сохраненной позиции
            playTrack();
            mediaPlayer.seekTo(currentPosition);

            if (!isPlaying) {
                mediaPlayer.pause();

            }
            updateSeekBar();
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            playTrack();
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            // Убираем изменение текста кнопки
            // btnPlay.setText("Play");
            isPlaying = false;
        } else {
            mediaPlayer.start();
            // Убираем изменение текста кнопки
            // btnPlay.setText("Pause");
            isPlaying = true;
            updateSeekBar();
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
        // Убираем изменение текста кнопки
        // btnPlay.setText("Pause");

        seekBar.setMax(mediaPlayer.getDuration());
        tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
        isPlaying = true;
        updateSeekBar();
    }

    private void playPreviousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + tracks.length) % tracks.length;
        playTrack();
    }

    private void playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % tracks.length;
        playTrack();
    }

    private void showNextBackground() {
        currentImageIndex = (currentImageIndex + 1) % images.length;
        backgroundImage.setImageResource(images[currentImageIndex]);
    }

    private void showPreviousBackground() {
        currentImageIndex = (currentImageIndex - 1 + images.length) % images.length;
        backgroundImage.setImageResource(images[currentImageIndex]);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Сохранение текущих значений
        outState.putInt("trackIndex", currentTrackIndex);
        outState.putInt("imageIndex", currentImageIndex);
        outState.putInt("currentPosition", mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0);
        outState.putBoolean("isPlaying", mediaPlayer != null && mediaPlayer.isPlaying());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null); // Остановка обновления SeekBar
    }
}
