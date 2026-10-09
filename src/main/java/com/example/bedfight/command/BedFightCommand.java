/*     */ package com.example.bedfight.command;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.Arena;
/*     */ import com.example.bedfight.arena.ArenaManager;
/*     */ import com.example.bedfight.arena.Pos;
/*     */ import com.example.bedfight.arena.TeamSpec;
/*     */ import com.example.bedfight.arena.Teams;
/*     */ import com.example.bedfight.game.GameManager;
/*     */ import com.example.bedfight.game.Match;
/*     */ import com.example.bedfight.game.Mode;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.DyeColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.TabCompleter;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public final class BedFightCommand implements CommandExecutor, TabCompleter {
/*  32 */   private static final List<String> ADMIN_SUBS = List.of(new String[] { "setuparena", "edit", "info", "po1", "po2", "setspawn", "createteam", "removeteam", "setteam", "setbed", "buildmode", "save", "reload" });
/*     */   
/*     */   private static final String ADMIN = "bedfight.admin";
/*     */   
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   public BedFightCommand(BedFightPlugin plugin) {
/*  39 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/*     */     Player p;
/*  44 */     if (sender instanceof Player) { p = (Player)sender; }
/*  45 */     else { sender.sendMessage("Players only.");
/*  46 */       return true; }
/*     */     
/*  48 */     ArenaManager arenas = this.plugin.getArenaManager();
/*     */     
/*  50 */     if (cmd.getName().equalsIgnoreCase("leave")) {
/*  51 */       return leave(p);
/*     */     }
/*  53 */     if (cmd.getName().equalsIgnoreCase("adminkiteditor")) {
/*  54 */       return kitEditor(p, args);
/*     */     }
/*     */     
/*  57 */     if (args.length == 0) {
/*  58 */       return queue(p, Mode.parse(this.plugin.getConfig().getString("default-mode", "1v1")));
/*     */     }
/*  60 */     String sub = args[0].toLowerCase(Locale.ROOT);
/*  61 */     Mode asMode = Mode.parse(sub);
/*  62 */     if (asMode != null) {
/*  63 */       return queue(p, asMode);
/*     */     }
/*  65 */     if (sub.equals("leave")) {
/*  66 */       return leave(p);
/*     */     }
/*  68 */     if (sub.equals("spec") || sub.equals("spectate")) {
/*  69 */       return spectate(p, args);
/*     */     }
/*  71 */     if (sub.equals("kiteditor") || sub.equals("kit")) {
/*  72 */       return playerKit(p, args);
/*     */     }
/*  74 */     if (sub.equals("private")) {
/*  75 */       if (!p.hasPermission("bedfight.rank.mvpplusplus")) {
/*  76 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Only MVP++ players can create private matches.");
/*  77 */         return true;
/*     */       } 
/*  79 */       if (this.plugin.getGameManager().isBusy(p)) {
/*  80 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You are already in a queue or match.");
/*  81 */         return true;
/*     */       } 
/*  83 */       this.plugin.getPrivateGui().open(p);
/*  84 */       return true;
/*     */     } 
/*  86 */     if (!ADMIN_SUBS.contains(sub)) {
/*  87 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Unknown sub-command. Usage: /bedfight [1v1|2v2|3v3|private|spec <player>|kiteditor|leave]");
/*  88 */       return true;
/*     */     } 
/*  90 */     if (!p.hasPermission("bedfight.admin")) {
/*  91 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
/*  92 */       return true;
/*     */     } 
/*     */     
/*  95 */     if (sub.equals("setuparena")) {
/*  96 */       if (args.length < 2) {
/*  97 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight setuparena <world_name>");
/*  98 */         return true;
/*     */       } 
/* 100 */       String err = arenas.setupArena(p, args[1]);
/* 101 */       if (err != null) {
/* 102 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*     */       } else {
/* 104 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "World '" + String.valueOf(ChatColor.GREEN) + "' loaded. You are now setting up arena '" + args[1] + "'. Next: /bedfight po1, po2, setspawn, createteam, setteam, setbed, save.");
/*     */       } 
/*     */       
/* 107 */       return true;
/*     */     } 
/* 109 */     if (sub.equals("buildmode")) {
/* 110 */       if (arenas.toggleBuildMode(p)) {
/* 111 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Build mode enabled. You can edit protected arena worlds. Run again to disable it.");
/*     */       } else {
/* 113 */         this.plugin.msg((CommandSender)p, "Build mode disabled. The map is protected again.");
/*     */       } 
/* 115 */       return true;
/*     */     } 
/* 117 */     if (sub.equals("reload")) {
/* 118 */       this.plugin.reloadAll();
/* 119 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Reloaded config.yml, settings.yml, voices.yml, scoreboard.yml, kit.yml and arenas.");
/* 120 */       return true;
/*     */     } 
/*     */     
/* 123 */     if (sub.equals("edit")) {
/* 124 */       return edit(p, args);
/*     */     }
/*     */     
/* 127 */     Arena arena = arenas.session(p);
/* 128 */     if (arena == null) {
/* 129 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Start with /bedfight setuparena <world_name> or /bedfight edit <arena> first.");
/* 130 */       return true;
/*     */     } 
/* 132 */     runSetup(p, arena, sub, args);
/* 133 */     return true;
/*     */   }
/*     */   
/*     */   private void runSetup(Player p, Arena arena, String sub, String[] args) {
/*     */     String err;
/* 138 */     ArenaManager arenas = this.plugin.getArenaManager();
/* 139 */     switch (sub) {
/*     */       case "info":
/* 141 */         info(p, arena);
/*     */         break;
/*     */       case "removeteam":
/* 144 */         removeTeam(p, arena, args);
/*     */         break;
/*     */       case "po1":
/*     */       case "po2":
/* 148 */         setBound(p, arena, sub.equals("po1"), args);
/*     */         break;
/*     */       case "setspawn":
/* 151 */         setSpawn(p, arena, args);
/*     */         break;
/*     */       case "createteam":
/* 154 */         createTeam(p, arena, args);
/*     */         break;
/*     */       case "setteam":
/* 157 */         setTeamSpawn(p, arena, args);
/*     */         break;
/*     */       case "setbed":
/* 160 */         setBed(p, arena, args);
/*     */         break;
/*     */       case "save":
/* 163 */         err = arenas.save(p);
/* 164 */         this.plugin.msg((CommandSender)p, (err == null) ? (
/* 165 */             String.valueOf(ChatColor.GREEN) + "Arena '" + String.valueOf(ChatColor.GREEN) + "' saved to arenas/" + arena.getName() + ".yml") : (
/* 166 */             String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED)));
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean leave(Player p) {
/* 176 */     if (!this.plugin.getGameManager().leaveAny(p)) {
/* 177 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You are not in a BedFight queue, match or spectating.");
/*     */     }
/* 179 */     return true;
/*     */   }
/*     */   
/*     */   private boolean queue(Player p, Mode mode) {
/* 183 */     if (!p.hasPermission("bedfight.play")) {
/* 184 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You don't have permission to play BedFight.");
/* 185 */       return true;
/*     */     } 
/* 187 */     this.plugin.getGameManager().joinQueue(p, (mode == null) ? Mode.SOLO : mode);
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean spectate(Player p, String[] args) {
/* 193 */     if (!p.hasPermission("bedfight.spectate")) {
/* 194 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You don't have permission to spectate.");
/* 195 */       return true;
/*     */     } 
/* 197 */     if (args.length < 2) {
/* 198 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight spec <player>");
/* 199 */       return true;
/*     */     } 
/* 201 */     Player target = Bukkit.getPlayerExact(args[1]);
/* 202 */     if (target == null) {
/* 203 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Player '" + String.valueOf(ChatColor.RED) + "' is not online.");
/* 204 */       return true;
/*     */     } 
/* 206 */     String err = this.plugin.getGameManager().spectate(p, target);
/* 207 */     if (err != null) {
/* 208 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*     */     }
/* 210 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean playerKit(Player p, String[] args) {
/* 215 */     if (this.plugin.getGameManager().isBusy(p)) {
/* 216 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You can't edit your kit while you are in a queue, a match or spectating.");
/* 217 */       return true;
/*     */     } 
/* 219 */     if (args.length > 1 && args[1].equalsIgnoreCase("reset")) {
/* 220 */       this.plugin.getKitManager().resetPersonal(p.getUniqueId());
/* 221 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Your kit layout was reset to the default.");
/* 222 */       return true;
/*     */     } 
/* 224 */     this.plugin.getKitEditor().openPlayer(p);
/* 225 */     return true;
/*     */   }
/*     */   
/*     */   private boolean kitEditor(Player p, String[] args) {
/* 229 */     if (!p.hasPermission("bedfight.admin")) {
/* 230 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
/* 231 */       return true;
/*     */     } 
/* 233 */     if (args.length > 0 && args[0].equalsIgnoreCase("reset")) {
/* 234 */       this.plugin.getKitManager().resetToDefault();
/* 235 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Kit reset to the default (iron sword, 64 wool, shears, golden apple, leather armor).");
/* 236 */       return true;
/*     */     } 
/* 238 */     this.plugin.getKitEditor().open(p);
/* 239 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setBound(Player p, Arena arena, boolean first, String[] args) {
/*     */     Pos pos;
/* 251 */     if (args.length >= 4) {
/*     */       try {
/* 253 */         pos = new Pos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 0.0F, 0.0F);
/* 254 */       } catch (NumberFormatException ex) {
/* 255 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight " + String.valueOf(ChatColor.RED) + " [x y z] (whole numbers)");
/*     */         return;
/*     */       } 
/* 258 */     } else if (args.length == 1) {
/* 259 */       pos = Pos.ofBlock(p.getLocation().getBlock());
/*     */     } else {
/* 261 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight " + String.valueOf(ChatColor.RED) + " [x y z]");
/*     */       return;
/*     */     } 
/* 264 */     if (first) {
/* 265 */       arena.setPos1(pos);
/*     */     } else {
/* 267 */       arena.setPos2(pos);
/*     */     } 
/*     */     
/* 270 */     String msg = String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.GREEN) + " set to block " + (first ? "po1" : "po2") + ", " + pos.blockX() + String.valueOf(ChatColor.WHITE) + pos.blockY() + ", " + String.valueOf(ChatColor.GREEN) + ".";
/* 271 */     if (args.length == 1) {
/* 272 */       msg = msg + " (feet block; the block you stand on is Y " + msg + ")";
/*     */     }
/* 274 */     if (arena.hasBounds())
/*     */     {
/* 276 */       msg = msg + " Void level (min Y) = " + msg + String.valueOf(ChatColor.WHITE) + arena.getMinY() + ", max build height (max Y) = " + String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.WHITE) + arena.getMaxY() + ".";
/*     */     }
/* 278 */     this.plugin.msg((CommandSender)p, msg);
/* 279 */     if (arena.hasBounds() && arena.getMaxY() - arena.getMinY() < 8) {
/* 280 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.YELLOW) + "Warning: only " + String.valueOf(ChatColor.YELLOW) + " blocks between void level and build limit. Set one point at the lowest void level and one high above the islands.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setSpawn(Player p, Arena arena, String[] args) {
/* 290 */     Location loc = p.getLocation();
/* 291 */     Pos pos = Pos.of(loc).level();
/* 292 */     if (args.length >= 2) {
/* 293 */       DyeColor c = resolveTeam(p, arena, args);
/* 294 */       if (c == null) {
/*     */         return;
/*     */       }
/* 297 */       arena.team(c, true).setWaitSpawn(pos);
/* 298 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Waiting spawn of team " + String.valueOf(ChatColor.GREEN) + Teams.display(c) + " set. Its dead players wait here until they respawn (the team's fighting spawn is still /bedfight setteam).");
/*     */       
/*     */       return;
/*     */     } 
/* 302 */     arena.setSpawn(pos);
/* 303 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Game spawn set (used by every team without its own /bedfight setspawn <team>).");
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean edit(Player p, String[] args) {
/* 308 */     ArenaManager arenas = this.plugin.getArenaManager();
/* 309 */     if (args.length < 2) {
/* 310 */       Arena cur = arenas.session(p);
/* 311 */       if (cur != null) {
/* 312 */         info(p, cur);
/*     */       }
/* 314 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.YELLOW) + "Usage: /bedfight edit <arena> [po1|po2|setspawn|createteam|removeteam|setteam|setbed|info ...]" + String.valueOf(ChatColor.YELLOW) + " Saved arenas: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE));
/*     */ 
/*     */       
/* 317 */       return true;
/*     */     } 
/* 319 */     String err = arenas.editArena(p, args[1]);
/* 320 */     if (err != null) {
/* 321 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/* 322 */       return true;
/*     */     } 
/* 324 */     Arena arena = arenas.session(p);
/* 325 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Editing arena '" + String.valueOf(ChatColor.GREEN) + "'. Build mode is ON (turn it off with /bedfight buildmode). Change anything with po1, po2, setspawn [team], createteam, removeteam, setteam, setbed and finish with /bedfight save.");
/*     */     
/* 327 */     if (args.length >= 3) {
/* 328 */       String sub2 = args[2].toLowerCase(Locale.ROOT);
/* 329 */       if (ADMIN_SUBS.contains(sub2) && !sub2.equals("edit") && !sub2.equals("setuparena") && 
/* 330 */         !sub2.equals("reload") && !sub2.equals("buildmode")) {
/*     */         
/* 332 */         String[] rest = new String[args.length - 2];
/* 333 */         System.arraycopy(args, 2, rest, 0, rest.length);
/* 334 */         rest[0] = sub2;
/* 335 */         runSetup(p, arena, sub2, rest);
/* 336 */         return true;
/*     */       } 
/*     */     } 
/* 339 */     info(p, arena);
/* 340 */     return true;
/*     */   }
/*     */   
/*     */   private void info(Player p, Arena arena) {
/* 344 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GOLD) + "Arena " + String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.WHITE) + arena.getName() + " (world " + String.valueOf(ChatColor.GRAY) + ")");
/* 345 */     p.sendMessage(String.valueOf(ChatColor.GRAY) + " po1: " + String.valueOf(ChatColor.GRAY) + posText(arena.getPos1()) + "   po2: " + String.valueOf(ChatColor.GRAY));
/* 346 */     if (arena.hasBounds()) {
/* 347 */       p.sendMessage(String.valueOf(ChatColor.GRAY) + " Void level (min Y): " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + arena.getMinY() + "   max build height (max Y): " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE));
/*     */     }
/*     */     
/* 350 */     p.sendMessage(String.valueOf(ChatColor.GRAY) + " Game spawn: " + String.valueOf(ChatColor.GRAY));
/* 351 */     for (TeamSpec t : arena.getTeams().values()) {
/* 352 */       p.sendMessage(String.valueOf(ChatColor.GRAY) + " Team " + String.valueOf(ChatColor.GRAY) + Teams.display(t.getColor()) + ": spawn " + String.valueOf(ChatColor.GRAY) + posText(t.getSpawn()) + ", bed " + String.valueOf(ChatColor.GRAY) + 
/* 353 */           posText(t.getBed()) + ", waiting spawn " + String.valueOf(ChatColor.GRAY));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static String posText(Pos p) {
/* 359 */     if (p == null) {
/* 360 */       return String.valueOf(ChatColor.RED) + "not set";
/*     */     }
/* 362 */     return ChatColor.WHITE.toString() + ChatColor.WHITE.toString() + ", " + p.blockX() + ", " + p.blockY();
/*     */   }
/*     */   
/*     */   private void removeTeam(Player p, Arena arena, String[] args) {
/* 366 */     if (args.length < 2) {
/* 367 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight removeteam <color>");
/*     */       return;
/*     */     } 
/* 370 */     DyeColor c = Teams.parse(args[1]);
/* 371 */     if (c == null || !arena.removeTeam(c)) {
/* 372 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "That team doesn't exist in this arena.");
/*     */       return;
/*     */     } 
/* 375 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Team " + String.valueOf(ChatColor.GREEN) + Teams.display(c) + " removed. Save with /bedfight save.");
/*     */   }
/*     */   
/*     */   private void createTeam(Player p, Arena arena, String[] args) {
/* 379 */     if (args.length < 2) {
/* 380 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Usage: /bedfight createteam <color>");
/*     */       return;
/*     */     } 
/* 383 */     DyeColor c = Teams.parse(args[1]);
/* 384 */     if (c == null) {
/* 385 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Unknown color. Valid: " + String.valueOf(ChatColor.RED));
/*     */       return;
/*     */     } 
/* 388 */     boolean existed = arena.getTeams().containsKey(c);
/* 389 */     arena.team(c, true);
/* 390 */     this.plugin.getArenaManager().select(p, c);
/* 391 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Team " + String.valueOf(ChatColor.GREEN) + Teams.display(c) + String.valueOf(ChatColor.GREEN) + " Now use /bedfight setteam and /bedfight setbed.");
/*     */   }
/*     */ 
/*     */   
/*     */   private void setTeamSpawn(Player p, Arena arena, String[] args) {
/* 396 */     DyeColor c = resolveTeam(p, arena, args);
/* 397 */     if (c == null) {
/*     */       return;
/*     */     }
/* 400 */     Location loc = p.getLocation();
/* 401 */     Block under = loc.getBlock().getRelative(0, -1, 0);
/* 402 */     if (under.getType() == Material.AIR) {
/* 403 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Stand on a solid block - the block under your feet is the spawn.");
/*     */       return;
/*     */     } 
/* 406 */     arena.team(c, true).setSpawn(new Pos(under.getX() + 0.5D, (under.getY() + 1), under.getZ() + 0.5D, loc.getYaw(), 0.0F));
/* 407 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Spawn of team " + String.valueOf(ChatColor.GREEN) + Teams.display(c) + " set to block " + String.valueOf(ChatColor.GREEN) + ", " + under
/* 408 */         .getX() + ", " + under.getY() + ".");
/*     */   }
/*     */   
/*     */   private void setBed(Player p, Arena arena, String[] args) {
/* 412 */     DyeColor c = resolveTeam(p, arena, args);
/* 413 */     if (c == null) {
/*     */       return;
/*     */     }
/* 416 */     Block bed = p.getTargetBlock((HashSet)null, 6);
/* 417 */     if (bed == null || bed.getType() != Material.BED_BLOCK) {
/* 418 */       Block feet = p.getLocation().getBlock();
/* 419 */       bed = (feet.getType() == Material.BED_BLOCK) ? feet : null;
/*     */     } 
/* 421 */     if (bed == null) {
/* 422 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Look at the team's bed (within 6 blocks) and run the command again.");
/*     */       return;
/*     */     } 
/* 425 */     arena.team(c, true).setBed(Pos.ofBlock(bed));
/* 426 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Bed of team " + String.valueOf(ChatColor.GREEN) + Teams.display(c) + " registered at " + String.valueOf(ChatColor.GREEN) + ", " + bed
/* 427 */         .getX() + ", " + bed.getY() + ".");
/*     */   }
/*     */   
/*     */   private DyeColor resolveTeam(Player p, Arena arena, String[] args) {
/* 431 */     if (args.length >= 2) {
/* 432 */       DyeColor dyeColor = Teams.parse(args[1]);
/* 433 */       if (dyeColor == null || !arena.getTeams().containsKey(dyeColor)) {
/* 434 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "That team doesn't exist. Use /bedfight createteam <color> first.");
/* 435 */         return null;
/*     */       } 
/* 437 */       this.plugin.getArenaManager().select(p, dyeColor);
/* 438 */       return dyeColor;
/*     */     } 
/* 440 */     DyeColor c = this.plugin.getArenaManager().selected(p);
/* 441 */     if (c == null || !arena.getTeams().containsKey(c)) {
/* 442 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "No team selected. Use /bedfight createteam <color> or pass the color.");
/* 443 */       return null;
/*     */     } 
/* 445 */     return c;
/*     */   }
/*     */   
/*     */   private static String colorList() {
/* 449 */     List<String> l = new ArrayList<>();
/* 450 */     for (DyeColor c : DyeColor.values()) {
/* 451 */       l.add(c.name().toLowerCase(Locale.ROOT));
/*     */     }
/* 453 */     return String.join(", ", (Iterable)l);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
/*     */     Player p;
/* 460 */     List<String> out = new ArrayList<>();
/* 461 */     if (sender instanceof Player) { p = (Player)sender; }
/* 462 */     else { return out; }
/*     */     
/* 464 */     String cn = cmd.getName().toLowerCase(Locale.ROOT);
/* 465 */     if (cn.equals("leave")) {
/* 466 */       return out;
/*     */     }
/* 468 */     if (cn.equals("adminkiteditor")) {
/* 469 */       if (args.length == 1) {
/* 470 */         out.add("reset");
/*     */       }
/* 472 */       return filter(out, args);
/*     */     } 
/* 474 */     if (args.length == 1) {
/* 475 */       out.addAll(List.of("1v1", "2v2", "3v3", "spec", "leave", "kiteditor"));
/* 476 */       if (p.hasPermission("bedfight.rank.mvpplusplus")) {
/* 477 */         out.add("private");
/*     */       }
/* 479 */       if (p.hasPermission("bedfight.admin")) {
/* 480 */         out.addAll(ADMIN_SUBS);
/*     */       }
/* 482 */     } else if (args.length == 2 && (args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectate"))) {
/* 483 */       GameManager games = this.plugin.getGameManager();
/* 484 */       for (Player online : Bukkit.getOnlinePlayers()) {
/* 485 */         Match m = games.getMatchAny(online);
/* 486 */         if (m != null && !online.equals(p)) {
/* 487 */           out.add(online.getName());
/*     */         }
/*     */       } 
/* 490 */     } else if (args.length == 2 && args[0].equalsIgnoreCase("kiteditor")) {
/* 491 */       out.add("reset");
/* 492 */     } else if (args.length == 3 && p.hasPermission("bedfight.admin") && args[0].equalsIgnoreCase("edit")) {
/* 493 */       out.addAll(List.of("po1", "po2", "setspawn", "createteam", "removeteam", "setteam", "setbed", "info"));
/* 494 */     } else if (args.length == 2 && p.hasPermission("bedfight.admin")) {
/* 495 */       String sub = args[0].toLowerCase(Locale.ROOT);
/* 496 */       if (sub.equals("edit")) {
/* 497 */         out.addAll(this.plugin.getArenaManager().savedNames());
/* 498 */       } else if (sub.equals("setspawn") || sub.equals("removeteam")) {
/* 499 */         Arena a = this.plugin.getArenaManager().session(p);
/* 500 */         if (a != null) {
/* 501 */           for (DyeColor c : a.getTeams().keySet()) {
/* 502 */             out.add(c.name().toLowerCase(Locale.ROOT));
/*     */           }
/*     */         }
/* 505 */       } else if (sub.equals("createteam")) {
/* 506 */         for (DyeColor c : DyeColor.values()) {
/* 507 */           out.add(c.name().toLowerCase(Locale.ROOT));
/*     */         }
/* 509 */       } else if (sub.equals("setteam") || sub.equals("setbed")) {
/* 510 */         Arena a = this.plugin.getArenaManager().session(p);
/* 511 */         if (a != null) {
/* 512 */           for (DyeColor c : a.getTeams().keySet()) {
/* 513 */             out.add(c.name().toLowerCase(Locale.ROOT));
/*     */           }
/*     */         }
/* 516 */       } else if (sub.equals("setuparena")) {
/* 517 */         Set<String> names = new HashSet<>();
/* 518 */         for (World w : Bukkit.getWorlds()) {
/* 519 */           names.add(w.getName());
/*     */         }
/* 521 */         File[] dirs = Bukkit.getWorldContainer().listFiles(f -> (f.isDirectory() && (new File(f, "level.dat")).isFile()));
/* 522 */         if (dirs != null) {
/* 523 */           for (File f : dirs) {
/* 524 */             names.add(f.getName());
/*     */           }
/*     */         }
/* 527 */         out.addAll(names);
/*     */       } 
/*     */     } 
/* 530 */     return filter(out, args);
/*     */   }
/*     */   
/*     */   private static List<String> filter(List<String> out, String[] args) {
/* 534 */     String prefix = (args.length == 0) ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
/* 535 */     out.removeIf(s -> !s.toLowerCase(Locale.ROOT).startsWith(prefix));
/* 536 */     return out;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\command\BedFightCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */