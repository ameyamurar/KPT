/* 
 * GUI.java
 * 
 * Author: Ameya Murar
 * 
 * Date: 2/23/2016
 *  
 * Revisions: 
 *      
 */

import javax.swing.JFrame;

public class GUI {
	
	/**
	 * The main method
	 *
	 * param     args    command line arguments 
	 */
	
	public static void main(String[] args) throws Exception {
    	PA1 app=new PA1();
    	app.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    	app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
    	app.setVisible(true);
    }
}