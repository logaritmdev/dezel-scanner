package ca.logaritm.dezel.scanner.camera

import android.media.Image

interface CameraListener {
	fun onCaptureFrame(camera: Camera, frame: Image)
	fun onStartMoving(camera: Camera)
	fun onStopMoving(camera: Camera)
}