package com.iwak.fishing;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class FishingManager {

    public static class Stats {
        public int fish = 0;
        public int points = 0;
    }

    private final FishingEventPlugin plugin;
    private final Map<UUID, Stats> stats = new HashMap<>();
    private final Map<Material, Integer> pointsTable = Map.of(
            Material.COD, 1,
            Material.SALMON, 2,
            Material.PUFFERFISH, 3,
            Material.TROPICAL_FISH, 5
    );

    private boolean running = false;
    private long endEpochMs = 0L;
    private BossBar bossBar;
    private BukkitTask ticker;

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isRunning() { return running; }

    public Optional<Integer> pointsFor(Material m) {
        return Optional.ofNullable(pointsTable.get(m));
    }

    // Called when a valid fish is caught
    public void addCatch(Player p, int points, Material fishType) {
        if (!running) return;

        Stats s = stats.computeIfAbsent(p.getUniqueId(), k -> new Stats());
        s.fish++;
        s.points += points;

        String fishName = formatEnumName(fishType);
        Bukkit.getServer().broadcastMessage(
                ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " caught " +
                        ChatColor.AQUA + fishName + ChatColor.YELLOW +
                        " (Total Score: " + ChatColor.GREEN + s.points + ChatColor.YELLOW + ")"
        );
    }

    private String formatEnumName(Material m) {
        String t = m.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        String[] parts = t.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }

    public void start(int seconds) {
        if (running) forceStop(false);

        running = true;
        stats.clear();

        endEpochMs = System.currentTimeMillis() + seconds * 1000L;
        bossBar = Bukkit.createBossBar(titleForRemaining(), BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        for (Player p : Bukkit.getOnlinePlayers()) bossBar.addPlayer(p);

        // Tick every second
        ticker = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long remaining = remainingSeconds();
            if (remaining <= 0) {
                forceStop(true);
                return;
            }
            double progress = Math.max(0.0, Math.min(1.0,
                    (double) remaining / Math.max(1, seconds)));
            bossBar.setProgress(progress);
            bossBar.setTitle(titleForRemaining());
        }, 0L, 20L);

        Bukkit.broadcastMessage(ChatColor.AQUA + "Fishing event started for " + seconds + "s!");
    }

    public void forceStop(boolean announceWinners) {
        if (ticker != null) { ticker.cancel(); ticker = null; }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (running && announceWinners) {
            List<Map.Entry<UUID, Stats>> top = getTop(5);
            Bukkit.broadcastMessage(ChatColor.AQUA + "Fishing event ended! Results:");
            for (int i = 0; i < Math.min(5, top.size()); i++) {
                Map.Entry<UUID, Stats> e = top.get(i);
                OfflinePlayer op = Bukkit.getOfflinePlayer(e.getKey());
                Bukkit.broadcastMessage(rankColor(i+1) + "#" + (i+1) + " " +
                        ChatColor.YELLOW + op.getName() +
                        ChatColor.GRAY + " | Fish: " + e.getValue().fish +
                        " | Score: " + ChatColor.GREEN + e.getValue().points);
            }
        }

        running = false;
        endEpochMs = 0L;
    }

    public long remainingSeconds() {
        if (!running) return 0;
        long ms = endEpochMs - System.currentTimeMillis();
        return Math.max(0, ms / 1000L);
    }

    private String titleForRemaining() {
        long s = remainingSeconds();
        long m = s / 60;
        long r = s % 60;
        return ChatColor.AQUA + "Fishing Event " + ChatColor.WHITE + String.format("(%02d:%02d)", m, r);
    }

    public void onJoin(Player p) {
        if (bossBar != null) bossBar.addPlayer(p);
    }

    public List<Map.Entry<UUID, Stats>> getTop(int n) {
        return stats.entrySet().stream()
                .sorted(Comparator.<Map.Entry<UUID, Stats>>comparingInt(e -> e.getValue().points)
                        .reversed()
                        .thenComparing(e -> e.getValue().fish, Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
    }

    public String getTopName(int rank) {
        List<Map.Entry<UUID, Stats>> t = getTop(rank);
        if (t.size() < rank) return "§7---";
        UUID id = t.get(rank - 1).getKey();
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        String name = (op.getName() != null) ? op.getName() : "§7---";
        return name;
    }

    public String getTopScore(int rank) {
        List<Map.Entry<UUID, Stats>> t = getTop(rank);
        if (t.size() < rank) return "§7---";
        return String.valueOf(t.get(rank - 1).getValue().points);
    }

    private ChatColor rankColor(int rank) {
        return switch (rank) {
            case 1 -> ChatColor.GOLD;
            case 2 -> ChatColor.YELLOW;
            case 3 -> ChatColor.GREEN;
            default -> ChatColor.GRAY;
        };
    }
}
