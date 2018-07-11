package bokecc.shortvideosdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.encoder.MediaAudioEncoder;
import com.seu.magicfilter.encoder.MediaEncoder;
import com.seu.magicfilter.encoder.MediaMuxerWrapper;
import com.seu.magicfilter.encoder.MediaVideoEncoder;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.MagicCameraView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import bokecc.shortvideosdk.cutvideo.VideoCutActivity;
import bokecc.shortvideosdk.merge.Mp4ParserMerger;
import bokecc.shortvideosdk.model.MediaObject;
import bokecc.shortvideosdk.widget.ProgressView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView flashControlView, switchCameraView, filterView;
    ImageView closeView;
    ImageView captureView, finishCaptureView, deleteLastVideoView;
    MagicCameraView cameraView;
    ProgressView progressView;
    String TAG = "MainActivity";

    //TODO 配置摄像参数
    final int CAMERA_WIDTH = 480;
    final int CAMERA_HEIGHT = 640;

    //最大录制时长
    int maxRecordDuration = 30 * 1000;

    String suffix = "cccc.mp4";

    String pathEx = Environment.getExternalStorageDirectory() + "/CCDownload/";
    String outName;

    //权限管理
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreen();
        setContentView(R.layout.activity_main);

        cameraView = findById(R.id.magic_camera_view);

        cameraView.setCamraParams(CAMERA_WIDTH, CAMERA_HEIGHT);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE);
        } else {
            cameraView.setVisibility(View.VISIBLE);
        }

        initMagicEngine();
        initView();
        resetViewStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(getApplicationContext(),"授权成功",Toast.LENGTH_SHORT).show();
            cameraView.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getApplicationContext(),"授权拒绝",Toast.LENGTH_SHORT).show();
            cameraView.setVisibility(View.GONE);
        }
    }

    //初始化屏幕设置
    public void initScreen(){
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getSupportActionBar().hide();
    }

    private void initView() {
        captureView = findById(R.id.capture_video);
        captureView.setOnClickListener(this);

        flashControlView = findById(R.id.flash_light);
        flashControlView.setOnClickListener(this);

        switchCameraView = findById(R.id.switch_camera);
        switchCameraView.setOnClickListener(this);

        filterView = findById(R.id.camera_filter);
        filterView.setOnClickListener(this);

        finishCaptureView = findById(R.id.finish_capture);
        finishCaptureView.setOnClickListener(this);

        closeView = findById(R.id.close_capture_all);
        closeView.setOnClickListener(this);

        deleteLastVideoView = findById(R.id.delete_last);
        deleteLastVideoView.setOnClickListener(this);

        progressView = findById(R.id.progress_view);
        progressView.setData(mediaObject);
        progressView.setMaxDuration(maxRecordDuration);
    }

    Timer timer = new Timer();

    TimerTask captureTimerTask;

    //更新录制进度
    private void startCaptureTimer() {
        stopCaptureTimer();

        captureTimerTask = new TimerTask() {
            @Override
            public void run() {

                mediaObject.getCurrentPart().addDuration(50);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mediaObject.getDuration() >= maxRecordDuration) {
                            finishCapture();
                            stopCaptureTimer();
                        }
                    }
                });

            }
        };

        timer.schedule(captureTimerTask, 0, 50);
    }

    private void stopCaptureTimer() {
        if (captureTimerTask != null) {
            captureTimerTask.cancel();
        }
    }

    private void resetViewStatus() {
        captureView.setImageResource(R.drawable.start_capture);

        filterView.setVisibility(View.VISIBLE);
        stopCaptureTimer();
        progressView.stop();

        resetFinishCaptureView();
    }

    private void showCaptureViewStatus() {
        finishCaptureView.setVisibility(View.VISIBLE);
        captureView.setImageResource(R.drawable.stop_capture);
        filterView.setVisibility(View.GONE);
        startCaptureTimer();
        progressView.start();
    }

    private void resetStopStatus() {
        isAudioStopped = false;
        isVideoStopped = false;
        isFinishCapture = false;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.delete_last) {
            deleteLastVideo();
            return;
        } else {
            resetLastVideoStatus();
        }

        switch (v.getId()) {
            case R.id.capture_video:
                switchRecordStatus();
                break;
            case R.id.finish_capture:
                finishCapture();
                break;
            case R.id.flash_light:
                switchFlash();
                break;
            case R.id.switch_camera:
                switchCamera();
                break;
            case R.id.close_capture_all:
                closeAll();
                break;
            case R.id.camera_filter:
                switchFilterStatus();
                break;
        }
    }

    private void deleteLastVideo() {

        if (mediaObject.getMedaParts().size() < 1) {
            toastSomeThing("请先录制一段视频");
            return;
        }

        if (mMuxer != null) {
            toastSomeThing("录制中，请停止录制后进行删除操作");
            return;
        }

        MediaObject.MediaPart currentPart = mediaObject.getCurrentPart();
        if (currentPart.remove) {
            mediaObject.removePart(currentPart, true);
            resetFinishCaptureView();

        } else {
            currentPart.remove = true;
        }
    }

    private void resetFinishCaptureView() {
        if (mediaObject.getMedaParts().size() > 0) {
            finishCaptureView.setVisibility(View.VISIBLE);
        } else {
            finishCaptureView.setVisibility(View.GONE);
        }
    }

    private void resetLastVideoStatus() {
        MediaObject.MediaPart currentPart = mediaObject.getCurrentPart();
        if (currentPart != null) {
            currentPart.remove = false;
        }
    }

    private void toastSomeThing(String thing) {
        Toast.makeText(this, thing, Toast.LENGTH_SHORT).show();
    }

    private void mp4ParserMerge(){
        Mp4ParserMerger.merge(mediaObject.getMedaParts(), outName, mergeListener);
    }

    Mp4ParserMerger.MergeListener mergeListener = new Mp4ParserMerger.MergeListener() {
        @Override
        public void mergeFinish() {

            mediaObject.clearAll(true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPreviewActivity();
                }
            });
        }

        @Override
        public void mergeFail(Exception e) {
            mediaObject.clearAll(true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "合并失败，请重新录制", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    boolean isFinishCapture = false;
    private void finishCapture() {
        isFinishCapture = true;
        Toast.makeText(this, "合并中，请稍候...", Toast.LENGTH_SHORT).show();

        if (mMuxer != null) {
            stopRecording();
        } else {
            mergeVideo();
        }
    }

    private void startPreviewActivity() {
        flashControlView.setImageResource(R.drawable.light_ic_off);
//        Intent intent = new Intent(MainActivity.this, CapturePreviewActivity.class);
//        intent.putExtra("video_path", outName);
        Intent intent = new Intent(MainActivity.this, VideoCutActivity.class);
        intent.putExtra("video_path", outName);
        startActivity(intent);
    }

    private void switchFlash() {
        switch (magicEngine.switchFlash()) {
            case CameraEngine.FLASH_ON:
                flashControlView.setImageResource(R.drawable.light_ic_on);
                break;
            case CameraEngine.FLASH_OFF:
                flashControlView.setImageResource(R.drawable.light_ic_off);
                break;
            default:
                break;
        }
    }

    boolean isOpen = false;
    private void switchFilterStatus() {
        if (isOpen) {
            filterView.setImageResource(R.drawable.filter_close);
            magicEngine.setFilter(MagicFilterType.NONE);
            isOpen = false;
        } else {
            filterView.setImageResource(R.drawable.filter_open);
            magicEngine.setFilter(MagicFilterType.BEAUTY);
            isOpen = true;
        }
    }

    private void switchCamera() {
        magicEngine.switchCamera();
        flashControlView.setImageResource(R.drawable.light_ic_off);
    }

    private void closeAll() {
        stopRecording();
        mediaObject.clearAll(false);
        finish();
    }

    private void switchRecordStatus() {

        if (mMuxer != null) {
            stopRecording();
            resetViewStatus();
        } else {
            startRecording();
            showCaptureViewStatus();
            resetStopStatus();
        }

    }

    private <E extends View> E findById(int resId) {
        return (E) findViewById(resId);
    }


    @Override
    protected void onPause() {
        super.onPause();
        resetViewStatus();
    }

    //=========================camera filter===========================================

    private MagicEngine magicEngine;

    private void initMagicEngine() {
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build((MagicCameraView)findViewById(R.id.magic_camera_view));
    }

    private MediaMuxerWrapper mMuxer;

    String recordVideoPath;

    MediaObject mediaObject = new MediaObject();
    private void startRecording() {
        recordVideoPath = pathEx + getDateTimeString() + ".mp4";

        File file = new File(pathEx);
        if (!file.exists()) {
            file.mkdirs();
        }

        mediaObject.buildMediaPart(recordVideoPath);

        try {
            mMuxer = new MediaMuxerWrapper(recordVideoPath);
            if (true) {
                //TODO 配置录制文件的大小,此处录制大小需要和camera预览分辨率一致
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, CAMERA_WIDTH, CAMERA_HEIGHT);
            }

            if (true) {
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.i(TAG, "startCapture:", e);
        }
    }

    private static final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        return mDateTimeFormat.format(now.getTime());
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        Log.i(TAG, "stopRecording:mMuxer=" + mMuxer);
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }

    boolean isAudioStopped;
    boolean isVideoStopped;

    /**
     * 编码器的回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            Log.i(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                cameraView.setVideoEncoder((MediaVideoEncoder)encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            Log.i(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder) {
                cameraView.setVideoEncoder(null);
                isVideoStopped = true;
            } else if (encoder instanceof MediaAudioEncoder){
                isAudioStopped = true;
            }

            //只有video和audio都结束，才表示视频录制完成
            if (isFinishCapture && isAudioStopped && isVideoStopped) {
                mergeVideo();
            }
        }
    };

    private void mergeVideo() {
        if (mediaObject.getMedaParts().size() > 1) {
            outName = pathEx + suffix;
            mp4ParserMerge();
        } else {
            outName = mediaObject.getMedaParts().get(0).mediaPath;
            mediaObject.clearAll(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPreviewActivity();
                }
            });
        }
    }

}