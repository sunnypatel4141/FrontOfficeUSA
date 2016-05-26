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
 * @idea
 * The purpose of this class is to allow the owner to take in to account
 * the amounts that this register has taken such as. cards, vouchers, accounts, 
 * and so on... This class will post all this in the endofday table and
 * the cash amounts in the endofadaydetail table.
 * this way we can keep track of who did end of day (checked everything) and 
 * what the amounts were.
 * Once that is done it will print a receipt showing the amounts posted.
 * there will/might be an option to update the float with the cash lift.
*/
package frontoffice.usa;

import frontoffice.event.NumberPadEvent;
import frontoffice.util.NumberPad;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * End Of Day is intended to allow float balancing.
 * When end of day is posted it will balance the float as well.
 * End of day has it's own database table that will keep track 
 * of amounts posted this will be useful for audits
 * 
 * @author Sunny Patel
 */
public class EndOfDay extends MainMenu implements NumberPadEvent, 
        MouseListener, ActionListener, TableModelListener {
    
    JDialog frameeod = new JDialog(frame, "End Of Day", true);
    Object[] cneod = {"Type", "Count"};
    Object[][] dataeod = {
        {"50", "0"},
        {"20", "0"},
        {"10", "0"},
        {"5", "0"},
        {"2", "0"},
        {"1", "0"},
        {"0.50", "0"},
        {"0.20", "0"},
        {"0.10", "0"},
        {"0.05", "0"},
        {"0.02", "0"},
        {"0.01", "0"},
    };
    
    // The payment Types
    String cashStr, cardStr, onlineStr, voucherStr, accountStr;
    DefaultTableModel dtmeod = new DefaultTableModel(dataeod, cneod);
    JTable tableeod = new JTable(dtmeod);
    JTextField cashFld, cardFld, onlineFld, voucherFld,  accountFld, totalCashFld;
    JButton saveeod, closeeod;
    JCheckBox cardok, onlineok, voucherok, accountok;
    int tablerowmapper = 0;
    
    int endOfDayID = 0;
    
    /**
     * Displays the end of day dialog and loads the amounts
     */
    public EndOfDay() {
        renderendofday();
    }
    
    /**
     * When we want to just update the end of day without entering any 
     * information in to the GUI
     * @param String callerClass
     */
    public EndOfDay(String callerClass) {
        // don't do anything
    }
    
    private void renderendofday() {
        // Render End Of Day
        JPanel tblPnl = new JPanel();
        JPanel infoFld = new JPanel();
        JPanel btmPnl = new JPanel();
        JPanel totalPnl = new JPanel();
        tableeod.addMouseListener(this);
        dtmeod.addTableModelListener(this);
        
        saveeod = new JButton("Save");
        saveeod.addActionListener(this);
        closeeod = new JButton("Close");
        closeeod.addActionListener(this);
        
        btmPnl.add(saveeod);
        btmPnl.add(closeeod);
        
        // JTextField For All the Info
        cashFld = new JTextField("0.00", 7);
        cashFld.setFont(large);
        cardFld = new JTextField("0.00", 7);
        cardFld.setFont(large);
        onlineFld = new JTextField("0.00", 7);
        onlineFld.setFont(large);
        voucherFld = new JTextField("0.00", 7);
        voucherFld.setFont(large);
        accountFld = new JTextField("0.00", 7);
        accountFld.setFont(large);
        totalCashFld = new JTextField("0.00", 7);
        totalCashFld.setFont(large);
        
        JLabel cashLbl = new JLabel("Cash");
        JLabel cardLbl = new JLabel("Card");
        JLabel onlineLbl = new JLabel("Online");
        JLabel voucherLbl = new JLabel("Voucher");
        JLabel accountLbl = new JLabel("Account");
        JLabel tickLbl = new JLabel("Amount OK");
        JLabel totalCashLbl = new JLabel("Total Cash");
        
        cardok = new JCheckBox();
        onlineok = new JCheckBox();
        voucherok = new JCheckBox();
        accountok = new JCheckBox();
        
        infoFld.add(cashLbl);
        infoFld.add(cashFld);
        infoFld.add(tickLbl);
        infoFld.add(cardLbl);
        infoFld.add(cardFld);
        infoFld.add(cardok);
        infoFld.add(onlineLbl);
        infoFld.add(onlineFld);
        infoFld.add(onlineok);
        infoFld.add(voucherLbl);
        infoFld.add(voucherFld);
        infoFld.add(voucherok);
        infoFld.add(accountLbl);
        infoFld.add(accountFld);
        infoFld.add(accountok);
        infoFld.setLayout(new GridLayout(5, 3));
        
        JScrollPane jspeod = new JScrollPane(tableeod);
        tableeod.setFont(h3);
        jspeod.setPreferredSize(new Dimension(300, 220));
        tblPnl.add(jspeod);
        
        totalPnl.add(totalCashLbl);
        totalPnl.add(totalCashFld);
        totalPnl.setLayout(new GridLayout(2, 1));
        
        frameeod.add(infoFld);
        frameeod.add(tblPnl);
        frameeod.add(totalPnl);
        frameeod.add(btmPnl);
        
        loadAmounts();
        addAmountsToTable();
        
        frameeod.setLayout(new FlowLayout());
        frameeod.setLocation(200, 100);
        frameeod.setSize(650, 550);
        frameeod.setVisible(true);
    }
    
    /**
     * Adds the values to the GUI
    */
    private void addAmountsToTable() {
        cashFld.setText(cashStr);
        cardFld.setText(cardStr);
        onlineFld.setText(onlineStr);
        voucherFld.setText(voucherStr);
        accountFld.setText(accountStr);
        // Lock the fields
        cashFld.setEnabled(false);
        cardFld.setEnabled(false);
        onlineFld.setEnabled(false);
        voucherFld.setEnabled(false);
        accountFld.setEnabled(false);
    }
    
    /**
     * We need to load amounts from the db all the things that happened in sale
     */
    private void loadAmounts() {
         // Load Amounts since last update or sicne epoch
        try {
            String sql = "select sum(cash), sum(card), sum(voucher), " +
                "sum(online), sum(account) from sale where `created` between " +
                "ifnull((select `created` from endofdaydetail order by `created` " +
                    "desc limit 1 ) , '1970-01-01') and curDate();";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // lets get all the things
                cashStr = rs.getString(1);
                cardStr = rs.getString(2);
                onlineStr = rs.getString(3);
                voucherStr = rs.getString(4);
                accountStr = rs.getString(5);
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Update the end of day table
     */
    private void updateendofday() {
        // We need to create an end of day id
        int endofdayid = getEndOfDayID();
        try {
            // Set the amounts in to endofday table
            String sql = "insert into endofdaydetail (`type`, `amount`, `amountcount`, " +
                    "`eodid`, `createdby`) values(?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            for(int i = 0; i < tableeod.getRowCount(); i++) {
                float amountFlt = Float.parseFloat(dtmeod.getValueAt(i, 0).toString());
                int amountcount = Integer.parseInt(dtmeod.getValueAt(i, 1).toString());
                float totalAmount = amountFlt * amountcount;
                pstmt.setFloat(1, amountFlt);
                pstmt.setFloat(2, totalAmount);
                pstmt.setInt(3, amountcount);
                pstmt.setInt(4, endofdayid);
                pstmt.setString(5, Settings.get("userid").toString());
                pstmt.execute();
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    private Integer getEndOfDayID() {
        // If there is no ID then create it
        if(endOfDayID == 0) {
            // End Of Day Balance
            try {
                String sql = "insert into endofday (`cash`, `card`, `online`, "
                        + "`voucher`, `account`, `cardcheck`, `onlinecheck`, "
                        + "`vouchercheck`, `accountcheck`) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                //cashFld, cardFld, onlineFld, voucherFld,  accountFld
                //cardok, onlineok, voucherok, accountok
                pstmt.setString(1, cashFld.getText());
                pstmt.setString(2, cardFld.getText());
                pstmt.setString(3, onlineFld.getText());
                pstmt.setString(4, voucherFld.getText());
                pstmt.setString(5, accountFld.getText());
                pstmt.setString(6, cardok.getText());
                pstmt.setString(7, onlineok.getText());
                pstmt.setString(8, voucherok.getText());
                pstmt.setString(9, accountok.getText());
                pstmt.execute();
                
                // Get the end of day id
                sql = "select last_insert_id()";
                rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    endOfDayID = rs.getInt(1);
                }
            } catch(Exception a) {
                a.printStackTrace();
            }
        }
        return endOfDayID;
    }
    
    private void cleanupeod() {
        frameeod.dispose();
    }
    
    /**
     * Updates the GUI got the end of day after an entry has been made
     */
    private void updateEODTotals() {
        // Update EOD
        float totalBalance = 0.00f;
        for(int i = 0; i < tableeod.getRowCount(); i++) {
            // Table END OF DAY
            float amountFlt = Float.parseFloat(dtmeod.getValueAt(i, 0).toString());
            int amountcount = Integer.parseInt(dtmeod.getValueAt(i, 1).toString());
            // Calculate amount and qty
            totalBalance += (amountFlt * amountcount);
        }
        // Now set the total
        String totalBalanceStr = "" + totalBalance;
        totalCashFld.setText(getCurrency(totalBalanceStr));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if(trigger == saveeod){
            // We need to also update the float
            Object[] options = {"Yes", "No, Just END OF DAY", "Cancel"};
            JOptionPane floatMsg = new JOptionPane();
            floatMsg.createDialog("Float Settle");
            int decision = floatMsg.showOptionDialog(frameeod, 
                    "Do you want to update the float?", "Update Float", 
                    JOptionPane.YES_NO_CANCEL_OPTION, 3, null, options, null);
            // Yes
            if( decision == 0 ) {
                FloatSettings fs = new FloatSettings();
            // No
            } else if( decision == 1 ) {
                updateendofday();
            // Cancel    
            } else {
                // Else
                JOptionPane.showMessageDialog(frameeod, "Changes Thrown Away");
            }
            cleanupeod();
        } else if(trigger == closeeod) {
            // Close eod
            cleanupeod();
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent tme) {
        // Table Changed
        updateEODTotals();
    }
    
    @Override
    public void numberPadEvent(String returnArg) {
        // Lets set the item here
        dtmeod.setValueAt(returnArg, tablerowmapper, 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        tablerowmapper = tableeod.getSelectedRow();
        NumberPad np = new NumberPad(this, frameeod);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
