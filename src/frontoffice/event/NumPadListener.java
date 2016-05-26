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
package frontoffice.event;

/**
 *
 * @author Sunny Patel
 */
public interface NumPadListener {
    
    /**
     * This is called when we want to change the qty, unit price or discount
     * of the sale table
     */
    
    /**
     * Takes the row that we are altering and column
     * @param value
     */
    public void editSaleTableData(String value);
}
