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

/**
 *
 * @author Sunny Patel
 */

import java.io.Serializable;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public class SalesTableModel extends AbstractTableModel implements Serializable {
    public static final int PRODUCTID = 0;
    public static final int DESCRIPTION = 1;
    public static final int QUANTITY = 2;
    public static final int DISCOUNT = 3;
    public static final int TAX = 4;
    public static final int UNIT_PRICE = 5;
    public static final int TOTAL = 6;

    public String[] colNames = {"ProductID",
                                "Description",
				"Quantity",
				"DISCOUNT",
                                "Tax",
				"Unit_Price",
				"Total"};
	
	
    public Class[] colTypes = {String.class,
                                String.class,
				Integer.class,
				double.class,
				double.class,
                                double.class,
				double.class};
    
    Vector dataVector;

    public SalesTableModel(Vector slDataVector) {
        super();
        dataVector = slDataVector;
    }

    public int getColumnCount() {
        return colNames.length;
    }

    public int getRowCount() {
        return dataVector.size();
    }

    public Vector getDataVector() {
        return dataVector;
    }

    private static Vector nonNullVector(Vector v) {
        return (v != null) ? v : new Vector();
    }

    public void setValueAt(Object aValue, int row, int column) {
        SalesData salesData = (SalesData)(dataVector.elementAt(row));

        switch(column){
            case PRODUCTID : salesData.setProductID((String) aValue);
            break;
            case DESCRIPTION : salesData.setDescription((String) aValue);
            break;
            case QUANTITY : salesData.setQty((Integer) aValue);
            break;
            case DISCOUNT : salesData.setDiscount((float) aValue);
            break;
            case TAX : salesData.setTax((float) aValue);
            break;
            case UNIT_PRICE : salesData.setUnit_Price((float) aValue);
            break;
            case TOTAL : salesData.setTotal((float) aValue);
            break;
        }
    }

    @Override
    public String getColumnName(int col) {
            return colNames[col];
    }

    @Override
    public Class getColumnClass(int col) {
            return colTypes[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        SalesData salesData = (SalesData)(dataVector.elementAt(row));

        switch(col) {
            case PRODUCTID : return salesData.getProductID();
            case DESCRIPTION : return salesData.getDescription();
            case QUANTITY : return salesData.getQty();
            case DISCOUNT : return salesData.getDiscount();
            case TAX : return salesData.getTax();
            case UNIT_PRICE : return salesData.getUnitPrice();
            case TOTAL : return salesData.getTotal();
        }
        return new String();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void removeRow(int row) {
        dataVector.removeElementAt(row);
        fireTableRowsDeleted(row, row);
    }
    public void removeAllRows() {
        dataVector.removeAllElements();
        fireTableStructureChanged();
    }

    public void addRow(Object Obj, int row) {
        dataVector.insertElementAt(Obj, row);
        fireTableRowsInserted(row, row);
    }    
}