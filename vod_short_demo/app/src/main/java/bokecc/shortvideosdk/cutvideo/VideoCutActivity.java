package bokecc.shortvideosdk.cutvideo;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import bokecc.shortvideosdk.R;



public class VideoCutActivity extends BaseActivity implements View.OnClickListener, DurView.IOnRangeChangeListener {

    private MyVideoView vv_play;
    private String path;
    private CutView mSizeCutView;
    private int windowWidth;
    private int windowHeight;
    private int videoWidth;
    private int videoHeight;
    private int leftMargin, topMargin, rightMargin, bottomMargin;
    private VideoBean videoBean;
    private DurView mDurationView;
    private int startT, endT;
    private TextView rl_finish;
    private TextView tv_one_to_one, tv_four_to_three, tv_sixteen_to_nine, tv_nine_to_sixteen, tv_start_cut_time, tv_end_cut_time, tv_cut_time;
    private String outPut;
    private Button btn_play;

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        return info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cut_size);

        VideoCutHelper.getInstance().init(this);

        windowWidth = getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getWindowManager().getDefaultDisplay().getHeight();

        initUI();

        path = getIntent().getStringExtra("video_path");

        videoBean = getLocalVideoInfo(path);
        startT = 0;
        endT = (int) videoBean.duration;
        vv_play.setVideoPath(Uri.parse(path));
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv_play.setLooping(true);
                vv_play.start();

                videoWidth = vv_play.getVideoWidth();
                videoHeight = vv_play.getVideoHeight();
                float widthF = 0.7f;
                float ra = videoWidth * 1f / videoHeight;
                if (ra >= 1) {
                    widthF = 0.8f;
                } else {
                    widthF = 0.8f;
                }

                ViewGroup.LayoutParams layoutParams = vv_play.getLayoutParams();
                layoutParams.width = (int) (windowWidth * widthF);
                layoutParams.height = (int) (layoutParams.width / ra);
                vv_play.setLayoutParams(layoutParams);
                int afterChangeWidth = (int) (windowWidth * widthF);
                int afterChangeHeight = (int) (layoutParams.width / ra);
                leftMargin = (windowWidth - afterChangeWidth) / 2;
                rightMargin = leftMargin;
                topMargin = (windowHeight - afterChangeHeight) / 2;
                bottomMargin = topMargin;
                mSizeCutView.setMargin(leftMargin, topMargin, rightMargin, bottomMargin);
            }
        });

        vv_play.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            int isFirstMeasure = 0;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (isFirstMeasure <= 1) {
                    mSizeCutView.setMargin(leftMargin, topMargin, rightMargin, bottomMargin);
                }
                isFirstMeasure++;
            }
        });


        mDurationView.setMediaFileInfo(videoBean);

        rl_finish.setVisibility(View.VISIBLE);
    }

    private void initUI() {

        vv_play = (MyVideoView) findViewById(R.id.vv_play);
        mSizeCutView = (CutView) findViewById(R.id.cv_video);
        tv_one_to_one = (TextView) findViewById(R.id.tv_one_to_one);
        tv_four_to_three = (TextView) findViewById(R.id.tv_four_to_three);
        tv_sixteen_to_nine = (TextView) findViewById(R.id.tv_sixteen_to_nine);
        tv_nine_to_sixteen = (TextView) findViewById(R.id.tv_nine_to_sixteen);
        rl_finish = (TextView) findViewById(R.id.rl_finish);
        tv_start_cut_time = (TextView) findViewById(R.id.tv_start_cut_time);
        tv_end_cut_time = (TextView) findViewById(R.id.tv_end_cut_time);
        tv_cut_time = (TextView) findViewById(R.id.tv_cut_time);
        mDurationView = (DurView) findViewById(R.id.cut_view);
        btn_play = (Button) findViewById(R.id.btn_play);
        mDurationView.setRangeChangeListener(this);
        rl_finish.setOnClickListener(this);
        tv_one_to_one.setOnClickListener(this);
        tv_four_to_three.setOnClickListener(this);
        tv_sixteen_to_nine.setOnClickListener(this);
        tv_nine_to_sixteen.setOnClickListener(this);
        btn_play.setOnClickListener(this);

    }

    /**
     * 裁剪视频大小
     */
    private void cutVideo(String path, int cropWidth, int cropHeight, int x, int y) {

        showProgressDialog();
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/CropVideo/";
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File cropVideo = new File(savePath, "out.mp4");
        if (cropVideo.exists()) {
            cropVideo.delete();
        }
        outPut = cropVideo.getAbsolutePath();
        int duration = (endT - startT) / 1000;
        int startTime = startT / 1000;
        VideoCutHelper.getInstance().cropVideo(this,
                path, outPut,
                startTime, duration,
                cropWidth, cropHeight,
                x, y, new VideoCutHelper.FFListener() {

                    public void onProgress(Integer progress) {
                        setProgressDialog(progress);
                    }

                    public void onFinish() {
                        closeProgressDialog();
                        startActivity(new Intent(VideoCutActivity.this, PreviewCutResultActivity.class).putExtra("videoPath", outPut));
//                        finish();
                    }

                    public void onFail(String msg) {
                        closeProgressDialog();

                    }
                });
    }


    private void editVideo() {

        //得到裁剪后的margin值
        float[] cutArr = mSizeCutView.getCutArr();
        float left = cutArr[0];
        float top = cutArr[1];
        float right = cutArr[2];
        float bottom = cutArr[3];
        int cutWidth = mSizeCutView.getRectWidth();
        int cutHeight = mSizeCutView.getRectHeight();


        //计算宽高缩放比
        float leftPro = left / cutWidth;
        float topPro = top / cutHeight;
        float rightPro = right / cutWidth;
        float bottomPro = bottom / cutHeight;

        //得到裁剪位置
        int cropWidth = (int) (videoWidth * (rightPro - leftPro));
        int cropHeight = (int) (videoHeight * (bottomPro - topPro));
        int x = (int) (leftPro * videoWidth);
        int y = (int) (topPro * videoHeight);

        cutVideo(path, cropWidth, cropHeight, x, y);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_finish:
                editVideo();
                break;
            case R.id.tv_one_to_one:
                mSizeCutView.setCutRatio(1, 1);
                break;
            case R.id.tv_four_to_three:
                mSizeCutView.setCutRatio(4, 3);
                break;
            case R.id.tv_sixteen_to_nine:
                mSizeCutView.setCutRatio(16, 9);
                break;
            case R.id.tv_nine_to_sixteen:
                mSizeCutView.setCutRatio(9, 16);
                break;
            case R.id.btn_play:
                vv_play.start();
                btn_play.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCutViewDown() {

    }

    @Override
    public void onCutViewUp(int startTime, int endTime) {
        // 设置时长裁剪
        startT = startTime;
        endT = endTime;

        vv_play.seekTo(startTime);
        if (!vv_play.isPlaying()) {
            btn_play.setVisibility(View.VISIBLE);
        }
        int startCutTime = startTime / 1000;
        tv_start_cut_time.setText("开始：" + startCutTime + "S");

        int endCutTime = endTime / 1000;
        tv_end_cut_time.setText("结束：" + endCutTime + "S");
        int cutTime = endCutTime - startCutTime;
        tv_cut_time.setText("时长：" + cutTime + "S");

    }

    @Override
    public void onCutViewPreview(int previewTime) {
        Log.i("Jack", "预览时间：" + previewTime);
        vv_play.seekTo(previewTime);
        vv_play.pause();
        btn_play.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vv_play != null) {
            vv_play.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vv_play != null) {
            vv_play.pause();
        }
    }

}
