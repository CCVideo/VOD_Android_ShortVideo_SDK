package bokecc.shortvideosdk.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;

import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.SelectImageAdapter;
import bokecc.shortvideosdk.adapter.SelectedImagesAdapter;
import bokecc.shortvideosdk.util.MultiUtils;

public class SelectImageFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView rv_select_image, rv_selected_images;
    private SelectImageAdapter selectImageAdapter;
    private SelectedImagesAdapter selectedImagesAdapter;
    private List<SelectImageInfo> selectedImages = new ArrayList<>();
    private TextView tv_selected_images;
    private LinearLayout ll_selected_images;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_image, null);
        activity = getActivity();

        rv_select_image = view.findViewById(R.id.rv_select_image);
        rv_selected_images = view.findViewById(R.id.rv_selected_images);
        tv_selected_images = view.findViewById(R.id.tv_selected_images);
        ll_selected_images = view.findViewById(R.id.ll_selected_images);

        rv_select_image.getItemAnimator().setChangeDuration(0);
        rv_select_image.getItemAnimator().setAddDuration(0);

        List<SelectImageInfo> imageDatas = MultiUtils.getImageDatas(activity);

        GridLayoutManager layoutManager = new GridLayoutManager(activity, 4);
        rv_select_image.setLayoutManager(layoutManager);
        selectImageAdapter = new SelectImageAdapter(imageDatas);
        rv_select_image.setAdapter(selectImageAdapter);

        selectedImagesAdapter = new SelectedImagesAdapter(selectedImages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_selected_images.setLayoutManager(linearLayoutManager);
        rv_selected_images.setAdapter(selectedImagesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemMoveCallbac(selectedImagesAdapter));
        itemTouchHelper.attachToRecyclerView(rv_selected_images);


        selectImageAdapter.setOnItemClickListener(new SelectImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SelectImageInfo item, int position) {

                if (item.isSelected()) {
                    item.setSelected(false);
                    selectedImages.remove(item);
                } else {
                    if (selectedImages.size() > 29) {
                        MultiUtils.showToast(activity, "最多只能选择30张图片");
                        return;
                    }
                    item.setSelected(true);
                    item.setSelectedPos(position);
                    selectedImages.add(item);
                }
                selectImageAdapter.notifyItemChanged(position);
                selectedImagesAdapter.notifyDataSetChanged();
                if (selectedImages.size() > 0) {
                    ll_selected_images.setVisibility(View.VISIBLE);
                    tv_selected_images.setText(selectedImages.size() + "");
                    rv_selected_images.scrollToPosition(selectedImages.size() - 1);
                } else {
                    ll_selected_images.setVisibility(View.GONE);
                }
            }
        });

        selectedImagesAdapter.setOnCancelSelectedListener(new SelectedImagesAdapter.OnCancelSelectedListener() {
            @Override
            public void onOnCancelSelected(SelectImageInfo item, int position) {
                selectedImages.remove(item);
                selectedImagesAdapter.notifyItemRemoved(position);

                int selectedPos = item.getSelectedPos();
                imageDatas.get(selectedPos).setSelected(false);
                selectImageAdapter.notifyItemChanged(selectedPos);

                if (selectedImages.size() > 0) {
                    ll_selected_images.setVisibility(View.VISIBLE);
                    tv_selected_images.setText(selectedImages.size() + "");
                } else {
                    ll_selected_images.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    public List<SelectImageInfo> getSelectedImages() {
        return selectedImages;
    }

}
