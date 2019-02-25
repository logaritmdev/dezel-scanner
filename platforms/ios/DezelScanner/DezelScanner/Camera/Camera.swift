import AVFoundation
import CoreMotion

/**
 * @class Camera
 * @since 0.1.0
 */
open class Camera : NSObject, AVCaptureVideoDataOutputSampleBufferDelegate {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * The camera delegate.
	 * @property preview
	 * @since 0.1.0
	 */
	public weak var delegate: CameraDelegate?

	/**
	 * The camera preview layer.
	 * @property preview
	 * @since 0.1.0cg
	 */
    private(set) public var preview: AVCaptureVideoPreviewLayer!

	/**
	 * The camera capture session.
	 * @property session
	 * @since 0.1.0
	 */
	private(set) public var session: AVCaptureSession = AVCaptureSession()

	/**
	 * The camera capture device.
	 * @property session
	 * @since 0.1.0
	 */
	private(set) public var device: AVCaptureDevice? = AVCaptureDevice.default(for: .video)

	/**
	 * @property queue
	 * @since 0.1.0
	 * @hidden
	 */
	private let queue: DispatchQueue = DispatchQueue(label: "Session_Queue")

	/**
	 * @property motionManager
	 * @since 0.1.0
	 * @hidden
	 */
	private let motionManager: CMMotionManager = CMMotionManager()

	/**
	 * @property configured
	 * @since 0.1.0
	 * @hidden
	 */
	private var configured: Bool = false

	/**
	 * @property moving
	 * @since 0.1.0
	 * @hidden
	 */
	private var moving: Bool = false

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	public override init() {

		super.init()

		self.motionManager.deviceMotionUpdateInterval = 0.25

		self.preview = AVCaptureVideoPreviewLayer(session: self.session)
		self.preview.videoGravity = .resizeAspectFill
	}

	/**
	 * Indicates whether the camera exists and is usable.
	 * @method isAvailable
	 * @since 0.1.0
	 */
	public func isAvailable() -> Bool {
		return self.device != nil
	}

	/**
	 * Indicates whether the camera authorization has been requested.
	 * @method isRequested
	 * @since 0.1.0
	 */
	public func isRequested() -> Bool {
		return AVCaptureDevice.authorizationStatus(for: .video) != .notDetermined
	}

	/**
	 * Indicates whether the camera authorization has been granted.
	 * @method isAuthorized
	 * @since 0.1.0
	 */
	public func isAuthorized() -> Bool {
		return AVCaptureDevice.authorizationStatus(for: .video) == .authorized
	}

	/**
	 * Starts the camera.
	 * @method start
	 * @since 0.1.0
	 */
	public func start() {

		self.queue.async { [unowned self] in
			self.configure()
			self.session.startRunning()
		}

		self.motionManager.startDeviceMotionUpdates(to: OperationQueue()) { [unowned self] (data, error) in
			
			if let acceleration = data?.userAcceleration {

				var motion = sqrt(acceleration.x * acceleration.x + acceleration.y * acceleration.y + acceleration.z * acceleration.z)
				if (motion < 0.01) {
					motion = 0.0
				}

				if (motion < 0.04) {
					if (self.moving == true) {
						self.moving = false
						self.delegate?.didStopMoving(camera: self)
					}
				} else {
					if (self.moving == false) {
						self.moving = true
						self.delegate?.didStartMoving(camera: self)
					}
				}
			}
		}
	}

	/**
	 * Stops the camera.
	 * @method stop
	 * @since 0.1.0
	 */
	public func stop() {

		self.queue.async { [unowned self] in
			self.session.stopRunning()
		}

		self.motionManager.stopDeviceMotionUpdates()
	}

	/**
	 * Toggles the flash.
	 * @method toggleFlash
	 * @since 0.1.0
	 */
	public func toggleFlash() {

		guard let device = self.device else {
			return
		}

		if (device.hasTorch) {

			do {

				try device.lockForConfiguration()

				if (device.torchMode == .on) {
					device.torchMode = .off
				} else {
					do {
						try device.setTorchModeOn(level: 1.0)
					} catch {
						print(error)
					}
				}

				device.unlockForConfiguration()

			} catch {
				print(error)
			}
		}
	}

	//--------------------------------------------------------------------------
	// MARK: Private API
	//--------------------------------------------------------------------------

	/**
	 * @method configure
	 * @since 0.1.0
	 * @hidden
	 */
	private func configure() {

		guard let device = self.device, self.isAuthorized(), self.configured == false else {
			return
		}

		self.session.beginConfiguration()
		self.session.sessionPreset = AVCaptureSession.Preset.hd1920x1080

		do {

            let input = try AVCaptureDeviceInput(device: device)

            if (self.session.canAddInput(input) == false) {
                self.session.commitConfiguration()
                print("Could not add video device input to the session.")
                return
            }

            self.session.addInput(input)

		} catch {
			self.session.commitConfiguration()
			print("Cannot create video device input \(error).")
			return
		}

		let output = AVCaptureVideoDataOutput()

		if (self.session.canAddOutput(output) == false) {
			self.session.commitConfiguration()
			print("Could not add video device output to the session.")
			return
		}

		self.session.addOutput(output)

		output.setSampleBufferDelegate(self, queue: DispatchQueue(label: "Sample_Buffer"))

		guard let connection = output.connection(with: .video) else {
			self.session.commitConfiguration()
			print("Could not retrieve output connection.")
			return
		}

		if (connection.isVideoOrientationSupported) {
			connection.videoOrientation = .portrait
		}

		self.session.commitConfiguration()

		self.configured = true
	}

	//--------------------------------------------------------------------------
	// MARK: Capture Delegate
	//--------------------------------------------------------------------------

	/**
	 * @method captureOutput
	 * @since 0.1.0
	 * @hidden
	 */
	public func captureOutput(_ output: AVCaptureOutput, didOutput buffer: CMSampleBuffer, from connection: AVCaptureConnection) {
		self.delegate?.didCaptureFrame(camera: self, buffer: buffer)
	}
}
