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
package frontoffice.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunny Patel
 */
public class UPCConverter {

    public UPCConverter() {
    }

    public String convertUPCEtoA(String upcString) {
        String upcToConvert = "";
        String manufacturerNo = "";
        String ItemNo = "";
        String UPCA = "";

        switch (upcString.length()) {
            case 6: upcToConvert = upcString;
            break;

            case 7: upcToConvert = upcString.substring(0, 6);
            break;

            case 8: upcToConvert = upcString.substring(1, 7);
            break;

            default: upcToConvert = "Invalid UPCE";
            break;
        }

        if (upcToConvert.equals("Invalid UPCE")) {
            return "";
        }

        // Give me 6 individual digits
        String Digit1 = upcToConvert.substring(0, 1);
        String Digit2 = upcToConvert.substring(1, 2);
        String Digit3 = upcToConvert.substring(2, 3);
        String Digit4 = upcToConvert.substring(3, 4);
        String Digit5 = upcToConvert.substring(4, 5);
        String Digit6 = upcToConvert.substring(5);

        // Calcualte based on the last digit
        //265731
        //2610000573

        switch(Integer.parseInt(Digit6)) {
            case 0 :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit6).append("00").toString();
                ItemNo = new StringBuffer("00").append(Digit3).append(Digit4).append(Digit5).toString();
                break;
            case 1 :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit6).append("00").toString();
                ItemNo = new StringBuffer("00").append(Digit3).append(Digit4).append(Digit5).toString();
                break;
            case 2 :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit6).append("00").toString();
                ItemNo = new StringBuffer("00").append(Digit3).append(Digit4).append(Digit5).toString();
                break;
            case 3 :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit3).append("00").toString();
                ItemNo = new StringBuffer("000").append(Digit4).append(Digit5).toString();
                break;
            case 4 :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit3).append(Digit4).append("0").toString();
                ItemNo = new StringBuffer("0000").append(Digit5).toString();
                break;
            default :
                manufacturerNo = new StringBuffer(Digit1).append(Digit2).append(Digit3).append(Digit4).append(Digit5).toString();
                ItemNo = new StringBuffer("0000").append(Digit6).toString();
                break;

        }

        // Create the first bit
        String UPCAWihoutCheckDigit = "0" + manufacturerNo + ItemNo;

        UPCA = UPCAWihoutCheckDigit + calcCheckDigit(UPCAWihoutCheckDigit);

        System.out.println("full Barcode is " + UPCA);
        return UPCA;
    }

    private int calcCheckDigit(String UPCAWihoutCheckDigitArg) {
        int Check = 0;
        System.out.println("UPCA WO CD IS " + UPCAWihoutCheckDigitArg);
        
        for(int i = 1; i < 12; i++) {
            int Test =  Integer.parseInt("" + UPCAWihoutCheckDigitArg.charAt((i - 1)));
            if(isOdd(i)) {
                Check = Check + Test * 7; // Odd Position Digits multiplied by 7
            } else {
                Check = Check + Test * 9; // Even Position digits multiplied by 9
            }
        }
        
        Check = (Check % 10) + 48;
        return charFromCharCode(Check);
    }
    
    private boolean isOdd(int valueArg) {
        return valueArg % 2 == 0 ? false : true;
    }
    
    private int charFromCharCode(int CheckDigitArg) {
        
        /**
         * How the flow works,
         * 1. Convert the asci char to Hex
         * 2. Convert the hex to a UTF-8 Legal String
         * 3. Return String to Integer
         */
        
        String hexString = Integer.toHexString(CheckDigitArg);
        String symbolChar = "";
        
        try {
            symbolChar = URLDecoder.decode("%" + hexString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UPCConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Integer.parseInt(symbolChar);
    }

    public int convertUPCAtoE(String upcString) {
        return 0;
    }
}
