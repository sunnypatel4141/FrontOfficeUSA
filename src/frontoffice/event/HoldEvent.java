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
package frontoffice.event;

import java.util.Vector;

/**
 *
 * @author Sunny Patel
 */
public interface HoldEvent {
    
    /**
     * This is called when a hold or an unhold button is clicked on the 
     * salesWindow. When the button is pressed it will bring up the orders
     * on hold window. When an order is selected it will then send the order
     * to the salesWindow and populate the table
     * @param saleTable
     */
    
    public void holdSaleOrder(Vector saleTable);
    
    /**
     * We might want an unhold order method
     */
}
