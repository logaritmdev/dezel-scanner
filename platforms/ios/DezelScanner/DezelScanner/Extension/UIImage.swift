import Foundation
import AVFoundation
import CoreImage

extension UIImage {

	/**
	 * @constructor
	 * @since 0.1.0
	 * @hidden
	 */
	internal convenience init(pointer: UnsafeMutableRawPointer) {
		self.init(cgImage: Unmanaged<CGImage>.fromOpaque(pointer).takeRetainedValue())
	}

	internal convenience init(pointer: Unmanaged<CGImage>) {
		self.init(cgImage: pointer.takeRetainedValue())
	}

}
