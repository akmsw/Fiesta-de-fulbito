package armameeldoparti.models;

import armameeldoparti.utils.Main;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Team class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 3.0.0
 *
 * @since 10/07/2022
 */
public class Team {

  // ---------------------------------------- Private fields ------------------------------------

  private List<Player> goalkeepers;
  private List<Player> centralDefenders;
  private List<Player> lateralDefenders;
  private List<Player> midfielders;
  private List<Player> forwards;

  private Map<Position, List<Player>> teamPlayers;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds a basic team with empty positions.
   */
  public Team() {
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
  }

  // ---------------------------------------- Public methods -------------------------------------

  /**
   * Clears all players lists.
   */
  public void clear() {
    teamPlayers.values()
               .forEach(List::clear);
  }

  // ---------------------------------------- Getters -------------------------------------------

  /**
   * Gets the amount of players in this team.
   *
   * @return The amount of players in this team.
   */
  public int getPlayersCount() {
    return teamPlayers.values()
                      .stream()
                      .mapToInt(List::size)
                      .sum();
  }

  /**
   * Gets the team skill points accumulated so far.
   *
   * @return The team skill points accumulated so far.
   */
  public int getTeamSkill() {
    return teamPlayers.values()
                      .stream()
                      .flatMap(List::stream)
                      .mapToInt(Player::getSkill)
                      .sum();
  }

  /**
   * Checks if a particular position players list is full.
   *
   * @param position The position to check.
   *
   * @return Whether the position players list is full or not.
   */
  public boolean isPositionFull(Position position) {
    return teamPlayers.get(position)
                      .size() == Main.getPlayersAmountMap()
                                     .get(position);
  }

  /**
   * Gets the map that associates each position with its corresponding players list.
   *
   * @return The map that associates each position with its corresponding players list.
   */
  public Map<Position, List<Player>> getPlayers() {
    return teamPlayers;
  }
}