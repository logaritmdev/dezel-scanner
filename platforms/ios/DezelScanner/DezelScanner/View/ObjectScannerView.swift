import AVFoundation
import Dezel

/**
 * @class ObjectScannerView
 * @since 0.1.0
 */
open class ObjectScannerView : UIView, CameraDelegate, ScannerDelegate {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * The content view delegate.
	 * @property delegate
	 * @since 0.1.0
	 */
	open weak var delegate: ObjectScannerViewDelegate?

	/**
	 * @property detector
	 * @since 0.1.0
	 * @hidden
	 */
	private var scanner: Scanner = Scanner()

	/**
	 * @property camera
	 * @since 0.1.0
	 * @hidden
	 */
	private var camera: Camera = Camera()

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	public override init(frame: CGRect) {

		super.init(frame: frame)

		self.camera.delegate = self
		self.scanner.delegate = self

		self.layer.addSublayer(self.camera.preview)
		self.addSubview(self.scanner.preview)
	}

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	required public init?(coder: NSCoder) {

		super.init(coder: coder)

		self.camera.delegate = self
		self.scanner.delegate = self

		self.layer.addSublayer(self.camera.preview)
		self.addSubview(self.scanner.preview)
	}

	/**
	 * @inherited
	 * @method layoutSubviews
	 * @since 0.1.0
	 */
	open override func layoutSubviews() {
		self.camera.preview.frame = self.bounds
		self.scanner.preview.frame = self.bounds
	}

	/**
	 * @method startCamera
	 * @since 0.1.0
	 */
	open func startCamera() {
		self.camera.start()
	}

	/**
	 * @method stopCamera
	 * @since 0.1.0
	 */
	open func stopCamera() {
		self.camera.stop()
	}

	/**
	 * @method enableScanner
	 * @since 0.1.0
	 */
	open func enableScanner() {
		self.scanner.enabled = true
	}

	/**
	 * @method disableScanner
	 * @since 0.1.0
	 */
	open func disableScanner() {
		self.scanner.enabled = false
	}

	/**
	 * @method restartScanner
	 * @since 0.1.0
	 */
	open func restartScanner() {
		self.scanner.restart()
	}

	//--------------------------------------------------------------------------
	// MARK: Methods - Animations
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method action
	 * @since 0.1.0
	 */
	override open func action(for layer: CALayer, forKey event: String) -> CAAction? {
		return Transition.action(for: layer, key: event)
	}

	//--------------------------------------------------------------------------
	// MARK: Camera Delegate
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method didCaptureFrame
	 * @since 0.1.0
	 */
	public func didCaptureFrame(camera: Camera, buffer: CMSampleBuffer) {
		self.scanner.process(buffer)
	}

	/**
	 * @inherited
	 * @method didStartMoving
	 * @since 0.1.0
	 */
	public func didStartMoving(camera: Camera) {
		self.scanner.enabled = false
		self.scanner.reset()
	}

	/**
	 * @inherited
	 * @method didStopMoving
	 * @since 0.1.0
	 */
	public func didStopMoving(camera: Camera) {
		self.scanner.enabled = true
		self.scanner.reset()
	}

	//--------------------------------------------------------------------------
	// MARK: Scanner Delegate
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method didFindObject
	 * @since 0.1.0
	 */
	public func didFindObject(detector: Scanner, shape: [CGPoint], image: UIImage) {
		self.delegate?.didFindDocument(view: self, shape: self.transform(shape), image: image)
	}

	/**
	 * @inherited
	 * @method didSpotObject
	 * @since 0.1.0
	 */
	public func didSpotObject(detector: Scanner, shape: [CGPoint]) {
		self.delegate?.didSpotDocument(view: self, shape: self.transform(shape))
	}

	/**
	 * @inherited
	 * @method didLoseObject
	 * @since 0.1.0
	 */
	public func didLoseObject(detector: Scanner) {
		self.delegate?.didLoseDocument(view: self)
	}

	/**
	 * @inherited
	 * @method didMissObject
	 * @since 0.1.0
	 */
	public func didMissObject(detector: Scanner) {
		self.delegate?.didMissDocument(view: self)
	}

	//--------------------------------------------------------------------------
	// MARK: Private API
	//--------------------------------------------------------------------------

	/**
	 * @method transform
	 * @since 0.1.0
	 * @hidden
	 */
	private func transform(_ points: [CGPoint]) -> [CGPoint] {

		let bw = self.scanner.sampleBufferWidth
		let bh = self.scanner.sampleBufferHeight

		let cx = bw / 2
		let cy = bh / 2

		var t = CGAffineTransform.identity

		if (self.scanner.debug) {
			t = t.translatedBy(x: +cx, y: +cy)
			t = t.scaledBy(x: bh / self.camera.preview.bounds.height, y: 1)
			t = t.translatedBy(x: -cx, y: -cy)
		}

		var result = [CGPoint]()
		result.append(self.camera.preview.layerPointConverted(fromCaptureDevicePoint: CGPoint(x: points[0].y / bh, y: 1 - points[0].x / bw)).applying(t))
		result.append(self.camera.preview.layerPointConverted(fromCaptureDevicePoint: CGPoint(x: points[1].y / bh, y: 1 - points[1].x / bw)).applying(t))
		result.append(self.camera.preview.layerPointConverted(fromCaptureDevicePoint: CGPoint(x: points[2].y / bh, y: 1 - points[2].x / bw)).applying(t))
		result.append(self.camera.preview.layerPointConverted(fromCaptureDevicePoint: CGPoint(x: points[3].y / bh, y: 1 - points[3].x / bw)).applying(t))

		return result
	}
}
