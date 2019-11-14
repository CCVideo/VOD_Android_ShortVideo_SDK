package bokecc.shortvideosdk;

import android.app.Application;

import com.bokecc.camerafilter.LocalVideoFilter;

public class ShortVideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalVideoFilter.init(this);
    }
}
