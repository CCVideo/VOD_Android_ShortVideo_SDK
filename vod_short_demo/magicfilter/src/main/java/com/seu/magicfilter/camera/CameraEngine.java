package com.seu.magicfilter.camera;

import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceView;

import com.seu.magicfilter.camera.utils.CameraUtils;

public class CameraEngine {
    private static Camera camera = null;
    private static int cameraID = 0;
    private static SurfaceTexture surfaceTexture;
    private static SurfaceView surfaceView;

    private static int cameraWidth = 640;
    private static int cameraHeight = 480;

    public static void setCameraParams(int width, int height) {
        cameraWidth = width;
        cameraHeight = height;
    }

    public static Camera getCamera(){
        return camera;
    }

    public static boolean openCamera(){
        if(camera == null){
            try{
                camera = Camera.open(cameraID);

                setDefaultParameters(cameraID);

                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id){
        if(camera == null){
            try{
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters(cameraID);
                return true;
            }catch(RuntimeException e){
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera(){
        if(camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void resumeCamera(){
        openCamera();
    }

    public void setParameters(Parameters parameters){
        camera.setParameters(parameters);
    }

    public Parameters getParameters(){
        if(camera != null)
            camera.getParameters();
        return null;
    }

    public static void switchCamera(){
        releaseCamera();
        cameraID = cameraID == 0 ? 1 : 0;
        openCamera(cameraID);
        startPreview(surfaceTexture);
    }

    public final static int FLASH_INVALID = -1;
    public final static int FLASH_OFF = 0;
    public final static int FLASH_ON = 1;

    public static int switchFlashMode() {

        if (cameraID == CameraInfo.CAMERA_FACING_FRONT) {
            return FLASH_INVALID;
        }

        Camera.Parameters parameters = camera.getParameters();
        String currentMode = parameters.getFlashMode();

        int status = FLASH_OFF;

        if (Camera.Parameters.FLASH_MODE_OFF.equals(currentMode)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            status = FLASH_ON;
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        camera.setParameters(parameters);

        return status;
    }

    private static void setDefaultParameters(int mCameraId){
        Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        parameters.setPreviewSize(cameraHeight, cameraWidth); //翻转，所以长宽为反的
        Size pictureSize = CameraUtils.getLargePictureSize(camera);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
//        parameters.setRotation(90);

        camera.setParameters(parameters);
    }

    private static Size getPreviewSize(){
        return camera.getParameters().getPreviewSize();
    }

    private static Size getPictureSize(){
        return camera.getParameters().getPictureSize();
    }

    public static void startPreview(SurfaceTexture surfaceTexture){
        if(camera != null)
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void startPreview(){
        if(camera != null)
            camera.startPreview();
    }

    public static void stopPreview(){
        camera.stopPreview();
    }

    public static void setRotation(int rotation){
        Log.e(TAG, "set rotation " + rotation);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback){
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    static String TAG = "CameraEngine";
    public static com.seu.magicfilter.camera.utils.CameraInfo getCameraInfo(){
        com.seu.magicfilter.camera.utils.CameraInfo info = new com.seu.magicfilter.camera.utils.CameraInfo();
        Size size = getPreviewSize();
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);
        info.previewWidth = size.width;
        info.previewHeight = size.height;

        info.orientation = cameraInfo.orientation;
        info.isFront = cameraID == 1 ? true : false;
        size = getPictureSize();
        info.pictureWidth = size.width;
        info.pictureHeight = size.height;

        return info;
    }
}