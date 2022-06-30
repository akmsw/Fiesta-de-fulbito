package akmsw.frames;

import akmsw.utils.BackButton;
import akmsw.utils.Player;
import akmsw.utils.Position;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * Clase correspondiente a la ventana de ingreso
 * de nombres de jugadores y mezcla de los mismos
 * para el sorteo de los equipos.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 3.0.0
 *
 * @since 28/02/2021
 */
public class InputFrame extends JFrame implements ActionListener {

    // ---------------------------------------- Constantes privadas ------------------------------

    /**
     * Cantidad máxima de caracteres por nombre.
     */
    private static final int MAX_NAME_LEN = 10;

    /**
     * Cantidad de filas para el área de texto.
     */
    private static final int TEXT_AREA_ROWS = 14;

    /**
     * Cantidad de columnas para el área de texto.
     */
    private static final int TEXT_AREA_COLUMNS = 12;

    /**
     * Opciones para el combobox.
     */
    private static final String[] OPTIONS_COMBOBOX = { "Agregar defensores centrales",
                                                       "Agregar defensores laterales",
                                                       "Agregar mediocampistas",
                                                       "Agregar delanteros",
                                                       "Agregar arqueros" };

    /**
     * Opciones de distribución de jugadores.
     */
    private static final String[] OPTIONS_MIX = { "Aleatoriamente", "Por puntajes" };

    /**
     * Regex para validación de nombres.
     */
    private static final String NAMES_VALIDATION_REGEX = "[a-z A-ZÁÉÍÓÚáéíóúñÑ]+";

    /**
     * Regex para obtener información del archivo .pda.
     */
    private static final String PDA_DATA_RETRIEVE_REGEX = "[CLMFG].+>.+";

    /**
     * Nombre del archivo .pda.
     */
    private static final String PDA_FILENAME = "/dist.pda";

    // ---------------------------------------- Campos privados ----------------------------------

    private int totalAnchorages;
    private int distribution;
    private int playersPerTeam;

    private boolean anchorages;

    private List<ArrayList<JTextField>> textFields;

    private EnumMap<Position, Integer> playersAmountMap;

    private transient Map<Position, ArrayList<Player>> playersSets;

    private JButton mixButton;

    private JComboBox<String> comboBox;

    private JFrame previousFrame;

    private JPanel leftPanel;
    private JPanel rightPanel;

    private JTextArea textArea;

    // ---------------------------------------- Constructor --------------------------------------

    /**
     * Constructor de la ventana de ingreso de jugadores.
     *
     * @param previousFrame Ventana fuente que crea la ventana InputFrame.
     * @param playersPerTeam Cantidad de jugadores por equipo.
     *
     * @throws IOException Cuando hay un error de lectura en el archivo pda.
     */
    public InputFrame(JFrame previousFrame, int playersPerTeam) throws IOException {
        this.previousFrame = previousFrame;
        this.playersPerTeam = playersPerTeam;

        anchorages = false;

        setTotalAnchorages(0);

        playersAmountMap = new EnumMap<>(Position.class);

        collectPDData();

        initializeComponents("Ingreso de jugadores - Fútbol " + playersPerTeam);
    }

    // --------------------------------------- Métodos privados ---------------------------------

