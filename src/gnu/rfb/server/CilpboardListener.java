package gnu.rfb.server;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;

public class CilpboardListener implements Runnable {

    RFBSocket client;
    Clipboard clipboard;
    String oldtext;
    Transferable transferable;

    public CilpboardListener(RFBSocket client) {
        this.client = client;
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        transferable = clipboard.getContents(null);

    }

    public void run() {
        {
            while (true) {

                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                String data = null;
                transferable = clipboard.getContents(null);
                try {
                    data = (String) transferable
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (data != null && !data.equals(oldtext)) {

                    try {
                        client.writeServerCutText(data);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                    oldtext = data;
                    System.out.println(oldtext);
                }
            }
        }
    }

}
