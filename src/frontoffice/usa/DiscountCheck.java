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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 *
 * @author Sunny Patel
 */
class DiscountCheck extends salesWindow {

    Vector SaleData = new Vector();

    // Some information about products
    private HashMap familyGroups = new HashMap();
    private HashMap familyGroupMapping = new HashMap();
    private HashMap multiProduct = new HashMap();
    private HashMap salesTableQty = new HashMap();

    public DiscountCheck(Vector saleDataArg) {
        SaleData = saleDataArg;
        loadChecks();
        condenseSalesTable(saleDataArg);
    }

    /**
     * Lets load everything for this table including the family groups
     */
    private void loadChecks() {
        for(int i = 0; i < SaleData.size(); i++) {
            // Sale Data Build
            Vector saleRow = (Vector) SaleData.get(i);
            String prid = saleRow.get(0).toString();
            loadFamilyGroup(prid);
            loadMultiBuy(prid);
        }
    }

    /**
     * We want a key value pair of prid and the qty
     * so this method will condense this
     */
    private void condenseSalesTable(Vector saleDataArg) {
        for(int i = 0; i < saleDataArg.size(); i++) {
            // Get the sale data
            Vector saleRow = (Vector) saleDataArg.get(i);
            salesTableQty.put(saleRow.get(0), saleRow.get(2));
        }
    }

    /**
     * Loads the family group for the product supplied and
     * saves it for later reference
     * storage is based on family group as key
     * and prid as vector
     */
    private void loadFamilyGroup(String prid) {
        // check if the values exist
        // If they don't load them in to the familyGroups hashmap
        try {
            String familyGroupID = "";
            Vector products = new Vector();
            // Get the famliy ID for this product
            String sql = "select * from familygroupitems where prid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prid);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                familyGroupID = rs.getString("grid");
            }
            // Family Only if this product was
            if (!familyGroupID.equals("")) {
                sql = "select * from familygroupitems where grid= " + familyGroupID + "";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    products.add(rs.getString("prid"));
                }
                familyGroups.put(familyGroupID, products);
                familyGroupMapping.put(prid, familyGroupID);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    /**
     * Given the product id we want to find any multi buy discounts
     * So load them up in to a multi buy hash
     */
    private void loadMultiBuy(String prid) {
        // Check that the product exists in the hash
        // if it does not exist then load it
        try {
            LinkedHashMap prices = new LinkedHashMap();
            String sql = "select * from productprice where prid='" + prid + "' order by qty";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                prices.put(rs.getString("qty"), rs.getString("price"));
            }
            // Lets add all the prices here for later reference
            multiProduct.put(prid, prices);
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    /**
     * Get the price of given product and qty
     */
    public String getPrice(String prid, int qty) {
       // return ""; // EMERGENCY
        LinkedHashMap prices = (LinkedHashMap) multiProduct.get(prid);
        
        // We must have a single qty
        if (!prices.containsKey("1")) {
            // Checks if the key exists
            return "";
        }
        String price = prices.get("1").toString(); // Get the base price for a single unit
        // Family ID checks
        String grid = "";
        if (familyGroupMapping.containsKey(prid)) {
            grid = familyGroupMapping.get(prid).toString();
        }
        // Lets get all the other qty's
        final Integer familyQty = getOtherQty(prid, grid, qty);
        // Create an Iteration to go through the prices Keys
        Object[] priceKeys = prices.keySet().toArray();
        float thisPrice = 0.000f;
        int overflowQty = 0;
        int totalQty = familyQty;
        // Array refs start at 0 and the length reports a count of 2 for [0, 1]
        // This allows us to count from the highest to lowest qty
        int keyCount = priceKeys.length - 1;
        for (int i = keyCount; i > -1; i--) {
            Integer currentQty = Integer.parseInt(priceKeys[i].toString());
            float priceFloat = Float.parseFloat(prices.get(priceKeys[i]).toString());
            // Skip Qty bands that exceed total amount
            if (currentQty > totalQty) {
                continue;
            }

            // Are there any remainders for current Qty and total Qty
            overflowQty = totalQty % currentQty;
            // What is the total a miltiple of current and total Qty
            int totalMultiple = totalQty / currentQty;
            // Get the total of multiple and currnet Qty
            int totalMultiQty = totalMultiple * currentQty;

            thisPrice += priceFloat * totalMultiQty;

            totalQty = totalQty - totalMultiQty;
            // Have we calculated all th e qty
            if(overflowQty < 1) {
                break;
            }
        }
        
        float unitPrice = thisPrice / familyQty;
        
        // Return a clean currency value
        String val =  getCurrency("" + unitPrice);
        return val;
    }

    /**
     * We go through the table and find related ptid based on the family id
     */
    private Integer getOtherQty(String prid, String grid, int qtyArg) {
        // If we don't find family then we can return default
        int Qty = qtyArg;
        // If there is a group then do this
        if (grid.equals("")) {
            return Qty;
        }

        // lets get the prid's vector and itereate through it
        Vector familyPRIDs = (Vector) familyGroups.get(grid);
        
        for(int i = 0; i < familyPRIDs.size(); i++) {
            String familyPRID = familyPRIDs.get(i).toString();
            // Ignore if for the supplied prid matches
            if (familyPRID.equals(prid)) {
                continue;
            }
            
            // get The Qty of the family in the sales table
            if (salesTableQty.containsKey(familyPRID)) { 
                Integer familyQty = Integer.parseInt(salesTableQty.get(familyPRID).toString());
                Qty += familyQty;
            }
        }

        return Qty;
    }

    /**
     * Sometimes there are products that
     * have promotions like buy a and get b for xyz
     * DO NOT USE NOT IMPLETEMENTED YET
     */
    private void loadBuyThisForThat(String prid) {
        // check if this product has any this for that
        //TODO: MAKE THIS WORK
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}