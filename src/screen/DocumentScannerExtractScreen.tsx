import { ref } from 'dezel'
import { bound } from 'dezel'
import { Event } from 'dezel'
import { Image } from 'dezel'
import { Fragment } from 'dezel'
import { View } from 'dezel'
import { Screen } from 'dezel'
import { Header } from 'dezel'
import { Footer } from 'dezel'
import { Content } from 'dezel'
import { Button } from 'dezel'
import { NavigationBar } from 'dezel'
import { NavigationBarBackButton } from 'dezel'
import { Point } from '../geom/Point'
import { Handle } from '../component/Handle'
import { DocumentOutlineView } from '../view/DocumentOutlineView'
import { DocumentExtractorView } from '../view/DocumentExtractorView'
import './DocumentScannerExtractScreen.ds'

/**
 * @class DocumentScannerExtractScreen
 * @since 1.0.0
 */
export class DocumentScannerExtractScreen<Image> extends Screen {

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
	 * @inherited
	 * @method render
	 * @since 1.0.0
	 */
	public render() {
		return (
			<Fragment>
				<Header>
					<NavigationBar backButton={
						<NavigationBarBackButton onPress={() => this.dismiss()} />
					} />
				</Header>
				<Content>
					<DocumentExtractorView id="extractor" image={this.image} />
					<DocumentOutlineView id="outline" />
					<Handle id="tl" onDrag={this.onDragHandle} />
					<Handle id="tr" onDrag={this.onDragHandle} />
					<Handle id="bl" onDrag={this.onDragHandle} />
					<Handle id="br" onDrag={this.onDragHandle} />
				</Content>
				<Footer>
					<Button style="accept" onPress={this.onAcceptButtonPress} />
				</Footer>
			</Fragment>
		)
	}

	/**
	 * @inherited
	 * @method onBeforePresent
	 * @since 1.0.0
	 */
	public onCreate() {
		this.statusBarForegroundColor = 'white'
	}

	/**
	 * @inherited
	 * @method onBeforePresent
	 * @since 1.0.0
	 */
	public onBeforePresent() {

		let w = this.measuredWidth
		let h = this.measuredHeight

		let cx = w / 2
		let cy = h / 2

		let itemW = this.tl.measuredWidth
		let itemH = this.tl.measuredHeight

		let tl = new Point(cx - w / 4 - itemW / 2, cy - h / 4 - itemH / 2)
		let tr = new Point(cx + w / 4 - itemW / 2, cy - h / 4 - itemH / 2)
		let bl = new Point(cx - w / 4 - itemW / 2, cy + h / 4 - itemH / 2)
		let br = new Point(cx + w / 4 - itemW / 2, cy + h / 4 - itemH / 2)

		this.tl.top = tl.y
		this.tl.left = tl.x

		this.tr.top = tr.y
		this.tr.left = tr.x

		this.br.top = br.y
		this.br.left = br.x

		this.bl.top = bl.y
		this.bl.left = bl.x

		this.tlp = new Point(tl.x + itemW / 2, tl.y + itemH / 2)
		this.trp = new Point(tr.x + itemW / 2, tr.y + itemH / 2)
		this.brp = new Point(br.x + itemW / 2, br.y + itemH / 2)
		this.blp = new Point(bl.x + itemW / 2, bl.y + itemH / 2)

		this.outline.path = [
			this.tlp,
			this.trp,
			this.brp,
			this.blp
		]
	}

	//--------------------------------------------------------------------------
	// Private API
	//--------------------------------------------------------------------------

	/**
	 * @property outline
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private outline!: DocumentOutlineView

	/**
	 * @property extractor
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private extractor!: DocumentExtractorView

	/**
	 * @property br
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private tl!: Handle

	/**
	 * @property br
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private tr!: Handle

	/**
	 * @property br
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private bl!: Handle

	/**
	 * @property br
	 * @since 1.0.0
	 * @hidden
	 */
	@ref private br!: Handle

	/**
	 * @property tlp
	 * @since 1.0.0
	 * @hidden
	 */
	private tlp: Point = new Point(0, 0)

	/**
	 * @property trp
	 * @since 1.0.0
	 * @hidden
	 */
	private trp: Point = new Point(0, 0)

	/**
	 * @property blp
	 * @since 1.0.0
	 * @hidden
	 */
	private blp: Point = new Point(0, 0)

	/**
	 * @property brp
	 * @since 1.0.0
	 * @hidden
	 */
	private brp: Point = new Point(0, 0)

	/**
	 * @method onDragHandle
	 * @since 1.0.0
	 * @hidden
	 */
	@bound private onDragHandle(event: Event) {

		let cx = 0
		let cy = 0

		let view = event.sender
		if (view instanceof View) {
			cx = view.measuredLeft + view.measuredWidth / 2
			cy = view.measuredTop + view.measuredHeight / 2
		}

		switch (event.sender) {

			case this.tl:
				this.tlp.x = cx
				this.tlp.y = cy
				break

			case this.tr:
				this.trp.x = cx
				this.trp.y = cy
				break

			case this.bl:
				this.blp.x = cx
				this.blp.y = cy
				break

			case this.br:
				this.brp.x = cx
				this.brp.y = cy
				break
		}

		this.outline.path = [
			this.tlp,
			this.trp,
			this.brp,
			this.blp
		]
	}

	/**
	 * @method onAcceptButtonPress
	 * @since 1.0.0
	 * @hidden
	 */
	@bound private onAcceptButtonPress(event: Event) {

		let extracted = this.extractor.extract(
			this.tlp, this.trp,
			this.blp, this.brp
		)

		if (extracted) {
			this.result = extracted
		}

		this.dismiss()
	}
}
