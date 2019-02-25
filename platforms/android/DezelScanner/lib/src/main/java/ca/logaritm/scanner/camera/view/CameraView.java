/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.logaritm.scanner.camera.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CameraView extends CameraTextureView {

	public CameraViewListener mListener;

	/**
	 * Conversion from screen rotation to JPEG orientation.
	 */
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	private static final int REQUEST_CAMERA_PERMISSION = 1;
	private static final String FRAGMENT_DIALOG = "dialog";

	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}

	/**
	 * Tag for the {@link Log}.
	 */
	private static final String TAG = "Camera2BasicFragment";

	/**
	 * Camera state: Showing camera preview.
	 */
	private static final int STATE_PREVIEW = 0;

	/**
	 * Camera state: Waiting for the focus to be locked.
	 */
	private static final int STATE_WAITING_LOCK = 1;

	/**
	 * Camera state: Waiting for the exposure to be precapture state.
	 */
	private static final int STATE_WAITING_PRECAPTURE = 2;

	/**
	 * Camera state: Waiting for the exposure state to be something other than precapture.
	 */
	private static final int STATE_WAITING_NON_PRECAPTURE = 3;

	/**
	 * Camera state: Picture was taken.
	 */
	private static final int STATE_PICTURE_TAKEN = 4;

	private Surface mSurface;

	/**
	 * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
	 * {@link TextureView}.
	 */
	private final TextureView.SurfaceTextureListener mSurfaceTextureListener
		= new TextureView.SurfaceTextureListener() {

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
			openCamera(width, height);
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
			configureTransform(width, height);
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			return true;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {

		}

	};

	private boolean mFlashAvailable = false;

	/**
	 * ID of the current {@link CameraDevice}.
	 */
	private String mCameraId;

	/**
	 * An {@link CameraTextureView} for camera preview.
	 */
	private CameraTextureView mTextureView;

	/**
	 * A {@link CameraCaptureSession } for camera preview.
	 */
	private CameraCaptureSession mCaptureSession;

	/**
	 * A reference to the opened {@link CameraDevice}.
	 */
	private CameraDevice mCameraDevice;

	/**
	 * The {@link android.util.Size} of camera preview.
	 */
	private Size mPreviewSize;

	/**
	 * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
	 */
	private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

		@Override
		public void onOpened(@NonNull CameraDevice cameraDevice) {
			// This method is called when the camera is opened.  We start camera preview here.
			mCameraOpenCloseLock.release();
			mCameraDevice = cameraDevice;
			createCameraPreviewSession();
		}

		@Override
		public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
		}

		@Override
		public void onError(@NonNull CameraDevice cameraDevice, int error) {
          	mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            // TODO
		}
	};

	/**
	 * An additional thread for running tasks that shouldn't block the UI.
	 */
	private HandlerThread mBackgroundThread;

	/**
	 * A {@link Handler} for running tasks in the background.
	 */
	private Handler mBackgroundHandler;

	/**
	 * An {@link ImageReader} that handles still source capture.
	 */
	private ImageReader mImageReader;

	/**
	 * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
	 * still source is ready to be saved.
	 */
	private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
		= new ImageReader.OnImageAvailableListener() {

		@Override
		public void onImageAvailable(ImageReader reader) {

			Image image = null;

			try {
				if (reader != null) {
					image = reader.acquireNextImage();
					if (mListener != null) {
						mListener.onCaptureFrame(image);
					}
					image.close();
				}

			} catch (IllegalStateException e) {
				System.out.println("whoops");
			}

		}

	};

	/**
	 * {@link CaptureRequest.Builder} for the camera preview
	 */
	private CaptureRequest.Builder mPreviewRequestBuilder;

	/**
	 * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
	 */
	private CaptureRequest mPreviewRequest;

	/**
	 * The current state of camera state for taking pictures.
	 */
	private int mState = STATE_PREVIEW;

	/**
	 * A {@link Semaphore} to prevent the app from exiting before closing the camera.
	 */
	private Semaphore mCameraOpenCloseLock = new Semaphore(1);

	/**
	 * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
	 */
	private CameraCaptureSession.CaptureCallback mCaptureCallback
		= new CameraCaptureSession.CaptureCallback() {

		private void process(CaptureResult result) {
			switch (mState) {
				case STATE_PREVIEW: {
					break;
				}
				case STATE_WAITING_LOCK: {
					Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
					if (afState == null) {
						// captureStillPicture();
					} else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
						CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
						// CONTROL_AE_STATE can be null on some devices
						Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
						if (aeState == null ||
							aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
							mState = STATE_PICTURE_TAKEN;
							// captureStillPicture();
						} else {
							runPrecaptureSequence();
						}
					}
					break;
				}
				case STATE_WAITING_PRECAPTURE: {
					// CONTROL_AE_STATE can be null on some devices
					Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
					if (aeState == null ||
						aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
						aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
						mState = STATE_WAITING_NON_PRECAPTURE;
					}
					break;
				}
				case STATE_WAITING_NON_PRECAPTURE: {
					// CONTROL_AE_STATE can be null on some devices
					Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
					if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
						mState = STATE_PICTURE_TAKEN;
						//  captureStillPicture();
					}
					break;
				}
			}
		}

		@Override
		public void onCaptureProgressed(@NonNull CameraCaptureSession session,
										@NonNull CaptureRequest request,
										@NonNull CaptureResult partialResult) {
			process(partialResult);
		}

		@Override
		public void onCaptureCompleted(@NonNull CameraCaptureSession session,
									   @NonNull CaptureRequest request,
									   @NonNull TotalCaptureResult result) {
			process(result);
		}

	};

	public CameraView(Context context) {
		this(context, null);
		mTextureView = this;
	}

	public CameraView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mTextureView = this;
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTextureView = this;
	}

	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


	public void start() {

		startBackgroundThread();

		// When the screen is turned off and turned back on, the SurfaceTexture is already
		// available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
		// a camera and start preview from here (otherwise, we wait until the preview is ready in
		// the SurfaceTextureListener).
		if (mTextureView.isAvailable()) {
			openCamera(mTextureView.getWidth(), mTextureView.getHeight());
		} else {
			mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
		}
	}

	public void stop() {
		closeCamera();
		stopBackgroundThread();
	}

	private boolean mFlash = false;

	public void toggleFlash() {

		if (mFlashAvailable == false) {
			return;
		}

		try {

			if (mFlash) {
				mFlash = false;
				mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
				mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
			} else {
				mFlash = true;
				mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
				mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
			}

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

	}

	public static Bitmap convert(Image image) {

		byte[] nv21;
		ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
		ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
		ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

		int ySize = yBuffer.remaining();
		int uSize = uBuffer.remaining();
		int vSize = vBuffer.remaining();

		nv21 = new byte[ySize + uSize + vSize];

		//U and V are swapped
		yBuffer.get(nv21, 0, ySize);
		vBuffer.get(nv21, ySize, vSize);
		uBuffer.get(nv21, ySize + vSize, uSize);

		int width = image.getWidth();
		int height = image.getHeight();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
		yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);

		byte[] data = out.toByteArray();

		return BitmapFactory.decodeByteArray(data, 0, data.length, null);
	}

	public void setListener(CameraViewListener listener) {
		this.mListener = listener;
	}

	private Activity getActivity() {
		return (Activity) getContext();
	}

	/**
	 * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
	 * width and height are at least as large as the respective requested values, and whose aspect
	 * ratio matches with the specified value.
	 *
	 * @param choices     The list of sizes that the camera supports for the intended output class
	 * @param width       The minimum desired width
	 * @param height      The minimum desired height
	 * @param aspectRatio The aspect ratio
	 * @return The optimal {@code Size}, or an arbitrary one if none were big enough
	 */
	private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
		// Collect the supported resolutions that are at least as big as the preview Surface
		List<Size> bigEnough = new ArrayList<>();
		int w = aspectRatio.getWidth();
		int h = aspectRatio.getHeight();
		for (Size option : choices) {
			if (option.getHeight() == option.getWidth() * h / w &&
				option.getWidth() >= width && option.getHeight() >= height) {
				bigEnough.add(option);
			}
		}

		// Pick the smallest of those, assuming we found any
		if (bigEnough.size() > 0) {
			return Collections.min(bigEnough, new CompareSizesByArea());
		} else {
			Log.e(TAG, "Couldn't find any suitable preview size");
			return choices[0];
		}
	}

	Size smallest;

	/**
	 * Sets up member variables related to camera.
	 *
	 * @param width  The width of available size for camera preview
	 * @param height The height of available size for camera preview
	 */
	private void setUpCameraOutputs(int width, int height) {
		Activity activity = getActivity();
		CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
		try {
			for (String cameraId : manager.getCameraIdList()) {
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

				// We don't use a front facing camera in this sample.
				Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
				if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
					continue;
				}

				StreamConfigurationMap map = characteristics.get(
					CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
				if (map == null) {
					continue;
				}

				// For still source captures, we use the largest available size.
				Size largest = Collections.max(
					Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
					new CompareSizesByArea());

				// For still source captures, we use the largest available size.
				smallest = Collections.min(
					Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
					new CompareSizesByArea());

				List<Size> sizes = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG));
				Collections.sort(sizes, new CompareSizesByArea() );
				// NOTE
				// THIS IS to retrieve an OK image size
				for (Size size : sizes) {
					smallest = size;
					if (size.getWidth() >1080) {
						break;
					}
				}

				mImageReader = ImageReader.newInstance(smallest.getWidth(), smallest.getHeight(), ImageFormat.YUV_420_888, 1);
				mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

				// Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
				// bus' bandwidth limitation, resulting in gorgeous previews but the storage of
				// garbage capture data.
				mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
					width, height, smallest);

				// We fit the aspect ratio of TextureView to the size of preview we picked.
				int orientation = getResources().getConfiguration().orientation;
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					mTextureView.setAspectRatio(
						mPreviewSize.getWidth(), mPreviewSize.getHeight());
				} else {
					mTextureView.setAspectRatio(
						mPreviewSize.getHeight(), mPreviewSize.getWidth());
				}

				mFlashAvailable = !!characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

				mCameraId = cameraId;
				return;
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Currently an NPE is thrown when the Camera2API is used but not supported on the
			// device this code runs.
		}
	}

	/**
	 * Opens the camera specified
	 */
	private void openCamera(int width, int height) throws SecurityException {


		setUpCameraOutputs(width, height);
		configureTransform(width, height);
		Activity activity = getActivity();
		CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
		try {
			if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
				throw new RuntimeException("Time out waiting to lock camera opening.");
			}
			manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
		}
	}

	/**
	 * Closes the current {@link CameraDevice}.
	 */
	private void closeCamera() {
		try {
			mCameraOpenCloseLock.acquire();
			if (null != mCaptureSession) {
				mCaptureSession.close();
				mCaptureSession = null;
			}
			if (null != mCameraDevice) {
				mCameraDevice.close();
				mCameraDevice = null;
			}
			if (null != mImageReader) {
				mImageReader.close();
				mImageReader = null;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
		} finally {
			mCameraOpenCloseLock.release();
		}
	}

	/**
	 * Starts a background thread and its {@link Handler}.
	 */
	private void startBackgroundThread() {
		mBackgroundThread = new HandlerThread("CameraBackground");
		mBackgroundThread.start();
		mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
	}

	/**
	 * Stops the background thread and its {@link Handler}.
	 */
	private void stopBackgroundThread() {
		mBackgroundThread.quitSafely();
		try {
			mBackgroundThread.join();
			mBackgroundThread = null;
			mBackgroundHandler = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new {@link CameraCaptureSession} for camera preview.
	 */
	private void createCameraPreviewSession() {
		try {
			SurfaceTexture texture = mTextureView.getSurfaceTexture();
			assert texture != null;

			// We configure the size of default buffer to be the size of camera preview we want.
			texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

			// This is the output Surface we need to start preview.
			mSurface = new Surface(texture);
			Surface mImageSurface = mImageReader.getSurface();

			// We set up a CaptureRequest.Builder with the output Surface.
			mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			mPreviewRequestBuilder.addTarget(mSurface);
			mPreviewRequestBuilder.addTarget(mImageSurface);

			// Here, we create a CameraCaptureSession for camera preview.
			mCameraDevice.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()),
				new CameraCaptureSession.StateCallback() {

					@Override
					public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
						// The camera is already closed
						if (null == mCameraDevice) {
							return;
						}
						// When the session is ready, we start displaying the preview.
						mCaptureSession = cameraCaptureSession;
						try {
							// Auto focus should be continuous for camera preview.
							mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
								CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
							mPreviewRequest = mPreviewRequestBuilder.build();
							mCaptureSession.setRepeatingRequest(mPreviewRequest,
								mCaptureCallback, mBackgroundHandler);


						} catch (CameraAccessException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onConfigureFailed(
						@NonNull CameraCaptureSession cameraCaptureSession) {
						Log.e("DEZEL", "Failed");
					}
				}, null
			);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
	 * This method should be called after the camera preview size is determined in
	 * setUpCameraOutputs and also the size of `mTextureView` is fixed.
	 *
	 * @param viewWidth  The width of `mTextureView`
	 * @param viewHeight The height of `mTextureView`
	 */
	private void configureTransform(int viewWidth, int viewHeight) {
		Activity activity = getActivity();
		if (null == mTextureView || null == mPreviewSize || null == activity) {
			return;
		}
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		Matrix matrix = new Matrix();
		RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
		RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
		float centerX = viewRect.centerX();
		float centerY = viewRect.centerY();
		if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
			bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
			matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
			float scale = Math.max(
				(float) viewHeight / mPreviewSize.getHeight(),
				(float) viewWidth / mPreviewSize.getWidth());
			matrix.postScale(scale, scale, centerX, centerY);
			matrix.postRotate(90 * (rotation - 2), centerX, centerY);
		} else if (Surface.ROTATION_180 == rotation) {
			matrix.postRotate(180, centerX, centerY);
		}
		mTextureView.setTransform(matrix);
	}

	/**
	 * Initiate a still source capture.
	 */
	private void takePicture() {
		lockFocus();
	}

	/**
	 * Lock the focus as the first step for a still source capture.
	 */
	private void lockFocus() {
		try {
			// This is how to tell the camera to lock focus.
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
				CameraMetadata.CONTROL_AF_TRIGGER_START);
			// Tell #mCaptureCallback to wait for the lock.
			mState = STATE_WAITING_LOCK;
			mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
				mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the precapture sequence for capturing a still source. This method should be called when
	 * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
	 */
	private void runPrecaptureSequence() {
		try {
			// This is how to tell the camera to trigger.
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
				CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
			// Tell #mCaptureCallback to wait for the precapture sequence to be set.
			mState = STATE_WAITING_PRECAPTURE;
			mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
				mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Capture a still picture. This method should be called when we get a response in
	 * {@link #mCaptureCallback} from both {@link #lockFocus()}.
	 */
	private void captureStillPicture() {
		this.unlockFocus();
	}

	/**
	 * Unlock the focus. This method should be called when still source capture sequence is
	 * finished.
	 */
	private void unlockFocus() {
		try {
			// Reset the auto-focus trigger
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
			mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
			// After this, the camera will go back to the normal state of preview.
			mState = STATE_PREVIEW;
			mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
				mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compares two {@code Size}s based on their areas.
	 */
	static class CompareSizesByArea implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			// We cast here to ensure the multiplications won't overflow
			return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
		}
	}
}