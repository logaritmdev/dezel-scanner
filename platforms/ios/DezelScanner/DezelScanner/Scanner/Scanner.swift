import AVFoundation
import Foundation

/**
 * @class Scanner
 * @since 0.1.0
 */
open class Scanner : NSObject  {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * The detector delegate.
	 * @property delegate
	 * @since 0.1.0
	 */
	open weak var delegate: ScannerDelegate?

	/**
	 * Whether the scanner is debugging.
	 * @property debug
	 * @since 0.1.0
	 */
	open var debug: Bool = false {
		willSet {
			DLScannerSetDebug(self.handle, newValue)
			self.preview.isHidden = newValue == false
		}
	}

	/**
	 * Whether the scanned is enabled.
	 * @property enabled
	 * @since 0.1.0
	 */
	open var enabled: Bool = true {
		willSet {
			DLScannerSetEnabled(self.handle, newValue)
		}
	}

	/**
	 * @property bitmap
	 * @since 0.1.0
	 * @hidden
	 */
	private(set) public var preview: UIImageView = UIImageView(frame: .zero)

	/**
	 * The sample buffer width.
	 * @property sampleBufferWidth
	 * @since 0.1.0
	 */
	private(set) public var sampleBufferWidth: CGFloat = 0

	/**
	 * The sample buffer height.
	 * @property sampleBufferHeight
	 * @since 0.1.0
	 */
	private(set) public var sampleBufferHeight: CGFloat = 0

	/**
	 * @property queue
	 * @since 0.1.0
	 * @hidden
	 */
	private var queue: DispatchQueue = DispatchQueue(label: "ca.logaritm.dezel.scanner.queue")

	/**
	 * @property handle
	 * @since 0.1.0
	 * @hidden
	 */
	private var handle: DLScannerRef!

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	public override init() {

		super.init()

		self.handle = DLScannerCreate()
		DLScannerSetFindObjectCallback(self.handle, findObjectCallback)
		DLScannerSetSpotObjectCallback(self.handle, spotObjectCallback)
		DLScannerSetLoseObjectCallback(self.handle, loseObjectCallback)
		DLScannerSetMissObjectCallback(self.handle, missObjectCallback)

		DLScannerSetData(self.handle, UnsafeMutableRawPointer(value: self))

		self.preview.isHidden = true
		self.preview.isOpaque = true
		self.preview.backgroundColor = UIColor.black
	}

	/**
	 * @destructor
	 * @since 0.1.0
	 */
	deinit {
		DLScannerGetData(self.handle).release()
		DLScannerDelete(self.handle)
	}

	/**
	 * Resets the current scanning session.
	 * @method reset
	 * @since 0.1.0
	 */
	open func reset() {
		DLScannerReset(self.handle)
	}

	/**
	 * Restarts the scanner afters its been stopped.
	 * @method restart
	 * @since 0.1.0
	 */
	open func restart() {
		DLScannerRestart(self.handle)
	}

	/**
	 * Process the specified frame.
	 * @method process
	 * @since 0.1.0
	 */
	open func process(_ buffer: CMSampleBuffer) {

		self.queue.async { [unowned self] in

			guard let image = CGImageCreateWithCMSampleBuffer(buffer) else {
				return
			}

			self.sampleBufferWidth = CGFloat(image.width)
			self.sampleBufferHeight = CGFloat(image.height)

			DLScannerProcessFrame(self.handle, Int32(image.width), Int32(image.height), image)

			if (self.debug) {
				if let data = DLScannerGetDebuggingImage(self.handle) {
					self.didProcessFrame(image: UIImage(pointer: data))
				}
			}
		}
	}

	//--------------------------------------------------------------------------
	// MARK: Private API
	//--------------------------------------------------------------------------

	/**
	 * @method didProcessFrame
	 * @since 0.1.0
	 * @hidden
	 */
	fileprivate func didProcessFrame(image: UIImage) {
		DispatchQueue.main.async {
			self.preview.image = image
		}
	}

	/**
	 * @method didFindObject
	 * @since 0.1.0
	 * @hidden
	 */
	fileprivate func didFindObject(shape: [CGPoint], image: UIImage) {
		DispatchQueue.main.async {
			self.delegate?.didFindObject(detector: self, shape: shape, image: image)
		}
	}

	/**
	 * @method didSpotObject
	 * @since 0.1.0
	 * @hidden
	 */
	fileprivate func didSpotObject(shape: [CGPoint]) {
		DispatchQueue.main.async {
			self.delegate?.didSpotObject(detector: self, shape: shape)
		}
	}

	/**
	 * @method didLoseObject
	 * @since 0.1.0
	 * @hidden
	 */
	fileprivate func didLoseObject() {
		DispatchQueue.main.async {
			self.delegate?.didLoseObject(detector: self)
		}
	}

	/**
	 * @method didMissObject
	 * @since 0.1.0
	 * @hidden
	 */
	fileprivate func didMissObject() {
		DispatchQueue.main.async {
			self.delegate?.didMissObject(detector: self)
		}
	}
}

/**
 * @since 0.1.0
 * @hidden
 */
private let findObjectCallback : @convention(c) (DLScannerRef?, Double, Double, Double, Double, Double, Double, Double, Double) -> Void = { ptr, x1, y1, x2, y2, x3, y3, x4, y4 in

	if let detector = DLScannerGetData(ptr).value as? Scanner {

		var shape = [CGPoint]()
		shape.append(CGPoint(x: x1, y: y1))
		shape.append(CGPoint(x: x2, y: y2))
		shape.append(CGPoint(x: x3, y: y3))
		shape.append(CGPoint(x: x4, y: y4))

		detector.didFindObject(shape: shape, image: UIImage(pointer: DLScannerGetExtractedImage(ptr)))
	}
}

/**
 * @since 0.1.0
 * @hidden
 */
private let spotObjectCallback : @convention(c) (DLScannerRef?, Double, Double, Double, Double, Double, Double, Double, Double) -> Void = { ptr, x1, y1, x2, y2, x3, y3, x4, y4 in

	if let detector = DLScannerGetData(ptr).value as? Scanner {

		var shape = [CGPoint]()
		shape.append(CGPoint(x: x1, y: y1))
		shape.append(CGPoint(x: x2, y: y2))
		shape.append(CGPoint(x: x3, y: y3))
		shape.append(CGPoint(x: x4, y: y4))

		detector.didSpotObject(shape: shape)
	}
}

/**
 * @since 0.1.0
 * @hidden
 */
private let loseObjectCallback : @convention(c) (DLScannerRef?) -> Void = { ptr in
	if let detector = DLScannerGetData(ptr).value as? Scanner {
		detector.didLoseObject()
	}
}

/**
 * @since 0.1.0
 * @hidden
 */
private let missObjectCallback : @convention(c) (DLScannerRef?) -> Void = { ptr in
	if let detector = DLScannerGetData(ptr).value as? Scanner {
		detector.didMissObject()
	}
}
