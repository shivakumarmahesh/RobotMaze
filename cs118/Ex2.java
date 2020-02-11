/*
* File: DumboController.java
* Created: 17 September 2002, 00:34
* Author: Stephen Jarvis
*/

//NOTE: "Walless" (wherever specified) would imply: Without a Wall (as in Wall-less). 
import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.util.Random;

public class Ex2
{

	public void controlRobot(IRobot robot) {
                Random rand = new Random();
		int direction;

                int[] Direction_Array = {IRobot.AHEAD,IRobot.RIGHT,IRobot.BEHIND,IRobot.LEFT};
                int[] Walless_Direction_Array = {-1,-1,-1,-1}; //~Walless_Direction_Array will equal          
                                                               // {IRobot.RIGHT,IRobot.LEFT,-1,-1}
                                                               // if RIGHT AND LEFT Directions have  
                                                               // no walls but AHEAD and BEHIND do.       
                
                int num_of_walless_directions = 0; //~Set to Number of Directions with
                                                   // no walls by the end of the program.                      
                
                
		for(int i=0;i<4;i++) //See which directions are free of walls:
                {
                
                if(robot.look(Direction_Array[i])!=IRobot.WALL) //~Check to see if there's no wall
                {                                               // in a given Direction
                Walless_Direction_Array[num_of_walless_directions] = Direction_Array[i]; 
                num_of_walless_directions++; 
                }
                
                }

                if(robot.look(IRobot.AHEAD)!=IRobot.WALL)
                {direction = IRobot.AHEAD;} //~If no wall AHEAD, direction <- AHEAD
                
                else
                {direction = Walless_Direction_Array[rand.nextInt(num_of_walless_directions)];} //~Pick a Walless Direction.
                
                if(rand.nextInt(8)==0)
                {direction = Walless_Direction_Array[rand.nextInt(num_of_walless_directions)];} //~1/8 chance of picking random direction.
                
		robot.face(direction); /* Face the robot in this direction */ 
                



                /*Performance Monitoring:*/
                System.out.print("I'm going ");
                switch(direction){
                case (IRobot.AHEAD): System.out.print("forward ");break;
                case (IRobot.RIGHT): System.out.print("right ");break;
                case (IRobot.BEHIND): System.out.print("behind ");break;
                case (IRobot.LEFT):System.out.print("left ");break;
                                                      
                }
                
                switch(num_of_walless_directions){  
                case 1: System.out.println("at a dead end");break;
                case 2:             if((robot.look(IRobot.AHEAD)!=IRobot.WALL)&(robot.look(IRobot.BEHIND)!=IRobot.WALL))
                        {System.out.println("down a corridor");}
                        else 
                        {System.out.println("at a junction");}break;
                case 3: System.out.println("at a junction");break;
                case 4: System.out.println("at a crossroads");break;
                default: System.out.println("I'm trapped!");break;
                }
                
                
	}

}
