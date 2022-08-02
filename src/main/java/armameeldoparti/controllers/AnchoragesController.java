package armameeldoparti.controllers;

import armameeldoparti.interfaces.Controller;
import armameeldoparti.utils.Main;
import armameeldoparti.views.AnchoragesView;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 * Anchorages view controller class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since 26/07/2022
 */
public class AnchoragesController implements Controller {

  // ---------------------------------------- Private fields ------------------------------------

  private int anchoredPlayersAmount = 0;
  private int anchoragesAmount = 0;

  private AnchoragesView anchoragesView;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds the anchorages view controller.
   *
   * @param anchoragesView View to control.
   */
  public AnchoragesController(AnchoragesView anchoragesView) {
    this.anchoragesView = anchoragesView;
  }

  // ---------------------------------------- Public methods ------------------------------------

  /**
   * Makes the controlled view visible.
   */
  @Override
  public void showView() {
    anchoragesView.setVisible(true);
  }

  /**
   * Makes the controlled view invisible.
   */
  @Override
  public void hideView() {
    anchoragesView.setVisible(false);
    Controller.centerView(anchoragesView);
  }

  /**
   * Resets the controlled view to its default values.
   */
  @Override
  public void resetView() {
    clearAnchoragesButtonEvent();
  }

  /**
   * Updates the checkboxes text with the players names.
   */
  public void updateCheckBoxesText() {
    anchoragesView.updateCheckBoxesText();
    anchoragesView.pack();
  }

  /**
   * 'Finish' button event handler.
   *
   * <p>Checks if the necessary anchorages conditions are met.
   * If so, it proceeds with the players distribution.
   */
  public void finishButtonEvent() {
    if (!validAnchoragesCombination()) {
      showErrorMessage("Error message");
      return;
    }

    finish();
  }

  /**
   * 'New anchorage' button event handler.
   *
   * <p>Checks if the necessary conditions to make a new anchorage
   * are met. If so, it does.
   */
  public void newAnchorageButtonEvent() {
    int playersToAnchorAmount = (int) anchoragesView.getCheckBoxesMap()
                                                    .values()
                                                    .stream()
                                                    .flatMap(List::stream)
                                                    .filter(JCheckBox::isSelected)
                                                    .count();

    if (!validChecksAmount(playersToAnchorAmount)) {
      showErrorMessage("No puede haber más de " + Main.MAX_PLAYERS_PER_ANCHORAGE
                       + " ni menos de 2 jugadores en un mismo anclaje");
      return;
    }

    if (!validCheckedPlayersPerPosition()) {
      showErrorMessage("No puede haber más de la mitad de jugadores de una misma posición "
                       + "en un mismo anclaje");
      return;
    }

    if (!validAnchoredPlayersAmount(playersToAnchorAmount)) {
      showErrorMessage("No puede haber más de " + Main.MAX_ANCHORED_PLAYERS
                       + " jugadores anclados en total");
      return;
    }

    newAnchorage();
    updateTextArea();
    toggleButtons();
  }

  /**
   * 'Delete last anchorage' button event handler.
   *
   * <p>Deletes the last anchorage made, updating the text area and the
   * state of the buttons.
   */
  public void deleteLastAnchorageButtonEvent() {
    deleteAnchorage(anchoragesAmount);
    updateTextArea();
    toggleButtons();
  }

  /**
   * 'Delete anchorage' button event handler.
   *
   * <p>Prompts the user for the number of the anchorage to delete,
   * and removes it, updating the text area and the state of the buttons.
   */
  public void deleteAnchorageButtonEvent() {
    String[] optionsDelete = new String[anchoragesAmount];

    for (int i = 0; i < anchoragesAmount; i++) {
      optionsDelete[i] = Integer.toString(i + 1);
    }

    int anchorageToDelete = JOptionPane.showOptionDialog(null,
                                                         "Seleccione qué anclaje desea borrar",
                                                         "Antes de continuar...", 2,
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         Main.SCALED_ICON, optionsDelete,
                                                         optionsDelete[0]);

    if (anchorageToDelete != JOptionPane.CLOSED_OPTION) {
      deleteAnchorage(anchorageToDelete + 1);
      updateTextArea();
      toggleButtons();
    }
  }

  /**
   * 'Clear anchorages' button event handler.
   *
   * <p>Clears every anchorage made, updating the text area and the
   * state of the buttons.
   */
  public void clearAnchoragesButtonEvent() {
    clearAnchorages();
    updateTextArea();
    toggleButtons();
  }

