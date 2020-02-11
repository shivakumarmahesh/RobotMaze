/*
* File: Ex2.java
* Created: 27 October 2019, 00:34
* Author: Shivakumar Mahesh
*/

//NOTE: "Walless" (wherever specified) would imply: Without a Wall (as in Wall-less). 
import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.Random;

public class Ex2{

  public void controlRobot(IRobot robot){
    Random rand = new Random();
    int direction;
    int[] Walless_Direction_Array = {-1,-1,-1,-1}; //Walless_Direction_Array will contain          
                                                   //the absolute directions with no walls.
                                                   //For Example, {IRobot.NORTH,IRobot.WEST,-1,-1}.
  
    int num_of_walless_directions = 0; //Set to Number of Directions with
                                       //no walls by the end of the program.
                                       //Also operates as index for Walless Direction Array.                      
                
                
    for(int i=IRobot.AHEAD;i<=IRobot.LEFT;i++){           
      if(robot.look(i)!=IRobot.WALL){//Check to see if there's no wall in direction i.                                               
        Walless_Direction_Array[num_of_walless_directions] = i; //Add walless direction to Direction Array.
        num_of_walless_directions++; //Increment index of Walless Direction Array.
      }          
    }

    if(robot.look(IRobot.AHEAD)!=IRobot.WALL){
      direction = IRobot.AHEAD;
    }
                
    else{//Pick a random Direction with no walls from Direction Array.
      direction = Walless_Direction_Array[rand.nextInt(num_of_walless_directions)];
    } 
                                                                                       
    if(rand.nextInt(8)==0){//~1/8 chance to change direction randomly.
      direction = Walless_Direction_Array[rand.nextInt(num_of_walless_directions)];
    }
                
    robot.face(direction);//Face the robot in this direction.
                
    //Performance Monitoring:
    System.out.print("I'm going ");
    switch(direction){
    case (IRobot.AHEAD): System.out.print("forward ");break;
    case (IRobot.RIGHT): System.out.print("right ");break;
    case (IRobot.BEHIND): System.out.print("backwards ");break;
    case (IRobot.LEFT):System.out.print("left ");break;
                                                      
    }
                
    switch(num_of_walless_directions){  
    case 1: System.out.println("at a dead end");break;
    case 2: System.out.println("down a corridor");break;
    case 3: System.out.println("at a junction");break;
    case 4: System.out.println("at a crossroads");break;
    
    }
                
                
  }

}
