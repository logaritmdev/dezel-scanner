import { Event, ViewRedrawEvent } from 'dezel'
import { View } from 'dezel'
import { Point } from '../geom/Point'

/**
 * @symbol PATH
 * @since 0.1.0
 */
export const PATH = Symbol('path')

/**
 * @class DocumentOutlineView
 * @super View
 * @since 0.1.0
 */
export class DocumentOutlineView extends View {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

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

	/**
	 * @property path
	 * @since 0.1.0
	 */
	public get path(): Array<Point> | undefined {
		return this[PATH]
	}

	/**
	 * @property path
	 * @since 0.1.0
	 */
	public set path(value: Array<Point> | undefined) {
		if (this[PATH] != value) {
			this[PATH] = value
			this.scheduleRedraw()
		}
	}

	//--------------------------------------------------------------------------
	// Events
	//--------------------------------------------------------------------------

	/**
	 * @inherited
	 * @method onRedraw
	 * @since 0.1.0
	 */
	onRedraw(event: Event<ViewRedrawEvent>) {

		let canvas = event.data.canvas

		var path = this.path
		if (path == null ||
			path.length < 4) {
			return
		}

		canvas.fillStyle = this.outlineFillColor
		canvas.strokeStyle = this.outlineLineColor
		canvas.lineWidth = 5

		canvas.beginPath()
		canvas.moveTo(path[0].x, path[0].y)
		canvas.lineTo(path[1].x, path[1].y)
		canvas.lineTo(path[2].x, path[2].y)
		canvas.lineTo(path[3].x, path[3].y)
		canvas.closePath()

		canvas.fill()
	}

	//--------------------------------------------------------------------------
	// Private API
	//--------------------------------------------------------------------------

	/**
	 * @property [PATH]
	 * @since 0.1.0
	 * @hidden
	 */
	private [PATH]: Array<Point> | undefined

}
