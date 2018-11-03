
import { bound } from 'dezel'
import { Fragment } from 'dezel'
import { Component } from 'dezel'
import { GestureEvent } from 'dezel'
import { PanGesture } from 'dezel'
import { PanGestureEvent } from 'dezel'
import './Handle.ds'

/**
 * @class Handle
 * @since 1.0.0
 */
export class Handle extends Component {

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @constructor
	 * @since 1.0.0
	 */
	constructor() {
		super()
		let gesture = new PanGesture()
		gesture.direction = 'both'
		this.on(gesture, this.onPan)
	}

	//--------------------------------------------------------------------------
	// Touch Event
	//--------------------------------------------------------------------------

	/**
	 * @method onPan
	 * @since 1.0.0
	 * @hidden
	 */
	@bound private onPan(event: PanGestureEvent) {
		this.top = event.viewY
		this.left = event.viewX
		this.emit('drag')
	}
}