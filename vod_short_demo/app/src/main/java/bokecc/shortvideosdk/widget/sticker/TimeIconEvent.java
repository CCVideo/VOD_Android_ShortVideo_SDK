package bokecc.shortvideosdk.widget.sticker;

import android.view.MotionEvent;


public class TimeIconEvent implements StickerIconEvent {
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
      stickerView.setStickTime();
  }
}
