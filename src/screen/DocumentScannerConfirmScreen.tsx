import { ref } from 'dezel'
import { bound } from 'dezel'
import { Event } from 'dezel'
import { Image } from 'dezel'
import { Fragment } from 'dezel'
import { View } from 'dezel'
import { ImageView } from 'dezel'
import { Screen } from 'dezel'
import { ScreenBeforeEnterEvent } from 'dezel'
import { ScreenBeforeLeaveEvent } from 'dezel'
import { Header } from 'dezel'
import { Footer } from 'dezel'
import { Content } from 'dezel'
import { Button } from 'dezel'
import { NavigationBar } from 'dezel'
import { NavigationBarBackButton } from 'dezel'

import './DocumentScannerConfirmScreen.ds'

/**
 * @class DocumentScannerConfirmResult
 * @since 1.0.0
 */
export enum DocumentScannerConfirmResult {
	ACCEPT,
	APPEND
}

/**
 * @class DocumentScannerConfirmScreen
 * @since 1.0.0
 */
export class DocumentScannerConfirmScreen extends Screen<DocumentScannerConfirmResult> {

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @property preview
	 * @since 1.0.0
	 */
	@ref public preview!: ImageView

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 1.0.0
	 */
	constructor(image?: Image) {
		super()
		this.image = image
	}

	/**
	 * @method render
	 * @since 1.0.0
	 */
	public render() {
		return (
			<Fragment>
				<Header>
					<NavigationBar backButton={
						<NavigationBarBackButton label="Reprendre" onPress={() => this.dismiss()} />
					} />
				</Header>
				<Content>
					<ImageView id="preview" style="preview" source={this.image} />
				</Content>
				<Footer>
					<Button style="accept" onPress={this.onAcceptButtonPress} />
					<Button style="append" onPress={this.onAppendButtonPress} />
				</Footer>
			</Fragment>
		)
	}

	/**
	 * @inherited
	 * @method onCreate
	 * @since 1.0.0
	 */
	public onCreate() {

		this.statusBarForegroundColor = 'white'

		this.content.zoomable = true
		this.content.zoomedView = this.preview
		this.content.minZoom = 1
		this.content.maxZoom = 10
	}

	/**
	 * @inherited
	 * @method onBeforePresent
	 * @since 1.0.0
	 */
	public onBeforeEnter(event: Event<ScreenBeforeEnterEvent>) {

		this.footer.translationY = '100%'

		View.transition({
			duration: event.data.transition.duration,
			equation: event.data.transition.equation
		}, () => {
			this.footer.translationY = 0
		})
	}

	/**
	 * @inherited
	 * @method onBeforePresent
	 * @since 1.0.0
	 */
	public onBeforeLeave(event: Event<ScreenBeforeLeaveEvent>) {

		this.footer.translationY = 0

		View.transition({
			duration: event.data.transition.duration,
			equation: event.data.transition.equation
		}, () => {
			this.footer.translationY = '100%'
		})
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @property image
	 * @since 1.0.0
	 */
	private image?: Image

	/**
	 * @method onAcceptButtonPress
	 * @since 1.0.0
	 * @hidden
	 */
	@bound private onAcceptButtonPress(event: Event) {
		this.result = DocumentScannerConfirmResult.ACCEPT
		this.dismiss()
	}

	/**
	 * @method onAppendButtonPress
	 * @since 1.0.0
	 * @hidden
	 */
	@bound private onAppendButtonPress(event: Event) {
		this.result = DocumentScannerConfirmResult.APPEND
		this.dismiss()
	}
}
