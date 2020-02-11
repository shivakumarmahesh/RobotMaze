/*
 * The file Ex2.java is a part of the second course-work required to be submitted for evaluation
 * of the CS118 module. 
 * 
 * This code is free software; you can modify it but you cannot redistribute it to First Year students
 * from the University of Warwick.
 * 
 * Please contact Shivakumar.Mahesh@warwick.ac.uk if you need additional information or have any
 * questions. The author of this file is Shivakumar Mahesh.
 */

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.Random;

/**
 * This is the principal class in this file which contains methods that communicate with the Maze Environment.
 * 
 * @author Shivakumar Mahesh
 */
public class Ex2 {
	
	/**
	 * This variable is used to set whether the Robot is in Explorer mode or BackTrack mode.
	 * If exploreMode equals 1 then it is in Explore mode, If exploreMode equals 0 then it is
	 * in BackTrack mode.
	 */
	private int explorerMode = 1; 
	
	/**
	 * This variable is used to count the number of moves the robot has made.
	 */
	private int pollRun = 0;
	
	/**
	 * This variable is used to determine if the first Junction has been encountered.
	 */
	private boolean isFirstJunctionEncountered = false; 
	
	/**
	 * This object is used to record information of each Junction that the Robot encounters.
	 */
	private RobotData robotData;
	
	/**
	 * <p>This method executes each time the reset button is clicked in the Maze Application.
	 * It resets certain variables to their initial values when the maze is reset. 
	 * </p>
	 */
	public void reset() {
		robotData.resetJunctionCounter();
		robotData = new RobotData();
		isFirstJunctionEncountered = false;
		explorerMode = 1;
		
	}
	
