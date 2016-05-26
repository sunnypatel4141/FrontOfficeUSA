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

/**
 *
 * @author Sunny Patel
 */
class SaleRecord extends salesWindow {
    
    private float cash = 0.00f;
    private float card = 0.00f;
    private float voucher = 0.00f;
    private float online = 0.00f;
    private float account = 0.00f;
    private float amounttopay = 0.00f;
    private float change = 0.00f;
    
    private String SaleID = "";
    
    Vector SaleData = new Vector();
    Float SaleInfo[] = new Float[7];
    
    public SaleRecord(Vector SaleDataArg, Float[] saleInfoArg) {
        // Generate a Sale ID
        SaleID = createSale();
        SaleData = SaleDataArg;
        SaleInfo = saleInfoArg;
        cash = SaleInfo[0];
        card = SaleInfo[1];
        voucher = SaleInfo[2];
        online = SaleInfo[3];
        account = SaleInfo[4];
        amounttopay = SaleInfo[5];
        change = SaleInfo[6];
    }
    
    // Load a sale for mainupulation
    public SaleRecord(String SaleIDArg) {
        SaleID = SaleIDArg;
        getSaleInfo(); // loads this class with the sale info
    }
    
    // Just wants to record a payment without reason
    public SaleRecord(Float[] amountsArg) {
        SaleID = createSale();
        SaleInfo = amountsArg;
    }
    
    public void postSale() {
        // Add all the products of the sale
        addSaleItem();
        // update the sale with the amounts and change
        updateSale();
    }
    
    public String getSaleId() {
        return SaleID;
    }
     
    public Float get(String typeArg) {
        Float returnVal = 0.00f;
        if(typeArg.equals("cash")) {
            returnVal = cash;
        } else if (typeArg.equals("card")) {
            returnVal = card;
        } else if (typeArg.equals("voucher")) {
            returnVal = voucher;
        } else if (typeArg.equals("online")) {
            returnVal = online;
        } else if (typeArg.equals("account")) {
            returnVal = account;
        } else if (typeArg.equals("amountopay")) {
            returnVal = amounttopay;
        } else if (typeArg.equals("change")) {
            returnVal = change;
        }
        
        return returnVal;
    }
    
    public Vector getSaleItem() {
        return SaleData;
    }
    
    public void updateSale() {
        try {
            String sql = "update `sale` set `cash` = ?, `card` = ?, `voucher` = ?," +
             " `online` = ?, `account` = ?, `amounttopay` = ?, `change` = ?, `taxamount` = ? " +
             "where `id` = ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql); 
            pstmt.setFloat(1, SaleInfo[0]);
            pstmt.setFloat(2, SaleInfo[1]);
            pstmt.setFloat(3, SaleInfo[2]);
            pstmt.setFloat(4, SaleInfo[3]);
            pstmt.setFloat(5, SaleInfo[4]);
            pstmt.setFloat(6, SaleInfo[5]);
            pstmt.setFloat(7, SaleInfo[6]);
            pstmt.setFloat(8, SaleInfo[7]);
            pstmt.setString(9, SaleID);
            pstmt.execute();
            
            // We also want to add data to the float table
            Float amount = Float.parseFloat(Settings.get("floatamount").toString());
            amount += Float.parseFloat(SaleInfo[5].toString());
            sql = "update cashdrawer set amount='" + amount + "' where id = '" + 
                    Settings.get("floatid") + "'";
            
            int success = stmt.executeUpdate(sql);
            // Float amount is being set
            Settings.put("floatamount", amount);
        } catch(Exception a) {
                a.printStackTrace();
        }
    }
    
    /* all private methods below here */
    private void addSaleItem() {
        try {
            for(int i = 0; i < SaleData.size(); i++) {
        
                Vector SaleDataRow = (Vector) SaleData.get(i);
                String sql = "insert into `saleitem` (`saleid`, `prid`, " +
                        "`name`, `qty`, `discount`, `taxrate`, `unitprice`, `total`) values " +
                        "(?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                // Assign All the values to the pstmt
                pstmt.setString(1, SaleID);
                pstmt.setObject(2, SaleDataRow.get(0));
                pstmt.setObject(3, SaleDataRow.get(1));
                pstmt.setObject(4, SaleDataRow.get(2));
                pstmt.setObject(5, SaleDataRow.get(3));
                pstmt.setObject(6, SaleDataRow.get(4));
                pstmt.setObject(7, SaleDataRow.get(5));
                pstmt.setObject(8, SaleDataRow.get(6));
                pstmt.execute();
                
                int productQty = Integer.parseInt(SaleDataRow.get(2).toString());
                String productID = SaleDataRow.get(0).toString();
                updateProductQty(productID, productQty);
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Set the product qty when a product has been sold
     * @parma productid
     * @param qty
     */
    private void updateProductQty(String productid, int qty) {
        try {
            String sql = "update product set currentstock = currentstock - ? where id= ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, qty);
            pstmt.setString(2, productid);
            pstmt.executeUpdate();
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    private String createSale() {
        String thisSaleID = "";
        try {
            String sql = "insert into `sale` (`userid`, `register`) " +
                "values (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, Settings.get("userid"));
                pstmt.setObject(2, Settings.get("tillNumber"));
                pstmt.execute();
            sql = "select last_insert_id()";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                thisSaleID = rs.getString(1);
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
        // Put the ID in the sale hash
        Settings.put("saleid", thisSaleID);
        
        return thisSaleID;
    }
    
    private void getSaleInfo() {
        try {
            String sql = "select * from `sale` where `id`='" + SaleID + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                cash = rs.getFloat("cash");
                card = rs.getFloat("card");
                voucher = rs.getFloat("voucher");
                online = rs.getFloat("online");
                account = rs.getFloat("account");
                amounttopay = rs.getFloat("amounttopay");
                change = rs.getFloat("change");
            }
            sql = "select * from saleitem where saleid " + SaleID + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Vector vm = new Vector();
                vm.add(rs.getString("prid"));
                vm.add(rs.getString("name"));
                vm.add(rs.getString("qty"));
                vm.add(rs.getString("discount"));
                vm.add(rs.getString("taxrate"));
                vm.add(rs.getString("unitprice"));
                vm.add(rs.getString("total"));
                SaleData.add(vm);
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
   
}
