import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.samples.javadraw.AnimationDecorator;
import org.jhotdraw.util.StorableInput;
import org.jhotdraw.util.StorableOutput;

public class AnimationDecoratorPlus extends AnimationDecorator implements MovementSet
{
	private int fXSpeed;
	private int fXTemp;
	private int fYSpeed;
	private int fDeceleration;
	private Rectangle box;
	private AIType aitype;
	//private boolean collide;
	private boolean playerCollisionRight;
	private boolean playerCollisionLeft;
	private boolean playerCollisionUp;
	private boolean playerCollisionDown;
	private KeyGetterSetter kGS = null;
	
	public AnimationDecoratorPlus()
	{
		super();
	}
	
	public AnimationDecoratorPlus(Figure figure, AIType atype, int xSpeed, int ySpeed, int deceleration,
			KeyGetterSetter k)
	{
		super(figure);
		aitype = atype;
		fXSpeed = xSpeed;
		fXTemp = fXSpeed;
		fYSpeed = ySpeed;
		fDeceleration = deceleration;
		
		changeSpeeds();
		//collide = false;
		playerCollisionRight = false;
		playerCollisionLeft = false;
		playerCollisionDown = false;
		playerCollisionUp = false;
		kGS = k;
		box = displayBox();
	}
	
	public void velocity(int xSpeed, int ySpeed) 
	{
		fXSpeed = xSpeed;
		fXTemp = fXSpeed;
		fYSpeed = ySpeed;
	}

	public Point velocity() 
	{
		return new Point(fXSpeed, fYSpeed);
	}

	public synchronized void animationStep()
	{
		if(aitype == AIType.ENEMY)
		{
			if(box.x + box.width > 600)//bounce if greater than 600
			{
				fXSpeed = moveLeft(fXTemp);
			}
			else if(box.x < 0)//bounce if less than 0
			{
				fXSpeed = moveRight(fXTemp);
			}
			
			if(playerCollisionDown == false)//drop (gravity) if no object support below
			{
				fYSpeed = fDeceleration;
			}
			
			if(box.y > 450)//hold below window if object falls below window
			{
				fYSpeed = 0;
			}
			else if(box.y < 0 && playerCollisionDown == false)//hold below window if object rises above window
			{
				fYSpeed = fDeceleration;
			}
			else if(box.y < 0)
			{
				fYSpeed = 0;
			}
			
			moveBy(fXSpeed, fYSpeed);//move the object
		}
		
		if(aitype == AIType.PLAYER)
		{
			if(fXSpeed > 0 && playerCollisionLeft == true)
			{
				fXSpeed = moveRight(0);
			}
			else if(fXSpeed < 0 && playerCollisionRight == true)
			{
				fXSpeed = moveLeft(0);
			}
			
			if(kGS.getRight() == true && playerCollisionRight == false)
			{
				fXSpeed = moveRight(fXTemp);
				//kGS.setRight(false);
			}
			else if(kGS.getLeft() == true && playerCollisionLeft == false)
			{
				fXSpeed = moveLeft(fXTemp);
				//kGS.setLeft(false);
			}
			else
			{
				fXSpeed = 0;
			}
			
			if(kGS.getJump() == true && playerCollisionUp == false && playerCollisionDown == true)
			{
				fYSpeed = -fDeceleration - 91;
				//kGS.setJump(false);
			}
			
			if(box.x + box.width > 600)//bounce if greater than 600
			{
				//fXSpeed = moveLeft(fXSpeed);
				fXSpeed = moveLeft(fXTemp);
			}
			else if(box.x < 0)//bounce if less than 0
			{
				//fXSpeed = moveRight(fXSpeed);
				fXSpeed = moveRight(fXTemp);
			}
			
			if(playerCollisionDown == false && kGS.getJump() == false)//drop (gravity) if no object support below
			{
				fYSpeed = fDeceleration;
			}
			
			if(box.y > 450)//hold below window if object falls below window
			{
				fYSpeed = 0;
			}
			else if(box.y < 0 && playerCollisionDown == false)//hold below window if object rises above window
			{
				fYSpeed = fDeceleration;
			}
			else if(box.y < 0)
			{
				fYSpeed = 0;
			}
			
			kGS.setJump(false);
			moveBy(fXSpeed, fYSpeed);//move the object
		}
		
		//collide = false;
		playerCollisionRight = false;
		playerCollisionLeft = false;
		playerCollisionDown = false;
		playerCollisionUp = false;
		box = displayBox();
	}

