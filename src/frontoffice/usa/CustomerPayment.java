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

import java.sql.PreparedStatement;
import java.util.Vector;
import javax.swing.JDialog;

/**
 *
 * @author Sunny Patel
 */
class CustomerPayment extends salesWindow {
    private int cid = 0;
    Vector outstandingPayments = new Vector();
    JDialog customerPaymentDialog;
    private Float outstangindBalance = 0.00f;

    public CustomerPayment(int cidarg){
        cid = cidarg;
        loadCustomerPayment();
    }
    
    private void loadCustomerPayment() {
    
        try {
            String sql = "select * from customerpayment where cuid='" + cid + "' and paid ='0'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Vector row = new Vector();
                row.addElement(rs.getString("saleid"));
                row.addElement(rs.getFloat("amount"));
                row.addElement(rs.getString("paid"));
                outstandingPayments.add(row);
            }
            sql = "select sum(amount) from customerpayment where cuid='" + cid + "' and paid='0'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                outstangindBalance = rs.getFloat(1);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public Vector customerOutstandingPayments() {
        return outstandingPayments;
    }
    
    public Float customerBalanceOutstanding() {
        
        return outstangindBalance;
    }
    
    public Vector getTransaction(String transactionID) {
        Vector transaction = new Vector();
        try {	
            String sql = "select * from saleitem where saleid = '" + transactionID + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Vector row = new Vector();
                row.add(rs.getString(3));
                row.add(rs.getString(4));
                row.add(rs.getString(5));
                row.add(rs.getString(6));
                row.add(rs.getString(7));
                row.add(rs.getString(8));
                transaction.add(row);
            }
        } catch(Exception a) {
                a.printStackTrace();
        }

        return transaction;
    }

    public void postTransaction(SaleRecord srArg) {
        // work out if transaction is paid or not
        Float amountArg = srArg.get("account");
        int paidArg = amountArg <= 0.00f ? 1 : 0;
        try {
            String sql = "insert into customerpayment (cuid, saleid, amount, paid) " +	
                    "values(?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cid);
            pstmt.setObject(2, srArg.getSaleId());
            pstmt.setFloat(3, amountArg);
            pstmt.setInt(4, paidArg);
            pstmt.execute();
        } catch(Exception a) {
            a.printStackTrace();
        }
    }

    public void markAsPaid(String paymentID, String newSaleID) {
        try {
            String sql = "update customerpayment set paid='1', saleidpaid='" +
                newSaleID + "' where saleid='" + paymentID + "'";
            int success = stmt.executeUpdate(sql);
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
		
}

