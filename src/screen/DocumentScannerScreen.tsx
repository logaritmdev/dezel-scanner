import { Dictionary } from 'lodash'
import { prop, ScreenTransition, ScreenLeaveEvent, ScreenEnterEvent } from 'dezel'
import { bound } from 'dezel'
import { Event } from 'dezel'
import { Fragment } from 'dezel'
import { View } from 'dezel'
import { TextView } from 'dezel'
import { Screen } from 'dezel'
import { Content } from 'dezel'
import { Header } from 'dezel'
import { Footer } from 'dezel'
import { NavigationBar } from 'dezel'
import { NavigationBarCloseButton } from 'dezel'
import { DocumentOutlineView } from '../view/DocumentOutlineView'
import { DocumentScannerView } from '../view/DocumentScannerView'
import { DocumentScannerViewFindDocumentEvent } from '../view/DocumentScannerView'
import { DocumentScannerViewSpotDocumentEvent } from '../view/DocumentScannerView'
import { DocumentScannerConfirmScreen } from './DocumentScannerConfirmScreen'
import { DocumentScannerManualScreen } from './DocumentScannerManualScreen'
import { ShutterButton } from '../component/ShutterButton'
import { FlashButton } from '../component/FlashButton'

import './DocumentScannerScreen.ds'
import './DocumentScannerScreen.ds.ios'
import './DocumentScannerScreen.ds.android'

export class DocumentScannerScreen extends Screen {

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

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
						onFindDocument={this.onFindDocument}
						onSpotDocument={this.onSpotDocument}
						onLoseDocument={this.onLoseDocument}
						onMissDocument={this.onMissDocument}
					/>
					<DocumentOutlineView id="outline" style="outline" />
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
	 * @method onLoad
	 * @since 0.1.0
	 */
	public onLoad() {
		this.statusBarForegroundColor = 'white'
		this.outline.opacity = 0
		this.outline.scaleX = 0.9
		this.outline.scaleY = 0.9
	}

	/**
	 * @inherited
	 * @method onPresent
	 * @since 0.1.0
	 */
	public onPresent() {
		if (this.scanner.authorized == false) {
			this.scanner.requestAuthorization()
			this.scanner.on('authorize', () => {
				this.scanner.startCamera()
			})
		} else {
			this.scanner.startCamera()
		}
	}

	/**
	 * @inherited
	 * @method onBeforeEnter
	 * @since 0.1.0
	 */
	public onBeforeEnter(event: Event<ScreenEnterEvent>) {

		this.message.text = this.labels.search

		this.outline.path = undefined

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
	 * @property scanner
	 * @since 0.1.0
	 * @hidden
	 */
	@prop private scanner!: DocumentScannerView

	/**
	 * @property outline
	 * @since 0.1.0
	 * @hidden
	 */
	@prop private outline!: DocumentOutlineView

	/**
	 * @property message
	 * @since 0.1.0
	 * @hidden
	 */
	@prop private message!: TextView

	/**
	 * @property shutterButton
	 * @since 0.1.0
	 * @hidden
	 */
	@prop private shutterButton!: ShutterButton

	/**
	 * @property flashButton
	 * @since 0.1.0
	 * @hidden
	 */
	@prop private flashButton!: FlashButton

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
	}

	/**
	 * @method onShutterButtonPress
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onShutterButtonPress(event: Event) {
		device.sound('shutter')
		this.present(new DocumentScannerManualScreen, 'dissolve')
	}

	/**
	 * @method onFindDocument
	 * @since 0.1.0
	 * @hidden
	 */
	@bound public onFindDocument(event: Event<DocumentScannerViewFindDocumentEvent>) {

		device.sound('shutter')

		this.message.text = this.labels.found

		if (this.detected == false) {
			this.detected = true
			this.showOutline()
		}

		this.outline.path = event.data.shape

		this.present(new DocumentScannerConfirmScreen(event.data.image), 'dissolve')
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
}