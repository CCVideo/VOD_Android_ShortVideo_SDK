package bokecc.shortvideosdk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bokecc.shortvideo.combineimages.model.SelectVideoInfo;

import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.SelectVideoAdapter;
import bokecc.shortvideosdk.util.MultiUtils;

public class SelectVideoFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView rv_select_video;
    private SelectVideoAdapter selectVideoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_video, null);
        activity = getActivity();

        rv_select_video = view.findViewById(R.id.rv_select_video);
        rv_select_video.getItemAnimator().setChangeDuration(0);
        rv_select_video.getItemAnimator().setAddDuration(0);

        List<SelectVideoInfo> videoDatas = MultiUtils.getVideoDatas(activity);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 4);
        rv_select_video.setLayoutManager(layoutManager);
        selectVideoAdapter = new SelectVideoAdapter(videoDatas);
        rv_select_video.setAdapter(selectVideoAdapter);

        selectVideoAdapter.setOnItemClickListener(new SelectVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SelectVideoInfo item, int position) {
                String path = item.getPath();
                Intent intent = new Intent();
                intent.putExtra("path", path);
                activity.setResult(-1,intent);
                activity.finish();
            }
        });
        return view;
    }
}
