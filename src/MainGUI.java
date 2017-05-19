import javax.swing.JFrame;


public class MainGUI {

	public static void main(String[] args) {
		JFrame frame = new AffineFrame(); 
		frame.setSize(800, 800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
