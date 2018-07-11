package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.camera.utils.CameraInfo;
import com.seu.magicfilter.encoder.MediaVideoEncoder;
import com.seu.magicfilter.encoder.video.TextureMovieEncoder;
import com.seu.magicfilter.filter.advanced.MagicBeautyFilter;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.widget.base.MagicBaseView;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicCameraView extends MagicBaseView {

    private MagicCameraInputFilter cameraInputFilter;
    private MagicBeautyFilter beautyFilter;

    private SurfaceTexture surfaceTexture;

    public MagicCameraView(Context context) {
        this(context, null);
    }

    private boolean recordingEnabled;
    private int recordingStatus;

    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;

    boolean DEBUG = false;


    public MagicCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);

        recordingStatus = -1;
        recordingEnabled = false;
        scaleType = ScaleType.CENTER_CROP;
    }

    public void setCamraParams(int cameraWidth, int cameraHeight) {
        CameraEngine.setCameraParams(cameraWidth, cameraHeight);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(DEBUG) Log.i(TAG, "onSurfaceCreated");
        super.onSurfaceCreated(gl, config);
//        recordingEnabled = videoEncoder.isRecording();
        if (recordingEnabled)
            recordingStatus = RECORDING_RESUMED;
        else
            recordingStatus = RECORDING_OFF;
        if(cameraInputFilter == null)
            cameraInputFilter = new MagicCameraInputFilter();
        cameraInputFilter.init();
        if (textureId == OpenGlUtils.NO_TEXTURE) {
            textureId = OpenGlUtils.getExternalOESTextureID();
            if (textureId != OpenGlUtils.NO_TEXTURE) {
                surfaceTexture = new SurfaceTexture(textureId);
                surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        openCamera();

        android.opengl.Matrix.setIdentityM(mMvpMatrix, 0);
    }

    private final float[] mMvpMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if(surfaceTexture == null)
            return;

        try {
            surfaceTexture.updateTexImage();
        } catch(Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return;
        }

        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        cameraInputFilter.setTextureTransformMatrix(mtx);
        int id = textureId;
        if(filter == null){
            cameraInputFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);

        }else{
            id = cameraInputFilter.onDrawToTexture(textureId);
            filter.onDrawFrame(id, gLCubeBuffer, gLTextureBuffer);
        }

        synchronized (this) {
            if (mVideoEncoder != null) {
                isDraw = !isDraw;
                if (isDraw) {
                    mVideoEncoder.setTextureId(id);
                    mVideoEncoder.frameAvailableSoon(surfaceTexture, gLCubeBuffer, gLTextureBuffer);
                }
            }
        }

    }

    boolean isDraw;

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    MagicFilterType type;

    @Override
    public void setFilter(MagicFilterType type) {
        super.setFilter(type);
        this.type = type;
    }

    private void openCamera(){
        if(CameraEngine.getCamera() == null)
            CameraEngine.openCamera();

        CameraInfo info = CameraEngine.getCameraInfo();
        if(info.orientation == 90 || info.orientation == 270){
            imageWidth = info.previewHeight;
            imageHeight = info.previewWidth;
        }else{
            imageWidth = info.previewWidth;
            imageHeight = info.previewHeight;
        }

        cameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);

        adjustSize(90, false, true); //目前是写死的，修复最小化再回来导致的镜像问题

        if(surfaceTexture != null)
            CameraEngine.startPreview(surfaceTexture);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        CameraEngine.releaseCamera();
    }

    public void changeRecordingState(boolean isRecording) {
        recordingEnabled = isRecording;
    }

    protected void onFilterChanged(){
        super.onFilterChanged();
        cameraInputFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
        if(filter != null)
            cameraInputFilter.initCameraFrameBuffer(imageWidth, imageHeight);
        else
            cameraInputFilter.destroyFramebuffers();
    }

    @Override
    public void savePicture(final SavePictureTask savePictureTask) {
        CameraEngine.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraEngine.stopPreview();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap photo = drawPhoto(bitmap,CameraEngine.getCameraInfo().isFront);
                        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
                        if (photo != null)
                            savePictureTask.execute(photo);
                    }
                });
                CameraEngine.startPreview();
            }
        });
    }

    private Bitmap drawPhoto(Bitmap bitmap,boolean isRotated){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] mFrameBuffers = new int[1];
        int[] mFrameBufferTextures = new int[1];
        if(beautyFilter == null)
            beautyFilter = new MagicBeautyFilter();
        beautyFilter.init();
        beautyFilter.onDisplaySizeChanged(width, height);
        beautyFilter.onInputSizeChanged(width, height);

        if(filter != null) {
            filter.onInputSizeChanged(width, height);
            filter.onDisplaySizeChanged(width, height);
        }
        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        GLES20.glViewport(0, 0, width, height);
        int textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, true);

        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        if(isRotated)
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, false)).position(0);
        else
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);


        if(filter == null){
            beautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
        }else{
            beautyFilter.onDrawFrame(textureId);
            filter.onDrawFrame(mFrameBufferTextures[0], gLCubeBuffer, gLTextureBuffer);
        }
        IntBuffer ib = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ib);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
        GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);

        beautyFilter.destroy();
        beautyFilter = null;
        if(filter != null) {
            filter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
            filter.onInputSizeChanged(imageWidth, imageHeight);
        }
        return result;
    }

    public void onBeautyLevelChanged() {
        cameraInputFilter.onBeautyLevelChanged();
    }


    String TAG = "magciCameraView";
    private MediaVideoEncoder mVideoEncoder;
    public void setVideoEncoder(final MediaVideoEncoder encoder) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (TAG) {
                    if (encoder != null) {
                        encoder.setEglContext(EGL14.eglGetCurrentContext(), textureId);
                    }
                    mVideoEncoder = encoder;
                    if (mVideoEncoder != null) {
                        mVideoEncoder.setFilter(type);
                    }
                }
            }
        });
    }
}
