package ca.logaritm.dezel.scanner

import ca.logaritm.dezel.core.JavaScriptContext
import ca.logaritm.dezel.core.Module
import ca.logaritm.dezel.scanner.modules.view.DocumentScannerView

/**
 * @class DezelScannerModule
 * @since 0.1.0
 * @hidden
 */
open class DezelScannerModule(context: JavaScriptContext): Module(context) {

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method initialize
	 * @since 0.1.0
	 */
	override fun initialize() {
		this.context.registerClass("dezel.scanner.view.DocumentScannerView", DocumentScannerView::class.java)
	}
}