package com.example.myapplication;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {

    private ImageView imageView;
    private Button btnPrev, btnNext;
    private int[] images = {R.drawable.image_one, R.drawable.image_two,R.drawable.image_three}; // Убедись, что изображения находятся в res/drawable
    private int currentIndex = 0;

    private Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = view.findViewById(R.id.imageView);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);

        btnPrev.setOnClickListener(v -> showPreviousImage());
        btnNext.setOnClickListener(v -> showNextImage());

        startAutoSwitchingImages();
        return view;
    }

    private void showPreviousImage() {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        updateImage();
    }

    private void showNextImage() {
        currentIndex = (currentIndex + 1) % images.length;
        updateImage();
    }

    private void updateImage() {
        imageView.setImageResource(images[currentIndex]);
    }

    private void startAutoSwitchingImages() {
        runnable = new Runnable() {
            @Override
            public void run() {
                showNextImage();
                handler.postDelayed(this, 30000); // Меняет изображение каждые 3 секунды
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSwitchingImages();
    }
}

