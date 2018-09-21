import CoreImage
import CoreMedia

private let context: CIContext = CIContext()

internal func CGImageCreateWithCMSampleBuffer(_ buffer: CMSampleBuffer) -> CGImage? {
	guard let pixels = CMSampleBufferGetImageBuffer(buffer) else {
		return nil
	}

	let ci = CIImage(cvPixelBuffer: pixels)

	guard let cg = context.createCGImage(ci, from: ci.extent) else {
		return nil
	}

	return cg
}


