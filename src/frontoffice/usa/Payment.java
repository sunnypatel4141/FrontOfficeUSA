
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

package frontoffice.usa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
public class Payment extends salesWindow implements ActionListener, KeyListener {

    private float total;
    private float subtotal;
    private float discount;
    private float taxamount;
    private int qty;
    private int customerid = 0;
    private float customerlimit = 0.00f;
    private boolean customerpayment = false; // When a customer wants to pay off their account
    private Vector saleIDList = new Vector(); // SaleID Vector List

    private Font baseFont = new Font("Verdana", Font.BOLD, 18);
    JPanel mainPanel;
    JDialog paymentDialog = new JDialog(frame, "Payment", true);
    JDialog changeDialog = new JDialog(paymentDialog, "Change", true);
    JPanel numPnl, optsPnl, tablePnl;
    JButton cash, card, voucher, online, account, deleteRow;
    JButton pound5, pound10, pound20, pound50, exactChange, wholeAmount;
    JTextField amountIn, amountToPay, amountPaid, amountTotal;
    String[] cn = {"Type", "Amount"};
    Object[][] data = null;
    DefaultTableModel dtm = new DefaultTableModel(data, cn);
    JTable paymenttable = new JTable(dtm);
    String[] numBtnTxt = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "C", "00"};
    JButton[] numBtn = new JButton[12];
    String scannedbarcode = "";
    float outstandingbalance = 0.00f;

    // we need the Sales Table Data
    Vector SaleData = new Vector();


    /**
     * Default Constructor will only set the totals in the class and not do anything else
     * to show and gui call render()
     */
    public Payment(float totalArg, float subtotalArg, float discountArg, float taxamountArg, int qtyArg) {
        total = totalArg;
        subtotal = subtotalArg;
        discount = discountArg;
        taxamount = taxamountArg;
        qty = qtyArg;
    }

    /**
     * This will only render the dialog box in model mode enabled
     * on the frame of the saleWindow class.
     */
    @Override
    public void render() {
        // Numbers Pad
        numPnl = new JPanel();
        JPanel numPnlIn = new JPanel();
        JPanel curBalancesPnl = new JPanel();
        mainPanel = new JPanel();
        amountTotal = new JTextField(getLikeCurrency(total), 10);
        amountTotal.setFont(baseFont);
        amountToPay = new JTextField(getLikeCurrency(total) , 10);
        amountToPay.setFont(baseFont);
        amountPaid = new JTextField("0.00", 10);
        amountPaid.setFont(baseFont);
        amountPaid.setForeground(Color.GREEN);
        JLabel amountTotalLbl = new JLabel("Total");
        JLabel amountToPayLbl = new JLabel("Balance");
        JLabel amountPaidLbl = new JLabel("Payed");
        curBalancesPnl.add(amountTotalLbl);
        curBalancesPnl.add(amountTotal);
        curBalancesPnl.add(amountToPayLbl);
        curBalancesPnl.add(amountToPay);
        curBalancesPnl.add(amountPaidLbl);
        curBalancesPnl.add(amountPaid);
        curBalancesPnl.setLayout(new GridLayout(3, 2));
        JPanel preDefPnl = new JPanel();
        pound5 = new JButton("$5");
        pound5.setBackground(new Color(248, 148, 6));
        pound5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Object[] row = {"Cash", "500"};
                addRowToTable(row);
            }
        });
        pound5.setFont(h3bold);
        pound10 = new JButton("$10");
        pound10.setBackground(new Color(248, 148, 6));
        pound10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Object[] row = {"Cash", "1000"};
                addRowToTable(row);
            }
        });
        pound10.setFont(h3bold);
        pound20 = new JButton("$20");
        pound20.setBackground(new Color(248, 148, 6));
        pound20.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Object[] row = {"Cash", "2000"};
                addRowToTable(row);
            }
        });
        pound20.setFont(h3bold);
        pound50 = new JButton("$50");
        pound50.setBackground(new Color(248, 148, 6));
        pound50.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Object[] row = {"Cash", "5000"};
                addRowToTable(row);
            }
        });
        pound50.setFont(h3bold);
        exactChange = new JButton("" + total);
        exactChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                float balance = Float.parseFloat(amountToPay.getText()) * 100;
                Object[] row = {"Cash", balance};
                addRowToTable(row);
            }
        });
        exactChange.setBackground(new Color(46, 204, 133));
        wholeAmount = new JButton(getLikeCurrencyWhole(total));
        wholeAmount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                float balance = Float.parseFloat(getLikeCurrencyWhole(total)) * 100;
                Object[] row = {"Cash", balance};
                addRowToTable(row);
            }
        });
        wholeAmount.setBackground(new Color(242, 38, 19));
        wholeAmount.setForeground(Color.WHITE);

        preDefPnl.add(pound50);
        preDefPnl.add(pound20);
        preDefPnl.add(pound10);
        preDefPnl.add(pound5);
        preDefPnl.add(wholeAmount);
        preDefPnl.add(exactChange);
        preDefPnl.setLayout(new GridLayout(1, 6, 5, 5));
        for(int i = 0; i < numBtnTxt.length; i++) {
            final int j = i;
            numBtn[i] = new JButton(numBtnTxt[i]);
            numBtn[i].setBackground(new Color(37, 116, 169));
            numBtn[i].setForeground(Color.WHITE);
            numBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    insertNumber(numBtnTxt[j]);
                }
            });
            numBtn[i].setFont(h1);
            // Add the nutton to the panel
            numPnlIn.add(numBtn[i]);
        }
        // You will never get the time back and so stop waisting it and get on with it
        numPnlIn.setLayout(new GridLayout(4, 3, 5, 5));
        numPnl.setLayout(new BorderLayout());
        numPnl.add(numPnlIn, BorderLayout.CENTER);
        amountIn = new JTextField(10);
        amountIn.setFont(baseFont);
        JPanel numNorthPnl = new JPanel();
        numNorthPnl.add(preDefPnl);
        numNorthPnl.add(amountIn);
        numNorthPnl.setLayout(new GridLayout(2, 1));
        numNorthPnl.setPreferredSize(new Dimension(300, 100));

        numPnl.add(numNorthPnl, BorderLayout.NORTH);
        // Ok now for the rest of the buttons
        optsPnl = new JPanel();
        // Buttons for Payments
        cash = new JButton("Cash", new ImageIcon("Icons/money.png"));
        cash.addActionListener(this);
        cash.setBackground(new Color(37, 116, 169));
        cash.setForeground(Color.WHITE);
        card = new JButton("Card", new ImageIcon("Icons/visa.png"));
        card.addActionListener(this);
        card.setBackground(new Color(37, 116, 169));
        card.setForeground(Color.WHITE);
        voucher = new JButton("Voucher", new ImageIcon("Icons/tag.png"));
        voucher.addActionListener(this);
        voucher.setBackground(new Color(37, 116, 169));
        voucher.setForeground(Color.WHITE);
        online = new JButton("On-Line", new ImageIcon("Icons/wire-transfer.png"));
        online.addActionListener(this);
        online.setBackground(new Color(37, 116, 169));
        online.setForeground(Color.WHITE);
        account = new JButton("Account", new ImageIcon("Icons/clipboard.png"));
        account.addActionListener(this);
        account.setBackground(new Color(37, 116, 169));
        account.setForeground(Color.WHITE);
        optsPnl.add(cash);
        optsPnl.add(card);
        optsPnl.add(voucher);
        optsPnl.add(online);
        optsPnl.add(account);
        // If the customer limit is grater then total or if customer is paying
        if ( customerid == 0 || customerlimit < total || customerpayment ) {
            account.setEnabled(false);
        }
        optsPnl.setLayout(new GridLayout(5, 1, 5, 5));
        optsPnl.setPreferredSize(new Dimension(150, 300));
        // Table Pnl
        tablePnl = new JPanel();
        JScrollPane jsp = new JScrollPane(paymenttable);
        tablePnl.add(jsp);
        deleteRow = new JButton("Delete");
        deleteRow.addActionListener(this);
        tablePnl.add(deleteRow);
        tablePnl.add(curBalancesPnl);
        tablePnl.setLayout(new BoxLayout(tablePnl, BoxLayout.PAGE_AXIS));

        mainPanel.add(tablePnl);
        mainPanel.add(optsPnl);
        mainPanel.add(numPnl);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        jsp.setPreferredSize(new Dimension(250, 200));
        JPanel backBtnPnl = new JPanel();
        JButton backPaymentDialog = new JButton("Back");
        backPaymentDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Just Remove the components within it
                Component[] toremove = paymentDialog.getComponents();
                for(int i = 0; i < toremove.length; i++) {
                    paymentDialog.remove(toremove[i]);
                }
                paymentDialog.dispose();
            }
        });
        backBtnPnl.add(backPaymentDialog);
        backPaymentDialog.setPreferredSize(new Dimension(70, 70));
        paymentDialog.add(mainPanel, BorderLayout.CENTER);
        paymentDialog.add(backBtnPnl, BorderLayout.SOUTH);
        //paymentDialog.add(preDefPnl, BorderLayout.EAST);
        paymentDialog.setSize(1024, 430);
        paymentDialog.setLocation(0, 310);
        paymentDialog.setUndecorated(true);
        paymentDialog.setVisible(true);
    }

    /**
     * @Deprecated please use addSaleData
     */
    @Deprecated
    public void addSaleItem(Vector saleDataArg) {
        // Build a Vector of Sale Data (The sale Table to pass later on)
        SaleData = saleDataArg;
    }

    public void addSaleData(Vector data) {
        SaleData = data;
    }

    // When a customer wants to pay off their account
    public void setCustomer(int customeridArg, Vector saleIDArg) {
        customerid = customeridArg;
        customerpayment = true;
        customerlimit = 0.00f; // This so that account button cannot be triggered
        saleIDList = saleIDArg;
    }

    // When a customer wants to check out on their account
    public void setCustomer(int customeridArg) {
        customerid = customeridArg;
        // Get the customer Info
        Customer c = new Customer(customerid);
        customerlimit = c.getCustomerLimit();
    }

    // Payment GUI Functions
    private void insertNumber(String intArg) {
        // Just add this number to the string
        if ( intArg.equals("C") ) {
            // Ok this is the sign to clear the box
            amountIn.setText("");
        } else {
            String curVal = amountIn.getText();
            String setVal = new StringBuffer().append(curVal).append(intArg).toString();
            amountIn.setText(setVal);
        }
    }

    private void addRowToTable(Object[] arg) {
        // Lets get through the rows
        boolean notfound = true;
        for(int i = 0; i < dtm.getRowCount(); i ++) {
            String type = dtm.getValueAt(i, 0).toString();
            // If a match is found then update it
            if ( type.equals(arg[0].toString()) ) {
                // lets flag that through
                notfound = false;
                // Now lets do the logic
                String currentAmountStr = dtm.getValueAt(i, 1).toString();
                float currentAmountFlt = Float.parseFloat(currentAmountStr);
                float newAmountFlt = Float.parseFloat(arg[1].toString()) / 100;
                float amountToUpdate = currentAmountFlt + newAmountFlt;
                dtm.setValueAt(amountToUpdate, i, 1);
            }
        }
        // Did we find a match?
        if ( notfound ) {
            float penceToPound = Float.parseFloat(arg[1].toString());
            arg[1] = penceToPound / 100;
            dtm.addRow(arg);
        }
        // Calculate Totals Please
        calculateTotals();
        amountIn.setText("");
    }

    private void calculateTotals() {
        float totalbalance = 0.00f; //Total balance of the table so far
        float cashamt = 0.00f;
        float cardamt = 0.00f;
        float voucheramt = 0.00f;
        float onlineamt = 0.00f;
        float accountamt = 0.00f;
        for(int i = 0; i < dtm.getRowCount(); i ++) {
            // Calculate Totals
            String amountStr = dtm.getValueAt(i, 1).toString();
            float amountFlt = Float.parseFloat(amountStr);
            // Increment the balance
            totalbalance += amountFlt;
            String amountType = dtm.getValueAt(i, 0).toString();
            // Build the balances up
            if ( amountType.equals("Cash") ) {
                cashamt += amountFlt;
            } else if ( amountType.equals("Card")) {
                cardamt += amountFlt;
            } else if ( amountType.equals("Voucher")) {
                voucheramt += amountFlt;
            } else if ( amountType.equals("On-Line")) {
                onlineamt += amountFlt;
            } else if ( amountType.equals("Account")) {
                accountamt += amountFlt;
            }
        }
        float totalBalanceToPay = Float.parseFloat(amountTotal.getText());
        amountPaid.setText(getLikeCurrency(totalbalance));
        float totalBalancePayed = Float.parseFloat(amountPaid.getText());
        outstandingbalance = totalBalanceToPay - totalBalancePayed;
        amountToPay.setText(getLikeCurrency(outstandingbalance));
        exactChange.setText(getLikeCurrency(outstandingbalance));
        // OK Balance is payed calc Change
        if ( outstandingbalance <= 0.00f ) {
            Object[] record = {totalBalanceToPay,
                    totalBalancePayed,
                    outstandingbalance,
                    customerid
                    };
            Float[] amounts = {cashamt, cardamt, voucheramt, onlineamt, accountamt,
                totalBalanceToPay, outstandingbalance, taxamount};
            paymentComplete(record, amounts);
        }
    }

    private void paymentComplete(Object[] record, Float[] amounts) {
        // When a customer is making a payment
        if ( customerpayment ) {
            SaleRecord s = new SaleRecord(amounts);
            s.updateSale();
            CustomerPayment cp = new CustomerPayment(customerid);
            for(int i = 0; i < saleIDList.size(); i++) {
                // Mark as paid for the transactions that the customer has paid for
                cp.markAsPaid(saleIDList.get(i).toString(), s.getSaleId());
            }
        } else {
            SaleRecord s = new SaleRecord(SaleData, amounts); // Init the Values
            s.postSale(); // Now Just do Posting of all the values
            // Is there a customer trying to checkout
            if( customerid > 0) {
                // Post this to a customer if a customer is selected
                CustomerPayment cp = new CustomerPayment(customerid);
                cp.postTransaction(s);
            }
        }

        Settings.remove("customerid");
        String drawerType = Settings.get("cashDrawerType").toString();
        String printerName = Settings.get("printer").toString();
        // OK Open the cashDrawer and Show change
        Object[] cdArg = {drawerType, printerName};
        CashDrawerGeneric cd = new CashDrawerGeneric(cdArg);
        cd.openDrawer();
        showChange(amounts);
    }

    public void clearDown() {
        Component[] comp = paymentDialog.getComponents();
        for(int i = 0; i < comp.length; i++) {
            paymentDialog.remove(comp[i]);
        }
        paymentDialog.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object caller = ae.getSource();
        String amountToPayStr = amountToPay.getText();
        // This is created so that it's easy to use
        Float amountToPayFlt = Float.parseFloat(amountToPayStr) * 100;
        if ( caller == cash ) {
            String value = amountIn.getText();
            if ( !value.equals("") ) {
                Object[] row = {"Cash", value};
                addRowToTable(row);
            } else {
                Object[] row = {"Cash", amountToPayFlt};
                addRowToTable(row);
            }
        } else if ( caller == card ) {
            String value = amountIn.getText();
            if ( !value.equals("") ) {
                Object[] row = {"Card", value};
                addRowToTable(row);
            } else {
                Object[] row = {"Card", amountToPayFlt};
                addRowToTable(row);
            }
        } else if ( caller == voucher ) {
            String value = amountIn.getText();
            if ( !value.equals("") ) {
                Object[] row = {"Voucher", value};
                addRowToTable(row);
            } else {
                Object[] row = {"Voucher", amountToPayFlt};
                addRowToTable(row);
            }
        } else if ( caller == online ) {
            String value = amountIn.getText();
            if ( !value.equals("") ) {
                Object[] row = {"On-Line", value};
                addRowToTable(row);
            } else {
                Object[] row = {"On-Line", amountToPayFlt};
                addRowToTable(row);
            }
        } else if ( caller == account ) {
            String value = amountIn.getText();
            // If a value is provided then post that else the totaloutstanding balance
            String finalVal = value.equals("") ? "" + outstandingbalance : value;
            Object[] row = {"Account", finalVal};
            addRowToTable(row);
        } else if ( caller == deleteRow ) {
            try {
                int rowselected = paymenttable.getSelectedRow();
                if ( rowselected == -1 ) {
                    // Remove the last row
                    int rowcount = paymenttable.getRowCount();
                    if ( rowcount > 0 ) {
                        dtm.removeRow(rowcount - 1);
                    }
                } else {
                    // Remove the selected row
                    dtm.removeRow(rowselected);
                }
            } catch(Exception a) {
                a.printStackTrace();
            }
            calculateTotals();
        }
    }

    private void showChange(Float[] amounts) {
        JButton changeOk = new JButton("OK");
        changeOk.setBackground(new Color(38, 166, 91));
        changeOk.setForeground(Color.WHITE);
        changeOk.setFont(large);
        changeOk.addKeyListener(this);
        String changeLblStr = getLikeCurrency((amounts[6] * -1));
        String paidLblStr = getLikeCurrency(amounts[5]);
        JLabel changeLbl = new JLabel("Change: " + changeLblStr);
        changeLbl.setFont(large);
        changeLbl.setForeground(new Color(242, 38, 19));
        JLabel paidLbl = new JLabel("Total: " + paidLblStr);
        paidLbl.setFont(large);
        Float TotalAmount = amounts[0] + amounts[1] + amounts[2] + amounts[3]
                + amounts[4];
        String totalAmtLblStr = getLikeCurrency(TotalAmount);
        JLabel totalAmtLblAmt = new JLabel("Paid Total: " + totalAmtLblStr);
        totalAmtLblAmt.setFont(large);
        changeOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cleanup();
                clearDown();
                closeShowChange();
            }
        });

        // Set the display
        String lineone = getDisplayLine("Total " + paidLblStr, (" " + paidLblStr));
        String linetwo = getDisplayLine("Change " + changeLblStr, (" " + changeLblStr));
        outputToDisplay(lineone, linetwo);

        changeDialog.add(totalAmtLblAmt);
        changeDialog.add(paidLbl);
        changeDialog.add(changeLbl);
        changeDialog.setLayout(new GridLayout(4, 1));
        changeDialog.add(changeOk);
        changeDialog.setSize(1024, 400);
        changeDialog.setLocation(0, 330);
        changeDialog.setUndecorated(true);
        changeDialog.setVisible(true);
    }

    /**
     * Send some data to the customer data
     * This will safely open and close it
     */
    private void outputToDisplay(String lineone, String linetwo) {

        //ldg.updateDisplay(lineone + linetwo);
    }
    /**
     * Get the display line propertly formatted
     * MandatoryArg is someting it must keep
     * and can trim in to the rest of the line
     */
    private String getDisplayLine(String line, String mandatoryArg) {
        // Trim to standards
        if(line.length() > 20 ) {
            // Let the length to trim
            int amountToTrim = line.length() - 20;
            line = line.substring(0, amountToTrim);
            line += mandatoryArg;

        } else if(line.length() < 20) {
            int amountToAdd = 20 - line.length();
            // Hack to stop out mandatory arg form line
            int frontOfLineIndex = line.length() - mandatoryArg.length();
            line = line.substring(0, frontOfLineIndex);
            // Lets just add the spaces
            for(int i = 0; i < amountToAdd; i++) {
                line = new StringBuffer(line).append(" ").toString();
            }

            line += mandatoryArg;
        }

        return line;
    }

    private void closeShowChange() {

        checkFloatBalance();

        Component[] changeElems = changeDialog.getComponents();
        for(int i = 0; i < changeElems.length; i++) {
            changeDialog.remove(changeElems[i]);
        }
        changeDialog.dispose();
    }

    /**
     * Show dialog if the float balance is above limit
     */
    private void checkFloatBalance() {
        float currentBalance = Float.parseFloat(Settings.get("floatamount").toString());
        float floatLimit = Float.parseFloat(Settings.get("floatlimit").toString());

        /** TODO: FIX THIS.. HOLD UNTIL FURHTER NOTICE
        if (currentBalance > floatLimit) {
            JOptionPane.showMessageDialog(changeDialog, "Float limit exceeded\n "
                    + "Please do a float lift soon as possible",
                    "Float Limit Warning", JOptionPane.WARNING_MESSAGE);
        }
        */
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == 10 ) {
            // For Scannes that do a double scan thing
            if(!scannedbarcode.equals("")) {
                // Need to reset Session for the main table here
                cleanup();
                fetchProduct(scannedbarcode);
                scannedbarcode = "";
            }
            closeShowChange();
            clearDown();
        } else {
            // We need to capture every thing
            String character = e.getKeyText(e.getKeyCode());
            scannedbarcode = new StringBuffer(scannedbarcode).append(character).toString();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    void cleanup() {
        super.cleanup();
    }
}
