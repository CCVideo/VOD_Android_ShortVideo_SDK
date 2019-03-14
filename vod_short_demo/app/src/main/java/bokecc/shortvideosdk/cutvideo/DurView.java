package bokecc.shortvideosdk.cutvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bokecc.shortvideosdk.R;


public class DurView extends RelativeLayout implements TimeSliderView.OnRangeChangeListener {
    public static final int THUMB_COUNT = 10;
    private static final String TAG = "DurView";
    private Context mContext;
    private TextView mTvTip;
    private RecyclerView mRecyclerView;
    private TimeSliderView mRangeSlider;
    private long mVideoDuration;
    private long mVideoStartPos;
    private long mVideoEndPos;
    private DurAdapter mAdapter;
    private IOnRangeChangeListener mRangeChangeListener;

    public DurView(Context context) {
        super(context);

        init(context);
    }

    public DurView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public DurView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    /**
     * 获取视频帧列表
     *
     * @param path
     * @param count    期望个数
     * @param width    期望压缩后宽度
     * @param height   期望压缩后高度
     * @param listener
     */
    public static void getLocalVideoBitmap(final String path, final int count, final int width, final int height, final OnBitmapListener listener) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                try {
                    mmr.setDataSource(path);
                    long duration = (Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) * 1000;
                    long inv = (duration / count);

                    for (long i = 0; i < duration; i += inv) {
                        //注意getFrameAtTime方法的timeUs 是微妙， 1us * 1000 * 1000 = 1s
                        Bitmap bitmap = mmr.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
//                        Log.d(VideoFFCrop.TAG, "getFrameAtTime "+ i + "===" + bitmap.getWidth() + "===" + bitmap.getHeight());
                        Bitmap destBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        Log.d(TAG, "getFrameAtTime " + i + "===" + destBitmap.getWidth() + "===" + destBitmap.getHeight());
                        bitmap.recycle();

                        publishProgress(destBitmap);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    mmr.release();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                if (listener != null) {
                    listener.onBitmapGet((Bitmap) values[0]);
                }
            }

            @Override
            protected void onPostExecute(Object result) {

            }
        };
        task.execute();
    }

    public void setRangeChangeListener(IOnRangeChangeListener listener) {
        mRangeChangeListener = listener;
    }

    private void init(Context context) {
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_edit_view, this, true);

        mTvTip = (TextView) findViewById(R.id.tv_tip);

        mRangeSlider = (TimeSliderView) findViewById(R.id.range_slider);
        mRangeSlider.setRangeChangeListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new DurAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    public int getSegmentFrom() {
        return (int) mVideoStartPos;
    }

    public int getSegmentTo() {
        return (int) mVideoEndPos;
    }

    public void setMediaFileInfo(VideoBean videoInfo) {
        // 每次获取缩略图前，清除上一次生成的缩略图
        mAdapter.recycleAllBitmap();
        if (videoInfo == null) {
            return;
        }
        mVideoDuration = videoInfo.duration;

        mVideoStartPos = 0;
        mVideoEndPos = mVideoDuration;

        getLocalVideoBitmap(videoInfo.src_path, DurView.THUMB_COUNT, 120, 120, new OnBitmapListener() {
            @Override
            public void onBitmapGet(Bitmap bitmap) {
                addBitmap(mAdapter.getItemCount(), bitmap);
            }
        });
    }

    public void addBitmap(int index, Bitmap bitmap) {
        mAdapter.add(index, bitmap);
    }

    @Override
    public void onKeyDown(int type) {
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutViewDown();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAdapter != null) {
            Log.d(TAG, "onDetachedFromWindow: 清除所有bitmap");
            mAdapter.recycleAllBitmap();
        }
    }

    @Override
    public void onKeyUp(int type, int leftPinIndex, int rightPinIndex) {
        int leftTime = (int) (mVideoDuration * leftPinIndex / 100); //ms
        int rightTime = (int) (mVideoDuration * rightPinIndex / 100);

        if (type == TimeSliderView.TYPE_LEFT) {
            mVideoStartPos = leftTime;
        } else {
            mVideoEndPos = rightTime;
        }
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutViewUp((int) mVideoStartPos, (int) mVideoEndPos);
        }
        Log.d(TAG, "onCutViewUp: " + leftTime + "===" + rightTime);
//        mTvTip.setText(String.format("左侧 : %s, 右侧 : %s ", CutUtils.duration(leftTime), CutUtils.duration(rightTime)));
    }

    @Override
    public void onPreviewLineMove(int middlePinIndex) {
        int previewTime = (int) (mVideoDuration * middlePinIndex / 100); //ms
        if (mRangeChangeListener != null) {
            mRangeChangeListener.onCutViewPreview(previewTime);
        }
    }

    public interface IOnRangeChangeListener {
        void onCutViewDown();

        void onCutViewUp(int startTime, int endTime);

        void onCutViewPreview(int previewTime);
    }

    public interface OnBitmapListener {
        void onBitmapGet(Bitmap bitmap);
    }

}
