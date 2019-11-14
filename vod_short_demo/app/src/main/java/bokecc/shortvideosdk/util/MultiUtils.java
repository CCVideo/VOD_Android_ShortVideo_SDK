package bokecc.shortvideosdk.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bokecc.camerafilter.media.VideoInfo;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bokecc.shortvideosdk.cutvideo.VideoBean;
import bokecc.shortvideosdk.model.MusicInfo;

public class MultiUtils {

    private static String DOWNLOAD_CONTENT = "content://downloads/public_downloads";

    public static String APP_FILE_PATH = "AHuodeShortVideo";


    public static void showToast(final Activity activity, final String content) {
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void setStatusBarColor(Activity activity, int color, boolean isDarkStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int option;
            if (isDarkStatusBar) {
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
            window.getDecorView().setSystemUiVisibility(option);

            window.getDecorView().setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(color));
        }
    }

    /**
     * 获取本地视频信息
     */
    public static VideoBean getLocalVideoInfo(String path) {
        VideoBean info = new VideoBean();
        info.src_path = path;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            info.src_path = path;
            info.duration = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            info.rate = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            info.width = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            info.height = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            info.rotation = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        return info;
    }

    /**
     * 获取本地视频信息
     */
    public static VideoInfo getVideoInfo(String path) {
        VideoInfo info = new VideoInfo();
        info.path = path;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            info.path = path;
            info.duration = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            info.bitRate = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            info.width = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            info.height = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            info.rotation = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        return info;
    }

    //设置为全屏
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //将dp转化为sp
    public static int dipToPx(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    //得到file
    public static File getNewFile(Context context, String folderName,String stickerName) {
        String path;
        if (isSDAvailable()) {
            path = getFolderName(folderName) + File.separator + stickerName + ".png";
        } else {
            path = context.getFilesDir().getPath() + File.separator + stickerName + ".png";
        }

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File saveFile = new File(path);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        return saveFile;
    }

    public static File getNewFile(Context context, String folderName) {
        String path;
        String stickerName = "sticker";
        if (isSDAvailable()) {
            path = getFolderName(folderName) + File.separator + stickerName + ".png";
        } else {
            path = context.getFilesDir().getPath() + File.separator + stickerName + ".png";
        }

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File saveFile = new File(path);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        return saveFile;
    }


    public static double div(int scale, int num1, int num2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(num1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(num2));
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float calFloat(int scale, int num1, int num2) {
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(num1));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(num2));
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 判断sd卡是否可以用
     */
    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFolderName(String name) {
        File mediaStorageDir =
                new File(Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FILE_PATH,
                        name);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return "";
            }
        }
        return mediaStorageDir.getAbsolutePath();
    }

    //获得输出视频的路径
    public static String getOutPutVideoPath() {
        String outPath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FILE_PATH;
        File file = new File(outPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File outPutVideo = new File(outPath, System.currentTimeMillis()+".mp4");
        if (outPutVideo.exists()) {
            outPutVideo.delete();
        }

        return outPutVideo.getAbsolutePath();
    }

    //获得输出裁剪音乐的路径
    public static String getOutPutMusicPath() {
        String outPath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FILE_PATH;
        File file = new File(outPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File outPutMusic = new File(outPath, "outPutMusic.mp3");
        if (outPutMusic.exists()) {
            outPutMusic.delete();
        }

        return outPutMusic.getAbsolutePath();
    }

    //获得屏幕可用的宽度
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int height = dm.widthPixels;
        return height;
    }

    //获得屏幕可用的高度
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int height = dm.heightPixels;
        return height;
    }

    public static void insertMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    //删除文件
    public static void deleteFile(String path){
        File file = new File(path);
        if (file!=null && file.exists()){
            file.delete();
        }
    }

    //得到本地音乐信息
    public static List getMusicInfos(Context context) {
        List list = new ArrayList();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.musicName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                musicInfo.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                musicInfo.musicPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                musicInfo.musicTime = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                musicInfo.isSelected = false;
                list.add(musicInfo);
            }
            cursor.close();
        }

        return list;
    }

    //先拷贝音乐，然后获得音乐路径
    public static String getMusicPath(String originPath) {
        String outPath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FILE_PATH;
        File file = new File(outPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File music = new File(outPath, "music.mp3");
        if (music.exists()) {
            music.delete();
        }
        String absolutePath = music.getAbsolutePath();
        FileUtils.fileCopy(originPath, absolutePath);
        return absolutePath;
    }

    //获得临时视频路径
    public static String getTempVideoPath(String originPath) {
        String outPath = Environment.getExternalStorageDirectory().getPath() + File.separator + APP_FILE_PATH;
        File file = new File(outPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File tempVideo = new File(outPath, "tempVideo.mp4");
        if (tempVideo.exists()) {
            tempVideo.delete();
        }
        String absolutePath = tempVideo.getAbsolutePath();
        FileUtils.fileCopy(originPath, absolutePath);
        return absolutePath;
    }

    public static boolean isActivityAlive(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            return true;
        }
        return false;
    }

    // 将毫秒转为分钟：秒
    public static String millsecondsToMinuteSecondStr(long ms) {
        int seconds = (int) (ms / 1000);
        String result = "";
        int min = 0, second = 0;
        min = seconds / 60;
        second = seconds - min * 60;

        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath_above19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");

                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return getRealStoragePath(context, "/" + split[1]);
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(DOWNLOAD_CONTENT), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    //反射循环判断文件的真实路径
    public static String getRealStoragePath(Context context, String pathTail) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object result = getVolumePathsMethod.invoke(sm);

            if (result != null && result instanceof String[]) {
                String[] paths = (String[]) result;
                for (String path : paths) {
                    if (new File(path + pathTail).exists()) {
                        return path + pathTail;
                    }
                }
            }

        } catch (Exception e) {
            Log.e("demo", "getSecondaryStoragePath() failed", e);
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * API19以下获取图片路径的方法
     *
     * @param uri
     */
    public static String getFilePath_below19(Context context, Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

}
