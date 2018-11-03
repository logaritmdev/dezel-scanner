import { native } from 'dezel'
import { bridge } from 'dezel'
import { Event } from 'dezel'
import { View } from 'dezel'
import { Image } from 'dezel'
import { Point } from '../geom/Point'

import './DocumentExtractorView.ds'

@bridge('dezel.scanner.view.DocumentExtractorView')

/**
 * Displays a rectangular element drawn onto the screen.
 * @class DocumentExtractorView
 * @super View
 * @since 0.1.0
 */
export class DocumentExtractorView extends View {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The image to extract from.
	 * @property image
	 * @since 0.1.0
	 */
	@native public image?: Image

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @method requestAuthorization
	 * @since 0.1.0
	 */
	public extract(p1: Point, p2: Point, p3: Point, p4: Point) {
		return this.native.extract(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
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

}