	/**
	 * <p>This is the principal method in this source code. It is used to make the Robot face 
	 * a direction at the end of every move. It implements this behaviour by calling exploreControl()
	 * whenever the Robot is exploring the Maze, and backTrackControl() whenever the Robot is 
	 * backtracking. This behaviour allows the Robot to use depth first search.  
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	public void controlRobot(IRobot robot) {
		
		//On the first move of the first run of a new maze:
		if((robot.getRuns() == 0) && (pollRun == 0)) {
			robotData = new RobotData();
		}
		
		pollRun++;
		
		if(explorerMode == 1) {
			exploreControl(robot);
		}
		
		else {
			backtrackControl(robot);
		}
		
		
	}
	
	/**
	 * <p>This method sets the direction of the Robot when it is in Explore Mode.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	public void exploreControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
		
		switch(exits) {
	    	case 1: 
	    		direction = deadEnd(robot);
	    		
	    		if(isFirstJunctionEncountered == true) {
	    			explorerMode = 0;
	    		}
	    			
	            break;
	    	case 2: 
	    		direction = corridor(robot);
	    		break;
	    	case 3: 
	    	case 4:
	    		direction = junction(robot);
	    		//If the junction is New:
	            if(beenbeforeExits(robot) <= 1) {
	            	robotData.recordJunction(robot);
	           		robotData.printJunction(robot);
	           	}
	           	
	           	if(passageExits(robot) == 0) {
	           		explorerMode = 0;
	           	}
	            	
	           	isFirstJunctionEncountered = true;
	            	
	           	break;
	    }
		
		robot.face(direction);
		
	}
	
	/**
	 * <p>This method sets the direction of the Robot when it is in BackTrack Mode.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	public void backtrackControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
			
		switch(exits) {
	    	case 1: 
	    		direction = deadEnd(robot);
	    		explorerMode = 0;
	    		break;
	    	case 2: 
	    		direction = corridor(robot);
	            break;
	    	case 3: 
	    	case 4:
	    	        
	    		if(beenbeforeExits(robot) <= 1) {
        			robotData.recordJunction(robot);
        			robotData.printJunction(robot);
        		}
	    		//If there are passage Exits:
	            if(passageExits(robot) > 0) {
	            	explorerMode = 1;
	           		direction = randomPassageDirection(robot);
	        	    
	           	}
	            //If there are no passage exit:
	            else {
	           		robot.setHeading(reverseHeading(robotData.searchJunction(robot)));
	           		return; 
	           	}
	           	
	           	break;
	            	
	    }
		
		robot.face(direction);
		
	}
	
	/**
	 * <p>This method is used to determine the number of non-wall exits that surround the Robot.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the number of non-wall exits that surround the Robot.
	 */
	public int nonwallExits(IRobot robot) {
			
		int numOfNonWallExits = 0;
			
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) != IRobot.WALL) {
				numOfNonWallExits++;
			}
			
		}
		
		return numOfNonWallExits;
	}
	
	/**
	 * <p>This method is used to determine the number of passage exits that surround the Robot.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the number of passage exits that surround the Robot.
	 */
	public int passageExits(IRobot robot) {
		
		int numOfPassageExits = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) == IRobot.PASSAGE) {
				numOfPassageExits++;
			}
			
		}
		return numOfPassageExits;
		
	}
	
	/**
	 * <p>This method is used to determine the number of exits the Robot has been before.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the number of exits that the Robot has traversed before.
	 */
	public int beenbeforeExits(IRobot robot) {
		
		int numOfBeenBeforeExits = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) == IRobot.BEENBEFORE) {
				numOfBeenBeforeExits++;
			}
			
		}
		return numOfBeenBeforeExits;
		
	}
	
	/**
	 * <p>This method is used to determine the direction the Robot should take at a Dead-End.
	 * It determines the only non-Wall direction at the dead end.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the direction the Robot should take at a Dead-End.
	 */
	private int deadEnd(IRobot robot) {
		
		int direction = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) != IRobot.WALL) {
				direction = i;
				break;
			}
			
		}
		
		return direction;
	}
	
	/**
	 * <p>This method is used to determine the direction the Robot should take at a Corridor.
	 * It calculates a direction that ensures the Robot doesn't collide into walls and doesn't 
	 * turn backwards.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the direction the Robot should take at a Corridor.
	 */
	private int corridor(IRobot robot) {
		
		int direction = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if((robot.look(i) != IRobot.WALL) && (i!=IRobot.BEHIND)) {
				direction = i;
			}
			
		}
		
		return direction;
		
	}
	
	/**
	 *<p>This method is used to determine the direction the Robot should take at a Junction.
	 * It calculates a direction that ensures the Robot doesn't collide into walls and takes 
	 * new routes at a Junction if possible.
	 *</p> 
	 *
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the direction the Robot should take at a Junction.
	 */
	private int junction(IRobot robot) {
		
		if(passageExits(robot) > 0) {
			return randomPassageDirection(robot);
		}
		
		else {
			return randomNonWallDirection(robot);
		}
	}
	
	/**
	 * <p>This method is used to select a Relative Direction that leads the Robot into a square
	 * it has not been to before.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return a random relative Passage Direction.
	 */
	private int randomPassageDirection(IRobot robot) {
		
		
		int[] passageArray = {-1, -1, -1};
		int passageArrayIndex = 0;
		
        for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) == IRobot.PASSAGE && i!=IRobot.BEHIND) {
				passageArray[passageArrayIndex] = i;
				passageArrayIndex++;
			}
			
		}  
        
        return randomElementofArray(passageArray, passageArrayIndex);
		
	}
	
	/**
	 * <p>This method is used to select a Relative Direction that leads the Robot into a square
	 * that is not a wall.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return a random relative Non-Wall Direction.
	 */
	private int randomNonWallDirection(IRobot robot) {
		
		int[] wallessArray = {-1, -1, -1};
		int wallessArrayIndex = 0;
		
        for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) != IRobot.WALL && i != IRobot.BEHIND) {
				wallessArray[wallessArrayIndex] = i;
				wallessArrayIndex++;
			}
			
		}
        
		return randomElementofArray(wallessArray, wallessArrayIndex);
		
	}
	
	/**
	 * <p>This method is used to select a random Element from an Array whose index in the Array
	 * is less that cutOffIndex.
	 * </p>
	 * 
	 * @param arr Generic Array.
	 * @param cutOffIndex the index at and after which elements in the Array are not considered.
	 * @return a random element from the array whose index is less that cutOffIndex.
	 */
	public static int randomElementofArray(int[] arr, int cutOffIndex) {
		
		Random rand = new Random();
		return arr[rand.nextInt(cutOffIndex)];
		
	}
	
	/**
	 * <p>This method reverses an Absolute Heading.
	 * </p>
	 * 
	 * @param heading the heading which the method reverses. 
	 * @return the reversed heading.
	 */
	public static int reverseHeading(int heading) {
		
		if(heading == IRobot.NORTH) {
			return IRobot.SOUTH;
		}
		
		else if(heading == IRobot.EAST) {
			return IRobot.WEST;
		}
		
		else if(heading == IRobot.SOUTH) {
			return IRobot.NORTH;
		}
		
		else {
			return IRobot.EAST;
		}

	}
	
	
}

/**
 * This class consists exclusively of variables and methods that operate on or return
 * junction information.
 * 
 * @author Shivakumar Mahesh
 */
