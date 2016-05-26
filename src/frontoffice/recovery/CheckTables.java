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
 * @idea This class is typically used when we want to backup a schema or check
 * the health of our schema
 * it hold no logical point at the moment
 */
package frontoffice.recovery;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
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
public class CheckTables implements ActionListener {
    JDialog checkFrame;
    JTextField urlTf, unTf;
    JPasswordField passTf;
    JButton OK, CLOSE, EXPORT;
    
    private String url = "";
    private String un = "";
    private String pw = "";
    
    Connection connthis = null;
    Statement stmtthis = null;
    ResultSet rsthis = null;
    
    public CheckTables(JFrame frameArg) {
        checkFrame = new JDialog(frameArg, "Recovery Of the Database", true);
        urlTf = new JTextField(7);
        JLabel urlLbl = new JLabel("URL");
        unTf = new JTextField(7);
        JLabel unLbl = new JLabel("UserName");
        passTf = new JPasswordField(7);
        JLabel passLbl = new JLabel("Password");
        
        OK = new JButton("HEALTH CHECK");
        OK.addActionListener(this);
        CLOSE = new JButton("CLOSE");
        CLOSE.addActionListener(this);
        EXPORT = new JButton("EXPORT");
        EXPORT.addActionListener(this);
        JPanel btnsPnl = new JPanel();
        JPanel box = new JPanel();
        btnsPnl.add(OK);
        btnsPnl.add(CLOSE);
        btnsPnl.add(EXPORT);
        btnsPnl.setLayout(new GridLayout(1, 3));
        
        JPanel fieldsPnl = new JPanel();
        fieldsPnl.add(urlLbl);
        fieldsPnl.add(urlTf);
        fieldsPnl.add(unLbl);
        fieldsPnl.add(unTf);
        fieldsPnl.add(passLbl);
        fieldsPnl.add(passTf);
        fieldsPnl.setLayout(new GridLayout(3, 2));
        fieldsPnl.setPreferredSize(new Dimension(50,50));
        
        box.add(fieldsPnl);
        box.add(btnsPnl);
        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        checkFrame.add(box);
        checkFrame.setSize(400, 200);
        checkFrame.setLocation(300, 300);
        checkFrame.setVisible(true);
    }
    
    private void checkHealth() {
        // CheckHealth
        try {
            // Check each table
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if(trigger == OK) {
            url = urlTf.getText();
            un = unTf.getText();
            pw = passTf.getText();
            checkHealth();
        } else if ( trigger == CLOSE) {
            cleanup();
        } else if( trigger == EXPORT) {
            url = urlTf.getText();
            un = unTf.getText();
            pw = passTf.getText();
            final JOptionPane exportConfirm = new JOptionPane("DANGER! \n " +
                    "This will OVERWRITE current schema file", 
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            final JDialog dialog = new JDialog(checkFrame, "Export Schema", true);
            dialog.setContentPane(exportConfirm);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            exportConfirm.addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            String prop = e.getPropertyName();
                            if ( dialog.isVisible()
                                && (e.getSource() == exportConfirm)
                                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                dialog.setVisible(false);
                            }
                        }
                    });
            dialog.pack();
            dialog.setVisible(true);
            int value = ((Integer)exportConfirm.getValue()).intValue();
            if ( value == JOptionPane.YES_OPTION ) {
                Object data1[][] = readSchema();
                if(data1 != null) {
                    exportSchema(data1);
                } else {
                    JOptionPane.showMessageDialog(null, "No data to export aborting", 
                            "DATA ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
            
    }
    
    String[][] readSchema() {
         String data[][] = null;
        try {
            String sql = "select count(*) from information_schema.columns " +
                    "where table_schema = database()";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connthis = DriverManager.getConnection(url, un, pw);
            stmtthis = connthis.createStatement();
            rsthis = stmtthis.executeQuery(sql);
            int size = 0;
            while ( rsthis.next() ) {
                size = rsthis.getInt(1);
            }
            data = new String[size][3];
            sql = "select cl.`table_name`, cl.`column_name`, cl.`column_type` from " +
                    "information_schema.columns cl where cl.`table_schema` = 'myepos'";
            rsthis = stmtthis.executeQuery(sql);
            int counter = 0;
            while (rsthis.next()) {
                Object row[] = new Object[3];
                data[counter][0] = rsthis.getString(1);
                data[counter][1] = rsthis.getString(2);
                data[counter][2] = rsthis.getString(3);
                counter++;
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        return data;
    }
    
    void exportSchema(Object[][] data) {
        String file = "schema.csv";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(int i = 0; i < data.length; i++) {
                String one = data[i][0] + ", ";
                String two = data[i][1] + ", ";
                String thr = data[i][2] + ", \n";
                String csvLine = new StringBuffer().append(one).append(two).append(thr).toString();
                bw.write(csvLine);
            }
            bw.flush();
            bw.close();
            JOptionPane.showMessageDialog(null, "Export Complete");
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    void importSchema() {
        
    }
    
    void cleanup() {
        Component[] elems = checkFrame.getComponents();
        for(int i = 0; i < elems.length; i++) {
            checkFrame.remove(elems[i]);
        }
        checkFrame.dispose();
    }
    
}
