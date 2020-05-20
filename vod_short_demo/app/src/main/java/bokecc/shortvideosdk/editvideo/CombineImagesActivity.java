package bokecc.shortvideosdk.editvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.shortvideo.combineimages.ImagesToVideoHelper;
import com.bokecc.shortvideo.combineimages.ImagesVideo;
import com.bokecc.shortvideo.combineimages.ImagesVideoPlayer;
import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;
import com.bokecc.shortvideo.combineimages.render.SurfaceImagesVideoRenderer;
import com.bokecc.shortvideo.combineimages.render.TextureImagesVideoRender;
import com.bokecc.shortvideo.combineimages.render.IVTextureView;
import com.bokecc.shortvideo.combineimages.timer.IVideoTimer;
import com.bokecc.shortvideo.combineimages.videorecord.ImagesVideoMaker;

import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.AnimAdapter;
import bokecc.shortvideosdk.adapter.ImagesPreviewAdapter;
import bokecc.shortvideosdk.adapter.TransitionAdapter;
import bokecc.shortvideosdk.model.AnimRes;
import bokecc.shortvideosdk.model.TransitionRes;
import bokecc.shortvideosdk.util.MultiUtils;
import bokecc.shortvideosdk.widget.CustomProgressDialog;


public class CombineImagesActivity extends Activity implements IVideoTimer.ImagesVideoListener, View.OnClickListener {
    private Activity activity;
    private ArrayList<SelectImageInfo> selectedImages;
    private int imagePos = 0;
    private IVTextureView ivTextureView;
    private SurfaceImagesVideoRenderer surfaceImagesVideoRenderer;
    private ImagesVideoPlayer imagesVideoPlayer;
    private ImagesVideo imagesVideo;
    private TextView tv_play_time, tv_total_time, tv_transition_time, tv_anim_time;
    private SeekBar sb_tran_time, sb_anim_time;
    private String playTime, totalTime;
    private int duration, imagesPreviewWidth, xScrollDistance, lastScrollPos, currentScrollPos;
    private RecyclerView rv_images_preview, rv_transaction, rv_anim;
    private ImagesPreviewAdapter imagesPreviewAdapter;
    private TransitionAdapter transitionAdapter;
    private AnimAdapter animAdapter;
    private List<TransitionRes> transitionResDatas;
    private List<AnimRes> animResDatas;
    private TextView tv_next;
    private int addTransitonPos = 0, addAnimPos = 0, addTransitonType = 0, addAnimType = 0;
    private int currentPlayPos = 0, totalScollDistance, startScrollPlayPos = 0;
    private boolean isMoveImagePreview = true;
    private LinearLayout ll_add_transition, ll_add_anim, ll_transition_time, ll_apply_to_all_transition, ll_apply_anim_to_all_images;
    private ImageView iv_back;
    //单位毫秒
    private final int DEFAULT_TRANSITION_TIME = 700;
    private float tranTimeSecond, imageTimeSecond;

