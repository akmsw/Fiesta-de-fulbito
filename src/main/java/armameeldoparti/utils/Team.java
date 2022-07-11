package armameeldoparti.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Clase representativa de los equipos.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 3.0.0
 *
 * @since 10/07/2022
 */
public class Team {

    // ---------------------------------------- Campos privados -----------------------------------

    private int teamNumber;

    private List<Player> goalkeepers;
    private List<Player> centralDefenders;
    private List<Player> lateralDefenders;
    private List<Player> midfielders;
    private List<Player> forwards;

    private Map<Position, List<Player>> teamPlayers;

    // ---------------------------------------- Constructor ---------------------------------------

    /**
     * Construye un equipo con sus posiciones vacías.
     */
    public Team(int teamNumber) {
        goalkeepers = new ArrayList<>();
        centralDefenders = new ArrayList<>();
        lateralDefenders = new ArrayList<>();
        midfielders = new ArrayList<>();
        forwards = new ArrayList<>();

        teamPlayers = new EnumMap<>(Position.class);

        teamPlayers.put(Position.GOALKEEPER, goalkeepers);
        teamPlayers.put(Position.CENTRAL_DEFENDER, centralDefenders);
        teamPlayers.put(Position.LATERAL_DEFENDER, lateralDefenders);
        teamPlayers.put(Position.MIDFIELDER, midfielders);
        teamPlayers.put(Position.FORWARD, forwards);

        setTeamNumber(teamNumber);
    }

    // ---------------------------------------- Métodos públicos ----------------------------------

    /**
     * Vacía las listas de jugadores de cada posición.
     */
    public void clear() {
        teamPlayers.values()
                   .forEach(List::clear);
    }

    // ---------------------------------------- Getters -------------------------------------------

    /**
     * @return Mapa con los jugadores de este equipo para cada posición.
     */
    public Map<Position, List<Player>> getPlayers() {
        return teamPlayers;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    // ---------------------------------------- Setters -------------------------------------------
    private void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }
}