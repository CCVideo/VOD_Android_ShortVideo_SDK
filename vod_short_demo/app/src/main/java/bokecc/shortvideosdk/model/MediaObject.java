package bokecc.shortvideosdk.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import bokecc.shortvideosdk.util.FileUtils;


@SuppressWarnings("serial")
public class MediaObject implements Serializable {

    /**
     * 默认最大时长
     */
    public final static int DEFAULT_MAX_DURATION = 10 * 1000;

    /**
     * 视频最大时长，默认10秒
     */
    private int mMaxDuration;

    /**
     * 当前分块
     */
    private volatile transient MediaPart mCurrentPart;
    /**
     * 获取所有分块
     */
    private LinkedList<MediaPart> mMediaList = new LinkedList<MediaPart>();

    public MediaObject() {
        this.mMaxDuration = DEFAULT_MAX_DURATION;
    }

    /**
     * 获取视频最大长度
     */
    public int getMaxDuration() {
        return mMaxDuration;
    }

    /**
     * 设置最大时长，必须大于1秒
     */
    public void setMaxDuration(int duration) {
        if (duration >= 1000) {
            mMaxDuration = duration;
        }
    }

    /**
     * 获取录制的总时长
     */
    public int getDuration() {
        int duration = 0;
        if (mMediaList != null) {
            for (MediaPart part : mMediaList) {
                duration += part.getDuration();
            }
        }
        return duration;
    }

    /**
     * 删除分块
     */
    public void removePart(MediaPart part, boolean deleteFile) {
        if (mMediaList != null)
            mMediaList.remove(part);

        if (part != null) {
            // 删除文件
            if (deleteFile) {
                part.delete();
            }

            mMediaList.remove(part);
            if (mCurrentPart != null && part.equals(mCurrentPart)) {
                mCurrentPart = null;
            }
        }
    }

    /**
     * 生成分块信息，主要用于拍摄
     *
     * @return
     */
    public MediaPart buildMediaPart(String mediaPath) {
        mCurrentPart = new MediaPart(mediaPath);
        mMediaList.add(mCurrentPart);
        return mCurrentPart;
    }

    /**
     * 获取当前分块
     */
    public MediaPart getCurrentPart() {
        if (mCurrentPart != null)
            return mCurrentPart;
        if (mMediaList != null && mMediaList.size() > 0)
            mCurrentPart = mMediaList.get(mMediaList.size() - 1);
        return mCurrentPart;
    }


    public MediaPart getPart(int index) {
        if (mCurrentPart != null && index < mMediaList.size())
            return mMediaList.get(index);
        return null;
    }

    public LinkedList<MediaPart> getMedaParts() {
        return mMediaList;
    }

    public void clearAll(boolean deleteFile) {
        Iterator<MediaPart> mediaPartIterator = mMediaList.iterator();
        while(mediaPartIterator.hasNext()) {
            MediaPart mediaPart = mediaPartIterator.next();
            mediaPartIterator.remove();
            if (deleteFile) {
                mediaPart.delete();
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        if (mMediaList != null) {
            result.append("[" + mMediaList.size() + "]");
            for (MediaPart part : mMediaList) {
                result.append(part.mediaPath + ":" + part.duration + "\n");
            }
        }
        return result.toString();
    }

    public static class MediaPart implements Serializable {

        /**
         * 视频路径
         */
        public String mediaPath;

        /**
         * 分段长度
         */
        public int duration;

        public transient boolean remove;

        public MediaPart(String mediaPath) {
            this.mediaPath = mediaPath;
        }

        public void delete() {
            FileUtils.deleteFile(mediaPath);
        }

        public int getDuration() {
            return duration;
        }

        public void addDuration(int addition) {
            duration += addition;
        }

    }

}
