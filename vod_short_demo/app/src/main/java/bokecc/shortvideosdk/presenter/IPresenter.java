package bokecc.shortvideosdk.presenter;

public class IPresenter<T> {

    private T mTarget;

    public IPresenter(T target) {
        mTarget = target;
    }

    public T getTarget() {
        return mTarget;
    }

    public void onCreate() {

    }

    public void onDestroy() {

    }
}
