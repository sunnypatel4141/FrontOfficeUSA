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
 * This is for when a quick key event is called.
 * A Quick key is pressed The Main window will 
 * have it's order table populated
 * @author Sunny Patel
 */
public interface QuickKeyEvent {
    
    /**
     * This is called when a quick key button is pressed
     * @param quickKey
     */
    public void quickKeyEvent(String quickKey);
}
