import org.jhotdraw.framework.ConnectionFigure;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.samples.javadraw.BouncingDrawing;

public class BouncingDrawingPlus extends BouncingDrawing
{
	private AIType aitype;
	private Figure[] figures = new Figure[30]; //MAX # OF FIGURES
	private int countFigures = 0;//CURRENT NUMBER OF FIGURES
	private KeyGetterSetter keyGS = null;
	
	public BouncingDrawingPlus(KeyGetterSetter k)
	{
		super();
		keyGS = k;
	}
	
	public synchronized Figure add(Figure figure)
	{
		//EVERYTHING BACKGROUND BY DEFAULT
		if (!(figure instanceof AnimationDecoratorPlus) && !(figure instanceof ConnectionFigure))
		{
			figure = new AnimationDecoratorPlus(figure, AIType.BACKGROUND, 2, 0, 9, keyGS); //SET SPEED HERE
			if(!(figure instanceof ConnectionFigure))
			{
				figures[countFigures] = figure;
				countFigures++;
			}
		}
		
		return super.add(figure);
	}
	
	public synchronized Figure remove(Figure figure)
	{
		Figure f = super.remove(figure);
		
		if (f instanceof AnimationDecoratorPlus)
		{
			return ((AnimationDecoratorPlus) f).peelDecoration();
		}
		
		return f;
	}
	
	/**
	 * @param figure figure to be replaced
	 * @param replacement figure that should replace the specified figure
	 * @return the figure that has been inserted (might be different from the figure specified)
	 */
	public synchronized Figure replace(Figure figure, Figure replacement)
	{
		if (!(replacement instanceof AnimationDecoratorPlus) && !(replacement instanceof ConnectionFigure))
		{
			replacement = new AnimationDecoratorPlus(replacement, aitype, 2, 0, 9, keyGS);
		}
		
		return super.replace(figure, replacement);
	}

	public void animationStep()
	{
		//FigureEnumeration fe = figures();
		/*int tempFigures = 0;
		
		//check if new figures have been added
		while (fe.hasNextFigure())
		{
			Figure f = fe.nextFigure();
			tempFigures++;
			
			//add new figures to the array to check for collision and animation
			if(!(f instanceof ConnectionFigure) && tempFigures > countFigures)
			{
				figures[countFigures] = f;
				countFigures++;
			}
		}*/
		
		//Check collision
		for(int e = 0; e < countFigures - 1; e++)
		{
			for(int f = e + 1; f < countFigures; f++)
			{
				if(((AnimationDecoratorPlus) figures[e]).getAIType() != AIType.BACKGROUND &&
				((AnimationDecoratorPlus) figures[f]).getAIType() != AIType.BACKGROUND && (e != f))
				{
					if(((AnimationDecoratorPlus)figures[e]).getBox().intersects(((AnimationDecoratorPlus)
							figures[f]).getBox()))
					{
						((AnimationDecoratorPlus) figures[e]).collision(figures[f], 
								((AnimationDecoratorPlus) figures[f]).getAIType());
						((AnimationDecoratorPlus) figures[f]).collision(figures[e], 
								((AnimationDecoratorPlus) figures[e]).getAIType());
					}
				}
			}
			
			((AnimationDecoratorPlus) figures[e]).animationStep();
			
			if(e == countFigures - 2)
			{
				((AnimationDecoratorPlus) figures[e + 1]).animationStep();
			}
		}
		
		if(countFigures == 1)
		{
			((AnimationDecoratorPlus) figures[0]).animationStep();
		}
		
		/*fe = figures();
		//Activate movement
		while (fe.hasNextFigure())
		{
			Figure f = fe.nextFigure();
			
			if(!(f instanceof ConnectionFigure) && ((AnimationDecoratorPlus) f).getAIType() != AIType.BACKGROUND)
			{
				((AnimationDecoratorPlus) f).animationStep();
			}
		}*/
	}
	
	//Resolve change in AI Property
	public void changeAI(Figure fig, AIType ait)
	{
		FigureEnumeration fe = figures();
		
		while (fe.hasNextFigure())
		{
			Figure f = fe.nextFigure();
			
			if(f == fig)
			{
				((AnimationDecoratorPlus) f).setAIType(ait);
			}
		}
	}
}