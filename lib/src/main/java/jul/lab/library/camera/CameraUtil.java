/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jul.lab.library.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraUtil {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final boolean IS_MUTE = false;

    public static boolean useTimeLapse(Context context){
        //음소거 모드거나 인코딩에 문제가 있는 단말(ex.갤럭시s2)일 경우에는 timelapse 방식으로 촬영하자.
        return IS_MUTE || problemDevice(context);
    }

    private static boolean problemDevice(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if(dm.widthPixels == 480 && dm.heightPixels == 800){
            return true;
        }

        return false;
    }

    public static CamcorderProfile getOptimalPreviewSizeDependOnCamcoder(Context context, List<Size> supportedPreviewSizes){
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

		CamcorderProfile profile;
		if (screenWidth >= 720) {
            if(useTimeLapse(context)){
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_720P);
            } else{
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            }
			for (Size size : supportedPreviewSizes) {
				if(profile.videoFrameWidth == size.width && profile.videoFrameHeight == size.height){
					return profile;
				}
			}
		}

        if(useTimeLapse(context)){
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_480P);
        } else{
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        }

		for (Size size : supportedPreviewSizes) {
			if(profile.videoFrameWidth == size.width && profile.videoFrameHeight == size.height){
				return profile;
			}
		}


		return null;
    }

    public static Camera.Size getOptimalPreviewSize(List<Size> sizes, int targetWidth, int targetHeight) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) targetWidth / targetHeight;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static Camera getDefaultCameraInstance() {
        return Camera.open();
    }


    public static Camera getDefaultBackFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public static Camera getDefaultFrontFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Camera getDefaultCamera(int position) {
        int  mNumberOfCameras = Camera.getNumberOfCameras();

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == position) {
                return Camera.open(i);

            }
        }

        return null;
    }

    public static File getOutputMediaFile(Context context, int type, String dirPath){
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return  null;
        }

        File mediaStorageDir = new File(dirPath);
        if (! mediaStorageDir.exists() || !mediaStorageDir.isDirectory()){
            if (! mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
