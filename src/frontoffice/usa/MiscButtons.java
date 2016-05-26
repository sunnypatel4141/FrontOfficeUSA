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
import frontoffice.event.MiscButtonEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
public class MiscButtons extends DBConnection implements MiscButtonEvent {

    int taxBtnCount = 25;
    int nTaxBtnCount = 25;
    int poBtnCount = 25;
    
    MiscButtonEvent mbe = null;
    
    ArrayList<Double> taxRates = new ArrayList<Double>();
    JComboBox taxRatesCombo;
    
    JTextField inputPrice = new JTextField(7);
    JTextField description;
    
    String[] keyboard = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", ";",
            "Z", "X", "C", "V", "B", "N", "M", ",", "."
        };
    JButton[] keyboardBtn = new JButton[keyboard.length];
    
    private String currentCard = "1";
    CardLayout layout;
    JPanel dispPnl = new JPanel();
    
    /**
     * Checks button length 
     * Sets it higher then what is required if need be
     */
    public MiscButtons(salesWindow sw) {
        mbe = sw;
        setButtonCounts();
    }
    
    /**
     * We want a place to bring to gather all the buttons
     * Here we will have a top with buttons and bottom with 
     * the numpad
     */
    public JPanel getMiscPanel() {
        
        JPanel returnPanel = new JPanel();
        
        dispPnl.add(renderTaxButtons(), "1");
        dispPnl.add(renderNonTaxButtons(), "2");
        dispPnl.add(renderPayOutButtons(), "3");
        dispPnl.setPreferredSize(new Dimension(300, 300));
        returnPanel.add(dispPnl);
        returnPanel.add(numberPad());
        returnPanel.setLayout(new GridLayout(2, 1));
        
        return returnPanel;
    }
    
    /**
     * Typically we want to check that we have enough buttons to render
     * what is in the database
     */
    private void setButtonCounts() {
        try {
            // For the tax first
            String sql = "select count(*) from tax";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                taxBtnCount = rs.getInt(1) + 1;
            }
            rs.close();
            // Now for the non Tax
            sql = "select count(*) from nontax";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                nTaxBtnCount = rs.getInt(1) + 1;
            }
            rs.close();
            // Count the payout
            sql = "select count(*) from payout";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                poBtnCount = rs.getInt(1) + 1;
            }
            
            // Now we need to adjust the count if it has changed
            
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    public JPanel getCallButtons() {
        JPanel callButtonsPnl = new JPanel();
        layout = new CardLayout();
        dispPnl.setLayout(layout);
        JButton vat = new JButton("TAX");
        vat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentCard = "1";
                layout.show(dispPnl, currentCard);
                mbe.miscButtonFocus("VAT");
            }
        });
        vat.setBackground(new Color(68, 108, 179));
        vat.setForeground(Color.WHITE);
        
        callButtonsPnl.add(vat);
        JButton nonVat = new JButton("Non TAX");
        nonVat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentCard = "2";
                layout.show(dispPnl, currentCard);
                mbe.miscButtonFocus("NON_VAT");
            }
        });
        nonVat.setBackground(new Color(68, 108, 179));
        nonVat.setForeground(Color.WHITE);
        
        callButtonsPnl.add(nonVat);
        JButton payOut = new JButton("Pay Out");
        payOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                currentCard = "3";
                layout.show(dispPnl, currentCard);
                mbe.miscButtonFocus("PAY_OUT");
            }
        });
        payOut.setBackground(new Color(68, 108, 179));
        payOut.setForeground(Color.WHITE);
        callButtonsPnl.add(payOut);
        callButtonsPnl.setLayout(new GridLayout(1, 3));
        
        return callButtonsPnl;
    }
    
    /**
     * Render the Tax Buttons
     */
    public JScrollPane renderTaxButtons() {
        JPanel taxPnl = new JPanel();
        JButton[] taxBtn = new JButton[taxBtnCount];
        Vector btnInfo = new Vector();
        try {
            String sql = "select * from tax";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Name and the product ID
                Vector info = new Vector();
                info.add(rs.getString("name"));
                info.add(rs.getString("prid"));
                info.add(rs.getString("taxrate"));
                btnInfo.add(info);
            }
            // Lets build the buttons
            for(int i = 0; i < btnInfo.size(); i++) {
                final Vector info = (Vector) btnInfo.get(i);
                taxBtn[i] = new JButton("" + info.get(0));
                taxBtn[i].setBackground(Color.WHITE);
                taxBtn[i].addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) {
                       Float amount = Float.parseFloat(inputPrice.getText()) / 100;
                       Float taxrate = Float.parseFloat(info.get(2).toString());
                       Object[] row = {info.get(1), info.get(0), 1, 0.00f, taxrate, amount, amount};
                       mbe.miscButtonEvent(row);
                       inputPrice.setText("");
                   }
                });
                taxBtn[i].setFont(h3);
                taxPnl.add(taxBtn[i]);
                taxPnl.setLayout(new GridLayout(4, 4));
            }
            
            JButton customButton = new JButton("Custom Description");
            customButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    showKeyboard(1, true);
                }
            });
            customButton.setFont(h3);
            customButton.setBackground(Color.WHITE);
            
            sql = "select * from taxcode";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                taxRates.add(rs.getDouble("taxpercentage"));
            }
            
            taxPnl.add(customButton);
            
        } catch(Exception a ) {
            a.printStackTrace();
        }
        // For each category the buttons are renderd
        // the layout is set correctly and a panel with the buttons is returned
            // When a button is initialiased we need to add an actionlistener that
            // this listener will trigger a miscButtonEvent with the prepared row
            // This will call sales window and attempt to add that row to the table
        JScrollPane taxSP = new JScrollPane(taxPnl);
        return taxSP;
    }
    
    /**
     * Render Non Tax Buttons
     */
    public JScrollPane renderNonTaxButtons() {
        JPanel nonTaxPnl = new JPanel();
        JButton[] nonTaxBtn = new JButton[nTaxBtnCount];
        Vector btnInfo = new Vector();
        try {
            String sql = "select * from nontax";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Name and the product ID
                Vector info = new Vector();
                info.add(rs.getString("name"));
                info.add(rs.getString("prid"));
                btnInfo.add(info);
            }
            // Lets build the buttons
            for(int i = 0; i < btnInfo.size(); i++) {
                final Vector info = (Vector) btnInfo.get(i);
                nonTaxBtn[i] = new JButton("" + info.get(0));
                nonTaxBtn[i].setBackground(Color.WHITE);
                nonTaxBtn[i].addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) {
                       Float amount = Float.parseFloat(inputPrice.getText()) / 100;
                       Object[] row = {info.get(1), info.get(0), 1, 0.00f, 0.00f, amount, amount};
                       mbe.miscButtonEvent(row);
                       inputPrice.setText("");
                   }
                });
                nonTaxBtn[i].setFont(h3);
                nonTaxPnl.add(nonTaxBtn[i]);
            }
            
            JButton customButton = new JButton("Custom Description");
            customButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    showKeyboard(1, false);
                }
            });
            customButton.setFont(h3);
            customButton.setBackground(Color.WHITE);
            
            nonTaxPnl.add(customButton);
            nonTaxPnl.setLayout(new GridLayout(4, 4));
        } catch(Exception a ) {
            a.printStackTrace();
        }
        JScrollPane ntSP = new JScrollPane(nonTaxPnl);
        return ntSP;
    }
    
    /**
     * Render Payout Buttons
     */
    public JScrollPane renderPayOutButtons() {
        JPanel poPnl = new JPanel();
        JButton[] poBtn = new JButton[poBtnCount];
        Vector btnInfo = new Vector();
        try {
            String sql = "select * from payout";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Name and the product ID
                Vector info = new Vector();
                info.add(rs.getString("name"));
                info.add(rs.getString("prid"));
                btnInfo.add(info);
            }
            // Lets build the buttons
            for(int i = 0; i < btnInfo.size(); i++) {
                final Vector info = (Vector) btnInfo.get(i);
                poBtn[i] = new JButton("" + info.get(0));
                poBtn[i].setBackground(Color.WHITE);
                poBtn[i].addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent ae) {
                       Float amount = Float.parseFloat(inputPrice.getText()) / 100;
                       amount = amount * -1;
                       Object[] row = {info.get(1), info.get(0), 1, 0.00f, 0.00, amount, amount};
                       mbe.miscButtonEvent(row);
                       inputPrice.setText("");
                   }
                });
                poBtn[i].setFont(h3);
                poPnl.add(poBtn[i]);
            }
            
            JButton customButton = new JButton("Custom Description");
            customButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    showKeyboard(-1, false);
                }
            });
            customButton.setFont(h3);
            customButton.setBackground(Color.WHITE);
            
            poPnl.add(customButton);
            poPnl.setLayout(new GridLayout(3, 3));
        } catch(Exception a ) {
            a.printStackTrace();
        }
        JScrollPane poSP = new JScrollPane(poPnl);
        return poSP;
    }
    
    public JPanel numberPad() {
        JPanel returnPnl = new JPanel();
        JPanel padPnl = new JPanel();
        JPanel inputPnl = new JPanel();
        final String[] numberbtns = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "00", "CLEAR"};
        JButton[] numBtn = new JButton[numberbtns.length];
        inputPrice.setFont(h1);
        inputPnl.add(inputPrice);
        inputPnl.setLayout(new GridLayout(2, 1));
        
        for(int i = 0; i < numberbtns.length; i++ ) {
            final String buttonText = numberbtns[i];
            numBtn[i] = new JButton(numberbtns[i]);
            numBtn[i].setBackground(Color.WHITE);
            numBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String curVal = inputPrice.getText();
                    String retval = numberPad(buttonText, curVal);
                    inputPrice.setText(retval);
                }
            });
            numBtn[i].setFont(h1);
            padPnl.add(numBtn[i]);
        }
        
        padPnl.setLayout(new GridLayout(4, 3));
        returnPnl.add(inputPnl);
        returnPnl.add(padPnl);
        returnPnl.setLayout(new FlowLayout());
        
        padPnl.setPreferredSize(new Dimension(300, 300));
        return returnPnl;
    }
    
    /**
     * For applying the Numbers to the text field form the misc numpad
     */
    private String numberPad(String input, String arg2) {
        String ret = arg2; /*The current value is any*/
        if ( input.equals("CLEAR") ) {
            // Must be Clear
            ret = "";
        } else {
            // Must be numbers
            ret = new StringBuffer().append(arg2).append(input).toString();
        }
        
        return ret;
    }
    
    /**
     * Creates Keyboard dialog
     * Takes argument on what to times the final amount by 1 or -1
     * @param multiple
     */
    private void showKeyboard(final int multiple, final boolean showTaxBox) {
        
        final JDialog keybaordDialog = new JDialog();
        
        description = new JTextField(50);
        description.setFont(new Font("Verdana", Font.BOLD, 16));
        
        taxRatesCombo = new JComboBox(taxRates.toArray());
        
        JButton closeKeyboard = new JButton("CLOSE");
        closeKeyboard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                keybaordDialog.dispose();
            }
        });
        
        JButton clearKeyboard = new JButton("CLEAR");
        clearKeyboard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                clearKeyboard();
            }
        });
        
        JButton spaceKeyboard = new JButton("SPACE");
        spaceKeyboard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                keyboardKeyPressed(" ");
            }
        });
        
        JButton enterKeyboard = new JButton("ENTER");
        enterKeyboard.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                double taxrate = 0.00;
                if (showTaxBox) {
                    taxrate = Double.parseDouble(taxRatesCombo.getSelectedItem().toString());
                }
                Float amount = (Float.parseFloat(inputPrice.getText()) / 100) * multiple;
                Object[] row = {00000, description.getText(), 1, 0.00f, taxrate, amount, amount};
                mbe.miscButtonEvent(row);
                inputPrice.setText("");
            }
        });
        
        // Load all the keys
        JPanel kbKeys = new JPanel();
        for(int i = 0; i < keyboard.length; i++) {
            final int j = i;
            keyboardBtn[i] = new JButton(keyboard[i]);
            keyboardBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    keyboardKeyPressed(keyboard[j]);
                }
            });
            kbKeys.add(keyboardBtn[i]);
        }
        
        kbKeys.setPreferredSize(new Dimension(850, 350));
        
        kbKeys.setLayout(new GridLayout(4, 10));
        
        keybaordDialog.add(description);
        if (showTaxBox) {
            keybaordDialog.add(taxRatesCombo);
        }
        keybaordDialog.add(kbKeys);
        JPanel controlBtns = new JPanel();
        controlBtns.add(closeKeyboard);
        controlBtns.add(clearKeyboard);
        controlBtns.add(spaceKeyboard);
        controlBtns.add(enterKeyboard);
        keybaordDialog.add(controlBtns);
        
        keybaordDialog.setLayout(new FlowLayout());
        keybaordDialog.setSize(950, 480);
        keybaordDialog.setLocation(0, 100);
        keybaordDialog.setVisible(true);
        
    }
    
    private void keyboardKeyPressed(String keyArg) {
        String descName = new StringBuffer(description.getText()).append(keyArg).toString();
        description.setText(descName);
    }
    
    private void clearKeyboard() {
        description.setText("");
    }

    @Override
    public void miscButtonEvent(Object[] rowArg) {
    }

    @Override
    public void miscButtonFocus(String buttonName) {
    }
    
    /**
     * Render the payout buttons
     */
    
}
