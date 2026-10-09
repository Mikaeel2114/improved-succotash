/*     */ package com.example.bedfight.party;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.util.Sounds;
/*     */ import com.example.bedfight.util.Text;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import net.md_5.bungee.api.ChatColor;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import net.md_5.bungee.api.chat.ClickEvent;
/*     */ import net.md_5.bungee.api.chat.HoverEvent;
/*     */ import net.md_5.bungee.api.chat.TextComponent;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public final class PartyManager
/*     */ {
/*  27 */   public static final String PREFIX = String.valueOf(ChatColor.BLUE) + "Party " + String.valueOf(ChatColor.BLUE) + "» " + String.valueOf(ChatColor.DARK_GRAY);
/*     */   
/*     */   private final BedFightPlugin plugin;
/*  30 */   private final Map<UUID, Party> byPlayer = new HashMap<>();
/*     */   
/*  32 */   private final Set<UUID> chatOn = new HashSet<>();
/*     */   
/*     */   public PartyManager(BedFightPlugin plugin) {
/*  35 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public Party of(Player p) {
/*  40 */     return this.byPlayer.get(p.getUniqueId()); } public Party of(UUID id) {
/*  41 */     return this.byPlayer.get(id);
/*     */   }
/*     */   public boolean isLeader(Player p) {
/*  44 */     Party party = of(p);
/*  45 */     return (party == null || party.getLeader().equals(p.getUniqueId()));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Player> getPartyMembers(Player player) {
/*  50 */     List<Player> out = new ArrayList<>();
/*  51 */     out.add(player);
/*  52 */     Party party = of(player);
/*  53 */     if (party != null) {
/*  54 */       for (UUID id : party.ids()) {
/*  55 */         Player m = Bukkit.getPlayer(id);
/*  56 */         if (m != null && m.isOnline() && !m.equals(player)) {
/*  57 */           out.add(m);
/*     */         }
/*     */       } 
/*     */     }
/*  61 */     return out;
/*     */   }
/*     */   
/*     */   public int defaultLimit() {
/*  65 */     return Math.max(2, Math.min(maxLimit(), this.plugin.getSettings().getInt("party.default-limit", 8)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxLimit() {
/*  70 */     return Math.max(2, Math.min(18, this.plugin.getSettings().getInt("party.max-limit", 16)));
/*     */   }
/*     */   
/*     */   private long inviteMillis() {
/*  74 */     return Math.max(10, this.plugin.getSettings().getInt("party.invite-expire-seconds", 60)) * 1000L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tell(Player p, String msg) {
/*  80 */     p.sendMessage(PREFIX + PREFIX);
/*     */   }
/*     */   
/*     */   public void broadcast(Party party, String msg) {
/*  84 */     for (UUID id : party.ids()) {
/*  85 */       Player m = Bukkit.getPlayer(id);
/*  86 */       if (m != null) {
/*  87 */         tell(m, msg);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String name(UUID id) {
/*  93 */     Player p = Bukkit.getPlayer(id);
/*  94 */     if (p != null) {
/*  95 */       return p.getName();
/*     */     }
/*  97 */     String n = Bukkit.getOfflinePlayer(id).getName();
/*  98 */     return (n == null) ? "?" : n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String invite(Player actor, Player target) {
/* 105 */     if (target == null) {
/* 106 */       return "That player is not online.";
/*     */     }
/* 108 */     if (target.equals(actor)) {
/* 109 */       return "You can't invite yourself.";
/*     */     }
/* 111 */     Party party = of(actor);
/* 112 */     if (party != null && !party.isStaff(actor.getUniqueId()) && !party.isAllInvite()) {
/* 113 */       return "Only the party leader and mods can invite players.";
/*     */     }
/* 115 */     if (of(target) != null) {
/* 116 */       return target.getName() + target.getName();
/*     */     }
/* 118 */     if (party != null) {
/* 119 */       purgeInvites(party);
/* 120 */       if (party.size() >= party.getLimit()) {
/* 121 */         return "Your party is full (limit " + party.getLimit() + ").";
/*     */       }
/* 123 */       if (party.getInvites().containsKey(target.getUniqueId())) {
/* 124 */         return target.getName() + " already has an invite.";
/*     */       }
/*     */     } 
/* 127 */     if (party == null) {
/*     */ 
/*     */       
/* 130 */       party = new Party(actor.getUniqueId(), defaultLimit(), this.plugin.getSettings().getBoolean("party.default-open", false), this.plugin.getSettings().getBoolean("party.default-all-invite", false));
/* 131 */       this.byPlayer.put(actor.getUniqueId(), party);
/* 132 */       tell(actor, String.valueOf(ChatColor.GREEN) + "You created a party.");
/*     */     } 
/* 134 */     Party fp = party;
/* 135 */     UUID tid = target.getUniqueId();
/* 136 */     long expireMs = inviteMillis();
/* 137 */     party.getInvites().put(tid, Long.valueOf(System.currentTimeMillis() + expireMs));
/*     */     
/* 139 */     broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + actor.getName() + " invited " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.YELLOW) + target.getName() + " to the party. They have " + String.valueOf(ChatColor.GRAY) + " seconds to accept.");
/*     */     
/* 141 */     sendInvite(target, actor, party);
/* 142 */     Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> { Long until = fp.getInvites().get(tid); if (until != null && until.longValue() <= System.currentTimeMillis()) { fp.getInvites().remove(tid); if (!fp.has(tid) && this.byPlayer.get(fp.getLeader()) == fp) broadcast(fp, String.valueOf(ChatColor.GRAY) + "The invite for " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.YELLOW) + name(tid) + " expired.");  }  }expireMs / 50L + 20L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     return null;
/*     */   }
/*     */   
/*     */   private void sendInvite(Player target, Player from, Party party) {
/* 155 */     String text = PREFIX + PREFIX + String.valueOf(ChatColor.YELLOW) + from.getName() + " invited you to their party! ";
/* 156 */     TextComponent line = new TextComponent("");
/* 157 */     for (BaseComponent b : TextComponent.fromLegacyText(text)) {
/* 158 */       line.addExtra(b);
/*     */     }
/* 160 */     TextComponent btn = new TextComponent("[CLICK TO JOIN]");
/* 161 */     btn.setColor(ChatColor.GREEN);
/* 162 */     btn.setBold(Boolean.valueOf(true));
/* 163 */     btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + from.getName()));
/* 164 */     btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { (BaseComponent)new TextComponent("Click to join " + from
/* 165 */               .getName() + "'s party") }));
/* 166 */     line.addExtra((BaseComponent)btn);
/* 167 */     target.spigot().sendMessage((BaseComponent)line);
/* 168 */     target.sendMessage(PREFIX + PREFIX + "Or type " + String.valueOf(ChatColor.GRAY) + "/party join " + String.valueOf(ChatColor.WHITE));
/* 169 */     Sounds.play(this.plugin, target, "queue-join");
/*     */   }
/*     */ 
/*     */   
/*     */   public String join(Player p, Player other) {
/* 174 */     if (other == null) {
/* 175 */       return "That player is not online.";
/*     */     }
/* 177 */     if (of(p) != null) {
/* 178 */       return "You are already in a party. Leave it first with /party leave.";
/*     */     }
/* 180 */     Party party = of(other);
/* 181 */     if (party == null) {
/* 182 */       return other.getName() + " is not in a party.";
/*     */     }
/* 184 */     purgeInvites(party);
/* 185 */     boolean invited = party.getInvites().containsKey(p.getUniqueId());
/* 186 */     if (!invited && !party.isOpen()) {
/* 187 */       return "You don't have an invite to that party, and it isn't open.";
/*     */     }
/* 189 */     if (party.size() >= party.getLimit()) {
/* 190 */       return "That party is full (limit " + party.getLimit() + ").";
/*     */     }
/* 192 */     party.getInvites().remove(p.getUniqueId());
/* 193 */     party.getMembers().put(p.getUniqueId(), Party.Role.MEMBER);
/* 194 */     this.byPlayer.put(p.getUniqueId(), party);
/* 195 */     broadcast(party, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.GREEN) + p.getName() + " joined the party. " + String.valueOf(ChatColor.GRAY) + "(" + String.valueOf(ChatColor.DARK_GRAY) + "/" + party
/* 196 */         .size() + ")");
/* 197 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String deny(Player p, Player from) {
/* 202 */     if (from == null) {
/* 203 */       return "That player is not online.";
/*     */     }
/* 205 */     Party party = of(from);
/* 206 */     if (party == null || party.getInvites().remove(p.getUniqueId()) == null) {
/* 207 */       return "You have no invite from " + from.getName() + ".";
/*     */     }
/* 209 */     tell(p, String.valueOf(ChatColor.GRAY) + "Invite declined.");
/* 210 */     broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + p.getName() + " declined the invite.");
/* 211 */     return null;
/*     */   }
/*     */   
/*     */   private static void purgeInvites(Party party) {
/* 215 */     long now = System.currentTimeMillis();
/* 216 */     Iterator<Map.Entry<UUID, Long>> it = party.getInvites().entrySet().iterator();
/* 217 */     while (it.hasNext()) {
/* 218 */       if (((Long)((Map.Entry)it.next()).getValue()).longValue() <= now) {
/* 219 */         it.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String leave(Player p) {
/* 227 */     Party party = of(p);
/* 228 */     if (party == null) {
/* 229 */       return "You are not in a party.";
/*     */     }
/* 231 */     remove(party, p.getUniqueId(), String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + p.getName() + " left the party.");
/* 232 */     tell(p, String.valueOf(ChatColor.GRAY) + "You left the party.");
/* 233 */     return null;
/*     */   }
/*     */   
/*     */   public String kick(Player actor, Player target, String targetName) {
/* 237 */     Party party = of(actor);
/* 238 */     if (party == null) {
/* 239 */       return "You are not in a party.";
/*     */     }
/* 241 */     if (!party.isStaff(actor.getUniqueId())) {
/* 242 */       return "Only the party leader and mods can kick players.";
/*     */     }
/* 244 */     UUID tid = (target != null) ? target.getUniqueId() : idByName(party, targetName);
/* 245 */     if (tid == null || !party.has(tid)) {
/* 246 */       return ((targetName == null) ? "That player" : targetName) + " is not in your party.";
/*     */     }
/* 248 */     if (tid.equals(actor.getUniqueId())) {
/* 249 */       return "You can't kick yourself. Use /party leave.";
/*     */     }
/* 251 */     Party.Role tr = party.roleOf(tid);
/* 252 */     if (tr == Party.Role.LEADER || (tr == Party.Role.MOD && party.roleOf(actor.getUniqueId()) != Party.Role.LEADER)) {
/* 253 */       return "You can't kick " + name(tid) + ".";
/*     */     }
/* 255 */     Player kicked = Bukkit.getPlayer(tid);
/* 256 */     remove(party, tid, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + name(tid) + " was kicked from the party by " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.YELLOW) + actor
/* 257 */         .getName() + ".");
/* 258 */     if (kicked != null) {
/* 259 */       tell(kicked, String.valueOf(ChatColor.RED) + "You were kicked from the party.");
/*     */     }
/* 261 */     return null;
/*     */   }
/*     */   
/*     */   private UUID idByName(Party party, String n) {
/* 265 */     if (n == null) {
/* 266 */       return null;
/*     */     }
/* 268 */     for (UUID id : party.ids()) {
/* 269 */       if (name(id).equalsIgnoreCase(n)) {
/* 270 */         return id;
/*     */       }
/*     */     } 
/* 273 */     return null;
/*     */   }
/*     */   
/*     */   public String disband(Player actor) {
/* 277 */     Party party = of(actor);
/* 278 */     if (party == null) {
/* 279 */       return "You are not in a party.";
/*     */     }
/* 281 */     if (!party.getLeader().equals(actor.getUniqueId())) {
/* 282 */       return "Only the party leader can disband the party.";
/*     */     }
/* 284 */     dissolve(party, String.valueOf(ChatColor.RED) + "The party was disbanded by " + String.valueOf(ChatColor.RED) + ".");
/* 285 */     return null;
/*     */   }
/*     */   
/*     */   private void dissolve(Party party, String message) {
/* 289 */     broadcast(party, message);
/* 290 */     for (UUID id : party.ids()) {
/* 291 */       this.byPlayer.remove(id);
/* 292 */       this.chatOn.remove(id);
/* 293 */       Player m = Bukkit.getPlayer(id);
/* 294 */       if (m != null) {
/* 295 */         this.plugin.getGameManager().leaveQueue(m, false);
/*     */       }
/*     */     } 
/* 298 */     party.getMembers().clear();
/* 299 */     party.getInvites().clear();
/*     */   }
/*     */ 
/*     */   
/*     */   private void remove(Party party, UUID id, String message) {
/* 304 */     boolean wasLeader = party.getLeader().equals(id);
/* 305 */     party.getMembers().remove(id);
/* 306 */     this.byPlayer.remove(id);
/* 307 */     this.chatOn.remove(id);
/* 308 */     Player p = Bukkit.getPlayer(id);
/* 309 */     if (p != null) {
/* 310 */       this.plugin.getGameManager().leaveQueue(p, false);
/*     */     }
/* 312 */     if (party.size() <= 1) {
/* 313 */       if (message != null) {
/* 314 */         broadcast(party, message);
/*     */       }
/* 316 */       dissolve(party, String.valueOf(ChatColor.RED) + "The party was disbanded because it is empty.");
/*     */       return;
/*     */     } 
/* 319 */     if (message != null) {
/* 320 */       broadcast(party, message);
/*     */     }
/* 322 */     if (wasLeader) {
/* 323 */       UUID next = null;
/* 324 */       for (UUID m : party.ids()) {
/* 325 */         if (party.roleOf(m) == Party.Role.MOD) {
/* 326 */           next = m;
/*     */           break;
/*     */         } 
/*     */       } 
/* 330 */       if (next == null) {
/* 331 */         next = party.ids().get(0);
/*     */       }
/* 333 */       party.getMembers().put(next, Party.Role.LEADER);
/* 334 */       broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + name(next) + " is the new party leader.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleQuit(Player p) {
/* 340 */     this.chatOn.remove(p.getUniqueId());
/* 341 */     Party party = of(p);
/* 342 */     if (party != null) {
/* 343 */       remove(party, p.getUniqueId(), String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + p.getName() + " disconnected and left the party.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String promote(Player actor, Player target, String targetName) {
/* 350 */     Party party = of(actor);
/* 351 */     String err = leaderCheck(party, actor);
/* 352 */     if (err != null) {
/* 353 */       return err;
/*     */     }
/* 355 */     UUID tid = (target != null) ? target.getUniqueId() : idByName(party, targetName);
/* 356 */     if (tid == null || !party.has(tid)) {
/* 357 */       return ((targetName == null) ? "That player" : targetName) + " is not in your party.";
/*     */     }
/* 359 */     Party.Role r = party.roleOf(tid);
/* 360 */     if (r == Party.Role.LEADER) {
/* 361 */       return "That player is already the leader.";
/*     */     }
/* 363 */     if (r == Party.Role.MOD) {
/* 364 */       return name(tid) + " is already a mod. Use /party transfer " + name(tid) + " to make them the leader.";
/*     */     }
/* 366 */     party.getMembers().put(tid, Party.Role.MOD);
/* 367 */     broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + name(tid) + " was promoted to " + String.valueOf(ChatColor.GRAY) + "party mod" + String.valueOf(ChatColor.GREEN) + ".");
/* 368 */     return null;
/*     */   }
/*     */   
/*     */   public String demote(Player actor, Player target, String targetName) {
/* 372 */     Party party = of(actor);
/* 373 */     String err = leaderCheck(party, actor);
/* 374 */     if (err != null) {
/* 375 */       return err;
/*     */     }
/* 377 */     UUID tid = (target != null) ? target.getUniqueId() : idByName(party, targetName);
/* 378 */     if (tid == null || !party.has(tid)) {
/* 379 */       return ((targetName == null) ? "That player" : targetName) + " is not in your party.";
/*     */     }
/* 381 */     if (party.roleOf(tid) != Party.Role.MOD) {
/* 382 */       return name(tid) + " is not a mod.";
/*     */     }
/* 384 */     party.getMembers().put(tid, Party.Role.MEMBER);
/* 385 */     broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + name(tid) + " was demoted to " + String.valueOf(ChatColor.GRAY) + "member" + String.valueOf(ChatColor.WHITE) + ".");
/* 386 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String transfer(Player actor, Player target, String targetName) {
/* 391 */     Party party = of(actor);
/* 392 */     String err = leaderCheck(party, actor);
/* 393 */     if (err != null) {
/* 394 */       return err;
/*     */     }
/* 396 */     UUID tid = (target != null) ? target.getUniqueId() : idByName(party, targetName);
/* 397 */     if (tid == null || !party.has(tid)) {
/* 398 */       return ((targetName == null) ? "That player" : targetName) + " is not in your party.";
/*     */     }
/* 400 */     if (tid.equals(actor.getUniqueId())) {
/* 401 */       return "You are already the leader.";
/*     */     }
/* 403 */     party.getMembers().put(tid, Party.Role.LEADER);
/* 404 */     party.getMembers().put(actor.getUniqueId(), Party.Role.MOD);
/* 405 */     broadcast(party, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + name(tid) + " is the new party leader.");
/* 406 */     return null;
/*     */   }
/*     */   
/*     */   private static String leaderCheck(Party party, Player actor) {
/* 410 */     if (party == null) {
/* 411 */       return "You are not in a party.";
/*     */     }
/* 413 */     if (!party.getLeader().equals(actor.getUniqueId())) {
/* 414 */       return "Only the party leader can do that.";
/*     */     }
/* 416 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String setLimit(Player actor, int limit) {
/* 422 */     Party party = of(actor);
/* 423 */     String err = leaderCheck(party, actor);
/* 424 */     if (err != null) {
/* 425 */       return err;
/*     */     }
/* 427 */     if (limit < 2 || limit > maxLimit()) {
/* 428 */       return "The limit must be between 2 and " + maxLimit() + ".";
/*     */     }
/* 430 */     if (limit < party.size()) {
/* 431 */       return "Your party already has " + party.size() + " members. Kick someone first.";
/*     */     }
/* 433 */     party.setLimit(limit);
/* 434 */     broadcast(party, String.valueOf(ChatColor.GRAY) + "Party limit set to " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + limit + ".");
/* 435 */     return null;
/*     */   }
/*     */   
/*     */   public String setMuted(Player actor, boolean muted) {
/* 439 */     Party party = of(actor);
/* 440 */     if (party == null) {
/* 441 */       return "You are not in a party.";
/*     */     }
/* 443 */     if (!party.isStaff(actor.getUniqueId())) {
/* 444 */       return "Only the party leader and mods can do that.";
/*     */     }
/* 446 */     if (party.isMuted() == muted) {
/* 447 */       return muted ? "Party chat is already muted." : "Party chat is not muted.";
/*     */     }
/* 449 */     party.setMuted(muted);
/* 450 */     broadcast(party, muted ? (
/* 451 */         String.valueOf(ChatColor.RED) + "Party chat was muted by " + String.valueOf(ChatColor.RED) + ". Only the leader and mods can talk.") : (
/* 452 */         String.valueOf(ChatColor.GREEN) + "Party chat was unmuted by " + String.valueOf(ChatColor.GREEN) + "."));
/* 453 */     return null;
/*     */   }
/*     */   
/*     */   public String setOpen(Player actor, boolean open) {
/* 457 */     Party party = of(actor);
/* 458 */     String err = leaderCheck(party, actor);
/* 459 */     if (err != null) {
/* 460 */       return err;
/*     */     }
/* 462 */     party.setOpen(open);
/* 463 */     broadcast(party, open ? (
/* 464 */         String.valueOf(ChatColor.GREEN) + "The party is now open: anybody can join with /party join " + String.valueOf(ChatColor.GREEN) + ".") : (
/* 465 */         String.valueOf(ChatColor.GRAY) + "The party is now invite-only."));
/* 466 */     return null;
/*     */   }
/*     */   
/*     */   public String setAllInvite(Player actor, boolean allInvite) {
/* 470 */     Party party = of(actor);
/* 471 */     String err = leaderCheck(party, actor);
/* 472 */     if (err != null) {
/* 473 */       return err;
/*     */     }
/* 475 */     party.setAllInvite(allInvite);
/* 476 */     broadcast(party, allInvite ? (
/* 477 */         String.valueOf(ChatColor.GREEN) + "Every member can invite players now.") : (
/* 478 */         String.valueOf(ChatColor.GRAY) + "Only the leader and mods can invite players now."));
/* 479 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isChatOn(Player p) {
/* 484 */     return this.chatOn.contains(p.getUniqueId());
/*     */   }
/*     */   public boolean toggleChat(Player p) {
/* 487 */     if (!this.chatOn.remove(p.getUniqueId())) {
/* 488 */       this.chatOn.add(p.getUniqueId());
/* 489 */       return true;
/*     */     } 
/* 491 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String chat(Player p, String message) {
/* 496 */     Party party = of(p);
/* 497 */     if (party == null) {
/* 498 */       this.chatOn.remove(p.getUniqueId());
/* 499 */       return "You are not in a party.";
/*     */     } 
/* 501 */     if (party.isMuted() && !party.isStaff(p.getUniqueId())) {
/* 502 */       return "Party chat is muted. Only the leader and mods can talk.";
/*     */     }
/* 504 */     Party.Role r = party.roleOf(p.getUniqueId());
/* 505 */     String rank = (r == Party.Role.LEADER) ? "&6[Leader] " : ((r == Party.Role.MOD) ? "&a[Mod] " : "");
/* 506 */     String format = this.plugin.getSettings().getString("party.chat-format", "&9Party &8> %rank%&f%player%&8: &7%message%");
/*     */     
/* 508 */     String out = Text.color(format.replace("%rank%", rank).replace("%player%", p.getName())
/* 509 */         .replace("%message%", message));
/* 510 */     for (UUID id : party.ids()) {
/* 511 */       Player m = Bukkit.getPlayer(id);
/* 512 */       if (m != null) {
/* 513 */         m.sendMessage(out);
/*     */       }
/*     */     } 
/* 516 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<List<Player>> splitIntoTeams(List<Player> players, int teamSize, int teamCount) {
/* 523 */     List<List<Player>> teams = new ArrayList<>();
/* 524 */     for (int i = 0; i < teamCount; i++) {
/* 525 */       teams.add(new ArrayList<>());
/*     */     }
/*     */     
/* 528 */     List<List<Player>> groups = new ArrayList<>();
/* 529 */     Set<UUID> used = new HashSet<>();
/* 530 */     for (Player p : players) {
/* 531 */       if (used.contains(p.getUniqueId())) {
/*     */         continue;
/*     */       }
/* 534 */       List<Player> g = new ArrayList<>();
/* 535 */       Party party = of(p);
/* 536 */       for (Player q : players) {
/* 537 */         if (!used.contains(q.getUniqueId()) && (q == p || (party != null && of(q) == party))) {
/* 538 */           g.add(q);
/* 539 */           used.add(q.getUniqueId());
/*     */         } 
/*     */       } 
/* 542 */       groups.add(g);
/*     */     } 
/* 544 */     Collections.shuffle(groups);
/* 545 */     groups.sort((a, b) -> Integer.compare(b.size(), a.size()));
/* 546 */     List<Player> leftovers = new ArrayList<>();
/* 547 */     for (List<Player> g : groups) {
/* 548 */       List<Player> best = null;
/* 549 */       for (List<Player> t : teams) {
/* 550 */         if (t.size() + g.size() <= teamSize && (best == null || t.size() > best.size())) {
/* 551 */           best = t;
/*     */         }
/*     */       } 
/* 554 */       if (best != null) {
/* 555 */         best.addAll(g); continue;
/*     */       } 
/* 557 */       leftovers.addAll(g);
/*     */     } 
/*     */     
/* 560 */     Collections.shuffle(leftovers);
/* 561 */     for (Player p : leftovers) {
/* 562 */       for (List<Player> t : teams) {
/* 563 */         if (t.size() < teamSize) {
/* 564 */           t.add(p);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 569 */     return teams;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\PartyManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */