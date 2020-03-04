package bokecc.shortvideosdk.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;
import com.bokecc.shortvideo.combineimages.model.SelectVideoInfo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.ShortVideoApplication;
import bokecc.shortvideosdk.cutvideo.VideoBean;
import bokecc.shortvideosdk.model.AnimRes;
import bokecc.shortvideosdk.model.MusicInfo;
import bokecc.shortvideosdk.model.TransitionRes;

public class MultiUtils {

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

    //显示图片
    public static void loadUriImage(Uri uri, ImageView imageView) {
        if (uri != null) {
            Glide.with(ShortVideoApplication.getContext()).load(uri).into(imageView);
        }
    }

    //显示图片
    public static void loadPathImage(String path, ImageView imageView) {
        if (!TextUtils.isEmpty(path)) {
            Glide.with(ShortVideoApplication.getContext()).load(path).into(imageView);
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


    //设置为全屏
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //将dp转化为sp
    public static int dipToPx(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    //得到file
    public static File getNewFile(Context context, String folderName, String stickerName) {
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

    public static float calTimeRatio(int scale, float num1, float num2) {
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

        File outPutVideo = new File(outPath, System.currentTimeMillis() + ".mp4");
        if (outPutVideo.exists()) {
            outPutVideo.delete();
        }

        return outPutVideo.getAbsolutePath();
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

    //获得图片的宽度
    public static int getImageWidth(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        int width = options.outWidth;
        return width;
    }

    //获得图片的高度
    public static int getImageHeight(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
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
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
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

    //得到本地视频数据
    public static List<SelectVideoInfo> getVideoDatas(Context context) {
        List<SelectVideoInfo> datas = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Video.VideoColumns.DATE_MODIFIED);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SelectVideoInfo selectVideoInfo = new SelectVideoInfo();
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Uri cover = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                selectVideoInfo.setCover(cover);
                int videoTime = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                selectVideoInfo.setVideoTime(videoTime);
                int videoSize = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                selectVideoInfo.setVideoSize(videoSize);
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                selectVideoInfo.setPath(path);
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
                selectVideoInfo.setDate(date);
                selectVideoInfo.setSelected(false);
                datas.add(selectVideoInfo);
            }
            cursor.close();
        }
        if (datas.size() > 0) {
            Collections.sort(datas, new Comparator<SelectVideoInfo>() {
                @Override
                public int compare(SelectVideoInfo o1, SelectVideoInfo o2) {
                    int rank = (int) (o2.getDate() - o1.getDate());
                    return rank;
                }
            });
        }
        return datas;
    }

    //得到本地图片数据
    public static List<SelectImageInfo> getImageDatas(Context context) {
        List<SelectImageInfo> datas = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Images.ImageColumns.DATE_MODIFIED);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SelectImageInfo selectImageInfo = new SelectImageInfo();
                int imageSize = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                selectImageInfo.setSize(imageSize);
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                selectImageInfo.setPath(path);
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
                selectImageInfo.setDate(date);
                selectImageInfo.setSelected(false);
                datas.add(selectImageInfo);
            }
            cursor.close();
        }
        if (datas.size() > 0) {
            Collections.sort(datas, new Comparator<SelectImageInfo>() {
                @Override
                public int compare(SelectImageInfo o1, SelectImageInfo o2) {
                    int rank = (int) (o2.getDate() - o1.getDate());
                    return rank;
                }
            });
        }
        return datas;
    }

    public static List<TransitionRes> getTransitionResDatas() {
        List<TransitionRes> datas = new ArrayList<>();
        TransitionRes transitionRes0 = new TransitionRes();
        transitionRes0.setNormalImgRes(R.mipmap.iv_no_transition_normal);
        transitionRes0.setSelectdImgRes(R.mipmap.iv_no_transition_selected);
        transitionRes0.setSelected(true);
        transitionRes0.setTransitionName("无");
        transitionRes0.setTransitionType(0);
        datas.add(transitionRes0);

        TransitionRes transitionRes1 = new TransitionRes();
        transitionRes1.setNormalImgRes(R.mipmap.iv_overlap_normal);
        transitionRes1.setSelectdImgRes(R.mipmap.iv_overlap_selected);
        transitionRes1.setSelected(false);
        transitionRes1.setTransitionName("重叠");
        transitionRes1.setTransitionType(1);
        datas.add(transitionRes1);

        TransitionRes transitionRes2 = new TransitionRes();
        transitionRes2.setNormalImgRes(R.mipmap.iv_flash_black_normal);
        transitionRes2.setSelectdImgRes(R.mipmap.iv_flash_black_selected);
        transitionRes2.setSelected(false);
        transitionRes2.setTransitionName("闪黑");
        transitionRes2.setTransitionType(2);
        datas.add(transitionRes2);

        TransitionRes transitionRes3 = new TransitionRes();
        transitionRes3.setNormalImgRes(R.mipmap.iv_flash_white_normal);
        transitionRes3.setSelectdImgRes(R.mipmap.iv_flash_white_selected);
        transitionRes3.setSelected(false);
        transitionRes3.setTransitionName("闪白");
        transitionRes3.setTransitionType(3);
        datas.add(transitionRes3);

        TransitionRes transitionRes4 = new TransitionRes();
        transitionRes4.setNormalImgRes(R.mipmap.iv_circle_normal);
        transitionRes4.setSelectdImgRes(R.mipmap.iv_circle_selected);
        transitionRes4.setSelected(false);
        transitionRes4.setTransitionName("圆形");
        transitionRes4.setTransitionType(4);
        datas.add(transitionRes4);
        return datas;
    }

    public static List<AnimRes> getAnimResDatas() {
        List<AnimRes> datas = new ArrayList<>();
        AnimRes animRes0 = new AnimRes();
        animRes0.setNormalImgRes(R.mipmap.iv_no_transition_normal);
        animRes0.setSelectdImgRes(R.mipmap.iv_no_transition_selected);
        animRes0.setSelected(true);
        animRes0.setAnimName("无");
        animRes0.setAnimType(0);
        datas.add(animRes0);

        AnimRes animRes1 = new AnimRes();
        animRes1.setNormalImgRes(R.mipmap.iv_zoom_big_normal);
        animRes1.setSelectdImgRes(R.mipmap.iv_zoom_big_selected);
        animRes1.setSelected(false);
        animRes1.setAnimName("放大");
        animRes1.setAnimType(1);
        datas.add(animRes1);

        AnimRes animRes2 = new AnimRes();
        animRes2.setNormalImgRes(R.mipmap.iv_zoom_small_normal);
        animRes2.setSelectdImgRes(R.mipmap.iv_zoom_small_selected);
        animRes2.setSelected(false);
        animRes2.setAnimName("缩小");
        animRes2.setAnimType(2);
        datas.add(animRes2);

        AnimRes animRes3 = new AnimRes();
        animRes3.setNormalImgRes(R.mipmap.iv_to_left_normal);
        animRes3.setSelectdImgRes(R.mipmap.iv_to_left_selected);
        animRes3.setSelected(false);
        animRes3.setAnimName("左滑");
        animRes3.setAnimType(3);
        datas.add(animRes3);

        AnimRes animRes4 = new AnimRes();
        animRes4.setNormalImgRes(R.mipmap.iv_to_right_normal);
        animRes4.setSelectdImgRes(R.mipmap.iv_to_right_selected);
        animRes4.setSelected(false);
        animRes4.setAnimName("右滑");
        animRes4.setAnimType(4);
        datas.add(animRes4);
        return datas;
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

}
