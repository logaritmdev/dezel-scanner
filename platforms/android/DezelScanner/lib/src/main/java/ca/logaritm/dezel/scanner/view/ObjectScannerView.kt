package ca.logaritm.dezel.scanner.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.media.Image
import android.util.Log
import android.view.ViewGroup
import ca.logaritm.dezel.scanner.camera.Camera
import ca.logaritm.dezel.scanner.camera.CameraListener
import ca.logaritm.dezel.scanner.camera.view.CameraView
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

	/**
	 * @property capturing
	 * @since 0.1.0
	 * @hidden
	 */
	private var capturing: Boolean = false

	/**
	 * @property scanning
	 * @since 0.1.0
	 * @hidden
	 */
	private var scanning: Boolean = true

	/**
	 * @property started
	 * @since 0.1.0
	 * @hidden
	 */
	private var started: Boolean = false

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
		//this.scanner.preview.alpha = 0.75f

		this.scanner.debug = true
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
	 * @method startScanner
	 * @since 0.1.0
	 */
	open fun startScanner() {
		if (this.scanning == false) {
			this.scanning = true
			this.scanner.enabled = true
		}
	}

	/**
	 * @method stopScanner
	 * @since 0.1.0
	 */
	open fun stopScanner() {
		if (this.scanning) {
			this.scanning = false
			this.scanner.enabled = false
			this.listener?.onLoseDocument(this)
			this.listener?.onMissDocument(this)
		}
	}

	/**
	 * @method restartScanner
	 * @since 0.1.0
	 */
	open fun restartScanner() {
		this.scanner.restart()
	}

	/**
	 * @method toggleFlash
	 * @since 0.1.0
	 */
	open fun toggleFlash() {
		this.camera.toggleFlash()
	}

	/**
	 * @method captureImage
	 * @since 0.1.0
	 */
	open fun captureImage() {
		this.capturing = true
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

		if (this.started == false) {
			this.started = true
			this.post {
				this.listener?.onActivate(this)
			}
		}

		if (this.capturing) {
			this.capturing = false

			val bitmap = CameraView.convert(frame)
			if (bitmap != null) {

				val matrix = Matrix()
				matrix.postRotate(90f)
				val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

				this.post {
					this.listener?.onCaptureImage(this, rotated)
				}
			}

			return
		}

		this.scanner.process(frame)
	}

	/**
	 * @inherited
	 * @method onStartMoving
	 * @since 0.1.0
	 */
	override fun onStartMoving(camera: Camera) {
		if (this.scanning) {
			this.scanner.enabled = false
			this.scanner.reset()
		}
	}

	/**
	 * @inherited
	 * @method onStopMoving
	 * @since 0.1.0
	 */
	override fun onStopMoving(camera: Camera) {
		if (this.scanning) {
			this.scanner.enabled = true
			this.scanner.reset()
		}
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
