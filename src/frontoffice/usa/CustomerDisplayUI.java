/*
 * Copyright (C) 2016 Sunny Patel
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

import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
class CustomerDisplayUI {
    
    JDialog CDUI = new JDialog();
    String[] CDUIcn = {"Description", "Qty", "Total"};
    Object[][] CDUIdata = null;
    DefaultTableModel CDUIdtm = new DefaultTableModel(CDUIdata, CDUIcn);
    JLabel changeLabel, totalLabel, tenderedLabel;
    JTextField changeFld, totalFld, tenderedFld;
    
    public CustomerDisplayUI() {
        // Start The UI
    }
    
    private void render() {
        //render the window with a table
        JTable CDUITable = new JTable(CDUIdtm);
        JScrollPane jsp = new JScrollPane(CDUITable);
        
        JPanel CDUITablePnl = new JPanel();
        JPanel CDUIFieldPnl = new JPanel();
        
        CDUITablePnl.add(jsp);
        
        changeLabel = new JLabel("Change:");
        totalLabel = new JLabel("Total:");
        tenderedLabel = new JLabel("Paid Total:");
        
        changeFld = new JTextField(7);
        totalFld = new JTextField(7);
        tenderedFld = new JTextField(7);
        
        CDUI.add(CDUITablePnl);
        CDUI.add(CDUIFieldPnl);
        CDUI.setSize(1024, 786);
        CDUI.setLocation(1000, 0);
        CDUI.setLayout(new FlowLayout());
        CDUI.setIconImage(new ImageIcon("Icons/CustomerDisp.png").getImage());
        CDUI.setVisible(true);   
    }
    
    public void clearTransaction() {
        while(CDUIdtm.getRowCount() > 0) {
            CDUIdtm.removeRow(0);
        }
    }
    
    public void addTransaction(Object[] dataRow) {
        //Objectp[] row = {dataRow[]};
        //CDUIdtm.addRow();
    }
}
