package mainpack;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Font;

public class ClientInfo {

    /**
     * @wbp.parser.entryPoint
     */
    public static void getClientInfo() {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(null);

        f.setTitle("Instructions");
        f.setBounds(100, 100, 447, 215);

        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textPane.setEditable(false);
        textPane.setBounds(12, 12, 424, 173);

        textPane.setText("Jamal client is a free remote control software. "
                + "With Jamal client, you can see the desktop of a remote machine "
                + "and control it with your local mouse and keyboard, just like "
                + "you would do it sitting in the front of that computer. "
                + "Jamal client is a slight modification of TightVNC client.");
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        f.getContentPane().add(textPane);
        f.setVisible(true);
    }
}
