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

import java.util.BitSet;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Sunny Patel
 */
/** TOTO: THIS CLASS IS MAINLY A COPY OF THE DefaultListSelectionModel class*/
public class SaleListSelectionModel implements ListSelectionModel {
    
    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private int selectionMode = SINGLE_INTERVAL_SELECTION;
    private final BitSet value = new BitSet(32);
    private int ancherSelectionIndex = -1;
    private int leadSelectionIndex = -1;
    private int selectedIndex = -1;
    private boolean valueIsAdjusting = false;
    
    protected EventListenerList listenerList = new EventListenerList();

    public void setSelection(int i) {
        i -= 1;
        value.clear();
        value.set(i);
        selectedIndex = i;
    }
    
    public int getSelectedIndex() {
        return selectedIndex; 
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        value.clear();
        value.set(index1);
        selectedIndex = index1;
        fireValueChanged(MAX, index0, valueIsAdjusting);
    }

    @Override
    public void addSelectionInterval(int index0, int index1) {
        value.set(index0, index1);
        selectedIndex = index1;
        fireValueChanged(MAX, index0, valueIsAdjusting);
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
        value.clear();
        selectedIndex = -1;
    }

    @Override
    public int getMinSelectionIndex() {
        return MIN;
    }

    @Override
    public int getMaxSelectionIndex() {
        return MAX;
    }

    @Override
    public boolean isSelectedIndex(int index) {
        // Work out the selected Index
        boolean isSelected = false;
        if ( index == selectedIndex) {
            isSelected = true;
        }
        return isSelected;
    }
    
    private void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {

        Object[] listeners = listenerList.getListenerList();
        ListSelectionEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
                }
                ((ListSelectionListener)listeners[i+1]).valueChanged(e);
            }
        }
    }

    @Override
    public int getAnchorSelectionIndex() {
        return ancherSelectionIndex;
    }

    @Override
    public void setAnchorSelectionIndex(int index) {
        ancherSelectionIndex = index;
        fireValueChanged(MAX, MIN, valueIsAdjusting);
    }

    @Override
    public int getLeadSelectionIndex() {
        return leadSelectionIndex;
    }

    @Override
    public void setLeadSelectionIndex(int index) {
        leadSelectionIndex = index;
        fireValueChanged(leadSelectionIndex, MIN, valueIsAdjusting);
    }

    @Override
    public void clearSelection() {
        // Not Really clearing becuase this is a single selection only
    }

    @Override
    public boolean isSelectionEmpty() {
        boolean selectionEmpty = false;
        if (selectedIndex == -1) {
            selectionEmpty = true;
        }
        
        return selectionEmpty;
    }

    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
        value.set(index, (length + index), before);
        // We are only interested in a single selection
        // So we are only going to select last value
        selectedIndex = index + length;
        fireValueChanged(selectedIndex, index, valueIsAdjusting);
    }

    @Override
    public void removeIndexInterval(int index0, int index1) {
        value.clear();
        selectedIndex = -1;
    }

    @Override
    public void setValueIsAdjusting(boolean valueIsAdjustingArg) {
        valueIsAdjusting = valueIsAdjustingArg;
    }

    @Override
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    @Override
    public void setSelectionMode(int selectionMode) {
        // We are only interested in having a single selection mode.
        this.selectionMode = selectionMode;
    }

    @Override
    public int getSelectionMode() {
        return selectionMode;
    }

    @Override
    public void addListSelectionListener(ListSelectionListener x) {
        listenerList.add(ListSelectionListener.class, x);
    }

    @Override
    public void removeListSelectionListener(ListSelectionListener x) {
        listenerList.remove(ListSelectionListener.class, x);
    }
}