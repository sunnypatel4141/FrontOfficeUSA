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

import frontoffice.base.DBConnection;
import java.util.Calendar;

/**
 *
 * @author Sunny Patel
 */
public class DaySummary extends DBConnection {
    
    // Date Format Must be in YYYY-MM-DD;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    /**
     * In this class we want to calculate the day summary regardless of the z-read
     * We will calculate the totals of the day based on the dates provided 
     * if no date is provided then we will use current date.
     */
    
    DaySummary() {
        // Set the default dates to filter by
        int yesterday = endDate.get(Calendar.DAY_OF_YEAR) - 1;
        startDate.set(Calendar.DAY_OF_YEAR, yesterday);
    }
    
    /**
     * This is for setting specific dates for the Day Summary
     * @params Calendar startDate 'yyyy-mm-dd'
     * @params Calendar endDate 'yyyy-mm-dd'
     */
    DaySummary(Calendar startDateArg, Calendar endDateArg) {
       startDate = startDateArg;
       endDate = endDateArg;
    }
    
    /**
     * Runs the Day Summary for the given dates
     */
    private void runReport() {
        // Compile and run the report
        
        // The report totals values should be like
        String sql = "select sum(cash), sum(card), sum(voucher), sum(online)from sale where " +
                "created date between '" + startDate + "' and  '" + endDate + "'";
        
        // The main report should consider
    }
    
}
