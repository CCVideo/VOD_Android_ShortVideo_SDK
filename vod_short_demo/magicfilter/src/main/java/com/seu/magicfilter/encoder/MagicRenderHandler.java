package com.seu.magicfilter.encoder;
/*
	视频流录制核心类
*/

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.seu.magicfilter.encoder.gles.EglCore;
import com.seu.magicfilter.encoder.glutils.EGLBase;
import com.seu.magicfilter.encoder.glutils.GLDrawer2D;
import com.seu.magicfilter.encoder.video.WindowSurface;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterType;

import java.nio.FloatBuffer;

/**
 * Helper class to draw texture to whole view on private thread
 */
public final class MagicRenderHandler implements Runnable {
	private static final boolean DEBUG = false;	// TODO set false on release
	private static final String TAG = "RenderHandler";

	private final Object mSync = new Object();
    private boolean mIsRecordable;
    private Surface mSurface;
	private int mTexId = -1;
	private float[] mMatrix = new float[32];

	private boolean mRequestSetEglContext;
	private boolean mRequestRelease;
	private int mRequestDraw;

	static int mWidth;
	static int mHeight;
	public static final MagicRenderHandler createHandler(final String name, int width, int height) {
		Log.e(TAG, "createHandler:" + width + " " + height);
		final MagicRenderHandler handler = new MagicRenderHandler();

		mWidth = width;
		mHeight = height;
		synchronized (handler.mSync) {
			new Thread(handler, !TextUtils.isEmpty(name) ? name : TAG).start();
			try {
				handler.mSync.wait();
			} catch (final InterruptedException e) {
			}
		}
		return handler;
	}

	int mTextureId;

	public final void setTextureId(final int tex_id) {
		synchronized (mSync) {
			if (mRequestRelease) return;
			mTextureId = tex_id;
			mSync.notifyAll();
		}
	}


	EGLContext shared_context;
	public final void setEglContext(final EGLContext shared_context, final int tex_id, final Surface surface, final boolean isRecordable) {
		Log.e(TAG, "setEglContext:surface");
//		if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture) && !(surface instanceof SurfaceHolder))
//			throw new RuntimeException("unsupported window type:" + surface);
		synchronized (mSync) {
			if (mRequestRelease) return;

			mTextureId = tex_id;
			this.shared_context = shared_context;

			mSurface = surface;
			mIsRecordable = isRecordable;
			mRequestSetEglContext = true;
			Matrix.setIdentityM(mMatrix, 0);
			Matrix.setIdentityM(mMatrix, 16);
			mSync.notifyAll();
			try {
				mSync.wait();
			} catch (final InterruptedException e) {
			}
		}
	}

	public final void draw() {
		draw(mTexId, mMatrix, null);
	}

	public final void draw(final int tex_id) {
		draw(tex_id, mMatrix, null);
	}

	public final void draw(final float[] tex_matrix) {
		draw(mTexId, tex_matrix, null);
	}

	public final void draw(final float[] tex_matrix, final float[] mvp_matrix) {
		draw(mTexId, tex_matrix, mvp_matrix);
	}

	public final void draw(final int tex_id, final float[] tex_matrix) {
		draw(tex_id, tex_matrix, null);
	}

	SurfaceTexture st;
	FloatBuffer gLCubeBuffer, gLTextureBuffer;
	public final void draw(final SurfaceTexture st, final FloatBuffer gLCubeBuffer, final FloatBuffer gLTextureBuffer) {
		synchronized (mSync) {

			if (mRequestRelease) return;
			this.st = st;
			this.gLCubeBuffer = gLCubeBuffer;
			this.gLTextureBuffer = gLTextureBuffer;

			mRequestDraw++;
			mSync.notifyAll();
/*			try {
				mSync.wait();
			} catch (final InterruptedException e) {
			} */
		}
	}


	public final void draw(final int tex_id, final float[] tex_matrix, final float[] mvp_matrix) {
		synchronized (mSync) {

			if (mRequestRelease) return;
			mTexId = tex_id;
			if ((tex_matrix != null) && (tex_matrix.length >= 16)) {
				System.arraycopy(tex_matrix, 0, mMatrix, 0, 16);
			} else {
				Matrix.setIdentityM(mMatrix, 0);
			}
			if ((mvp_matrix != null) && (mvp_matrix.length >= 16)) {
				System.arraycopy(mvp_matrix, 0, mMatrix, 16, 16);
			} else {
				Matrix.setIdentityM(mMatrix, 16);
			}
			mRequestDraw++;
			mSync.notifyAll();
/*			try {
				mSync.wait();
			} catch (final InterruptedException e) {
			} */
		}
	}

