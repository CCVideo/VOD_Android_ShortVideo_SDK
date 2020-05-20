package bokecc.shortvideosdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.camerafilter.camera.engine.CameraEngine;
import com.bokecc.camerafilter.camera.engine.CameraParam;
import com.bokecc.camerafilter.camera.recordervideo.PreviewRecorder;
import com.bokecc.camerafilter.camera.render.PreviewRenderer;
import com.bokecc.camerafilter.camera.widget.AspectFrameLayout;
import com.bokecc.camerafilter.camera.widget.CameraTextureView;
import com.bokecc.camerafilter.glfilter.color.bean.DynamicColor;
import com.bokecc.camerafilter.glfilter.resource.FilterHelper;
import com.bokecc.camerafilter.multimedia.VideoCombiner;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bokecc.shortvideosdk.cutvideo.VideoBean;
import bokecc.shortvideosdk.editvideo.CutVideoActivity;
import bokecc.shortvideosdk.editvideo.EditVideoActivity;
import bokecc.shortvideosdk.editvideo.SelectVideoOrImageActivity;
import bokecc.shortvideosdk.presenter.CameraPreviewPresenter;
import bokecc.shortvideosdk.util.FileUtils;
import bokecc.shortvideosdk.util.MultiUtils;
import bokecc.shortvideosdk.widget.BeautyDialog;
import bokecc.shortvideosdk.widget.CustomProgressDialog;
import bokecc.shortvideosdk.widget.FilterDialog;
import bokecc.shortvideosdk.widget.ProgressView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_flash_light, switchCameraView, iv_record_video, iv_complete_record,
            iv_delete_last, iv_beauty, iv_countdown_time, iv_countdown, iv_close;
    private ProgressView progressView;
    private String videoPath, dirName = "AHuodeShortVideo";
    private int SELECTVIDEOCODE = 1, currentBeautyWhiteValue = 0, currentBeautySkinValue = 0, currentFilter = 0,
            delayRecordTime = 3;
    private Activity activity;
    private LinearLayout ll_beauty, ll_filter, ll_select_video, ll_camera_control;
    private TextView tv_record_time;
    private AspectFrameLayout afl_layout;
    private CameraTextureView cameraTextureView;
    private CameraPreviewPresenter mPreviewPresenter;
    // 预览参数
    private CameraParam mCameraParam;
    //是否开启了闪光灯
    private boolean flashOn = false;
    //是否可以启动倒计时拍摄任务
    private boolean isCanStartDelayRecord = true, isOpenDelayRecord = false;
    private Timer delayTimer;
    private DelayRecordTask delayRecordTask;
    private boolean isCanChangeSize = false;
    private int focusSize = 120;

    private IsCanCombineTask isCanCombineTask;
    private Timer isCanCombineTimer;
    private CustomProgressDialog customProgressDialog;
    private boolean isCombineVideo = false;
    private CloseReceiver closeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        MultiUtils.setFullScreen(this);
        //修改录制时长 单位：毫秒
        CameraParam.DEFAULT_RECORD_TIME = 60 * 1000;

        initView();
        resetViewStatus();
        initResources();

        mCameraParam = CameraParam.getInstance();
        mPreviewPresenter = new CameraPreviewPresenter(this);
        mPreviewPresenter.onAttach(activity);

        //注册广播
        closeReceiver = new CloseReceiver();
        IntentFilter intentFilter = new IntentFilter("bokecc.shortvideosdk.CLOSE_MAIN");
        registerReceiver(closeReceiver, intentFilter);
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //预览画面
        afl_layout = findViewById(R.id.afl_layout);
        cameraTextureView = new CameraTextureView(activity);
        cameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        cameraTextureView.addCameraViewClickListener(onCameraViewClickListener);
        afl_layout.addView(cameraTextureView);
        afl_layout.setAspectRatio(MultiUtils.div(2, 9, 16));

        iv_beauty = findViewById(R.id.iv_beauty);

        iv_record_video = findViewById(R.id.iv_record_video);
        iv_record_video.setOnClickListener(this);

        iv_countdown_time = findViewById(R.id.iv_countdown_time);
        iv_countdown_time.setOnClickListener(this);

        iv_flash_light = findViewById(R.id.iv_flash_light);
        iv_flash_light.setOnClickListener(this);

        switchCameraView = findViewById(R.id.iv_switch_camera);
        switchCameraView.setOnClickListener(this);


        iv_countdown = findViewById(R.id.iv_countdown);

        iv_complete_record = findViewById(R.id.iv_complete_record);
        iv_complete_record.setOnClickListener(this);

        iv_delete_last = findViewById(R.id.iv_delete_last);
        iv_delete_last.setOnClickListener(this);

        iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        ll_beauty = findViewById(R.id.ll_beauty);
        ll_beauty.setOnClickListener(this);

        ll_filter = findViewById(R.id.ll_filter);
        ll_filter.setOnClickListener(this);

        ll_select_video = findViewById(R.id.ll_select_video);
        ll_select_video.setOnClickListener(this);

        tv_record_time = findViewById(R.id.tv_record_time);

        progressView = findViewById(R.id.progress_view);
        ll_camera_control = findViewById(R.id.ll_camera_control);
        progressView.setData();
        progressView.setMaxDuration(CameraParam.DEFAULT_RECORD_TIME);
        PreviewRecorder.getInstance().setRecordTime(CameraParam.DEFAULT_RECORD_TIME);
    }

    /**
     * 初始化滤镜资源
     */
    private void initResources() {
        new Thread(() -> {
            FilterHelper.initAssetsFilter(activity);
        }).start();
    }

    private String getOutPutPath() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + dirName, "RecordVideo");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".mp4";
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.bindSurface(surface);
            mPreviewPresenter.changePreviewSize(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.changePreviewSize(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    TimerTask captureTimerTask;

    //更新录制进度
    public void updateRecordProgress(long duration) {
        progressView.setCurrentDuration((int) duration);
        if (duration >= CameraParam.DEFAULT_RECORD_TIME) {
            mPreviewPresenter.stopRecord();
            startIsCanCombineTimer();
            tv_record_time.setVisibility(View.GONE);
        } else {
            int recordTime = (int) (duration / 1000);
            tv_record_time.setText(recordTime + "s");
            tv_record_time.setVisibility(View.VISIBLE);
        }

    }

    private void stopCaptureTimer() {
        if (captureTimerTask != null) {
            captureTimerTask.cancel();
        }
    }

    private void resetViewStatus() {
        iv_record_video.setImageResource(R.mipmap.iv_record_video);
        stopCaptureTimer();
        resetFinishCaptureView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record_video:
                if (isOpenDelayRecord) {
                    if (isCanStartDelayRecord) {
                        startDelayRecordTask();
                    }
                } else {
                    switchRecordStatus();
                }
                break;
            case R.id.iv_delete_last:
                deleteLastVideo();
                break;
            case R.id.iv_complete_record:
                if (!(iv_delete_last.getVisibility() == View.VISIBLE)) {
                    mPreviewPresenter.stopRecord();
                }
                startIsCanCombineTimer();
                break;
            case R.id.iv_flash_light:
                switchFlash();
                break;
            case R.id.iv_switch_camera:
                switchCamera();
                break;

            case R.id.ll_filter:
                FilterDialog filterDialog = new FilterDialog(activity, currentFilter, new FilterDialog.OnSelectFilter() {
                    @Override
                    public void selectFilter(int filterPos, DynamicColor color) {
                        currentFilter = filterPos;
                        mPreviewPresenter.changeDynamicFilter(color);
                    }
                });
                filterDialog.show();
                break;

            case R.id.ll_beauty:
                iv_beauty.setImageResource(R.mipmap.iv_beauty_on);
                if (currentBeautySkinValue == 0) {
                    currentBeautySkinValue = 50;
                    currentBeautyWhiteValue = 50;
                    mCameraParam.beauty.beautyIntensity = 0.5f;
                    mCameraParam.beauty.complexionIntensity = 0.5f;
                }

                BeautyDialog beautyDialog = new BeautyDialog(activity, currentBeautyWhiteValue, currentBeautySkinValue, new BeautyDialog.OnBeauty() {
                    @Override
                    public void getBeautyWhiteValue(int value) {
                        currentBeautyWhiteValue = value;
                    }

                    @Override
                    public void getBeautySkinValue(int value) {
                        currentBeautySkinValue = value;
                    }
                });
                beautyDialog.show();
                break;
            case R.id.ll_select_video:
                startActivityForResult(new Intent(activity, SelectVideoOrImageActivity.class), SELECTVIDEOCODE);
                break;
            case R.id.iv_countdown_time:
                if (!isCanStartDelayRecord) {
                    return;
                }
                isOpenDelayRecord = !isOpenDelayRecord;
                if (isOpenDelayRecord) {
                    iv_countdown_time.setImageResource(R.mipmap.iv_countdown_on);
                } else {
                    iv_countdown_time.setImageResource(R.mipmap.iv_countdown_off);
                }
                break;

            case R.id.iv_close:
                if (mPreviewPresenter.isRecording()) {
                    MultiUtils.showToast(activity, "请稍候");
                } else {
                    mPreviewPresenter.removeAllSubVideo();
                    resetView();
                }

                break;

        }
    }

    //预览画面点击事件
    private CameraTextureView.OnCameraViewClickListener onCameraViewClickListener = new CameraTextureView.OnCameraViewClickListener() {
        @Override
        public void onCameraViewClick(float x, float y) {
            CameraEngine.getInstance().setFocusArea(CameraEngine.getFocusArea((int) x, (int) y,
                    cameraTextureView.getWidth(), cameraTextureView.getHeight(), focusSize));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cameraTextureView.showFocusAnimation();
                }
            });
        }
    };

    private void startDelayRecordTask() {
        isCanStartDelayRecord = false;
        cancelDelayRecordTask();
        delayRecordTime = 3;
        iv_countdown.setImageResource(R.mipmap.iv_countdown_three);
        iv_countdown.setVisibility(View.VISIBLE);
        iv_countdown_time.setImageResource(R.mipmap.iv_countdown_on);
        delayTimer = new Timer();
        delayRecordTask = new DelayRecordTask();
        delayTimer.schedule(delayRecordTask, 1000, 1000);
    }

    private void cancelDelayRecordTask() {
        if (delayTimer != null) {
            delayTimer.cancel();
        }
        if (delayRecordTask != null) {
            delayRecordTask.cancel();
        }
    }

    // 倒计时拍摄任务
    class DelayRecordTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    delayRecordTime = delayRecordTime - 1;
                    if (delayRecordTime == 2) {
                        iv_countdown.setImageResource(R.mipmap.iv_countdown_two);
                    } else if (delayRecordTime == 1) {
                        iv_countdown.setImageResource(R.mipmap.iv_countdown_one);
                    } else if (delayRecordTime == 0) {
                        iv_countdown.setVisibility(View.GONE);
                        switchRecordStatus();
                    } else {
                        isOpenDelayRecord = false;
                        isCanStartDelayRecord = true;
                        iv_countdown_time.setImageResource(R.mipmap.iv_countdown_off);
                        cancelDelayRecordTask();
                    }

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SELECTVIDEOCODE) {
            videoPath = data.getStringExtra("path");

            if (TextUtils.isEmpty(videoPath)) {
                MultiUtils.showToast(activity, "文件有误，请重新选择");
                return;
            }
            long videoSize = FileUtils.getFileSize(videoPath);
            if (videoSize > 100 * 1024 * 1024) {
                MultiUtils.showToast(activity, "视频大小不能超过100MB");
                return;
            }
            VideoBean localVideoInfo = MultiUtils.getLocalVideoInfo(videoPath);
            int rotation = localVideoInfo.rotation;
            long duration = localVideoInfo.duration;
            if (duration > 3 * 60 * 1000) {
                MultiUtils.showToast(activity, "视频时长不能超过3分钟");
                return;
            }
            startActivity(new Intent(activity, CutVideoActivity.class).putExtra("videoPath", videoPath).putExtra("rotation", rotation));
            finish();
        }

    }

    private void deleteLastVideo() {
        if (PreviewRecorder.getInstance().getNumberOfSubVideo() < 1) {
            MultiUtils.showToast(activity, "请先录制一段视频");
        }

        if (mPreviewPresenter.isRecording()) {
            MultiUtils.showToast(activity, "录制中，请停止录制后进行删除操作");
            return;
        }


        PreviewRecorder.getInstance().removeLastSubVideo();
        resetFinishCaptureView();

        int currentDuration = PreviewRecorder.getInstance().getDuration();
        int recordTime = currentDuration / 1000;
        tv_record_time.setText(recordTime + "s");
    }

    private void resetFinishCaptureView() {
        if (PreviewRecorder.getInstance().getNumberOfSubVideo() > 0) {
            iv_complete_record.setVisibility(View.VISIBLE);
            iv_delete_last.setVisibility(View.VISIBLE);
            iv_close.setVisibility(View.VISIBLE);
        } else {
            resetView();
        }
    }

    private void startIsCanCombineTimer() {
        isCombineVideo = false;
        cancelIsCanCombineTimer();
        customProgressDialog = new CustomProgressDialog(activity);
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.show();

        isCanCombineTimer = new Timer();
        isCanCombineTask = new IsCanCombineTask();
        isCanCombineTimer.schedule(isCanCombineTask, 0, 30);
    }


    private void cancelIsCanCombineTimer() {

        if (isCanCombineTimer != null) {
            isCanCombineTimer.cancel();
        }
        if (isCanCombineTask != null) {
            isCanCombineTask.cancel();
        }
    }


    class IsCanCombineTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mPreviewPresenter.isRecording() && !isCombineVideo) {
                        cancelIsCanCombineTimer();
                        isCombineVideo = true;
                        combineVideo();
                    }
                }
            });
        }
    }


    //合成视频
    public void combineVideo() {
        mPreviewPresenter.destroyRecorder();
        //分段视频的数量
        int numberOfSubVideo = mPreviewPresenter.getNumberOfSubVideo();
        if (numberOfSubVideo > 1) {
            videoPath = getOutPutPath();
            PreviewRecorder.getInstance().combineVideo(videoPath, mCombineListener);
        } else {
            List<String> subVideoPathList = PreviewRecorder.getInstance().getSubVideoPathList();
            if (subVideoPathList != null && subVideoPathList.size() > 0) {
                videoPath = subVideoPathList.get(0);
                startEdit();
            } else {
                startEdit();
            }
        }
    }

    // 合成监听器
    private VideoCombiner.CombineListener mCombineListener = new VideoCombiner.CombineListener() {
        @Override
        public void onCombineStart() {

        }

        @Override
        public void onCombineProcessing(final int current, final int sum) {

        }

        @Override
        public void onCombineFinished(final boolean success) {
            startEdit();
        }
    };

    private void startEdit() {
        mPreviewPresenter.removeAllSubVideo();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                intoEditActivity();
                resetView();
            }
        });
    }

    private void resetView() {
        ll_beauty.setVisibility(View.VISIBLE);
        ll_camera_control.setVisibility(View.VISIBLE);
        ll_filter.setVisibility(View.VISIBLE);
        ll_select_video.setVisibility(View.VISIBLE);
        tv_record_time.setVisibility(View.GONE);
        iv_delete_last.setVisibility(View.GONE);
        iv_close.setVisibility(View.GONE);
        iv_complete_record.setVisibility(View.GONE);
    }

    private void intoEditActivity() {

        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            customProgressDialog.dismiss();
            customProgressDialog = null;
        }

        if (mCameraParam.supportFlash) {
            CameraEngine.getInstance().setFlashLight(false);
        }
        iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
        Intent intent = new Intent(activity, EditVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("isRecord", true);
        startActivity(intent);
        finish();

    }

    private void switchFlash() {
        if (mCameraParam.supportFlash) {
            boolean flashLightOn = CameraEngine.getInstance().isFlashLightOn();
            flashOn = !flashLightOn;
            CameraEngine.getInstance().setFlashLight(flashOn);
            if (flashOn) {
                iv_flash_light.setImageResource(R.mipmap.iv_flash_on);
            } else {
                iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
            }
        }
    }

    private void switchCamera() {
        mPreviewPresenter.switchCamera();
        iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
    }

    private void switchRecordStatus() {
        ll_filter.setVisibility(View.GONE);
        ll_beauty.setVisibility(View.GONE);
        ll_select_video.setVisibility(View.GONE);
        ll_camera_control.setVisibility(View.GONE);
        if (mPreviewPresenter.isRecording()) {
            //暂停录制
            iv_delete_last.setVisibility(View.VISIBLE);
            iv_close.setVisibility(View.VISIBLE);
            iv_complete_record.setVisibility(View.VISIBLE);
            iv_record_video.setImageResource(R.mipmap.iv_record_video);
            mPreviewPresenter.stopRecord();
            progressView.stop();
        } else {
            //开始录制
            iv_delete_last.setVisibility(View.GONE);
            iv_close.setVisibility(View.GONE);
            iv_complete_record.setVisibility(View.VISIBLE);
            iv_record_video.setImageResource(R.mipmap.iv_recording_video);

            int width = mCameraParam.DEFAULT_9_16_WIDTH;
            int height = mCameraParam.DEFAULT_9_16_HEIGHT;
            videoPath = getOutPutPath();
            progressView.start();
            mPreviewPresenter.startRecord(width, height, videoPath);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetViewStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (flashOn) {
            CameraEngine.getInstance().setFlashLight(!flashOn);
            iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCanChangeSize) {
            mCameraParam.backCamera = !mCameraParam.backCamera;
            mPreviewPresenter.switchCamera();

            if (flashOn) {
                iv_flash_light.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraEngine.getInstance().setFlashLight(flashOn);
                        iv_flash_light.setImageResource(R.mipmap.iv_flash_on);
                    }
                }, 500);
            }
        }
        isCanChangeSize = true;
    }

    class CloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreviewPresenter.removeAllSubVideo();
        PreviewRenderer.getInstance().stopRecording();
        CameraEngine.getInstance().releaseCamera();

        mPreviewPresenter.onDestroy();
        mPreviewPresenter.onDetach();
        mPreviewPresenter = null;
        cancelDelayRecordTask();
        cancelIsCanCombineTimer();

        if (closeReceiver != null) {
            unregisterReceiver(closeReceiver);
        }
    }
}