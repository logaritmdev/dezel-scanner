import Dezel
import AVFoundation

open class DocumentScannerView : View, ObjectScannerViewDelegate {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * Whether the scanner's camera services were requested.
	 * @property requested
	 * @since 0.1.0
	 */
	private(set) public var requested: Bool = false

	/**
	 * Whether the scanner's camera services were authorized.
	 * @property authorized
	 * @since 0.1.0
	 */
	private(set) public var authorized: Bool = false

	/**
	 * @property view
	 * @since 0.1.0
	 * @hidden
	 */
	private var view: ObjectScannerView {
		return self.content as! ObjectScannerView
	}

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	public required init(context: JavaScriptContext) {
		super.init(context: context)
		NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterForeground), name: UIApplication.willEnterForegroundNotification, object: nil)
		NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
		self.view.delegate = self
	}

	/**
	 * Removes listeners upon destruction.
	 * @destructor
	 * @since 0.1.0
	 */
	deinit {
		NotificationCenter.default.removeObserver(self)
	}

	/**
	 * @inherited
	 * @method createContentView
	 * @since 0.5.0
	 */
	override open func createContentView() -> UIView {
		return ObjectScannerView(frame: .zero)
	}

	/**
     * @method updateServiceStatus
     * @since 0.1.0
     * @hidden
     */
	open func updateServiceStatus() {

		if (self.isServiceRequested() == false) {
			return
		}

		let authorized = self.isServiceAuthorized()

		if (self.requested == false || self.authorized != authorized) {

			self.requested = true
			self.authorized = authorized

			self.property("requested", boolean: self.requested)
			self.property("authorized", boolean: self.authorized)

			if (authorized) {
				self.holder.callMethod("nativeAuthorize")
			} else {
				self.holder.callMethod("nativeUnauthorize")
			}
		}
	}

	/**
	 * @method applicationDidEnterBackground
	 * @since 0.1.0
 	 * @hidden
	 */
	@objc open func applicationDidEnterBackground() {

	}

	/**
	 * @method applicationDidEnterForeground
	 * @since 0.1.0
 	 * @hidden
	 */
	@objc open func applicationDidEnterForeground() {
		self.updateServiceStatus()
	}

	//--------------------------------------------------------------------------
	// MARK: Content Camera View Delegate
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method didActivate
	 * @since 0.1.0
	 */
	open func didActivate(view: ObjectScannerView) {
		self.holder.callMethod("nativeOnActivate")
	}

	/**
	 * @inherited
	 * @method didFindDocument
	 * @since 0.1.0
	 */
	open func didFindDocument(view: ObjectScannerView, shape: [CGPoint], image: UIImage) {

		var length = 0
		let points = self.context.createEmptyArray()

		for point in shape {

			let p = self.context.createEmptyObject()
			p.property("x", number: point.x)
			p.property("y", number: point.y)
			points.property(length, value: p)

			length += 1
		}

		self.holder.callMethod("nativeOnFindDocument", arguments: [points, Image.with(image, in: self.context).holder])
	}

	/**
	 * @inherited
	 * @method didSpotDocument
	 * @since 0.1.0
	 */
	open func didSpotDocument(view: ObjectScannerView, shape: [CGPoint]) {

		var length = 0
		let points = self.context.createEmptyArray()

		for point in shape {

			let p = self.context.createEmptyObject()
			p.property("x", number: point.x)
			p.property("y", number: point.y)
			points.property(length, value: p)

			length += 1
		}

		self.holder.callMethod("nativeOnSpotDocument", arguments: [points])
	}

	/**
	 * @inherited
	 * @method didLoseDocument
	 * @since 0.1.0
	 */
	open func didLoseDocument(view: ObjectScannerView) {
		self.holder.callMethod("nativeOnLoseDocument")
	}

	/**
	 * @inherited
	 * @method didMissDocument
	 * @since 0.1.0
	 */
	open func didMissDocument(view: ObjectScannerView) {
		self.holder.callMethod("nativeOnMissDocument")
	}

	/**
	 * @inherited
	 * @method didCaptureImage
	 * @since 0.1.0
	 */
	open func didCaptureImage(view: ObjectScannerView, image: UIImage) {
		self.holder.callMethod("nativeOnCaptureImage", arguments: [Image.with(image, in: self.context).holder])
	}

	//--------------------------------------------------------------------------
	// MARK: Private API
	//--------------------------------------------------------------------------

	/**
     * @method isServiceRequested
     * @since 0.1.0
	 * @hidden
     */
	private func isServiceRequested() -> Bool {
		return AVCaptureDevice.authorizationStatus(for: .video) != .notDetermined
	}

	/**
     * @method isServiceAuthorized
     * @since 0.1.0
	 * @hidden
     */
	private func isServiceAuthorized() -> Bool {
		return AVCaptureDevice.authorizationStatus(for: .video) == .authorized
	}

	//--------------------------------------------------------------------------
	// MARK: JavaScript Methods
	//--------------------------------------------------------------------------

	/**
     * @method jsFunction_constructor
     * @since 0.1.0
     * @hidden
     */
	@objc override open func jsFunction_constructor(callback: JavaScriptFunctionCallback) {
		super.jsFunction_constructor(callback: callback)
		self.requested = self.isServiceRequested()
		self.authorized = self.isServiceAuthorized()
		self.property("requested", boolean: self.requested)
		self.property("authorized", boolean: self.authorized)
		self.updateServiceStatus()
	}

	/**
	 * @method jsFunction_requestAuthorization
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_requestAuthorization(callback: JavaScriptFunctionCallback) {
		AVCaptureDevice.requestAccess(for: .video) { _ in
			self.updateServiceStatus()
		}
	}

	/**
	 * @method jsFunction_startCamera
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_startCamera(callback: JavaScriptFunctionCallback) {
		self.view.startCamera()
	}

	/**
	 * @method jsFunction_stopCamera
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_stopCamera(callback: JavaScriptFunctionCallback) {
		self.view.stopCamera()
	}

	/**
	 * @method jsFunction_startScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_startScanner(callback: JavaScriptFunctionCallback) {
		self.view.startScanner()
	}

	/**
	 * @method jsFunction_stopScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_stopScanner(callback: JavaScriptFunctionCallback) {
		self.view.stopScanner()
	}

	/**
	 * @method jsFunction_restartScanner
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_restartScanner(callback: JavaScriptFunctionCallback) {
		self.view.restartScanner()
	}

	/**
	 * @method jsFunction_toggleFlash
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_toggleFlash(callback: JavaScriptFunctionCallback) {
		self.view.toggleFlash()
	}

	/**
	 * @method jsFunction_captureImage
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_captureImage(callback: JavaScriptFunctionCallback) {
		self.view.captureImage()
	}
}
