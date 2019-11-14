package bokecc.shortvideosdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import bokecc.shortvideosdk.R;


public class HandleProgressDialog extends Dialog {

    private Context context;
    private TextView tv_progress;
    private ProgressBar pb_progress;

    public HandleProgressDialog(Context context) {
        super(context, R.style.HandleProgressDialog);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_progress, null);
        setContentView(view);
        tv_progress = view.findViewById(R.id.tv_progress);
        pb_progress = view.findViewById(R.id.pb_progress);

        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.9);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);
    }

    public void updateProgress(Integer progress){
        if (tv_progress!=null){
            tv_progress.setText(progress + "%");
        }

        if (pb_progress!=null){
            pb_progress.setProgress(progress);
        }
    }

}
