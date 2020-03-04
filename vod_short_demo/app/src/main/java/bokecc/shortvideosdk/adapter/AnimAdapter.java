package bokecc.shortvideosdk.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.model.AnimRes;

public class AnimAdapter extends RecyclerView.Adapter<AnimAdapter.ViewHolder> {

    private List<AnimRes> mData;
    private AnimAdapter.OnItemClickListener onItemClickListener;

    public AnimAdapter(List<AnimRes> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(AnimRes item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anim, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AnimRes animRes = mData.get(position);
        holder.tv_anim_name.setText(animRes.getAnimName());
        if (animRes.isSelected()) {
            holder.iv_anim.setImageResource(animRes.getSelectdImgRes());
            holder.tv_anim_name.setTextColor(Color.parseColor("#FF9520"));
        } else {
            holder.iv_anim.setImageResource(animRes.getNormalImgRes());
            holder.tv_anim_name.setTextColor(Color.parseColor("#B3FFFFFF"));
        }

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
        ImageView iv_anim;
        TextView tv_anim_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_anim = itemView.findViewById(R.id.iv_anim);
            tv_anim_name = itemView.findViewById(R.id.tv_anim_name);
        }
    }
}
