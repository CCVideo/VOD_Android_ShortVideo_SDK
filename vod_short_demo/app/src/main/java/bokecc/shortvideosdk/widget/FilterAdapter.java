package bokecc.shortvideosdk.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bokecc.shortvideosdk.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<FilterType> mData;
    private FilterAdapter.OnItemClickListener onItemClickListener;
    public FilterAdapter(List<FilterType> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(FilterType item, int position);
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
        holder.tv_filter_name.setText(mData.get(position).getEnglishName());

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
        TextView tv_filter_name;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_filter_name = (TextView) itemView.findViewById(R.id.tv_filter_name);
        }
    }
}
