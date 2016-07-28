package gnu.vnc.awt;

import gnu.rfb.Colour;
import gnu.rfb.PixelFormat;
import gnu.rfb.Rect;
import gnu.rfb.server.RFBClient;
import gnu.rfb.server.RFBServer;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.io.IOException;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * A very limited implementation of a {@link java.awt.Robot} that supports RFB
 * clients.
 **/

public class VNCRobot extends Component implements RFBServer {

    private static final long serialVersionUID = 5691967656894600282L;

    private String displayName;

    private boolean is1torelease = false;
    private boolean is2torelease = false;
    private boolean is3torelease = false;

    private GraphicsDevice device;

    private Robot robot;

    public VNCRobot(int display, String displayName, int width, int height) {
        this.displayName = displayName;
        device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        try {
            robot = new Robot(device);
        } catch (AWTException x) {
        }
    }

    @Override
    public void addClient(RFBClient client) {
    }

    @Override
    public boolean allowShared() {
        return true;
    }

    @Override
    public void clientCutText(RFBClient client, String text)
            throws IOException {

        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

    }

    // Messages from client to server

    @Override
    public void fixColourMapEntries(RFBClient client, int firstColour,
            Colour[] colourMap) throws IOException {
    }

    @Override
    public void frameBufferUpdateRequest(RFBClient client, boolean incremental,
            int x, int y, int w, int h) throws IOException {
        // If you really really want automatic refreshes, comment out the following two lines.
        // BEWARE, it will send the entire screen, and probably be unusably slow. For now,
        // you must "request screen refresh" manually from your VNC viewer.
        //if (incremental)
        //    return;

        // Create image
        BufferedImage image = robot
                .createScreenCapture(new Rectangle(x, y, w, h));

        // Encode image
        Rect r = Rect.encode(client.getPreferredEncoding(),
                client.getPixelFormat(), image, x, y, w, h);

        // Write to client
        Rect[] rects = { r };
        try {
            client.writeFrameBufferUpdate(rects);
        } catch (IOException xx) {
            xx.printStackTrace();
        }
    }

    @Override
    public String getDesktopName(RFBClient client) {
        return displayName;
    }

    @Override
    public int getFrameBufferHeight(RFBClient client) {
        return device.getDefaultConfiguration().getBounds().height;
    }

    @Override
    public int getFrameBufferWidth(RFBClient client) {
        return device.getDefaultConfiguration().getBounds().width;
    }

    @Override
    public PixelFormat getPreferredPixelFormat(RFBClient client) {
        return PixelFormat.RGB888;
    }

    @Override
    public void keyEvent(RFBClient client, boolean down, int key)
            throws IOException {
        try {
            if (down) {
                robot.keyPress(key);
            } else {
                robot.keyRelease(key);
            }
        } catch (Exception e) {
            System.out.println("Exception at keyboard event in VNCRobot.class");
        }

    }

    @Override
    public void pointerEvent(RFBClient client, int buttonMask, int x, int y)
            throws IOException {

        try {
            robot.mouseMove(x, y);
            if (buttonMask > 0) {
                if (buttonMask == 1 && !is1torelease) {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    is1torelease = true;
                } else if (buttonMask == 2 && !is2torelease) {
                    robot.mousePress(InputEvent.BUTTON2_MASK);
                    is2torelease = true;
                } else if (buttonMask == 4 && !is3torelease) {
                    robot.mousePress(InputEvent.BUTTON3_MASK);
                    is3torelease = true;
                } else if (buttonMask == 8) {
                    robot.mouseWheel(-1);
                } else if (buttonMask == 16) {
                    robot.mouseWheel(1);
                }
            }
            if (buttonMask == 0) {
                if (is1torelease) {
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    is1torelease = false;
                }
                if (is2torelease) {
                    robot.mouseRelease(InputEvent.BUTTON2_MASK);
                    is2torelease = false;
                }
                if (is3torelease) {
                    robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    is3torelease = false;
                }
            }
        } catch (Exception e) {
            System.out.println("An exception at mouse event in VNCRobot.class");
        }

    }

    @Override
    public void removeClient(RFBClient client) {
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private

    @Override
    public void setClientProtocolVersionMsg(RFBClient client,
            String protocolVersionMsg) throws IOException {
    }

    @Override
    public void setEncodings(RFBClient client, int[] encodings)
            throws IOException {
    }

    @Override
    public void setPixelFormat(RFBClient client, PixelFormat pixelFormat)
            throws IOException {
        pixelFormat.setDirectColorModel(
                (DirectColorModel) Toolkit.getDefaultToolkit().getColorModel());
    }

    @Override
    public void setShared(RFBClient client, boolean shared) throws IOException {
    }
}