	public boolean isValid() {
		synchronized (mSync) {
			return !(mSurface instanceof Surface) || ((Surface)mSurface).isValid();
		}
	}

	public final void release() {
		if (DEBUG) Log.i(TAG, "release:");
		synchronized (mSync) {
			if (mRequestRelease) return;
			mRequestRelease = true;
			mSync.notifyAll();
			try {
				mSync.wait();
			} catch (final InterruptedException e) {
			}
		}
	}

	public void releaseDraw() {
		if (mInputWindowSurface != null) {
			mInputWindowSurface.release();
			mInputWindowSurface = null;
		}
		if (mInput != null) {
			mInput.destroy();
			mInput = null;
		}
		if (mEglCore != null) {
			mEglCore.release();
			mEglCore = null;
		}
		if(filter != null){
			filter.destroy();
			filter = null;
			type = MagicFilterType.NONE;
		}
	}

//********************************************************************************
//********************************************************************************

	@Override
	public final void run() {
		if (DEBUG) Log.i(TAG, "RenderHandler thread started:");
		synchronized (mSync) {
			mRequestSetEglContext = mRequestRelease = false;
			mRequestDraw = 0;
			mSync.notifyAll();
		}
        boolean localRequestDraw;

        for (;;) {

        	synchronized (mSync) {
        		if (mRequestRelease) break;
	        	if (mRequestSetEglContext) {
	        		mRequestSetEglContext = false;
	        		internalPrepare();
	        	}
	        	localRequestDraw = mRequestDraw > 0;
	        	if (localRequestDraw) {
	        		mRequestDraw--;
//					mSync.notifyAll();
				}
        	}
        	if (localRequestDraw) {

        		if ((mEglCore != null) && mTextureId >= 0) {
					handleFrameAvailable();
        		}
        	} else {
        		synchronized(mSync) {
        			try {
						mSync.wait();
					} catch (final InterruptedException e) {
						break;
					}
        		}
        	}
        }

        synchronized (mSync) {
        	mRequestRelease = true;
            internalRelease();
            mSync.notifyAll();
        }
		if (DEBUG) Log.i(TAG, "RenderHandler thread finished:");
	}

	private final void internalPrepare() {
		if (DEBUG) Log.i(TAG, "internalPrepare:");
		internalRelease();
		prepareEncoder();

		mSurface = null;
		mSync.notifyAll();
	}

	private final void internalRelease() {
		releaseDraw();
	}

	private WindowSurface mInputWindowSurface;
	private MagicCameraInputFilter mInput;
	private EglCore mEglCore;
	private GPUImageFilter filter;

	private void prepareEncoder() {
		mEglCore = new EglCore(shared_context, EglCore.FLAG_RECORDABLE);

		mInputWindowSurface = new WindowSurface(mEglCore, mSurface, true);
		mInputWindowSurface.makeCurrent();

		mInput = new MagicCameraInputFilter();
		mInput.init();

		if(DEBUG) Log.i(TAG, "prepareEncoder end");
	}

	private MagicFilterType type = MagicFilterType.NONE;
	public void setFilter(MagicFilterType type) {

		synchronized (mSync) {
			if (mRequestRelease) return;

			this.type = type;

			if (type == null) {
				return;
			}

			filter = MagicFilterFactory.initFilters(type);
			if(filter != null){
				filter.init();
				filter.onInputSizeChanged(mWidth, mHeight);
				filter.onDisplaySizeChanged(mWidth, mHeight);
			}

			mSync.notifyAll();
		}

	}

	private void handleFrameAvailable() {

		float[] transform = new float[16];      // TODO - avoid alloc every frame
		st.getTransformMatrix(transform);
		long timestamp = st.getTimestamp();
		if (timestamp == 0) {
			// Seeing this after device is toggled off/on with power button.  The
			// first frame back has a zero timestamp.
			//
			// MPEG4Writer thinks this is cause to abort() in native code, so it's very
			// important that we just ignore the frame.
			Log.w(TAG, "HEY: got SurfaceTexture with timestamp of zero");
			return;
		}

		mInput.setTextureTransformMatrix(transform);

		if(filter == null) {
			mInput.onDrawFrame(mTextureId, gLCubeBuffer, gLTextureBuffer);
		}else {
			filter.onDrawFrame(mTextureId, gLCubeBuffer, gLTextureBuffer);
		}

//		mInputWindowSurface.setPresentationTime(timestamp);
		boolean isSend = mInputWindowSurface.swapBuffers();
	}

}
