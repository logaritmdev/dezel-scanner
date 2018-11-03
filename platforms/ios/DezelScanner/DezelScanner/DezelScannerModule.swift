import Dezel

/**
 * @class DezelScannerModule
 * @since 0.1.0
 * @hidden
 */
open class DezelScannerModule: Module {

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method initialize
	 * @since 0.1.0
	 */
	override open func initialize() {
		self.context.registerClass("dezel.scanner.view.DocumentScannerView", type: DocumentScannerView.self)
		self.context.registerClass("dezel.scanner.view.DocumentExtractorView", type: DocumentExtractorView.self)
	}
}
