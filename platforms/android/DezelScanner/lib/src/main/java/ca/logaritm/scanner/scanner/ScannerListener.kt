package ca.logaritm.scanner.scanner

import android.graphics.Bitmap
import android.graphics.PointF

/**
 * @protocol ScannerListener
 * @since 0.1.0
 */
public interface ScannerListener {

	/**
	 * Called when a valid object is found.
	 * @method onFindObject
	 * @since 0.1.0
	 */
	fun onFindObject(detector: Scanner, shape: Array<PointF>, image: Bitmap)

	/**
	 * Called when a possible object is found.
	 * @method onSpotObject
	 * @since 0.1.0
	 */
	fun onSpotObject(detector: Scanner, shape: Array<PointF>)

	/**
	 * Called when an object is lost.
	 * @method onLoseObject
	 * @since 0.1.0
	 */
	fun onLoseObject(detector: Scanner)

	/**
	 * Called when nothing has been found.
	 * @method onLoseObject
	 * @since 0.1.0
	 */
	fun onMissObject(detector: Scanner)
}

