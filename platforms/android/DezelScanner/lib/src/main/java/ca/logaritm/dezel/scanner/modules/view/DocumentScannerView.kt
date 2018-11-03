package ca.logaritm.dezel.scanner.modules.view

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import ca.logaritm.dezel.R
import ca.logaritm.dezel.core.JavaScriptContext
import ca.logaritm.dezel.core.JavaScriptFunctionCallback
import ca.logaritm.dezel.modules.graphic.Image
import ca.logaritm.dezel.modules.view.View
import ca.logaritm.dezel.scanner.view.ObjectScannerView
import ca.logaritm.dezel.scanner.view.ObjectScannerViewListener


open class DocumentScannerView(context: JavaScriptContext) : View(context), ObjectScannerViewListener {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * Whether the scanner's camera services were requested.
	 * @property requested
	 * @since 0.1.0
	 */
	public var requested: Boolean = false
		private set

	/**
	 * Whether the scanner's camera services were authorized.
	 * @property authorized
	 * @since 0.1.0
	 */
	public var authorized: Boolean = false
		private set

	/**
	 * @property view
	 * @since 0.1.0
	 * @hidden
	 */
	private val view: ObjectScannerView
		get() = this.content as ObjectScannerView

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @property preferences
	 * @since 0.1.0
	 * @hidden
	 */
	private var preferences: SharedPreferences = this.context.application.getSharedPreferences("dezel.scanner", android.content.Context.MODE_PRIVATE)

