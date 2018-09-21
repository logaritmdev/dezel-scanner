import { native } from 'dezel'
import { bridge } from 'dezel'
import { Event } from 'dezel'
import { View } from 'dezel'
import { Image } from 'dezel'
import { Point } from '../geom/Point'

@bridge('dezel.scanner.view.DocumentScannerView')

/**
 * Displays a rectangular element drawn onto the screen.
 * @class DocumentScannerView
 * @super View
 * @since 0.1.0
 */
export class DocumentScannerView extends View {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------


	/**
	 * Whether camera services were requested.
	 * @property requested
	 * @since 0.1.0
	 */
	public get requested(): boolean {
		return this.native.requested
	}

	/**
	 * Whether camera services were authorized.
	 * @property authorized
	 * @since 0.1.0
	 */
	public get authorized(): boolean {
		return this.native.authorized
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @method requestAuthorization
	 * @since 0.1.0
	 */
	public requestAuthorization() {
		this.native.requestAuthorization()
		return this
	}

	/**
	 * @method startCamera
	 * @since 0.1.0
	 */
	public startCamera() {
		this.native.startCamera()
		return this
	}

	/**
	 * @method stopCamera
	 * @since 0.1.0
	 */
	public stopCamera() {
		this.native.stopCamera()
		return this
	}

	/**
	 * @method enableScanner
	 * @since 0.1.0
	 */
	public enableScanner() {
		this.native.enableScanner()
		return this
	}

	/**
	 * @method disableScanner
	 * @since 0.1.0
	 */
	public disableScanner() {
		this.native.disableScanner()
		return this
	}

	/**
	 * @method restartScanner
	 * @since 0.1.0
	 */
	public restartScanner() {
		this.native.restartScanner()
		return this
	}

	//--------------------------------------------------------------------------
	// Events
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onEmit
	 * @since 0.1.0
	 */
	public onEmit(event: Event) {

		switch (event.type) {

			case 'finddocument':
				this.onFindDocument(event)
				break

			case 'spotdocument':
				this.onSpotDocument(event)
				break

			case 'losedocument':
				this.onLoseDocument(event)
				break

			case 'missdocument':
				this.onMissDocument(event)
				break
		}

		super.onEmit(event)
	}

	/**
	 * Called when a document is found.
	 * @method onFindDocument
	 * @since 0.1.0
	 */
	public onFindDocument(event: Event<DocumentScannerViewFindDocumentEvent>) {

	}

	/**
	 * Called when a document is tracked.
	 * @method onSpotDocument
	 * @since 0.1.0
	 */
	public onSpotDocument(event: Event<DocumentScannerViewSpotDocumentEvent>) {

	}

	/**
	 * Called when the tracked document is lost.
	 * @method onLoseDocument
	 * @since 0.1.0
	 */
	public onLoseDocument(event: Event) {

	}

	/**
	 * Called when no documents were found after a period of time.
	 * @method onMissDocument
	 * @since 0.1.0
	 */
	public onMissDocument(event: Event) {

	}

	//--------------------------------------------------------------------------
	// Native API
	//--------------------------------------------------------------------------

	/**
	 * @property native
	 * @since 0.1.0
	 * @hidden
	 */
	public native: any

	/**
	 * @method nativeAuthorize
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeAuthorize() {
		this.emit('authorize')
	}

	/**
	 * @method nativeUnauthorize
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeUnauthorize() {
		this.emit('unauthorize')
	}

	/**
	 * @method nativeOnFindDocument
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeOnFindDocument(shape: Array<{ x: number, y: number }>, image: Image) {
		this.emit<DocumentScannerViewFindDocumentEvent>('finddocument', { data: { shape: shape.map(p => new Point(p.x, p.y)), image } })
	}

	/**
	 * @method nativeOnSpotDocument
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeOnSpotDocument(shape: Array<{ x: number, y: number }>, image: Image) {
		this.emit<DocumentScannerViewSpotDocumentEvent>('spotdocument', { data: { shape: shape.map(p => new Point(p.x, p.y)) } })
	}

	/**
	 * @method nativeOnLoseDocument
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeOnLoseDocument() {
		this.emit('losedocument')
	}

	/**
	 * @method nativeOnMissDocument
	 * @since 0.1.0
	 * @hidden
	 */
	private nativeOnMissDocument() {
		this.emit('missdocument')
	}
}

/**
 * @type DocumentScannerViewFindDocumentEvent
 * @since 0.1.0
 */
export type DocumentScannerViewFindDocumentEvent = {
	shape: Array<Point>
	image: Image
}

/**
 * @type DocumentScannerViewSpotDocumentEvent
 * @since 0.1.0
 */
export type DocumentScannerViewSpotDocumentEvent = {
	shape: Array<Point>
}