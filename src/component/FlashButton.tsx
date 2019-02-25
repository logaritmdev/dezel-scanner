import { ref } from 'dezel'
import { state } from 'dezel'
import { ImageView } from 'dezel'
import { Button } from 'dezel'

import './FlashButton.ds'

/**
 * @class FlashButton
 * @since 1.0.0
 */
export class FlashButton extends Button {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The button's image.
	 * @property image
	 * @since 1.0.0
	 */
	@ref public readonly image!: ImageView

	/**
	 * The buttons's active state.
	 * @property active
	 * @since 0.1.0
	 */
	@state public active: boolean = false

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------


}