    /**
     * Este método obtiene la cantidad de jugadores para cada posición por equipo
     * mediante expresiones regulares.
     *
     * [CLMFG].+>.+ : Matchea las líneas que comiencen con C, L, M, F, ó W,
     * seguido por al menos un caracter '>' (esta regex busca las líneas que
     * nos importan en el archivo .pda).
     *
     * (?!(?<=X)\\d). : Matchea el trozo de la línea que no sea un número que nos
     * interesa (el número que nos interesa ocuparía el lugar de la X).
     *
     * ¡¡¡IMPORTANTE!!!
     *
     * Si el archivo .pda es modificado en cuanto a orden de las líneas importantes,
     * se debe tener en cuenta que Position.values()[index] confía en que lo hallado
     * se corresponde con el orden en el que están declarados los valores en el enum
     * Position. Idem, si se cambian de orden los valores del enum Position, se
     * deberá tener en cuenta que Position.values()[index] confía en el orden en el
     * que se leerán los datos del archivo .pda y, por consiguiente, se deberá rever
     * el orden de las líneas importantes de dichos archivos.
     *
     * @throws IOException Si el archivo no existe.
     */
    private void collectPDData() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass()
                                                                              .getResourceAsStream(PDA_FILENAME)))) {
            int index = 0;

            String line;

            while ((line = br.readLine()) != null) {
                if (line.matches(PDA_DATA_RETRIEVE_REGEX)) {
                    playersAmountMap.put(Position.values()[index],
                                         Integer.parseInt(line.replaceAll("(?!(?<=" + playersPerTeam + ")\\d).", "")));
                    index++;
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Este método llena el conjunto de jugadores recibido con
     * jugadores sin nombre y con la posición especificada.
     *
     * @param set      Arreglo de jugadores a inicializar.
     * @param position Posición de los jugadores del arreglo.
     * @param capacity Capacidad del arreglo.
     */
    private void initializeSet(ArrayList<Player> set, Position position, int capacity) {
        for (int i = 0; i < capacity; i++) {
            set.add(new Player("", position));
        }
    }

    /**
     * Este método se encarga de inicializar los componentes de la ventana de
     * mezcla.
     *
     * @param frameTitle Título de la ventana.
     */
    private void initializeComponents(String frameTitle) {
        ArrayList<JTextField> textFieldCD = new ArrayList<>();
        ArrayList<JTextField> textFieldLD = new ArrayList<>();
        ArrayList<JTextField> textFieldMF = new ArrayList<>();
        ArrayList<JTextField> textFieldFW = new ArrayList<>();
        ArrayList<JTextField> textFieldGK = new ArrayList<>();

        ArrayList<Player> setCD = new ArrayList<>();
        ArrayList<Player> setLD = new ArrayList<>();
        ArrayList<Player> setMF = new ArrayList<>();
        ArrayList<Player> setFW = new ArrayList<>();
        ArrayList<Player> setGK = new ArrayList<>();

        initializeSet(setCD, Position.CENTRAL_DEFENDER, playersAmountMap.get(Position.CENTRAL_DEFENDER) * 2);
        initializeSet(setLD, Position.LATERAL_DEFENDER, playersAmountMap.get(Position.LATERAL_DEFENDER) * 2);
        initializeSet(setMF, Position.MIDFIELDER, playersAmountMap.get(Position.MIDFIELDER) * 2);
        initializeSet(setFW, Position.FORWARD, playersAmountMap.get(Position.FORWARD) * 2);
        initializeSet(setGK, Position.GOALKEEPER, playersAmountMap.get(Position.GOALKEEPER) * 2);

        textFields = Arrays.asList(textFieldCD, textFieldLD, textFieldMF, textFieldFW, textFieldGK);

        playersSets = new TreeMap<>();

        playersSets.put(Position.CENTRAL_DEFENDER, setCD);
        playersSets.put(Position.LATERAL_DEFENDER, setLD);
        playersSets.put(Position.MIDFIELDER, setMF);
        playersSets.put(Position.FORWARD, setFW);
        playersSets.put(Position.GOALKEEPER, setGK);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(frameTitle);
        setIconImage(MainFrame.ICON.getImage());

        leftPanel = new JPanel(new MigLayout("wrap"));
        rightPanel = new JPanel(new MigLayout("wrap"));

        addComboBox();
        addTextFields(Position.CENTRAL_DEFENDER, textFieldCD, setCD);
        addTextFields(Position.LATERAL_DEFENDER, textFieldLD, setLD);
        addTextFields(Position.MIDFIELDER, textFieldMF, setMF);
        addTextFields(Position.FORWARD, textFieldFW, setFW);
        addTextFields(Position.GOALKEEPER, textFieldGK, setGK);
        addTextArea();
        addButtons();
        addAnchorCheckBox();

        // Se muestra el output correspondiente al estado inicial del JComboBox
        updateTextFields(comboBox.getSelectedItem().toString());

        JPanel masterPanel = new JPanel(new MigLayout("wrap 2"));

        leftPanel.setBackground(Main.FRAMES_BG_COLOR);
        rightPanel.setBackground(Main.FRAMES_BG_COLOR);
        masterPanel.setBackground(Main.FRAMES_BG_COLOR);

        masterPanel.add(leftPanel, "west");
        masterPanel.add(rightPanel, "east");

        add(masterPanel);

        pack();

        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Este método se encarga de crear, almacenar y configurar los campos de texto
     * correspondientes a cada posición.
     *
     * @param position     Posición a buscar en el EnumMap.
     * @param textFieldSet Arreglo de campos de texto para cada posición.
     * @param playersSet   Arreglo de jugadores donde se almacenarán los nombres
     *                     ingresados en los campos de texto.
     */
    private void addTextFields(Position position, ArrayList<JTextField> textFieldSet, ArrayList<Player> playersSet) {
        for (int i = 0; i < (playersAmountMap.get(position) * 2); i++) {
            JTextField tf = new JTextField();

            tf.addActionListener(e -> {
                int index = textFieldSet.indexOf(e.getSource());

                if (!(Pattern.matches(NAMES_VALIDATION_REGEX, tf.getText()))) {
                    JOptionPane.showMessageDialog(null,
                                          "El nombre del jugador debe estar formado sólo por letras de la A a la Z",
                                            "¡Error!", JOptionPane.ERROR_MESSAGE, null);
                } else {
                    String name = tf.getText().trim().toUpperCase().replace(" ", "_");

                    if ((name.length() > MAX_NAME_LEN) || name.isBlank()
                            || name.isEmpty() || alreadyExists(name)) {
                        JOptionPane.showMessageDialog(null,
                                                      "El nombre del jugador no puede estar vacío, tener más de "
                                                      + MAX_NAME_LEN + " caracteres, o estar repetido",
                                                "¡Error!", JOptionPane.ERROR_MESSAGE, null);

                        tf.setText("");
                    } else {
                        playersSet.get(index).setName(name);

                        updateTextArea();

                        // Se habilita el botón de mezcla sólo cuando todos los jugadores tengan nombre
                        mixButton.setEnabled(!alreadyExists(""));
                    }
                }
            });

            textFieldSet.add(tf);
        }
    }

    /**
     * Este método se encarga de agregar la lista desplegable
     * al frame y setear el handler de eventos a la misma.
     */
    private void addComboBox() {
        comboBox = new JComboBox<>(OPTIONS_COMBOBOX);

        comboBox.setSelectedIndex(0);
        comboBox.addActionListener(this);

        leftPanel.add(comboBox, "growx");
    }

    /**
     * Este método se encarga de añadir los botones
     * al panel de ingreso de jugadores.
     */
    private void addButtons() {
        BackButton backButton = new BackButton(InputFrame.this, previousFrame);

        mixButton = new JButton("Distribuir");

        mixButton.setEnabled(false);
        mixButton.setVisible(true);

        mixButton.addActionListener(e -> {
            distribution = JOptionPane.showOptionDialog(null,
                    "Seleccione el criterio de distribución de jugadores", "Antes de continuar...", 2,
                    JOptionPane.QUESTION_MESSAGE, MainFrame.SCALED_ICON, OPTIONS_MIX, OPTIONS_MIX[0]);

            if (distribution != JOptionPane.CLOSED_OPTION) {
                if (thereAreAnchorages()) {
                    AnchorageFrame anchorageFrame = new AnchorageFrame(InputFrame.this, playersPerTeam);

                    anchorageFrame.setVisible(true);
                } else if (distribution == 0) {
                    // Distribución aleatoria
                    ResultFrame resultFrame = new ResultFrame(InputFrame.this, InputFrame.this);

                    resultFrame.setVisible(true);
                } else {
                    // Distribución por puntajes
                    RatingFrame ratingFrame = new RatingFrame(InputFrame.this, InputFrame.this);

                    ratingFrame.setVisible(true);
                }

                InputFrame.this.setVisible(false);
                InputFrame.this.setLocationRelativeTo(null);
            }
        });

        rightPanel.add(mixButton, "grow");
        rightPanel.add(backButton, "grow");
    }

    /**
     * Este método se encarga de añadir al panel el campo de texto de sólo lectura
     * donde se mostrarán en tiempo real los nombres de jugadores ingresados por el
     * usuario.
     */
    private void addTextArea() {
        textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);

        textArea.setBorder(BorderFactory.createBevelBorder(1));
        textArea.setEditable(false);
        textArea.setVisible(true);

        JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.add(scrollPane, "push, grow, span");

        updateTextArea();
    }

    /**
     * Este método se encarga de agregar el checkbox de anclaje de jugadores en el
     * panel del frame.
     */
    private void addAnchorCheckBox() {
        JCheckBox anchorCheckBox = new JCheckBox("Anclar jugadores", false);

        anchorCheckBox.setBackground(Main.FRAMES_BG_COLOR);
        anchorCheckBox.setVisible(true);

        anchorCheckBox.addActionListener(e -> anchorages = !anchorages);

        rightPanel.add(anchorCheckBox, "center");
    }

    /**
     * Este método se encarga de togglear la visibilidad de los
     * campos de texto de ingreso de jugadores en base al ítem
     * seleccionado en la lista desplegable.
     *
     * @param text Opción seleccionada del arreglo OPTIONS_COMBOBOX.
     */
    private void updateTextFields(String text) {
        int index;

        for (index = 0; index < OPTIONS_COMBOBOX.length; index++) {
            if (text.equals(OPTIONS_COMBOBOX[index])) {
                break;
            }
        }

        clearLeftPanel();

        for (JTextField tf : textFields.get(index)) {
            leftPanel.add(tf, "growx");
        }

        leftPanel.revalidate();
        leftPanel.repaint();
    }

    /**
     * Este método se encarga de actualizar el texto mostrado en el campo de sólo
     * lectura. Se muestran los jugadores ingresados en el orden en el que estén
     * posicionados en sus respectivos arreglos. El orden en el que se muestran es:
     * Defensores centrales > Defensores laterales > Mediocampistas > Delanteros >
     * Arqueros.
     */
    private void updateTextArea() {
        int counter = 0;

        textArea.setText(null);

        for (Map.Entry<Position, ArrayList<Player>> ps : playersSets.entrySet()) {
            for (Player p : ps.getValue()) {
                if (!p.getName().equals("")) {
                    if (counter != 0 && playersPerTeam * 2 - counter != 0) {
                        textArea.append("\n");
                    }

                    textArea.append((counter + 1) + " - " + p.getName());

                    counter++;
                }
            }
        }
    }

    /**
     * Este método se encarga de quitar los elementos de tipo
     * JTextField en el panel izquierdo antes de togglear
     * cuáles deben permanecer en el mismo.
     */
    private void clearLeftPanel() {
        textFields.stream()
                  .flatMap(Collection::stream)
                  .filter(tf -> isComponentInPanel(tf, leftPanel))
                  .forEach(tf -> leftPanel.remove(tf));
    }

    /**
     * Este método se encarga de verificar si cierto componente es
     * parte de algún panel en particular de este JFrame.
     *
     * @param c Componente cuya pertenencia se verificará.
     * @param p Panel al cual se verificará la pertenencia.
     *
     * @return Si el componente es parte del panel especificado.
     */
    private boolean isComponentInPanel(JComponent c, JPanel p) {
        return c.getParent() == p;
    }

    /**
     * Este método se encarga de chequear si un nombre está repetido en un arreglo
     * de jugadores.
     *
     * @param name Nombre a chequear.
     *
     * @return Si hay algún jugador con el mismo nombre.
     */
    private boolean alreadyExists(String name) {
        return playersSets.values()
                          .stream()
                          .flatMap(Collection::stream)
                          .anyMatch(p -> p.getName().equals(name));
    }

    // --------------------------------------- Métodos públicos ---------------------------------

    /**
     * Handler para los eventos ocurridos de la lista desplegable.
     *
     * @param e Evento de click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateTextFields((String) ((JComboBox<?>) e.getSource()).getSelectedItem());
    }

    /**
     * @param totalAnchorages Cantidad total de anclajes existentes.
     */
    public void setTotalAnchorages(int totalAnchorages) {
        this.totalAnchorages = totalAnchorages;
    }

    /**
     * @return Cantidad total de anclajes existentes.
     */
    public int getTotalAnchorages() {
        return totalAnchorages;
    }

    /**
     * @return Tipo de distribución de jugadores elegida.
     */
    public int getDistribution() {
        return distribution;
    }

    /**
     * @return Cantidad de jugadores por equipo.
     */
    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    /**
     * @return Mapa que indica cuántos jugadores
     *         hay por posición en cada equipo.
     */
    public Map<Position, Integer> getPlayersAmountMap() {
        return playersAmountMap;
    }

    /**
     * @return Mapa con los arreglos de jugadores.
     */
    public Map<Position, ArrayList<Player>> getPlayersMap() {
        return playersSets;
    }

    /**
     * @return Si el usuario desea hacer anclajes.
     */
    public boolean thereAreAnchorages() {
        return anchorages;
    }
}