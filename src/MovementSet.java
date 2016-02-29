import org.jhotdraw.framework.Figure;

public interface MovementSet 
{
	public int moveRight(int speed);
	
	public int moveLeft(int speed);
	
	public void collision(Figure collidingFigure, AIType colliType);
}