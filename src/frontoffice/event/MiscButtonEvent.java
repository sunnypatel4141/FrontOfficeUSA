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

/**
 *
 * @author Sunny Patel
 */
public interface MiscButtonEvent {
    
    /**
     * This is called when a Misc Button is pressed from any category
     * This returns an array of name and prid
     * @param rowArg
     */
    
    public void miscButtonEvent(Object[] rowArg);
    
    /**
     * This is so that the right panel cardlayout can 
     * be shown correctly and be triggered
     * @param buttonName
     */
    
    public void miscButtonFocus(String buttonName);
}
