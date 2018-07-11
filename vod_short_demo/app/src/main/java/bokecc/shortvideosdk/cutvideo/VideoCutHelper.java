package bokecc.shortvideosdk.cutvideo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


public final class VideoCutHelper {
    public static final String TAG = "VideoCutHelper";
    private static VideoCutHelper mVideoFFCrop;
    private static boolean isDebug = true;


    private VideoCutHelper() {

    }

    public static VideoCutHelper getInstance() {
        if (mVideoFFCrop == null) {
            synchronized (VideoCutHelper.class) {
                if (mVideoFFCrop == null) {
                    mVideoFFCrop = new VideoCutHelper();
                }
            }
        }
        return mVideoFFCrop;
    }

    /**
     * 初始化
     */
    public void init(final Context context) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                String executablePath = "/data/data/" + context.getPackageName() + "/ffmpeg";
                Log(TAG, "init...");
                InputStream ffcutSrc = null;
                FileOutputStream ffcutDest = null;
                try {
                    File exFile = new File(executablePath);
                    ffcutSrc = context.getAssets().open("ffmpeg");
                    if (exFile != null && exFile.exists() && ffcutSrc.available() == exFile.length()) {
                        Log(TAG, "init already...");
                        return null;
                    }

                    ffcutDest = new FileOutputStream(executablePath);
                    Log(TAG, "copying executable...");
                    byte[] buf = new byte[96 * 1024];
                    int length = 0;
                    while ((length = ffcutSrc.read(buf)) != -1) {
                        ffcutDest.write(buf, 0, length);
                    }
                    ffcutDest.flush();
                    ffcutDest.close();
                    ffcutSrc.close();
                    Log(TAG, "executable is copyed, applying permissions...");
                    Process chmod = Runtime.getRuntime().exec("/system/bin/chmod 755 " + executablePath);
                    chmod.waitFor();
                    Log(TAG, "ffcut is initialized");
                } catch (Exception e) {
                    Log(TAG, "ffcut initialization is failed, " + e.getClass().getName() + ": " + e.getMessage());
                } finally {
                    if (ffcutSrc != null) {
                        try {
                            ffcutSrc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ffcutDest != null) {
                        try {
                            ffcutDest.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {

            }

            @Override
            protected void onPostExecute(Object result) {

            }
        };
        task.execute();
    }

    /**
     * 视频 时长&尺寸裁剪
     */
    public void cropVideo(final Context context,
                          final String srcVideoPath, final String destPath,
                          final int start, final int duration,
                          final int width, final int height,
                          final int x, final int y,
                          final FFListener listener) {
        AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                String cmd = params[0];
                Log(TAG, "running command " + cmd);
                try {
                    Process ffcut = Runtime.getRuntime().exec(cmd);
                    InputStream error = ffcut.getErrorStream();
                    Scanner errorScanner = new Scanner(error);
                    int count = 0;
                    while (errorScanner.hasNextLine()) {
                        String line = errorScanner.nextLine();
                        Log(TAG, "ffmpeg: " + line);
                        publishProgress(++count);
                    }
                    return new Integer(ffcut.waitFor());
                } catch (Exception e) {
                    Log(TAG, "exception " + e.getClass().getName() + ": " + e.getMessage());
                    return new Integer(200);
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int fz = values[0];
                int fm = duration * 1000 / 100;
                int progress = fz * 100 / fm;
                progress = progress > 99 ? 99 : progress;
                if (listener != null) {
                    listener.onProgress(progress);
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                Log(TAG, "ffmpeg is finished with code " + result.intValue());
                if (result.intValue() == 0) {
                    if (listener != null) {
                        listener.onProgress(100);
                        listener.onFinish();
                    }
                } else {
                    if (listener != null) {
                        listener.onFail("crop doInBackground exception");
                    }
                }
            }

            @Override
            protected void onCancelled() {
                if (listener != null) {
                    listener.onFail("crop canceled");
                }
            }
        };

        //-ss 0 -t 5  时间裁切
        //-strict -2 -vf crop=500:500:0:100   尺寸裁切
        String cmd = "/data/data/" + context.getPackageName() + "/ffmpeg" + " -y -i "
                + srcVideoPath
                + " -ss " + start + " -t " + duration
                + " -strict -2 -vf crop=" + width + ":" + height + ":" + x + ":" + y + " -preset fast "
                + destPath;
        Log.d(TAG, "ghc cmd: " + cmd);
        task.execute(cmd);
    }

    /**
     * Log信息
     *
     * @param tag
     * @param msg
     */
    public void Log(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    /**
     * 回调监听
     */
    public static interface FFListener {
        /**
         */
        void onProgress(Integer progress);

        /**
         */
        void onFinish();

        /**
         */
        void onFail(String msg);
    }
}
