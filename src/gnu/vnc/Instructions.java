package gnu.vnc;

import javax.swing.JFrame;
import javax.swing.JTextPane;


public class Instructions {
    public static void getInstructions() {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(null);
        
        f.setTitle("Instructions");
        f.setBounds(100, 100, 447, 372);
        
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBounds(12, 12, 424, 318);
        
        textPane.setText("Instructions on Java remote access server setup \n" +
        		"1.Enter IPs that will be allowed to connect to this machine. \n" +
        		"Note: Leaving blank lets any IP connect. \n" +
        		"Example: \"255.255.255.0, 198.0.1.101 \" \n \n" +
        		"2.Enter desired port. \n \n" +
        		"3.Start Server. \n \n" +
        		"If you encounter any problems, you can restart server \n" +
        		"by pressing Restart Server and reconnecting on client. \n" +
        		"Note: Restarted server will use values from text fields. \n" +
        		"Warning: Wrong values of port will brake connection.");
        
        
        f.getContentPane().add(textPane);
        f.setVisible(true);
    }
}
