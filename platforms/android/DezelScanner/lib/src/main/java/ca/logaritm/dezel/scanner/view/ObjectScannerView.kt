package ca.logaritm.dezel.scanner.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.media.Image
import android.util.Log
import android.view.ViewGroup
import ca.logaritm.dezel.scanner.camera.Camera
import ca.logaritm.dezel.scanner.camera.CameraListener
import ca.logaritm.dezel.scanner.scanner.Scanner
import ca.logaritm.dezel.scanner.scanner.ScannerListener
import ca.logaritm.dezel.view.graphic.Convert

open class ObjectScannerView(context: Context) : ViewGroup(context), CameraListener, ScannerListener {

	//--------------------------------------------------------------------------
	// Static
	//--------------------------------------------------------------------------

	companion object {
		init {
			System.loadLibrary("dezel_scanner")
		}
	}

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The object scanner listener.
	 * @property listener
	 * @since 0.1.0
	 */
	open var listener: ObjectScannerViewListener? = null

	/**
	 * @property camera
	 * @since 0.1.0
	 * @hidden
	 */
	private var camera: Camera = Camera(context)

	/**
	 * @property scanner
	 * @since 0.1.0
	 * @hidden
	 */
	private var scanner: Scanner = Scanner(context)

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	init {
		this.addView(this.camera.preview)
		this.addView(this.scanner.preview)
		this.camera.listener = this
		this.scanner.listener = this
		this.scanner.debug = true
		this.scanner.preview.alpha = 0.5f
	}

	/**
	 * @method startCamera
	 * @since 0.1.0
	 * @hidden
	 */
	open fun startCamera() {
		this.camera.start()
	}

	/**
	 * @method stopCamera
	 * @since 0.1.0
	 * @hidden
	 */
	open fun stopCamera() {
		this.camera.stop()
	}

	/**
	 * @method enableScanner
	 * @since 0.1.0
	 */
	open fun enableScanner() {
		this.scanner.enabled = true
	}

	/**
	 * @method disableScanner
	 * @since 0.1.0
	 */
	open fun disableScanner() {
		this.scanner.enabled = false
	}

	/**
	 * @method restartScanner
	 * @since 0.1.0
	 */
	open fun restartScanner() {
		this.scanner.reset()
	}

	/**
	 * @inherited
	 * @method onMeasure
	 * @since 0.1.0
	 */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		this.camera.preview.measure(widthMeasureSpec, heightMeasureSpec)
		this.scanner.preview.measure(widthMeasureSpec, heightMeasureSpec)
	}

	/**
	 * @inherited
	 * @method onLayout
	 * @since 0.1.0
	 */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		this.camera.preview.layout(l, t, r, b)
		this.scanner.preview.layout(l, t, r, b)
	}

	//--------------------------------------------------------------------------
	// Camera Listener
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onCaptureFrame
	 * @since 0.1.0
	 */
	override fun onCaptureFrame(camera: Camera, frame: Image) {
		this.scanner.process(frame)
	}

	/**
	 * @inherited
	 * @method onStartMoving
	 * @since 0.1.0
	 */
	override fun onStartMoving(camera: Camera) {
		this.scanner.enabled = false
		this.scanner.reset()
	}

	/**
	 * @inherited
	 * @method onStopMoving
	 * @since 0.1.0
	 */
	override fun onStopMoving(camera: Camera) {
		this.scanner.enabled = true
		this.scanner.reset()
	}

	//--------------------------------------------------------------------------
	// Scanner Listener
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onFindObject
	 * @since 0.1.0
	 */
	override fun onFindObject(detector: Scanner, shape: Array<PointF>, image: Bitmap) {
		this.listener?.onFindDocument(this, this.transform(shape), image)
	}

	/**
	 * @inherited
	 * @method onSpotObject
	 * @since 0.1.0
	 */
	override fun onSpotObject(detector: Scanner, shape: Array<PointF>) {
		this.listener?.onSpotDocument(this, this.transform(shape))
	}

	/**
	 * @inherited
	 * @method onLoseObject
	 * @since 0.1.0
	 */
	override fun onLoseObject(detector: Scanner) {
		this.listener?.onLoseDocument(this)
	}

	/**
	 * @inherited
	 * @method onMissObject
	 * @since 0.1.0
	 */
	override fun onMissObject(detector: Scanner) {
		this.listener?.onMissDocument(this)
	}

	//--------------------------------------------------------------------------
	// Private API
	//--------------------------------------------------------------------------

	/**
	 * @method transform
	 * @since 0.1.0
	 * @hidden
	 */
	private fun transform(points: Array<PointF>): Array<PointF> {

		val transformed = mutableListOf<PointF>()

		for (point in points) {
			val p = this.camera.convert(point)
			transformed.add(PointF(
				Convert.toDp(p.x),
				Convert.toDp(p.y)
			))
		}

		return transformed.toTypedArray()
	}
}
