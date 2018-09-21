/**
 * A Point object represents a point in a 2D space.
 * @class Point
 * @since 0.1.0
 */
export class Point {

	//--------------------------------------------------------------------------
	// Properties
	//--------------------------------------------------------------------------

	/**
	 * The value of the point on the X axis.
	 * @property x
	 * @since 0.1.0
	 */
	public x: number = 0

	/**
	 * The value of the point on the Y axis.
	 * @property y
	 * @since 0.1.0
	 */
	public y: number = 0

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * Initializes the point.
	 * @constructor
	 * @since 0.1.0
	 */
	constructor(x: number, y: number) {
		this.x = x
		this.y = y
	}
}