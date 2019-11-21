package bokecc.shortvideosdk.editvideo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bokecc.camerafilter.gpufilter.filter.BeautySkinAdjust;
import com.bokecc.camerafilter.gpufilter.filter.BeautyWhiteAdjust;
import com.bokecc.camerafilter.gpufilter.helper.MagicFilterType;
import com.bokecc.camerafilter.mediacodec.VideoClipper;
import com.bokecc.shortvideo.model.MusicSet;
import com.bokecc.shortvideo.model.StickerSet;
import com.bokecc.shortvideo.videoedit.HandleProcessListener;
import com.bokecc.shortvideo.videoedit.ShortVideoHelper;

import java.io.IOException;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.util.MultiUtils;
import bokecc.shortvideosdk.widget.CustomProgressDialog;

public class UploadActivity extends Activity {

    private String videoPath, bacMusicPath;
    private int filterPosition = 0, skinValue = 0, whiteValue = 0, handleTimes = 0,
            startCutMusicPosition;
    private float beautySkinValue, beautyWhiteValue, originVolume, musicVolume;
    private int videoDuration;
    private CustomProgressDialog handleProgressDialog;
    private MagicFilterType filterType = MagicFilterType.NONE;
    private Activity activity;
    private boolean isAddFilter = false, isBeautySkin = false, isBeautyWhite = false, isAddMusic = false, isAddSticker = false;
    private TextView tv_handle_success;
    private List<StickerSet> stickerSets;
    private ImageView iv_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        activity = this;
        iv_back = findViewById(R.id.iv_back);
        tv_handle_success = findViewById(R.id.tv_handle_success);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPath = getIntent().getStringExtra("videoPath");
        filterPosition = getIntent().getIntExtra("filterPosition", 0);
        skinValue = getIntent().getIntExtra("skinValue", 0);
        whiteValue = getIntent().getIntExtra("whiteValue", 0);
        beautySkinValue = getIntent().getFloatExtra("beautySkinValue", 0.0f);
        beautyWhiteValue = getIntent().getFloatExtra("beautyWhiteValue", 0.0f);
        videoDuration = getIntent().getIntExtra("videoDuration", 0);
        bacMusicPath = getIntent().getStringExtra("bacMusicPath");
        startCutMusicPosition = getIntent().getIntExtra("startCutMusicPosition", 0);
        originVolume = getIntent().getFloatExtra("originVolume", 0.0f);
        musicVolume = getIntent().getFloatExtra("musicVolume", 0.0f);
        stickerSets = (List<StickerSet>) getIntent().getSerializableExtra("stickerSets");
        videoDuration = getIntent().getIntExtra("videoDuration", 0);

        if (filterPosition > 0) {
            isAddFilter = true;
        }

        if (skinValue > 0) {
            isBeautySkin = true;
        }

        if (whiteValue > 0) {
            isBeautyWhite = true;
        }

        if (!TextUtils.isEmpty(bacMusicPath)) {
            isAddMusic = true;
        }

        if (stickerSets.size() > 0) {
            isAddSticker = true;
        }


        handleProgressDialog = new CustomProgressDialog(activity);
        makeVideo();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void makeVideo() {
        handleProgressDialog.show();

        if (isBeautyWhite || isBeautySkin || isAddFilter) {
            addFilter();
        } else {
            if (isAddMusic || isAddSticker) {
                addMusicAndSticker();
            } else {
                handleVideoSuccess();
            }
        }
    }

    private void addFilter() {
        //添加滤镜
        getFilterType();
        VideoClipper addFilter = new VideoClipper();
        addFilter.setInputVideoPath(videoPath);
        String outputFilterPath = MultiUtils.getOutPutVideoPath();
        if (isBeautyWhite) {
            BeautyWhiteAdjust beautyWhiteAdjust = new BeautyWhiteAdjust(beautyWhiteValue);
            addFilter.setBeautyWhiteAdjust(beautyWhiteAdjust);
        }
        if (isBeautySkin) {
            BeautySkinAdjust beautySkinAdjust = new BeautySkinAdjust(beautySkinValue);
            addFilter.setBeautySkinAdjust(beautySkinAdjust);
        }
        if (isAddFilter) {
            addFilter.setFilterType(filterType);
        }
        addFilter.setOutputVideoPath(outputFilterPath);
        try {
            addFilter.clipVideo(0, videoDuration * 1000);
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleVideoFail();
                }
            });
        }
        addFilter.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
            @Override
            public void onFinish() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (handleTimes > 0) {
                            MultiUtils.deleteFile(videoPath);
                        }
                        videoPath = outputFilterPath;
                        handleTimes++;
                        if (isAddMusic || isAddSticker) {
                            addMusicAndSticker();
                        } else {
                            handleVideoSuccess();
                        }

                    }
                });
            }
        });
    }

    //添加音乐和贴纸
    private void addMusicAndSticker() {
        String outPutAddStickerPath = MultiUtils.getOutPutVideoPath();
        MusicSet musicSet = null;
        if (isAddMusic) {
            musicSet = new MusicSet();
            int startMusicPos = startCutMusicPosition / 1000;
            musicSet.setMusicPath(bacMusicPath);
            musicSet.setStartMusicPos(startMusicPos);
            musicSet.setOriginVolume(originVolume);
            musicSet.setMusicVolume(musicVolume);
        }
        ShortVideoHelper.AddMusicAndSticker(activity, videoPath, outPutAddStickerPath, musicSet, stickerSets, new HandleProcessListener() {
            @Override
            public void onStart() {

            }


            @Override
            public void onFinish() {

                if (handleTimes > 0) {
                    MultiUtils.deleteFile(videoPath);
                }

                videoPath = outPutAddStickerPath;
                handleTimes++;
                handleVideoSuccess();

                if (stickerSets.size() > 0) {
                    for (int i = 0; i < stickerSets.size(); i++) {
                        MultiUtils.deleteFile(stickerSets.get(i).getStickerPath());
                    }
                }

            }

            @Override
            public void onFail(int errorCode, String msg) {
                handleVideoFail();
            }
        });
    }

    private void handleVideoFail() {
        handleProgressDialog.dismiss();
        MultiUtils.showToast(activity, "处理失败");
    }

    private void handleVideoSuccess() {

        handleProgressDialog.dismiss();
        MultiUtils.insertMp4ToGallery(activity, videoPath);
        MultiUtils.showToast(activity, "处理成功");
        tv_handle_success.setVisibility(View.VISIBLE);
    }

    private void getFilterType() {
        if (filterPosition == 0) {
            filterType = MagicFilterType.NONE;
        } else if (filterPosition == 1) {
            filterType = MagicFilterType.COOL;
        } else if (filterPosition == 2) {
            filterType = MagicFilterType.ANTIQUE;
        } else if (filterPosition == 3) {
            filterType = MagicFilterType.HUDSON;
        } else if (filterPosition == 4) {
            filterType = MagicFilterType.HEFE;
        } else if (filterPosition == 5) {
            filterType = MagicFilterType.BRANNAN;
        }
    }
}
