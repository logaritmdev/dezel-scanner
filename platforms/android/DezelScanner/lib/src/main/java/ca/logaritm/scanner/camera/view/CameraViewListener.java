package ca.logaritm.scanner.camera.view;

import android.media.Image;

public interface CameraViewListener {
	void onCaptureFrame(Image frame);
}
