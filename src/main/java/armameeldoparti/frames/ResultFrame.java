package armameeldoparti.frames;

import armameeldoparti.utils.BackButton;
import armameeldoparti.utils.Main;
import armameeldoparti.utils.Player;
import armameeldoparti.utils.PlayersMixer;
import armameeldoparti.utils.Position;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import net.miginfocom.swing.MigLayout;

/**
 * Clase correspondiente a la ventana de resultados de distribución de jugadores.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 3.0.0
 *
 * @since 06/03/2021
 */
public class ResultFrame extends JFrame {

    // ---------------------------------------- Constantes privadas ------------------------------

    /**
     * Configuración utilizada frecuentemente.
     */
    private static final String GROWX = "growx";

    /**
     * Tamaño de ancho (en píxeles) fijo para las celdas de la tabla de resultados.
     * Valor ajustado a fuente del programa teniendo en cuenta su tamaño y la cantidad
     * máxima de caracteres en los nombres de los jugadores.
     */
    private static final int FIXED_CELL_WIDTH = 250;

    /**
     * Cantidad de columnas para la tabla de resultados.
     */
    private static final int TABLE_COLUMNS = 3;

    // ---------------------------------------- Campos privados ----------------------------------

    private transient List<Player> team1;
    private transient List<Player> team2;

    private transient List<List<Player>> teams;

    private JFrame previousFrame;

    private JPanel panel;

    private JTable table;

    private transient PlayersMixer mixer;

    private String frameTitle;

    private InputFrame inputFrame;

    // ---------------------------------------- Constructor --------------------------------------

    /**
     * Construye una ventana de resultados.
     *
     * @param inputFrame    Ventana de ingreso de datos, de la cual se obtendrá información importante.
     * @param previousFrame Ventana fuente que crea la ventana ResultFrame.
     */
    public ResultFrame(InputFrame inputFrame, JFrame previousFrame) {
        this.inputFrame = inputFrame;
        this.previousFrame = previousFrame;

        team1 = new ArrayList<>();
        team2 = new ArrayList<>();
        teams = new ArrayList<>();

        teams.add(team1);
        teams.add(team2);

        mixer = new PlayersMixer();

        if (inputFrame.getDistribution() == Main.RANDOM_MIX) {
            setFrameTitle("Aleatorio - ");

            table = new JTable(inputFrame.getPlayersPerTeam() + 1, TABLE_COLUMNS);

            if (inputFrame.thereAreAnchorages()) {
                setFrameTitle(getFrameTitle().concat("Con anclajes - "));
            } else {
                setFrameTitle(getFrameTitle().concat("Sin anclajes - "));

                teams = mixer.randomMix(inputFrame, getTeams(), inputFrame.thereAreAnchorages());
            }
        } else {
            setFrameTitle("Por puntajes - ");

            table = new JTable(inputFrame.getPlayersPerTeam() + 2, TABLE_COLUMNS);

            if (inputFrame.thereAreAnchorages()) {
                setFrameTitle(getFrameTitle().concat("Con anclajes - "));
            } else {
                setFrameTitle(getFrameTitle().concat("Sin anclajes - "));

                teams = mixer.ratingsMix(inputFrame, getTeams(), inputFrame.thereAreAnchorages());
            }
        }

        initializeComponents();
    }

    // ---------------------------------------- Métodos públicos ---------------------------------

    /**
     * @return El título de la ventana.
     */
    public String getFrameTitle() {
        return frameTitle;
    }

    /**
     * @return El arreglo de equipos.
     */
    public List<List<Player>> getTeams() {
        return teams;
    }

