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
package frontoffice.util;

import frontoffice.usa.EndOfDay;
import frontoffice.usa.FloatSettings;
import frontoffice.event.NumberPadEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
public class NumberPad implements ActionListener {
    
    private NumberPadEvent npe;
    
    String[] numTxt = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "CLEAR", "OK"};
    JButton[] numBtns = new JButton[numTxt.length];
    JTextField value;
    JDialog frameNumPad;
    JButton closeDialog;

    public NumberPad(FloatSettings aThis, JDialog frameArg) {
        npe = aThis;
        renderNumberPad(frameArg);
        
    }
    
    public NumberPad(EndOfDay aThis, JDialog frameArg) {
        npe = aThis;
        renderNumberPad(frameArg);
    }
    
    public void NumberPad(NumberPadEvent event) {
        npe = event;
    }
    
    private void renderNumberPad(JDialog frameArg) {
        frameNumPad = new JDialog(frameArg, "Number Pad", true);
        // Number Pad is rendered here
        JPanel btnPnl = new JPanel();
        value = new JTextField(7);
        Font large = new Font("Verdana", Font.BOLD, 16);
        value.setFont(large);
        
        closeDialog = new JButton("Close");
        closeDialog.addActionListener(this);
        
        for(int i = 0; i < numTxt.length; i++) {
            numBtns[i] = new JButton(numTxt[i]);
            numBtns[i].addActionListener(this);
            btnPnl.add(numBtns[i]);
            numBtns[i].setFont(large);
        }
        btnPnl.setLayout(new GridLayout(4, 3));
        btnPnl.setPreferredSize(new Dimension(300, 300));
        frameNumPad.add(value);
        frameNumPad.add(btnPnl);
        frameNumPad.add(closeDialog);
        
        frameNumPad.setLayout(new FlowLayout());
        frameNumPad.setLocation(320, 200);
        frameNumPad.setSize(350, 420);
        frameNumPad.setVisible(true);
        
    }
    
    private void cleardownNumberPad() {
        frameNumPad.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        Object trigger = e.getSource();
        
        if( trigger == closeDialog) {
            cleardownNumberPad();
        }
        
        for(int i = 0; i < numTxt.length; i++ ) {
            if(trigger == numBtns[i]) {
                // Action for the type of button
                if( numTxt[i].equals("OK")) {
                    String inputText = value.getText();
                    if(inputText.equals("")) {
                        inputText = "0";
                    }
                    npe.numberPadEvent(inputText);
                    cleardownNumberPad();
                    //CLOSE THE BOX
                } else if ( numTxt[i].equals("CLEAR") ) {
                    value.setText("");
                } else {
                    // Get current Text
                    String currenttext = value.getText();
                    String msg = new StringBuffer().append(currenttext).append(numTxt[i]).toString();
                    value.setText(msg);
                }
            }
        }
    }
    
}
