package ubicomp.vnc;

import gnu.rfb.server.DefaultRFBAuthenticator;
import gnu.rfb.server.RFBHost;
import gnu.vnc.awt.VNCRobot;

/**
 * Main method creates a {@link gnu.rfb.server.RFBServer RFB server} and
 * {@link gnu.vnc.WebServer VNC-viewer-applet-serving web server} according to
 * passed arguments.
 * 
 * Edited by Cameron Pickett
 **/

public class VNCHost {

    private static final String sServerClassName = "gnu.vnc.awt.VNCRobot";
    private final String sServerIp;
    private final String sDisplayName;
    private final int sDisplayPort;
    private final int sWidth;
    private final int sHeight;
    private boolean mStarted;
    private RFBHost mRFBHost;
    private String password;

    public VNCHost() {
        sServerIp = null;
        sDisplayName = "0";
        sDisplayPort = 5900;
        sHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        sWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        mStarted = false;
    }

    public VNCHost(String serverIp, String displayName, int displayPort,
            int height, int width, String password) {
        sServerIp = serverIp;
        sDisplayName = displayName;
        sDisplayPort = displayPort;
        sHeight = height;
        sWidth = width;
        this.password = password;
        mStarted = false;
    }

    public void start() {
        if (!mStarted) {
            // Create RFB hosts and web servers
            int display = sDisplayPort;
            String serverClassName = sServerClassName;
            String displayName = sDisplayName;
            int height = sHeight;
            int width = sWidth;
            String restrictedTo = sServerIp;
            String noPasswordFor = sServerIp;

            System.out.println(displayName);
            Class<VNCRobot> serverClass = VNCRobot.class;
            // RFB host
            try {
                mRFBHost = new RFBHost(display, displayName, serverClass, width,
                        height, new DefaultRFBAuthenticator(password,
                                restrictedTo, noPasswordFor));
                mRFBHost.setUp();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            // Webserver
            // new WebServer(display, displayName, width, height);

            mStarted = true;

            System.out.println("  VNC display " + display);

            System.out.println("  Class: " + serverClassName);
        }
    }

    public void stop() {
        mRFBHost.stop();
    }

}
