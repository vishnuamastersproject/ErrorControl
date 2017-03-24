import java.awt.BorderLayout;

import javax.swing.JFrame;


public class Application {
	
	public Application() {
		JFrame guiFrame = new JFrame();  //make sure the program exits when the frame closes 
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		guiFrame.setTitle("Error Control"); 
		guiFrame.setSize(910, 880);  //This will center the JFrame in the middle of the screen 
		guiFrame.setLocationRelativeTo(null);  //Options for the JComboBox  
		
		guiFrame.add(new ErrorServer(), BorderLayout.NORTH);
		guiFrame.add(new ErrorClient(), BorderLayout.SOUTH);
		
		guiFrame.setVisible(true);		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Application();
	}

}
