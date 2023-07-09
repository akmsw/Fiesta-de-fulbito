package armameeldoparti.utils.common.custom.graphical;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * Custom label class.
 *
 * <p>This class is used to instantiate a custom label that fits the overall program
 * aesthetics.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since v3.0
 */
public class CustomBorderedLabel extends JLabel {

  // ---------------------------------------- Constructors --------------------------------------

  /**
   * Builds a basic empty label using the established program aesthetics.
   *
   * @param alignment The text alignment.
   */
  public CustomBorderedLabel(int alignment) {
    super();
    setupGraphicalProperties(alignment);
  }

  /**
   * Builds a basic label using the established program aesthetics.
   *
   * @param text      The label text.
   * @param alignment The text alignment.
   */
  public CustomBorderedLabel(String text, int alignment) {
    super(text);
    setupGraphicalProperties(alignment);
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Configures the graphical properties of the label in order to fit the program aesthetics.
   *
   * @param alignment The text alignment.
   */
  private void setupGraphicalProperties(int alignment) {
    setBorder(BorderFactory.createLoweredSoftBevelBorder());
    setHorizontalAlignment(alignment);
  }
}