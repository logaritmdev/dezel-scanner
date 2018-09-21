/**
 * @protocol ScannerDelegate
 * @since 0.1.0
 */
public protocol ScannerDelegate : class {

	/**
	 * Called when a valid object is found.
	 * @method didFindObject
	 * @since 0.1.0
	 */
	func didFindObject(detector: Scanner, shape: [CGPoint], image: UIImage)

	/**
	 * Called when a possible object is found.
	 * @method didSpotObject
	 * @since 0.1.0
	 */
	func didSpotObject(detector: Scanner, shape: [CGPoint])

	/**
	 * Called when an object is lost.
	 * @method didLoseObject
	 * @since 0.1.0
	 */
	func didLoseObject(detector: Scanner)

	/**
	 * Called when nothing has been found.
	 * @method didMissObject
	 * @since 0.1.0
	 */
	func didMissObject(detector: Scanner)
}
