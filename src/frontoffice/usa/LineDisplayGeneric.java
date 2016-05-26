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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Sunny Patel
 */

//TODO: Maybe implement all of this under a perif class where it can be handled nicely
// pLEase MULTI ThREAD THIS.... AS IT TAKES A WHILE TO ADD A PRODUCT
public class LineDisplayGeneric extends salesWindow implements SerialPortEventListener {

    CommPort commPortHandle;
    OutputStream out;
    InputStream in;
    CommPortIdentifier portID;
    static int PortInUseInt = 0;
    int flushval = 0;
    private byte[] buffer = new byte[1024];
    private String buffer_string;
    private int IN_TEST_MODE = 0;
    
    public LineDisplayGeneric(String port) {
        // Create a generic Display Object
        // TODO Expand this design
        // In the unlikely event that this actually is used by a lot of people 
        // we need make this the base class and expand around it
        openPort(port);
    }
    
    public LineDisplayGeneric(String port, String testMode) {
        // Create a generic Display Object
        // TODO Expand this design
        // In the unlikely event that this actually is used by a lot of people 
        // we need make this the base class and expand around it
        if (testMode.equals("1")) {
            IN_TEST_MODE = 1;
        } else {
            openPort(port);
        }
    }
    
    /**
     * Put this class in test mode so it will emulate a to stderr
     */
    public LineDisplayGeneric(String port, int TEST_MODE) {
        IN_TEST_MODE = TEST_MODE;
    }
    
    /**
     * Open the provided Communications Port
     * @param portName
     * @return boolean
     */
    public boolean openPort(String portName) {
        
        try {
            CommPortIdentifier commPort = CommPortIdentifier.getPortIdentifier(portName);

            // Is this port currently occupied
            if (commPort.isCurrentlyOwned()) {
                System.out.println("The port " + portName + " is currently opened elsewhere");
                return false;
            }

            commPortHandle = commPort.open(this.getClass().getName(), 2000);
            
            // Make sure that this is the serial Port
            if (commPortHandle instanceof SerialPort ) {
                
                SerialPort serialPortHandle = (SerialPort) commPortHandle;
                
                serialPortHandle.setSerialPortParams(9600, SerialPort.DATABITS_8, 
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                
                out = serialPortHandle.getOutputStream();
                in = serialPortHandle.getInputStream();

                
                serialPortHandle.addEventListener(this);
                serialPortHandle.notifyOnDataAvailable(true);
                
                Thread.sleep(105); // cos i can
            }
        } catch (Exception a) {
            JOptionPane.showMessageDialog(null, "Port Error: " + portName, 
                "Comm Port Error", JOptionPane.ERROR_MESSAGE);
            a.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * writes out the lines to the display and then closes the port
     */
    public void closePort() {
        try {
            if (IN_TEST_MODE == 1) {
                System.err.println("Closing Port");
                return;
            }
            out.flush();
            out.close();
            in.close();
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        commPortHandle.close();
    }
    
    /**
     * Prove the line what you want with a end of line character
     * this will display the lines on the screen the best it can
     */
    public void updateDisplay(String line) {
        try {
            if(IN_TEST_MODE == 1) {
                System.err.println(line);
                return;
            }
            out.write(line.getBytes());
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Close output Stream
     * @param spe
     */

    @Override
    public void serialEvent(SerialPortEvent spe) {
        int data = 0;
        
        try {
            int len = 0;
            while((data = in.read()) > -1) {
                if (data == '\n') {
                    break;
                }
                buffer[len++] = (byte) data;
            }
            buffer_string = new String(buffer, 0, len);
            
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
}
