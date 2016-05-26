/*
 * Copyright (C) 2015 Sunny Patel
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
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
public class SearchProduct extends salesWindow {
    
    JDialog searchDialog = new JDialog(frame, "Search Products", true);
    Object[][] data = null;
    String[] cn = {"ID", "Name", "Main Category", "Sub Categroy", "Single Price"};
    DefaultTableModel dtmSP = new DefaultTableModel(data, cn);
    JTable tableSP = new JTable(dtmSP);
    JTextField productName = new JTextField(7);
    
    String[] alphabet = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                        "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                        "A", "S", "D", "F", "G", "H", "J", "K", "L", ":",
                         "\\", "Z", "X", "C", "V", "B", "N", "M", ",", "."};
    
    JButton[] alphBtn = new JButton[alphabet.length];
    
    /**
     * Load the default class 
     */
    SearchProduct() {
        loadWindow();
    }
    
    private void loadWindow() {
        
        JPanel topPnl = new JPanel();
        JPanel keybdPnl = new JPanel();
        
        productName.setFont(h1);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                productName.setText("");
            }
        });
        
        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedRow = tableSP.getSelectedRow();
                if(selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Must select product to edit", 
                        "Edit error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String productID = dtmSP.getValueAt(tableSP.getSelectedRow(), 0).toString();
                }
            }
        });
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                searchDialog.dispose();
            }
        });
        
        JLabel searchNameLbl = new JLabel("Name");
        
        topPnl.add(searchNameLbl);
        topPnl.add(productName);
        topPnl.add(clearBtn);
        topPnl.add(closeBtn);
        //topPnl.add(editBtn);
        
        for(int i = 0; i < alphabet.length; i++) {
            final int j = i;
            alphBtn[i] = new JButton(alphabet[i]);
            alphBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String currentVal = productName.getText();
                    String newVal = new StringBuffer(currentVal).append(alphabet[j]).toString();
                    productName.setText(newVal);
                    searchProduct();
                }
            });
            
            keybdPnl.add(alphBtn[i]);
        }
        keybdPnl.setLayout(new GridLayout(4, 10));
        
        JButton spaceBtn = new JButton("SPACE");
        spaceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String currentVal = productName.getText();
                    String newVal = new StringBuffer(currentVal).append(" ").toString();
                    productName.setText(newVal);
                    searchProduct();
            }
        });
        
        JScrollPane searchJSP = new JScrollPane(tableSP);
        
        topPnl.setPreferredSize(new Dimension(650, 50));
        searchJSP.setPreferredSize(new Dimension(650, 250));
        keybdPnl.setPreferredSize(new Dimension(700, 300));
        spaceBtn.setPreferredSize(new Dimension(105, 50));
        
        JPanel boxPnl = new JPanel();
        boxPnl.add(topPnl);
        boxPnl.add(searchJSP);
        boxPnl.add(keybdPnl);
        boxPnl.add(spaceBtn);
        boxPnl.setLayout(new BoxLayout(boxPnl, BoxLayout.PAGE_AXIS));
        
        searchDialog.add(boxPnl);
        searchDialog.setSize(800, 700);
        searchDialog.setLayout(new FlowLayout());
        searchDialog.setVisible(true);
    }
    
    /**
     * Execute the query when a key is typed
     */
    private void searchProduct() {
        
        while(dtmSP.getRowCount() > 0) {
            dtmSP.removeRow(0);
        }
        
        try {
            String sql = "select pr.id, pr.name, mc.name, sc.name, pp.price from product pr" +
                " left join productprice pp on pp.prid=pr.id left join maincategory mc" +
                " on mc.id=pr.mcid left join subcategory sc on sc.id=pr.scid" +
                " where pp.qty='1' and pr.name like ? limit 150";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" +productName.getText() + "%");
            rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {rs.getString(1), rs.getString(2), 
                    rs.getString(3), rs.getString(4), rs.getString(5)};
                dtmSP.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SearchProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
