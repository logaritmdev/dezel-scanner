package ca.logaritm.dezel.scanner.camera.view;

import android.media.Image;

public interface CameraViewListener {
	void onCaptureFrame(Image frame);
}
