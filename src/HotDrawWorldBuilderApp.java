import org.jhotdraw.samples.javadraw.Animator;
import org.jhotdraw.util.Animatable;

public class HotDrawWorldBuilderApp extends JavaAnimBaseApp
{
	private Animator fAnimator;
	
	public HotDrawWorldBuilderApp()
	{
		super("Hot Draw World Builder");
	}
	
	//-- application life cycle --------------------------------------------

	/*public void destroy()
	{
		super.destroy();
		endAnimation();
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
	}*/

	public static void main(String[] args) 
	{
		HotDrawWorldBuilderApp window = new HotDrawWorldBuilderApp();
		window.open();
	}
}