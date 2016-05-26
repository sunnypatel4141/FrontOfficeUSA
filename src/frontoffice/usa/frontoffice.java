/*
 * Copyright (C) 2014 Sunny Patel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Sunny Patel
 */

package frontoffice.usa;
import frontoffice.base.DBConnection;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */

public class frontoffice extends DBConnection implements ActionListener, FocusListener {
    // This is the login page
    JFrame frame = new JFrame("System Login");
    JPanel inputFldPnl, numPadPnl, detailPnl, optsPnl;
    String[] numberbtns = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "CLEAR", "OK"};
    JButton[] numbers = new JButton[numberbtns.length];
    JTextField unFld, swpFld;
    JPasswordField pwFld;
    Integer focusfield;
    JButton exitBtn;
    // All the user information
    
    void render() {
        // Render All Panels
        inputFldPnl = new JPanel();
        numPadPnl = new JPanel();
        detailPnl = new JPanel();
        optsPnl = new JPanel();
        exitBtn = new JButton(new ImageIcon("Icons/system-shutdown.png"));
        exitBtn.addActionListener(this);
        JLabel unFldLbl = new JLabel("User Name");
        unFldLbl.setFont(h1);
        unFld = new JTextField(14);
        unFld.setFont(h1);
        unFld.addFocusListener(this);
        JLabel pwFldLbl = new JLabel("Password");
        pwFldLbl.setFont(h1);
        pwFld = new JPasswordField(14);
        pwFld.setFont(h1);
        pwFld.addFocusListener(this);
        
        detailPnl.add(unFldLbl);
        detailPnl.add(unFld);
        detailPnl.add(pwFldLbl);
        detailPnl.add(pwFld);
        detailPnl.setLayout(new GridLayout(2, 2, 5, 5));
        detailPnl.setPreferredSize(new Dimension(200, 75));
        detailPnl.setOpaque(false);
        
        optsPnl.add(exitBtn);
        
        // Numpad Render
        for ( int i = 0; i < numberbtns.length; i++ ) {
            //numbers = new JButton[j];
            numbers[i] = new JButton("" + numberbtns[i]);
            numbers[i].addActionListener(this);
            numbers[i].setFont(h1);
            numPadPnl.add(numbers[i]);
        }
        numbers[10].setIcon(new ImageIcon("Icons/edit-clear.png"));
        numbers[10].setText("");
        numbers[11].setIcon(new ImageIcon("Icons/go-next.png"));
        numPadPnl.setLayout(new GridLayout(4, 3, 5, 5));
        numPadPnl.setPreferredSize(new Dimension(300, 300));
        String address = "";
        try {
            InetAddress addObj = InetAddress.getLocalHost();
            address = "IP: " + addObj.getHostAddress();
        } catch( UnknownHostException a ) {
            address = "IP: Unknown";
        }
        JLabel detailsLbl = new JLabel(address);
        JPanel detailsPnl = new JPanel();
        detailsPnl.add(detailsLbl);
        detailsLbl.setFont(h1);
        JPanel framep = new JPanel();
        BoxLayout bl = new BoxLayout(framep, BoxLayout.PAGE_AXIS);
        
        // Are we in test mode?
        if (Settings.containsKey("TestMode") && Settings.get("TestMode").equals("1")) {
            JLabel testModeLbl = new JLabel("TEST MODE");
            testModeLbl.setFont(large);
            framep.add(testModeLbl);
        }
        
        framep.add(detailsPnl);
        framep.add(detailPnl);
        framep.add(numPadPnl);
        framep.add(optsPnl);
        framep.setLayout(bl);
        framep.setPreferredSize(new Dimension(350, 450));
        
        //Set Opaque on the correct manor
        optsPnl.setOpaque(false);
        framep.setOpaque(false);
        numPadPnl.setOpaque(false);
        detailsPnl.setOpaque(false);
        
        frame.add(framep);
        frame.setLayout(new FlowLayout());
        frame.setSize(1024, 786);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    void clearDown() {
        frame.remove(detailPnl);
        frame.remove(numPadPnl);
        frame.remove(optsPnl);
        frame.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object aeObj = ae.getSource();
        if ( aeObj == exitBtn ) {
            clearDown();
            System.gc();
            System.exit(0);
        }
        // Lets Iterate through the loop
        for(int i = 0; i < numberbtns.length; i++) {
            if ( aeObj.equals(numbers[i]) && !numberbtns[i].equals("CLEAR") && !numberbtns[i].equals("OK") ) {
                // for userName Field
                if ( focusfield == 1 ) {
                    String currentUNStr = unFld.getText();
                    String unStr = new StringBuffer().append(currentUNStr).append(numberbtns[i]).toString();
                    unFld.setText(unStr);
                } else {
                    String currentPWStr = pwFld.getText();
                    String pwStr = new StringBuffer().append(currentPWStr).append(numberbtns[i]).toString();
                    pwFld.setText(pwStr);
                }
                break;
            } else if ( aeObj.equals(numbers[i]) && numberbtns[i].equals("CLEAR") ) {
                if ( focusfield == 1 ) {
                    unFld.setText("");
                } else {
                    pwFld.setText("");
                }
                break;
            } else if ( aeObj.equals(numbers[i]) && numberbtns[i].equals("OK") ) {
                if ( !unFld.getText().equals("") || !pwFld.getText().equals("") ) {
                    // Get the Un and Paswd And do the Authen
                    String unStr = unFld.getText();
                    String pwStr = pwFld.getText();
                    if(authenticate(unStr, pwStr)) {
                        login();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Must enter username and password");
                }
                break;          
            }
        }
    }
    
    public boolean authenticate(String unStr, String pwStr) {
        boolean allow = false;
        try {
            String sql = "select * from users where loginID='" + unStr +
                    "' and loginpass='" + pwStr  +"' and canlogin='1';";
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                Settings.put("username", rs.getString("firstname"));
                Settings.put("userid", rs.getString("id"));
            }
            rs.close();
            if ( Settings.get("username") == null ) {
                JOptionPane.showMessageDialog(frame, "Invalid user credentials", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Typically all the rights should be loaded for the user
                sql = "select r from applicationright where userid='" + 
                        Settings.get("userid").toString() + "' and apid = " +
                        "(select id from application where code = 'saleswindow')";
                rs = stmt.executeQuery(sql);
                while ( rs.next() ) {
                    Settings.put("saleswindow", rs.getString("r"));
                }
                rs.close();
                if ( Settings.get("saleswindow") == null ) {
                    JOptionPane.showMessageDialog(frame, "Access Denied", 
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
                } else {
                    allow = true;
                }
            }
        } catch ( Exception a ) {
            JOptionPane.showMessageDialog(frame, "Unable to Connect", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            a.printStackTrace();
        }
        return allow;
    }
    
    void login() {
        clearDown();
        // Login to the main application
        setUpSession();
        loadSystemPrefs();
        loadPeripherals();
        salesWindow sw = new salesWindow();
        sw.render();
    }
    
    private void setUpSession() {
        try {
            // Set the float here
            int userid = Integer.parseInt(Settings.get("userid").toString());
            String register = Settings.get("tillNumber").toString();
            int floatid = 0;
            // Check is a float for today has been set
            String sql = "select * from cashdrawer where userid ='" + userid +
                    "' and register='" + register + "' and created=curdate()";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Get the results
                floatid = rs.getInt("id");
            }
            // get the opening Balance of the last time this was active
            sql = "select * from cashdrawer where userid ='" + userid +
                    "' and register='" + register + "' and created != curdate() " +
                    "order by created desc limit 1";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Set the balance that was left yesterday
                Settings.put("floatamountopening", rs.getString("amount"));
            }
            // if this is a new instance then we need to set it to zero
            if(!Settings.containsKey("floatamountopening")) {
                Settings.put("floatamountopening", "0.00");
            }
            
            if( floatid == 0 ) {
                // If float does not exist set it
                String yesterdaybalance = Settings.get("floatamountopening").toString();
                sql = "insert into cashdrawer (register, amount, userid, created) " +
                        "values ('" + register + "', '" + yesterdaybalance + "', '" + 
                        userid + "', CURDATE())";
                int success = stmt.executeUpdate(sql);
            }
            // Cashdrawer ENSURE VALUES ARE SET
            sql = "select * from cashdrawer where userid ='" + userid +
                    "' and register='" + register + "' and created=curdate()";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Now set the cashdrawer amounts
                Settings.put("floatamount", rs.getString("amount"));
                Settings.put("floatid", rs.getString("id"));
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    // Load some preferences
    private void loadSystemPrefs() {
        // Lets load all the system preferences
        try {
            String sql = "select * from systempref";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Settings.put(rs.getString("ukey"), rs.getString("uvalue"));
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    @Override
    public void focusGained(FocusEvent fe) {
        if ( fe.getSource() == unFld ) {
            focusfield = 1;
        } else {
            focusfield = 2;
        }
    }

    @Override
    public void focusLost(FocusEvent fe) {
    }
    /*
    public static void main(String[] args) {
        frontoffice fo = new frontoffice();
        fo.render();
    }
    */
}