import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.Random;


public class GrandFinale {
	
	private int explorerMode = 1; // 1 = explore, 0 = backtrack
	private int pollRun = 0;
	private int learnedIndex = 0;
	private boolean isFirstJunctionEncountered = false; 
	private RobotData robotData;
	
	public void reset() {
		
		explorerMode = 1;
		isFirstJunctionEncountered = false;
		learnedIndex = 0;
		pollRun = 0;
		
		
	}
	
	
	public void controlRobot(IRobot robot) {
		
		System.out.println(pollRun);
		//On the first move of the first run of a new maze:
		if((robot.getRuns() == 0) && (pollRun == 0)) {
			robotData = new RobotData();
		}
		
		if(robot.getRuns() > 0) {
			System.out.println("Second Run!");
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
	
	public void exploreControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
		
		switch(exits) {
	    	case 1: direction = deadEnd(robot);
	    		
	    			if(isFirstJunctionEncountered == true) {
	    				explorerMode = 0;
	    			}
	    			
	            	break;
	    	case 2: direction = corridor(robot);
	    			
	    			if(robotData.isJunctionVisitedBefore(robot)){
	    				robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
	    			}
	    			
	    			
	    	 		if(pollRun == 0) {
	    				robotData.recordJunction(robot);
	    				robotData.printJunction(robot);
	    				robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
	    			}
	    			
	    			
	    			break;
	    	case 3: 
	    	case 4: direction = junction(robot);
	    			//If the junction is New:
	            	if(!robotData.isJunctionVisitedBefore(robot)) {
	            		robotData.recordJunction(robot);
	            		robotData.printJunction(robot);
	            	}
	            	
	            	if(passageExits(robot) == 0) {
	            		explorerMode = 0;
	            	}
	            	
	            	isFirstJunctionEncountered = true;
	            	robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
	            	
	            	break;
	    }
		
		robot.face(direction);
		
	}
	
	public void backtrackControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
			
		switch(exits) {
	    	case 1: direction = deadEnd(robot);
	    			explorerMode = 0;
	    			break;
	    	case 2: direction = corridor(robot);
	    			if(robotData.isJunctionVisitedBefore(robot)){
	    				robotData.updateDeparted(robot, relativeToAbsoluteHeading(robot, direction));
	    			}
	            	break;
	    	case 3: 
	    	case 4: direction = junction(robot);
	    	        
	    			if(!robotData.isJunctionVisitedBefore(robot)) {
        				robotData.recordJunction(robot);
        				robotData.printJunction(robot);
        			}
	    			//If there are passage Exits:
	            	if(passageExits(robot) > 0) {
	            		explorerMode = 1;
	            		direction = randomPassageDirection(robot);
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
	
	public void learnedControl(IRobot robot) {
		
		int direction = 0;
		int exits = nonwallExits(robot);
		
		switch(exits) {
	    	case 1: direction = deadEnd(robot);
	            	break;
	    	case 2: direction = corridor(robot);
	    			if(pollRun == 0) {
	    				robot.setHeading(robotData.getDeparted(learnedIndex));
		    	        learnedIndex++;
		    	        return;
	    			}
	    			break;
	    	case 3: 
	    	case 4: robot.setHeading(robotData.getDeparted(learnedIndex));
	    	        learnedIndex++;
	    	        return;
	            	
	    }
		
		robot.face(direction);
		
	}
	
	public int nonwallExits(IRobot robot) {
			
		int num_of_non_wall_exits = 0;
			
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			if(robot.look(i) != IRobot.WALL) {
				num_of_non_wall_exits++;
			}
		}
		return num_of_non_wall_exits;
	}
	
	public int passageExits(IRobot robot) {
		
		int num_of_passage_exits = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			if(robot.look(i) == IRobot.PASSAGE) {
				num_of_passage_exits++;
			}
		}
		return num_of_passage_exits;
		
	}
	
	public int beenbeforeExits(IRobot robot) {
		
		int num_of_beenbefore_exits = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			if(robot.look(i) == IRobot.BEENBEFORE) {
				num_of_beenbefore_exits++;
			}
		}
		return num_of_beenbefore_exits;
		
	}
	
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
	
	private int corridor(IRobot robot) {
		
		int direction = 0;
		
		for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			if((robot.look(i) != IRobot.WALL) && (i!=IRobot.BEHIND)) {
				direction = i;
			}
		}
		
