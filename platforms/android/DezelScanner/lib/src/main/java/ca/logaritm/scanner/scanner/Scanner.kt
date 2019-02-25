package ca.logaritm.scanner.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.media.Image
import android.os.Handler
import android.view.View
import android.widget.ImageView
import ca.logaritm.dezel.extension.Delegates

/**
 * @class Scanner
 * @since 0.1.0
 */
open class Scanner(context: Context)  {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The scanner listener.
	 * @property listener
	 * @since 0.1.0
	 */
	open var listener: ScannerListener? = null

	/**
	 * Whether the scanner is in debug mode.
	 * @property debug
	 * @since 0.1.0
	 */
	open var debug: Boolean by Delegates.OnSet(false) { value ->
		ScannerExternal.setDebug(this.handle, value)
		this.preview.visibility = if (value == false) View.GONE else View.VISIBLE
	}

	/**
	 * Whether the scanned is enabled.
	 * @property enabled
	 * @since 0.1.0
	 */
	open var enabled: Boolean by Delegates.OnSet(true) { value ->
		ScannerExternal.setEnabled(this.handle, value)
	}

	/**
	 * The scanner preview layer.
	 * @property preview
	 * @since 0.1.0
	 */
	public var preview: ImageView = ImageView(context)
		private set

	/**
	 * @property handle
	 * @since 0.1.0
	 * @hidden
	 */
	private var handle: Long = 0L

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	init {
		this.handle = ScannerExternal.create(this)
	}

	/**
	 * @destructor
	 * @since 0.1.0
	 * @hidden
	 */
	@Throws(Throwable::class)
	protected fun finalize() {
		ScannerExternal.delete(this.handle)
	}

	/**
	 * Resets the current scanning session.
	 * @method reset
	 * @since 0.1.0
	 */
	open fun reset() {
		ScannerExternal.reset(this.handle)
	}

	/**
	 * Restarts the scanner afters its been stopped.
	 * @method restart
	 * @since 0.1.0
	 */
	open fun restart() {
		ScannerExternal.restart(this.handle)
	}

	/**
	 * Process the specified frame.
	 * @method process
	 * @since 0.1.0
	 */
	open fun process(frame: Image) {

		val planes = frame.planes
		if (planes[1].pixelStride != 1 &&
			planes[1].pixelStride != 2) {
			return
		}

		val nv21: ByteArray
		val yBuffer = frame.planes[0].buffer
		val uBuffer = frame.planes[1].buffer
		val vBuffer = frame.planes[2].buffer

		val ySize = yBuffer.remaining()
		val uSize = uBuffer.remaining()
		val vSize = vBuffer.remaining()

		nv21 = ByteArray(ySize + uSize + vSize)

		yBuffer.get(nv21, 0, ySize)
		vBuffer.get(nv21, ySize, vSize)
		uBuffer.get(nv21, ySize + vSize, uSize)

		ScannerExternal.process(this.handle, frame.width, frame.height, nv21)

		if (this.debug) {
			val bitmap = ScannerExternal.getDebuggingImage(this.handle)
			if (bitmap != null) {
				this.onProcessFrame(bitmap)
			}
		}
	}

	//--------------------------------------------------------------------------
	// Private API
	//--------------------------------------------------------------------------

	/**
	 * @property handler
	 * @since 0.1.0
	 * @hidden
	 */
	private var handler: Handler = Handler()

	/**
	 * @method didProcessFrame
	 * @since 0.1.0
	 * @hidden
	 */
	private fun onProcessFrame(image: Bitmap) {
		this.handler.post {
			this.preview.setImageBitmap(image)
		}
	}

	/**
	 * @method onFindObject
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	private fun onFindObject(shape: Array<PointF>, image: Bitmap) {
		this.handler.post {
			this.listener?.onFindObject(this, shape, image)
		}
	}

	/**
	 * @method didSpotObject
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	private fun onSpotObject(shape: Array<PointF>) {
		this.handler.post {
			this.listener?.onSpotObject(this, shape)
		}
	}

	/**
	 * @method didLoseObject
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	private fun onLoseObject() {
		this.handler.post {
			this.listener?.onLoseObject(this)
		}
	}

	/**
	 * @method didMissObject
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	private fun onMissObject() {
		this.handler.post {
			this.listener?.onMissObject(this)
		}
	}
}