  /**
   * 'Back' button event handler.
   *
   * <p>Deletes every anchorage made, resets the controlled view
   * to its default state, makes it invisible, and shows the
   * names input view.
   */
  public void backButtonEvent() {
    resetView();
    hideView();

    Main.getNamesInputController()
        .showView();
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Builds an error window with a custom message.
   *
   * @param errorMessage Custom error message to show.
   */
  private void showErrorMessage(String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, "¡Error!", JOptionPane.ERROR_MESSAGE, null);
  }

  /**
   * Sets a new anchorage based on the players checked.
   */
  private void newAnchorage() {
    anchoragesAmount++;

    anchoragesView.getCheckBoxesMap()
                  .values()
                  .stream()
                  .filter(cbs -> cbs.stream()
                                    .anyMatch(JCheckBox::isSelected))
                  .forEach(this::setAnchorages);

    anchoredPlayersAmount = (int) Main.getPlayersSets()
                                      .values()
                                      .stream()
                                      .flatMap(List::stream)
                                      .filter(p -> p.getAnchorageNumber() != 0)
                                      .count();
  }

  /**
   * Updates the text area showing the anchorages details.
   */
  private void updateTextArea() {
    anchoragesView.getTextArea()
                  .setText(null);

    var wrapperAnchorageNum = new Object() {
      private int anchorageNum;
    };

    var wrapperCounter = new Object() {
      private int counter = 1;
    };

    for (wrapperAnchorageNum.anchorageNum = 1;
         wrapperAnchorageNum.anchorageNum <= anchoragesAmount;
         wrapperAnchorageNum.anchorageNum++) {
      anchoragesView.getTextArea()
                    .append(" ----- ANCLAJE #" + wrapperAnchorageNum.anchorageNum
                            + " -----" + System.lineSeparator());

      Main.getPlayersSets()
          .entrySet()
          .forEach(ps -> ps.getValue()
                           .stream()
                           .filter(p -> p.getAnchorageNumber() == wrapperAnchorageNum.anchorageNum)
                           .forEach(p -> {
                             anchoragesView.getTextArea()
                                           .append(" " + wrapperCounter.counter + ". "
                                                   + p.getName() + System.lineSeparator());
                             wrapperCounter.counter++;
                           }));

      if (wrapperAnchorageNum.anchorageNum != anchoragesAmount) {
        anchoragesView.getTextArea()
                      .append(System.lineSeparator());
      }

      wrapperCounter.counter = 1;
    }
  }

  /**
   * Toggles the buttons and checkboxes states.
   */
  private void toggleButtons() {
    if (anchoragesAmount > 0 && anchoragesAmount < 2) {
      anchoragesView.getFinishButton()
                    .setEnabled(true);
      anchoragesView.getDeleteAnchorageButton()
                    .setEnabled(false);
      anchoragesView.getDeleteLastAnchorageButton()
                    .setEnabled(true);
      anchoragesView.getClearAnchoragesButton()
                    .setEnabled(true);
    } else if (anchoragesAmount >= 2) {
      anchoragesView.getDeleteAnchorageButton()
                    .setEnabled(true);
      anchoragesView.getDeleteLastAnchorageButton()
                    .setEnabled(true);
    } else {
      anchoragesView.getFinishButton()
                    .setEnabled(false);
      anchoragesView.getDeleteAnchorageButton()
                    .setEnabled(false);
      anchoragesView.getDeleteLastAnchorageButton()
                    .setEnabled(false);
      anchoragesView.getClearAnchoragesButton()
                    .setEnabled(false);
    }

    if (Main.MAX_ANCHORED_PLAYERS - anchoredPlayersAmount < 2) {
      anchoragesView.getNewAnchorageButton()
                    .setEnabled(false);
      anchoragesView.getCheckBoxesMap()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .forEach(cb -> cb.setEnabled(!cb.isEnabled()));
    } else {
      anchoragesView.getNewAnchorageButton()
                    .setEnabled(true);
      anchoragesView.getCheckBoxesMap()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .filter(cb -> !cb.isEnabled() && !cb.isSelected())
                    .forEach(cb -> cb.setEnabled(true));
    }
  }

  /**
   * Clears the anchorages made.
   *
   * @see armameeldoparti.controllers.AnchoragesController#deleteAnchorage(int)
   */
  private void clearAnchorages() {
    do {
      deleteAnchorage(anchoragesAmount);
    } while (anchoragesAmount > 0);
  }

