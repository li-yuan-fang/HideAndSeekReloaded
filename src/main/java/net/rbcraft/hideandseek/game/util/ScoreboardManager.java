package net.rbcraft.hideandseek.game.util;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ScoreboardManager {

    private Scoreboard empty;

    private final LinkedHashMap<Game, Scoreboard> scoreboards = new LinkedHashMap<>();

    public void init() {
        empty = getNewScoreboard();
    }

    private static Scoreboard getNewScoreboard() {
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void start(Game game) {
        Scoreboard board = getNewScoreboard();
        scoreboards.put(game, board);
        updateScoreboard(game);
    }

    public void onCount(Game game) {
        if (game == null || !game.isGaming() || !scoreboards.containsKey(game)) return;
        Scoreboard board = scoreboards.get(game);
        board.getObjective(game.getData().name).getScore(I18n.formats("scoreboard_remain")).setScore(game.getTimeLeft());
    }

    public void stop(Game game) {
        if (game == null || !scoreboards.containsKey(game)) return;
        scoreboards.remove(game);
        for (Player player : game.getPlayers()) {
            clearScoreboard(player);
        }
    }

    public void updateScoreboard(Game game) {
        if (game == null || !game.isGaming() || !scoreboards.containsKey(game)) return;
        Scoreboard board = scoreboards.get(game);
        Objective obj = board.getObjective(game.getData().name);
        if (obj != null) obj.unregister();
        createObjective(game, board);

        for (Player player : game.getPlayers()) {
            player.setScoreboard(board);
        }
    }

    public void updateAll() {
        for (Game game : scoreboards.keySet()) {
            updateScoreboard(game);
        }
    }

    public void clearScoreboard(Player player) {
        if (player == null) return;
        player.setScoreboard(empty);
    }

    private void createObjective(Game game, Scoreboard board) {
        if (game == null || board == null || !game.isGaming()) return;
        Objective obj = board.registerNewObjective(game.getData().name, "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', game.getData().display));

        obj.getScore(I18n.formats("scoreboard_remain")).setScore(game.getTimeLeft());
        LinkedList<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String prefix = Role.getRole(game, player).getPrefix();
            if (prefix == null || prefix.length() == 0) continue;

            obj.getScore(prefix + player.getName()).setScore(-(i + 1));
        }
    }

    private enum Role {
        SEEKER,
        HIDER,
        SPECTATOR;

        public static Role getRole(Game game, Player player) {
            if (game.getSeekers().contains(player)) {
                return SEEKER;
            } else if (game.getHiders().contains(player)) {
                return HIDER;
            } else {
                return SPECTATOR;
            }
        }

        public String getPrefix() {
            return I18n.formats("scoreboard_" + name().toLowerCase());
        }
    }

}
