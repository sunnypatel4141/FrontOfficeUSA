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

import frontoffice.event.NumPadListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
public class NumPad extends salesWindow {

    // So we can tell them to do this
    private NumPadListener npl;
    
    JDialog numPadFrame = new JDialog(frame, "NumPad", true);
    String[] numTxt = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "CLEAR", "OK"};
    JButton[] numbers = new JButton[12];
    JTextField qtyInputFld = new JTextField(7);
    
    NumPad(salesWindow salesWindowArg) {
        npl = salesWindowArg;
        renderWindow();
    }

    private void renderWindow() {
        JPanel numPadPnl = new JPanel();
        
        for(int i = 0; i < numTxt.length; i++) {
            final String numCharStr = numTxt[i];
            numbers[i] = new JButton(numCharStr);
            numbers[i].setFont(h1);
            numbers[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    numberPressed(numCharStr);
                }
            });
            numPadPnl.add(numbers[i]);
        }
        numPadPnl.setLayout(new GridLayout(4, 3));
        numPadPnl.setPreferredSize(new Dimension(300, 300));
        
        JButton closeBtn = new JButton("CLOSE");
        closeBtn.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                numPadFrame.dispose();
            }
        });
        closeBtn.setPreferredSize(new Dimension(120, 70));
        
        qtyInputFld.setFont(h1);
        
        numPadFrame.add(qtyInputFld);
        numPadFrame.add(numPadPnl);
        numPadFrame.add(closeBtn);
        
        numPadFrame.setLayout(new FlowLayout());
        numPadFrame.setSize(350, 450);
        numPadFrame.setLocation(300, 200);
        numPadFrame.setVisible(true);
    }
    
    private void numberPressed(String numCharArg) {
        // Work out what to do 
        if(numCharArg.equals("OK")) {
            // Input Value must exist
            if(!qtyInputFld.getText().equals("")) {
                npl.editSaleTableData(qtyInputFld.getText());
            }
            // Allways close on ok
            numPadFrame.dispose();
        } else if(numCharArg.equals("CLEAR")) {
            qtyInputFld.setText("");
        } else {
            // Append the number char to the field
            String numValStr = new StringBuffer(qtyInputFld.getText()).append(numCharArg).toString();
            qtyInputFld.setText(numValStr);
        }
    }
    
    
}
