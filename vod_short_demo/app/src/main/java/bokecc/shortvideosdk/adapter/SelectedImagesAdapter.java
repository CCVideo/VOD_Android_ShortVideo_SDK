package bokecc.shortvideosdk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;

import java.util.Collections;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.fragment.OnItemMove;
import bokecc.shortvideosdk.util.MultiUtils;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> implements OnItemMove {

    private List<SelectImageInfo> mData;
    private SelectedImagesAdapter.OnCancelSelectedListener onCancelSelectedListener;

    public SelectedImagesAdapter(List<SelectImageInfo> mData) {
        this.mData = mData;
    }

    @Override
    public void onItemMove(int oldPosition, int newPosition) {
        Collections.swap(mData, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    public interface OnCancelSelectedListener {
        void onOnCancelSelected(SelectImageInfo item, int position);
    }

    public void setOnCancelSelectedListener(OnCancelSelectedListener onCancelSelectedListener) {
        this.onCancelSelectedListener = onCancelSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_images, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SelectImageInfo selectImageInfo = mData.get(position);
        MultiUtils.loadPathImage(selectImageInfo.getPath(), holder.iv_cover);

        holder.iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onCancelSelectedListener != null) {
                    int pos = holder.getLayoutPosition();
                    onCancelSelectedListener.onOnCancelSelected(mData.get(pos), pos);
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
        ImageView iv_cancel;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            iv_cancel = itemView.findViewById(R.id.iv_cancel);
        }
    }
}