		return direction;
		
	}
	
	private int junction(IRobot robot) {
		
		if(passageExits(robot) > 0) {
			return randomPassageDirection(robot);
		}
		
		else {
			return randomNonWallDirection(robot);
		}
	}
	
	
	private int randomPassageDirection(IRobot robot) {
		
		
		int[] passage_array = {-1,-1,-1};
		int pa_index = 0;
		
        for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) == IRobot.PASSAGE && i!=IRobot.BEHIND) {
				passage_array[pa_index] = i;
				pa_index++;
			}
			
		}  
        
        return randomElementofArray(passage_array, pa_index);
		
	}
	
	private int randomNonWallDirection(IRobot robot) {
		
		int[] walless_array = {-1,-1,-1};
		int wa_index = 0;
		
        for(int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) {
			
			if(robot.look(i) != IRobot.WALL && i!=IRobot.BEHIND) {
				walless_array[wa_index] = i;
				wa_index++;
			}
			
		}
        
		return randomElementofArray(walless_array, wa_index);
		
	}
	
	
	
	public static int randomElementofArray(int[] A, int num_of_elements) {
		
		Random rand = new Random();
		return A[rand.nextInt(num_of_elements)];
		
	}
	
	
	
	public static int reverseHeading(int Heading) {
		
		if(Heading == IRobot.NORTH) {
			return IRobot.SOUTH;
		}
		
		else if(Heading == IRobot.EAST) {
			return IRobot.WEST;
		}
		
		else if(Heading == IRobot.SOUTH) {
			return IRobot.NORTH;
		}
		
		else {
			return IRobot.EAST;
		}

	}
	
	public static int relativeToAbsoluteHeading(IRobot robot, int relativeDirection) {
		return (((robot.getHeading() - IRobot.NORTH) + (relativeDirection - IRobot.AHEAD)) % 4) + IRobot.NORTH;  
	}
	
	
	 
}
	
class RobotData {
	
	private static int maxJunctions = 10000; //Max junctions likely to occur.
	private static int junctionIndex;
	private static int junctionCounter;//No.of junctions stored.
	
	class Junction{
		
		private int juncX;     //X-coordinate of Junction.
		private int juncY;    //Y-coordinate of Junction.
		private int arrived; //Heading the robot first arrived from
		private int departed;//Final heading when the robot leaves this junction.
		
	}
	
	Junction junctionArray[] = new Junction[maxJunctions];
	
	public RobotData() {
		
		junctionCounter = 0;
		junctionIndex = 0;
		
		for(int i = 0; i < maxJunctions; i++) {
			junctionArray[i] = new Junction();
		}
		
    }
	
    
	
	public void resetJunctionCounter() {
		junctionIndex = 0;
		junctionCounter = 0;
	}
	
    public void recordJunction(IRobot robot) {	
		junctionArray[junctionIndex].arrived = robot.getHeading();
		junctionArray[junctionIndex].juncX = robot.getLocation().x;
		junctionArray[junctionIndex].juncY = robot.getLocation().y;
		
		junctionIndex++;
		junctionCounter++;
		
	}
    
    public void printJunction(IRobot robot) {
    	
    	System.out.print("Junction " + (junctionCounter));
    	System.out.print(" (x=" + robot.getLocation().x);
    	System.out.print(",y=" + robot.getLocation().y);
    	System.out.print(") heading ");
    	
    	switch(junctionArray[junctionIndex - 1].arrived) {
    	    case (IRobot.NORTH): System.out.println("NORTH");
    	                         break;
    	    case (IRobot.EAST) : System.out.println("EAST");
                                 break;
    	    case (IRobot.SOUTH): System.out.println("SOUTH");
                                 break;
    	    case (IRobot.WEST) : System.out.println("WEST");
                                 break;
    	}
    }
    
    private int highestElement() {
    	
    	return junctionArray[junctionIndex - 1].arrived;
    	
    }
    
    private void popElement() {
    	
    	junctionArray[junctionIndex - 1].arrived = -1;
    	
    	junctionIndex--;
    }
    
    //searchJunction method assumes robot is at a Junction with no Passage Exits.
    public int searchJunction(IRobot robot) {
    	
    	int junctionHeading = 0; // value to return
    	
    	junctionHeading = highestElement();
        popElement(); 
    	
    	
    	return junctionHeading;
    	
    }
    
    public int findJunctionIndex(IRobot robot) {
		
		for(int i = 0; i < junctionIndex; i++) {
			if((robot.getLocation().x == junctionArray[i].juncX) && (robot.getLocation().y == junctionArray[i].juncY)) {
				return i;
			}
		}
		
		return -1;
	}
    
    public void updateDeparted(IRobot robot, int heading) {
    	
    	junctionArray[findJunctionIndex(robot)].departed = heading ;
    	
    }
    
    public int getDeparted(int index) {
    	return junctionArray[index].departed;
    }
    
    public boolean isJunctionVisitedBefore(IRobot robot) {
		
		if(findJunctionIndex(robot) == -1) {
			return false;
		}
		
		else {
			return true;
		}
	}
    
   
}


