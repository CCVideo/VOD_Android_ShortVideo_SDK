package bokecc.shortvideosdk.editvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bokecc.shortvideo.specialeffect.DecoderOutputSurface;
import com.bokecc.shortvideo.specialeffect.EncoderSurface;
import com.bokecc.shortvideo.specialeffect.EffectConfig;
import com.bokecc.shortvideo.specialeffect.EffectList;
import com.bokecc.shortvideo.specialeffect.EffectPeriod;
import com.bokecc.shortvideo.specialeffect.Resolution;
import com.bokecc.shortvideo.specialeffect.composer.Mp4Composer;
import com.bokecc.shortvideo.specialeffect.effect.BaseEffect;
import com.bokecc.shortvideo.specialeffect.effect.EffectType;
import com.bokecc.shortvideo.specialeffect.effect.FlashEffect;
import com.bokecc.shortvideo.specialeffect.effect.IllusionEffect;
import com.bokecc.shortvideo.specialeffect.effect.BurrEffect;
import com.bokecc.shortvideo.specialeffect.effect.ScaleEffect;
import com.bokecc.shortvideo.specialeffect.effect.ShakeEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.EffectImagesPreviewAdapter;
import bokecc.shortvideosdk.adapter.SpecialEffectAdapter;
import bokecc.shortvideosdk.model.SpecialEffectInfo;
import bokecc.shortvideosdk.model.SpecialEffectRes;
import bokecc.shortvideosdk.util.MultiUtils;
import bokecc.shortvideosdk.util.OnBitmapGetListener;
import bokecc.shortvideosdk.widget.CustomProgressDialog;

