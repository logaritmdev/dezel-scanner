import AVFoundation

/**
 * @protocol CameraDelegate
 * @since 0.1.0
 */
public protocol CameraDelegate : class {

	/**
	 * Called when a frame is captured.
	 * @method didCaptureFrame
	 * @since 0.1.0
	 */
	func didCaptureFrame(camera: Camera, buffer: CMSampleBuffer)

	/**
	 * Called when the camera started moving.
	 * @method didStartMoving
	 * @since 0.1.0
	 */
	func didStartMoving(camera: Camera)

	/**
	 * Called when the camera stopped moving.
	 * @method didStopMoving
	 * @since 0.1.0
	 */
	func didStopMoving(camera: Camera)
}
