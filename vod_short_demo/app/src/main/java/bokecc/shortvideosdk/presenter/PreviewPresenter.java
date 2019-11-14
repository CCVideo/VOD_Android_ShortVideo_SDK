package bokecc.shortvideosdk.presenter;

import android.app.Activity;


public class PreviewPresenter<T extends Activity> extends IPresenter<T> {

    public PreviewPresenter(T target) {
        super(target);
    }

}
