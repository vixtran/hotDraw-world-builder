
import org.jhotdraw.framework.Drawing;
import org.jhotdraw.framework.DrawingChangeEvent;
import org.jhotdraw.framework.DrawingEditor;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.standard.StandardDrawing;
import org.jhotdraw.standard.StandardDrawingView;
import org.jhotdraw.util.Geom;

import javax.swing.JViewport;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

/**
 * A view that can display drawings at an arbitrary scale.
 *
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class KeyDrawingView extends StandardDrawingView {

	/**
	 * The current scaling factor
	 */
	private double scale = 1.0;
	private double zoomSpeed = 2.0;
	private KeyGetterSetter keyGS = null;

	/*public KeyDrawingView(DrawingEditor editor) {
		this(editor, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}*/

	public KeyDrawingView(DrawingEditor editor, int width, int height, KeyGetterSetter k) {
		super(editor, width, height);
		keyGS = k;
	}

	/**
	 * @return The current zoom scale of this view.  The dimensions of
	 *         figures are multiplied by this number before display.
	 */
	public final double getScale() {
		return scale;
	}

	/**
	 * Sets a new zoom scale for this view.  The dimensions of figures
	 * are multiplied by this number before display.
	 */
	private void setScale(double newScale) {
		// "de"-scale with old scale
		Dimension oldSize = getUserSize();
		scale = newScale;
		// re-scale with new scale
		setUserSize(oldSize.width, oldSize.height);
		centralize(drawing());
		forceRedraw();
	}

	private void forceRedraw() {
		drawingInvalidated(new DrawingChangeEvent
				(drawing(), new Rectangle(getSize())));
		repairDamage();
	}

	/**
	 * Sets the size of this view in user coordinates.  The size of the view
	 * on the screen will be this size, multiplied by the current scale.
	 */
	public void setUserSize(int width, int height) {
		setSize((int) (width * getScale()),
				(int) (height * getScale()));
	}

	/**
	 * Sets the size of this view in user coordinates.  The size of the view
	 * on the screen will be this size, multiplied by the current scale.
	 */
	public void setUserSize(Dimension d) {
		setUserSize(d.width, d.height);
	}

	/**
	 * @return the size of this view, in screen coordinates
	 */
	public Dimension getSize() {
		return super.getSize();
	}

	public Dimension getViewportSize() {
		return getParent().getSize();
	}

	protected boolean hasZoomSupport() {
		return getParent() instanceof JViewport;
	}

	/**
	 * Sets the coordinates of the left top corner displayed by the view.<br>
	 */
	public void setOriginPosition(Point newOrigin) {
		setViewPosition(newOrigin);
		forceRedraw();
	}

	protected void setViewPosition(Point newPosition) {
		((JViewport)getParent()).setViewPosition(newPosition);
	}

	/**
	 * @return The size of this view, in user coordinates.  The size
	 *         on the screen is this size, multiplied by the current scale.
	 */
	public Dimension getUserSize() {
		Dimension screenSize = getSize();
		return new Dimension((int) (screenSize.width / getScale()),
				(int) (screenSize.height / getScale()));
	}

	/**
	 * Readjusts this view and its containing ScrollPane to display the
	 * given rectangle, which is given in user coordinates.  This method
	 * only works if this view is contained in a JViewport.  It throws
	 * a RuntimeException otherwise.
	 */
	public void zoom(int x, int y, int width, int height) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			double xScale = (double) viewportSize.width / (double) width;
			double yScale = (double) viewportSize.height / (double) height;
			double newScale = Math.min(xScale, yScale);

			// "de"-scale with old scale
			Dimension userSize = getUserSize();
			this.scale = newScale;
			// re-scale with new scale
			setUserSize(userSize);

			revalidate();
			setViewPosition(
					new Point((int) (x * getScale()), (int) (y * getScale())));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * Set this view's scale factor
	 */
	public void zoom(float newScale) {
		if (hasZoomSupport()) {
			JViewport viewport = (JViewport) getParent();
			Dimension viewportSize = viewport.getSize();
			Dimension userSize = getUserSize();
			scale = newScale;
			Point viewOrg = viewport.getViewPosition();
			viewOrg.x = viewOrg.x + (viewportSize.width / 2);
			viewOrg.y = viewOrg.y + (viewportSize.height / 2);
			int xScreen = (int) (viewOrg.x * scale);
			int yScreen = (int) (viewOrg.y * scale);
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			setUserSize(userSize);
			revalidate();
			viewport.setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * Zooms out by a factor of two, keeping point (x,y), which is given
	 * in user coordinates, in the center.
	 */
	public void zoomOut(int x, int y) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			// "de"-scale with old scale
			Dimension userSize = getUserSize();
			this.scale = getScale() / getZoomSpeed();
			int xScreen = (int) (x * getScale());
			int yScreen = (int) (y * getScale());
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			// re-scale with new scale
			setUserSize(userSize);
			revalidate();
			setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * InContext
	 * Zooms in by a factor of the current scale, keeping point (x,y), which is given
	 * in user coordinates, in the center.
	 */
	public void zoomIn(int x, int y) {
		if (hasZoomSupport()) {
			JViewport viewport = (JViewport) getParent();
			Dimension viewportSize = viewport.getSize();
			Dimension userSize = getUserSize();
			this.scale = getScale() * getZoomSpeed();
			int xScreen = (int) (x * getScale());
			int yScreen = (int) (y * getScale());
			int xOrigin = xScreen - viewportSize.width / 2;
			int yOrigin = yScreen - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			setUserSize(userSize);
			revalidate();
			viewport.setViewPosition(new Point(xOrigin, yOrigin));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	/**
	 * Sets the zoom scale to 1.0 and adjusts the scroll pane
	 * so that point (x, y) is in the center.
	 */
	public void deZoom(int x, int y) {
		if (hasZoomSupport()) {
			Dimension viewportSize = getViewportSize();
			Dimension userSize = getUserSize();
			int xOrigin = x - viewportSize.width / 2;
			int yOrigin = y - viewportSize.height / 2;
			if (xOrigin < 0) xOrigin = 0;
			if (yOrigin < 0) yOrigin = 0;
			this.scale = 1.0;
			setUserSize(userSize);
			revalidate();
			setViewPosition(new Point((int) (xOrigin),
					(int) (yOrigin)));
			forceRedraw();
		}
		else {
			throw new RuntimeException
					("zooming only works if this view is contained in a ScrollPane");
		}
	}

	public void paint(Graphics g) {
		super.paint(transformGraphics(g, getScale()));
	}

	public Graphics getGraphics() {
		return transformGraphics(super.getGraphics(), getScale());
	}

	private final Graphics transformGraphics(Graphics g, double currentScale) {
		if (currentScale != 1.0) {
			Graphics2D g2 = (Graphics2D) g;
			// Don't use setTransform() here because that would destroy
			// any transformation that Swing sets for partial redrawing.
			// Simply add our own transformation to any existing one.
			g2.transform(AffineTransform.getScaleInstance(currentScale, currentScale));
		}
        return g;
	}

	/**
	 * Constrain to user coordinates, not screen coordinates.
	 */
	protected Point constrainPoint(Point p) {
		Dimension size = getSize();
		p.x = Geom.range(1, (int) (size.width / getScale()), p.x);
		p.y = Geom.range(1, (int) (size.height / getScale()), p.y);
		if (getConstrainer() != null) {
			return getConstrainer().constrainPoint(p);
		}
		return p;
	}

	public void drawBackground(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0,
				(int) (getBounds().width / getScale()),
				(int) (getBounds().height / getScale()));
	}

	private void centralize(Drawing d, Dimension bounds) {
		Point boundsCenter = new Point(bounds.width / 2, bounds.height / 2);
		Rectangle r = ((StandardDrawing) d).displayBox();
		Point drawingCenter = new Point(r.x + r.width / 2, r.y + r.height / 2);
		int diffX = boundsCenter.x - drawingCenter.x;
		int diffY = boundsCenter.y - drawingCenter.y;
		if (diffX != 0 || diffY != 0) {
			for (FigureEnumeration fe = d.figures(); fe.hasNextFigure();) {
				fe.nextFigure().moveBy(diffX, diffY);
			}
		}
	}

	private void centralize(Drawing d) {
		centralize(d, getUserSize());
	}

	public void setDrawing(Drawing d) {
		super.setDrawing(d);

		Rectangle r = ((StandardDrawing) d).displayBox();
		//Dimension drawingSize = new Dimension(r.width, r.height);
		Dimension viewportSize = new Dimension(r.width, r.height);
		if (getParent() != null) {
			viewportSize = getViewportSize();
		}
/*
		Dimension userSize = new Dimension(viewportSize);
		this.scale = 1.0;

		while (drawingSize.width > userSize.width ||
				drawingSize.height > userSize.height) {
			this.scale = getScale() / 2.0;
			userSize.width = userSize.width * 2;
			userSize.height = userSize.height * 2;
		}
		centralize(d, userSize);
*/
		super.setPreferredSize(viewportSize);
		super.setSize(viewportSize);
		revalidate();
	}

	public Dimension getMinimumSize() {
		return super.getSize();
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	/**
	 * Overridden to scale damage to screen coordinates.
	 */
	public void repairDamage() {
		Rectangle damagedArea = getDamage();
		if (damagedArea != null) {
			repaint((int) (damagedArea.x * getScale()),
					(int) (damagedArea.y * getScale()),
					(int) (damagedArea.width * getScale()),
					(int) (damagedArea.height * getScale()));
			setDamage(null);
		}
	}

	/**
	 * Overridden to accumulate damage in an instance variable of this class.
	 */
	public void drawingInvalidated(DrawingChangeEvent e) {
		Rectangle r = e.getInvalidatedRectangle();
		if (getDamage() == null) {
			setDamage(r);
		}
		else {
			Rectangle damagedArea = getDamage();
			damagedArea.add(r);
			// the returned rectange may be a clone so we better set it again
			setDamage(damagedArea);
		}
	}

	/**
	 * @return a new MouseEvent, the coordinates of which are transformed
	 *         to compensate for the current zoom factor
	 */
	private MouseEvent createScaledEvent(MouseEvent e) {
		return new MouseEvent(e.getComponent(),
				e.getID(),
				e.getWhen(),
				e.getModifiers(),
				(int) (e.getX() / getScale()),
				(int) (e.getY() / getScale()),
				e.getClickCount(),
				e.isPopupTrigger());
	}


	protected MouseListener createMouseListener() {
		return new StandardDrawingView.DrawingViewMouseListener() {
			public void mousePressed(MouseEvent e) {
				super.mousePressed(createScaledEvent(e));
			}
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(createScaledEvent(e));
			}
		};
	}

	protected MouseMotionListener createMouseMotionListener() {
		return new StandardDrawingView.DrawingViewMouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(createScaledEvent(e));
			}
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(createScaledEvent(e));
			}
		};
	}

	protected KeyListener createKeyListener() {
		return new StandardDrawingView.DrawingViewKeyListener() {
			public void keyPressed(KeyEvent e) 
			{
				super.keyPressed(e);
				if (e.getKeyChar() == ' ') {
					forceRedraw();
				}
				else if (e.getKeyCode() == KeyEvent.VK_A) {
					keyGS.setLeft(true);
				}
				else if (e.getKeyCode() == KeyEvent.VK_D) {
					keyGS.setRight(true);
				}
				else if (e.getKeyCode() == KeyEvent.VK_W) {
					keyGS.setJump(true);
				}
				else {
					super.keyPressed(e);
				}
			}
			
			public void keyReleased(KeyEvent e) 
			{
				super.keyReleased(e);
				if (e.getKeyChar() == ' ') 
				{
					forceRedraw();
				}
				else if (e.getKeyCode() == KeyEvent.VK_A) {
					keyGS.setLeft(false);
				}
				else if (e.getKeyCode() == KeyEvent.VK_D) {
					keyGS.setRight(false);
				}
				else if (e.getKeyCode() == KeyEvent.VK_W) {
					//keyGS.setJump(false);
				}
				else {
					super.keyReleased(e);
				}
			}
		};
	}

	/**
	 * Returns the current zoom speed
	 */
	public double getZoomSpeed()
	{
		return zoomSpeed;
	}

	/**
	 * Set the zoom speed. Will be greater than 1.
	 */
	public void setZoomSpeed(double newZoomSpeed)
	{
		// check greater than 1. A smaller value would reverse the zooming
		// operation, and a zero value would provoque divide by zero exceptions
		zoomSpeed = Math.max(1.1, newZoomSpeed);
	}
}
