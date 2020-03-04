package bokecc.shortvideosdk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.shortvideo.combineimages.model.SelectVideoInfo;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.util.MultiUtils;

public class SelectVideoAdapter extends RecyclerView.Adapter<SelectVideoAdapter.ViewHolder> {

    private List<SelectVideoInfo> mData;
    private SelectVideoAdapter.OnItemClickListener onItemClickListener;

    public SelectVideoAdapter(List<SelectVideoInfo> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(SelectVideoInfo item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SelectVideoInfo selectVideoInfo = mData.get(position);
        MultiUtils.loadUriImage(selectVideoInfo.getCover(), holder.iv_cover);
        int videoTime = selectVideoInfo.getVideoTime();
        String time = MultiUtils.millsecondsToMinuteSecondStr(videoTime);
        holder.tv_video_time.setText(time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(mData.get(pos), pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cover;
        TextView tv_video_time;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            tv_video_time = itemView.findViewById(R.id.tv_video_time);
        }
    }
}
