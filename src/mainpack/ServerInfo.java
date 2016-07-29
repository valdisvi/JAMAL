package mainpack;


import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Font;

public class ServerInfo {

    /**
     * @wbp.parser.entryPoint
     */
    public static void getServerInfo() {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(null);

        f.setTitle("Instructions");
        f.setBounds(100, 100, 447, 225);

        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textPane.setEditable(false);
        textPane.setBounds(12, 12, 424, 185);

        textPane.setText("Jamal server is an easy 2 click setup remote "
                + "connection server. It allows for quick access to another computer "
                + "and easy remote control. "
                + "It transmits the keyboard and mouse "
                + "events from one computer to another, relaying the "
                + "graphical screen updates back in the other direction, "
                + "over a network.");
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        f.getContentPane().add(textPane);
        f.setVisible(true);
    }
}
