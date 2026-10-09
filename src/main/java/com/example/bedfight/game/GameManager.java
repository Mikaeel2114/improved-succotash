/*     */ package com.example.bedfight.game;
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.Arena;
/*     */ import com.example.bedfight.party.Party;
/*     */ import com.example.bedfight.util.Sounds;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public final class GameManager {
/*     */   private final BedFightPlugin plugin;
/*  22 */   private final Random random = new Random();
/*  23 */   private final Map<Mode, LinkedHashSet<UUID>> queues = new EnumMap<>(Mode.class);
/*  24 */   private final Map<UUID, Mode> queued = new HashMap<>();
/*  25 */   private final Map<UUID, Long> queuedAt = new HashMap<>();
/*  26 */   private final Set<Match> matches = new LinkedHashSet<>();
/*  27 */   private final Map<UUID, Match> byPlayer = new HashMap<>();
/*     */   
/*  29 */   private final Map<UUID, Match> spectating = new HashMap<>();
/*  30 */   private final Map<String, Match> byWorld = new HashMap<>();
/*     */   
/*     */   public GameManager(BedFightPlugin plugin) {
/*  33 */     this.plugin = plugin;
/*  34 */     for (Mode m : Mode.values()) {
/*  35 */       this.queues.put(m, new LinkedHashSet<>());
/*     */     }
/*     */   }
/*     */   
/*     */   public Match getMatch(Player p) {
/*  40 */     return this.byPlayer.get(p.getUniqueId());
/*     */   }
/*     */   public Match getSpectatedMatch(Player p) {
/*  43 */     return this.spectating.get(p.getUniqueId());
/*     */   }
/*     */   
/*     */   public Match getMatchAny(Player p) {
/*  47 */     Match m = this.byPlayer.get(p.getUniqueId());
/*  48 */     return (m != null) ? m : this.spectating.get(p.getUniqueId());
/*     */   }
/*     */   
/*  51 */   public Match getMatchByWorld(World w) { return this.byWorld.get(w.getName()); }
/*  52 */   public Mode getQueuedMode(Player p) { return this.queued.get(p.getUniqueId()); }
/*  53 */   public int getQueueSize(Mode m) { return ((LinkedHashSet)this.queues.get(m)).size(); } public Set<Match> allMatches() {
/*  54 */     return this.matches;
/*     */   }
/*     */   public long getQueuedSince(Player p) {
/*  57 */     Long t = this.queuedAt.get(p.getUniqueId());
/*  58 */     return (t == null) ? System.currentTimeMillis() : t.longValue();
/*     */   }
/*     */   
/*     */   public Set<UUID> activePlayers() {
/*  62 */     Set<UUID> s = new HashSet<>(this.byPlayer.keySet());
/*  63 */     s.addAll(this.spectating.keySet());
/*  64 */     s.addAll(this.queued.keySet());
/*  65 */     return s;
/*     */   }
/*     */   
/*     */   public boolean isBusy(Player p) {
/*  69 */     UUID id = p.getUniqueId();
/*  70 */     return (this.byPlayer.containsKey(id) || this.queued.containsKey(id) || this.spectating.containsKey(id));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void joinQueue(Player p, Mode mode) {
/*  76 */     List<Player> group = this.plugin.getParties().getPartyMembers(p);
/*  77 */     if (group.size() > 1 && !this.plugin.getParties().isLeader(p)) {
/*  78 */       this.plugin.msg((CommandSender)p, "Only your party leader can queue the party.");
/*     */       return;
/*     */     } 
/*  81 */     if (group.size() > mode.totalPlayers()) {
/*  82 */       this.plugin.msg((CommandSender)p, "Your party is too large for " + mode.label() + ".");
/*     */       return;
/*     */     } 
/*  85 */     for (Player member : group) {
/*  86 */       if (isBusy(member)) {
/*  87 */         this.plugin.msg((CommandSender)p, member.getName() + " is already in a queue, match or spectating.");
/*     */         return;
/*     */       } 
/*     */     } 
/*  91 */     if (this.plugin.getArenaManager().all().isEmpty()) {
/*  92 */       this.plugin.msg((CommandSender)p, "No arenas are available right now.");
/*     */       return;
/*     */     } 
/*  95 */     LinkedHashSet<UUID> q = this.queues.get(mode);
/*  96 */     for (Player member : group) {
/*  97 */       q.add(member.getUniqueId());
/*  98 */       this.queued.put(member.getUniqueId(), mode);
/*  99 */       this.queuedAt.put(member.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
/* 100 */       this.plugin.msg((CommandSender)member, "Joined the " + mode.label() + " queue (" + q.size() + "/" + mode.totalPlayers() + "). Use /bedfight leave to leave.");
/*     */       
/* 102 */       Sounds.play(this.plugin, member, "queue-join");
/*     */     } 
/* 104 */     matchmake(mode);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean leaveQueue(Player p) {
/* 109 */     return leaveQueue(p, true);
/*     */   }
/*     */   
/*     */   public boolean leaveQueue(Player p, boolean wholeParty) {
/* 113 */     Mode m = this.queued.remove(p.getUniqueId());
/* 114 */     this.queuedAt.remove(p.getUniqueId());
/* 115 */     if (m == null) {
/* 116 */       return false;
/*     */     }
/* 118 */     ((LinkedHashSet)this.queues.get(m)).remove(p.getUniqueId());
/* 119 */     Sounds.play(this.plugin, p, "queue-leave");
/* 120 */     if (wholeParty) {
/* 121 */       Party party = this.plugin.getParties().of(p);
/* 122 */       if (party != null) {
/* 123 */         for (UUID id : party.ids()) {
/* 124 */           if (this.queued.get(id) == m) {
/* 125 */             this.queued.remove(id);
/* 126 */             this.queuedAt.remove(id);
/* 127 */             ((LinkedHashSet)this.queues.get(m)).remove(id);
/* 128 */             Player member = Bukkit.getPlayer(id);
/* 129 */             if (member != null) {
/* 130 */               this.plugin.msg((CommandSender)member, p.getName() + " left the queue, so your party left it too.");
/* 131 */               Sounds.play(this.plugin, member, "queue-leave");
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 137 */     return true;
/*     */   }
/*     */   
/*     */   private void matchmake(Mode mode) {
/* 141 */     LinkedHashSet<UUID> q = this.queues.get(mode);
/* 142 */     int need = mode.totalPlayers();
/* 143 */     while (q.size() >= need) {
/* 144 */       List<Arena> arenas = new ArrayList<>(this.plugin.getArenaManager().all());
/* 145 */       if (arenas.isEmpty()) {
/*     */         return;
/*     */       }
/*     */       
/* 149 */       for (UUID id : new ArrayList(q)) {
/* 150 */         Player pl = Bukkit.getPlayer(id);
/* 151 */         if (pl == null || !pl.isOnline()) {
/* 152 */           q.remove(id);
/* 153 */           this.queued.remove(id);
/* 154 */           this.queuedAt.remove(id);
/*     */         } 
/*     */       } 
/*     */       
/* 158 */       List<UUID> picked = new ArrayList<>();
/* 159 */       Set<UUID> seen = new HashSet<>();
/* 160 */       for (UUID id : q) {
/* 161 */         if (picked.size() >= need) {
/*     */           break;
/*     */         }
/* 164 */         if (!seen.add(id)) {
/*     */           continue;
/*     */         }
/* 167 */         List<UUID> group = new ArrayList<>();
/* 168 */         group.add(id);
/* 169 */         Party party = this.plugin.getParties().of(id);
/* 170 */         if (party != null) {
/* 171 */           for (UUID mate : party.ids()) {
/* 172 */             if (!mate.equals(id) && q.contains(mate) && seen.add(mate)) {
/* 173 */               group.add(mate);
/*     */             }
/*     */           } 
/*     */         }
/* 177 */         if (picked.size() + group.size() <= need) {
/* 178 */           picked.addAll(group);
/*     */         }
/*     */       } 
/* 181 */       if (picked.size() != need) {
/*     */         return;
/*     */       }
/* 184 */       List<Player> players = new ArrayList<>();
/* 185 */       for (UUID id : picked) {
/* 186 */         q.remove(id);
/* 187 */         this.queued.remove(id);
/* 188 */         this.queuedAt.remove(id);
/* 189 */         players.add(Bukkit.getPlayer(id));
/*     */       } 
/* 191 */       startMatch(arenas.get(this.random.nextInt(arenas.size())), mode, players, MatchOptions.defaults(this.plugin.getSettings()));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String startPrivateMatch(Player host, Arena arena, Mode mode, MatchOptions options) {
/* 199 */     if (!this.plugin.getParties().isLeader(host)) {
/* 200 */       return "Only the party leader can start a private match.";
/*     */     }
/* 202 */     List<Player> members = this.plugin.getParties().getPartyMembers(host);
/* 203 */     if (members.size() != mode.totalPlayers()) {
/* 204 */       return "A " + mode.label() + " needs exactly " + mode.totalPlayers() + " players in your party (you have " + members
/* 205 */         .size() + ").";
/*     */     }
/* 207 */     for (Player m : members) {
/* 208 */       if (isBusy(m)) {
/* 209 */         return m.getName() + " is already in a queue, match or spectating.";
/*     */       }
/*     */     } 
/* 212 */     if (arena == null) {
/* 213 */       List<Arena> all = new ArrayList<>(this.plugin.getArenaManager().all());
/* 214 */       if (all.isEmpty()) {
/* 215 */         return "There are no arenas available.";
/*     */       }
/* 217 */       arena = all.get(this.random.nextInt(all.size()));
/*     */     } 
/* 219 */     startMatch(arena, mode, members, options);
/* 220 */     return null;
/*     */   }
/*     */   
/*     */   public Match startMatch(Arena arena, Mode mode, List<Player> players, MatchOptions options) {
/* 224 */     for (Player p : players) {
/* 225 */       leaveQueue(p, false);
/*     */     }
/* 227 */     List<List<Player>> teams = this.plugin.getParties().splitIntoTeams(players, mode.teamSize(), 2);
/* 228 */     Match match = new Match(this.plugin, this, arena, mode, options, teams);
/* 229 */     register(match);
/* 230 */     match.prepare();
/* 231 */     for (Player p : players) {
/* 232 */       this.plugin.getScoreboards().refresh(p);
/* 233 */       Sounds.play(this.plugin, p, "match-found");
/*     */     } 
/* 235 */     return match;
/*     */   }
/*     */   
/*     */   private void register(Match m) {
/* 239 */     this.matches.add(m);
/* 240 */     this.byWorld.put(m.getWorldName(), m);
/* 241 */     for (UUID id : m.getPlayerIds()) {
/* 242 */       this.byPlayer.put(id, m);
/*     */     }
/*     */   }
/*     */   
/*     */   void detach(Player p) {
/* 247 */     this.byPlayer.remove(p.getUniqueId());
/*     */   }
/*     */   
/*     */   void unregister(Match m) {
/* 251 */     this.matches.remove(m);
/* 252 */     this.byWorld.remove(m.getWorldName());
/* 253 */     for (UUID id : m.getPlayerIds()) {
/* 254 */       this.byPlayer.remove(id, m);
/*     */     }
/* 256 */     for (UUID id : new ArrayList(this.spectating.keySet())) {
/* 257 */       if (this.spectating.get(id) == m) {
/* 258 */         this.spectating.remove(id);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String spectate(Player spectator, Player target) {
/* 267 */     if (!this.plugin.getSettings().getBoolean("spectator.enabled", true)) {
/* 268 */       return "Spectating is disabled.";
/*     */     }
/* 270 */     if (spectator.equals(target)) {
/* 271 */       return "You can't spectate yourself.";
/*     */     }
/* 273 */     UUID id = spectator.getUniqueId();
/* 274 */     if (this.byPlayer.containsKey(id) || this.queued.containsKey(id)) {
/* 275 */       return "Leave your queue or match first.";
/*     */     }
/* 277 */     Match m = getMatchAny(target);
/* 278 */     if (m == null) {
/* 279 */       return target.getName() + " is not in a BedFight match.";
/*     */     }
/* 281 */     if (m.getState() != Match.State.COUNTDOWN && m.getState() != Match.State.RUNNING) {
/* 282 */       return "That match is not running.";
/*     */     }
/* 284 */     if (!(m.getOptions()).allowSpectators) {
/* 285 */       return "That match does not allow spectators.";
/*     */     }
/* 287 */     Match old = this.spectating.get(id);
/* 288 */     if (old != null && old != m) {
/* 289 */       old.removeSpectator(spectator, false);
/* 290 */       this.spectating.remove(id);
/*     */     } 
/* 292 */     this.spectating.put(id, m);
/* 293 */     m.addSpectator(spectator, target);
/* 294 */     this.plugin.getScoreboards().refresh(spectator);
/* 295 */     Sounds.play(this.plugin, spectator, "spectate-start");
/* 296 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stopSpectating(Player p) {
/* 301 */     Match m = this.spectating.remove(p.getUniqueId());
/* 302 */     if (m == null) {
/* 303 */       return false;
/*     */     }
/* 305 */     m.removeSpectator(p, true);
/* 306 */     this.plugin.getScoreboards().refresh(p);
/* 307 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean leaveAny(Player p) {
/* 312 */     if (leaveQueue(p)) {
/* 313 */       this.plugin.msg((CommandSender)p, "You left the queue.");
/* 314 */       return true;
/*     */     } 
/* 316 */     Match m = getMatch(p);
/* 317 */     if (m != null) {
/* 318 */       m.onLeave(p);
/* 319 */       this.plugin.msg((CommandSender)p, "You left the BedFight match.");
/* 320 */       return true;
/*     */     } 
/* 322 */     if (stopSpectating(p)) {
/* 323 */       this.plugin.msg((CommandSender)p, "You stopped spectating.");
/* 324 */       return true;
/*     */     } 
/* 326 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleQuit(Player p) {
/* 331 */     leaveQueue(p);
/* 332 */     Match m = this.byPlayer.get(p.getUniqueId());
/* 333 */     if (m != null) {
/* 334 */       m.onQuit(p);
/*     */     }
/* 336 */     Match s = this.spectating.remove(p.getUniqueId());
/* 337 */     if (s != null) {
/* 338 */       s.removeSpectator(p, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void shutdown() {
/* 343 */     for (Match m : new ArrayList(this.matches))
/* 344 */       m.forceEnd(); 
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\game\GameManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */