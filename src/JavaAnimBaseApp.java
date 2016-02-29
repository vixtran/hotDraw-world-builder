import org.jhotdraw.framework.*;
import org.jhotdraw.samples.javadraw.Animator;
import org.jhotdraw.samples.javadraw.MySelectionTool;
import org.jhotdraw.standard.*;
import org.jhotdraw.figures.*;
import org.jhotdraw.util.*;
import org.jhotdraw.application.*;
import org.jhotdraw.contrib.*;
import org.jhotdraw.contrib.zoom.ZoomDrawingView;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.net.URL;

public  class JavaAnimBaseApp extends MDI_DrawApplication
{
	private Animator            fAnimator;
	private static String       fgSampleImagesPath = "/org/jhotdraw/images2";
	private static String       fgSampleImagesResourcePath = fgSampleImagesPath + "/";
	private Drawing dwg;
	private KeyGetterSetter keyGS = new KeyGetterSetter();

	public JavaAnimBaseApp() 
	{
		super("JHotDraw");
	}

	/**
	 * Expose constructor for benefit of subclasses.
	 *
	 * @param title The window title for this application's frame.
	 */
	public JavaAnimBaseApp(String title)
	{
		super(title);
	}

	/**
	 * Factory method which create a new instance of this
	 * application.
	 *
	 * @return	newly created application
	 */
	protected DrawApplication createApplication()
	{
		return new JavaAnimBaseApp();
	}

	protected DrawingView createDrawingView(Drawing newDrawing)
	{
		Dimension d = getDrawingViewSize();
		DrawingView newDrawingView = new KeyDrawingView(this, d.width, d.height, keyGS);
		newDrawingView.setDrawing(newDrawing);
		// notify listeners about created view when the view is added to the desktop
		//fireViewCreatedEvent(newDrawingView);
		return newDrawingView;
	}

	//-- application life cycle --------------------------------------------

	public void destroy()
	{
		super.destroy();
		endAnimation();
	}

	//-- DrawApplication overrides -----------------------------------------

	protected void createTools(JToolBar palette)
	{
		super.createTools(palette);

		Tool tool = new UndoableTool(new CreationTool(this, new RectangleFigure()));
		palette.add(createToolButton(IMAGES + "RECT", "Rectangle Tool", tool));

		tool = new UndoableTool(new CreationTool(this, new RoundRectangleFigure()));
		palette.add(createToolButton(IMAGES + "RRECT", "Round Rectangle Tool", tool));

		tool = new UndoableTool(new CreationTool(this, new EllipseFigure()));
		palette.add(createToolButton(IMAGES + "ELLIPSE", "Ellipse Tool", tool));

		tool = new UndoableTool(new PolygonTool(this));
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new UndoableTool(new CreationTool(this, new TriangleFigure()));
		palette.add(createToolButton(IMAGES + "TRIANGLE", "Triangle Tool", tool));

		tool = new UndoableTool(new CreationTool(this, new DiamondFigure()));
		palette.add(createToolButton(IMAGES + "DIAMOND", "Diamond Tool", tool));

		/**tool = new UndoableTool(new CreationTool(this, new LineFigure()));
		palette.add(createToolButton(IMAGES + "LINE", "Line Tool", tool));

		tool = new UndoableTool(new ConnectionTool(this, new LineConnection()));
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));

		tool = new UndoableTool(new ConnectionTool(this, new ElbowConnection()));
		palette.add(createToolButton(IMAGES + "OCONN", "Elbow Connection Tool", tool));

		Component button = new JButton("Hello World");
		tool = new CreationTool(this, new ComponentFigure(button));
		palette.add(createToolButton(IMAGES + "RECT", "Component Tool", tool));

		tool = new TextAreaTool(this, new TextAreaFigure());
		palette.add(createToolButton(IMAGES + "TEXTAREA", "TextArea Tool", tool));*/

	}

	protected Tool createSelectionTool() 
	{
		return new MySelectionTool(this);
	}

	protected void createMenus(JMenuBar mb) 
	{
		super.createMenus(mb);
		
		addMenuIfPossible(mb, createImagesMenu());
		addMenuIfPossible(mb, createPropertyMenu());
		addMenuIfPossible(mb, createAnimationMenu());
		addMenuIfPossible(mb, createWindowMenu());
	}
	
