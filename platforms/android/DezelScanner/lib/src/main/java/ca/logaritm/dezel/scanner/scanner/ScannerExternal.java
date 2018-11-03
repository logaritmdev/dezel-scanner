package ca.logaritm.dezel.scanner.scanner;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.nio.ByteBuffer;

public class ScannerExternal {

	/**
	 * @method create
	 * @since 0.1.0
	 * @hidden
	 */
	static native public long create(Object scanner);

	/**
	 * @method delete
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void delete(long handle);

	/**
	 * @method setDebug
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void setDebug(long handle, boolean debugging);

	/**
	 * @method setEnabled
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void setEnabled(long handle, boolean enabled);

	/**
	 * @method reset
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void reset(long handle);

	/**
	 * @method restart
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void restart(long handle);

	/**
	 * @method process
	 * @since 0.1.0
	 * @hidden
	 */
	static native public void process(long handle, int w, int h, byte[] buffer);

	/**
	 * @method getExtractedImage
	 * @since 0.1.0
	 * @hidden
	 */
	static native public Bitmap getExtractedImage(long handle);

	/**
	 * @method getProcessedImage
	 * @since 0.1.0
	 * @hidden
	 */
	static native public Bitmap getProcessedImage(long handle);

	/**
	 * @method pullImage
	 * @since 0.1.0
	 * @hidden
	 */
	static native public Bitmap pullImage(Bitmap image, int width, int height, PointF p1, PointF p2, PointF p3, PointF p4);

}
