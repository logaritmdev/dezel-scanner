import { bound } from 'dezel'
import { Event } from 'dezel'
import { Image } from 'dezel'
import { Fragment } from 'dezel'
import { ImageView } from 'dezel'
import { Screen } from 'dezel'
import { Header } from 'dezel'
import { Footer } from 'dezel'
import { Content } from 'dezel'
import { Button } from 'dezel'
import { NavigationBar } from 'dezel'
import { NavigationBarBackButton } from 'dezel'

import './DocumentScannerConfirmScreen.ds'

/**
 * @class DocumentScannerConfirmScreen
 * @since 1.0.0
 */
export class DocumentScannerConfirmScreen extends Screen {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * @property image
	 * @since 1.0.0
	 */
	public image?: Image

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
						<NavigationBarBackButton label="Reprendre" onPress={(e: Event) => this.dismiss()} />
					} />
				</Header>
				<Content>
					<ImageView style="image" image={this.image} />
				</Content>
				<Footer>
				</Footer>
			</Fragment>
		)
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @method onAccept
	 * @since 1.0.0
	 */
	@bound onAcceptButtonTap(event: Event) {
		this.dismiss().then(() => {
			setTimeout(() => this.emit('accept', { data: { image: this.image } }), 50)
		})
	}

	/**
	 * @method onRetake
	 * @since 1.0.0
	 */
	@bound onRetakeButtonTap(event: Event) {
		this.dismiss().then(() => {
			setTimeout(() => this.emit('retake', { data: { image: this.image } }), 50)
		})
	}
}
