package com.google.gwt.user.client.ui;

import java.util.Collection;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SimpleSuggestionDisplay extends SuggestionDisplay implements HasAnimation {

   private final HTML messageLabel;
   private final SuggestionMenu suggestionMenu;
   private final PopupPanel suggestionPopup;

   /**
    * We need to keep track of the last {@link SuggestBox} because it acts as an autoHide partner
    * for the {@link PopupPanel}. If we use the same display for multiple {@link SuggestBox}, we
    * need to switch the autoHide partner.
    */
   private SuggestBox lastSuggestBox = null;

   /**
    * Sub-classes making use of {@link decorateSuggestionList} to add elements to the suggestion
    * popup _may_ want those elements to show even when there are 0 suggestions. An example would be
    * showing a "No matches" message.
    */
   private boolean hideWhenEmpty = true;

   /**
    * Object to position the suggestion display next to, instead of the associated suggest box.
    */
   private UIObject positionRelativeTo;

   /**
    * Construct a new {@link SimpleSuggestionDisplay}.
    */
   public SimpleSuggestionDisplay() {
      FlowPanel suggestionContainer = new FlowPanel();
      suggestionMenu = new SuggestionMenu(true);
      suggestionContainer.add(suggestionMenu);
      messageLabel = new HTML();
      suggestionContainer.add(messageLabel);
      suggestionPopup = createPopup();
      suggestionPopup.setWidget(decorateSuggestionList(suggestionContainer));
   }

   @Override
   public void hideSuggestions() {
      suggestionPopup.hide();
   }

   public boolean isAnimationEnabled() {
      return suggestionPopup.isAnimationEnabled();
   }

   /**
    * Check whether or not the suggestion list is hidden when there are no suggestions to display.
    * 
    * @return true if hidden when empty, false if not
    */
   public boolean isSuggestionListHiddenWhenEmpty() {
      return hideWhenEmpty;
   }

   /**
    * Check whether or not the list of suggestions is being shown.
    * 
    * @return true if the suggestions are visible, false if not
    */
   public boolean isSuggestionListShowing() {
      return suggestionPopup.isShowing();
   }

   public void setAnimationEnabled(boolean enable) {
      suggestionPopup.setAnimationEnabled(enable);
   }

   /**
    * Sets the style name of the suggestion popup.
    * 
    * @param style the new primary style name
    * @see UIObject#setStyleName(String)
    */
   public void setPopupStyleName(String style) {
      suggestionPopup.setStyleName(style);
   }

   /**
    * Sets the UI object where the suggestion display should appear next to.
    * 
    * @param uiObject the uiObject used for positioning, or null to position relative to the suggest
    *           box
    */
   public void setPositionRelativeTo(UIObject uiObject) {
      positionRelativeTo = uiObject;
   }

   /**
    * Set whether or not the suggestion list should be hidden when there are no suggestions to
    * display. Defaults to true.
    * 
    * @param hideWhenEmpty true to hide when empty, false not to
    */
   public void setSuggestionListHiddenWhenEmpty(boolean hideWhenEmpty) {
      this.hideWhenEmpty = hideWhenEmpty;
   }

   /**
    * Create the PopupPanel that will hold the list of suggestions.
    * 
    * @return the popup panel
    */
   protected PopupPanel createPopup() {
      PopupPanel p = new PopupPanel(true, false);
      p.setStyleName("gwt-SuggestBoxPopup");
      p.setPreviewingAllNativeEvents(true);
      p.setAnimationType(AnimationType.ROLL_DOWN);
      return p;
   }

   /**
    * Wrap the list of suggestions before adding it to the popup. You can override this method if
    * you want to wrap the suggestion list in a decorator.
    * 
    * @param suggestionList the widget that contains the list of suggestions
    * @return the suggestList, optionally inside of a wrapper
    */
   protected Widget decorateSuggestionList(Widget suggestionList) {
      return suggestionList;
   }

   @Override
   protected Suggestion getCurrentSelection() {
      if (!isSuggestionListShowing()) {
         return null;
      }
      MenuItem item = suggestionMenu.getSelectedItem();
      return item == null ? null : ((SuggestionMenuItem) item).getSuggestion();
   }

   /**
    * Get the {@link PopupPanel} used to display suggestions.
    * 
    * @return the popup panel
    */
   protected PopupPanel getPopupPanel() {
      return suggestionPopup;
   }

   @Override
   protected void moveSelectionDown() {
      // Make sure that the menu is actually showing. These keystrokes
      // are only relevant when choosing a suggestion.
      if (isSuggestionListShowing()) {
         // If nothing is selected, getSelectedItemIndex will return -1 and we
         // will select index 0 (the first item) by default.
         suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() + 1);
      }
   }

   @Override
   protected void moveSelectionUp() {
      // Make sure that the menu is actually showing. These keystrokes
      // are only relevant when choosing a suggestion.
      if (isSuggestionListShowing()) {
         // if nothing is selected, then we should select the last suggestion by
         // default. This is because, in some cases, the suggestions menu will
         // appear above the text box rather than below it (for example, if the
         // text box is at the bottom of the window and the suggestions will not
         // fit below the text box). In this case, users would expect to be able
         // to use the up arrow to navigate to the suggestions.
         if (suggestionMenu.getSelectedItemIndex() == -1) {
            suggestionMenu.selectItem(suggestionMenu.getNumItems() - 1);
         } else {
            suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() - 1);
         }
      }
   }

   /**
    * <b>Affected Elements:</b>
    * <ul>
    * <li>-popup = The popup that appears with suggestions.</li>
    * <li>-item# = The suggested item at the specified index.</li>
    * </ul>
    * 
    * @see UIObject#onEnsureDebugId(String)
    */
   @Override
   protected void onEnsureDebugId(String baseID) {
      suggestionPopup.ensureDebugId(baseID + "-popup");
      suggestionMenu.setMenuItemDebugIds(baseID);
   }

   @Override
   protected void showSuggestions(final SuggestBox suggestBox,
            Collection<? extends Suggestion> suggestions, boolean isDisplayStringHTML,
            boolean isAutoSelectEnabled, final SuggestionCallback callback) {
      // Hide the popup if there are no suggestions to display.
      boolean anySuggestions = (suggestions != null && suggestions.size() > 0);
      if (!anySuggestions && hideWhenEmpty) {
         if (!showNoSuggestion()) {
            // hide all
            hideSuggestions();
         } else {
            // just hide previous suggestions, keep message in popup displayed
            suggestionMenu.clearItems();
            // popup can have never been displayed before
            showPopup(suggestionPopup, suggestBox);
         }
         return;
      }

      // Hide the popup before we manipulate the menu within it. If we do not
      // do this, some browsers will redraw the popup as items are removed
      // and added to the menu.
      if (suggestionPopup.isAttached()) {
         suggestionPopup.hide();
      }

      suggestionMenu.clearItems();
      messageLabel.setVisible(false);

      int index = 0;
      for (final Suggestion curSuggestion : suggestions) {
         addSuggestion(index, curSuggestion, isDisplayStringHTML, callback);
         index++;
      }

      if (isAutoSelectEnabled && anySuggestions) {
         // Select the first item in the suggestion menu.
         suggestionMenu.selectItem(0);
      }

      // Link the popup autoHide to the TextBox.
      if (lastSuggestBox != suggestBox) {
         // If the suggest box has changed, free the old one first.
         if (lastSuggestBox != null) {
            suggestionPopup.removeAutoHidePartner(lastSuggestBox.getElement());
         }
         lastSuggestBox = suggestBox;
         suggestionPopup.addAutoHidePartner(suggestBox.getElement());
      }

      showPopup(suggestionPopup, suggestBox);
   }

   /**
    * Is called when 0 suggestions are returned. Return false to hide popup, true to let it
    * displayed. A message can be displayed inside (with showMessage).
    * 
    * @return false if popup must be hidden
    */
   // FIXME mlorber make a showNoSuggestionWidget which returns null or a widget to be displayed
   protected boolean showNoSuggestion() {
      return false;
   }

   /**
    * Can be extended to place the popup anywhere (not necessarily relative to an uiobject)
    * 
    */
   protected void showPopup(PopupPanel suggestionPopup, SuggestBox suggestBox) {
      suggestionPopup.showRelativeTo(positionRelativeTo != null ? positionRelativeTo : suggestBox);
   }

   protected void addSuggestion(int index, final Suggestion suggestion,
            boolean isDisplayStringHTML, final SuggestionCallback callback) {
      SuggestionMenuItem menuItem = new SuggestionMenuItem(suggestion, isDisplayStringHTML);
      menuItem.setScheduledCommand(new ScheduledCommand() {
         public void execute() {
            callback.onSuggestionSelected(suggestion);
         }
      });
      addSuggestionItemToMenu(menuItem);
   }

   protected void addSuggestionItemToMenu(SuggestionMenuItem menuItem) {
      suggestionMenu.addItem(menuItem);
   }

   protected void addSeparatorToMenu(MenuItemSeparator separator) {
      suggestionMenu.addSeparator(separator);
   }

   protected void showMessage(String message, boolean isHtml) {
      messageLabel.setVisible(true);
      // FIXME mlorber how GWT deals generally with isHtml ?
      if (isHtml) {
         messageLabel.setHTML(message);
      } else {
         messageLabel.setText(message);
      }
   }

   @Override
   boolean isAnimationEnabledImpl() {
      return isAnimationEnabled();
   }

   @Override
   boolean isSuggestionListShowingImpl() {
      return isSuggestionListShowing();
   }

   @Override
   void setAnimationEnabledImpl(boolean enable) {
      setAnimationEnabled(enable);
   }

   @Override
   void setPopupStyleNameImpl(String style) {
      setPopupStyleName(style);
   }

}
