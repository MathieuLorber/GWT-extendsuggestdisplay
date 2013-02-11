package com.google.gwt.user.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The SuggestionMenu class is used for the display and selection of suggestions in the SuggestBox
 * widget. SuggestionMenu differs from MenuBar in that it always has a vertical orientation, and it
 * has no submenus. It also allows for programmatic selection of items in the menu, and
 * programmatically performing the action associated with the selected item. In the MenuBar class,
 * items cannot be selected programatically - they can only be selected when the user places the
 * mouse over a particlar item. Additional methods in SuggestionMenu provide information about the
 * number of items in the menu, and the index of the currently selected item.
 */
public class SuggestionMenu extends MenuBar {

   public SuggestionMenu(boolean vertical) {
      super(vertical);
      // Make sure that CSS styles specified for the default Menu classes
      // do not affect this menu
      setStyleName("");
      setFocusOnHoverEnabled(false);
   }

   public int getNumItems() {
      return getItems().size();
   }

   /**
    * Returns the index of the menu item that is currently selected.
    * 
    * @return returns the selected item
    */
   public int getSelectedItemIndex() {
      // The index of the currently selected item can only be
      // obtained if the menu is showing.
      MenuItem selectedItem = getSelectedItem();
      if (selectedItem != null) {
         return getItems().indexOf(selectedItem);
      }
      return -1;
   }

   /**
    * Selects the item at the specified index in the menu. Selecting the item does not perform the
    * item's associated action; it only changes the style of the item and updates the value of
    * SuggestionMenu.selectedItem.
    * 
    * @param index index
    */
   public void selectItem(int index) {
      List<MenuItem> items = getItems();
      if (index > -1 && index < items.size()) {
         itemOver(items.get(index), false);
      }
   }
}
