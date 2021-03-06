package ca.logaritm.scanner.modules.view

import android.graphics.PointF
import ca.logaritm.dezel.core.*
import ca.logaritm.dezel.modules.graphic.Image
import ca.logaritm.dezel.modules.view.View
import ca.logaritm.scanner.view.ObjectExtractorView

open class DocumentExtractorView(context: JavaScriptContext): View(context) {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * @property view
	 * @since 0.1.0
	 * @hidden
	 */
	private val view: ObjectExtractorView
		get() = this.content as ObjectExtractorView

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method createContentView
	 * @since 0.1.0
	 */
	override fun createContentView(): ObjectExtractorView {
		return ObjectExtractorView(this.context.application)
	}

	//--------------------------------------------------------------------------
	// JavaScript Properties
	//--------------------------------------------------------------------------

	/**
	 * @method jsGet_image
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsGet_image(callback: JavaScriptGetterCallback) {
		val image = this.view.image
		if (image != null) {
			callback.returns(Image.with(image, this.context).holder)
		}
	}

	/**
	 * @method jsSet_image
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsSet_image(callback: JavaScriptSetterCallback) {
		val image = callback.value.cast(Image::class.java)
		if (image != null) {
			this.view.image = image.data
		}
	}

	//--------------------------------------------------------------------------
	// JavaScript Methods
	//--------------------------------------------------------------------------

	/**
	 * @method jsFunction_extract
	 * @since 0.1.0
	 * @hidden
	 */
	@Suppress("unused")
	open fun jsFunction_extract(callback: JavaScriptFunctionCallback) {

		val p1 = PointF(callback.argument(0).number.toFloat(), callback.argument(1).number.toFloat())
		val p2 = PointF(callback.argument(2).number.toFloat(), callback.argument(3).number.toFloat())
		val p3 = PointF(callback.argument(4).number.toFloat(), callback.argument(5).number.toFloat())
		val p4 = PointF(callback.argument(6).number.toFloat(), callback.argument(7).number.toFloat())

		val image = this.view.extract(p1, p2, p3, p4)
		if (image != null) {
			callback.returns(Image.with(image, this.context).holder)
		}
	}
	
}