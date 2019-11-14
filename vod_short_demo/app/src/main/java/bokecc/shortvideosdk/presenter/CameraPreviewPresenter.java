package bokecc.shortvideosdk.presenter;

import android.app.Activity;
import android.graphics.SurfaceTexture;

import com.bokecc.camerafilter.camera.engine.CameraParam;
import com.bokecc.camerafilter.camera.listener.OnCameraCallback;
import com.bokecc.camerafilter.camera.listener.OnFpsListener;
import com.bokecc.camerafilter.camera.listener.OnRecordListener;
import com.bokecc.camerafilter.camera.recordervideo.PreviewRecorder;
import com.bokecc.camerafilter.camera.render.PreviewRenderer;
import com.bokecc.camerafilter.filterutil.BrightnessUtils;
import com.bokecc.camerafilter.glfilter.color.bean.DynamicColor;

import bokecc.shortvideosdk.MainActivity;
import bokecc.shortvideosdk.util.MultiUtils;


public class CameraPreviewPresenter extends PreviewPresenter<MainActivity>
        implements OnRecordListener, OnCameraCallback, OnFpsListener {

    // 预览参数
    private CameraParam mCameraParam;

    private Activity mActivity;

    public CameraPreviewPresenter(MainActivity target) {
        super(target);
        mCameraParam = CameraParam.getInstance();
    }

    public void onAttach(Activity activity) {
        mActivity = activity;
        int currentMode = BrightnessUtils.getSystemBrightnessMode(mActivity);
        if (currentMode == 1) {
            mCameraParam.brightness = -1;
        } else {
            mCameraParam.brightness = BrightnessUtils.getSystemBrightness(mActivity);
        }
        mCameraParam.audioPermitted = true;
        //设置启动后置摄像头
        mCameraParam.setBackCamera(true);
        // 初始化相机渲染引擎
        PreviewRenderer.getInstance()
                .setCameraCallback(this)
                .setFpsCallback(this)
                .initRenderer(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 关闭渲染引擎
        PreviewRenderer.getInstance().destroyRenderer();
    }

    public void onDetach() {
        mActivity = null;
    }


    /**
     * 绑定SurfaceTexture
     *
     * @param surfaceTexture
     */
    public void bindSurface(SurfaceTexture surfaceTexture) {
        PreviewRenderer.getInstance().bindSurface(surfaceTexture);
    }

    /**
     * 改变预览尺寸
     *
     * @param width
     * @param height
     */
    public void changePreviewSize(int width, int height) {
        PreviewRenderer.getInstance().changePreviewSize(width, height);
    }

    /**
     * 解绑Surface
     */
    public void unBindSurface() {
        PreviewRenderer.getInstance().unbindSurface();
    }

    /**
     * 相机打开回调
     */
    @Override
    public void onCameraOpened() {
        if (getTarget() != null) {

        }
    }

    /**
     * 相机预览回调
     *
     * @param data
     */
    @Override
    public void onPreviewCallback(byte[] data) {
        PreviewRenderer.getInstance().requestRender();
    }

    /**
     * fps数值回调
     *
     * @param fps
     */
    @Override
    public void onFpsCallback(float fps) {
        if (getTarget() != null) {

        }
    }

    /**
     * 录制开始
     */
    @Override
    public void onRecordStarted() {
        if (getTarget() != null) {

        }
    }

    /**
     * 录制发生变化
     *
     * @param duration
     */
    @Override
    public void onRecordProgressChanged(final long duration) {
        if (getTarget() != null) {
            getTarget().updateRecordProgress(duration);
        }
    }


    @Override
    public void onRecordFinish() {
        if (getTarget() != null) {

        }
    }

    /**
     * 开始录制
     *
     * @param width
     * @param height
     * @param outPutPath
     */
    public void startRecord(int width, int height, String outPutPath) {

        PreviewRecorder.getInstance()
                .setRecordType(PreviewRecorder.RecordType.Video)
                .setOutputPath(outPutPath)
                .enableAudio(true)
                .setRecordSize(width, height)
                .setOnRecordListener(this)
                .startRecord();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        PreviewRecorder.getInstance().stopRecord();
    }


    /**
     * 销毁录制器
     */
    public void destroyRecorder() {
        PreviewRecorder.getInstance().destroyRecorder();
    }

    /**
     * 是否正处于录制过程
     *
     * @return
     */
    public boolean isRecording() {
        return PreviewRecorder.getInstance().isRecording();
    }

    /**
     * 移除所有分段视频
     */
    public void removeAllSubVideo() {
        PreviewRecorder.getInstance().removeAllSubVideo();
        PreviewRecorder.getInstance().deleteRecordDuration();
    }

    /**
     * 获取分段视频数量
     *
     * @return
     */
    public int getNumberOfSubVideo() {
        return PreviewRecorder.getInstance().getNumberOfSubVideo();
    }


    /**
     * 切换相机
     */
    public void switchCamera() {
        PreviewRenderer.getInstance().switchCamera();
    }


    /**
     * 切换滤镜
     *
     * @param color
     */
    public void changeDynamicFilter(DynamicColor color) {
        PreviewRenderer.getInstance().changeDynamicFilter(color);
    }

}
