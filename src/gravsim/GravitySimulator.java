package gravitypackage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



@SuppressWarnings("serial")
public class GravitySimulator extends JPanel implements MouseListener, MouseMotionListener
{
	ArrayList<Entity> EntityList = new ArrayList<Entity>();//the type of the Entity. Can be 

	//these variables are for the tracing. Every dot has a x and y coordinate
	static final int MAX_TRACER_SIZE = 5000;
	ArrayList<Double> TracerX = new ArrayList<Double>();
	ArrayList<Double> TracerY = new ArrayList<Double>();


	boolean mouseDown = false;
	boolean gamepaused = true;

	int mousex = 0;
	int mousey = 0;

	int speed = 100;

	boolean tracerOn = true;

	public GravitySimulator()
	{
		addMouseListener(this);
		addMouseMotionListener(this);	 
	}

	@Override
	public void mouseClicked(MouseEvent event) 
	{
		mousex = event.getX();
		mousey = event.getY();

		//pause button
		if(IsButtonClicked(710, 20, 80, 20))
		{
			gamepaused = !gamepaused;
		}
		//toggles tracing of Entities
		if(IsButtonClicked(890, 20, 80,20))
		{
			TracerX.clear();
			TracerY.clear();
			tracerOn = !tracerOn;
		}

		//changes the speed of the application
		if(IsButtonClicked(710, 260, 260,20))
		{
			speed = (int)getResponse("Input application speed. Only positive integers.", speed, true, true);
		}

		checkEdit();//check if this action is to change the editable object.
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{	

	}

	@Override
	public void mouseExited(MouseEvent e) 
	{

	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		mouseDown = true;

		//creates a Entity that can be dragged
		if(IsButtonClicked(800, 20, 80, 20) && mouseDown)
		{
			if(getEdited() > -1)
			{
				EntityList.get(getEdited()).edited = false;
			}
			Entity entity = new Entity(mousex, mousey, 1, Entity.TYPE_DRAGGABLE);
			entity.edited = true;
			EntityList.add(entity);
		}

		checkEdit();//also, check if this is to click on an editable entity
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{    
		mouseDown = false;  
	}


	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{	
		mousex = e.getX();
		mousey = e.getY();
	}

	//checks if mouse is within bounds of button
	boolean IsButtonClicked(int x, int y, int width, int height)
	{
		if(mousex >= x && mousex <= x+width && mousey >= y && mousey <=y+height)
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	double getResponse(String prompt, double defaultValue, 
			boolean needstobeint, boolean needstobepositive)
	{
		gamepaused = true;//pause game
		//creates the actual prompt.
		String response = JOptionPane.showInputDialog(prompt);
		double numberInResponse = 0;
		try
		{
			//convert the string response into a double
			numberInResponse = Double.parseDouble(response);
			//if the number is not an integer when it should be
			if(needstobeint && numberInResponse != (int)numberInResponse)
			{
				JOptionPane.showMessageDialog(null, "Invalid input. Integer value required.");
				numberInResponse = defaultValue;
			}
			//if the number is negative when it should be positive
			if(needstobepositive && numberInResponse <= 0)
			{
				JOptionPane.showMessageDialog(null, "Invalid input. Positive values only.");
				numberInResponse = defaultValue;
			}
		}
		catch(java.lang.NumberFormatException NumError)
		{
			//if it cannot parse the string
			JOptionPane.showMessageDialog(null, "Invalid input. Only numeric characters accepted.");
			numberInResponse = defaultValue;
		}
		catch(java.lang.NullPointerException canceled)
		{
			//if canceled
			numberInResponse = defaultValue;
		}
		return numberInResponse;
	}

	int getEdited()
	{
		for(int i = 0; i < EntityList.size();i++)
		{
			if(EntityList.get(i).edited)
			{
				return i;
			}
		}
		return -1;
	}



	void collideEntities(int EntityIndex1, int EntityIndex2)
	{
		//double check to make sure these are within bounds
		if(EntityIndex1 < EntityList.size() &&
				EntityIndex2 < EntityList.size() && 
				EntityIndex1!=EntityIndex2)//check that we are not colliding the same objects
		{
			Entity e1 = EntityList.get(EntityIndex1);
			Entity e2 = EntityList.get(EntityIndex2);
			double newX = (e1.X*e1.Mass + e2.X*e2.Mass)/(e1.Mass+e2.Mass);//weighted averages
			double newY = (e1.Y*e1.Mass + e2.Y*e2.Mass)/(e1.Mass+e2.Mass);
			double newXMomentum = e1.XMomentum + e2.XMomentum;//add together momentum, conserving it
			double newYMomentum = e1.YMomentum + e2.YMomentum;
			double newMass = e1.Mass + e2.Mass;
			Entity newEntity = new Entity(newX, newY, newXMomentum, newYMomentum, newMass, Entity.TYPE_GRAVITYAFFECTED);
			if(e1.edited || e2.edited)
			{
				newEntity.edited = true;
			}
			EntityList.add(newEntity);
			EntityList.remove(e1);
			EntityList.remove(e2);//remove these entities
		}	
	}

	//initializes the game
	void initialize()
	{
		while(true)
		{
			try
			{
				functions();
			}
			catch(InterruptedException e)
			{

			}
		}
	}

	void functions() throws InterruptedException
	{
		repaint(10);
		moveEntity();
		gravity();
		Thread.sleep(speed);
	}




	void checkEdit()//lets you click to edit
	{
		if(EntityList.size() > 0)//if there are more than one objects
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				double entityRadius = 1+e.getRadius();//getting radius from the area, in this case the mass
				double mousedistance = Math.sqrt(Math.pow(mousex - e.X,2) + Math.pow(mousey - e.Y,2));
				if(mousedistance < entityRadius+2)//if it is within the radius
				{

					int editedID = getEdited();
					if(editedID > -1)//remove
					{
						EntityList.get(editedID).edited = false;
					}
					e.edited = true;
					if(mouseDown == true)
					{
						e.Type = Entity.TYPE_DRAGGABLE;
					}
				}
			}
		}

		int editedID = getEdited();
		//the following buttons edit the properties of the selected Entity
		if(IsButtonClicked(900, 90, 50, 12))//set mass to a positive value
		{
			Entity e = EntityList.get(editedID);
			double newMass = getResponse("Input Mass", e.Mass, false,true);

			double xVelocity = e.XMomentum/e.Mass;//get velocity of entity
			double yVelocity = e.YMomentum/e.Mass;
			e.Mass = newMass;
			e.XMomentum = e.Mass*xVelocity;
			e.YMomentum = e.Mass*yVelocity;
		}
		if(IsButtonClicked(900, 130, 50, 12))
		{
			Entity e = EntityList.get(editedID);
			e.X = getResponse("Input X location", e.X ,false,true);//set location (positive values only)
		}
		if(IsButtonClicked(900, 150, 50, 12))
		{
			Entity e = EntityList.get(editedID);
			e.Y = getResponse("Input Y location", e.Y ,false,true);
		}
		if(IsButtonClicked(900, 190, 50, 12))
		{	
			Entity e = EntityList.get(editedID);
			e.XMomentum = e.Mass*getResponse("Input X momentum", e.XMomentum ,false,false);//sets the x momentum to the mass of the object * the user input velocity
		}
		if(IsButtonClicked(900, 210, 50, 12))
		{
			Entity e = EntityList.get(editedID);
			e.YMomentum = e.Mass*getResponse("Input Y momentum", e.YMomentum ,false,false);
		}
	}


	void moveEntity()
	{
		if(EntityList.size() > 0)
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				if(e.Type == Entity.TYPE_DRAGGABLE)
				{
					if(mouseDown)
					{
						e.X = mousex;
						e.Y = mousey;
					}
					else
					{
						e.Type = Entity.TYPE_GRAVITYAFFECTED;
					}
				}

				if(e.X < 0 || e.X > 700 || e.Y < 0 || e.Y > 700)
				{
					if(e.Type == Entity.TYPE_GRAVITYAFFECTED)
					{
						EntityList.remove(i);
					}
				}			
			}
		}