  /**
   * Deletes a specific anchorage.
   *
   * <p>The players that have the specified anchorage, now
   * will have anchorage number 0.
   * If the anchorage number to delete is not the last one,
   * then the remaining players (from the chosen anchor + 1
   * to anchoragesAmount) will have their anchorage number
   * decremented by 1.
   *
   * @param anchorageToDelete Anchorage number to delete.
   */
  private void deleteAnchorage(int anchorageToDelete) {
    for (int j = 0; j < anchoragesView.getCheckBoxesMap()
                                      .size(); j++) {
      changeAnchorage(anchorageToDelete, 0);
    }

    if (anchorageToDelete != anchoragesAmount) {
      for (int k = anchorageToDelete + 1; k <= anchoragesAmount; k++) {
        for (int j = 0; j < anchoragesView.getCheckBoxesMap()
                                          .size(); j++) {
          changeAnchorage(k, k - 1);
        }
      }
    }

    anchoragesAmount--;
  }

  /**
   * Changes the anchorage number of certain players.
   *
   * <p>If the replacement is 0 (an anchorage must be removed),
   * then the players corresponding checkboxes will be visible and enabled
   * again, and the anchored players amount will be decremented as needed.
   *
   * @param target      Anchorage to replace.
   * @param replacement New anchorage number to set.
   */
  private void changeAnchorage(int target, int replacement) {
    Main.getPlayersSets()
        .values()
        .stream()
        .flatMap(List::stream)
        .filter(p -> p.getAnchorageNumber() == target)
        .forEach(p -> {
          p.setAnchorageNumber(replacement);

          if (replacement == 0) {
            anchoragesView.getCheckBoxesMap()
                          .values()
                          .stream()
                          .flatMap(List::stream)
                          .filter(cb -> cb.getText()
                                          .equals(p.getName()))
                          .forEach(cb -> {
                            cb.setVisible(true);
                            anchoredPlayersAmount--;
                          });
          }
        });
  }

  /**
   * The checkboxes that were selected whose players were not anchored,
   * are deselected. Then, shows the corresponding following view.
   */
  private void finish() {
    anchoragesView.getCheckBoxesMap()
                  .values()
                  .stream()
                  .flatMap(List::stream)
                  .filter(cb -> cb.isSelected() && cb.isVisible())
                  .forEach(cb -> cb.setSelected(false));

    if (Main.getDistribution() == Main.BY_SKILLS_MIX) {
      // By skill points distribution
      Main.getSkillsInputController()
          .showView();
    } else {
      // Random distribution
      Main.getResultsController()
          .setUp();

      Main.getResultsController()
          .showView();
    }

    hideView();
  }

  /**
   * Sets the corresponding anchorage number to the selected players.
   * Then, unchecks their checkboxes and makes them invisible.
   *
   * @param cbSet Checkboxes set with players checked.
   */
  private void setAnchorages(List<JCheckBox> cbSet) {
    Main.getPlayersSets()
        .get(Main.getCorrespondingPosition(anchoragesView.getCheckBoxesMap(), cbSet))
        .stream()
        .filter(p -> cbSet.stream()
                          .filter(JCheckBox::isSelected)
                          .anyMatch(cb -> cb.getText()
                                            .equals(p.getName())))
        .forEach(p -> p.setAnchorageNumber(anchoragesAmount));

    cbSet.stream()
         .filter(JCheckBox::isSelected)
         .forEach(cb -> {
           cb.setVisible(false);
           cb.setSelected(false);
         });
  }

  /**
   * Checks if the selected players amount is at least 2 and
   * no more than MAX_PLAYERS_PER_ANCHORAGE.
   *
   * @param playersToAnchorAmount Checked players to anchor.
   *
   * @return Whether the checked players amount is at least 2 and
   *         no more than MAX_PLAYERS_PER_ANCHORAGE or not.
   */
  private boolean validChecksAmount(int playersToAnchorAmount) {
    return playersToAnchorAmount <= Main.MAX_PLAYERS_PER_ANCHORAGE
           && playersToAnchorAmount >= 2;
  }

  /**
   * Checks if half of any players set is selected or not.
   *
   * @return Whether half of any players set is checked or not.
   */
  private boolean validCheckedPlayersPerPosition() {
    return anchoragesView.getCheckBoxesMap()
                         .values()
                         .stream()
                         .noneMatch(cbs -> cbs.stream()
                                              .filter(JCheckBox::isSelected)
                                              .count() > cbs.size() / 2);
  }

  /**
   * Checks if the selected players amount is less than the maximum allowed per anchorage.
   *
   * @param playersToAnchorAmount Checked players amount.
   *
   * @return Whether the selected players amount is less than the maximum allowed per
   *         anchorage or not.
   */
  private boolean validAnchoredPlayersAmount(int playersToAnchorAmount) {
    return anchoredPlayersAmount + playersToAnchorAmount <= Main.MAX_ANCHORED_PLAYERS;
  }

  /**
   * WIP.
   *
   * @return WIP.
   */
  private boolean validAnchoragesCombination() {
    return false;
  }
}