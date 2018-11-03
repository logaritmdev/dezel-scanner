import { Dictionary } from 'lodash'
import { ref } from 'dezel'
import { bound } from 'dezel'
import { Event } from 'dezel'
import { Image } from 'dezel'
import { Fragment } from 'dezel'
import { View } from 'dezel'
import { TextView } from 'dezel'
import { SpinnerView } from 'dezel'
import { Screen } from 'dezel'
import { ScreenEnterEvent } from 'dezel'
import { ScreenLeaveEvent } from 'dezel'
import { ScreenBeforeDismissEvent } from 'dezel'
import { Content } from 'dezel'
import { Header } from 'dezel'
import { Footer } from 'dezel'
import { NavigationBar } from 'dezel'
import { NavigationBarCloseButton } from 'dezel'
import { DocumentOutlineView } from '../view/DocumentOutlineView'
import { DocumentScannerView } from '../view/DocumentScannerView'
import { DocumentScannerViewFindDocumentEvent } from '../view/DocumentScannerView'
import { DocumentScannerViewSpotDocumentEvent } from '../view/DocumentScannerView'
import { DocumentScannerCaptureImageEvent } from '../view/DocumentScannerView'
import { DocumentScannerConfirmScreen } from './DocumentScannerConfirmScreen'
import { DocumentScannerConfirmResult } from './DocumentScannerConfirmScreen'
import { DocumentScannerExtractScreen } from './DocumentScannerExtractScreen'
import { ShutterButton } from '../component/ShutterButton'
import { FlashButton } from '../component/FlashButton'

import './DocumentScannerScreen.ds'
import './DocumentScannerScreen.ds.ios'
import './DocumentScannerScreen.ds.android'

export class DocumentScannerScreen extends Screen<Array<Image>> {

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @property scanner
	 * @since 0.1.0
	 */
	@ref public scanner!: DocumentScannerView

	/**
	 * @property outline
	 * @since 0.1.0
	 */
	@ref public outline!: DocumentOutlineView

	/**
	 * @property message
	 * @since 0.1.0
	 */
	@ref public message!: TextView

	/**
	 * @property spinner
	 * @since 0.1.0
	 */
	@ref public spinner!: SpinnerView

	/**
	 * @property flashButton
	 * @since 0.1.0
	 */
	@ref public flashButton!: FlashButton

	/**
	 * @property shutterButton
	 * @since 0.1.0
	 */
	@ref public shutterButton!: ShutterButton

	/**
	 * @property outlineLineColor
	 * @since 0.1.0
	 */
	public labels: Dictionary<string> = {
		'search': 'Recherche...',
		'still': 'Ne bougez plus...',
		'found': 'Trouvé'
	}

	/**
	 * @property outlineLineColor
	 * @since 0.1.0
	 */
	public outlineLineColor: string = 'rgba(255, 0, 0, 0.75)'

	/**
	 * @property outlineFillColor
	 * @since 0.1.0
	 */
	public outlineFillColor: string = 'rgba(255, 0, 0, 0.25)'

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method render
	 * @since 0.1.0
	 */
	public render() {
		return (
			<Fragment>
				<Header>
					<NavigationBar closeButton={
						<NavigationBarCloseButton label="Retour" onPress={() => this.dismiss()} />
					} />
				</Header>
				<Content>
					<DocumentScannerView
						id="scanner"
						onActivate={this.onActivate}
						onFindDocument={this.onFindDocument}
						onSpotDocument={this.onSpotDocument}
						onLoseDocument={this.onLoseDocument}
						onMissDocument={this.onMissDocument}
						onCaptureImage={this.onCaptureImage}
					/>
					<DocumentOutlineView id="outline" style="outline" />
					<SpinnerView id="spinner" style="spinner" />
					<TextView id="message" style="message" />
				</Content>
				<Footer>
					<FlashButton id="flashButton" onPress={this.onFlashButtonPress} />
					<ShutterButton id="shutterButton" onPress={this.onShutterButtonPress} />
				</Footer>
			</Fragment>
		)
	}

	/**
	 * @inherited
	 * @method onCreate
	 * @since 0.1.0
	 */
	public onCreate() {

		this.statusBarForegroundColor = 'white'

		this.message.opacity = 0

		this.outline.opacity = 0
		this.outline.scaleX = 0.9
		this.outline.scaleY = 0.9

		this.spinner.active = true
		this.spinner.visible = true

		this.result = []
	}

	/**
	 * @inherited
	 * @method onPresent
	 * @since 0.1.0
	 */
	public onPresent() {

		if (this.scanner.authorized == false) {
			this.scanner.requestAuthorization()
			this.scanner.on('authorize', () => this.scanner.startCamera())
			return
		}

		this.scanner.startCamera()
	}