    private LinearLayout ll_ivtv;
    private int currentTransitionTime = 700, currentImageTime = 3000, previewPos = 0;
    private boolean isPlayImages = true;
    private boolean isPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_images);
        activity = this;
        MultiUtils.setFullScreen(this);

        selectedImages = getIntent().getParcelableArrayListExtra("images");
        initView();
        //初始化图片播放器
        initImagesPlayer();

        if (selectedImages != null && selectedImages.size() > 0) {
            for (int i = 0; i < selectedImages.size(); i++) {
                selectedImages.get(i).setTransitionType(0);
                selectedImages.get(i).setImageTime(3000);
                selectedImages.get(i).setTransitionTime(0);
                selectedImages.get(i).setSelected(false);
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        rv_images_preview.setLayoutManager(layoutManager);
        imagesPreviewAdapter = new ImagesPreviewAdapter(selectedImages);
        rv_images_preview.setAdapter(imagesPreviewAdapter);

        //显示转场列表
        transitionResDatas = MultiUtils.getTransitionResDatas();
        LinearLayoutManager layoutManagerTransition = new LinearLayoutManager(activity);
        layoutManagerTransition.setOrientation(OrientationHelper.HORIZONTAL);
        rv_transaction.setLayoutManager(layoutManagerTransition);
        transitionAdapter = new TransitionAdapter(transitionResDatas);
        rv_transaction.setAdapter(transitionAdapter);


        //显示动效列表
        animResDatas = MultiUtils.getAnimResDatas();
        LinearLayoutManager layoutManagerAnim = new LinearLayoutManager(activity);
        layoutManagerAnim.setOrientation(OrientationHelper.HORIZONTAL);
        rv_anim.setLayoutManager(layoutManagerAnim);
        animAdapter = new AnimAdapter(animResDatas);
        rv_anim.setAdapter(animAdapter);

        imagesVideo = ImagesToVideoHelper.makeImagesVideo(activity, selectedImages);
        imagesVideoPlayer.setDataSource(imagesVideo);
        imagesVideoPlayer.prepare();

        //点击添加转场
        imagesPreviewAdapter.setOnAddTransitionClickListener(new ImagesPreviewAdapter.OnAddTransitionClickListener() {
            @Override
            public void onAddTransitionClick(SelectImageInfo item, int position) {
                ll_add_anim.setVisibility(View.GONE);
                ll_add_transition.setVisibility(View.VISIBLE);
                addTransitonPos = position;
                previewPos = getStartTimeByImagePos(addTransitonPos);

                for (SelectImageInfo selectImageInfo : selectedImages) {
                    selectImageInfo.setAddTransition(false);
                    selectImageInfo.setSelected(false);
                }
                selectedImages.get(position).setAddTransition(true);
                imagesPreviewAdapter.notifyDataSetChanged();

                //更新已选择的转场
                for (TransitionRes transitionRes : transitionResDatas) {
                    transitionRes.setSelected(false);
                }

                int transitionType = selectedImages.get(addTransitonPos).getTransitionType();
                if (transitionType == 0) {
                    ll_transition_time.setVisibility(View.INVISIBLE);
                } else {
                    ll_transition_time.setVisibility(View.VISIBLE);
                }
                if (transitionType == 0) {
                    transitionResDatas.get(0).setSelected(true);
                } else if (transitionType == 1) {
                    transitionResDatas.get(1).setSelected(true);
                } else if (transitionType == 2) {
                    transitionResDatas.get(2).setSelected(true);
                } else if (transitionType == 3) {
                    transitionResDatas.get(3).setSelected(true);
                } else if (transitionType == 4) {
                    transitionResDatas.get(4).setSelected(true);
                }
                transitionAdapter.notifyDataSetChanged();

                int transitionTime = selectedImages.get(addTransitonPos).getTransitionTime();
                currentTransitionTime = transitionTime;
                if (transitionTime > 0) {
                    tranTimeSecond = MultiUtils.calFloat(1, transitionTime, 1000);
                    tv_transition_time.setText(tranTimeSecond + "s");
                    sb_tran_time.setProgress(transitionTime);
                } else {
                    tv_transition_time.setText("0.0s");
                    sb_tran_time.setProgress(0);
                }
            }
        });

        //选择转场
        transitionAdapter.setOnItemClickListener(new TransitionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TransitionRes item, int position) {
                addTransitonType = position;
                for (TransitionRes transitionRes : transitionResDatas) {
                    transitionRes.setSelected(false);
                }
                transitionResDatas.get(position).setSelected(true);
                transitionAdapter.notifyDataSetChanged();
                if (position == 0) {
                    ll_transition_time.setVisibility(View.INVISIBLE);
                    tv_transition_time.setText("0.0s");
                    sb_tran_time.setProgress(0);
                    selectedImages.get(addTransitonPos).setTransitionTime(0);
                } else {
                    ll_transition_time.setVisibility(View.VISIBLE);
                    int transitionTime = selectedImages.get(addTransitonPos).getTransitionTime();
                    if (transitionTime > 0) {
                        tranTimeSecond = MultiUtils.calFloat(1, transitionTime, 1000);
                        tv_transition_time.setText(tranTimeSecond + "s");
                        selectedImages.get(addTransitonPos).setTransitionTime(transitionTime);
                    } else {
                        currentTransitionTime = DEFAULT_TRANSITION_TIME;
                        tv_transition_time.setText("0.7s");
                        sb_tran_time.setProgress(700);
                        selectedImages.get(addTransitonPos).setTransitionTime(DEFAULT_TRANSITION_TIME);
                    }

                }
                //更新预览图
                selectedImages.get(addTransitonPos).setTransitionType(position);
                imagesPreviewAdapter.notifyDataSetChanged();
                makeImagesVideo();

            }
        });

        //转场时长设置
        sb_tran_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tranTimeSecond = MultiUtils.calFloat(1, progress, 1000);
                tv_transition_time.setText(tranTimeSecond + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int transitionType = selectedImages.get(addTransitonPos).getTransitionType();
                if (transitionType != 0) {
                    currentTransitionTime = seekBar.getProgress();
                    selectedImages.get(addTransitonPos).setTransitionTime(seekBar.getProgress());
                    imagesPreviewAdapter.notifyDataSetChanged();

                    makeImagesVideo();
                }
            }
        });

        //点击添加动效
        imagesPreviewAdapter.setOnAddImageAnimClickListener(new ImagesPreviewAdapter.OnAddImageAnimClickListener() {
            @Override
            public void onAddImageAnimClick(SelectImageInfo item, int position) {
                ll_add_anim.setVisibility(View.VISIBLE);
                ll_add_transition.setVisibility(View.GONE);
                addAnimPos = position;
                previewPos = getStartTimeByImagePos(addAnimPos);

                for (SelectImageInfo selectImageInfo : selectedImages) {
                    selectImageInfo.setAddTransition(false);
                    selectImageInfo.setSelected(false);
                }

                selectedImages.get(position).setSelected(true);
                imagesPreviewAdapter.notifyDataSetChanged();

                //更新已选择的转场
                for (AnimRes animRes : animResDatas) {
                    animRes.setSelected(false);
                }

                int imageAnimType = selectedImages.get(addAnimPos).getImageAnimType();
                if (imageAnimType == 0) {
                    animResDatas.get(0).setSelected(true);
                } else if (imageAnimType == 1) {
                    animResDatas.get(1).setSelected(true);
                } else if (imageAnimType == 2) {
                    animResDatas.get(2).setSelected(true);
                } else if (imageAnimType == 3) {
                    animResDatas.get(3).setSelected(true);
                } else if (imageAnimType == 4) {
                    animResDatas.get(4).setSelected(true);
                }
                animAdapter.notifyDataSetChanged();

                int imageTime = selectedImages.get(addAnimPos).getImageTime();
                currentImageTime = imageTime;
                imageTimeSecond = MultiUtils.calFloat(1, imageTime, 1000);
                tv_anim_time.setText(imageTimeSecond + "s");
                sb_anim_time.setProgress((imageTime - 1000));
            }
        });

        //选择动效
        animAdapter.setOnItemClickListener(new AnimAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AnimRes item, int position) {
                addAnimType = position;
                for (AnimRes animRes : animResDatas) {
                    animRes.setSelected(false);
                }
                animResDatas.get(position).setSelected(true);
                animAdapter.notifyDataSetChanged();

                selectedImages.get(addAnimPos).setImageAnimType(position);

                imagesPreviewAdapter.notifyDataSetChanged();
                makeImagesVideo();
            }
        });

        //动效时长设置
        sb_anim_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    sb_anim_time.setProgress(1);
                    progress = 1;
                }
                imageTimeSecond = MultiUtils.calFloat(1, (progress + 1000), 1000);
                tv_anim_time.setText(imageTimeSecond + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                currentImageTime = 1000 + progress;
                if (progress < 1) {
                    progress = 1;
                }
                selectedImages.get(addAnimPos).setImageTime((1000 + progress));
                imagesPreviewAdapter.notifyDataSetChanged();
                makeImagesVideo();
            }
        });


        //滚动预览轴
        rv_images_preview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    imagesVideoPlayer.pause();
                    startScrollPlayPos = currentPlayPos;
                    isMoveImagePreview = false;
                }

                if (newState == 0) {

                    int absScollDistance = Math.abs(totalScollDistance);
                    float moveRate = MultiUtils.calTimeRatio(3, absScollDistance, imagesPreviewWidth);
                    int changeTime = (int) (moveRate * duration);
                    int newPlayPos = 0;
                    if (totalScollDistance > 0) {
                        newPlayPos = startScrollPlayPos + changeTime;
                    } else {
                        newPlayPos = startScrollPlayPos - changeTime;
                    }

                    if (newPlayPos < 0) {
                        newPlayPos = 0;
                    }
                    if (newPlayPos > duration) {
                        newPlayPos = duration;
                    }
                    lastScrollPos = (int) (MultiUtils.calFloat(5, newPlayPos * imagesPreviewWidth, duration));
                    imagesVideoPlayer.seekTo(newPlayPos);
                    imagesVideoPlayer.start();
                    isMoveImagePreview = true;
                    totalScollDistance = 0;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isMoveImagePreview) {
                    totalScollDistance = totalScollDistance + dx;
                }
            }
        });

    }

    private void initView() {
        ivTextureView = findViewById(R.id.iv_textureview);
        tv_play_time = findViewById(R.id.tv_play_time);
        tv_next = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);
        tv_total_time = findViewById(R.id.tv_total_time);
        tv_transition_time = findViewById(R.id.tv_transition_time);
        tv_anim_time = findViewById(R.id.tv_anim_time);
        rv_images_preview = findViewById(R.id.rv_images_preview);
        rv_transaction = findViewById(R.id.rv_transaction);
        rv_anim = findViewById(R.id.rv_anim);
        sb_tran_time = findViewById(R.id.sb_tran_time);
        sb_anim_time = findViewById(R.id.sb_anim_time);
        ll_add_transition = findViewById(R.id.ll_add_transition);
        ll_add_anim = findViewById(R.id.ll_add_anim);
        ll_transition_time = findViewById(R.id.ll_transition_time);
        ll_apply_to_all_transition = findViewById(R.id.ll_apply_to_all_transition);
        ll_apply_anim_to_all_images = findViewById(R.id.ll_apply_anim_to_all_images);
        ll_ivtv = findViewById(R.id.ll_ivtv);
        ll_ivtv.postDelayed(new Runnable() {
            @Override
            public void run() {
                int widthSpace = ll_ivtv.getWidth();
                int heightSpace = ll_ivtv.getHeight();
                SelectImageInfo selectImageInfo = selectedImages.get(0);
                String path = selectImageInfo.getPath();
                int imageWidth = MultiUtils.getImageWidth(path);
                int imageHeight = MultiUtils.getImageHeight(path);
                int gltvWidth = widthSpace;
                int gltvHeight = heightSpace;
                if (imageHeight > 0 && imageWidth > 0) {
                    if (imageHeight > imageWidth) {
                        gltvHeight = heightSpace;
                        gltvWidth = gltvHeight * imageWidth / imageHeight;
                    } else {
                        gltvWidth = widthSpace;
                        gltvHeight = gltvWidth * imageHeight / imageWidth;
                    }
                }

                ViewGroup.LayoutParams gltvParams = ivTextureView.getLayoutParams();
                gltvParams.height = gltvHeight;
                gltvParams.width = gltvWidth;
                ivTextureView.setLayoutParams(gltvParams);

            }
        }, 100);


        tv_next.setOnClickListener(this);
        ll_apply_to_all_transition.setOnClickListener(this);
        ll_apply_anim_to_all_images.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ivTextureView.setOnClickListener(this);
    }

    private void initImagesPlayer() {
        surfaceImagesVideoRenderer = new TextureImagesVideoRender(ivTextureView);
        imagesVideoPlayer = new ImagesVideoPlayer(activity);
        imagesVideoPlayer.setImagesVideoRenderer(surfaceImagesVideoRenderer);
        imagesVideoPlayer.setImagesVideoListener(this);
        imagesVideoPlayer.setLoop(true);
        imagesVideoPlayer.setOnPreparedListener(new ImagesVideoPlayer.OnPreparedListener() {
            @Override
            public void onPreparing(ImagesVideoPlayer imagesPlayer, float progress) {

            }

            @Override
            public void onPrepared(ImagesVideoPlayer imagesPlayer, int prepared, int total) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isPrepared = true;
                        View childAt1 = rv_images_preview.getLayoutManager().getChildAt(1);
                        if (childAt1 != null) {
                            imagesPreviewWidth = childAt1.getWidth() * selectedImages.size();
                        }
                        duration = imagesVideo.getDuration();
                        totalTime = MultiUtils.millsecondsToMinuteSecondStr(duration);
                        tv_total_time.setText(totalTime);
                        imagesVideoPlayer.seekTo(previewPos);
                        imagesVideoPlayer.start();
                    }
                });
            }

            @Override
            public void onError(ImagesVideoPlayer imagesPlayer) {

            }
        });
    }

    @Override
    public void onImagesVideoUpdate(int elapsedTime) {
        int timeByImagePos = getEndTimeByImagePos(imagePos);
        if (elapsedTime > timeByImagePos) {
            imagePos = imagePos + 1;
        }

        currentScrollPos = (int) (MultiUtils.calFloat(5, elapsedTime * imagesPreviewWidth, duration));
        xScrollDistance = currentScrollPos - lastScrollPos;
        rv_images_preview.scrollBy(xScrollDistance, 0);
        lastScrollPos = currentScrollPos;

        currentPlayPos = elapsedTime;
        playTime = MultiUtils.millsecondsToMinuteSecondStr(elapsedTime);
        tv_play_time.setText(playTime);
    }

    @Override
    public void onImagesVideoStarted() {

    }

    @Override
    public void onImagesVideoPaused() {

    }

    @Override
    public void onImagesVideoResumed() {

    }

    @Override
    public void onImagesVideoEnd() {
        imagePos = 0;
        currentScrollPos = 0;
        lastScrollPos = 0;
        rv_images_preview.scrollToPosition(0);
    }

    private int getEndTimeByImagePos(int imagePos) {
        int time = 0;
        for (int i = 0; i < (imagePos + 1); i++) {
            time = time + selectedImages.get(i).getImageTime() + selectedImages.get(i).getTransitionTime();
        }
        return time;
    }

    private int getStartTimeByImagePos(int imagePos) {
        int time = 0;
        for (int i = 0; i < imagePos; i++) {
            time = time + selectedImages.get(i).getImageTime() + selectedImages.get(i).getTransitionTime();
        }
        return time;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                combineImagesToVideo();
                break;

            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_apply_to_all_transition:
                for (int i = 0; i < selectedImages.size(); i++) {
                    if (i != selectedImages.size() - 1) {
                        selectedImages.get(i).setTransitionType(addTransitonType);
                        if (addTransitonType != 0) {
                            if (currentTransitionTime <= 0) {
                                currentTransitionTime = DEFAULT_TRANSITION_TIME;
                            }
                            selectedImages.get(i).setTransitionTime(currentTransitionTime);
                        } else {
                            selectedImages.get(i).setTransitionTime(0);
                        }
                    }
                }
                imagesPreviewAdapter.notifyDataSetChanged();
                makeImagesVideo();
                break;

            case R.id.ll_apply_anim_to_all_images:
                for (SelectImageInfo selectImageInfo : selectedImages) {
                    selectImageInfo.setImageAnimType(addAnimType);
                    selectImageInfo.setImageTime(currentImageTime);
                }
                imagesPreviewAdapter.notifyDataSetChanged();
                makeImagesVideo();
                break;

            case R.id.iv_textureview:
                if (isPlayImages) {
                    imagesVideoPlayer.pause();
                    isPlayImages = false;
                } else {
                    imagesVideoPlayer.start();
                    isPlayImages = true;
                }
                break;

        }
    }

    private void combineImagesToVideo() {
        //渲染合成视频
        imagesVideoPlayer.pause();
        CustomProgressDialog handleProgressDialog = new CustomProgressDialog(activity);
        handleProgressDialog.show();

        final ImagesVideoMaker imagesVideoMaker = new ImagesVideoMaker(activity);
        int bitrate = 6000000;
        String outPutVideoPath = MultiUtils.getOutPutVideoPath();
        imagesVideoMaker.setConfig(ivTextureView.getWidth(), ivTextureView.getHeight(), 30, bitrate, 1, outPutVideoPath);
        ImagesVideo newImagesVideo = ImagesToVideoHelper.makeImagesVideo(activity, selectedImages);
        SurfaceImagesVideoRenderer videoRenderer = new SurfaceImagesVideoRenderer(surfaceImagesVideoRenderer);
        videoRenderer.setImagesVideo(newImagesVideo);
        imagesVideoMaker.setDataSource(videoRenderer);
        imagesVideoMaker.startMake(new ImagesVideoMaker.OnMakeListener() {
            @Override
            public void onMakeFinish(boolean success) {
                handleProgressDialog.dismiss();
                if (success) {
                    MultiUtils.insertMp4ToGallery(activity, outPutVideoPath);
                    Intent intent = new Intent(activity, EditVideoActivity.class);
                    intent.putExtra("videoPath", outPutVideoPath);
                    intent.putExtra("combineImages", true);
                    intent.putExtra("isRecord", false);
                    startActivity(intent);
                } else {
                    MultiUtils.showToast(activity, "处理失败");
                }
            }

            @Override
            public void onMakeProgress(int makedDuration, int totalDuration) {

            }
        });
    }

    private void makeImagesVideo() {
        imagesVideoPlayer.stop();
        imagesVideo = ImagesToVideoHelper.makeImagesVideo(activity, selectedImages);
        imagesVideoPlayer.setDataSource(imagesVideo);
        imagesVideoPlayer.prepare();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlayImages && isPrepared) {
            if (imagesVideoPlayer != null) {
                imagesVideoPlayer.start();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imagesVideoPlayer != null) {
            imagesVideoPlayer.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
