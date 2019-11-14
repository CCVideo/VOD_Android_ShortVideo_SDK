package bokecc.shortvideosdk.adapter;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.model.MusicInfo;
import bokecc.shortvideosdk.model.StickerRes;
import bokecc.shortvideosdk.util.MultiUtils;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<MusicInfo> mData;
    private MusicAdapter.OnItemClickListener onItemClickListener;
    public MusicAdapter(List<MusicInfo> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(MusicInfo item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MusicInfo musicInfo = mData.get(position);
        if (musicInfo!=null){
            holder.tv_music_name.setText(musicInfo.getMusicName());
            holder.tv_singer.setText(musicInfo.getSinger());
            holder.tv_music_time.setText(MultiUtils.millsecondsToMinuteSecondStr(musicInfo.getMusicTime()));
            if (musicInfo.isSelected){
                holder.tv_music_name.setTextColor(Color.parseColor("#FF9520"));
                holder.tv_singer.setTextColor(Color.parseColor("#FF9520"));
                holder.iv_playing.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) holder.iv_playing.getDrawable();
                animationDrawable.start();
                holder.tv_music_time.setVisibility(View.GONE);
            }else {
                holder.tv_music_name.setTextColor(Color.parseColor("#FFFFFF"));
                holder.tv_singer.setTextColor(Color.parseColor("#99FFFFFF"));
                holder.iv_playing.setVisibility(View.GONE);
                holder.tv_music_time.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
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
        TextView tv_music_name;
        TextView tv_singer;
        TextView tv_music_time;
        ImageView iv_playing;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_music_name = itemView.findViewById(R.id.tv_music_name);
            tv_singer = itemView.findViewById(R.id.tv_singer);
            iv_playing = itemView.findViewById(R.id.iv_playing);
            tv_music_time = itemView.findViewById(R.id.tv_music_time);
        }
    }
}
