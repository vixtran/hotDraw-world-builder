
public class KeyGetterSetter 
{
	private boolean moveRight;
	private boolean moveLeft;
	private boolean jump;
	
	public KeyGetterSetter()
	{
		moveRight = false;
		moveLeft = false;
		jump = false;
	}
	
	public void setRight(boolean r)
	{
		moveRight = r;
	}
	
	public void setLeft(boolean l)
	{
		moveLeft = l;
	}
	
	public void setJump(boolean j)
	{
		jump = j;
	}
	
	public boolean getRight()
	{
		return moveRight;
	}
	
	public boolean getLeft()
	{
		return moveLeft;
	}
	
	public boolean getJump()
	{
		return jump;
	}
}