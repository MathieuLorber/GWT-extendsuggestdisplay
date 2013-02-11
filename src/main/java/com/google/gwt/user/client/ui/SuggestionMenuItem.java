package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * Class for menu items in a SuggestionMenu. A SuggestionMenuItem differs from a MenuItem in that
 * each item is backed by a Suggestion object. The text of each menu item is derived from the
 * display string of a Suggestion object, and each item stores a reference to its Suggestion object.
 */
public class SuggestionMenuItem extends MenuItem {

   private static final String STYLENAME_DEFAULT = "item";

   private Suggestion suggestion;

   public SuggestionMenuItem(Suggestion suggestion, boolean asHTML) {
      super(suggestion.getDisplayString(), asHTML);
      // Each suggestion should be placed in a single row in the suggestion
      // menu. If the window is resized and the suggestion cannot fit on a
      // single row, it should be clipped (instead of wrapping around and
      // taking up a second row).
      DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
      setStyleName(STYLENAME_DEFAULT);
      setSuggestion(suggestion);
   }

   public Suggestion getSuggestion() {
      return suggestion;
   }

   public void setSuggestion(Suggestion suggestion) {
      this.suggestion = suggestion;
   }
}