package gnu.vnc;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import ubicomp.vnc.VNCHost;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import gnu.vnc.Instructions;
import mainpack.AllowedIpList;
import mainpack.Main;
import sun.awt.X11.InfoWindow.Tooltip;

import java.awt.Toolkit;
import javax.swing.JPasswordField;
// Install WindowBulder plugin on Eclipse
// Then right click on this class in Project Explorer
// and choose "Open With" and choose "WindowBuilder editor"
// Then choose "Design" tab of the editor

public class GUI {

    public VNCHost myHost;
    public JFrame GUIFrame;
    public JTextField ipTextField;
    private JTextField portTextField;
    private JLabel lblServerNotRunning;
    private JProgressBar progressBar;
    private JTextPane txtpnInfo;
    private String IP;
    private int port;
    private String computername;
    private static List<GUI> guiList = new ArrayList<GUI>();
    private String stringer;
    private JPasswordField passwordField;
    private AllowedIpList allowedIpList = new AllowedIpList();
    public boolean ipListOpen = false;
    private String ipFromList;

    /**
     * Launch the application.
     */
    public static void serverMain(String[] args) {
        GUI gui = new GUI();
        gui.GUIFrame.setVisible(true);
        guiList.add(0, gui);
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

    public String getStringer() {
        return stringer;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        GUIFrame = new JFrame();
        GUIFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class
                .getResource("/mainpack/images/watermelon_jamal_16x16.png")));

        // Form properties
        GUIFrame.setTitle("Jamal Server");
        GUIFrame.setBounds(100, 100, 460, 420);
        GUIFrame.setLocationRelativeTo(null);
        GUIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUIFrame.getContentPane().setLayout(null);

