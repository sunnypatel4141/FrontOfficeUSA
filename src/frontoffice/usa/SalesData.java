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

import frontoffice.base.DBConnection;

/**
 *
 * @author Sunny Patel
 */

class SalesData extends DBConnection {
    private String S_ProductID;
    private String S_Description;
    private Integer S_Qty;
    private double S_Discount;
    private double S_Tax;
    private double S_Unit_Price;
    private double S_Total;
    
    public SalesData(String ProductID, String Description, Integer Qty, double Discount, double Tax, double Unit_Price, double Total) {
        /** Sales Tax is calcuated on on the purchase price
        * Therefore it is added on to the price after discount 
        * and all other things applied to it.
        */
        double taxRate = (Tax / 100) + 1;
        double TotalPre = (Qty * Unit_Price) - Discount;
        Total = TotalPre * taxRate;
        S_Description = Description;
        S_Qty = Qty;
        S_Discount = Discount;
        S_Unit_Price = Unit_Price;
        S_Total = Total;
        S_ProductID = ProductID;
        S_Tax = Tax;
    }
    
    public String getProductID() {
            return S_ProductID;
    }
    public String getDescription() {
            return S_Description;
    }

    public Integer getQty() {
            return S_Qty;
    }

    public double getDiscount() {
            return S_Discount;
    }

    public double getUnitPrice() {
            return S_Unit_Price;
    }

    public double getTotal() {
            setTotal(S_Total);
            return S_Total;
    }
    
    public double getTax() {
        return S_Tax;
    }

    public void setProductID(String ProductID) {
            S_ProductID = ProductID;
    }
    public void setDescription(String Description) {
            S_Description = Description;
    }

    public void setQty(Integer Qty) {
            S_Qty = Qty;
    }

    public void setDiscount(float Discount) {
            S_Discount = Discount;
    }

    public void setUnit_Price(float Unit_Price) {
            S_Unit_Price = Unit_Price;
    }

    public void setTotal(double Total) {
        /** Sales Tax is calcuated on on the purchase price
        * Therefore it is added on to the price after discount 
        * and all other things applied to it.
        */
        double taxRate = (S_Tax / 100) + 1;
        double TotalPre = (S_Qty * S_Unit_Price) - S_Discount;
        S_Total = TotalPre * taxRate;
    }
    
    public void setTax(double Tax) {
        S_Tax = Tax;
    }
}
