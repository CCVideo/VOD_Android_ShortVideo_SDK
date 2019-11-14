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

import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.StickerAdapter;
import bokecc.shortvideosdk.model.StickerRes;


public class StickerDialog extends Dialog {
    private Activity context;
    private StickerAdapter stickerAdapter;
    private List<StickerRes> datas;
    private SelectSticker selectSticker;

    public StickerDialog(@NonNull Activity context, SelectSticker selectSticker) {
        super(context, R.style.StickerDialog);
        this.context = context;
        this.selectSticker = selectSticker;
        datas = new ArrayList<>();
    }

    public interface SelectSticker{
        void selectSticker(StickerRes sticker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFilterDatas();
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_sticker, null);
        setContentView(view);
        RecyclerView rv_view = (RecyclerView) view.findViewById(R.id.rv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(OrientationHelper. HORIZONTAL);
        rv_view.setLayoutManager(layoutManager);
        stickerAdapter = new StickerAdapter(datas);
        rv_view.setAdapter(stickerAdapter);

        stickerAdapter.setOnItemClickListener(new StickerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StickerRes item, int position) {
                selectSticker.selectSticker(item);
                dismiss();
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

        StickerRes sticker1 = new StickerRes(R.mipmap.iv_sticker1);
        datas.add(sticker1);

        StickerRes sticker2 = new StickerRes(R.mipmap.iv_sticker2);
        datas.add(sticker2);

        StickerRes sticker3 = new StickerRes(R.mipmap.iv_sticker3);
        datas.add(sticker3);

        StickerRes sticker4 = new StickerRes(R.mipmap.iv_sticker4);
        datas.add(sticker4);

        StickerRes sticker5 = new StickerRes(R.mipmap.iv_sticker5);
        datas.add(sticker5);

    }

}
