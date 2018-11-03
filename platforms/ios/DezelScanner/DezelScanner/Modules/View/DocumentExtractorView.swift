import Dezel
import AVFoundation

open class DocumentExtractorView : View {

	//--------------------------------------------------------------------------
	// MARK: Properties
	//--------------------------------------------------------------------------

	/**
	 * @property view
	 * @since 0.1.0
	 * @hidden
	 */
	private var view: ObjectExtractorView {
		return self.content as! ObjectExtractorView
	}

	//--------------------------------------------------------------------------
	// MARK: Methods
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method createView
	 * @since 0.1.0
	 */
	override open func createView() -> UIView {
		return ObjectExtractorView(frame: .zero)
	}

	//--------------------------------------------------------------------------
	// MARK: JavaScript Properties
	//--------------------------------------------------------------------------

	/**
	 * @method jsGet_image
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsGet_image(callback: JavaScriptGetterCallback) {
		if let image = self.view.image {
			callback.returns(Image.with(image, in: self.context).holder)
		}
	}

	/**
	 * @method jsSet_image
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsSet_image(callback: JavaScriptSetterCallback) {
		if let image = callback.value.cast(Image.self) {
			self.view.image = image.data
		}
	}

	//--------------------------------------------------------------------------
	// MARK: JavaScript Methods
	//--------------------------------------------------------------------------

	/**
	 * @method jsFunction_extract
	 * @since 0.1.0
	 * @hidden
	 */
	@objc open func jsFunction_extract(callback: JavaScriptFunctionCallback) {

		let p1 = CGPoint(x: CGFloat(callback.argument(0).number), y: CGFloat(callback.argument(1).number))
		let p2 = CGPoint(x: CGFloat(callback.argument(2).number), y: CGFloat(callback.argument(3).number))
		let p3 = CGPoint(x: CGFloat(callback.argument(4).number), y: CGFloat(callback.argument(5).number))
		let p4 = CGPoint(x: CGFloat(callback.argument(6).number), y: CGFloat(callback.argument(7).number))

		if let image = self.view.extract(p1: p1, p2: p2, p3: p3, p4: p4) {
			callback.returns(Image.with(image, in: self.context).holder)
		}
	}
}
