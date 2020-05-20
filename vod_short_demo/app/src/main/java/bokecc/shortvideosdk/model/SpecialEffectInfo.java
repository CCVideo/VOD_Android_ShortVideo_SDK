package bokecc.shortvideosdk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SpecialEffectInfo implements Parcelable {
    private long startTime;
    private long endTime;
    private int effectType;

    public SpecialEffectInfo() {
    }

    protected SpecialEffectInfo(Parcel in) {
        startTime = in.readLong();
        endTime = in.readLong();
        effectType = in.readInt();
    }

    public static final Creator<SpecialEffectInfo> CREATOR = new Creator<SpecialEffectInfo>() {
        @Override
        public SpecialEffectInfo createFromParcel(Parcel in) {
            return new SpecialEffectInfo(in);
        }

        @Override
        public SpecialEffectInfo[] newArray(int size) {
            return new SpecialEffectInfo[size];
        }
    };

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = effectType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeInt(effectType);
    }
}
