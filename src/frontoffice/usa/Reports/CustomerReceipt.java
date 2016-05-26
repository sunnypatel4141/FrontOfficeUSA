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
package frontoffice.usa.Reports;

import frontoffice.util.JRXMLTOJASPER;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Sunny Patel
 * 
 * This class is used to give a specific height to the jasper report
 * It can be called in the normal way
 */
public class CustomerReceipt extends frontoffice.base.DBConnection {
    
    String fileName = "";
    String saleID;
    JasperDesign jd;
    JFrame report = new JFrame("Transaction Receipt");
    
    public CustomerReceipt(String fileNameArgument, String saleIDArg) {
        // Get the jasperReportsContext
        fileName = fileNameArgument;
        saleID = saleIDArg;
        JRXMLTOJASPER jx = new JRXMLTOJASPER("StoreInformation");
        
        loadReport();
    }
    
    public void printReport() {
        try {
            HashMap reportdata = new HashMap();
            reportdata.put("saleid", new Integer(saleID));
            JasperReport jReport = JasperCompileManager.compileReport(jd);
            JasperPrint jPrint = JasperFillManager.fillReport(jReport, reportdata, conn);
            if (Settings.containsKey("TestMode") && "1".equals(Settings.get("TestMode"))) {
                // FIXME: Keeping this FOR TESTING ONLY
                JRViewer viewer = new JRViewer(jPrint);
                viewer.setOpaque(true);
                report.add(viewer);
                report.setSize(701, 1001);
                report.setVisible(true);
            } else {
                String printerName = Settings.get("printer").toString();
                int selectedService = 0;
                
                /* // OLD CODE MIGHT BE USEFUL FOR REFERENCE
                boolean printDocument = JasperPrintManager.printReport(jPrint, false);
                PrinterJob pj = PrinterJob.getPrinterJob();
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                for( int i = 0; i < services.length; i ++) {
                    if ( services[i].getName().equals(printerName) ) {
                        selectedService = i;
                        break;
                    }
                }
                pj.setPrintService(services[selectedService]);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jPrint);
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
                exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                
                */
                
                PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                
                PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                printServiceAttributeSet.add(new PrinterName(printerName, null));
                
                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                
                exporter.setExporterInput(new SimpleExporterInput(jPrint));
                
                SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
                configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
                configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
                configuration.setDisplayPageDialog(false);
                configuration.setDisplayPrintDialog(false);
                exporter.setConfiguration(configuration);
                
                exporter.exportReport();
            }
            
            
        } catch (JRException ex) {
            Logger.getLogger(CustomerReceipt.class.getName()).log(Level.SEVERE, null, ex);
        //} catch (PrinterException ex) {
            Logger.getLogger(CustomerReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Just want to load the report and print it.
     */
    private void loadReport() {
        try {
            JRXmlLoader jrxmlLoader = new JRXmlLoader(new org.apache.commons.digester.Digester());
            jd = jrxmlLoader.load(fileName);
            int pageHeight = getPageHeight();
            jd.setPageHeight(pageHeight);
            
        } catch (JRException ex) {
            Logger.getLogger(CustomerReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Calculates the page height based on the number of items
     */
    private int getPageHeight() {
        try {
            String sql = "select count(*) from saleitem where saleid= ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, saleID);
            rs = pstmt.executeQuery();
            int itemcount = 0;
            while(rs.next()) {
                itemcount = rs.getInt(1);
            }
            int pageHeight = (itemcount * 32) + 331;
            return pageHeight;
        } catch (SQLException ex) {
            Logger.getLogger(CustomerReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // If there is nothing return so just a single reciept
        return 165;
    }
}
