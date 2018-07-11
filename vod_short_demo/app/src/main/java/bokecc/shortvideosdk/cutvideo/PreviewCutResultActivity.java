package bokecc.shortvideosdk.cutvideo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.VideoView;

import bokecc.shortvideosdk.R;

public class PreviewCutResultActivity extends Activity {
    private VideoView vv_preview;
    private String videoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_cut_result);
        videoPath = getIntent().getStringExtra("videoPath");
        vv_preview = (VideoView) findViewById(R.id.vv_preview);
        vv_preview.setVideoPath(videoPath);
        vv_preview.start();

        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vv_preview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                vv_preview.start();
            }
        });
    }
}
