package jul.lab.library.camera.camcoder;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jul.lab.library.R;
import jul.lab.library.camera.CameraUtil;
import jul.lab.library.log.Log;

public class CamcoderPreview extends SurfaceView implements
        SurfaceHolder.Callback, View.OnTouchListener {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private ImageView mIvFocus;
    private Drawable mFocusBitmap;

    public enum Orientation {
        PORTRAIT(90), PORTRAIT_REVERSE(270), LANDSCAPE_LEFT(0), LANDSCAPE_RIGHT(
                180);

        private int mNumber;

        Orientation(int v) {
            mNumber = v;
        }

        public int toNumber() {
            return mNumber;
        }
    }

    public CamcoderPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        mFocusBitmap = getContext().obtainStyledAttributes(attrs, R.styleable.Camera).getDrawable(R.styleable.Camera_focusDrawable);
        Log.e("w = " + mFocusBitmap.getIntrinsicWidth() + "/h = " + mFocusBitmap.getIntrinsicHeight());

        init();
    }

    private void init(){
        mCamera = CameraUtil.getDefaultCameraInstance();
        mCamera.setDisplayOrientation(Orientation.PORTRAIT.toNumber());

        mIvFocus = new ImageView(getContext());
        mIvFocus.setImageDrawable(mFocusBitmap);

        mHolder = getHolder();
        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setOnTouchListener(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mViewWidth = 0;
        mViewHeight = 0;
    }

    CamcorderProfile mCamcorderProfile;
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            return;
        }

        mViewWidth = w;
        mViewHeight = h;

        stopPreview();

        try {
            Camera.Parameters parameters = mCamera.getParameters();
//			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//			for (Size size : mSupportedPreviewSizes) {
//				Log.e("test", size.width+"/"+size.height);
//			}
            Camera.Size s = CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, w, h);

            mCamcorderProfile = CameraUtil.getOptimalPreviewSizeDependOnCamcoder(getContext(), supportedPreviewSizes);
            if(mCamcorderProfile != null){
                s.width = mCamcorderProfile.videoFrameWidth;
                s.height = mCamcorderProfile.videoFrameHeight;
            }
            parameters.setPreviewSize(s.width, s.height);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mHolder);

            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //	public List<Camera.Size> getSupportedPreviewSizes(){
//		return mSupportedPreviewSizes;
//	}
//
    public CamcorderProfile getCamcorderProfile() {
        return mCamcorderProfile;
    }

    private void startPreview() {
        if(mViewWidth == 0 || mViewHeight == 0){
            return;
        }
        try {
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean bAutoFocusEnable = true;

    public void setAutoFocusEnable(boolean enable){
        bAutoFocusEnable = enable;
    }

    private void stopPreview() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release(){
        stopPreview();

        mCamera.release();
        mCamera = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
//			stopPreview();
            float x = event.getX();
            float y = event.getY();

            addFocusView(x, y);

            Rect touchRect = new Rect((int)(x - 100),
                    (int)(y - 100),
                    (int)(x + 100),
                    (int)(y + 100));

            Rect targetFocusRect = new Rect(touchRect.left * 2000 / getWidth() - 1000,
                    touchRect.top * 2000 / getHeight() - 1000,
                    touchRect.right * 2000 / getWidth() - 1000,
                    touchRect.bottom * 2000 / getHeight() - 1000);

            if(targetFocusRect.left > 1000){
                targetFocusRect.left = 1000;
            } else if(targetFocusRect.left < -1000){
                targetFocusRect.left = -1000;
            }

            if(targetFocusRect.top > 1000){
                targetFocusRect.top = 1000;
            } else if(targetFocusRect.top < -1000){
                targetFocusRect.top = -1000;
            }

            if(targetFocusRect.right > 1000){
                targetFocusRect.right = 1000;
            } else if(targetFocusRect.right < -1000){
                targetFocusRect.right = -1000;
            }

            if(targetFocusRect.bottom > 1000){
                targetFocusRect.bottom = 1000;
            } else if(targetFocusRect.bottom < -1000){
                targetFocusRect.bottom = -1000;
            }

            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            focusList.add(new Camera.Area(targetFocusRect, 1000));

            Camera.Parameters param = null;
            try{
                param = mCamera.getParameters();
            }catch(RuntimeException e){
                return false;
            }
            param.setFocusAreas(focusList);
            param.setMeteringAreas(focusList);

            mCamera.setParameters(param);

            mCamera.autoFocus(new Camera.AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success){
                        camera.cancelAutoFocus();

                        mIvFocus.removeCallbacks(mRemoveFocusViewRunnable);
                        mIvFocus.post(mRemoveFocusViewRunnable);
                    }
                }
            });

//			startPreview();
        }
        return false;
    }

    private Runnable mRemoveFocusViewRunnable = new Runnable() {

        @Override
        public void run() {
            removeFocusView();
        }
    };

    private void removeFocusView(){
        if(mIvFocus.getParent() != null){
            ((ViewGroup)mIvFocus.getParent()).removeView(mIvFocus);
        }
    }

    private void addFocusView(float x, float y){
        removeFocusView();

        ViewGroup parent = (ViewGroup) getParent();

        parent.addView(mIvFocus);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mIvFocus.getLayoutParams();
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.leftMargin = (int) (x - (mFocusBitmap.getIntrinsicWidth()/2.0f));
        params.topMargin = (int) (y - (mFocusBitmap.getIntrinsicHeight()/2.0f));

        mIvFocus.setLayoutParams(params);

        mIvFocus.postDelayed(mRemoveFocusViewRunnable, 3000);
    }
}