class RobotData {
	
	/**
	 * The maximum number of Junctions likely to occur.
	 */
	private static int maxJunctions = 10000; 
	
	/**
	 * The number of Junctions encountered by the Robot.
	 */
	private static int junctionCounter;
	
	/**
	 * The number of Junctions in the junctionStack.
	 */
	private static int junctionIndex;
	
	/**
	 * This class consists of Junction information pertaining to each particular junction.
	 * @author Shivakumar Mahesh
	 */
	class Junction{
		
		/**
		 * The X-coordinate of a Junction.
		 */
		private int juncX;    
		
		/**
		 * The Y-coordinate of a Junction.
		 */
		private int juncY;    
		
		/**
		 * The heading the Robot arrived with at a Junction.
		 */
		private int arrived; 
		
	}
	
	/**
	 * <p>This array is used as a Stack to store all the information pertaining to particular Junctions.
	 * Junctions are pushed onto the stack as they are encountered. Junctions are popped from the 
	 * stack whenever the Robot backtracks away from a Junction.
	 * </p>
	 * 
	 * <p>Pushing is implemented using recordJunction() method and Popping is implemented using
	 * popJunction() method. A Peek functionality is also implemented using peekAtJunctionStack() 
	 * method.
	 * </p>
	 */
	private Junction junctionStack[] = new Junction[maxJunctions];
	
	/**
	 * <p>Constructor of RobotData class. Sets junctionCounter and junctionIndex to 0 and initialises
	 * the junctionStack with Junction objects.
	 * </p>
	 */	
	public RobotData() {
		
		junctionCounter = 0;
		junctionIndex = 0;
		
		for(int i = 0; i < maxJunctions; i++) {
			junctionStack[i] = new Junction();
		}
		
    }
	
	/**
	 * <p>This method resets the values of junctionCounter and junctionIndex to 0.
	 * </p>
	 */
	public void resetJunctionCounter() {
		junctionIndex = 0;
		junctionCounter = 0;
	}
	
	/**
	 * <p>This method records Junction Information of each Junction.
	 * Example output for a particular Junction: <br />
     * Junction 13 (x=15,y=4) heading EAST
	 * </p>
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
    public void recordJunction(IRobot robot) {	
		junctionStack[junctionIndex].arrived = robot.getHeading();
		junctionStack[junctionIndex].juncX = robot.getLocation().x;
		junctionStack[junctionIndex].juncY = robot.getLocation().y;
		
		junctionIndex++;
		junctionCounter++;
		
	}
    
    /**
     * <p>This method prints Junction Information of each Junction.
     * </p>
     */
    public void printJunction(IRobot robot) {
    	
    	System.out.print("Junction " + (junctionCounter));
    	System.out.print(" (x=" + robot.getLocation().x);
    	System.out.print(",y=" + robot.getLocation().y);
    	System.out.print(") heading ");
    	
    	switch(junctionStack[junctionIndex-1].arrived) {
    	
	    	case (IRobot.NORTH): 
	    		System.out.println("NORTH");
	    		break;
	    	case (IRobot.EAST):
	    		System.out.println("EAST");
	    		break;
	    	case (IRobot.SOUTH): 
	    		System.out.println("SOUTH");
	    		break;
	    	case (IRobot.WEST): 
	    		System.out.println("WEST");
	    		break;
    	}
    }
    
    /**
     * <p>This method is used to retrieve the heading the Robot arrived with at the most recent
     * Junction the Robot encountered.
     * </p>
     * @return the arrived Heading of the junction at the top of the junctionStack.
     */
    private int peekAtJunctionStack() {
    	
    	return junctionStack[junctionIndex - 1].arrived;
    	
    }
    
    /**
     * <p>This method is used to pop the Junction at the top of the junctionStack.
     * </p>
     */
    private void popJunction() {
    	
    	junctionIndex--;
    }
    
    /**
     * <p>This method is used return the heading the Robot arrived with at the most recent Junction the Robot
     *  encountered, after the heading is returned the most recent Junction is popped of the 
     *  JunctionStack.
     * </p>
     * @param robot This is the object from the IRobot Class that is used throughout this source code.
     * @return heading the Robot arrived with at the most recent Junction the Robot encountered.
     */
    public int searchJunction(IRobot robot) {
    	
    	int junctionHeading = 0; // value to return
    	
    	junctionHeading = peekAtJunctionStack();
        popJunction(); 
    	
    	return junctionHeading;
    	
    }
   
}


