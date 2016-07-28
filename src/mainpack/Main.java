package mainpack;

import gnu.vnc.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import viewer_swing.java.com.glavsoft.viewer.Viewer;
import mainpack.ServerInfo;
import mainpack.ClientInfo;;

public class Main {

    final static public JFrame frame = new JFrame("Jamal Client & Server");

    public static void main(final String[] args) {
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/mainpack/images/watermelon_jamal_16x16.png")));

        frame.setSize(420, 235);
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 20, 0, 198, 62, 0 };
        gbl_panel.rowHeights = new int[] { 40, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        frame.getContentPane().add(panel);
        
        JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(
                Main.class.getResource("/mainpack/images/client.png")));
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 1;
        gbc_lblNewLabel.gridy = 1;
        panel.add(label1, gbc_lblNewLabel);

        JButton button1 = new JButton("Client");
        button1.setFont(new Font("Dialog", Font.BOLD, 25));
        GridBagConstraints gbc_button1 = new GridBagConstraints();
        gbc_button1.fill = GridBagConstraints.BOTH;
        gbc_button1.insets = new Insets(0, 0, 5, 5);
        gbc_button1.gridx = 2;
        gbc_button1.gridy = 1;
        panel.add(button1, gbc_button1);
        button1.setToolTipText(
                "This will open client, which creates connection to a server.");

        JButton infoButton1 = new JButton();
        infoButton1.setIcon(
                new ImageIcon(Main.class.getResource("/mainpack/images/info.png")));
        infoButton1.setPreferredSize(new Dimension(50, 50));
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton.gridx = 3;
        gbc_btnNewButton.gridy = 1;
        panel.add(infoButton1, gbc_btnNewButton);

        infoButton1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ClientInfo.getClientInfo();
            }
        });

        JButton infoButton2 = new JButton();
        infoButton2.setIcon(
                new ImageIcon(Main.class.getResource("/mainpack/images/info.png")));
        infoButton2.setPreferredSize(new Dimension(50, 50));
        GridBagConstraints gbc_btnNewButton1 = new GridBagConstraints();
        gbc_btnNewButton1.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton1.gridx = 3;
        gbc_btnNewButton1.gridy = 2;
        panel.add(infoButton2, gbc_btnNewButton1);

        infoButton2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ServerInfo.getServerInfo();
            }
        });

        JLabel label2 = new JLabel();
        label2.setIcon(
                new ImageIcon(Main.class.getResource("/mainpack/images/watermelon-icon.png")));
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 2;
        panel.add(label2, gbc_lblNewLabel_1);
        JButton button2 = new JButton("Server");
        button2.setFont(new Font("Dialog", Font.BOLD, 25));
        GridBagConstraints gbc_button2 = new GridBagConstraints();
        gbc_button2.fill = GridBagConstraints.BOTH;
        gbc_button2.insets = new Insets(0, 0, 0, 5);
        gbc_button2.gridx = 2;
        gbc_button2.gridy = 2;
        panel.add(button2, gbc_button2);
        button2.setToolTipText(
                "This will open server setup, where server settings are defined");

        button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.serverMain(args);
                frame.setVisible(false);
            }
        });
        button1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Viewer.viewerMain(args);
                frame.setVisible(false);
            }
        });
        frame.setVisible(true);

    }

}