	/**
	 * @inherited
	 * @method onDismiss
	 * @since 0.1.0
	 */
	public onDismiss() {
		this.scanner.stopCamera()
		this.scanner.stopScanner()
	}

	/**
	 * @inherited
	 * @method onBeforeEnter
	 * @since 0.1.0
	 */
	public onBeforeEnter(event: Event<ScreenEnterEvent>) {
		console.log('ON BEFORE ENTERdsadsa ')
		this.message.text = this.labels.search

		this.outline.path = undefined
		console.log('TEST RESTART SCANNER HERE !')
		this.scanner.restartScanner()

		let transition = event.data.transition
		if (transition == null) {
			return
		}

		let equation = transition.equation
		let duration = transition.duration

		View.transition({
			equation,
			duration
		}, () => {
			this.header.translationY = 0
			this.footer.translationY = 0
		})
	}

	/**
	 * @inherited
	 * @method onBeforeLeave
	 * @since 0.1.0
	 */
	public onBeforeLeave(event: Event<ScreenLeaveEvent>) {

		let transition = event.data.transition
		if (transition == null) {
			return
		}

		let equation = transition.equation
		let duration = transition.duration

		View.transition({
			equation,
			duration
		}, () => {
			this.header.translationY = '-100%'
			this.footer.translationY = '+100%'
		})
	}

	//--------------------------------------------------------------------------
	// Private API
	//--------------------------------------------------------------------------

	/**
	 * @property detected
	 * @since 0.1.0
	 * @hidden
	 */
	private detected: boolean = false

	/**
	 * @method showOutline
	 * @since 0.1.0
	 * @hidden
	 */
	private showOutline() {
		View.transition({ duration: 150 }, () => {
			this.outline.opacity = 1
			this.outline.scaleX = 1
			this.outline.scaleY = 1
		})
	}

	/**
	 * @method hideOutline
	 * @since 0.1.0
	 * @hidden
	 */
	private hideOutline() {
		View.transition({ duration: 150 }, () => {
			this.outline.opacity = 0
			this.outline.scaleX = 0.9
			this.outline.scaleY = 0.9
		})
	}

	/**
	 * @method onFlashButtonPress
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onFlashButtonPress(event: Event) {
		this.flashButton.active = !this.flashButton.active
		this.scanner.toggleFlash()
	}

	/**
	 * @method onShutterButtonPress
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onShutterButtonPress(event: Event) {
		this.scanner.captureImage()
		this.scanner.stopScanner()
	}

	/**
	 * @method onActivate
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onActivate(event: Event) {
		this.spinner.active = false
		this.spinner.visible = false
		this.message.opacity = 1
	}

	/**
	 * @method onFindDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onFindDocument(event: Event<DocumentScannerViewFindDocumentEvent>) {

		let image = event.data.image

		device.sound('shutter')

		this.message.text = this.labels.found

		if (this.detected == false) {
			this.detected = true
			this.showOutline()
		}

		this.outline.path = event.data.shape

		let screen = new DocumentScannerConfirmScreen(image)

		screen.once('beforedismiss', (event: Event<ScreenBeforeDismissEvent>) => {

			let result = event.data.result
			if (result == DocumentScannerConfirmResult.ACCEPT ||
				result == DocumentScannerConfirmResult.APPEND) {
				if (this.result) {
					this.result.push(image)
				}
			}

			if (result == DocumentScannerConfirmResult.ACCEPT) {
				event.cancel()
				this.dismiss()
			}
		})

		this.present(screen, 'dissolve')
	}

	/**
	 * @method onSpotDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onSpotDocument(event: Event<DocumentScannerViewSpotDocumentEvent>) {

		this.message.text = this.labels.still

		if (this.detected == false) {
			this.detected = true
			this.showOutline()
		}

		this.outline.path = event.data.shape
	}

	/**
	 * @method onLoseDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onLoseDocument() {

		this.message.text = this.labels.search

		if (this.detected) {
			this.detected = false
			this.hideOutline()
		}

		this.outline.path = undefined
	}

	/**
	 * @method onMissDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onMissDocument() {

	}

	/**
	 * @method onMissDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public async onCaptureImage(event: Event<DocumentScannerCaptureImageEvent>) {

		device.sound('shutter')

		let image = await this.prompt(new DocumentScannerExtractScreen(event.data.image), 'dissolve')
		if (image) {
			if (this.result) {
				this.result.push(image)
			}
			console.log('Found image', image)
		}

		this.dismiss()
	}
}