	public int moveRight(int speed) 
	{
		return Math.abs(speed);
	}

	public int moveLeft(int speed)
	{
		return -Math.abs(speed);
	}

	public synchronized void collision(Figure collidingFigure, AIType colliType)//if colliding
	{
		if(aitype == AIType.ENEMY || aitype == AIType.PLAYER)
		{
			//object moving right
			/*if(fXSpeed > 0 && collidingFigure.containsPoint(this.box.x + this.box.width - 5, this.center().y))
			{
				fXSpeed = moveLeft(fXSpeed);
			}
			else if(fXSpeed < 0 && collidingFigure.containsPoint(this.box.x + 5, 
					this.center().y))//object moving left, this.center().y
			{
				fXSpeed = moveRight(fXSpeed);
			}*/
			
			if(Math.abs(collidingFigure.displayBox().y - (this.box.y + this.box.height)) < 10)//object falling
			{
				//collide = true;
				fYSpeed = collidingFigure.displayBox().y - (this.box.y + this.box.height) + 1;
				//moveBy(fXSpeed, -(collidingFigure.displayBox().y - (this.box.y + this.box.height) + 1));
				//fYSpeed = 0;
				playerCollisionDown = true;
			}
			else if(fYSpeed < 0 && collidingFigure.containsPoint(this.box.x + (this.box.width/2), 
					this.box.y))//when object is rising
			{
				//fYSpeed = -(collidingFigure.displayBox().y + collidingFigure.displayBox().height) - 
						//this.box.y;
				moveBy(fXSpeed, (collidingFigure.displayBox().y + collidingFigure.displayBox().height) - 
						this.box.y);//move the object
				playerCollisionUp = true;
			}
			else if(this.box.intersectsLine(collidingFigure.displayBox().x, collidingFigure.displayBox().y,
					collidingFigure.displayBox().x, collidingFigure.displayBox().y
					+ collidingFigure.displayBox().height) && fXSpeed > 0)
			{
				fXSpeed = moveLeft(fXTemp);
				playerCollisionRight = true;
			}
			else if(this.box.intersectsLine(collidingFigure.displayBox().x + 
					collidingFigure.displayBox().width, collidingFigure.displayBox().y,
					collidingFigure.displayBox().x + collidingFigure.displayBox().width, 
					collidingFigure.displayBox().y + collidingFigure.displayBox().height) && fXSpeed < 0)
			{
				fXSpeed = moveRight(fXTemp);
				playerCollisionLeft = true;
			}
		}
	}
	
	public Rectangle getBox()
	{
		return box;
	}
	
	public AIType getAIType()
	{
		return aitype;
	}
	
	public void setAIType(AIType ai)
	{
		aitype = ai;
		changeSpeeds();
	}
	
	public void changeSpeeds()
	{
		if(aitype == AIType.PLAYER)
		{
			fXSpeed = 6;
			fXTemp = fXSpeed;
			fYSpeed = 0;
			fDeceleration = 5;
		}
		else
		{
			fXSpeed = 2;
			fXTemp = fXSpeed;
			fYSpeed = 0;
			fDeceleration = 9;
		}
	}
	
	//-- store / load ----------------------------------------------

	public void write(StorableOutput dw)
	{
		super.write(dw);
		dw.writeInt(fXSpeed);
		dw.writeInt(fYSpeed);
	}

	public void read(StorableInput dr) throws IOException
	{
		super.read(dr);
		fXSpeed = dr.readInt();
		fYSpeed = dr.readInt();
	}
}