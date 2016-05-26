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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
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
//*********************THIS NEEDS TO BE FIXED************************
public class Customer extends salesWindow implements ActionListener {
    private int customerid = 0;
    private String customername = "";
    private String customerpostcode = "";
    private float customerlimit = 0.00f;

    JTextField nameFld, postcodeFld, idFld;
    int customerCount = 101;
    JButton[] customer = new JButton[customerCount];
    JDialog frameCustomer = new JDialog(frame, "Customer", true);

    Object[] cnCustomer = {"Sale ID", "Amount", "Pay"};
    Object[][] dataCustomer = null;
    DefaultTableModel dtmCustomer = new DefaultTableModel(dataCustomer, cnCustomer);
    JTable tableCustomer = new JTable(dtmCustomer);
    
    // Customer Info 
    JDialog frameCustomerInfo = new JDialog(frameCustomer, "Customer Info", true);
    JButton paymentCustomer, searchCustomer, updateCustomer, closeCustomer, selectCustomer;
    
    // Customer Record
    JDialog customerRecordFrame;
    JTextField customerIDFld, firstNameFld, lastNameFld, add1Fld, add2Fld, 
            postcodeCRFld, phone1Fld, mobileFld, limitFld;
    /**
    * This is for viewing all the customers
    */
    public Customer() {
            renderCustomers();
    }

    /**
    * This is for loading a customers object
    */
    public Customer(int idArg) {
        customerid = idArg;
        loadCustomer();
    }

    public int getID() {
            return customerid;
    }

    public String getCustomerName() {
            return customername;
    }

    public float getCustomerLimit() {
            return customerlimit;
    }

