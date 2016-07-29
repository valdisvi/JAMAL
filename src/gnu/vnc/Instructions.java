package gnu.vnc;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Font;
import java.awt.Color;


public class Instructions {
    /**
     * @wbp.parser.entryPoint
     */
    public static void getInstructions() {
        JFrame f = new JFrame();
        f.getContentPane().setBackground(Color.WHITE);
        f.getContentPane().setLayout(null);
        
        f.setTitle("Instructions");
        f.setBounds(100, 100, 447, 440);
        
        JTextPane JEditorPane = new JTextPane();
        JEditorPane.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
        JEditorPane.setEditable(false);
        JEditorPane.setBounds(12, 0, 424, 412);
        JEditorPane.setContentType("text/html");
        JEditorPane.setText("<p><b>Instructions on Java remote access server setup:</b></p> \n \n" +
        		"<p>1. Enter IPs that will be allowed to connect to this machine. Leaving "
        		+ "blank lets any IP connect. Example: \"10.0.1.51, 198.0.1.101\". \n \n </p>" +
        		"<p>2. Enter desired port. Leaving this field blank, creates server on default "
        		+ "5900 port. \n \n</p>" +
        		"<p>3. Enter password if necessary. Leaving this field blank, creates "
        		+ "server without password. \n \n</p>" +
        		"<p>4. Press button \"Start Server\". \n \n</p>" +
        		"<p>If you encounter any problems, you can stop/restart server by pressing "
        		+ "\"Stop Server\" or \"Restart Server\" to stop or reconnect client. Restarted server will "
        		+ "use values from text fields. \n</p>" +
        		"<p><b><font color=\"red\">Warning: Use correct values for parameters, otherwise server will not start.</font></b></p>");
        
        
        
        StyledDocument doc = JEditorPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        
        
        f.getContentPane().add(JEditorPane);
        f.setVisible(true);
    }
}
