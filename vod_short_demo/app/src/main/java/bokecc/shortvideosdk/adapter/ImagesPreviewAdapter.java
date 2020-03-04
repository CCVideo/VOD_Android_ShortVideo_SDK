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

public class ImagesPreviewAdapter extends RecyclerView.Adapter<ImagesPreviewAdapter.ViewHolder> {

    private List<SelectImageInfo> mData;
    private ImagesPreviewAdapter.OnItemClickListener onItemClickListener;
    private OnAddTransitionClickListener onAddTransitionClickListener;
    private OnAddImageAnimClickListener onAddImageAnimClickListener;


    public ImagesPreviewAdapter(List<SelectImageInfo> mData) {
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(SelectImageInfo item, int position);
    }

    public interface OnAddTransitionClickListener {
        void onAddTransitionClick(SelectImageInfo item, int position);
    }

    public interface OnAddImageAnimClickListener {
        void onAddImageAnimClick(SelectImageInfo item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnAddTransitionClickListener(OnAddTransitionClickListener onAddTransitionClickListener) {
        this.onAddTransitionClickListener = onAddTransitionClickListener;
    }

    public void setOnAddImageAnimClickListener(OnAddImageAnimClickListener onAddImageAnimClickListener) {
        this.onAddImageAnimClickListener = onAddImageAnimClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images_preview, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SelectImageInfo selectImageInfo = mData.get(position);
        holder.tv_image_time.setText(MultiUtils.calFloat(1, selectImageInfo.getImageTime(), 1000) + "s");

        int transitionType = selectImageInfo.getTransitionType();
        if (selectImageInfo.isAddTransition()) {
            if (transitionType == 0) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_transiion_selected);
            } else if (transitionType == 1) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_overlap_selected);
            } else if (transitionType == 2) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_flash_black_selected);
            } else if (transitionType == 3) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_flash_white_selected);
            } else if (transitionType == 4) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_circle_selected);
            }
        } else {
            if (transitionType == 0) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_transition_normal);
            } else if (transitionType == 1) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_overlap_normal);
            } else if (transitionType == 2) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_flash_black_normal);
            } else if (transitionType == 3) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_flash_white_normal);
            } else if (transitionType == 4) {
                holder.iv_add_transition.setImageResource(R.mipmap.iv_add_circle_normal);
            }
        }

        if (selectImageInfo.isSelected()) {
            holder.iv_selected_flag.setVisibility(View.VISIBLE);
        } else {
            holder.iv_selected_flag.setVisibility(View.GONE);
        }
        MultiUtils.loadPathImage(selectImageInfo.getPath(), holder.iv_image);
        holder.tv_transition_time.setText(MultiUtils.calFloat(1, selectImageInfo.getTransitionTime(), 1000) + "s");
        if (position == 0) {
            holder.tv_start.setVisibility(View.VISIBLE);
        } else {
            holder.tv_start.setVisibility(View.GONE);
        }
        if (position == mData.size() - 1) {
            holder.tv_end.setVisibility(View.VISIBLE);
            holder.iv_add_transition.setVisibility(View.INVISIBLE);
            holder.tv_transition_time.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_end.setVisibility(View.GONE);
            holder.iv_add_transition.setVisibility(View.VISIBLE);
            holder.tv_transition_time.setVisibility(View.VISIBLE);
        }

        int imageAnimType = selectImageInfo.getImageAnimType();
        if (imageAnimType == 0) {
            holder.tv_selected_anim.setText("");
        } else if (imageAnimType == 1) {
            holder.tv_selected_anim.setText("放大");
        } else if (imageAnimType == 2) {
            holder.tv_selected_anim.setText("缩小");
        } else if (imageAnimType == 3) {
            holder.tv_selected_anim.setText("左滑");
        } else if (imageAnimType == 4) {
            holder.tv_selected_anim.setText("右滑");
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

        holder.iv_add_transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onAddTransitionClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onAddTransitionClickListener.onAddTransitionClick(mData.get(pos), pos);
                }
            }
        });

        holder.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddImageAnimClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onAddImageAnimClickListener.onAddImageAnimClick(mData.get(pos), pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_add_transition;
        ImageView iv_image;
        TextView tv_image_time;
        TextView tv_transition_time;
        TextView tv_start;
        TextView tv_end;
        ImageView iv_selected_flag;
        TextView tv_selected_anim;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_add_transition = itemView.findViewById(R.id.iv_add_transition);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_image_time = itemView.findViewById(R.id.tv_image_time);
            tv_transition_time = itemView.findViewById(R.id.tv_transition_time);
            tv_start = itemView.findViewById(R.id.tv_start);
            tv_end = itemView.findViewById(R.id.tv_end);
            iv_selected_flag = itemView.findViewById(R.id.iv_selected_flag);
            tv_selected_anim = itemView.findViewById(R.id.tv_selected_anim);
        }
    }
}
