/*
 * The file GrandFinale.java is a part of the second course-work required to be submitted for evaluation
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
public class GrandFinale {
	
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
	 * This variable is used as an index for the junctionStack in all Runs after the first Run
	 * in a given maze.
	 */
	private int learnedIndex = 0;
	
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
		
		explorerMode = 1;
		isFirstJunctionEncountered = false;
		learnedIndex = 0;
		pollRun = 0;
		
	}
	
	/**
	 * <p>This is the principal method in this source code. It is used to make the Robot face 
	 * a direction at the end of every move. It implements this behaviour by calling exploreControl()
	 * whenever the Robot is exploring the Maze, and backTrackControl() whenever the Robot is 
	 * backtracking. This behaviour allows the Robot to use Tremaux's Algorithm.   
	 * </p>
	 * 
	 * <p>From the second run onwards, control switches over to learnedControl, which allows
	 * the Robot to traverse the maze and reach the target with the optimal solution.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	public void controlRobot(IRobot robot) {
		
		//On the first move of the first run of a new maze:
		if((robot.getRuns() == 0) && (pollRun == 0)) {
			robotData = new RobotData();
		}
		
		if(robot.getRuns() > 0) {
			learnedControl(robot);
		}
		
		else if(explorerMode == 1) {
			exploreControl(robot);
		}
		
		else {
			backtrackControl(robot);
		}
		
		pollRun++;
		
	}
	
	/**
	 * <p>This method sets the direction of the Robot when it is in Explore Mode on the First Run.
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
	    			
	    		/* If the Robot starts the Maze at a Corridor then record that Corridor as a Junction
	    		 * at the Start of the Maze.
	   			 */
	   	 		if(pollRun == 0) {
	   				unexploredJunctionProcedure(robot, direction);
	   			}
	    			
	    		break;
	    	case 3: 
	    	case 4: 
	    		direction = junction(robot);
	    			
	           	if(robotData.isJunctionVisitedBefore(robot)) {
	           		direction = IRobot.BEHIND;
	           		explorerMode = 0;
	           	}
	            	
	           	else {
	           		unexploredJunctionProcedure(robot, direction);
	           	}
	            	
	            isFirstJunctionEncountered = true;
	            	
	            break;
	    }
		
		robot.face(direction);
		
	}
	
	/**
	 * <p>This method sets the direction of the Robot when it is in BackTrack Mode on the First Run.
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	private void backtrackControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
			
		switch(exits) {
	    	case 1: 
	    		direction = deadEnd(robot);
	    		explorerMode = 0;
	    		break;
	    	case 2: 
	    		direction = corridor(robot);
	    		
	    		if(robotData.isJunctionVisitedBefore(robot)) {
	    			robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
	    		}
	    		
	           	break;
	    	case 3: 
	    	case 4: 
	    		direction = junction(robot);
	    	     
	    		//If there are passage Exits:
	        
	    		if(passageExits(robot) > 0) {
	            
	    			explorerMode = 1;
	    			robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
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
	 * <p>This method sets the direction of the Robot from the second run onwards. It utilises the
	 * junctionStack and the departed headings in it to traverse the Maze and reach the target 
	 * with the optimal solution.
	 * </p>
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 */
	private void learnedControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
		
		switch(exits) {
	    	case 1: 
	    		direction = deadEnd(robot);
	           	break;
	    	case 2:
	    		direction = corridor(robot);
	    		
	    		if(pollRun == 0) {
	    			
	    			robot.setHeading(robotData.getDeparted(learnedIndex));
		   	        learnedIndex++;
		   	        return;
	    		}
	    		
	   			break;
	    	case 3: 
	    	case 4: 
	    		robot.setHeading(robotData.getDeparted(learnedIndex));
	    	    learnedIndex++;
	    	    return;
	            	
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
	 * <p>This method Records and Prints Junction information. It also updates the departed heading of
	 * that Junction. It is used only when the Robot is at a previously unencountered Junction. 
	 * </p>
	 * 
	 * @param robot This is the object from the IRobot Class that is used throughout this source code.
	 * @param direction The relative direction the Robot takes to exit a Junction.
	 */
	private void unexploredJunctionProcedure(IRobot robot, int direction) {
		
		robotData.recordJunction(robot);
		robotData.printJunction(robot);
		robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
		
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
	
	/**
	 * <p>This method determines the Absolute heading when given a direction relative to the Robot.<br />
	 * For Example:<br />
	 * When the Robot is facing NORTH and the relative direction to turn is LEFT, the method
	 * will return IRobot.WEST
	 * </p>
	 * @param robot
	 * @param relativeDirection A direction relative to the Robot such as IRobot.AHEAD or IRobot.LEFT.
	 * @return
	 */
	private static int relativeToAbsoluteHeading(IRobot robot, int relativeDirection) {
		
		return (((robot.getHeading() - IRobot.NORTH) + (relativeDirection - IRobot.AHEAD)) % 4) + IRobot.NORTH;  
		
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
	 * The number of Junctions stored in the junctionStack.
	 */
	private static int junctionIndex;
	
	/**
	 * The number of Junctions encountered by the Robot.
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
		
		/**
		 * The final heading the Robot leaves a junction with.
		 * This value may be updated a few times before it reaches it's final value.
		 */
		private int departed;
		
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
     * <p>This method prints Junction Information of each Junction.<br />
     * Example output:<br />
     * Junction 13 (x=7,y=4) heading EAST  
     * </p>
     */
    public void printJunction(IRobot robot) {
    	
    	System.out.print("Junction " + (junctionCounter));
    	System.out.print(" (x=" + robot.getLocation().x);
    	System.out.print(",y=" + robot.getLocation().y);
    	System.out.print(") heading ");
    	
    	switch(junctionStack[junctionIndex - 1].arrived) {
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
     * <p>This method is used to retrieve the arrived heading of the Junction at the top of the
     * junctionStack.
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
     *  encountered. This method also pops the Junction at the top of the junctionStack.
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
    
    /**
     * <p>This method searches through junctionArray for the Junction with (x,y) coordinates
     * that equal the Robot's (x,y) coordinates in order to find the Index of that Junction. 
     * This method is called only when the Robot is at a Junction.
     * </p>
     * @param robot This is the object from the IRobot Class that is used throughout this source code.
     * @return the index of that particular Junction if present in the array and -1 otherwise.
     */
    private int findJunctionIndex(IRobot robot) {
		
		for(int i = 0; i < junctionIndex; i++) {
			if((robot.getLocation().x == junctionStack[i].juncX) && (robot.getLocation().y == junctionStack[i].juncY)) {
				return i;
			}
		}
		
		return -1;
		
	}
    
    /**
     * <p>This method is used to update the departed heading of a particular Junction.
     * </p>
     * @param robot This is the object from the IRobot Class that is used throughout this source code.
     * @param heading the departed heading of the Junction is updated to this heading.
     */
    public void updateDeparted(IRobot robot, int heading) {
    	
    	junctionStack[findJunctionIndex(robot)].departed = heading ;
    	
    }
    
    /**
     * <p>This method is used to obtain the departed heading of a particular Junction.
     * </p>
     * @param indexOfJunction The index used to refer to a particular Junction in the JunctionStack.
     * @return the departed heading of a particular Junction.
     */
    public int getDeparted(int indexOfJunction) {
    	
    	return junctionStack[indexOfJunction].departed;
    	
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


