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
import bokecc.shortvideosdk.model.FilterRes;
import bokecc.shortvideosdk.model.StickerRes;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<FilterRes> mData;
    private FilterAdapter.OnItemClickListener onItemClickListener;
    public FilterAdapter(List<FilterRes> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(FilterRes item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FilterRes filterRes = mData.get(position);
        holder.tv_filter_name.setText(filterRes.getFilterName());
        if (filterRes.isSelected()){
            holder.iv_sticker.setImageResource(filterRes.getSelectdImgRes());
            holder.tv_filter_name.setTextColor(Color.parseColor("#FF9520"));
        }else {
            holder.iv_sticker.setImageResource(filterRes.getNormalImgRes());
            holder.tv_filter_name.setTextColor(Color.parseColor("#B3FFFFFF"));
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
        ImageView iv_sticker;
        TextView tv_filter_name;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_sticker = itemView.findViewById(R.id.iv_sticker);
            tv_filter_name = itemView.findViewById(R.id.tv_filter_name);
        }
    }
}
