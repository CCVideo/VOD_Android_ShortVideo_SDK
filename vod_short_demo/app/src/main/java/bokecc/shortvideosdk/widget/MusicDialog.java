package bokecc.shortvideosdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.adapter.MusicAdapter;
import bokecc.shortvideosdk.adapter.StickerAdapter;
import bokecc.shortvideosdk.model.MusicInfo;
import bokecc.shortvideosdk.model.StickerRes;
import bokecc.shortvideosdk.util.MultiUtils;


public class MusicDialog extends Dialog {
    private Activity context;
    private MusicAdapter musicAdapter;
    private List<MusicInfo> datas;
    private OnSelectMusic onSelectMusic;
    private String selectedMusic,bacMusicPath;

    public MusicDialog(@NonNull Activity context,String bacMusicPath,OnSelectMusic onSelectMusic) {
        super(context, R.style.MusicDialog);
        this.context = context;
        this.onSelectMusic = onSelectMusic;
        datas = new ArrayList<>();
        this.bacMusicPath = bacMusicPath;
        selectedMusic = bacMusicPath;
    }

    public interface OnSelectMusic{
        void selectMusic(String musicPath);

        void cancelMusic();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initDatas();

    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_music, null);
        setContentView(view);
        ImageView iv_close_music = view.findViewById(R.id.iv_close_music);

        RecyclerView rv_view = (RecyclerView) view.findViewById(R.id.rv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        rv_view.setLayoutManager(layoutManager);
        datas = new ArrayList<>();
        musicAdapter = new MusicAdapter(datas);
        rv_view.setAdapter(musicAdapter);

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MusicInfo item, int position) {
                if (item.isSelected){
                    item.setSelected(false);
                    selectedMusic = null;
                    musicAdapter.notifyDataSetChanged();
                    onSelectMusic.cancelMusic();
                    return;
                }
                selectedMusic = item.getMusicPath();
                onSelectMusic.selectMusic(selectedMusic);
                for (MusicInfo musicInfo:datas){
                    musicInfo.setSelected(false);
                }
               datas.get(position).setSelected(true);
               musicAdapter.notifyDataSetChanged();
            }
        });

        iv_close_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 1.0);
        lp.height = (int) (d.heightPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
    }

    private void initDatas() {
        List musicInfos = MultiUtils.getMusicInfos(context);
        for (int i = 0;i<musicInfos.size();i++){
            MusicInfo musicInfo = (MusicInfo) musicInfos.get(i);
            if (!TextUtils.isEmpty(bacMusicPath) && bacMusicPath.equals(musicInfo.getMusicPath())){
                musicInfo.setSelected(true);
            }
        }
        datas.addAll(musicInfos);
        musicAdapter.notifyDataSetChanged();
    }


}
