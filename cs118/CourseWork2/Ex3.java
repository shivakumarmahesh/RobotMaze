/*
 * The file Ex3.java is a part of the second course-work required to be submitted for evaluation
 * for the CS118 module. 
 * 
 * This code is free software; you can modify it if you are given access, but you may not redistribute it.
 * 
 * Please contact Shivakumar.Mahesh@warwick.ac.uk if you need additional information or have any
 * questions. The author of this file is Shivakumar Mahesh.
 */

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.Random;

/**
 * This is the principal class in this file. It contains methods that interact with the Maze Environment.
 * 
 * @author Shivakumar Mahesh
 */
public class Ex3 {
	
	/**
	 * This variable is used to set whether the Robot is in Explorer mode or BackTrack mode.
	 * If explorerMode equals 1 then it is in Explore mode, If explorerMode equals 0 then it is
	 * in BackTrack mode.
	 */
	private int explorerMode = 1; 
	
	/**
	 * This variable is used to count the number of moves the Robot has made.
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
		isFirstJunctionEncountered = false;
		explorerMode = 1;
		robotData = new RobotData();
		
	}
	
	/**
	 * <p>This is the principal method in this source code. It is used to make the Robot face 
	 * a direction at the end of every move. It implements this behaviour by calling exploreControl()
	 * whenever the Robot is exploring the Maze, and backTrackControl() whenever the Robot is 
	 * backtracking. This behaviour allows the Robot to use Tremaux's Algorithm.   
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
	private void exploreControl(IRobot robot) {
		
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
	    	    isFirstJunctionEncountered = true;
	    	
	    	    if(robotData.isJunctionVisitedBefore(robot)) {
	    	    	direction = IRobot.BEHIND;
	    	    	explorerMode = 0;
	            }
	            	
	            else {
	            	robotData.recordJunction(robot);
	            	robotData.printJunction();
	            }
	            	
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
	private void backtrackControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
			
		switch(exits) {
	    	case 2: 
	    		direction = corridor(robot);
	            break;
	    	case 3: 
	    	case 4: 
	    		direction = junction(robot);
	    	        
	           	if(passageExits(robot) > 0) {
	           		explorerMode = 1;
	        	    
	           	}
	           	//If there are no passage Exits:
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
	private int nonwallExits(IRobot robot) {
			
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
	private int passageExits(IRobot robot) {
		
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
	private int beenbeforeExits(IRobot robot) {
		
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
	private static int randomElementofArray(int[] arr, int cutOffIndex) {
		
		Random rand = new Random();
		return arr[rand.nextInt(cutOffIndex)];
		
	}
	
	/**
	 * <p>This method reverses an Absolute Heading.<br />
	 * For Example:<br />
	 * When passed IRobot.NORTH the method returns IRobot.SOUTH. 
	 * </p>
	 * 
	 * @param heading the heading which the method reverses. 
	 * @return the reversed heading.
	 */
	private static int reverseHeading(int heading) {
		
		return (((heading - IRobot.NORTH) + 2) % 4) + IRobot.NORTH;
		
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
	 * The number of Junctions the Robot has encountered. Since every Junction that is encountered is stored in this
	 * implementation, it is also the number of Junctions stored in junctionArray.
	 */
	private static int junctionCounter;
	
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
	 * This array is used to store all the information pertaining to particular junctions.
	 */
	private Junction junctionArray[] = new Junction[maxJunctions];
	
	/**
	 * <p>Constructor of RobotData class. Sets junctionCounter to 0 and initialises the junctionArray with Junction objects.
	 * </p>
	 */
	public RobotData() {
		
		junctionCounter = 0;
		
		for(int i = 0; i < maxJunctions; i++) {
			junctionArray[i] = new Junction();
		}
		
    }
	
	/**
	 * <p>This method resets the value of junctionCounter to 0.
	 * </p>
	 */
	public void resetJunctionCounter() {
		
		junctionCounter = 0;
		
	}
	
	/**
	 * <p>This method records Junction Information of each Junction.
	 * </p>
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
    public void recordJunction(IRobot robot) {	
    	
		junctionArray[junctionCounter].juncX = robot.getLocation().x;
		junctionArray[junctionCounter].juncY = robot.getLocation().y;
		junctionArray[junctionCounter].arrived = robot.getHeading();
		
		junctionCounter++;
		
	}
    
    /**
     * <p>This method prints Junction Information of each Junction.
     * Example output:<br />
     * Junction 13 (x=7,y=4) heading EAST 
     * </p>
     */
    public void printJunction() {
    	
    	System.out.print("Junction " + (junctionCounter));
    	System.out.print(" (x=" + junctionArray[junctionCounter-1].juncX);
    	System.out.print(",y=" + junctionArray[junctionCounter-1].juncY);
    	System.out.print(") heading ");
    	
    	switch(junctionArray[junctionCounter-1].arrived) {
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
	 * <p>This method is used to find the heading the Robot arrived with the first time it arrived at
	 * a particular Junction. This method is called only when the Robot is at a Junction.
	 * </p>
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return the heading the Robot arrived with at that particular Junction.
	 */
    public int searchJunction(IRobot robot) {
    	
    	int indexofJunction = findJunctionIndex(robot);
    	return junctionArray[indexofJunction].arrived;

    }
    
    /**
     * <p>This method searches through junctionArray for the Junction with (x,y) coordinates
     * that equal the Robot's (x,y) coordinates in order to find the Index of that Junction. 
     * This method is called only when the Robot is at a Junction.
     * </p>
     * @param robot This is the object from the IRobot Class that is used throughout this source code.
     * @return the index of that particular Junction if present in the array and -1 otherwise.
     */
	private int findJunctionIndex(IRobot robot) {
		
		for(int i = 0; i < junctionCounter; i++) {
			if((robot.getLocation().x == junctionArray[i].juncX) && (robot.getLocation().y == junctionArray[i].juncY)) {
				return i;
			}
		}
		
		return -1;
		
	}
	
	/**
	 * <p>This method is used to determine if the Robot has encountered a particular Junction before.
	 * </p>
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @return true if the Junction has been visited before, and false if it hasn't been visited before.
	 */
	public boolean isJunctionVisitedBefore(IRobot robot) {
		
		if(findJunctionIndex(robot) == -1) {
			return false;
		}
		
		else {
			return true;
		}
		
	}
   
}


