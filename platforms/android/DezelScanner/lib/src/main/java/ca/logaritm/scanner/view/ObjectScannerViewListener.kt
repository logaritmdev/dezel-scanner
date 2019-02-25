package ca.logaritm.scanner.view

import android.graphics.Bitmap
import android.graphics.PointF

public interface ObjectScannerViewListener {

	/**
	 * Called when the video feed is activated.
	 * @method onFindDocument
	 * @since 0.1.0
	 */
	fun onActivate(view: ObjectScannerView)

	/**
	 * Called when a valid document is found.
	 * @method onFindDocument
	 * @since 0.1.0
	 */
	fun onFindDocument(view: ObjectScannerView, shape: Array<PointF>, image: Bitmap)

	/**
	 * Called when a possible document is found.
	 * @method onSpotObject
	 * @since 0.1.0
	 */
	fun onSpotDocument(view: ObjectScannerView, shape: Array<PointF>)

	/**
	 * Called when a document is lost.
	 * @method onLoseDocument
	 * @since 0.1.0
	 */
	fun onLoseDocument(view: ObjectScannerView)

	/**
	 * Called when nothing is found.
	 * @method onMissDocument
	 * @since 0.1.0
	 */
	fun onMissDocument(view: ObjectScannerView)

	/**
	 * Called when nothing is found.
	 * @method onCaptureImage
	 * @since 0.1.0
	 */
	fun onCaptureImage(view: ObjectScannerView, image: Bitmap)
}
