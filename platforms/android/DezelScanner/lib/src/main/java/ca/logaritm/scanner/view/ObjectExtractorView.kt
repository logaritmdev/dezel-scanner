package ca.logaritm.scanner.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.view.ViewGroup
import android.widget.ImageView
import ca.logaritm.dezel.extension.Delegates
import ca.logaritm.scanner.scanner.ScannerExternal
import ca.logaritm.dezel.view.graphic.Convert

open class ObjectExtractorView(context: Context) : ViewGroup(context) {

	open var image: Bitmap? by Delegates.OnSetOptional<Bitmap>(null) { value ->
		this.background.setImageBitmap(value)
	}

	private var background: ImageView = ImageView(context)

	init {
		this.background.scaleType =  ImageView.ScaleType.CENTER_CROP
		this.addView(this.background)
	}

	/**
	 * @inherited
	 * @method onMeasure
	 * @since 0.1.0
	 */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		this.background.measure(widthMeasureSpec, heightMeasureSpec)
	}

	/**
	 * @inherited
	 * @method onLayout
	 * @since 0.1.0
	 */
	override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
		this.background.layout(l, t, r, b)
	}

	/**
	 * @method extract
	 * @since 0.1.0
	 */
	open fun extract(p1: PointF, p2: PointF, p3: PointF, p4: PointF): Bitmap? {

		val image = this.image
		if (image == null) {
			return null
		}

		val p1 = this.convert(p1, image)
		val p2 = this.convert(p2, image)
		val p3 = this.convert(p3, image)
		val p4 = this.convert(p4, image)

		return ScannerExternal.pullImage(image, image.width, image.height, p1, p2, p3, p4)
	}

	/**
	 * @method convert
	 * @since 0.1.0
	 */
	private fun convert(point: PointF, image: Bitmap): PointF {

		val backgroundW = Convert.toDp(this.background.width).toFloat()
		val backgroundH = Convert.toDp(this.background.height).toFloat()
		val imageW = image.width.toFloat()
		val imageH = image.height.toFloat()

		val ratioX = backgroundW / imageW
		val ratioY = backgroundH / imageH

		val scale = Math.max(ratioX, ratioY)

		var converted = point

		converted.x -= (backgroundW - imageW * scale) / 2f
		converted.y -= (backgroundH - imageH * scale) / 2f
		converted.x /= scale
		converted.y /= scale

		return converted
	}
}