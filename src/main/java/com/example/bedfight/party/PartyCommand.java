/*     */ package com.example.bedfight.party;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.TabCompleter;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public final class PartyCommand
/*     */   implements CommandExecutor, TabCompleter {
/*  19 */   private static final List<String> SUBS = Arrays.asList(new String[] { "invite", "join", "accept", "deny", "leave", "kick", "disband", "chat", "mute", "unmute", "setting", "list", "limit", "promote", "demote", "transfer", "help" });
/*     */   
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   private final PartyManager parties;
/*     */   private final PartyGui gui;
/*     */   
/*     */   public PartyCommand(BedFightPlugin plugin, PartyManager parties, PartyGui gui) {
/*  27 */     this.plugin = plugin;
/*  28 */     this.parties = parties;
/*  29 */     this.gui = gui;
/*     */   }
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/*     */     Player p;
/*  34 */     if (sender instanceof Player) { p = (Player)sender; }
/*  35 */     else { sender.sendMessage("Players only.");
/*  36 */       return true; }
/*     */     
/*  38 */     if (cmd.getName().equalsIgnoreCase("pc")) {
/*  39 */       return chat(p, args, 0);
/*     */     }
/*  41 */     if (args.length == 0) {
/*  42 */       if (this.parties.of(p) != null) {
/*  43 */         list(p);
/*     */       } else {
/*  45 */         help(p);
/*     */       } 
/*  47 */       return true;
/*     */     } 
/*  49 */     String sub = args[0].toLowerCase(Locale.ROOT);
/*  50 */     switch (sub) {
/*     */       case "help":
/*     */       case "?":
/*  53 */         help(p);
/*  54 */         return true;
/*     */       case "invite":
/*     */       case "add":
/*  57 */         if (args.length < 2) {
/*  58 */           return usage(p, "/party invite <player>");
/*     */         }
/*  60 */         result(p, this.parties.invite(p, Bukkit.getPlayer(args[1])));
/*  61 */         return true;
/*     */       case "join":
/*     */       case "accept":
/*  64 */         if (args.length < 2) {
/*  65 */           return usage(p, "/party join <player>");
/*     */         }
/*  67 */         result(p, this.parties.join(p, Bukkit.getPlayer(args[1])));
/*  68 */         return true;
/*     */       case "deny":
/*     */       case "decline":
/*  71 */         if (args.length < 2) {
/*  72 */           return usage(p, "/party deny <player>");
/*     */         }
/*  74 */         result(p, this.parties.deny(p, Bukkit.getPlayer(args[1])));
/*  75 */         return true;
/*     */       case "leave":
/*  77 */         result(p, this.parties.leave(p));
/*  78 */         return true;
/*     */       case "kick":
/*  80 */         if (args.length < 2) {
/*  81 */           return usage(p, "/party kick <player>");
/*     */         }
/*  83 */         result(p, this.parties.kick(p, Bukkit.getPlayerExact(args[1]), args[1]));
/*  84 */         return true;
/*     */       case "disband":
/*  86 */         result(p, this.parties.disband(p));
/*  87 */         return true;
/*     */       case "chat":
/*     */       case "c":
/*  90 */         return chat(p, args, 1);
/*     */       case "mute":
/*  92 */         result(p, this.parties.setMuted(p, true));
/*  93 */         return true;
/*     */       case "unmute":
/*  95 */         result(p, this.parties.setMuted(p, false));
/*  96 */         return true;
/*     */       case "setting":
/*     */       case "settings":
/*     */       case "options":
/* 100 */         this.gui.open(p);
/* 101 */         return true;
/*     */       case "list":
/*     */       case "members":
/* 104 */         list(p);
/* 105 */         return true;
/*     */       case "limit":
/* 107 */         if (args.length < 2) {
/* 108 */           return usage(p, "/party limit <number>");
/*     */         }
/*     */         try {
/* 111 */           result(p, this.parties.setLimit(p, Integer.parseInt(args[1])));
/* 112 */         } catch (NumberFormatException ex) {
/* 113 */           this.parties.tell(p, String.valueOf(ChatColor.RED) + "'" + String.valueOf(ChatColor.RED) + "' is not a number.");
/*     */         } 
/* 115 */         return true;
/*     */       case "promote":
/* 117 */         if (args.length < 2) {
/* 118 */           return usage(p, "/party promote <player>");
/*     */         }
/* 120 */         result(p, this.parties.promote(p, Bukkit.getPlayerExact(args[1]), args[1]));
/* 121 */         return true;
/*     */       case "demote":
/* 123 */         if (args.length < 2) {
/* 124 */           return usage(p, "/party demote <player>");
/*     */         }
/* 126 */         result(p, this.parties.demote(p, Bukkit.getPlayerExact(args[1]), args[1]));
/* 127 */         return true;
/*     */       case "transfer":
/* 129 */         if (args.length < 2) {
/* 130 */           return usage(p, "/party transfer <player>");
/*     */         }
/* 132 */         result(p, this.parties.transfer(p, Bukkit.getPlayerExact(args[1]), args[1]));
/* 133 */         return true;
/*     */     } 
/*     */     
/* 136 */     result(p, this.parties.invite(p, Bukkit.getPlayer(args[0])));
/* 137 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean usage(Player p, String text) {
/* 142 */     this.parties.tell(p, String.valueOf(ChatColor.RED) + "Usage: " + String.valueOf(ChatColor.RED));
/* 143 */     return true;
/*     */   }
/*     */   
/*     */   private void result(Player p, String error) {
/* 147 */     if (error != null) {
/* 148 */       this.parties.tell(p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean chat(Player p, String[] args, int from) {
/* 154 */     if (args.length <= from) {
/* 155 */       if (this.parties.of(p) == null) {
/* 156 */         this.parties.tell(p, String.valueOf(ChatColor.RED) + "You are not in a party.");
/* 157 */         return true;
/*     */       } 
/* 159 */       boolean on = this.parties.toggleChat(p);
/* 160 */       this.parties.tell(p, on ? (
/* 161 */           String.valueOf(ChatColor.GREEN) + "Party chat is ON: everything you type goes to your party. Use /pc again to switch it off.") : (
/* 162 */           String.valueOf(ChatColor.GRAY) + "Party chat is OFF: you talk in public chat again."));
/* 163 */       return true;
/*     */     } 
/* 165 */     StringBuilder sb = new StringBuilder();
/* 166 */     for (int i = from; i < args.length; i++) {
/* 167 */       if (i > from) {
/* 168 */         sb.append(' ');
/*     */       }
/* 170 */       sb.append(args[i]);
/*     */     } 
/* 172 */     result(p, this.parties.chat(p, sb.toString()));
/* 173 */     return true;
/*     */   }
/*     */   
/*     */   private void list(Player p) {
/* 177 */     Party party = this.parties.of(p);
/* 178 */     if (party == null) {
/* 179 */       this.parties.tell(p, String.valueOf(ChatColor.RED) + "You are not in a party. Invite someone with /party <player>.");
/*     */       return;
/*     */     } 
/* 182 */     p.sendMessage(String.valueOf(ChatColor.DARK_GRAY) + String.valueOf(ChatColor.DARK_GRAY) + "-----------------------------------");
/* 183 */     p.sendMessage(String.valueOf(ChatColor.BLUE) + "Party " + String.valueOf(ChatColor.BLUE) + "(" + String.valueOf(ChatColor.GRAY) + "/" + party.size() + ")" + party.getLimit() + "  " + String.valueOf(ChatColor.DARK_GRAY) + (
/* 184 */         party.isOpen() ? (String.valueOf(ChatColor.GREEN) + "open") : (String.valueOf(ChatColor.GRAY) + "invite-only")) + (
/* 185 */         party.isMuted() ? (String.valueOf(ChatColor.RED) + "  muted") : ""));
/*     */     
/* 187 */     List<String> mods = new ArrayList<>();
/* 188 */     List<String> members = new ArrayList<>();
/* 189 */     String leader = "?";
/* 190 */     for (UUID id : party.ids()) {
/* 191 */       Player m = Bukkit.getPlayer(id);
/* 192 */       String n = String.valueOf((m != null) ? ChatColor.GREEN : ChatColor.RED) + String.valueOf((m != null) ? ChatColor.GREEN : ChatColor.RED);
/* 193 */       switch (party.roleOf(id)) {
/*     */         case LEADER:
/* 195 */           leader = n;
/*     */           continue;
/*     */         case MOD:
/* 198 */           mods.add(n);
/*     */           continue;
/*     */       } 
/* 201 */       members.add(n);
/*     */     } 
/*     */ 
/*     */     
/* 205 */     p.sendMessage(String.valueOf(ChatColor.GOLD) + "Leader: " + String.valueOf(ChatColor.GOLD));
/* 206 */     if (!mods.isEmpty()) {
/* 207 */       p.sendMessage(String.valueOf(ChatColor.GREEN) + "Mods: " + String.valueOf(ChatColor.GREEN));
/*     */     }
/* 209 */     if (!members.isEmpty()) {
/* 210 */       p.sendMessage(String.valueOf(ChatColor.WHITE) + "Members: " + String.valueOf(ChatColor.WHITE));
/*     */     }
/* 212 */     if (!party.getInvites().isEmpty()) {
/* 213 */       List<String> inv = new ArrayList<>();
/* 214 */       for (UUID id : party.getInvites().keySet()) {
/* 215 */         Player m = Bukkit.getPlayer(id);
/* 216 */         if (m != null) {
/* 217 */           inv.add(m.getName());
/*     */         }
/*     */       } 
/* 220 */       if (!inv.isEmpty()) {
/* 221 */         p.sendMessage(String.valueOf(ChatColor.GRAY) + "Invited: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.YELLOW));
/*     */       }
/*     */     } 
/* 224 */     p.sendMessage(String.valueOf(ChatColor.DARK_GRAY) + String.valueOf(ChatColor.DARK_GRAY) + "-----------------------------------");
/*     */   }
/*     */   
/*     */   private void help(Player p) {
/* 228 */     p.sendMessage(String.valueOf(ChatColor.DARK_GRAY) + String.valueOf(ChatColor.DARK_GRAY) + "-----------------------------------");
/* 229 */     p.sendMessage(String.valueOf(ChatColor.BLUE) + String.valueOf(ChatColor.BLUE) + "Party commands");
/* 230 */     String[][] lines = { { "/party <player>", "Invite a player (creates the party)" }, { "/party join <player>", "Join a party you were invited to (or an open one)" }, { "/party deny <player>", "Decline an invite" }, { "/party leave", "Leave your party" }, { "/party list", "Show the members" }, { "/party chat [msg] | /pc [msg]", "Party chat (toggle, or one message)" }, { "/party kick <player>", "Kick a member (leader/mod)" }, { "/party mute | unmute", "Mute party chat for normal members (leader/mod)" }, { "/party promote | demote <player>", "Make a member a mod / back to member (leader)" }, { "/party transfer <player>", "Give the leadership away (leader)" }, { "/party limit <number>", "Maximum party size (leader)" }, { "/party setting", "Open the settings menu (leader)" }, { "/party disband", "Disband the party (leader)" } };
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
/* 245 */     for (String[] l : lines) {
/* 246 */       p.sendMessage(String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW) + l[0] + " - " + String.valueOf(ChatColor.GRAY));
/*     */     }
/* 248 */     p.sendMessage(String.valueOf(ChatColor.DARK_GRAY) + String.valueOf(ChatColor.DARK_GRAY) + "-----------------------------------");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
/* 255 */     List<String> out = new ArrayList<>();
/* 256 */     if (sender instanceof Player) { Player p = (Player)sender; if (!cmd.getName().equalsIgnoreCase("pc")) {
/*     */ 
/*     */         
/* 259 */         if (args.length == 1) {
/* 260 */           out.addAll(SUBS);
/* 261 */           addOnline(out, p, null);
/* 262 */         } else if (args.length == 2) {
/* 263 */           String sub = args[0].toLowerCase(Locale.ROOT);
/* 264 */           Party party = this.parties.of(p);
/* 265 */           switch (sub) {
/*     */             case "invite":
/*     */             case "add":
/* 268 */               addOnline(out, p, party);
/*     */               break;
/*     */             case "join":
/*     */             case "accept":
/*     */             case "deny":
/*     */             case "decline":
/* 274 */               for (Player o : Bukkit.getOnlinePlayers()) {
/* 275 */                 Party op = this.parties.of(o);
/* 276 */                 if (op != null && op != party) {
/* 277 */                   out.add(o.getName());
/*     */                 }
/*     */               } 
/*     */               break;
/*     */             case "kick":
/*     */             case "promote":
/*     */             case "demote":
/*     */             case "transfer":
/* 285 */               if (party != null) {
/* 286 */                 for (UUID id : party.ids()) {
/* 287 */                   Player m = Bukkit.getPlayer(id);
/* 288 */                   if (m != null && !m.equals(p)) {
/* 289 */                     out.add(m.getName());
/*     */                   }
/*     */                 } 
/*     */               }
/*     */               break;
/*     */             case "limit":
/* 295 */               out.addAll(Arrays.asList(new String[] { "2", "4", "6", "8", "10", "16" }));
/*     */               break;
/*     */           } 
/*     */ 
/*     */         
/*     */         } 
/* 301 */         String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
/* 302 */         out.removeIf(s -> !s.toLowerCase(Locale.ROOT).startsWith(prefix));
/* 303 */         return out;
/*     */       }  }
/*     */     
/*     */     return out;
/*     */   } private void addOnline(List<String> out, Player p, Party own) {
/* 308 */     for (Player o : Bukkit.getOnlinePlayers()) {
/* 309 */       if (!o.equals(p) && this.parties.of(o) == null)
/* 310 */         out.add(o.getName()); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\PartyCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */