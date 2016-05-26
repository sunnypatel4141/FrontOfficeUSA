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
package frontoffice.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Sunny Patel
 * 
 * The core goal of this class is to create .jasper files from .jrxml files
 */
public class JRXMLTOJASPER {
    String fileName = "";
    String outputFile = "";
    String saleID;
    JasperDesign jd;
    
    /**
     * Compiles a jrxml to jasper file
     * all reports are assumed to be in the Reports Folder
     * DO NOT INCLUDE the word Report in the name and not extension
     * @params String jrxmlFileName
     */
    public JRXMLTOJASPER(String jrxmlFileName) {
        fileName = "Reports\\" + jrxmlFileName + ".jrxml";
        outputFile = "Reports\\" + jrxmlFileName + ".jasper";
        loadReport();
    }
    
    private void loadReport() {
        try {
            JRXmlLoader jrxmlLoader = new JRXmlLoader(new org.apache.commons.digester.Digester());
            jd = jrxmlLoader.load(fileName);
            //JasperReport jReport =
            JasperCompileManager.compileReportToFile(jd, outputFile);
        } catch (JRException ex) {
            Logger.getLogger(JRXMLTOJASPER.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
