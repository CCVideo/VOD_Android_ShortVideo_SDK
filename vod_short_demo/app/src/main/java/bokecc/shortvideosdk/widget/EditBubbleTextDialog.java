package bokecc.shortvideosdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import bokecc.shortvideosdk.R;
import bokecc.shortvideosdk.util.MultiUtils;


public class EditBubbleTextDialog extends Dialog {

    private Context context;
    private String oldText;
    private onEditBubbleText onEditBubbleText;
    private CharSequence charSequence;
    private int start,end;

    public EditBubbleTextDialog(Context context,String oldText,onEditBubbleText onEditBubbleText) {
        super(context, R.style.EditBubbleText);
        this.context = context;
        this.oldText = oldText;
        this.onEditBubbleText = onEditBubbleText;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_edit_bubble_text, null);
        setContentView(view);

        final EditText et_bubble_text = view.findViewById(R.id.et_bubble_text);
        et_bubble_text.setText(oldText);

        TextView tv_sure = view.findViewById(R.id.tv_sure);

        et_bubble_text.requestFocus();
        et_bubble_text.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) et_bubble_text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 200);

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bubbleText = et_bubble_text.getText().toString();
                onEditBubbleText.getBubbleText(bubbleText);
                dismiss();
            }
        });

        et_bubble_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                charSequence = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                start = et_bubble_text.getSelectionStart();
                end = et_bubble_text.getSelectionEnd();
                if (charSequence.length()>15){
                    MultiUtils.showToast((Activity) context,"气泡文字不能超过15个字");
                    s.delete(start-1, end);
                    int currentSelection = end;
                    et_bubble_text.setText(s);
                    et_bubble_text.setSelection(currentSelection);
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 1.0);
        dialogWindow.setAttributes(lp);
        dialogWindow.setDimAmount(0);
        dialogWindow.setGravity(Gravity.BOTTOM);
    }


}
