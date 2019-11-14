package bokecc.shortvideosdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import com.bokecc.camerafilter.glfilter.color.bean.DynamicColor;
import com.bokecc.camerafilter.glfilter.resource.FilterHelper;
import com.bokecc.camerafilter.glfilter.resource.ResourceJsonCodec;
import com.bokecc.camerafilter.glfilter.resource.bean.ResourceData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.FilterAdapter;
import bokecc.shortvideosdk.adapter.StickerAdapter;
import bokecc.shortvideosdk.model.FilterRes;
import bokecc.shortvideosdk.model.StickerRes;


public class FilterDialog extends Dialog {
    private Activity context;
    private FilterAdapter filterAdapter;
    private List<FilterRes> datas;
    private OnSelectFilter onSelectFilter;
    private int currentFilter;
    private DynamicColor dynamicColor = null;

    public FilterDialog(@NonNull Activity context, int currentFilter, OnSelectFilter onSelectFilter) {
        super(context, R.style.StickerDialog);
        this.context = context;
        this.onSelectFilter = onSelectFilter;
        this.currentFilter = currentFilter;
        datas = new ArrayList<>();
    }

    public interface OnSelectFilter {
        void selectFilter(int filterPos, DynamicColor color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFilterDatas();
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_filter, null);
        setContentView(view);
        RecyclerView rv_view = (RecyclerView) view.findViewById(R.id.rv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        rv_view.setLayoutManager(layoutManager);
        filterAdapter = new FilterAdapter(datas);
        rv_view.setAdapter(filterAdapter);

        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FilterRes item, int position) {
                for (int i = 0; i < datas.size(); i++) {
                    if (i == position) {
                        datas.get(i).setSelected(true);
                    } else {
                        datas.get(i).setSelected(false);
                    }
                }
                filterAdapter.notifyDataSetChanged();
                if (position == 0) {
                    dynamicColor = null;
                } else if (position == 1) {
                    //清新fairytale
                    dynamicColor = getDynamicColor("fairytale");
                } else if (position == 2) {
                    //淡雅calm
                    dynamicColor = getDynamicColor("calm");
                } else if (position == 3) {
                    //白皙hudson
                    dynamicColor = getDynamicColor("hudson");
                } else if (position == 4) {
                    //复古earlybird
                    dynamicColor = getDynamicColor("earlybird");
                } else if (position == 5) {
                    //微光healthy
                    dynamicColor = getDynamicColor("healthy");
                }
                onSelectFilter.selectFilter(position,dynamicColor);

            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setDimAmount(0f);

    }

    private void initFilterDatas() {
        FilterRes filterRes0 = new FilterRes();
        filterRes0.setNormalImgRes(R.mipmap.iv_no_filter);
        filterRes0.setSelectdImgRes(R.mipmap.iv_no_filter);
        filterRes0.setFilterName("");
        if (currentFilter == 0) {
            filterRes0.setSelected(true);
        } else {
            filterRes0.setSelected(false);
        }
        datas.add(filterRes0);

        FilterRes filterRes1 = new FilterRes();
        filterRes1.setNormalImgRes(R.mipmap.iv_pure_fresh_normal);
        filterRes1.setSelectdImgRes(R.mipmap.iv_pure_fresh_selected);
        filterRes1.setFilterName("清新");
        if (currentFilter == 1) {
            filterRes1.setSelected(true);
        } else {
            filterRes1.setSelected(false);
        }
        datas.add(filterRes1);

        FilterRes filterRes2 = new FilterRes();
        filterRes2.setNormalImgRes(R.mipmap.iv_quietly_elegant_normal);
        filterRes2.setSelectdImgRes(R.mipmap.iv_quietly_elegant_selected);
        filterRes2.setFilterName("淡雅");
        if (currentFilter == 2) {
            filterRes2.setSelected(true);
        } else {
            filterRes2.setSelected(false);
        }
        datas.add(filterRes2);

        FilterRes filterRes3 = new FilterRes();
        filterRes3.setNormalImgRes(R.mipmap.iv_white_skin_normal);
        filterRes3.setSelectdImgRes(R.mipmap.iv_white_skin_selected);
        filterRes3.setFilterName("白皙");
        if (currentFilter == 3) {
            filterRes3.setSelected(true);
        } else {
            filterRes3.setSelected(false);
        }
        datas.add(filterRes3);

        FilterRes filterRes4 = new FilterRes();
        filterRes4.setNormalImgRes(R.mipmap.iv_ancient_normal);
        filterRes4.setSelectdImgRes(R.mipmap.iv_ancient_selected);
        filterRes4.setFilterName("复古");
        if (currentFilter == 4) {
            filterRes4.setSelected(true);
        } else {
            filterRes4.setSelected(false);
        }
        datas.add(filterRes4);

        FilterRes filterRes5 = new FilterRes();
        filterRes5.setNormalImgRes(R.mipmap.iv_micro_light_normal);
        filterRes5.setSelectdImgRes(R.mipmap.iv_micro_light_selected);
        filterRes5.setFilterName("微光");
        if (currentFilter == 5) {
            filterRes5.setSelected(true);
        } else {
            filterRes5.setSelected(false);
        }
        datas.add(filterRes5);

    }

    public DynamicColor getDynamicColor(String filterName) {
        List<ResourceData> filterList = FilterHelper.getFilterList();
        for (int i = 0; i < filterList.size(); i++) {
            ResourceData resourceData = filterList.get(i);
            if (resourceData.name.equals(filterName)) {
                String folderPath = FilterHelper.getFilterDirectory(context) + File.separator + resourceData.unzipFolder;
                DynamicColor color = null;
                try {
                    color = ResourceJsonCodec.decodeFilterData(folderPath);
                    return color;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

}
