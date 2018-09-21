import Foundation

public protocol ObjectScannerViewDelegate : class {
	func didFindDocument(view: ObjectScannerView, shape: [CGPoint], image: UIImage)
	func didSpotDocument(view: ObjectScannerView, shape: [CGPoint])
	func didLoseDocument(view: ObjectScannerView)
	func didMissDocument(view: ObjectScannerView)
}
