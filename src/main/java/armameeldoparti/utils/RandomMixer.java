package armameeldoparti.utils;

import armameeldoparti.Main;
import armameeldoparti.models.Player;
import armameeldoparti.models.Positions;
import armameeldoparti.models.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Random distribution class.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 0.0.1
 *
 * @since 12/07/2022
 */
public class RandomMixer implements PlayersMixer {

  // ---------------------------------------- Private fields ------------------------------------

  private int index;
  private int chosenTeam1;
  private int chosenTeam2;

  private Random randomGenerator;

  // ---------------------------------------- Constructor ---------------------------------------

  /**
   * Builds the random distributor.
   */
  public RandomMixer() {
    randomGenerator = new Random();
  }

  // ---------------------------------------- Public methods ------------------------------------

  /**
   * Distributes the players randomly without considering anchorages.
   *
   * <p>Half of the players of each players set are randomly assigned a team number.
   *
   * <p>The rest of the players in the group without team (team == 0) are assigned
   * the oppsing team number.
   *
   * @param teams List that contains the two teams.
   *
   * @return The updated teams with the players distributed randomly
   *         without considering anchorages.
   */
  @Override
  public List<Team> withoutAnchorages(List<Team> teams) {
    updateChosenTeams(teams.size());

    List<Integer> alreadySetted = new ArrayList<>();

    for (Positions position : Positions.values()) {
      List<Player> playersSet = Main.getPlayersSets()
                                    .get(position);

      for (int i = 0; i < playersSet.size() / 2; i++) {
        updateIndex(playersSet.size(), alreadySetted);

        alreadySetted.add(index);

        Player chosenPlayer = playersSet.get(index);

        chosenPlayer.setTeam(chosenTeam1 + 1);

        teams.get(chosenTeam1)
             .getPlayers()
             .get(chosenPlayer.getPosition())
             .add(chosenPlayer);
      }

      playersSet.stream()
                .filter(p -> p.getTeam() == 0)
                .forEach(p -> {
                  p.setTeam(chosenTeam2 + 1);

                  teams.get(chosenTeam2)
                       .getPlayers()
                       .get(p.getPosition())
                       .add(p);
                });

      alreadySetted.clear();
    }

    return teams;
  }

  /**
   * Distributes the players randomly considering anchorages.
   *
   * <p>First, the anchored players are distributed among the teams
   * randomly. If a set of anchored players cannot be added to one
   * team, it will be added to the other.
   *
   * <p>The anchored players are grouped in different lists by their
   * anchorage number, and they're distributed starting from the sets
   * with most anchored players in order to avoid inconsistencies.
   *
   * <p>Then, the players that are not anchored are distributed
   * randomly. They will be added to a team only if the players
   * amount per position or the players per team amount are not
   * exceeded.
   *
   * @param teams List that contains the two teams.
   *
   * @return The updated teams with the players distributed randomly considering anchorages.
   */
  @Override
  public List<Team> withAnchorages(List<Team> teams) {
    List<List<Player>> anchoredPlayers = Main.getPlayersSets()
                                             .values()
                                             .stream()
                                             .flatMap(List::stream)
                                             .filter(Player::isAnchored)
                                             .collect(
                                               Collectors.groupingBy(Player::getAnchorageNumber))
                                             .values()
                                             .stream()
                                             .collect(Collectors.toList());

    for (List<Player> ps : anchoredPlayers) {
      updateChosenTeams(teams.size());

      int team = chosenTeam1;

      if (!anchorageCanBeAdded(teams.get(team), ps)) {
        team = chosenTeam2;
      }

      for (Player p : ps) {
        p.setTeam(team + 1);

        teams.get(team)
             .getPlayers()
             .get(p.getPosition())
             .add(p);
      }
    }

    Main.getPlayersSets()
        .values()
        .stream()
        .flatMap(List::stream)
        .filter(p -> p.getTeam() == 0)
        .forEach(p -> {
          updateChosenTeams(teams.size());

          int teamNumber = chosenTeam1;

          if (teams.get(teamNumber)
                   .isPositionFull(p.getPosition())
              || teams.get(teamNumber)
                      .getPlayersCount() + 1 > Main.PLAYERS_PER_TEAM) {
            teamNumber = chosenTeam2;
          }

          p.setTeam(teamNumber + 1);

          teams.get(teamNumber)
               .getPlayers()
               .get(p.getPosition())
               .add(p);
        });

    return teams;
  }

  // ---------------------------------------- Private methods -----------------------------------

  /**
   * Randomly updates the player selection index.
   *
   * @param range      Upper limit (exclusive) for the random number generator.
   * @param indexesSet Set where to check if the generated index is already present.
   */
  private void updateIndex(int range, List<Integer> indexesSet) {
    do {
      index = randomGenerator.nextInt(range);
    } while (indexesSet.contains(index));
  }

  /**
   * Randomly updates the team numbers.
   *
   * @param range Upper limit (exclusive) for the random number generator.
   */
  private void updateChosenTeams(int range) {
    chosenTeam1 = randomGenerator.nextInt(range);
    chosenTeam2 = 1 - chosenTeam1;
  }

  /**
   * Checks if all anchored players can be added to a team.
   *
   * <p>It checks if the position of any of the anchored players
   * in the destination team is already complete, and it checks
   * if adding them does not exceed the number of players allowed
   * per position per team. This is done in order to avoid more
   * than half of the registered players of the same position
   * remaining on the same team.
   *
   * @param team            Team where the anchored players should be added.
   * @param anchoredPlayers List containing the players with the same anchorage number.
   *
   * @return Whether all anchored players can be added to the team or not.
   */
  private boolean anchorageCanBeAdded(Team team, List<Player> anchoredPlayers) {
    return team.getPlayersCount() + anchoredPlayers.size() <= Main.PLAYERS_PER_TEAM
           && anchoredPlayers.stream()
                             .noneMatch(p -> team.isPositionFull(p.getPosition())
                                             || team.getPlayers()
                                                    .get(p.getPosition())
                                                    .size()
                                                + anchoredPlayers.stream()
                                                                 .filter(ap ->
                                                                   ap.getPosition()
                                                                   == p.getPosition())
                                                                 .count()
                                                                 > Main.getPlayersAmountMap()
                                                                       .get(p.getPosition()));
  }
}