	/**
	 * @property applicationEnterForegroundReceiver
	 * @since 0.1.0
	 * @hidden
	 */
	private val applicationEnterForegroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			updateServieStatus()
		}
	}

	/**
	 * @property applicationEnterBackgroundReceiver
	 * @since 0.1.0
	 * @hidden
	 */
	private val applicationEnterBackgroundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			updateServieStatus()
		}
	}

	/**
	 * @property applicationPermissionChangedReceiver
	 * @since 0.1.0
	 * @hidden
	 */
	private val applicationPermissionChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			val permission = intent.getStringExtra("permission")
			if (permission == Manifest.permission.CAMERA) {
				updateServieStatus()
			}
		}
	}

	/**
	 * @constructor
	 * @since 0.1.0
	 * @hidden
	 */
	init {
		LocalBroadcastManager.getInstance(this.context.application).registerReceiver(this.applicationEnterBackgroundReceiver, IntentFilter("dezel.application.BACKGROUND"))
		LocalBroadcastManager.getInstance(this.context.application).registerReceiver(this.applicationEnterForegroundReceiver, IntentFilter("dezel.application.FOREGROUND"))
		LocalBroadcastManager.getInstance(this.context.application).registerReceiver(this.applicationPermissionChangedReceiver, IntentFilter("dezel.application.PERMISSION_CHANGED"))
		this.view.listener = this
	}

	/**
	 * @destructor
	 * @since 0.1.0
	 * @hidden
	 */
	@Throws(Throwable::class)
	override fun finalize() {
		this.dispose()
		LocalBroadcastManager.getInstance(this.context.application).unregisterReceiver(this.applicationEnterBackgroundReceiver)
		LocalBroadcastManager.getInstance(this.context.application).unregisterReceiver(this.applicationEnterForegroundReceiver)
		LocalBroadcastManager.getInstance(this.context.application).unregisterReceiver(this.applicationPermissionChangedReceiver)
	}

	/**
	 * @inherited
	 * @method createView
	 * @since 0.1.0
	 */
	override fun createView(): ObjectScannerView {
		return ObjectScannerView(this.context.application)
	}

	/**
	 * Called when the status of the location service changes.
	 * @method updateServieStatus
	 * @since 0.1.0
	 */
	open fun updateServieStatus() {

		if (this.isServiceRequested() == false) {
			return
		}

		val authorized = this.isServiceAuthorized()

		if (this.requested == false || this.authorized != authorized) {

			this.requested = true
			this.authorized = authorized

			this.property("requested", this.requested)
			this.property("authorized", this.authorized)

			if (authorized) {
				this.holder.callMethod("nativeAuthorize")
			} else {
				this.holder.callMethod("nativeUnauthorize")
			}
		}
	}

	//--------------------------------------------------------------------------
	// Content Camera View Delegate
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onActivate
	 * @since 0.1.0
	 */
	override fun onActivate(view: ObjectScannerView) {
		this.holder.callMethod("nativeOnActivate")
	}

	/**
	 * @inherited
	 * @method onFindDocument
	 * @since 0.1.0
	 */
	override fun onFindDocument(view: ObjectScannerView, shape: Array<PointF>, image: Bitmap) {

		var length = 0
		val points = this.context.createEmptyArray()

		for (point in shape) {

			val p = this.context.createEmptyObject()
			p.property("x", point.x)
			p.property("y", point.y)
			points.property(length, p)

			length += 1
		}

		this.holder.callMethod("nativeOnFindDocument", arrayOf(points, Image.with(image, this.context).holder))
	}

	/**
	 * @inherited
	 * @method onSpotDocument
	 * @since 0.1.0
	 */
	override fun onSpotDocument(view: ObjectScannerView, shape: Array<PointF>) {

		var length = 0
		val points = this.context.createEmptyArray()

		for (point in shape) {

			val p = this.context.createEmptyObject()
			p.property("x", point.x)
			p.property("y", point.y)
			points.property(length, p)

			length += 1
		}

		this.holder.callMethod("nativeOnSpotDocument", arrayOf(points))
	}

	/**
	 * @inherited
	 * @method onLoseDocument
	 * @since 0.1.0
	 */
	override fun onLoseDocument(view: ObjectScannerView) {
		this.holder.callMethod("nativeOnLoseDocument")
	}

	/**
	 * @inherited
	 * @method onMissDocument
	 * @since 0.1.0
	 */
	override fun onMissDocument(view: ObjectScannerView) {
		this.holder.callMethod("nativeOnMissDocument")
	}

	/**
	 * @inherited
	 * @method onCaptureImage
	 * @since 0.1.0
	 */
	override fun onCaptureImage(view: ObjectScannerView, image: Bitmap) {
		this.holder.callMethod("nativeOnCaptureImage", arrayOf(Image.with(image, this.context).holder))
	}

	//--------------------------------------------------------------------------
	// Privat API
	//--------------------------------------------------------------------------

	/**
	 * @method isServiceRequested
	 * @since 0.1.0
	 * @hidden
	 */
	private fun isServiceRequested(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.preferences.getBoolean("requested", false) else true
	}

	/**
	 * @method isServiceAuthorized
	 * @since 0.1.0
	 * @hidden
	 */
	private fun isServiceAuthorized(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.context.application.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED else true
	}

	/**
	 * @method getAlertTitle
	 * @since 0.1.0
	 * @hidden
	 */
	private fun getAlertTitle(): String {

		try {

			return this.context.application.getString(
				this.context.application.resources.getIdentifier("dezel_scanner_camera_usage_title", "string", this.context.application.packageName)
			)

		} catch (e: Exception) {

		}

		return this.context.application.getString(R.string.app_name)
	}

	/**
	 * @method getAlertMessage
	 * @since 0.1.0
	 * @hidden
	 */
	private fun getAlertMessage(): String {

		try {

			return this.context.application.getString(
				this.context.application.resources.getIdentifier("dezel_scanner_camera_usage_description", "string", this.context.application.packageName)
			)

		} catch (e: Exception) {

		}

		return "This application requires the usage of the camera"
	}

	//--------------------------------------------------------------------------
	// JavaScript Methods
	//--------------------------------------------------------------------------

	/**
	 * @method jsFunction_constructor
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	override fun jsFunction_constructor(callback: JavaScriptFunctionCallback) {
		super.jsFunction_constructor(callback)
		this.requested = this.isServiceRequested()
		this.authorized = this.isServiceAuthorized()
		this.property("requested", this.requested)
		this.property("authorized", this.authorized)
		this.updateServieStatus()
	}

	/**
	 * @method jsFunction_requestAuthorization
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_requestAuthorization(callback: JavaScriptFunctionCallback) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return
		}

		if (this.isServiceRequested()) {
			return
		}

		val editor = this.preferences.edit()
		if (editor != null) {
			editor.putBoolean("requested", true)
			editor.apply()
		}

		val request = {
			this.context.application.requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
		}

		AlertDialog.Builder(this.context.application)
			.setTitle(this.getAlertTitle())
			.setMessage(this.getAlertMessage())
			.setPositiveButton("OK") { _, _ -> request() }
			.create()
			.show()
	}

	/**
	 * @method jsFunction_startCamera
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_startCamera(callback: JavaScriptFunctionCallback) {
		this.view.startCamera()
	}

	/**
	 * @method jsFunction_stopCamera
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_stopCamera(callback: JavaScriptFunctionCallback) {
		this.view.stopCamera()
	}

	/**
	 * @method jsFunction_startScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_startScanner(callback: JavaScriptFunctionCallback) {
		this.view.startScanner()
	}

	/**
	 * @method jsFunction_stopScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_stopScanner(callback: JavaScriptFunctionCallback) {
		this.view.stopScanner()
	}

	/**
	 * @method jsFunction_restartScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_restartScanner(callback: JavaScriptFunctionCallback) {
		this.view.restartScanner()
	}

	/**
	 * @method jsFunction_toggleFlash
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_toggleFlash(callback: JavaScriptFunctionCallback) {
		this.view.toggleFlash()
	}

	/**
	 * @method jsFunction_captureImage
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_captureImage(callback: JavaScriptFunctionCallback) {
		this.view.captureImage()
	}
}