	protected JMenu createImagesMenu()
	{
		CommandMenu menu = new CommandMenu("Images");
		URL url = getClass().getResource(fgSampleImagesPath);
		
		if (url == null)
		{
			throw new JHotDrawRuntimeException("Could not locate images: " + fgSampleImagesPath);
		}
		
		File imagesDirectory = new File(url.getFile());

		try 
		{
			String name = "natureBackground.gif";
			String path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "earthBackground.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "darkBackground.jpg";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "desertBackground.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "ruinedcityBackground.jpg";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "cityBackground.jpg";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "brickFloor.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "logFloor.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "pressureFloor.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "signFloor.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "stoneFloor.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "goombaAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "cactusAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "ghostAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "pirateAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "piratepartyAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "robotAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "spiderAnimated.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "diamondItem.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "treasureItem.gif";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
			name = "tree1.png";
			path = fgSampleImagesResourcePath+name;
			menu.add(new UndoableCommand(new InsertImageCommand(name, path, this)));
		}
		catch (Exception e) 
		{
			// do nothing
		}
		
		return menu;
	}

	protected JMenu createPropertyMenu() 
	{
		CommandMenu menu = new CommandMenu("Properties");
		Command cmd = new AbstractCommand("Background", this) {
			public void execute() 
			{
				FigureEnumeration fe = getDrawingEditor().view().selection();
				
				while(fe.hasNextFigure())
				{
					Figure f = fe.nextFigure();
					((BouncingDrawingPlus) dwg).changeAI(f, AIType.BACKGROUND);
				}
			}
		};
		menu.add(cmd);

		cmd = new AbstractCommand("Floor", this) {
			public void execute() 
			{
				FigureEnumeration fe = getDrawingEditor().view().selection();
				
				while(fe.hasNextFigure())
				{
					Figure f = fe.nextFigure();
					((BouncingDrawingPlus) dwg).changeAI(f, AIType.FLOOR);
				}
			}
		};
		menu.add(cmd);
		
		cmd = new AbstractCommand("Enemy", this) {
			public void execute() 
			{
				FigureEnumeration fe = getDrawingEditor().view().selection();
				
				while(fe.hasNextFigure())
				{
					Figure f = fe.nextFigure();
					((BouncingDrawingPlus) dwg).changeAI(f, AIType.ENEMY);
				}
			}
		};
		menu.add(cmd);
		
		cmd = new AbstractCommand("Player", this) {
			public void execute() 
			{
				FigureEnumeration fe = getDrawingEditor().view().selection();
				
				while(fe.hasNextFigure())
				{
					Figure f = fe.nextFigure();
					((BouncingDrawingPlus) dwg).changeAI(f, AIType.PLAYER);
				}
			}
		};
		menu.add(cmd);
		
		return menu;
	}
	
	protected JMenu createAnimationMenu() {
		CommandMenu menu = new CommandMenu("Animation");
		Command cmd = new AbstractCommand("Start Animation", this) {
			public void execute() {
				startAnimation();
			}
		};
		menu.add(cmd);

		cmd = new AbstractCommand("Stop Animation", this) {
			public void execute() {
				endAnimation();
			}
		};
		menu.add(cmd);
		return menu;
	}

	protected JMenu createWindowMenu() {
		CommandMenu menu = new CommandMenu("Window");
		Command cmd = new AbstractCommand("New View", this) {
			public void execute() {
				newView();
			}
		};
		menu.add(cmd);

		cmd = new AbstractCommand("New Window", this, false) {
			public void execute() {
				newWindow(createDrawing());
			}
		};
		menu.add(cmd);

		menu.addSeparator();
		menu.add(new WindowMenu("Window List", (MDIDesktopPane)getDesktop(), this));
		return menu;
	}

	protected Drawing createDrawing() 
	{
		dwg = new BouncingDrawingPlus(keyGS);
        dwg.setTitle(getDefaultDrawingTitle());
		return dwg;
	}

	//---- animation support --------------------------------------------

	public void startAnimation()
	{
		if (view().drawing() instanceof Animatable && fAnimator == null)
		{
			fAnimator = new Animator((Animatable)view().drawing(), view());
			fAnimator.start();
		}
	}

	public void endAnimation() 
	{
		if (fAnimator != null) 
		{
			fAnimator.end();
			fAnimator = null;
		}
	}
}