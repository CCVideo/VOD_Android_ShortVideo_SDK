package bokecc.shortvideosdk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.util.MultiUtils;

public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.ViewHolder> {

    private List<SelectImageInfo> mData;
    private SelectImageAdapter.OnItemClickListener onItemClickListener;

    public SelectImageAdapter(List<SelectImageInfo> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(SelectImageInfo item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SelectImageInfo selectImageInfo = mData.get(position);
        MultiUtils.loadPathImage(selectImageInfo.getPath(), holder.iv_cover);
        if (selectImageInfo.isSelected()) {
            holder.view_selected.setVisibility(View.VISIBLE);
            holder.iv_is_selected.setImageResource(R.mipmap.iv_selected_img);
        } else {
            holder.view_selected.setVisibility(View.GONE);
            holder.iv_is_selected.setImageResource(R.mipmap.iv_unselect_img);
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
        ImageView iv_cover;
        View view_selected;
        ImageView iv_is_selected;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            view_selected = itemView.findViewById(R.id.view_selected);
            iv_is_selected = itemView.findViewById(R.id.iv_is_selected);
        }
    }
}