    /**
     * @param frameTitle El nuevo título para la ventana.
     */
    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
    }

    // ---------------------------------------- Métodos privados ---------------------------------

    /**
     * Inicializa los componentes de la ventana de resultados.
     */
    private void initializeComponents() {
        panel = new JPanel(new MigLayout("wrap"));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(MainFrame.ICON.getImage());

        addTable();
        addButtons();
        add(panel);

        fillTable();

        // Ajuste del ancho de las celdas
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel()
                                           .getColumn(column);

            tableColumn.setPreferredWidth(FIXED_CELL_WIDTH);
        }

        // Ajuste del alto de las celdas
        for (int i = 0; i < table.getRowCount(); i++) {
            int rowHeight = table.getRowHeight();

            for (int j = 0; j < table.getColumnCount(); j++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(i, j), i, j);

                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(i, rowHeight);
        }

        setTitle(getFrameTitle().concat("Fútbol " + inputFrame.getPlayersPerTeam()));
        setResizable(false);

        pack();

        setLocationRelativeTo(null);
    }

    /**
     * Coloca en el panel principal de la ventana los botones de navegación
     * y de redistribución en caso de ser necesario.
     */
    private void addButtons() {
        JButton mainMenuButton = new JButton("Volver al menú principal");

        BackButton backButton = new BackButton(ResultFrame.this, previousFrame, null);

        mainMenuButton.addActionListener(e -> {
            resetTeams();

            ResultFrame.this.dispose();
            inputFrame.dispose();
            previousFrame.dispose();

            MainFrame mainFrame = new MainFrame();

            mainFrame.setVisible(true);
        });

        // Se eliminan todos los equipos asignados en caso de querer retroceder
        backButton.addActionListener(e -> {
            resetTeams();

            previousFrame.setVisible(true);

            ResultFrame.this.dispose();
        });

        if (inputFrame.getDistribution() == 0) {
            JButton remixButton = new JButton("Redistribuir");

            remixButton.addActionListener(e -> {
                resetTeams();

                teams = mixer.randomMix(inputFrame, getTeams(), inputFrame.thereAreAnchorages());

                fillTable();
            });

            panel.add(remixButton, GROWX);
        }

        panel.add(backButton, GROWX);
        panel.add(mainMenuButton, GROWX);
    }

    /**
     * Coloca en el panel principal de la ventana la tabla donde se mostrarán los jugadores
     * y sus respectivas posiciones para cada equipo armado.
     */
    private void addTable() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            /**
             * Configura el color de fondo y de letra de las casillas de la tabla.
             *
             * @param table      Tabla fuente.
             * @param value      El valor a configurar en la celda.
             * @param isSelected Si la celda está seleccionada.
             * @param hasFocus   Si la celda está en foco.
             * @param row        Coordenada de fila de la celda.
             * @param column     Coordenada de columna de la celda.
             */
            @Override
            public Component getTableCellRendererComponent(JTable myTable, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(myTable, value, isSelected,
                        hasFocus, row, column);

                if (row == 0) {
                    c.setBackground(Main.DARK_GREEN);
                    c.setForeground(Color.WHITE);

                    ((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 0) {
                    if (inputFrame.getDistribution() == Main.RATINGS_MIX && row == table.getRowCount() - 1) {
                        c.setBackground(Main.LIGHT_YELLOW);
                        c.setForeground(Color.BLACK);

                        ((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        c.setBackground(Main.DARK_GREEN);
                        c.setForeground(Color.WHITE);

                        ((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.LEFT);
                    }
                } else {
                    if (inputFrame.getDistribution() == Main.RATINGS_MIX && row == table.getRowCount() - 1) {
                        c.setBackground(Main.LIGHT_YELLOW);
                        c.setForeground(Color.BLACK);

                        ((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);

                        ((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }

                return c;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setBorder(BorderFactory.createLineBorder(Main.DARK_GREEN));
        table.setEnabled(false);
        table.setVisible(true);

        ((DefaultTableCellRenderer) table.getTableHeader()
                                         .getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < 2; i++) {
            table.setValueAt("EQUIPO #" + (i + 1), 0, i + 1);
        }

        int halfCDSetLength = inputFrame.getPlayersMap()
                                        .get(Position.CENTRAL_DEFENDER)
                                        .size() / 2;
        int halfLDSetLength = inputFrame.getPlayersMap()
                                        .get(Position.LATERAL_DEFENDER)
                                        .size() / 2;
        int halfMFSetLength = inputFrame.getPlayersMap()
                                        .get(Position.MIDFIELDER)
                                        .size() / 2;
        int halfFWSetLength = inputFrame.getPlayersMap()
                                        .get(Position.FORWARD)
                                        .size() / 2;

        for (int i = 0; i < halfCDSetLength; i++) {
            table.setValueAt(Main.getPositionsMap()
                                 .get(Position.CENTRAL_DEFENDER), i + 1, 0);
        }

        for (int i = 0; i < halfLDSetLength; i++) {
            table.setValueAt(Main.getPositionsMap()
                                 .get(Position.LATERAL_DEFENDER), i + 1 + halfCDSetLength, 0);
        }

        for (int i = 0; i < halfMFSetLength; i++) {
            table.setValueAt(Main.getPositionsMap()
                                 .get(Position.MIDFIELDER),
                             i + 1 + halfCDSetLength + halfLDSetLength, 0);
        }

        for (int i = 0; i < halfFWSetLength; i++) {
            table.setValueAt(Main.getPositionsMap()
                                 .get(Position.FORWARD),
                             i + 1 + halfCDSetLength + halfLDSetLength + halfMFSetLength, 0);
        }

        table.setValueAt(Main.getPositionsMap()
                             .get(Position.GOALKEEPER),
                         1 + halfCDSetLength + halfLDSetLength + halfMFSetLength + halfFWSetLength, 0);

        if (inputFrame.getDistribution() == Main.RATINGS_MIX) {
            table.setValueAt("PUNTAJE DEL EQUIPO", table.getRowCount() - 1, 0);
        }

        panel.add(table, "push, grow, span, center");
    }

    /**
     * Llena la tabla con los datos cargados de los jugadores en cada equipo.
     */
    private void fillTable() {
        /*
         * ¡¡¡IMPORTANTE!!!
         *
         * Aquí se llenan los recuadros de la tabla confiando en el
         * orden en el que se escribieron las posiciones en las filas
         * de la columna 0 (el mismo orden del enum de posiciones).
         * Es decir, los primeros jugadores a cargar serán defensores
         * centrales, luego defensores laterales, mediocampistas,
         * delanteros y por último arqueros. Si se cambian de lugar las
         * etiquetas de las posiciones en la tabla, deberá cambiarse esta
         * manera de llenarla, ya que no se respetará el nuevo orden establecido.
         */

        for (int i = 0; i < teams.size(); i++) {
            int row = 1;

            for (Player player : teams.get(i)) {
                table.setValueAt(player.getName(), row++, i + 1);
            }
        }

        if (inputFrame.getDistribution() == Main.RATINGS_MIX) {
            table.setValueAt(team1.stream()
                                  .mapToInt(Player::getRating)
                                  .reduce(0, Math::addExact), table.getRowCount() - 1, 1);
            table.setValueAt(team2.stream()
                                  .mapToInt(Player::getRating)
                                  .reduce(0, Math::addExact), table.getRowCount() - 1, 2);
        }
    }

    /**
     * Reparte los jugadores en dos equipos de la manera más equitativa posible
     * en base a los puntajes ingresados por el usuario.
     *
     * @param anchorages Si la mezcla por puntajes debe tener en cuenta anclajes establecidos.
     */

    /**
     * Reinicia los equipos de todos los jugadores y vacía
     * los arreglos representativos de cada equipo.
     */
    private void resetTeams() {
        for (Map.Entry<Position, ArrayList<Player>> ps : inputFrame.getPlayersMap().entrySet()) {
            for (Player p : ps.getValue()) {
                p.setTeam(0);
            }
        }

        team1.clear();
        team2.clear();
    }
}
