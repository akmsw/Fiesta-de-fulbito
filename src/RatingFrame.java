/**
 * Clase correspondiente a la ventana de ingreso
 * de puntaje de jugadores.
 * 
 * @author Bonino, Francisco Ignacio.
 * 
 * @version 3.0.0
 * 
 * @since 06/03/2021
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

public class RatingFrame extends JFrame {

    /* ---------------------------------------- Campos privados ---------------------------------- */

    private JPanel panel;

    private JButton finishButton, resetButton;

    private BackButton backButton;
    
    private HashMap<Player, JSpinner> spinnersMap;

    private ResultFrame resultFrame;

    /**
     * Creación de la ventana de ingreso de puntajes.
     * 
     * @param inputFrame    Ventana de ingreso de datos, de la cual se obtendrá
     *                      información importante.
     * @param previousFrame Ventana fuente que crea la ventana RatingFrame.
     */
    public RatingFrame(InputFrame inputFrame, JFrame previousFrame) {
        panel = new JPanel(new MigLayout());

        finishButton = new JButton("Finalizar");
        resetButton = new JButton("Reiniciar puntajes");
        
        spinnersMap = new HashMap<>();

        backButton = new BackButton(RatingFrame.this, previousFrame);

        panel.setBackground(Main.FRAMES_BG_COLOR);

        finishButton.addActionListener(new ActionListener() {
            /**
             * Este método envía togglear la visibilidad de las ventanas.
             * 
             * @param e Evento de click.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnersMap.forEach((k, v) -> k.setRating((int) v.getValue()));

                resultFrame = new ResultFrame(inputFrame, RatingFrame.this);

                resultFrame.setVisible(true);

                RatingFrame.this.setVisible(false);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            /**
             * Este método se encarga de resetear las
             * puntuaciones de los jugadores.
             * 
             * @param e Evento de click.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnersMap.forEach((k, v) -> { v.setValue(1); k.setRating(0); });
            }
        });

        for (int i = 0; i < inputFrame.getPlayersSets().size(); i++) {
            JLabel label = new JLabel(Main.positions.get(i));

            label.setFont(Main.PROGRAM_FONT.deriveFont(Font.BOLD));

            panel.add(label, "span");
            panel.add(new JSeparator(JSeparator.HORIZONTAL), "growx, span");

            for (int j = 0; j < inputFrame.getPlayersSets().get(i).length; j++) {
                spinnersMap.put(inputFrame.getPlayersSets().get(i)[j],
                                new JSpinner(new SpinnerNumberModel(1, 1, 5, 1)));

                panel.add(new JLabel(inputFrame.getPlayersSets().get(i)[j].getName()), "pushx");

                if ((j % 2) != 0)
                    panel.add(spinnersMap.get(inputFrame.getPlayersSets().get(i)[j]), "wrap");
                else
                    panel.add(spinnersMap.get(inputFrame.getPlayersSets().get(i)[j]));
            }
        }

        panel.add(finishButton, "grow, span");
        panel.add(resetButton, "grow, span");
        panel.add(backButton, "grow, span");

        add(panel);

        setTitle("Puntuaciones");
        setIconImage(MainFrame.icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();

        setResizable(false);
        setLocationRelativeTo(null);
    }
}