package bokecc.shortvideosdk;

import android.app.Application;
import android.content.Context;

import com.bokecc.camerafilter.LocalVideoFilter;

public class ShortVideoApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        LocalVideoFilter.init(this);
    }

    public static Context getContext() {
        return context;
    }


}