		if(gamepaused == false)
		{
			if(EntityList.size() > 0)
			{
				for(int i = 0; i < EntityList.size(); i++)
				{
					Entity e = EntityList.get(i);
					e.X += e.XMomentum/e.Mass;
					e.Y += e.YMomentum/e.Mass;//move each entity
				}
			}
		}
	}


	void gravity()
	{
		if(EntityList.size() > 0 && gamepaused == false)
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				if(EntityList.get(i).Type==Entity.TYPE_GRAVITYAFFECTED)
				{
					Entity e1 = EntityList.get(i);
					for(int a = 0; a < EntityList.size(); a++)
					{
						Entity e2 = EntityList.get(a); 
						if(i != a && e2.Type == Entity.TYPE_GRAVITYAFFECTED)
						{
							double distance = Math.sqrt(Math.pow(e1.X-e2.X,2)+ Math.pow(e1.Y-e2.Y,2));
							if(distance > 1 + 0.7*(e1.getRadius()+e2.getRadius()))//this block changes the momentum of e2 
							{
								//this algorithm calculates gravity and modifies e2 velocity 
								//finds the angle from e2 to e1
								double E2toE1Angle = Math.atan2(e1.Y-e2.Y,e1.X-e2.X);
								//finds the magnitude of the force
								double force = 1*(e1.Mass*e2.Mass)*Math.pow(distance,-2);
								//apply force to e2
								e2.XMomentum += Math.cos(E2toE1Angle)*force;
								e2.YMomentum += Math.sin(E2toE1Angle)*force;
							}
							else//otherwise, if the objects are too close
							{
								//collide entities
								collideEntities(i,a);
							}
						}
					}
				}
			}
		}
	}



	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		setBackground(Color.gray);
		g2d.setPaint(Color.black);

		g2d.fillRect(0, 0, 700, 700);
		g2d.setPaint(Color.white);
		g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
		g2d.fillRect(710, 20, 80, 20);
		g2d.fillRect(800, 20, 80, 20);
		g2d.fillRect(890, 20, 80, 20);
		g2d.fillRect(710, 260, 260, 20);
		g2d.setPaint(Color.black);
		g2d.drawString("TRACING", 893, 35);
		g2d.drawString("ADD_OBJECT", 803, 35);
		g2d.drawString("CHANGE_APPLICATION_SPEED", 713, 275);
		if(gamepaused == true)
		{
			g2d.drawString("PLAY", 713, 35);
		}
		else
		{
			g2d.drawString("PAUSE", 720, 35);
		}

		//draw entities
		if(EntityList.size() > 0)
		{
			g2d.setPaint(Color.white);
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				int eRadius = 1+(int)e.getRadius();
				g2d.fillOval((int)(e.X-eRadius), (int)(e.Y-eRadius), eRadius*2, eRadius*2);
			}
		}

		//draw the tracing points
		if(tracerOn)
		{
			for(int i = 0; i < TracerX.size(); i++)
			{
				if(TracerX.get(i) != null && TracerY.get(i) != null)
				{
					g2d.fillOval(TracerX.get(i).intValue(),TracerY.get(i).intValue(),1,1);
				}
			}
		}

		//add new dots and remove old ones
		if(TracerX.size() > MAX_TRACER_SIZE)
		{
			TracerX.remove(TracerX.size() - MAX_TRACER_SIZE);
			TracerY.remove(TracerY.size() - MAX_TRACER_SIZE);
		}
		for(int i = 0; i < EntityList.size(); i++)
		{
			Entity e = EntityList.get(i);
			if(e.Type == Entity.TYPE_GRAVITYAFFECTED && tracerOn == true)
			{
				TracerX.add(e.X);
				TracerY.add(e.Y);
			}	
		}

		//draw the edited id
		int editedID = getEdited();
		if(editedID > -1)
		{
			Entity e = EntityList.get(editedID);
			int bigradius = (int)(5+Math.sqrt(e.Mass));
			g2d.drawOval((int)(e.X-bigradius), (int)(e.Y-bigradius), bigradius*2, bigradius*2);
			g2d.drawString("STATS:", 710, 60);//draw stats

			g2d.drawString("MASS: " + (float)e.Mass, 710, 100);

			g2d.drawString("X_POSITION: " +(float)e.X, 710, 140);
			g2d.drawString("Y_POSITION: " +(float)e.Y, 710, 160);

			g2d.drawString("X_VELOCITY: " +(float)(e.XMomentum/e.Mass), 710, 200);
			g2d.drawString("Y_VELOCITY: " +(float)(e.YMomentum/e.Mass), 710, 220);

			g2d.fillRect(900, 90, 50, 12);//draw edit buttons

			g2d.fillRect(900, 130, 50, 12);
			g2d.fillRect(900, 150, 50, 12);

			g2d.fillRect(900, 190, 50, 12);
			g2d.fillRect(900, 210, 50, 12);

			g2d.setPaint(Color.black);
			g2d.drawString("EDIT", 900, 100);

			g2d.drawString("EDIT", 900, 140);
			g2d.drawString("EDIT", 900, 160);

			g2d.drawString("EDIT", 900, 200);
			g2d.drawString("EDIT", 900, 220);
			g2d.setPaint(Color.white);
		}		
	}	

	public static void main(String[] args) throws InterruptedException 
	{
		JFrame frame = new JFrame("Gravity Simulator");
		GravitySimulator game = new GravitySimulator();
		frame.add(game);
		frame.setSize(1000, 700);                                        //creating window
		frame.setVisible(true);
		game.initialize();
	}
}

class Entity
{
	public static final int TYPE_GRAVITYAFFECTED = 0;
	public static final int TYPE_DRAGGABLE = 1;
	public double X;
	public double Y;
	public double YMomentum = 0;
	public double XMomentum = 0;
	public double Mass;
	public int Type;

	public boolean edited = false;
	public boolean anchored = false;
	public Entity(double x, double y, double mass, int type)
	{
		X = x;
		Y = y;
		Mass = mass;
		Type = type;
	}

	public Entity(double x, double y, double xmomentum, double ymomentum, double mass, int type)
	{
		X = x;
		Y = y;
		XMomentum = xmomentum;
		YMomentum = ymomentum;
		Mass = mass;
		Type = type;
	}

	public double getRadius()
	{
		return Math.sqrt(Mass/Math.PI);
	}
}
