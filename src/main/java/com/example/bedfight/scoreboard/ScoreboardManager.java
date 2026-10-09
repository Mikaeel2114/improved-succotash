/*     */ package com.example.bedfight.scoreboard;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.Teams;
/*     */ import com.example.bedfight.game.GameManager;
/*     */ import com.example.bedfight.game.Match;
/*     */ import com.example.bedfight.game.Mode;
/*     */ import com.example.bedfight.util.ConfigMerger;
/*     */ import com.example.bedfight.util.Ping;
/*     */ import com.example.bedfight.util.Text;
/*     */ import com.example.bedfight.util.YamlFiles;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ScoreboardManager
/*     */ {
/*     */   private final BedFightPlugin plugin;
/*     */   private final File file;
/*     */   private YamlConfiguration cfg;
/*  35 */   private final Map<UUID, PlayerBoard> boards = new HashMap<>();
/*     */   private BukkitTask task;
/*     */   
/*     */   public ScoreboardManager(BedFightPlugin plugin) {
/*  39 */     this.plugin = plugin;
/*  40 */     this.file = new File(plugin.getDataFolder(), "scoreboard.yml");
/*  41 */     reload();
/*     */   }
/*     */   
/*     */   public void reload() {
/*  45 */     stop();
/*  46 */     ConfigMerger.merge((JavaPlugin)this.plugin, "scoreboard.yml");
/*  47 */     this.cfg = YamlFiles.load(this.file);
/*  48 */     if (this.cfg.getBoolean("enabled", true)) {
/*  49 */       long period = Math.max(2L, this.cfg.getLong("update-ticks", 20L));
/*  50 */       this.task = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, this::tick, 1L, period);
/*  51 */       for (UUID id : (this.plugin.getGameManager() == null) ? new ArrayList() : new ArrayList(this.plugin.getGameManager().activePlayers())) {
/*  52 */         Player p = Bukkit.getPlayer(id);
/*  53 */         if (p != null) {
/*  54 */           refresh(p);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void stop() {
/*  61 */     if (this.task != null) {
/*  62 */       this.task.cancel();
/*  63 */       this.task = null;
/*     */     } 
/*  65 */     for (UUID id : new ArrayList(this.boards.keySet())) {
/*  66 */       Player p = Bukkit.getPlayer(id);
/*  67 */       if (p != null) {
/*  68 */         p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
/*     */       }
/*     */     } 
/*  71 */     this.boards.clear();
/*     */   }
/*     */   
/*     */   private void tick() {
/*  75 */     GameManager games = this.plugin.getGameManager();
/*  76 */     Set<UUID> active = games.activePlayers();
/*  77 */     for (UUID id : active) {
/*  78 */       Player p = Bukkit.getPlayer(id);
/*  79 */       if (p != null) {
/*  80 */         refresh(p);
/*     */       }
/*     */     } 
/*  83 */     for (UUID id : new ArrayList(this.boards.keySet())) {
/*  84 */       if (!active.contains(id)) {
/*  85 */         Player p = Bukkit.getPlayer(id);
/*  86 */         if (p != null) {
/*  87 */           refresh(p); continue;
/*     */         } 
/*  89 */         this.boards.remove(id);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void refresh(Player p) {
/*     */     String title;
/*     */     List<String> lines;
/*  97 */     if (this.cfg == null || !this.cfg.getBoolean("enabled", true) || !p.isOnline()) {
/*     */       return;
/*     */     }
/* 100 */     GameManager games = this.plugin.getGameManager();
/* 101 */     Match playing = games.getMatch(p);
/* 102 */     Match spectated = games.getSpectatedMatch(p);
/*     */ 
/*     */ 
/*     */     
/* 106 */     if (playing != null) {
/* 107 */       title = this.cfg.getString("match.title", "&b&lBedFight");
/* 108 */       lines = matchLines(p, playing, "match.lines");
/* 109 */     } else if (spectated != null) {
/* 110 */       title = this.cfg.getString("spectator.title", this.cfg.getString("match.title", "&b&lBedFight"));
/* 111 */       lines = matchLines(p, spectated, "spectator.lines");
/*     */     } else {
/*     */       
/* 114 */       clear(p);
/*     */       return;
/*     */     } 
/* 117 */     PlayerBoard board = this.boards.computeIfAbsent(p.getUniqueId(), k -> new PlayerBoard(p));
/* 118 */     board.render(p, Text.color(title), lines);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear(Player p) {
/* 123 */     if (this.boards.remove(p.getUniqueId()) != null) {
/* 124 */       p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(Player p) {
/* 130 */     this.boards.remove(p.getUniqueId());
/*     */   }
/*     */   
/*     */   private List<String> matchLines(Player p, Match m, String path) {
/* 134 */     Map<String, String> ph = m.placeholders(p);
/* 135 */     ph.put("%ip%", this.cfg.getString("ip", "minemen.club"));
/* 136 */     List<String> out = new ArrayList<>();
/* 137 */     List<String> raw = this.cfg.getStringList(path);
/* 138 */     if (raw.isEmpty()) {
/* 139 */       raw = this.cfg.getStringList("match.lines");
/*     */     }
/* 141 */     for (String line : raw) {
/* 142 */       if (line.contains("%teams%")) {
/* 143 */         for (Match.TeamView tv : m.teamViews(p)) {
/* 144 */           out.add(Text.color(teamLine(tv)));
/*     */         }
/*     */         continue;
/*     */       } 
/* 148 */       out.add(Text.color(replace(line, ph)));
/*     */     } 
/* 150 */     return out;
/*     */   }
/*     */   
/*     */   private String teamLine(Match.TeamView tv) {
/* 154 */     String status, name = Teams.name(tv.color());
/*     */     
/* 156 */     if (tv.bedAlive()) {
/* 157 */       status = this.cfg.getString("match.bed-alive", "&a✔");
/* 158 */     } else if (tv.alive() > 0) {
/* 159 */       status = this.cfg.getString("match.bed-dead", "&a%alive%").replace("%alive%", String.valueOf(tv.alive()));
/*     */     } else {
/* 161 */       status = this.cfg.getString("match.eliminated", "&c✘");
/*     */     } 
/* 163 */     Map<String, String> ph = new HashMap<>();
/* 164 */     ph.put("%color%", Teams.chat(tv.color()).toString());
/* 165 */     ph.put("%letter%", name.substring(0, 1));
/* 166 */     ph.put("%name%", name);
/* 167 */     ph.put("%team_status%", status);
/* 168 */     ph.put("%you%", tv.mine() ? this.cfg.getString("match.you", " &7YOU") : "");
/* 169 */     return replace(this.cfg.getString("match.team-line", "%color%%letter% &f%name%&7: %team_status%%you%"), ph);
/*     */   }
/*     */   
/*     */   private List<String> queueLines(Player p, GameManager games) {
/* 173 */     Mode mode = games.getQueuedMode(p);
/* 174 */     long secs = Math.max(0L, (System.currentTimeMillis() - games.getQueuedSince(p)) / 1000L);
/* 175 */     Map<String, String> ph = new HashMap<>();
/* 176 */     ph.put("%player%", p.getName());
/* 177 */     ph.put("%mode%", mode.label());
/* 178 */     ph.put("%queued%", String.valueOf(games.getQueueSize(mode)));
/* 179 */     ph.put("%needed%", String.valueOf(mode.totalPlayers()));
/* 180 */     ph.put("%queue_time%", String.format("%d:%02d", new Object[] { Long.valueOf(secs / 60L), Long.valueOf(secs % 60L) }));
/* 181 */     ph.put("%ping%", String.valueOf(Ping.get(p)));
/* 182 */     ph.put("%ip%", this.cfg.getString("ip", "minemen.club"));
/* 183 */     List<String> out = new ArrayList<>();
/* 184 */     for (String raw : this.cfg.getStringList("queue.lines")) {
/* 185 */       out.add(Text.color(replace(raw, ph)));
/*     */     }
/* 187 */     return out;
/*     */   }
/*     */   
/*     */   private static String replace(String s, Map<String, String> ph) {
/* 191 */     for (Map.Entry<String, String> e : ph.entrySet()) {
/* 192 */       s = s.replace(e.getKey(), e.getValue());
/*     */     }
/* 194 */     return s;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\scoreboard\ScoreboardManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */