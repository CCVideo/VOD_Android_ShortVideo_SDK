package bokecc.shortvideosdk.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import bokecc.shortvideosdk.R;

public class EffectImagesPreviewAdapter extends RecyclerView.Adapter<EffectImagesPreviewAdapter.ViewHolder> {

    private List<Bitmap> mData;
    private EffectImagesPreviewAdapter.OnItemClickListener onItemClickListener;

    public EffectImagesPreviewAdapter(List<Bitmap> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(Bitmap item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_effect_images_preview, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Bitmap bitmap = mData.get(position);
        holder.iv_image.setImageBitmap(bitmap);

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
        ImageView iv_image;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }
}
