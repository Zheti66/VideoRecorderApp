package com.example.videorecorderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private ActivityResultLauncher<Intent> videoCaptureLauncher;
    private Uri currentVideoUri;

    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        Button btnRecord = findViewById(R.id.btnRecordVideo);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);

        videoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri videoUri = result.getData().getData();
                        if (videoUri != null) {
                            currentVideoUri = videoUri;
                            videoView.setVideoURI(videoUri);
                            videoView.start();
                            Toast.makeText(this, "Видео записано и воспроизводится",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Запись отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnRecord.setOnClickListener(v -> dispatchTakeVideoIntent());

        btnPlay.setOnClickListener(v -> {
            if (currentVideoUri == null) {
                Toast.makeText(this, "Сначала запишите видео",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!videoView.isPlaying()) {
                if (videoView.getCurrentPosition() == 0) {
                    videoView.setVideoURI(currentVideoUri);
                }
                videoView.start();
                Toast.makeText(this, "Воспроизведение", Toast.LENGTH_SHORT).show();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                Toast.makeText(this, "Пауза", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(v -> {
            videoView.stopPlayback();
            Toast.makeText(this, "Остановлено", Toast.LENGTH_SHORT).show();
        });
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            videoCaptureLauncher.launch(takeVideoIntent);
        } else {
            Toast.makeText(this, "На устройстве нет приложения камеры",
                    Toast.LENGTH_SHORT).show();
        }
    }
}