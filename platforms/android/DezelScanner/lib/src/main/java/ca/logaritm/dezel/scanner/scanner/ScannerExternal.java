package ca.logaritm.dezel.scanner.scanner;

import android.graphics.Bitmap;

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
	static native public void process(long handle, int w, int h, ByteBuffer buf);

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

}
