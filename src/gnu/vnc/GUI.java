package gnu.vnc;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import ubicomp.vnc.VNCHost;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import gnu.vnc.Instructions;
import mainpack.Main;
import java.awt.Toolkit;
import javax.swing.JPasswordField;
// Install WindowBulder plugin on Eclipse
// Then right click on this class in Project Explorer
// and choose "Open With" and choose "WindowBuilder editor"
// Then choose "Design" tab of the editor

public class GUI {

    public VNCHost myHost;
    public JFrame GUIFrame;
    private JTextField textField;
    private JTextField textField_1;
    private JLabel lblServerNotRunning;
    private JProgressBar progressBar;
    private JTextPane txtpnInfo;
    private String IP;
    private int port;
    private String computername;
    private static List<GUI> guiList = new ArrayList<GUI>();
    private String stringer;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void serverMain(String[] args) {
        GUI gui = new GUI();
        gui.GUIFrame.setVisible(true);
        guiList.add(0,gui);
    }

    /**
     * Create the application objects and add listeners
     */
    public GUI() {
        initialize();
    }
    
    public static List<GUI> getGui() {
        return guiList;
    }
    
    public String getStringer(){
        return stringer;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        GUIFrame = new JFrame();
        GUIFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/mainpack/images/watermelon_jamal_16x16.png")));

        // Form properties
        GUIFrame.setTitle("Jamal Server");
        GUIFrame.setBounds(100, 100, 460, 420);
        GUIFrame.setLocationRelativeTo(null);
        GUIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUIFrame.getContentPane().setLayout(null);

        final JButton btnStartServer = new JButton("Start Server");
        btnStartServer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (myHost == null) {
                    IP = null;
                    port = 5900;
                    try {
                        computername = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    if (textField.getText() != null
                            && !textField.getText().equals("")) {
                        System.out.println("IP " + textField.getText());
                        IP = textField.getText();
                    }
                    if (textField_1.getText() == null
                            || textField_1.getText().equals("")) {
                        textField_1.setText("5900");
                    }

                    port = Integer.parseInt(textField_1.getText());

                    myHost = new VNCHost(IP, computername, port,

                    java.awt.Toolkit.getDefaultToolkit().getScreenSize().height

                    ,

                    java.awt.Toolkit.getDefaultToolkit().getScreenSize()

                    .width, new String(passwordField.getPassword()));

                    myHost.start();

                    lblServerNotRunning.setText("Server Running");
                    progressBar.setBackground(Color.GREEN);
                    String someips = "Any";
                    if (textField.getText() != null
                            && !textField.getText().equals("")) {
                        someips = textField.getText();
                        someips = someips.replace(" ", "");
                        someips = someips.replace(",", "\n");
                    }
                    stringer = "Info:\n" + "Allowed IPs: \n" + someips
                            + "\n" + "Port: " + textField_1.getText() + "\n"
                            + "Connected IPs: \n";
                    
                    txtpnInfo.setText(stringer);
                    

                }

            }
        });
        btnStartServer.setBounds(12, 287, 170, 25);
        GUIFrame.getContentPane().add(btnStartServer);

        final JButton btnCloseServer = new JButton("Stop Server");
        btnCloseServer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if (myHost != null) {
                    myHost.stop();
                    myHost = null;
                    lblServerNotRunning.setText("Server Stopped");
                    progressBar.setBackground(Color.RED);
                }
            }
        });
        btnCloseServer.setBounds(266, 287, 170, 25);
        GUIFrame.getContentPane().add(btnCloseServer);

        textField = new JTextField();
        
        textField.setBounds(95, 30, 316, 19);
        GUIFrame.getContentPane().add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel = new JLabel("IPs");
        lblNewLabel.setBounds(12, 30, 70, 15);
        GUIFrame.getContentPane().add(lblNewLabel);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(95, 69, 316, 19);
        GUIFrame.getContentPane().add(textField_1);

        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(12, 71, 70, 15);
        GUIFrame.getContentPane().add(lblPort);

        progressBar = new JProgressBar();
        progressBar.setForeground(Color.GREEN);
        progressBar.setBackground(Color.LIGHT_GRAY);
        progressBar.setBounds(194, 287, 60, 25);
        GUIFrame.getContentPane().add(progressBar);

        lblServerNotRunning = new JLabel("Server not running",
                SwingConstants.CENTER);
        lblServerNotRunning.setBounds(12, 260, 424, 15);
        GUIFrame.getContentPane().add(lblServerNotRunning);

        txtpnInfo = new JTextPane();
        txtpnInfo.setFont(new Font("DejaVu Serif Condensed", Font.BOLD | Font.ITALIC, 12));
        txtpnInfo.setEditable(false);
        txtpnInfo.setText("Info:");
        txtpnInfo.setBounds(12, 100, 216, 94);

        JButton btnNewButton = new JButton("Instructions");
        btnNewButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // Instructions

                Instructions.getInstructions();

            }
        });
        btnNewButton.setBounds(266, 165, 145, 25);
        GUIFrame.getContentPane().add(btnNewButton);

        JButton btnRestartServer = new JButton("Restart Server");
        btnRestartServer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                btnCloseServer.doClick();
                btnStartServer.doClick();
            }

        });
        btnRestartServer.setBounds(266, 202, 145, 25);
        GUIFrame.getContentPane().add(btnRestartServer);
        
        JButton btnBackToMain = new JButton("Back to main menu");
        btnBackToMain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                btnCloseServer.doClick();
                GUIFrame.dispose();
                Main.frame.setVisible(true);
            }
        });
        btnBackToMain.setBounds(148, 335, 170, 23);
        GUIFrame.getContentPane().add(btnBackToMain);
        
        JScrollPane scrollPane = new JScrollPane(txtpnInfo);
        scrollPane.setBounds(12, 154, 216, 94);
        GUIFrame.getContentPane().add(scrollPane);
        
        JLabel label = new JLabel("Password");
        label.setBounds(12, 109, 70, 15);
        GUIFrame.getContentPane().add(label);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(95, 107, 316, 19);
        GUIFrame.getContentPane().add(passwordField);

    }
    
    public void setText(List<Socket> socketList){
        String sockets = getStringer();
        if(!socketList.isEmpty()) {
            for(Socket s : socketList){
                sockets=sockets+ s.getInetAddress() +"\n";
            }
            txtpnInfo.setText("");
            txtpnInfo.setText(sockets);
        } else {
            txtpnInfo.setText(getStringer());
        }
        
    }
}
