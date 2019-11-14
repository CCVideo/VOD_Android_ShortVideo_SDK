package bokecc.shortvideosdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.camerafilter.camera.engine.CameraParam;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.util.MultiUtils;


public class BeautyDialog extends Dialog {
    private Activity context;
    private int currentBeautyWhiteValue,currentBeautySkinValue;
    private OnBeauty onBeauty;
    private CameraParam mCameraParam;
    public BeautyDialog(Activity context,int currentBeautyWhiteValue,int currentBeautySkinValue,OnBeauty onBeauty) {
        super(context, R.style.StickerDialog);
        this.context = context;
        this.currentBeautyWhiteValue = currentBeautyWhiteValue;
        this.currentBeautySkinValue = currentBeautySkinValue;
        this.onBeauty = onBeauty;
        mCameraParam = CameraParam.getInstance();
    }

    public interface OnBeauty{
        void getBeautyWhiteValue(int value);
        void getBeautySkinValue(int value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_beauty, null);
        setContentView(view);
        TextView tv_beauty_white_value = view.findViewById(R.id.tv_beauty_white_value);
        TextView tv_beauty_skin_value = view.findViewById(R.id.tv_beauty_skin_value);
        SeekBar sb_beauty_white = view.findViewById(R.id.sb_beauty_white);
        SeekBar sb_beauty_skin = view.findViewById(R.id.sb_beauty_skin);
        sb_beauty_white.setProgress(currentBeautyWhiteValue);
        sb_beauty_skin.setProgress(currentBeautySkinValue);
        tv_beauty_white_value.setText(currentBeautyWhiteValue+"");
        tv_beauty_skin_value.setText(currentBeautySkinValue+"");

        sb_beauty_white.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_beauty_white_value.setText(progress+"");
                onBeauty.getBeautyWhiteValue(progress);
                mCameraParam.beauty.complexionIntensity = MultiUtils.calFloat(2,progress,200);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_beauty_skin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_beauty_skin_value.setText(progress+"");
                onBeauty.getBeautySkinValue(progress);
                mCameraParam.beauty.beautyIntensity = MultiUtils.calFloat(2,progress,200);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

}