        GUIFrame.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                ipListLocation();

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

        });

        GUIFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub
                allowedIpList.setState(java.awt.Frame.ICONIFIED);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub
                allowedIpList.setState(java.awt.Frame.NORMAL);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }
        });

        final JButton btnStartServer = new JButton("Start Server");
        btnStartServer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	if (portTextField.getText() == null
                        || portTextField.getText().equals("")) {
                    portTextField.setText("5900");
                }
            	if(Double.parseDouble(portTextField.getText()) > 65535 || Integer.parseInt(portTextField.getText()) <= 0){
            		JOptionPane.showMessageDialog(GUIFrame, "Please enter valid port");
            		return;
            	}
            	if(!isAdmin()){
            		if(Integer.parseInt(portTextField.getText()) <= 1024)
            		{
            			JOptionPane.showMessageDialog(GUIFrame, "Please choose port starting from 1025 or get Administrator privilegies");
            			return;
            		}
            	}
                getIpList();
                if (myHost == null) {
                    IP = null;
                    port = 5900;
                    try {
                        computername = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    if (ipFromList != null && !ipFromList.equals("")) {
                        System.out.println("IP " + ipFromList);
                        IP = ipFromList;
                    }
                    

                    port = Integer.parseInt(portTextField.getText());

                    myHost = new VNCHost(IP, computername, port,

                            java.awt.Toolkit.getDefaultToolkit()
                                    .getScreenSize().height

                            ,

                            java.awt.Toolkit.getDefaultToolkit().getScreenSize()

                                    .width,
                            new String(passwordField.getPassword()));

                    myHost.start();

                    lblServerNotRunning.setText("Server Running");
                    progressBar.setBackground(Color.GREEN);
                    String someips = "Any";
                    if (ipFromList != null && !ipFromList.equals("")) {
                        someips = ipFromList;
                        someips = someips.replace(" ", "");
                        someips = someips.replace(",", "\n");
                    } else {
                        someips = "Any \n";
                    }
                    stringer = "Info:\n" + "Allowed IPs: \n" + someips
                            + "Port: " + portTextField.getText() + "\n"
                            + "Connected IP: \n";

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

        ipTextField = new JTextField();

        ipTextField.setBounds(95, 30, 170, 19);
        GUIFrame.getContentPane().add(ipTextField);
        ipTextField.setColumns(10);

        JLabel lblNewLabel = new JLabel("IP");
        lblNewLabel.setBounds(12, 30, 70, 15);
        GUIFrame.getContentPane().add(lblNewLabel);

        portTextField = new JTextField();
        portTextField.setColumns(10);
        portTextField.setBounds(95, 69, 170, 19);
        GUIFrame.getContentPane().add(portTextField);

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
        txtpnInfo.setFont(new Font("DejaVu Serif Condensed",
                Font.BOLD | Font.ITALIC, 12));
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
        btnNewButton.setBounds(266, 165, 170, 25);
        GUIFrame.getContentPane().add(btnNewButton);

        JButton btnRestartServer = new JButton("Restart Server");
        btnRestartServer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                btnCloseServer.doClick();
                btnStartServer.doClick();
            }

        });
        btnRestartServer.setBounds(266, 202, 170, 25);
        GUIFrame.getContentPane().add(btnRestartServer);

        JButton btnBackToMain = new JButton("Back to main menu");
        btnBackToMain.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                btnCloseServer.doClick();
                GUIFrame.dispose();
                Main.frame.setVisible(true);
                allowedIpList.dispose();
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
        passwordField.setBounds(95, 107, 170, 19);
        GUIFrame.getContentPane().add(passwordField);

        final JButton btnAdd = new JButton("Add");

        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if (allowedIpList.listedIPs.contains(ipTextField.getText())) {
                    ipTextField.setText(null);
                }
                if (ipTextField.getText() != null
                        && !ipTextField.getText().equals("")) {

                    String select = ipTextField.getText()
                            .replaceAll("[^A-Za-z0-9^.]", "");
                    Object obj = select;
                    AllowedIpList.listedIPs.add(0, obj);
                    ipTextField.setText(null);
                }
            }
        });
        btnAdd.setBounds(275, 27, 79, 25);
        GUIFrame.getContentPane().add(btnAdd);

        JButton btnShowList = new JButton("IP list");
        btnShowList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if (!ipListOpen) {
                    ipListLocation();
                    allowedIpList.setState(java.awt.Frame.NORMAL);
                    allowedIpList.setVisible(true);
                    ipListOpen = true;
                } else {
                    allowedIpList.setVisible(false);
                    ipListOpen = false;
                }
            }

        });
        btnShowList.setBounds(357, 27, 79, 25);
        GUIFrame.getContentPane().add(btnShowList);

    }

    public void ipListLocation() {
        allowedIpList.setLocation(GUIFrame.getX() + GUIFrame.getWidth(),
                GUIFrame.getY());
    }

    public void setText(List<Socket> socketList) {
        String sockets = getStringer();
        if (!socketList.isEmpty()) {
            for (Socket s : socketList) {
                sockets = sockets + s.getInetAddress() + "\n";
            }
            txtpnInfo.setText("");
            txtpnInfo.setText(sockets);
        } else {
            txtpnInfo.setText(getStringer());
        }

    }

    public void getIpList() {
        ipFromList = "";
        if (allowedIpList.listedIPs.size() != 0) {
            for (int i = 0; i < allowedIpList.listedIPs.size(); i++) {
                String ip = (String) allowedIpList.listedIPs.get(i);
                ipFromList = ipFromList + ip + ",";
            }
        } else {
            ipFromList = "";
        }
    }
    
    // Copy from SO
    
    public static boolean isAdmin(){
        Preferences prefs = Preferences.systemRoot();
        PrintStream systemErr = System.err;
        synchronized(systemErr){    // better synchroize to avoid problems with other threads that access System.err
            System.setErr(null);
            try{
                prefs.put("foo", "bar"); // SecurityException on Windows
                prefs.remove("foo");
                prefs.flush(); // BackingStoreException on Linux
                return true;
            }catch(Exception e){
                return false;
            }finally{
                System.setErr(systemErr);
            }
        }
    }

}
