package ca.logaritm.dezel.scanner.camera

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Image
import android.util.Log
import ca.logaritm.dezel.scanner.camera.view.CameraView
import ca.logaritm.dezel.scanner.camera.view.CameraViewListener

open class Camera(context: Context): CameraViewListener, SensorEventListener {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The camera listener.
	 * @property preview
	 * @since 0.1.0
	 */
	public var listener: CameraListener? = null

	/**
	 * The camera preview layer.
	 * @property preview
	 * @since 0.1.0
	 */
	public var preview: CameraView = CameraView(context)

	/**
	 * @property sensorManager
	 * @since 0.1.0
	 * @hidden
	 */
	private var sensorManager: SensorManager

	/**
	 * @property accelerometer
	 * @since 0.1.0
	 * @hidden
	 */
	private var accelerometer: Sensor

	/**
	 * @property moving
	 * @since 0.1.0
	 * @hidden
	 */
	private var moving: Boolean = false

	/**
	 * @property processedFrameW
	 * @since 0.1.0
	 * @hidden
	 */
	private var processedFrameW: Int = 0

	/**
	 * @property processedFrameH
	 * @since 0.1.0
	 * @hidden
	 */
	private var processedFrameH: Int = 0

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	init {
		this.preview.setListener(this)
		this.sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
		this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
	}

	/**
	 * Starts the camera.
	 * @method start
	 * @since 0.1.0
	 */
	open fun start() {
		this.preview.start()
		this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
	}

	/**
	 * Stops the camera.
	 * @method stop
	 * @since 0.1.0
	 */
	open fun stop() {
		this.preview.stop()
		this.sensorManager.unregisterListener(this)
	}

	/**
	 * Toggles the flash
	 * @method toggleFlash
	 * @since 0.1.0
	 */
	open fun toggleFlash() {
		this.preview.toggleFlash()
	}

	/**
	 * Stops the camera.
	 * @method stop
	 * @since 0.1.0
	 */
	open fun convert(point: PointF): PointF {
		val sx = this.preview.width.toFloat() / this.processedFrameW.toFloat()
		val sy = this.preview.height.toFloat() / this.processedFrameH.toFloat()
		return PointF(point.x * sx, point.y * sy)
	}

	//--------------------------------------------------------------------------
	// Camera View Listener
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onCaptureFrame
	 * @since 0.1.0
	 */
	override fun onCaptureFrame(frame: Image) {
		this.processedFrameW = frame.width
		this.processedFrameH = frame.height
		this.listener?.onCaptureFrame(this, frame)
	}

	//--------------------------------------------------------------------------
	// Sensor Listener
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onSensorChanged
	 * @since 0.1.0
	 */
	override fun onSensorChanged(event: SensorEvent) {

		if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {

			val x = event.values[0].toDouble()
			val y = event.values[1].toDouble()
			val z = event.values[2].toDouble()

			var motion = Math.sqrt(x * x + y * y + z * z)
			if (motion < 0.1) {
				motion = 0.0
			}

			if (motion < 1.0) {
				if (this.moving == true) {
					this.moving = false
					this.listener?.onStopMoving(this)
				}
			} else {
				if (this.moving == false) {
					this.moving = true
					this.listener?.onStartMoving(this)
				}
			}
		}
	}

	/**
	 * @inherited
	 * @method onAccuracyChanged
	 * @since 0.1.0
	 */
	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

	}
}