    private void loadCustomer() {
        try {
            String sql = "select * from customer where id='" + customerid + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                customername = rs.getString("first_name") + " " + rs.getString("last_name");
                customerpostcode = rs.getString("postcode");
                customerlimit = rs.getFloat("limit");
            }
        } catch (Exception a) {
                a.printStackTrace();
        }
    }
    
    private void loadCustomerInfo() {
        try {
            JPanel customerInfoPnl = new JPanel();
            JPanel customerBtnPnl = new JPanel();

            paymentCustomer = new JButton("Payments");
            updateCustomer = new JButton("Update");
            closeCustomer = new JButton("Close");
            selectCustomer = new JButton("Select");

            paymentCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    
                    customerPayment();
                }
            });
            closeCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                        customerInfoCleanup();
                }
            });
            updateCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    customerUpdateWindow();
                }
            });
            selectCustomer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Settings.put("customerid", customerid);
                    customerNameLbl.setText(customername);
                    frameCustomer.dispose();
                }
            });
            
            // The transactions that the client has made
            CustomerPayment cp = new CustomerPayment(customerid);
            Vector columnName = new Vector();
            columnName.addElement("Sale ID");
            columnName.addElement("Amount");
            columnName.addElement("Paid");
            dtmCustomer.setDataVector(cp.customerOutstandingPayments(), columnName);
            
            JScrollPane jspCustomerInfo = new JScrollPane(tableCustomer);
            
            customerBtnPnl.add(paymentCustomer);
            customerBtnPnl.add(updateCustomer);
            customerBtnPnl.add(closeCustomer);
            customerBtnPnl.add(selectCustomer);

            JLabel custNameLbl = new JLabel("Name: " + customername);
            JLabel custPCLbl = new JLabel("Post Code: " + customerpostcode);
            JLabel custLimitLbl = new JLabel("Total Limit: " + customerlimit);
            Float balanceToDisp = customerlimit - cp.customerBalanceOutstanding();
            JLabel custBalLbl = new JLabel("Credit Remaining: " + balanceToDisp);
            JLabel custSpentLbl = new JLabel("Amount on credit: " + cp.customerBalanceOutstanding());

            customerInfoPnl.add(custNameLbl);
            customerInfoPnl.add(custPCLbl);
            customerInfoPnl.add(custLimitLbl);
            customerInfoPnl.add(custBalLbl);
            customerInfoPnl.add(custSpentLbl);
            customerInfoPnl.setLayout(new GridLayout(5, 1));

            customerBtnPnl.add(paymentCustomer);
            customerBtnPnl.add(updateCustomer);
            customerBtnPnl.add(closeCustomer);

            frameCustomerInfo.add(customerInfoPnl);
            frameCustomerInfo.add(jspCustomerInfo);
            frameCustomerInfo.add(customerBtnPnl);

            frameCustomerInfo.setLayout(new GridLayout(3, 1));

            frameCustomerInfo.setSize(701, 701);
            frameCustomerInfo.setLocation(150, 20);
            frameCustomerInfo.setVisible(true); 
        } catch(Exception a) {
            a.printStackTrace();
        }
    }

    private void customerInfoCleanup() {
        frameCustomerInfo.setVisible(false);
    }
    
    private void customerUpdateWindow() {
        renderCustomerRecord();
        loadCustomerRecord();
        customerRecordFrame.setVisible(true);
    }
    
    private void customerPayment() {
        
        CustomerPayment cp = new CustomerPayment(customerid); // Set the customer Payment Object
        Vector saleIDList = new Vector();
        float totalAmount = 0.00f;
        for(int i = 0; i < tableCustomer.getRowCount(); i++){
            String payTrue = dtmCustomer.getValueAt(i, 2).toString();
            String saleID = dtmCustomer.getValueAt(i, 0).toString();
            // Some form of checkbox check 
            if ( !payTrue.equals("")) {
                saleIDList.add(saleID);
                String amtStr = dtmCustomer.getValueAt(i, 1).toString();
                float totalAmountFlt = Float.parseFloat(amtStr);
                totalAmount += totalAmountFlt;
            }
        }
        
        Payment p = new Payment(totalAmount, totalAmount, new Float(0.00f), 0.00f, new Integer(1));
        p.setCustomer(customerid, saleIDList);
        p.render();
        frameCustomer.dispose(); // Remove screen and let payment handle everything
    }
    
    private void renderCustomers() {
        JPanel btnsPnlCustomer = new JPanel();

        try {
            String sql = "select * from customer";
            rs = stmt.executeQuery(sql);
            int counter = 0; // We already have one customer in array
            while(rs.next()) {
                final int thisCustID = rs.getInt("id");
                String custname = "<html><center>" + rs.getString("first_name") + 
                        "<br>" + rs.getString("last_name") + 
                        "<br>(" + thisCustID + ")" +
                        "<br>(" + rs.getString("postcode") + ")</center></html>";
                customer[counter] = new JButton(custname);
                customer[counter].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        selectCustomer(thisCustID);
                    }
                });
                btnsPnlCustomer.add(customer[counter]);
                counter++;
            }
            btnsPnlCustomer.setLayout(new GridLayout(4, 4));
        } catch (Exception a) {
            a.printStackTrace();
        }
       
        JButton closeFrameCustomer = new JButton("Close");
        JButton newCustomer = new JButton("<html>Add New<br>Customer</html>");
        closeFrameCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                    frameCustomer.dispose();
            }
        });
        newCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

            }
        });
        JScrollPane jspCust = new JScrollPane(btnsPnlCustomer);
        jspCust.setPreferredSize(new Dimension(650, 550));
        JPanel optBtnsPnl = new JPanel();
        optBtnsPnl.add(closeFrameCustomer);
        optBtnsPnl.add(newCustomer);
        
        frameCustomer.add(jspCust);
        frameCustomer.add(optBtnsPnl);
        frameCustomer.setLayout(new FlowLayout());
        frameCustomer.setSize(700, 700);
        frameCustomer.setLocation(150, 20);
        frameCustomer.setVisible(true);
        
    }
    
    private void selectCustomer(int custIDArg) {
        customerid = custIDArg; // Set the customer id
        loadCustomer(); // Convert this class in to a customer accessor
        loadCustomerInfo();
        frameCustomer.dispose();
    }      

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
    private void renderCustomerRecord() {
        customerRecordFrame = new JDialog(frameCustomerInfo, "Customer", true);
        JPanel dataPnl = new JPanel();
        JPanel btnPnl = new JPanel();
        
        JLabel idLbl = new JLabel("Customer ID");
        JLabel firstNameLbl = new JLabel("First Name");
        JLabel lastNameLbl = new JLabel("Last Name");
        JLabel add1Lbl = new JLabel("Address 1");
        JLabel add2Lbl = new JLabel("Address 1");
        JLabel postcodeLbl = new JLabel("Post Code");
        JLabel phoneLbl = new JLabel("Phone");
        JLabel mobileLbl = new JLabel("Mobile");
        JLabel limitLbl = new JLabel("Limit");
        
        customerIDFld = new JTextField(7);
        firstNameFld = new JTextField(7);
        lastNameFld = new JTextField(7);
        add1Fld = new JTextField(7);
        add2Fld = new JTextField(7); 
        postcodeCRFld = new JTextField(7); 
        phone1Fld = new JTextField(7);
        mobileFld = new JTextField(7);
        limitFld = new JTextField(7);
        
        dataPnl.add(idLbl);
        dataPnl.add(customerIDFld);
        dataPnl.add(firstNameLbl);
        dataPnl.add(firstNameFld);
        dataPnl.add(lastNameLbl);
        dataPnl.add(lastNameFld);
        dataPnl.add(add1Lbl);
        dataPnl.add(add1Fld);
        dataPnl.add(add2Lbl);
        dataPnl.add(add2Fld);
        dataPnl.add(postcodeLbl);
        dataPnl.add(postcodeCRFld);
        dataPnl.add(phoneLbl);
        dataPnl.add(phone1Fld);
        dataPnl.add(mobileLbl);
        dataPnl.add(mobileFld);
        dataPnl.add(limitLbl);
        dataPnl.add(limitFld);
        
        dataPnl.setLayout(new GridLayout(9, 2));
        
        JButton closeCustRec = new JButton("Close");
        JButton updateCustRec = new JButton("Save");
        closeCustRec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cleanupCustomerRec();
            }
        });
        
        updateCustRec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateCustomer();
                cleanupCustomerRec();
            }
        });
        
        btnPnl.add(closeCustRec);
        btnPnl.add(updateCustRec);
        
        customerRecordFrame.add(dataPnl);
        customerRecordFrame.add(btnPnl);
        customerRecordFrame.setLayout(new FlowLayout());
        customerRecordFrame.setLocation(150, 20);
        customerRecordFrame.setSize(700, 700);
    }
    
    private void cleanupCustomerRec() {
        Component[] comp = customerRecordFrame.getComponents();
        for(int i = 0; i < comp.length; i ++) {
            customerRecordFrame.remove(comp[i]);
        }
        
        customerRecordFrame.setVisible(false);
    }
    
    private void loadCustomerRecord() {
        try {
            String sql = "select * from customer where id='" + customerid + "'";
            customerIDFld.setText("" + customerid);
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                firstNameFld.setText(rs.getString("first_name"));
                lastNameFld.setText(rs.getString("last_name"));
                add1Fld.setText(rs.getString("address1"));
                add2Fld.setText(rs.getString("address2")); 
                postcodeCRFld.setText(rs.getString("postcode")); 
                phone1Fld.setText(rs.getString("phone1"));
                mobileFld.setText(rs.getString("mobile"));
                limitFld.setText(rs.getString("limit"));
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }
    
    private void updateCustomer() {
        try {
            // New Customer
            if (customerid == 0) {
                String sql = "insert into customer (`first_name`, " +
                        "`last_name`, `address1`, `address2`, `postcode`, " +
                        "`phone1`, `mobile`, `limit` ) values(" +
                        "?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, firstNameFld.getText());
                pstmt.setString(2, lastNameFld.getText());
                pstmt.setString(3, add1Fld.getText());
                pstmt.setString(4, add2Fld.getText());
                pstmt.setString(5, postcodeCRFld.getText());
                pstmt.setString(6, phone1Fld.getText());
                pstmt.setString(7, mobileFld.getText());
                pstmt.setString(8, limitFld.getText());
                pstmt.execute();
            // Existhing Customer 
            } else {
                String sql = "update customer set `first_name` = ? , `last_name` = ?, " +
                        "`address1` = ?, `address2` = ?, `postcode` = ?, `phone1` = ?, " +
                        "`mobile` = ?, `limit` = ? where `id` = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, firstNameFld.getText());
                pstmt.setString(2, lastNameFld.getText());
                pstmt.setString(3, add1Fld.getText());
                pstmt.setString(4, add2Fld.getText());
                pstmt.setString(5, postcodeCRFld.getText());
                pstmt.setString(6, phone1Fld.getText());
                pstmt.setString(7, mobileFld.getText());
                pstmt.setString(8, limitFld.getText());
                pstmt.setInt(9, customerid);
                pstmt.execute();
            }
            JOptionPane.showMessageDialog(customerRecordFrame, "Update Successful", "Customer Update", JOptionPane.INFORMATION_MESSAGE);
        } catch( Exception a) {
            a.printStackTrace();
            JOptionPane.showMessageDialog(customerRecordFrame, "Update Error", "Customer", JOptionPane.ERROR_MESSAGE);
        }
    }
}