public class EditSpecialEffectActivity extends Activity implements
        TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, View.OnClickListener {

    private Activity activity;
    private RecyclerView rv_special_effect, rv_preview;
    private List<SpecialEffectRes> specialEffectResDatas;
    private List<Bitmap> bitmapDatas;
    private SpecialEffectAdapter specialEffectAdapter;
    private EffectImagesPreviewAdapter effectImagesPreviewAdapter;
    private TextureView tv_video;
    private LinearLayout ll_video;
    protected MediaPlayer mediaPlayer;
    private String videoPath;
    private SurfaceTexture surfaceTexture;
    private int duration;
    private EffectList effectList = null;
    private String outPath;
    private List<EffectConfig> effectConfigList;
    private EncoderSurface encoderSurface;
    private DecoderOutputSurface decoderSurface;
    protected long currentPostion = 0L;
    private volatile boolean running = false;
    protected volatile boolean notDestroyed = true;
    private int textureViewWidth, textureViewHeight;
    private Timer timer;
    private VideoTask videoTask;
    private ImageView iv_confirm_effect, iv_close_effect, iv_revoke;
    private SeekBar sb_play_progress;
    private boolean isPrepared = false;
    private long effectStartTime = 0, effectEndTime = 0;
    private EffectPeriod effectPeriod;
    private RelativeLayout rl_preview;
    private boolean isAddEffect = false;
    private ProgressBar currentPb;
    private List<ProgressBar> progressBars;
    private ArrayList<SpecialEffectInfo> effects;
    private int rvWidth, bitmapWidth;
    private boolean isRevoke = false, isEditEffect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_special_effect);
        activity = this;
        initView();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        outPath = MultiUtils.getEffectOutputVideoPath();

        effectList = new EffectList();
        effectConfigList = new ArrayList<>();

        videoPath = getIntent().getStringExtra("videoPath");
        effects = getIntent().getParcelableArrayListExtra("effects");
        if (effects != null && effects.size() > 0) {
            initEffects();
        }
        if (effects == null) {
            effects = new ArrayList<>();
        }
        progressBars = new ArrayList<>();

        ll_video = findViewById(R.id.ll_video);
        tv_video = findViewById(R.id.tv_video);
        tv_video.setSurfaceTextureListener(this);
        tv_video.setOnClickListener(this);

        iv_confirm_effect = findViewById(R.id.iv_confirm_effect);
        iv_confirm_effect.setOnClickListener(this);

        iv_close_effect = findViewById(R.id.iv_close_effect);
        iv_close_effect.setOnClickListener(this);

        iv_revoke = findViewById(R.id.iv_revoke);
        iv_revoke.setOnClickListener(this);

        rl_preview = findViewById(R.id.rl_preview);
        rv_special_effect = findViewById(R.id.rv_special_effect);
        specialEffectResDatas = MultiUtils.getSpecialEffectResDatas();
        LinearLayoutManager layoutManagerEffect = new LinearLayoutManager(activity);
        layoutManagerEffect.setOrientation(OrientationHelper.HORIZONTAL);
        rv_special_effect.setLayoutManager(layoutManagerEffect);
        specialEffectAdapter = new SpecialEffectAdapter(specialEffectResDatas);
        rv_special_effect.setAdapter(specialEffectAdapter);

        rv_preview = findViewById(R.id.rv_preview);
        bitmapDatas = new ArrayList<>();
        LinearLayoutManager layoutManagerPreview = new LinearLayoutManager(activity);
        layoutManagerPreview.setOrientation(OrientationHelper.HORIZONTAL);
        rv_preview.setLayoutManager(layoutManagerPreview);
        effectImagesPreviewAdapter = new EffectImagesPreviewAdapter(bitmapDatas);
        rv_preview.setAdapter(effectImagesPreviewAdapter);

        bitmapWidth = MultiUtils.dipToPx(activity, 40);
        rvWidth = MultiUtils.getScreenWidth(activity) - MultiUtils.dipToPx(activity, 30);
        int bitmapCount = rvWidth / bitmapWidth;
        MultiUtils.getLocalVideoBitmap(videoPath, bitmapCount, 120, 120, new OnBitmapGetListener() {
            @Override
            public void onBitmapGet(Bitmap bitmap) {
                bitmapDatas.add(bitmap);
                if (bitmapDatas.size() > 0) {
                    effectImagesPreviewAdapter.notifyItemInserted(bitmapDatas.size() - 1);
                }
            }
        });

        sb_play_progress = findViewById(R.id.sb_play_progress);
        sb_play_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mediaPlayer.isPlaying()) {
                    if (isPrepared) {
                        mediaPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (isPrepared) {
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.start();
                }
            }
        });

        specialEffectAdapter.setOnOnTouchListener(new SpecialEffectAdapter.OnOnTouchListener() {
            @Override
            public void onActionDown(SpecialEffectRes item, int position) {
                if (!isPrepared) {
                    return;
                }
                isAddEffect = true;
                effectStartTime = currentPostion;
                ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(rvWidth, bitmapWidth);
                if (duration > 0) {
                    int pbLeftMargin = (int) (effectStartTime * rvWidth / duration);
                    params.leftMargin = pbLeftMargin;
                }
                progressBar.setLayoutParams(params);
                progressBar.setMax((int) (duration - effectStartTime));

                if (position == 0) {
                    effectPeriod = addEffect(effectStartTime, duration, new ShakeEffect(activity));
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_shake));
                } else if (position == 1) {
                    effectPeriod = addEffect(effectStartTime, duration, new FlashEffect(activity));
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_flash));
                } else if (position == 2) {
                    effectPeriod = addEffect(effectStartTime, duration, new IllusionEffect(activity));
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_huanjue));
                } else if (position == 3) {
                    effectPeriod = addEffect(effectStartTime, duration, new BurrEffect(activity));
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_itch));
                } else if (position == 4) {
                    effectPeriod = addEffect(effectStartTime, duration, new ScaleEffect(activity));
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_scale));
                }

                currentPb = progressBar;
                progressBars.add(progressBar);
                rl_preview.addView(progressBar);
            }

            @Override
            public void onActionUp(SpecialEffectRes item, int position) {
                if (!isPrepared) {
                    return;
                }
                isAddEffect = false;
                isEditEffect = true;
                effectEndTime = currentPostion;
                effectPeriod.endTime = effectEndTime;

                SpecialEffectInfo specialEffectInfo = new SpecialEffectInfo();
                specialEffectInfo.setStartTime(effectPeriod.startTime);
                specialEffectInfo.setEndTime(effectPeriod.endTime);
                if (position == 0) {
                    specialEffectInfo.setEffectType(0);
                    effectConfigList.add(new EffectConfig(EffectType.SHAKE, effectPeriod.startTime, effectPeriod.endTime));
                } else if (position == 1) {
                    specialEffectInfo.setEffectType(1);
                    effectConfigList.add(new EffectConfig(EffectType.FLASH, effectPeriod.startTime, effectPeriod.endTime));
                } else if (position == 2) {
                    specialEffectInfo.setEffectType(2);
                    effectConfigList.add(new EffectConfig(EffectType.ILLUSION, effectPeriod.startTime, effectPeriod.endTime));
                } else if (position == 3) {
                    specialEffectInfo.setEffectType(3);
                    effectConfigList.add(new EffectConfig(EffectType.BURR, effectPeriod.startTime, effectPeriod.endTime));
                } else if (position == 4) {
                    specialEffectInfo.setEffectType(4);
                    effectConfigList.add(new EffectConfig(EffectType.SCALE, effectPeriod.startTime, effectPeriod.endTime));
                }
                effects.add(specialEffectInfo);
            }
        });


    }

    private void initEffects() {
        for (SpecialEffectInfo specialEffectInfo : effects) {
            int effectType = specialEffectInfo.getEffectType();
            long startTime = specialEffectInfo.getStartTime();
            long endTime = specialEffectInfo.getEndTime();
            if (effectType == 0) {
                EffectPeriod period = new EffectPeriod(startTime, endTime, new ShakeEffect(activity));
                effectList.putEffect(period);
                effectConfigList.add(new EffectConfig(EffectType.SHAKE, startTime, endTime));
            } else if (effectType == 1) {
                EffectPeriod period = new EffectPeriod(startTime, endTime, new FlashEffect(activity));
                effectList.putEffect(period);
                effectConfigList.add(new EffectConfig(EffectType.FLASH, startTime, endTime));
            } else if (effectType == 2) {
                EffectPeriod period = new EffectPeriod(startTime, endTime, new IllusionEffect(activity));
                effectList.putEffect(period);
                effectConfigList.add(new EffectConfig(EffectType.ILLUSION, startTime, endTime));
            } else if (effectType == 3) {
                EffectPeriod period = new EffectPeriod(startTime, endTime, new BurrEffect(activity));
                effectList.putEffect(period);
                effectConfigList.add(new EffectConfig(EffectType.BURR, startTime, endTime));
            } else if (effectType == 4) {
                EffectPeriod period = new EffectPeriod(startTime, endTime, new ScaleEffect(activity));
                effectList.putEffect(period);
                effectConfigList.add(new EffectConfig(EffectType.SCALE, startTime, endTime));
            }
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setLooping(true);
            try {
                mediaPlayer.setDataSource(videoPath);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        surfaceTexture = surface;
        initMediaPlayer();
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void poll() {
        while (notDestroyed) {
            if (running) {
                try {
                    decoderSurface.awaitNewImage();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
                decoderSurface.drawImage(currentPostion * 1000 * 1000);
                encoderSurface.setPresentationTime(System.currentTimeMillis());
                encoderSurface.swapBuffers();
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        isPrepared = true;
        running = true;
        setVideoSize();
        startVideoTimer();

        duration = mediaPlayer.getDuration();
        sb_play_progress.setMax(duration);

        if (effects != null && effects.size() > 0) {
            for (SpecialEffectInfo specialEffectInfo : effects) {
                int effectType = specialEffectInfo.getEffectType();
                long startTime = specialEffectInfo.getStartTime();
                long endTime = specialEffectInfo.getEndTime();
                ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(rvWidth, bitmapWidth);
                if (duration > 0) {
                    int pbLeftMargin = (int) (startTime * rvWidth / duration);
                    params.leftMargin = pbLeftMargin;
                }
                progressBar.setLayoutParams(params);
                if (effectType == 0) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_shake));
                } else if (effectType == 1) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_flash));
                } else if (effectType == 2) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_huanjue));
                } else if (effectType == 3) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_itch));
                } else if (effectType == 4) {
                    progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_scale));
                }

                progressBar.setMax((int) (duration - startTime));
                long progress = endTime - startTime;
                if (progress > 0) {
                    progressBar.setProgress((int) progress);
                }
                rl_preview.addView(progressBar);
            }
        }

    }

    public EffectPeriod addEffect(long startTime, long endTime, BaseEffect baseEffect) {
        EffectPeriod period = new EffectPeriod(startTime, endTime, baseEffect);
        effectList.putEffect(period);
        return period;
    }

    private void setVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        int width = ll_video.getWidth();
        int height = ll_video.getHeight();

        if (videoHeight > videoWidth) {
            textureViewHeight = height;
            textureViewWidth = textureViewHeight * videoWidth / videoHeight;
        } else {
            textureViewWidth = width;
            textureViewHeight = textureViewWidth * videoHeight / videoWidth;
        }

        ViewGroup.LayoutParams tvParams = tv_video.getLayoutParams();
        tvParams.height = textureViewHeight;
        tvParams.width = textureViewWidth;
        tv_video.setLayoutParams(tvParams);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                encoderSurface = new EncoderSurface(new Surface(surfaceTexture));
                encoderSurface.makeCurrent();
                decoderSurface = new DecoderOutputSurface(new BaseEffect(), effectList);
                decoderSurface.setOutputResolution(new Resolution(textureViewWidth, textureViewHeight));
                decoderSurface.setInputResolution(new Resolution(textureViewWidth, textureViewHeight));
                decoderSurface.setupAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        while (decoderSurface.getSurface() == null) {
                            try {
                                Thread.sleep(30);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        Surface surface = decoderSurface.getSurface();
                        mediaPlayer.setSurface(surface);
                    }
                });
                poll();

            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_confirm_effect:
                if (!isEditEffect && !isRevoke) {
                    finish();
                } else {
                    applyEffect();
                }
                break;

            case R.id.tv_video:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                break;
            case R.id.iv_close_effect:
                if (isRevoke) {
                    applyEffect();
                } else {
                    finish();
                }
                break;

            case R.id.iv_revoke:
                if (effects != null && effects.size() > 0) {
                    effects.remove(effects.size() - 1);
                    effectList.removeEffect();
                    int childCount = rl_preview.getChildCount();
                    if (childCount > 1) {
                        rl_preview.removeViewAt(childCount - 1);
                    }
                    isRevoke = true;
                }

                if (effectConfigList != null && effectConfigList.size() > 0) {
                    effectConfigList.remove(effectConfigList.size() - 1);
                }
                break;
        }
    }

    private void applyEffect() {
        EffectList outPutEffectList = new EffectList();
        for (EffectConfig effectConfig : effectConfigList) {
            outPutEffectList.putEffect(new EffectPeriod(effectConfig.startTime, effectConfig.endTime, EffectType.createEffect(effectConfig.effectType, null, this)));
        }
        CustomProgressDialog handleProgressDialog = new CustomProgressDialog(activity);
        handleProgressDialog.show();
        new Mp4Composer(videoPath, outPath).frameRate(30).effectList(outPutEffectList).listener(new Mp4Composer.Listener() {
            @Override
            public void onProgress(double progress) {

            }

            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleProgressDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("path", outPath);
                        intent.putParcelableArrayListExtra("effects", effects);
                        activity.setResult(-1, intent);
                        activity.finish();
                    }
                });
            }

            @Override
            public void onCanceled() {

            }

            @Override
            public void onFailed(Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleProgressDialog.dismiss();
                        MultiUtils.showToast(activity, "处理失败");
                    }
                });
            }
        }).start();
    }

    //开启更新播放进度任务
    private void startVideoTimer() {
        cancelVideoTimer();
        timer = new Timer();
        videoTask = new VideoTask();
        timer.schedule(videoTask, 0, 10);
    }

    //取消更新播放进度任务
    private void cancelVideoTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (videoTask != null) {
            videoTask.cancel();
        }
    }

    // 播放进度计时器
    class VideoTask extends TimerTask {
        @Override
        public void run() {
            if (MultiUtils.isActivityAlive(activity)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPostion = mediaPlayer.getCurrentPosition();
                        sb_play_progress.setProgress((int) currentPostion);
                        if (isAddEffect) {
                            long efffectProgress = currentPostion - effectStartTime;
                            if (efffectProgress > 0 && currentPb != null) {
                                currentPb.setProgress((int) efffectProgress);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        notDestroyed = false;
        running = false;
        decoderSurface.stopRun();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        cancelVideoTimer();
    }

    @Override
    public void onBackPressed() {
        if (isRevoke) {
            applyEffect();
        } else {
            super.onBackPressed();
        }
    }
}
