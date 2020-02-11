/*
 * File:    Ex3	.java
 * Created: 27 October 2019
 * Author:  Shivakumar Mahesh
 */

import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.lang.*;
import java.util.Random;

public class Ex3{
    
  public void controlRobot(IRobot robot){

    int num_of_walled_directions = 0;
    int heading = headingController(robot);
    int direction_to_turn = absolute_to_relative_heading(robot.getHeading(), heading);
    ControlTest.test(heading, robot);
    robot.setHeading(heading);
   
    for(int i=IRobot.AHEAD;i<=IRobot.LEFT;i++){//Finding the Number of Directions
      if(robot.look(i)==IRobot.WALL){          //with walls.
        num_of_walled_directions++;
      }
    }
    
    //Performance Monitoring.
    System.out.print("I'm going ");
    switch(direction_to_turn){
      case (IRobot.AHEAD): System.out.print("forward ");break;
      case (IRobot.RIGHT): System.out.print("right ");break;
      case (IRobot.BEHIND):System.out.print("backwards ");break;
      case (IRobot.LEFT):  System.out.print("left ");break;
    }

    switch(num_of_walled_directions){  
      case 3: System.out.println("at a dead end");break;
      case 2: System.out.println("down a corridor");break;
      case 1: System.out.println("at a junction");break;
      case 0: System.out.println("at a crossroads");break;
      
    }
    

  }//End of controlRobot method.

  
  private byte isTargetNorth(IRobot robot){
    
    if(robot.getLocation().y>robot.getTargetLocation().y){return 1;}
    else if(robot.getLocation().y<robot.getTargetLocation().y){return -1;}
    else {return 0;}
  }

  private byte isTargetEast(IRobot robot){
    
    if(robot.getLocation().x>robot.getTargetLocation().x){return -1;}
    else if(robot.getLocation().x<robot.getTargetLocation().x){return 1;}
    else {return 0;}
  }
  
  public static int absolute_to_relative_heading(int current_heading, int absolute_direction){
    
    int relative_direction = 0;

    /*This implimentation uses Arithmetic of Absolute and Relative directions of IRobot Class*/ 
    for(int i=0;i<4;i++){
      if((absolute_direction == current_heading+i) || (absolute_direction == current_heading - (4-i))){
        relative_direction = (IRobot.AHEAD + i);
      }
    }

    return relative_direction; 
  }//For Example: absolute_to_relative_heading(IRobot.NORTH, IRobot.WEST) returns IRobot.LEFT.


  private int lookHeading(IRobot robot, int absolute_direction){
    return robot.look(absolute_to_relative_heading(robot.getHeading(), absolute_direction)); 
  }


  public int headingController(IRobot robot){
    int x_direction = 0;//Takes the value IRobot.EAST or IRobot.WEST
                        //based on whether Target is to the EAST or WEST.
    int y_direction = 0;//Takes the value IRobot.NORTH or IRobot.SOUTH
                        //based on whether Target is to the NORTH or SOUTH.
 
    int direction = 0;//Takes on an Absolute Direction value, It is the value headingController returns.
    boolean need_to_return_random_direction = false;
    
    Random rand = new Random();
    
    switch(isTargetNorth(robot)){
    case 1: y_direction = IRobot.NORTH;
            break;
    case -1: y_direction = IRobot.SOUTH;
            break;     
    }

    switch(isTargetEast(robot)){
    case 1: x_direction = IRobot.EAST;
            break;
    case -1: x_direction = IRobot.WEST;
             break;
    }

 
    if(Math.abs(isTargetNorth(robot))+Math.abs(isTargetEast(robot))==2){//if(Robot must move horizontally and 
                                                                        //vertically to reach the target).
      if(lookHeading(robot,y_direction)!=IRobot.WALL && lookHeading(robot,x_direction)!=IRobot.WALL){
          if(rand.nextInt(2)==0){direction = y_direction;}//Randomly pick vertical or
          else {direction = x_direction;}                 //horizontal Direction.
      }
      else if(lookHeading(robot,y_direction)!=IRobot.WALL && lookHeading(robot,x_direction)==IRobot.WALL){
        direction=y_direction;
      }
      else if(lookHeading(robot,y_direction)==IRobot.WALL && lookHeading(robot,x_direction)!=IRobot.WALL){
        direction=x_direction;
      }
      else{      
        need_to_return_random_direction=true;
      }   
    }

    else if(isTargetNorth(robot)!=0){//else if(Robot needs to move vertically to reach the target).
      if(lookHeading(robot,y_direction)!=IRobot.WALL){
	direction=y_direction;
	}
      else{
	need_to_return_random_direction=true;
	}
    }

    else if(isTargetEast(robot)!=0){//else if(Robot needs to move horizontally to reach the target).
      if(lookHeading(robot,x_direction)!=IRobot.WALL){direction=x_direction;}
      else{need_to_return_random_direction=true;} 
    } 
    

    if(need_to_return_random_direction){
      int randno;
      do{
      
         randno = rand.nextInt(4);

         if ( randno == 0)
            direction = IRobot.NORTH;
         else if (randno == 1)
            direction = IRobot.EAST;
         else if (randno == 2)
            direction = IRobot.SOUTH;
         else 
            direction = IRobot.WEST;
         }while (lookHeading(robot,direction)==IRobot.WALL);
     
      return direction;  //Face the direction.
    }
    else{
      return direction;
    }
    
  
  }//End of headingController method.

public void reset() {
ControlTest.printResults();
}

}//End of Ex3 Class.
