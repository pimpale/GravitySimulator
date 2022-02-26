package gravitysimulator;

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
	boolean paused = true;

	int mousex = 0;
	int mousey = 0;

	int speed = 100;
	final int vectormultiplier = 30;

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
			paused = !paused;
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

    if(IsButtonClicked(710, 290, 290,20)) {
      centerPosition();
      centerVelocity();
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
		if(IsButtonClicked(800, 20, 80, 20))
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
		paused = true;//pause game
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

	void removeEdited()
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			EntityList.get(i).edited = false;
		}
	}
	
	void removeVelEdited()
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			EntityList.get(i).veledited = false;
		}
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

	int getVelEdited()
	{
		for(int i = 0; i < EntityList.size(); i++)
		{
			if(EntityList.get(i).veledited)
			{
				return i;
			}
		}
		return -1;
	}


  void centerPosition() {
    double accX = 0;
    double accY = 0;
    double totalMass = 0;

    int entityCount = EntityList.size();
    for(int i = 0; i < entityCount; i++) {
      Entity e = EntityList.get(i);
      accX += e.x*e.mass;
      accY += e.y*e.mass;
      totalMass += e.mass;
    }

    double x = accX/totalMass;
    double y = accY/totalMass;

    // Now subtract this velocity from everyone
    for(int i = 0; i < entityCount; i++) {
      Entity e = EntityList.get(i);
      e.x = e.x - x + 350;
      e.y = e.y - y + 350;
    }
  }

  // Subtracts the average velocity from all objects TODO
  void centerVelocity() {

    double totalXMom = 0;
    double totalYMom = 0;

    double totalMass = 0;

    int entityCount = EntityList.size();
    for(int i = 0; i < entityCount; i++) {
      Entity e = EntityList.get(i);
      totalXMom += e.xMomentum;
      totalYMom += e.yMomentum;
      totalMass += e.mass;
    }

    double averageXVel = totalXMom / totalMass;
    double averageYVel = totalYMom / totalMass;

    // Now subtract this velocity from everyone
    for(int i = 0; i < entityCount; i++) {
      Entity e = EntityList.get(i);
      e.xMomentum -= averageXVel*e.mass;
      e.yMomentum -= averageYVel*e.mass;
    }
  }
	
	double getRadius(Entity e)
	{
		return Math.sqrt(e.mass/Math.PI);
	}
	
	void setVelocity(Entity e, double xVel, double yVel)
	{
		e.xMomentum = xVel*e.mass;
		e.yMomentum = xVel*e.mass;
	}
	
	void applyForce(Entity e, Force f)
	{
		e.xMomentum += f.magnitude*Math.cos(f.direction);
		e.yMomentum += f.magnitude*Math.sin(f.direction);
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
			double newx = (e1.x*e1.mass + e2.x*e2.mass)/(e1.mass+e2.mass);//weighted averages
			double newY = (e1.y*e1.mass + e2.y*e2.mass)/(e1.mass+e2.mass);
			double newxMomentum = e1.xMomentum + e2.xMomentum;//add together momentum, conserving it
			double newyMomentum = e1.yMomentum + e2.yMomentum;
			double newMass = e1.mass + e2.mass;
			Entity newEntity = new Entity(newx, newY, newxMomentum, newyMomentum, newMass, Entity.TYPE_GRAVITYAFFECTED);
			if(e1.edited || e2.edited)
			{
				newEntity.edited = true;
			}
			EntityList.add(newEntity);
			EntityList.remove(e1);
			EntityList.remove(e2);//remove these entities
		}	
	}

	double getDistance(Entity e1, Entity e2)
	{
		return Math.sqrt(Math.pow(e1.x-e2.x,2) + Math.pow(e1.y-e2.y,2));
	}
	
	/**
	 * gets the direction to "to" from "from" in radians
	 */
	double getDirection(Entity from, Entity to)
	{
		return Math.atan2(to.y-from.y,to.x-from.x);
	}
	
	/**
	 * gets the Force of gravity acting on entity "affected" from the entity "source" 
	 */
	Force getGravity(Entity source, Entity affected)
	{
		double dist = getDistance(source, affected);
		double dire = getDirection(affected, source);
		double magn = (source.mass*affected.mass)*Math.pow(dist,-2);
		return new Force(dire, magn);
	}
	
  Force getMomentum(Entity e) {
    double dir = Math.atan2(e.yMomentum, e.xMomentum);
    double mag = Math.hypot(e.xMomentum, e.yMomentum);
    return new Force(dir, mag);
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
		moveEntity();
		gravity();
		repaint();
		Thread.sleep(speed);
	}



	void checkEdit()//lets you click to edit
	{
		if(EntityList.size() > 0)//if there are more than one objects
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				double entityRadius = 1+getRadius(e);//getting radius from the area, in this case the mass
				double mousedistance = Math.sqrt(Math.pow(mousex - e.x,2) + Math.pow(mousey - e.y,2));
				if(mousedistance < entityRadius+2)//if it is within the radius
				{
					removeEdited();
					e.edited = true;
					if(mouseDown == true)
					{
						e.type = Entity.TYPE_DRAGGABLE;
					}
				}
				
			}
		}

		{
			Entity e = EntityList.get(getEdited());
			int vellocx = (int)(e.x + vectormultiplier*(e.xMomentum/e.mass)); 
			int vellocy = (int)(e.y + vectormultiplier*(e.yMomentum/e.mass));
			double velocitydistance = Math.sqrt(Math.pow(mousex - vellocx,2) + Math.pow(mousey - vellocy,2));
			if(velocitydistance < 10)
			{
				e.veledited = true;
			}
		}
	
		if(getEdited() != -1)
		{
			Entity e = EntityList.get(getEdited());
			//the following buttons edit the properties of the selected Entity
			if(IsButtonClicked(900, 90, 50, 12))//set mass to a positive value
			{
				double newMass = getResponse("Input Mass", e.mass, false,true);
				double xVelocity = e.xMomentum/e.mass;//get velocity of entity
				double yVelocity = e.yMomentum/e.mass;
				e.mass = newMass;
				e.xMomentum = e.mass*xVelocity;
				e.yMomentum = e.mass*yVelocity;
			}
			if(IsButtonClicked(900, 130, 50, 12))
			{
				e.x = getResponse("Input x location", e.x ,false,true);//set location (positive values only)
			}
			if(IsButtonClicked(900, 150, 50, 12))
			{
				e.y = getResponse("Input Y location", e.y ,false,true);
			}
			if(IsButtonClicked(900, 190, 50, 12))
			{	
				e.xMomentum = e.mass*getResponse("Input x momentum", e.xMomentum ,false,false);//sets the x momentum to the mass of the object * the user input velocity
			}
			if(IsButtonClicked(900, 210, 50, 12))
			{
				e.yMomentum = e.mass*getResponse("Input Y momentum", e.yMomentum ,false,false);
			}
		}
	}


	void moveEntity()
	{
		if(EntityList.size() > 0)
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				if(e.type == Entity.TYPE_DRAGGABLE)
				{
					if(mouseDown)
					{
						e.x = mousex;
						e.y = mousey;
					}
					else
					{
						e.type = Entity.TYPE_GRAVITYAFFECTED;
					}
				}
				
				if(e.edited && e.veledited)
				{
					if(mouseDown)
					{
						e.xMomentum = ((mousex - e.x)/vectormultiplier)*e.mass;
						e.yMomentum = ((mousey - e.y)/vectormultiplier)*e.mass;
					}
					else
					{
						e.veledited = false;
					}
				}
				
				if(e.x < 0 || e.x > 700 || e.y < 0 || e.y > 700)
				{
					if(e.type == Entity.TYPE_GRAVITYAFFECTED)
					{
						EntityList.remove(i);
					}
				}			
			}
		}

		if(paused == false)
		{
			if(EntityList.size() > 0)
			{
				for(int i = 0; i < EntityList.size(); i++)
				{
					Entity e = EntityList.get(i);
					if(e.type == Entity.TYPE_GRAVITYAFFECTED && !e.anchored)
					{
						e.x += e.xMomentum/e.mass;
						e.y += e.yMomentum/e.mass;//move each entity
					}
				}
			}
		}
	}
	

	void gravity()
	{
		if(EntityList.size() > 0 && paused == false)
		{
			for(int i = 0; i < EntityList.size(); i++)
			{
				if(EntityList.get(i).type==Entity.TYPE_GRAVITYAFFECTED)
				{
					Entity e1 = EntityList.get(i);
					for(int a = 0; a < EntityList.size(); a++)
					{
						Entity e2 = EntityList.get(a); 
						if(i != a && e2.type == Entity.TYPE_GRAVITYAFFECTED && !e2.anchored)
						{
							double distance = Math.sqrt(Math.pow(e1.x-e2.x,2)+ Math.pow(e1.y-e2.y,2));
							if(distance > 1 + 0.7*(getRadius(e1)+getRadius(e2)))//this block changes the momentum of e2 
							{
								Force f = getGravity(e1, e2);
								applyForce(e2, f);
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
		g2d.setFont(new Font("Courier", Font.BOLD, 12));
		g2d.fillRect(710, 20, 80, 20);
		g2d.fillRect(800, 20, 80, 20);
		g2d.fillRect(890, 20, 80, 20);
		g2d.fillRect(710, 260, 260, 20);
		g2d.fillRect(710, 290, 260, 20);
		g2d.setPaint(Color.black);
		g2d.drawString("TRACING", 893, 35);
		g2d.drawString("ADD_OBJECT", 803, 35);
		g2d.drawString("CHANGE_APPLICATION_SPEED", 713, 275);
		g2d.drawString("CENTER_AND_TRACK", 713, 305);
		if(paused == true)
		{
			g2d.drawString("PLAY", 713, 35);
		}
		else
		{
			g2d.drawString("PAUSE", 720, 35);
		}

		//draw the tracing points
		if(tracerOn)
		{
			g2d.setPaint(new Color(255,0,0,50));
			for(int i = 0; i < TracerX.size(); i++)
			{
				if(TracerX.get(i) != null && TracerY.get(i) != null)
				{
					g2d.fillOval(TracerX.get(i).intValue(),TracerY.get(i).intValue(),3,3);
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
			if(!paused && e.type == Entity.TYPE_GRAVITYAFFECTED && tracerOn == true)
			{
				TracerX.add(e.x);
				TracerY.add(e.y);
			}	
		}
		
		
		//draw entities
		if(EntityList.size() > 0)
		{
			g2d.setPaint(Color.white);
			for(int i = 0; i < EntityList.size(); i++)
			{
				Entity e = EntityList.get(i);
				int eRadius = 1+(int)getRadius(e);
				g2d.fillOval((int)(e.x-eRadius), (int)(e.y-eRadius), eRadius*2, eRadius*2);
			}
		}

		//draw the edited id
		int editedID = getEdited();
		if(editedID > -1)
		{
			Entity e = EntityList.get(editedID);
			int bigradius = (int)(5+Math.sqrt(e.mass));
			g2d.setPaint(Color.WHITE);
			g2d.drawOval((int)(e.x-bigradius), (int)(e.y-bigradius), bigradius*2, bigradius*2);
			
			int vellocx = (int)(e.x + vectormultiplier*(e.xMomentum/e.mass)); 
			int vellocy = (int)(e.y + vectormultiplier*(e.yMomentum/e.mass));
			g2d.setPaint(Color.CYAN);
			g2d.drawLine((int)e.x, (int)e.y, vellocx, vellocy);
			g2d.drawOval(vellocx - 5, vellocy - 5, 10, 10);
			g2d.setPaint(Color.WHITE);
			g2d.drawString("V", vellocx -2, vellocy +2);
			
			g2d.drawString("STATS:", 710, 60);//draw stats
			g2d.drawString("MASS: " + (float)e.mass, 710, 100);
			g2d.drawString("X_POSITION: " +(float)e.x, 710, 140);
			g2d.drawString("Y_POSITION: " +(float)e.y, 710, 160);
			g2d.drawString("X_VELOCITY: " +(float)(e.xMomentum/e.mass), 710, 200);
			g2d.drawString("Y_VELOCITY: " +(float)(e.yMomentum/e.mass), 710, 220);

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
		}		
	}	

	public static void main(String[] args) throws InterruptedException 
	{
		JFrame frame = new JFrame("Gravity Simulator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	public double x;
	public double y;
	public double yMomentum = 0;
	public double xMomentum = 0;
	public double mass;
	public int type;

	public boolean edited = false;
	public boolean veledited = false;
	public boolean anchored = false;
	public Entity(double x, double y, double mass, int type)
	{
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.type = type;
	}

	public Entity(double x, double y, double xmomentum, double ymomentum, double mass, int type)
	{
		this.x = x;
		this.y = y;
		this.xMomentum = xmomentum;
		this.yMomentum = ymomentum;
		this.mass = mass;
		this.type = type;
	}

}

class Force 
{
	public double direction;
	public double magnitude;
	
	public Force(double direction, double magnitude)
	{
		this.direction = direction;
		this.magnitude = magnitude;
	}
}
