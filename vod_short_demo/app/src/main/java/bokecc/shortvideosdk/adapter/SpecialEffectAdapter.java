package bokecc.shortvideosdk.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.model.SpecialEffectRes;

public class SpecialEffectAdapter extends RecyclerView.Adapter<SpecialEffectAdapter.ViewHolder> {

    private List<SpecialEffectRes> mData;
    private SpecialEffectAdapter.OnItemClickListener onItemClickListener;
    private OnOnTouchListener onOnTouchListener;

    public SpecialEffectAdapter(List<SpecialEffectRes> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(SpecialEffectRes item, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnOnTouchListener {
        void onActionDown(SpecialEffectRes item, int position);

        void onActionUp(SpecialEffectRes item, int position);

    }

    public void setOnOnTouchListener(OnOnTouchListener onOnTouchListener) {
        this.onOnTouchListener = onOnTouchListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_special_effect, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SpecialEffectRes specialEffectRes = mData.get(position);
        holder.tv_effect_name.setText(specialEffectRes.getSpEffectName());
        if (specialEffectRes.isSelected()) {
            holder.iv_special_effect.setImageResource(specialEffectRes.getSelectdImgRes());
            holder.tv_effect_name.setTextColor(Color.parseColor("#FF9520"));
        } else {
            holder.iv_special_effect.setImageResource(specialEffectRes.getNormalImgRes());
            holder.tv_effect_name.setTextColor(Color.parseColor("#B3FFFFFF"));
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

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (onOnTouchListener != null) {
                            int pos = holder.getLayoutPosition();
                            onOnTouchListener.onActionDown(mData.get(pos), pos);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (onOnTouchListener != null) {
                            int pos = holder.getLayoutPosition();
                            onOnTouchListener.onActionUp(mData.get(pos), pos);
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        if (onOnTouchListener != null) {
                            int pos = holder.getLayoutPosition();
                            onOnTouchListener.onActionUp(mData.get(pos), pos);
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_special_effect;
        TextView tv_effect_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_special_effect = itemView.findViewById(R.id.iv_special_effect);
            tv_effect_name = itemView.findViewById(R.id.tv_effect_name);
        }
    }
}
