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

import frontoffice.usa.Reports.CustomerReceipt;
import frontoffice.usa.Reports.ZLog;
import frontoffice.Update;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Sunny Patel
 */
class MainMenu extends salesWindow implements ActionListener {
    private JPanel menuPnl = new JPanel();
    private String[] menuTxt = {"Exit", "Update", "Z Read", "X Read", "Last Reciept", "History",
        "End Of Day", "Float", "Settings", "Hourly Report", "Customer", "No Sale", "Day Summary", 
        "LD STOP", "LD START", "Search Product", "Clear Sale"};
    private JButton[] menuBtn = new JButton[menuTxt.length];
    
    MainMenu() {
        for(int i = 0; i < menuTxt.length; i++) {
            final int j = i;
            menuBtn[i] = new JButton(menuTxt[i]);
            menuBtn[i].setBackground(new Color(65, 131, 215));
            menuBtn[i].setForeground(Color.WHITE);
            if(menuTxt[i].equals("Exit") ) {
                menuBtn[i].setIcon(new ImageIcon("Icons/system-shutdown.png"));
            }
            menuBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if ( menuTxt[j].equals("Exit")) {
                        close();
                    } else if ( menuTxt[j].equals("Update")) {
                        if ( !readRight("updatemenubtn") ) { 
                            menuBtn[j].setEnabled(false);
                        }
                        update();
                    } else if ( menuTxt[j].equals("Z Read")) {
                        if ( !readRight("zreadmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        zread();
                    } else if ( menuTxt[j].equals("X Read")) {
                        if ( !readRight("xreadmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        xread();
                    } else if ( menuTxt[j].equals("Last Reciept")) {
                        if ( !readRight("lastrecieptmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        lastrec();
                    } else if ( menuTxt[j].equals("History")) {
                        if ( !readRight("historymenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        history();
                    } else if ( menuTxt[j].equals("End Of Day")) {
                        if ( !readRight("endofdaymenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        endofday();
                    } else if ( menuTxt[j].equals("Float")) {
                        if ( !readRight("floatmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        floatcheck();
                    } else if ( menuTxt[j].equals("Settings")) {
                        if ( !readRight("settingsmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        settings();
                    } else if ( menuTxt[j].equals("Hourly Report")) {
                        if ( !readRight("hourlyreportmenubtn") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        hourlyreport();
                    } else if (menuTxt[j].equals("Customer")) {
                        if ( !readRight("customer") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        customer();
                    } else if (menuTxt[j].equals("No Sale")) {
                        if ( !readRight("nosale") ) {
                            menuBtn[j].setEnabled(false);
                        }
                        nosale();
                    } else if (menuTxt[j].equals("Day Summary")) {
                        if (!readRight("daysummary")) {
                            menuBtn[j].setEnabled(false);
                        }
                        dayreport();
                    } else if (menuTxt[j].equals("LD STOP")) {
                        if (!readRight("daysummary")) {
                            menuBtn[j].setEnabled(false);
                        }
                        ldstop();
                    } else if (menuTxt[j].equals("LD START")) {
                        if (!readRight("daysummary")) {
                            menuBtn[j].setEnabled(false);
                        }
                        ldstart();
                    } else if (menuTxt[j].equals("Search Product")) {
                        if (!readRight("daysummary")) {
                            menuBtn[j].setEnabled(false);
                        }
                        searchproduct();
                    } else if (menuTxt[j].equals("Clear Sale")) {
                        if (!readRight("clearsale")) {
                            menuBtn[j].setEnabled(false);
                        }
                        cleanup();
                    }
                }
            });
            
            menuPnl.setLayout(new GridLayout(5, 3, 2, 2));
            // Do some application rights checks
            if ( menuTxt[j].equals("Exit")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Update") && readRight("updatemenubtn")) {
                //menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Z Read") && readRight("zreadmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("X Read") && readRight("xreadmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Last Reciept") && readRight("lastrecieptmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("History") && readRight("historymenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("End Of Day") && readRight("endofdaymenubtn")) {
                // TODO: OBI ERROR IN  flaot Class !!! FIX
                //menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Float") && readRight("floatmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Settings") && readRight("settingsmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Hourly Report") && readRight("hourlyreportmenubtn")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Customer") && readRight("customer")) {
                //menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("No Sale") && readRight("nosale")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Day Summary") && readRight("daysummary")) {
                menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("LD STOP") && readRight("daysummary")) {
                //menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("LD START") && readRight("daysummary")) {
                //menuPnl.add(menuBtn[i]);
            } else if ( menuTxt[j].equals("Search Product") && readRight("daysummary")) {
                menuPnl.add(menuBtn[i]);
            } else if (menuTxt[j].equals("Clear Sale") && readRight("clearsale")) {
                menuPnl.add(menuBtn[j]);
            }
        }
    }
    
    public JPanel getMenu() {
        return menuPnl;
    }
    
    private void close() {
        closePeripherals();
        System.exit(0);
    }
    
    private void update() {
        String cv = Settings.get("currentVersion").toString();
        String url = Settings.get("updateUrl").toString();
        boolean updateApproval = false; //flag for update approval and checking
        Update u = new Update(cv, url, updateApproval);
        u.proceedWithUpdate();
    }
    
    private void zread() {
        ZLog zl = new ZLog(ZLog.Z_READ);
    }
    
    private void xread() {
        ZLog zl = new ZLog(ZLog.X_READ);
    }
    
    private void lastrec() {
        if (Settings.containsKey("saleid")) {
            CustomerReceipt rp = new CustomerReceipt("Reports/CustomerReceipt.jrxml",
                Settings.get("saleid").toString());
            rp.printReport();
        }
    }
    
    private void history() {
        SaleHistory sh = new SaleHistory();
    }
    
    private void floatcheck() {
        FloatSettings fs = new FloatSettings();
    }
    
    private void settings() {
        showFunctionSupressed();
    }
    
    private void hourlyreport() {
        Report r = new Report("Reports/HourlyReport.jrxml");
        r.viewReport();
    }
    
    private void endofday() {
        EndOfDay eod = new EndOfDay();
    }
    
    private void customer() {
        Customer c = new Customer();
    }
    
    private void nosale() {
        Object[] row = {Settings.get("cashDrawerType").toString(),
            Settings.get("printer")};
        CashDrawerGeneric cdg = new CashDrawerGeneric(row);
        cdg.openDrawer();
    }
    
    private void ldstop() {
        System.err.println("Stopping Line Display");
        try {
            ldg.closePort();
        } catch(Exception ex) {
            System.err.println(ex.getCause());
        }
        System.err.println("Line Display Stopped");
    }
    
    private void ldstart() {
        System.err.println("Starting Line Display");
        try {
            ldg = new LineDisplayGeneric(Settings.get("COMPort").toString());
        } catch(Exception ex) {
            System.err.println(ex.getCause());
        }
        System.err.println("Line Display Started");
    }
    
    private void searchproduct() {
        SearchProduct sp = new SearchProduct();
    }
    
    private void dayreport() {
        Report r = new Report("Reports/DaySummary.jrxml");
        r.viewReport();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }
    
    private void showFunctionSupressed() {
        JOptionPane.showMessageDialog(null, "Function Supressed", 
                "Function Supressed", JOptionPane.ERROR_MESSAGE);
    }
}
