package bokecc.shortvideosdk;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CapturePreviewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, View.OnClickListener {

    String videoPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initScreen();
        setContentView(R.layout.activity_capture_preview);

        Intent intent = getIntent();
        videoPath = intent.getStringExtra("video_path");

        initView();

    }

    //初始化屏幕设置
    public void initScreen(){
        getSupportActionBar().hide();
    }

    TextureView videoView;
    TextView videoPathView, startUploadView;
    ImageView closeView;
    private void initView() {
        videoView = findById(R.id.video_preview);
        videoView.setSurfaceTextureListener(this);

        videoPathView = findById(R.id.preview_video_path);
        videoPathView.setText(videoPath);

        closeView = findById(R.id.close_preview_all);
        closeView.setOnClickListener(this);

        startUploadView = findById(R.id.preview_upload);
        startUploadView.setOnClickListener(this);

    }

    private <E extends View> E findById(int resId) {
        return (E)findViewById(resId);
    }

    MediaPlayer player;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        if (videoPath == null) {
            return;
        }

        player = new MediaPlayer();
        player.setLooping(true);
        player.setOnPreparedListener(this);
        player.setSurface(new Surface(surfaceTexture));
        try {
            player.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.prepareAsync();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isPrepared = false;
        player.stop();
        player.release();
        player = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && isPrepared) {
            player.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    private boolean isPrepared = false;
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (player != null) {
            player.start();
            isPrepared = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_preview_all:
                finish();
                break;
            case R.id.preview_upload:

                // TODO，暂时未作，可以和cc play demo结合来使用
                Toast.makeText(this, "开始上传 TODO", Toast.LENGTH_SHORT).show();
                muxMp4();
                break;
        }
    }

    private void muxMp4() {
    }
}
