import { prop, bound, GestureEvent, PanGestureEvent } from 'dezel'
import { Fragment } from 'dezel'
import { TextView } from 'dezel'
import { Component } from 'dezel'
import { PanGesture } from 'dezel'
import './Handle.ds'

/**
 * @class Handle
 * @since 1.0.0
 */
export class Handle extends Component {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

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

	/**
	 * @inherited
	 * @method render
	 * @since 1.0.0
	 */
	public render() {
		return (
			<Fragment>

			</Fragment>
		)
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