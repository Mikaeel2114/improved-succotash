/*     */ package com.example.bedfight.game;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.Arena;
/*     */ import com.example.bedfight.arena.Pos;
/*     */ import com.example.bedfight.arena.TeamSpec;
/*     */ import com.example.bedfight.arena.Teams;
/*     */ import com.example.bedfight.util.Defense;
/*     */ import com.example.bedfight.util.Ping;
/*     */ import com.example.bedfight.util.Sounds;
/*     */ import com.example.bedfight.util.Titles;
/*     */ import com.example.bedfight.util.WorldFiles;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.DyeColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Item;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityExplodeEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ import org.bukkit.projectiles.ProjectileSource;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ 
/*     */ public final class Match {
/*  53 */   public enum State { PREPARING, COUNTDOWN, RUNNING, ENDING; } public record TeamView(DyeColor color, boolean bedAlive, int alive, boolean mine) {
/*     */   
/*  57 */   } private record Damage(UUID by, long time) {
/*     */      }
/*     */   
/*     */   private static final class TeamState { final DyeColor color;
/*     */     final Pos spawn;
/*     */     final Pos bed;
/*  63 */     final Set<UUID> members = new LinkedHashSet<>();
/*  64 */     final Set<UUID> alive = new HashSet<>();
/*     */     boolean bedAlive = true;
/*     */     
/*     */     TeamState(DyeColor color, Pos spawn, Pos bed) {
/*  68 */       this.color = color;
/*  69 */       this.spawn = spawn;
/*  70 */       this.bed = bed;
/*     */     } }
/*     */ 
/*     */   
/*  74 */   private static final AtomicInteger IDS = new AtomicInteger();
/*     */   
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   private final GameManager games;
/*     */   private final Arena arena;
/*     */   private final Mode mode;
/*     */   private final MatchOptions options;
/*     */   private final String worldName;
/*  83 */   private final Map<DyeColor, TeamState> teams = new LinkedHashMap<>();
/*  84 */   private final Map<UUID, TeamState> byPlayer = new LinkedHashMap<>();
/*  85 */   private final Set<UUID> respawning = new HashSet<>();
/*  86 */   private final Set<UUID> spectators = new LinkedHashSet<>();
/*  87 */   private final Map<UUID, Damage> lastDamage = new HashMap<>();
/*  88 */   private final Map<UUID, Long> protectedUntil = new HashMap<>();
/*     */   
/*  90 */   private final Set<Long> placed = new HashSet<>();
/*     */   
/*  92 */   private final Set<Long> pendingDrops = new HashSet<>();
/*     */   
/*  94 */   private final Set<UUID> pickableDrops = new HashSet<>();
/*  95 */   private final Map<UUID, Integer> kills = new HashMap<>();
/*  96 */   private final Map<UUID, Integer> bedBreaks = new HashMap<>();
/*     */   
/*  98 */   private State state = State.PREPARING;
/*     */   
/*     */   private boolean finished;
/*     */   private World world;
/*     */   private BukkitTask countdownTask;
/*     */   private BukkitTask ticker;
/*     */   private int countdownLeft;
/*     */   private long startMillis;
/*     */   
/*     */   public Match(BedFightPlugin plugin, GameManager games, Arena arena, Mode mode, MatchOptions options, List<List<Player>> split) {
/* 108 */     this.plugin = plugin;
/* 109 */     this.games = games;
/* 110 */     this.arena = arena;
/* 111 */     this.mode = mode;
/* 112 */     this.options = options;
/* 113 */     this.worldName = "bf_" + arena.getWorldName() + "_" + IDS.incrementAndGet();
/* 114 */     List<DyeColor> colors = new ArrayList<>(arena.getTeams().keySet());
/* 115 */     Collections.shuffle(colors);
/* 116 */     for (int i = 0; i < split.size(); i++) {
/* 117 */       TeamSpec spec = (TeamSpec)arena.getTeams().get(colors.get(i));
/* 118 */       TeamState ts = new TeamState(spec.getColor(), spec.getSpawn(), spec.getBed());
/* 119 */       for (Player p : split.get(i)) {
/* 120 */         ts.members.add(p.getUniqueId());
/* 121 */         ts.alive.add(p.getUniqueId());
/* 122 */         this.byPlayer.put(p.getUniqueId(), ts);
/*     */       } 
/* 124 */       this.teams.put(ts.color, ts);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public State getState() {
/* 130 */     return this.state; }
/* 131 */   public String getWorldName() { return this.worldName; }
/* 132 */   public World getWorld() { return this.world; }
/* 133 */   public Arena getArena() { return this.arena; }
/* 134 */   public Mode getMode() { return this.mode; }
/* 135 */   public MatchOptions getOptions() { return this.options; } public Set<UUID> getPlayerIds() {
/* 136 */     return this.byPlayer.keySet();
/*     */   }
/* 138 */   public boolean isPlacedBlock(Block b) { return this.placed.contains(Long.valueOf(key(b))); }
/* 139 */   public void markPlaced(Block b) { this.placed.add(Long.valueOf(key(b))); } public boolean unmarkPlaced(Block b) {
/* 140 */     return this.placed.remove(Long.valueOf(key(b)));
/*     */   }
/*     */   
/*     */   public List<Player> activePlayers() {
/* 144 */     List<Player> out = new ArrayList<>();
/* 145 */     for (UUID id : this.byPlayer.keySet()) {
/* 146 */       Player p = Bukkit.getPlayer(id);
/* 147 */       if (p != null && isActive(p)) {
/* 148 */         out.add(p);
/*     */       }
/*     */     } 
/* 151 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Player> viewers() {
/* 156 */     List<Player> out = new ArrayList<>();
/* 157 */     for (UUID id : this.byPlayer.keySet()) {
/* 158 */       Player p = Bukkit.getPlayer(id);
/* 159 */       if (p != null) {
/* 160 */         out.add(p);
/*     */       }
/*     */     } 
/* 163 */     for (UUID id : this.spectators) {
/* 164 */       Player p = Bukkit.getPlayer(id);
/* 165 */       if (p != null) {
/* 166 */         out.add(p);
/*     */       }
/*     */     } 
/* 169 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isGhost(Player p) {
/* 174 */     UUID id = p.getUniqueId();
/* 175 */     if (this.spectators.contains(id)) {
/* 176 */       return true;
/*     */     }
/* 178 */     TeamState t = this.byPlayer.get(id);
/* 179 */     return (t != null && this.state == State.RUNNING && (!t.alive.contains(id) || this.respawning.contains(id)));
/*     */   }
/*     */   
/*     */   private boolean isActive(Player p) {
/* 183 */     TeamState t = this.byPlayer.get(p.getUniqueId());
/* 184 */     return (this.state == State.RUNNING && t != null && t.alive.contains(p.getUniqueId()) && 
/* 185 */       !this.respawning.contains(p.getUniqueId()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void prepare() {
/* 191 */     broadcast(String.valueOf(ChatColor.GRAY) + "Preparing your " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + this.arena.getName() + " arena...");
/* 192 */     File src = new File(Bukkit.getWorldContainer(), this.arena.getWorldName());
/* 193 */     File dst = new File(Bukkit.getWorldContainer(), this.worldName);
/* 194 */     World template = Bukkit.getWorld(this.arena.getWorldName());
/* 195 */     if (template != null) {
/* 196 */       template.save();
/*     */     }
/* 198 */     Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
/*     */           try {
/*     */             WorldFiles.copy(src.toPath(), dst.toPath());
/*     */             Bukkit.getScheduler().runTask((Plugin)this.plugin, this::begin);
/* 202 */           } catch (IOException ex) {
/*     */             this.plugin.getLogger().log(Level.SEVERE, "Could not copy arena world " + String.valueOf(src), ex);
/*     */             Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> abort("Could not prepare the arena."));
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   private void begin() {
/* 210 */     Path dst = (new File(Bukkit.getWorldContainer(), this.worldName)).toPath();
/* 211 */     if (this.state != State.PREPARING) {
/* 212 */       Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> WorldFiles.delete(dst));
/*     */       return;
/*     */     } 
/* 215 */     for (UUID id : this.byPlayer.keySet()) {
/* 216 */       if (Bukkit.getPlayer(id) == null) {
/* 217 */         abort("A player left before the match started.");
/*     */         return;
/*     */       } 
/*     */     } 
/* 221 */     this.world = Bukkit.createWorld(new WorldCreator(this.worldName));
/* 222 */     if (this.world == null) {
/* 223 */       abort("Could not load the arena.");
/*     */       return;
/*     */     } 
/* 226 */     this.world.setAutoSave(false);
/* 227 */     this.world.setKeepSpawnInMemory(false);
/* 228 */     this.world.setPVP(true);
/* 229 */     this.world.setDifficulty(Difficulty.NORMAL);
/* 230 */     this.world.setStorm(false);
/* 231 */     this.world.setTime(6000L);
/* 232 */     this.world.setGameRuleValue("doDaylightCycle", "false");
/* 233 */     this.world.setGameRuleValue("doMobSpawning", "false");
/* 234 */     this.world.setGameRuleValue("mobGriefing", "false");
/* 235 */     Pos sp = this.arena.getSpawn();
/* 236 */     this.world.setSpawnLocation(sp.blockX() + 100000, 64, sp.blockZ() + 100000);
/*     */     
/* 238 */     for (TeamSpec spec : this.arena.getTeams().values()) {
/*     */       
/* 240 */       if (!this.teams.containsKey(spec.getColor()) || !this.options.bedsEnabled) {
/* 241 */         removeBed(this.world.getBlockAt(spec.getBed().blockX(), spec.getBed().blockY(), spec.getBed().blockZ()));
/*     */       }
/*     */     } 
/* 244 */     if (!this.options.bedsEnabled) {
/* 245 */       for (TeamState t : this.teams.values()) {
/* 246 */         t.bedAlive = false;
/*     */       }
/*     */     }
/*     */     
/* 250 */     this.state = State.COUNTDOWN;
/* 251 */     for (TeamState t : this.teams.values()) {
/* 252 */       for (UUID id : t.members) {
/* 253 */         Player p = Bukkit.getPlayer(id);
/* 254 */         if (p != null) {
/* 255 */           equip(p, t);
/* 256 */           p.teleport(t.spawn.toLocation(this.world));
/* 257 */           p.sendMessage(BedFightPlugin.PREFIX + "You are on team " + BedFightPlugin.PREFIX + Teams.display(t.color) + String.valueOf(ChatColor.GRAY));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 262 */     for (Player p : viewers()) {
/* 263 */       this.plugin.getScoreboards().refresh(p);
/*     */     }
/*     */     
/* 266 */     final int seconds = Math.max(1, this.plugin.getSettings().getInt("game.countdown-seconds", 5));
/* 267 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 291 */       .countdownTask = (new BukkitRunnable() { int left = seconds; public void run() { if (Match.this.state != Match.State.COUNTDOWN) { cancel(); return; }  if (this.left <= 0) { cancel(); Match.this.startGame(); return; }  Match.this.countdownLeft = this.left; ChatColor c = (this.left > 3) ? ChatColor.GREEN : ((this.left == 3) ? ChatColor.YELLOW : ChatColor.RED); String soundKey = (this.left <= 3) ? "countdown-last" : "countdown-tick"; for (Player p : Match.this.viewers()) { Titles.send(p, String.valueOf(c) + String.valueOf(c) + String.valueOf(ChatColor.BOLD), String.valueOf(ChatColor.GRAY) + "Get ready to fight!", 0, 25, 5); Sounds.play(Match.this.plugin, p, soundKey); }  this.left--; } }).runTaskTimer((Plugin)this.plugin, 0L, 20L);
/*     */   }
/*     */   
/*     */   private void startGame() {
/* 295 */     if (this.state != State.COUNTDOWN) {
/*     */       return;
/*     */     }
/* 298 */     this.state = State.RUNNING;
/* 299 */     this.startMillis = System.currentTimeMillis();
/* 300 */     for (TeamState t : this.teams.values()) {
/* 301 */       for (UUID id : t.members) {
/* 302 */         Player p = Bukkit.getPlayer(id);
/* 303 */         if (p != null) {
/* 304 */           Titles.send(p, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.GREEN) + "FIGHT!", String.valueOf(ChatColor.GRAY) + "Team " + String.valueOf(ChatColor.GRAY) + 
/* 305 */               Teams.display(t.color) + String.valueOf(ChatColor.GRAY), 
/* 306 */               0, 30, 10);
/*     */           
/* 308 */           Sounds.play(this.plugin, p, "fight-start");
/*     */         } 
/*     */       } 
/*     */     } 
/* 312 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 326 */       .ticker = (new BukkitRunnable() { public void run() { if (Match.this.state != Match.State.RUNNING) { cancel(); return; }  if (Match.this.options.timeLimitMinutes > 0 && System.currentTimeMillis() - Match.this.startMillis >= Match.this.options.timeLimitMinutes * 60000L) { cancel(); Match.this.broadcast(String.valueOf(ChatColor.YELLOW) + "The time limit was reached."); Match.this.end(null); }  } }).runTaskTimer((Plugin)this.plugin, 20L, 20L);
/*     */   }
/*     */   
/*     */   private void abort(String reason) {
/* 330 */     if (this.state == State.ENDING) {
/*     */       return;
/*     */     }
/* 333 */     this.state = State.ENDING;
/* 334 */     broadcast(String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/* 335 */     finish(true);
/*     */   }
/*     */   
/*     */   private void end(TeamState winner) {
/* 339 */     if (this.state == State.ENDING) {
/*     */       return;
/*     */     }
/* 342 */     this.state = State.ENDING;
/* 343 */     if (winner == null) {
/* 344 */       broadcast(String.valueOf(ChatColor.YELLOW) + "The match ended in a draw.");
/*     */     } else {
/* 346 */       broadcast(Teams.display(winner.color) + Teams.display(winner.color) + " team wins the match!");
/*     */     } 
/* 348 */     for (TeamState t : this.teams.values()) {
/* 349 */       boolean won = (t == winner);
/* 350 */       for (UUID id : t.members) {
/* 351 */         Player p = Bukkit.getPlayer(id);
/* 352 */         if (p == null) {
/*     */           continue;
/*     */         }
/* 355 */         if (winner == null) {
/* 356 */           Titles.send(p, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + "DRAW", "", 0, 60, 10);
/* 357 */           Sounds.play(this.plugin, p, "draw");
/*     */           continue;
/*     */         } 
/* 360 */         if (won) {
/* 361 */           Titles.send(p, String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.GOLD) + "VICTORY!", String.valueOf(ChatColor.GRAY) + "You won the BedFight!", 0, 80, 10);
/*     */           
/* 363 */           Sounds.play(this.plugin, p, "victory");
/*     */           continue;
/*     */         } 
/* 366 */         Titles.send(p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "DEFEAT", String.valueOf(ChatColor.GRAY) + "Better luck next time!", 0, 80, 10);
/*     */         
/* 368 */         Sounds.play(this.plugin, p, "defeat");
/*     */       } 
/*     */     } 
/* 371 */     long delay = this.plugin.getSettings().getLong("game.end-delay-seconds", 8L) * 20L;
/* 372 */     Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> finish(true), delay);
/*     */   }
/*     */   
/*     */   public void forceEnd() {
/* 376 */     this.state = State.ENDING;
/* 377 */     finish(false);
/*     */   }
/*     */   
/*     */   private void finish(boolean async) {
/* 381 */     if (this.finished) {
/*     */       return;
/*     */     }
/* 384 */     this.finished = true;
/* 385 */     if (this.countdownTask != null) {
/* 386 */       this.countdownTask.cancel();
/*     */     }
/* 388 */     if (this.ticker != null) {
/* 389 */       this.ticker.cancel();
/*     */     }
/* 391 */     List<Player> everyone = viewers();
/* 392 */     for (Player p : everyone) {
/* 393 */       if (this.world != null && p.getWorld().equals(this.world)) {
/* 394 */         resetPlayer(p);
/* 395 */         p.teleport(this.plugin.getLobby());
/*     */       } 
/*     */     } 
/* 398 */     this.games.unregister(this);
/* 399 */     for (Player p : everyone) {
/* 400 */       this.plugin.getScoreboards().refresh(p);
/*     */     }
/* 402 */     if (this.world != null) {
/* 403 */       Bukkit.unloadWorld(this.world, false);
/* 404 */       Path dir = (new File(Bukkit.getWorldContainer(), this.worldName)).toPath();
/* 405 */       if (async) {
/* 406 */         Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> WorldFiles.delete(dir));
/*     */       } else {
/* 408 */         WorldFiles.delete(dir);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onQuit(Player p) {
/* 416 */     leave(p, p.getName() + " disconnected.");
/*     */   }
/*     */   
/*     */   public void onLeave(Player p) {
/* 420 */     leave(p, p.getName() + " left the match.");
/*     */   }
/*     */   
/*     */   private void leave(Player p, String message) {
/* 424 */     if (this.state == State.PREPARING || this.state == State.COUNTDOWN) {
/* 425 */       abort(p.getName() + " left before the match started.");
/*     */       return;
/*     */     } 
/* 428 */     if (this.state == State.RUNNING) {
/* 429 */       this.games.detach(p);
/* 430 */       resetPlayer(p);
/* 431 */       if (p.isOnline()) {
/* 432 */         p.teleport(this.plugin.getLobby());
/*     */       }
/* 434 */       eliminate(p, String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.GRAY), false);
/* 435 */     } else if (this.state == State.ENDING && this.world != null && p.getWorld().equals(this.world)) {
/* 436 */       this.games.detach(p);
/* 437 */       resetPlayer(p);
/* 438 */       p.teleport(this.plugin.getLobby());
/*     */     } 
/* 440 */     this.plugin.getScoreboards().refresh(p);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addSpectator(Player p, Player target) {
/* 445 */     this.spectators.add(p.getUniqueId());
/* 446 */     this.plugin.getSpectators().enable(p, this, true);
/*     */ 
/*     */     
/* 449 */     Location to = target.getWorld().equals(this.world) ? target.getLocation().clone().add(0.0D, 2.0D, 0.0D) : this.arena.getSpawn().level().toLocation(this.world);
/* 450 */     p.teleport(to);
/* 451 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "You are now spectating " + String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.WHITE) + target.getName() + ". Use /leave to stop.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeSpectator(Player p, boolean toLobby) {
/* 456 */     this.spectators.remove(p.getUniqueId());
/* 457 */     this.plugin.getSpectators().disable(p);
/* 458 */     if (toLobby && p.isOnline()) {
/* 459 */       resetPlayer(p);
/* 460 */       p.teleport(this.plugin.getLobby());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onPlace(BlockPlaceEvent e) {
/* 471 */     Player p = e.getPlayer();
/* 472 */     Block b = e.getBlock();
/* 473 */     if (!isActive(p)) {
/* 474 */       e.setCancelled(true);
/*     */       return;
/*     */     } 
/* 477 */     if (!this.arena.inBuildArea(b.getX(), b.getY(), b.getZ())) {
/* 478 */       e.setCancelled(true);
/* 479 */       p.sendMessage(this.plugin.getSettings().message("cannot-build", "&cYou cannot build here!"));
/* 480 */       Sounds.play(this.plugin, p, "cannot-build");
/*     */       return;
/*     */     } 
/* 483 */     e.setCancelled(false);
/* 484 */     e.setBuild(true);
/* 485 */     this.placed.add(Long.valueOf(key(b)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBedDefense(Block b) {
/* 493 */     return Defense.isBreakable(this.plugin, b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onBreak(BlockBreakEvent e) {
/* 498 */     Player p = e.getPlayer();
/* 499 */     Block b = e.getBlock();
/* 500 */     if (!isActive(p)) {
/* 501 */       e.setCancelled(true);
/*     */       return;
/*     */     } 
/* 504 */     if (b.getType() == Material.BED_BLOCK) {
/* 505 */       e.setCancelled(true);
/* 506 */       breakBed(p, b);
/*     */       return;
/*     */     } 
/* 509 */     if (this.placed.remove(Long.valueOf(key(b))) || isBedDefense(b)) {
/* 510 */       e.setCancelled(false);
/* 511 */       if (Defense.dropsItems(this.plugin, b)) {
/* 512 */         long k = key(b);
/* 513 */         this.pendingDrops.add(Long.valueOf(k));
/*     */         
/* 515 */         Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.pendingDrops.remove(Long.valueOf(k)));
/*     */       } 
/*     */     } else {
/* 518 */       e.setCancelled(true);
/* 519 */       String cb = this.plugin.getSettings().message("cannot-break", "&cYou can only break blocks placed by players!");
/* 520 */       if (!cb.isEmpty()) {
/* 521 */         p.sendMessage(cb.replace("%block%", b.getType().name()));
/*     */       }
/* 523 */       Sounds.play(this.plugin, p, "cannot-break");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean allowItemSpawn(Item item) {
/* 529 */     Location l = item.getLocation();
/* 530 */     if (this.pendingDrops.contains(Long.valueOf(key(l.getBlockX(), l.getBlockY(), l.getBlockZ())))) {
/* 531 */       this.pickableDrops.add(item.getUniqueId());
/* 532 */       return true;
/*     */     } 
/* 534 */     return false;
/*     */   }
/*     */   
/*     */   public boolean canPickup(Player p, Item item) {
/* 538 */     if (!isActive(p) || !this.pickableDrops.contains(item.getUniqueId())) {
/* 539 */       return false;
/*     */     }
/* 541 */     this.pickableDrops.remove(item.getUniqueId());
/* 542 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onExplode(EntityExplodeEvent e) {
/* 547 */     e.setYield(0.0F);
/* 548 */     e.blockList().removeIf(b -> (!this.placed.contains(Long.valueOf(key(b))) && !isBedDefense(b)));
/* 549 */     for (Block b : e.blockList()) {
/* 550 */       this.placed.remove(Long.valueOf(key(b)));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDamage(EntityDamageEvent e) {
/*     */     Player p;
/* 557 */     Entity entity = e.getEntity(); if (entity instanceof Player) { p = (Player)entity; }
/*     */     else
/*     */     { return; }
/* 560 */      if (this.state != State.RUNNING || !isActive(p)) {
/* 561 */       e.setCancelled(true);
/*     */       return;
/*     */     } 
/* 564 */     Long until = this.protectedUntil.get(p.getUniqueId());
/* 565 */     if (until != null && until.longValue() > System.currentTimeMillis()) {
/* 566 */       e.setCancelled(true);
/*     */       return;
/*     */     } 
/* 569 */     if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !this.options.fallDamage) {
/* 570 */       e.setCancelled(true);
/*     */       return;
/*     */     } 
/* 573 */     if (e instanceof EntityDamageByEntityEvent) { EntityDamageByEntityEvent be = (EntityDamageByEntityEvent)e;
/* 574 */       Player attacker = attacker(be.getDamager());
/* 575 */       if (attacker != null) {
/* 576 */         if (!isActive(attacker) || this.byPlayer.get(attacker.getUniqueId()) == this.byPlayer.get(p.getUniqueId())) {
/* 577 */           e.setCancelled(true);
/*     */           return;
/*     */         } 
/* 580 */         this.lastDamage.put(p.getUniqueId(), new Damage(attacker.getUniqueId(), System.currentTimeMillis()));
/*     */       }  }
/*     */     
/* 583 */     if (e.getCause() == EntityDamageEvent.DamageCause.VOID || p.getHealth() - e.getFinalDamage() <= 0.0D) {
/* 584 */       e.setCancelled(true);
/* 585 */       handleDeath(p);
/*     */       return;
/*     */     } 
/* 588 */     e.setCancelled(false);
/*     */   }
/*     */   
/*     */   public void onMove(PlayerMoveEvent e) {
/* 592 */     Player p = e.getPlayer();
/* 593 */     Location to = e.getTo();
/* 594 */     Location from = e.getFrom();
/*     */     
/* 596 */     if (this.state == State.COUNTDOWN) {
/* 597 */       if (this.byPlayer.containsKey(p.getUniqueId()) && !this.spectators.contains(p.getUniqueId()) && (
/* 598 */         from.getX() != to.getX() || from.getZ() != to.getZ() || to.getY() > from.getY())) {
/* 599 */         Location fixed = from.clone();
/* 600 */         fixed.setYaw(to.getYaw());
/* 601 */         fixed.setPitch(to.getPitch());
/* 602 */         e.setTo(fixed);
/*     */       } 
/*     */       
/* 605 */       if (!isGhost(p)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 611 */     if (isGhost(p) || this.spectators.contains(p.getUniqueId())) {
/* 612 */       if (to.getY() < (this.arena.getMinY() - 2)) {
/* 613 */         e.setTo(this.arena.getSpawn().level().toLocation(this.world));
/*     */         return;
/*     */       } 
/* 616 */       int pad = Math.max(0, this.plugin.getSettings().getInt("spectator.boundary-padding", 8));
/* 617 */       if (!this.arena.inHorizontalRange(to.getX(), to.getZ(), pad) || to.getY() > (this.arena.getMaxY() + 40)) {
/* 618 */         Location back = from.clone();
/* 619 */         back.setYaw(to.getYaw());
/* 620 */         back.setPitch(to.getPitch());
/* 621 */         e.setTo(back);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 626 */     if (this.state != State.RUNNING || to.getY() >= this.arena.getMinY()) {
/*     */       return;
/*     */     }
/* 629 */     if (isActive(p)) {
/* 630 */       handleDeath(p);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void breakBed(Player p, Block b) {
/* 637 */     TeamState owner = bedOwner(b);
/* 638 */     if (owner == null) {
/*     */       return;
/*     */     }
/* 641 */     TeamState mine = this.byPlayer.get(p.getUniqueId());
/* 642 */     if (owner == mine) {
/* 643 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You can't break your own bed!");
/* 644 */       Sounds.play(this.plugin, p, "own-bed");
/*     */       return;
/*     */     } 
/* 647 */     if (!owner.bedAlive) {
/*     */       return;
/*     */     }
/* 650 */     owner.bedAlive = false;
/* 651 */     this.bedBreaks.merge(p.getUniqueId(), Integer.valueOf(1), Integer::sum);
/* 652 */     removeBed(b);
/* 653 */     broadcast(String.valueOf(ChatColor.GRAY) + "The bed of team " + String.valueOf(ChatColor.GRAY) + Teams.display(owner.color) + " was destroyed by " + String.valueOf(ChatColor.GRAY) + 
/* 654 */         String.valueOf(Teams.chat(mine.color)) + p.getName() + "!");
/* 655 */     Sounds.play(this.plugin, p, "bed-break");
/* 656 */     for (UUID id : owner.members) {
/* 657 */       Player victim = Bukkit.getPlayer(id);
/* 658 */       if (victim != null) {
/* 659 */         Titles.send(victim, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "BED DESTROYED!", String.valueOf(ChatColor.GRAY) + "You will no longer respawn!", 0, 50, 10);
/*     */         
/* 661 */         Sounds.play(this.plugin, victim, "bed-destroyed");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private TeamState bedOwner(Block b) {
/* 667 */     for (TeamState t : this.teams.values()) {
/* 668 */       int dx = Math.abs(b.getX() - t.bed.blockX());
/* 669 */       int dz = Math.abs(b.getZ() - t.bed.blockZ());
/* 670 */       if (b.getY() == t.bed.blockY() && dx + dz <= 1) {
/* 671 */         return t;
/*     */       }
/*     */     } 
/* 674 */     return null;
/*     */   }
/*     */   
/*     */   private static void removeBed(Block b) {
/* 678 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
/* 679 */     for (BlockFace f : faces) {
/* 680 */       Block n = b.getRelative(f);
/* 681 */       if (n.getType() == Material.BED_BLOCK) {
/* 682 */         n.setType(Material.AIR);
/*     */       }
/*     */     } 
/* 685 */     b.setType(Material.AIR);
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleDeath(Player p) {
/*     */     String how;
/* 691 */     if (!isActive(p)) {
/*     */       return;
/*     */     }
/* 694 */     TeamState t = this.byPlayer.get(p.getUniqueId());
/* 695 */     Damage d = this.lastDamage.remove(p.getUniqueId());
/* 696 */     long creditMs = Math.max(1, this.plugin.getSettings().getInt("game.kill-credit-seconds", 10)) * 1000L;
/* 697 */     Player killer = null;
/* 698 */     if (d != null && System.currentTimeMillis() - d.time() < creditMs) {
/* 699 */       Player k = Bukkit.getPlayer(d.by());
/* 700 */       if (k != null && !k.equals(p) && this.byPlayer.containsKey(k.getUniqueId())) {
/* 701 */         killer = k;
/*     */       }
/*     */     } 
/* 704 */     boolean finalKill = !t.bedAlive;
/*     */     
/* 706 */     String who = String.valueOf(Teams.chat(t.color)) + String.valueOf(Teams.chat(t.color));
/*     */     
/* 708 */     if (killer != null) {
/* 709 */       TeamState kt = this.byPlayer.get(killer.getUniqueId());
/* 710 */       how = String.valueOf(ChatColor.GRAY) + " was killed by " + String.valueOf(ChatColor.GRAY) + String.valueOf(Teams.chat(kt.color)) + killer.getName() + ".";
/* 711 */       this.kills.merge(killer.getUniqueId(), Integer.valueOf(1), Integer::sum);
/* 712 */       Sounds.play(this.plugin, killer, finalKill ? "final-kill" : "kill");
/*     */     } else {
/* 714 */       how = String.valueOf(ChatColor.GRAY) + " died.";
/*     */     } 
/* 716 */     Sounds.play(this.plugin, p, "death");
/* 717 */     broadcast(who + who + how);
/*     */     
/* 719 */     resetPlayer(p);
/* 720 */     if (t.bedAlive) {
/* 721 */       respawn(p, t);
/*     */     } else {
/* 723 */       Titles.send(p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "ELIMINATED", String.valueOf(ChatColor.GRAY) + "You can't respawn.", 0, 50, 10);
/*     */       
/* 725 */       eliminate(p, null, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void respawn(Player p, final TeamState t) {
/* 730 */     final UUID id = p.getUniqueId();
/* 731 */     this.respawning.add(id);
/*     */     
/* 733 */     this.plugin.getSpectators().enable(p, this, false);
/* 734 */     p.teleport(this.arena.waitSpawn(t.color).toLocation(this.world));
/* 735 */     final int total = Math.max(1, this.options.respawnDelaySeconds);
/* 736 */     (new BukkitRunnable() {
/* 737 */         int left = total;
/*     */ 
/*     */         
/*     */         public void run() {
/* 741 */           Player pl = Bukkit.getPlayer(id);
/* 742 */           if (Match.this.state != Match.State.RUNNING || !Match.this.respawning.contains(id) || pl == null || !t.alive.contains(id)) {
/* 743 */             Match.this.respawning.remove(id);
/* 744 */             cancel();
/*     */             return;
/*     */           } 
/* 747 */           if (this.left <= 0) {
/* 748 */             cancel();
/* 749 */             Match.this.respawning.remove(id);
/* 750 */             Match.this.plugin.getSpectators().disable(pl);
/* 751 */             Match.this.equip(pl, t);
/* 752 */             pl.teleport(t.spawn.toLocation(Match.this.world));
/* 753 */             long prot = Math.max(0, Match.this.plugin.getSettings().getInt("game.respawn-protection-seconds", 3)) * 1000L;
/* 754 */             Match.this.protectedUntil.put(id, Long.valueOf(System.currentTimeMillis() + prot));
/* 755 */             Titles.send(pl, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.GREEN) + "RESPAWNED", "", 0, 20, 10);
/* 756 */             Sounds.play(Match.this.plugin, pl, "respawn");
/*     */             return;
/*     */           } 
/* 759 */           Titles.send(pl, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "YOU DIED!", String.valueOf(ChatColor.YELLOW) + "Respawning in " + String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.RED) + this.left + "s", 0, 25, 0);
/*     */           
/* 761 */           this.left--;
/*     */         }
/* 763 */       }).runTaskTimer((Plugin)this.plugin, 0L, 20L);
/*     */   }
/*     */   
/*     */   private void eliminate(Player p, String message, boolean spectate) {
/* 767 */     TeamState t = this.byPlayer.get(p.getUniqueId());
/* 768 */     if (t == null || !t.alive.remove(p.getUniqueId())) {
/*     */       return;
/*     */     }
/* 771 */     this.respawning.remove(p.getUniqueId());
/* 772 */     if (message != null) {
/* 773 */       broadcast(message);
/*     */     }
/* 775 */     if (spectate && p.isOnline() && this.world != null) {
/* 776 */       this.plugin.getSpectators().enable(p, this, true);
/* 777 */       p.teleport(this.arena.waitSpawn(t.color).toLocation(this.world));
/*     */     } 
/* 779 */     checkWin();
/*     */   }
/*     */   
/*     */   private void checkWin() {
/* 783 */     if (this.state != State.RUNNING) {
/*     */       return;
/*     */     }
/* 786 */     List<TeamState> left = new ArrayList<>();
/* 787 */     for (TeamState t : this.teams.values()) {
/* 788 */       if (!t.alive.isEmpty()) {
/* 789 */         left.add(t);
/*     */       }
/*     */     } 
/* 792 */     if (left.size() <= 1) {
/* 793 */       end(left.isEmpty() ? null : left.get(0));
/*     */     }
/*     */   }
/*     */   
/*     */   private static Player attacker(Entity damager) {
/* 798 */     if (damager instanceof Player) { Player p = (Player)damager;
/* 799 */       return p; }
/*     */     
/* 801 */     if (damager instanceof Projectile) { Projectile proj = (Projectile)damager;
/* 802 */       ProjectileSource src = proj.getShooter();
/* 803 */       if (src instanceof Player) { Player p = (Player)src;
/* 804 */         return p; }
/*     */        }
/*     */     
/* 807 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void broadcast(String message) {
/* 813 */     for (Player p : viewers()) {
/* 814 */       p.sendMessage(BedFightPlugin.PREFIX + BedFightPlugin.PREFIX);
/*     */     }
/*     */   }
/*     */   
/*     */   private static long key(Block b) {
/* 819 */     return key(b.getX(), b.getY(), b.getZ());
/*     */   }
/*     */ 
/*     */   
/*     */   private static long key(int x, int y, int z) {
/* 824 */     return (x & 0x3FFFFFF) << 38L | (z & 0x3FFFFFF) << 12L | y & 0xFFFL;
/*     */   }
/*     */   
/*     */   private void resetPlayer(Player p) {
/* 828 */     this.plugin.getSpectators().disable(p);
/* 829 */     p.setGameMode(GameMode.SURVIVAL);
/* 830 */     p.getInventory().clear();
/* 831 */     p.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
/* 832 */     p.setHealth(p.getMaxHealth());
/* 833 */     p.setFoodLevel(20);
/* 834 */     p.setSaturation(20.0F);
/* 835 */     p.setFireTicks(0);
/* 836 */     p.setFallDistance(0.0F);
/* 837 */     for (PotionEffect pe : new ArrayList(p.getActivePotionEffects())) {
/* 838 */       p.removePotionEffect(pe.getType());
/*     */     }
/*     */   }
/*     */   
/*     */   private void equip(Player p, TeamState t) {
/* 843 */     resetPlayer(p);
/* 844 */     this.plugin.getKitManager().apply(p, t.color);
/* 845 */     p.updateInventory();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<TeamView> teamViews(Player viewer) {
/* 851 */     TeamState mine = this.byPlayer.get(viewer.getUniqueId());
/* 852 */     List<TeamView> out = new ArrayList<>();
/* 853 */     for (TeamState t : this.teams.values()) {
/* 854 */       out.add(new TeamView(t.color, t.bedAlive, t.alive.size(), (t == mine)));
/*     */     }
/* 856 */     return out;
/*     */   }
/*     */   public Map<String, String> placeholders(Player viewer) {
/*     */     String status;
/* 860 */     Map<String, String> m = new HashMap<>();
/* 861 */     TeamState mine = this.byPlayer.get(viewer.getUniqueId());
/* 862 */     List<String> enemyNames = new ArrayList<>();
/* 863 */     int pingSum = 0;
/* 864 */     int pingCount = 0;
/* 865 */     for (TeamState t : this.teams.values()) {
/* 866 */       if (t == mine) {
/*     */         continue;
/*     */       }
/* 869 */       for (UUID id : t.members) {
/* 870 */         Player pl = Bukkit.getPlayer(id);
/* 871 */         enemyNames.add((pl != null) ? pl.getName() : "?");
/* 872 */         if (pl != null) {
/* 873 */           pingSum += Ping.get(pl);
/* 874 */           pingCount++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 879 */     long elapsed = (this.state == State.RUNNING || this.state == State.ENDING) ? Math.max(0L, (System.currentTimeMillis() - this.startMillis) / 1000L) : 0L;
/*     */     
/* 881 */     switch (this.state) { case PREPARING:
/* 882 */         status = "Preparing"; break;
/* 883 */       case COUNTDOWN: status = "Starting in " + this.countdownLeft + "s"; break;
/* 884 */       case RUNNING: status = "Fighting"; break;
/* 885 */       default: status = "Ending"; break; }
/*     */     
/* 887 */     m.put("%player%", viewer.getName());
/* 888 */     m.put("%mode%", this.mode.label());
/* 889 */     m.put("%map%", this.arena.getName());
/* 890 */     m.put("%team%", (mine != null) ? Teams.display(mine.color) : "-");
/* 891 */     m.put("%opponent%", String.join(", ", (Iterable)enemyNames));
/* 892 */     m.put("%ping%", String.valueOf(Ping.get(viewer)));
/* 893 */     m.put("%opponent_ping%", String.valueOf((pingCount == 0) ? 0 : (pingSum / pingCount)));
/* 894 */     m.put("%status%", status);
/* 895 */     m.put("%countdown%", String.valueOf(this.countdownLeft));
/* 896 */     m.put("%time%", String.format("%d:%02d", new Object[] { Long.valueOf(elapsed / 60L), Long.valueOf(elapsed % 60L) }));
/* 897 */     m.put("%kills%", String.valueOf(this.kills.getOrDefault(viewer.getUniqueId(), Integer.valueOf(0))));
/* 898 */     m.put("%bed_breaks%", String.valueOf(this.bedBreaks.getOrDefault(viewer.getUniqueId(), Integer.valueOf(0))));
/* 899 */     m.put("%spectators%", String.valueOf(this.spectators.size()));
/* 900 */     return m;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\game\Match.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */