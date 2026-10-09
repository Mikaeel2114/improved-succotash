/*     */ package com.example.bedfight.party;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.ClickType;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryDragEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.SkullMeta;
/*     */ 
/*     */ public final class PartyGui implements Listener {
/*  24 */   private static final String TITLE = String.valueOf(ChatColor.DARK_GRAY) + "Party settings";
/*     */   private static final int INFO = 4;
/*     */   private static final int OPEN = 10;
/*     */   private static final int ALL_INVITE = 12;
/*     */   private static final int MUTE = 14;
/*     */   private static final int LIMIT = 16;
/*     */   private static final int MEMBERS_FROM = 27;
/*     */   private static final int MEMBERS_TO = 45;
/*     */   private static final int DISBAND = 49;
/*     */   private static final int CLOSE = 53;
/*     */   private final BedFightPlugin plugin;
/*     */   private final PartyManager parties;
/*     */   
/*     */   private static final class Holder
/*     */     implements InventoryHolder {
/*     */     Inventory inventory;
/*  40 */     final UUID[] memberAt = new UUID[54];
/*     */ 
/*     */     
/*     */     public Inventory getInventory() {
/*  44 */       return this.inventory;
/*     */     }
/*     */   }
/*     */   
/*     */   public PartyGui(BedFightPlugin plugin, PartyManager parties) {
/*  49 */     this.plugin = plugin;
/*  50 */     this.parties = parties;
/*     */   }
/*     */   
/*     */   public void open(Player p) {
/*  54 */     if (this.parties.of(p) == null) {
/*  55 */       this.parties.tell(p, String.valueOf(ChatColor.RED) + "You are not in a party. Invite someone with /party <player> first.");
/*     */       return;
/*     */     } 
/*  58 */     Holder holder = new Holder();
/*  59 */     Inventory inv = Bukkit.createInventory(holder, 54, TITLE);
/*  60 */     holder.inventory = inv;
/*  61 */     render(p, inv, holder);
/*  62 */     p.openInventory(inv);
/*     */   }
/*     */   
/*     */   private void render(Player viewer, Inventory inv, Holder holder) {
/*  66 */     Party party = this.parties.of(viewer);
/*  67 */     if (party == null) {
/*     */       return;
/*     */     }
/*  70 */     boolean leader = party.getLeader().equals(viewer.getUniqueId());
/*  71 */     boolean staff = party.isStaff(viewer.getUniqueId());
/*  72 */     ItemStack pane = pane(7);
/*  73 */     ItemStack accent = pane(11);
/*  74 */     for (int i = 0; i < 54; i++) {
/*  75 */       inv.setItem(i, (i < 9 || i >= 45) ? accent : pane);
/*  76 */       holder.memberAt[i] = null;
/*     */     } 
/*  78 */     String lock = leader ? null : (String.valueOf(ChatColor.RED) + "Only the party leader can change this.");
/*     */     
/*  80 */     inv.setItem(4, item(Material.NETHER_STAR, 1, String.valueOf(ChatColor.BLUE) + String.valueOf(ChatColor.BLUE) + "Party", new String[] { String.valueOf(ChatColor.GRAY) + "Members: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + party
/*  81 */             .size() + "/" + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE), String.valueOf(ChatColor.GRAY) + "Leader: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.GOLD), 
/*  82 */             "", String.valueOf(ChatColor.GRAY) + "Your role: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) }));
/*     */ 
/*     */ 
/*     */     
/*  86 */     inv.setItem(10, item(Material.IRON_DOOR, 1, 
/*  87 */           String.valueOf(party.isOpen() ? ChatColor.GREEN : ChatColor.RED) + "Open party: " + String.valueOf(party.isOpen() ? ChatColor.GREEN : ChatColor.RED), new String[] { String.valueOf(ChatColor.GRAY) + "ON: everybody can join with", String.valueOf(ChatColor.WHITE) + "/party join " + String.valueOf(ChatColor.WHITE), 
/*     */             
/*  89 */             String.valueOf(ChatColor.GRAY) + "OFF: invite only.", "", 
/*     */ 
/*     */             
/*  92 */             (lock == null) ? (String.valueOf(ChatColor.YELLOW) + "Click to toggle") : lock }));
/*  93 */     inv.setItem(12, item(Material.NAME_TAG, 1, 
/*  94 */           String.valueOf(party.isAllInvite() ? ChatColor.GREEN : ChatColor.RED) + "Everyone can invite: " + String.valueOf(party.isAllInvite() ? ChatColor.GREEN : ChatColor.RED), new String[] { String.valueOf(ChatColor.GRAY) + "ON: every member can invite players.", String.valueOf(ChatColor.GRAY) + "OFF: only the leader and mods can.", "", 
/*     */ 
/*     */ 
/*     */             
/*  98 */             (lock == null) ? (String.valueOf(ChatColor.YELLOW) + "Click to toggle") : lock }));
/*  99 */     inv.setItem(14, item(Material.BOOK_AND_QUILL, 1, 
/* 100 */           String.valueOf(party.isMuted() ? ChatColor.RED : ChatColor.GREEN) + "Party chat: " + String.valueOf(party.isMuted() ? ChatColor.RED : ChatColor.GREEN), new String[] { String.valueOf(ChatColor.GRAY) + "Muted: only the leader and mods", String.valueOf(ChatColor.GRAY) + "can talk in party chat.", "", 
/*     */ 
/*     */ 
/*     */             
/* 104 */             staff ? (String.valueOf(ChatColor.YELLOW) + "Click to toggle") : (String.valueOf(ChatColor.RED) + "Only the leader and mods can change this.") }));
/* 105 */     inv.setItem(16, item(Material.PAPER, Math.max(1, Math.min(64, party.getLimit())), String.valueOf(ChatColor.AQUA) + "Party limit: " + String.valueOf(ChatColor.AQUA) + String.valueOf(ChatColor.WHITE), 
/* 106 */           new String[] { String.valueOf(ChatColor.GRAY) + "Maximum number of players (2-" + String.valueOf(ChatColor.GRAY) + ").", 
/* 107 */             "", 
/*     */             
/* 109 */             (lock == null) ? (String.valueOf(ChatColor.YELLOW) + "Left click: +1   Right click: -1") : lock }));
/*     */     
/* 111 */     int slot = 27;
/* 112 */     for (UUID id : party.ids()) {
/* 113 */       if (slot >= 45) {
/*     */         break;
/*     */       }
/* 116 */       Party.Role role = party.roleOf(id);
/* 117 */       ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
/* 118 */       SkullMeta meta = (SkullMeta)head.getItemMeta();
/* 119 */       String n = nameOf(id);
/* 120 */       meta.setOwner(n);
/* 121 */       ChatColor c = (role == Party.Role.LEADER) ? ChatColor.GOLD : ((role == Party.Role.MOD) ? ChatColor.GREEN : ChatColor.WHITE);
/* 122 */       meta.setDisplayName(String.valueOf(c) + String.valueOf(c));
/* 123 */       List<String> lore = new ArrayList<>();
/* 124 */       lore.add(String.valueOf(ChatColor.GRAY) + "Role: " + String.valueOf(ChatColor.GRAY) + String.valueOf(c));
/* 125 */       if (leader && role != Party.Role.LEADER) {
/* 126 */         lore.add("");
/* 127 */         lore.add(String.valueOf(ChatColor.YELLOW) + "Left click: " + String.valueOf(ChatColor.YELLOW));
/* 128 */         lore.add(String.valueOf(ChatColor.YELLOW) + "Right click: kick");
/* 129 */         lore.add(String.valueOf(ChatColor.YELLOW) + "Shift + left click: make leader");
/* 130 */       } else if (staff && role == Party.Role.MEMBER) {
/* 131 */         lore.add("");
/* 132 */         lore.add(String.valueOf(ChatColor.YELLOW) + "Right click: kick");
/*     */       } 
/* 134 */       meta.setLore(lore);
/* 135 */       head.setItemMeta((ItemMeta)meta);
/* 136 */       inv.setItem(slot, head);
/* 137 */       holder.memberAt[slot] = id;
/* 138 */       slot++;
/*     */     } 
/*     */     
/* 141 */     inv.setItem(49, item(Material.TNT, 1, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "Disband party", new String[] { String.valueOf(ChatColor.GRAY) + "Removes everybody from the party.", "", 
/*     */ 
/*     */             
/* 144 */             leader ? (String.valueOf(ChatColor.YELLOW) + "Shift + click to confirm") : lock }));
/* 145 */     inv.setItem(53, item(Material.BARRIER, 1, String.valueOf(ChatColor.RED) + "Close", new String[0])); } @EventHandler public void onClick(InventoryClickEvent e) { Holder holder;
/*     */     Player p;
/*     */     UUID target;
/*     */     String tn;
/*     */     Player tp;
/* 150 */     InventoryHolder inventoryHolder = e.getInventory().getHolder(); if (inventoryHolder instanceof Holder) { holder = (Holder)inventoryHolder; HumanEntity humanEntity = e.getWhoClicked(); if (humanEntity instanceof Player) { p = (Player)humanEntity; } else { return; }
/*     */        }
/*     */     else { return; }
/* 153 */      e.setCancelled(true);
/* 154 */     int raw = e.getRawSlot();
/* 155 */     if (raw < 0 || raw >= 54) {
/*     */       return;
/*     */     }
/* 158 */     Party party = this.parties.of(p);
/* 159 */     if (party == null) {
/* 160 */       p.closeInventory();
/*     */       return;
/*     */     } 
/* 163 */     String err = null;
/* 164 */     boolean leaderClick = false;
/* 165 */     switch (raw) {
/*     */       case 10:
/* 167 */         err = this.parties.setOpen(p, !party.isOpen());
/*     */         break;
/*     */       case 12:
/* 170 */         err = this.parties.setAllInvite(p, !party.isAllInvite());
/*     */         break;
/*     */       case 14:
/* 173 */         err = this.parties.setMuted(p, !party.isMuted());
/*     */         break;
/*     */       case 16:
/* 176 */         if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
/* 177 */           err = this.parties.setLimit(p, party.getLimit() - 1); break;
/*     */         } 
/* 179 */         err = this.parties.setLimit(p, party.getLimit() + 1);
/*     */         break;
/*     */       
/*     */       case 49:
/* 183 */         if (!e.isShiftClick()) {
/* 184 */           this.parties.tell(p, String.valueOf(ChatColor.YELLOW) + "Shift + click the TNT to disband the party.");
/*     */           return;
/*     */         } 
/* 187 */         err = this.parties.disband(p);
/* 188 */         if (err == null) {
/* 189 */           p.closeInventory();
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       case 53:
/* 194 */         p.closeInventory();
/*     */         return;
/*     */       default:
/* 197 */         target = holder.memberAt[raw];
/* 198 */         if (target == null || target.equals(p.getUniqueId())) {
/*     */           return;
/*     */         }
/* 201 */         tn = nameOf(target);
/* 202 */         tp = Bukkit.getPlayer(target);
/* 203 */         if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
/* 204 */           err = this.parties.kick(p, tp, tn); break;
/* 205 */         }  if (e.getClick() == ClickType.SHIFT_LEFT) {
/* 206 */           err = this.parties.transfer(p, tp, tn); break;
/* 207 */         }  if (e.getClick() == ClickType.LEFT) {
/* 208 */           err = (party.roleOf(target) == Party.Role.MOD) ? this.parties.demote(p, tp, tn) : this.parties.promote(p, tp, tn);
/*     */           break;
/*     */         } 
/*     */         return;
/*     */     } 
/*     */     
/* 214 */     if (err != null) {
/* 215 */       this.parties.tell(p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*     */     }
/* 217 */     if (this.parties.of(p) == null) {
/* 218 */       p.closeInventory();
/*     */     } else {
/* 220 */       render(p, e.getInventory(), holder);
/*     */     }  }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onDrag(InventoryDragEvent e) {
/* 226 */     if (e.getInventory().getHolder() instanceof Holder) {
/* 227 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String nameOf(UUID id) {
/* 232 */     Player p = Bukkit.getPlayer(id);
/* 233 */     if (p != null) {
/* 234 */       return p.getName();
/*     */     }
/* 236 */     String n = Bukkit.getOfflinePlayer(id).getName();
/* 237 */     return (n == null) ? "?" : n;
/*     */   }
/*     */   
/*     */   private static String onOff(boolean b) {
/* 241 */     return b ? "ON" : "OFF";
/*     */   }
/*     */   
/*     */   private static ItemStack pane(int data) {
/* 245 */     return item(Material.STAINED_GLASS_PANE, 1, " ", data, new String[0]);
/*     */   }
/*     */   
/*     */   private static ItemStack item(Material m, int amount, String name, String... lore) {
/* 249 */     return item(m, amount, name, 0, lore);
/*     */   }
/*     */   
/*     */   private static ItemStack item(Material m, int amount, String name, int data, String... lore) {
/* 253 */     ItemStack it = new ItemStack(m, amount, (short)data);
/* 254 */     ItemMeta meta = it.getItemMeta();
/* 255 */     meta.setDisplayName(name);
/* 256 */     List<String> l = new ArrayList<>();
/* 257 */     for (String s : lore) {
/* 258 */       l.add(s);
/*     */     }
/* 260 */     if (!l.isEmpty()) {
/* 261 */       meta.setLore(l);
/*     */     }
/* 263 */     it.setItemMeta(meta);
/* 264 */     return it;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\PartyGui.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */