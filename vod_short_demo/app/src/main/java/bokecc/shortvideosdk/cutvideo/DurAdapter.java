package bokecc.shortvideosdk.cutvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;

import bokecc.shortvideosdk.R;


public class DurAdapter extends RecyclerView.Adapter<DurAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Bitmap> data = new ArrayList<Bitmap>();

    public DurAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int itemCount = DurView.THUMB_COUNT;
        int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        final int itemWidth = (screenWidth - 2 * padding) / itemCount;
        int height = mContext.getResources().getDimensionPixelOffset(R.dimen.ugc_item_thumb_height);
//        int height = (int) (itemWidth / VideoCropHelper.WHA);
        ImageView view = new ImageView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, height));
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.thumb.setImageBitmap(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(int position, Bitmap b) {
        data.add(b);
        notifyItemInserted(position);
    }

    public void addAll(ArrayList<Bitmap> bitmap) {
        recycleAllBitmap();

        data.addAll(bitmap);
        notifyDataSetChanged();
    }

    public void recycleAllBitmap() {
        for (Bitmap b : data) {
            if (!b.isRecycled())
                b.recycle();
        }
        data.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumb;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView) itemView;
        }
    }
}
