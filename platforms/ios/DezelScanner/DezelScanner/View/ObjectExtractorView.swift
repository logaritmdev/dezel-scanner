import AVFoundation
import Dezel

/**
 * @class ObjectExtractorView
 * @since 0.1.0
 */
open class ObjectExtractorView : UIView {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * The image that contains the soon to be extracted image.
	 * @property image
	 * @since 0.1.0
	 */
	open var image: UIImage? {
		didSet {
			self.background.image = image
		}
	}

	/**
	 * @property background
	 * @since 0.1.0
	 * @hidden
	 */
	private var background: UIImageView = UIImageView(frame: .zero)

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	public override init(frame: CGRect) {
		super.init(frame: frame)
		self.background.contentMode = .scaleAspectFill
		self.addSubview(self.background)
	}

	/**
	 * @constructor
	 * @since 0.1.0
	 */
	required public init?(coder: NSCoder) {
		super.init(coder: coder)
		self.background.contentMode = .scaleAspectFill
		self.addSubview(self.background)
	}

	/**
	 * @inherited
	 * @method layoutSubviews
	 * @since 0.1.0
	 */
	open override func layoutSubviews() {
		self.background.frame = self.bounds
	}

	/**
	 * @method extract
	 * @since 0.1.0
	 */
	open func extract(p1: CGPoint, p2: CGPoint, p3: CGPoint, p4: CGPoint) -> UIImage? {

		guard let image = self.image?.cgImage else {
			return nil
		}

		let p1 = self.convert(p1, of: image)
		let p2 = self.convert(p2, of: image)
		let p3 = self.convert(p3, of: image)
		let p4 = self.convert(p4, of: image)

		if let extracted = DLExtractorPullImage(image, Int32(image.width), Int32(image.height), p1, p2, p3, p4) {
			return UIImage(pointer: extracted)
		}

		return nil
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
	// MARK: Private API
	//--------------------------------------------------------------------------

	/**
	 * @method convert
 	 * @since 0.1.0
	 * @hidden
	 */
	private func convert(_ point: CGPoint, of image: CGImage) -> CGPoint {

		let backgroundW = self.background.bounds.width
		let backgroundH = self.background.bounds.height
		let imageW = CGFloat(image.width)
		let imageH = CGFloat(image.height)

		let ratioX = backgroundW / imageW
		let ratioY = backgroundH / imageH

 		let scale = max(ratioX, ratioY)

		var converted = point

        converted.x -= (backgroundW - imageW * scale) / 2.0
        converted.y -= (backgroundH - imageH * scale) / 2.0
     	converted.x /= scale
        converted.y /= scale

        return converted
	}
}
