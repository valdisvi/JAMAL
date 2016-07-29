package mainpack;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

import gnu.vnc.GUI;

import java.awt.event.*;

public class AllowedIpList extends JFrame implements ActionListener {

    JList l1;
    JButton b2, b3;
    int ret;
    public static DefaultListModel listedIPs;

    public AllowedIpList() {
        setTitle("Allowed IP list");
        setBounds(100, 100, 460, 420);
        listedIPs = new DefaultListModel();

//creating J List
        l1 = new JList(listedIPs);
        b2 = new JButton("Remove");
        b2.setBounds(171, 12, 91, 25);
        b2.addActionListener(this);
        b2.setActionCommand("remove");
        b3 = new JButton("Clear All");
        b3.setBounds(171, 161, 92, 25);
        b3.addActionListener(this);
        b3.setActionCommand("clear");
        getContentPane().setLayout(null);
//adding jlist to a scrollpane
        JScrollPane js = new JScrollPane(l1);
        js.setBounds(9, 5, 150, 200);
        js.setPreferredSize(new Dimension(300, 200));
        getContentPane().add(js);
        getContentPane().add(b2);
        getContentPane().add(b3);
        setSize(300, 280);
        setVisible(false);
    }

    public void actionPerformed(ActionEvent ae) {
        if ("clear".equals(ae.getActionCommand())) {
            listedIPs.removeAllElements();

        }

        else if ("remove".equals(ae.getActionCommand())) {
            ret = l1.getSelectedIndex();
            listedIPs.remove(ret);
        } 
    }

}