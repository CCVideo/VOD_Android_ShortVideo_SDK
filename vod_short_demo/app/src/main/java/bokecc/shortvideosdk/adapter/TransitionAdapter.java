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
import bokecc.shortvideosdk.model.TransitionRes;

public class TransitionAdapter extends RecyclerView.Adapter<TransitionAdapter.ViewHolder> {

    private List<TransitionRes> mData;
    private TransitionAdapter.OnItemClickListener onItemClickListener;

    public TransitionAdapter(List<TransitionRes> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(TransitionRes item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transition, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TransitionRes transitionRes = mData.get(position);
        holder.tv_transition_name.setText(transitionRes.getTransitionName());
        if (transitionRes.isSelected()) {
            holder.iv_transition.setImageResource(transitionRes.getSelectdImgRes());
            holder.tv_transition_name.setTextColor(Color.parseColor("#FF9520"));
        } else {
            holder.iv_transition.setImageResource(transitionRes.getNormalImgRes());
            holder.tv_transition_name.setTextColor(Color.parseColor("#B3FFFFFF"));
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
        ImageView iv_transition;
        TextView tv_transition_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_transition = itemView.findViewById(R.id.iv_transition);
            tv_transition_name = itemView.findViewById(R.id.tv_transition_name);
        }
    }
}
