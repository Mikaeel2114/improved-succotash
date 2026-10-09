/*     */ package com.example.bedfight.gui;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.Arena;
/*     */ import com.example.bedfight.game.MatchOptions;
/*     */ import com.example.bedfight.game.Mode;
/*     */ import com.example.bedfight.util.Sounds;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.enchantments.Enchantment;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.ClickType;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryDragEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemFlag;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PrivateMatchGui
/*     */   implements Listener
/*     */ {
/*     */   public static final String PERMISSION = "bedfight.rank.mvpplusplus";
/*  37 */   private static final String TITLE = String.valueOf(ChatColor.DARK_GRAY) + "Private match";
/*     */   
/*     */   private static final int INFO = 4;
/*     */   private static final int MAP = 13;
/*  41 */   private static final int[] MODE_SLOTS = new int[] { 20, 22, 24 };
/*     */   
/*     */   private static final int RESPAWN = 38;
/*     */   private static final int BEDS = 39;
/*     */   private static final int FALL = 40;
/*     */   private static final int SPECS = 41;
/*     */   private static final int TIME = 42;
/*     */   private static final int CLOSE = 45;
/*     */   private static final int START = 49;
/*     */   private static final int RESET = 53;
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   private static final class Session
/*     */   {
/*  55 */     int mapIndex = -1;
/*  56 */     Mode mode = Mode.SOLO;
/*     */     MatchOptions options;
/*  58 */     List<String> maps = new ArrayList<>();
/*     */   }
/*     */   
/*     */   private static final class Holder implements InventoryHolder {
/*     */     final PrivateMatchGui.Session session;
/*     */     Inventory inventory;
/*     */     
/*     */     Holder(PrivateMatchGui.Session session) {
/*  66 */       this.session = session;
/*     */     }
/*     */ 
/*     */     
/*     */     public Inventory getInventory() {
/*  71 */       return this.inventory;
/*     */     }
/*     */   }
/*     */   
/*     */   public PrivateMatchGui(BedFightPlugin plugin) {
/*  76 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */   public void open(Player p) {
/*  80 */     Session s = new Session();
/*  81 */     for (Arena a : this.plugin.getArenaManager().all()) {
/*  82 */       s.maps.add(a.getName());
/*     */     }
/*  84 */     if (s.maps.isEmpty()) {
/*  85 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "There are no arenas available.");
/*     */       return;
/*     */     } 
/*  88 */     s.options = MatchOptions.defaults(this.plugin.getSettings());
/*     */     
/*  90 */     int party = this.plugin.getParties().getPartyMembers(p).size();
/*  91 */     for (Mode m : Mode.values()) {
/*  92 */       if (m.totalPlayers() == party) {
/*  93 */         s.mode = m;
/*     */       }
/*     */     } 
/*  96 */     Holder holder = new Holder(s);
/*  97 */     Inventory inv = Bukkit.createInventory(holder, 54, TITLE);
/*  98 */     holder.inventory = inv;
/*  99 */     render(p, inv, s);
/* 100 */     p.openInventory(inv);
/* 101 */     Sounds.play(this.plugin, p, "gui-open");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void render(Player p, Inventory inv, Session s) {
/* 107 */     ItemStack gray = pane(7, " ");
/* 108 */     ItemStack accent = pane(3, " ");
/* 109 */     for (int i = 0; i < 54; i++) {
/* 110 */       inv.setItem(i, (i < 9 || i >= 45) ? accent : gray);
/*     */     }
/*     */     
/* 113 */     int party = this.plugin.getParties().getPartyMembers(p).size();
/* 114 */     boolean partyOk = (party == s.mode.totalPlayers());
/* 115 */     inv.setItem(4, item(Material.NETHER_STAR, 0, String.valueOf(ChatColor.AQUA) + String.valueOf(ChatColor.AQUA) + "Private Match", false, new String[] { String.valueOf(ChatColor.GRAY) + "Configure your match, then press START.", "", String.valueOf(ChatColor.GRAY) + "Party size: " + String.valueOf(ChatColor.GRAY) + 
/*     */ 
/*     */             
/* 118 */             String.valueOf(partyOk ? ChatColor.GREEN : ChatColor.RED) + party + " / " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + s.mode
/* 119 */             .totalPlayers() + " needed" }));
/*     */ 
/*     */     
/* 122 */     String mapName = (s.mapIndex < 0) ? "Random" : s.maps.get(s.mapIndex);
/* 123 */     List<String> mapLore = new ArrayList<>();
/* 124 */     mapLore.add(String.valueOf(ChatColor.GRAY) + "Selected: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.GREEN));
/* 125 */     mapLore.add("");
/* 126 */     mapLore.add(mark((s.mapIndex < 0)) + "Random");
/* 127 */     for (int j = 0; j < s.maps.size() && j < 12; j++) {
/* 128 */       Arena a = this.plugin.getArenaManager().get(s.maps.get(j));
/* 129 */       String teams = (a == null) ? "" : (String.valueOf(ChatColor.DARK_GRAY) + " (" + String.valueOf(ChatColor.DARK_GRAY) + " teams)");
/* 130 */       mapLore.add(mark((j == s.mapIndex)) + mark((j == s.mapIndex)) + (String)s.maps.get(j));
/*     */     } 
/* 132 */     mapLore.add("");
/* 133 */     mapLore.add(String.valueOf(ChatColor.YELLOW) + "Left click: next map");
/* 134 */     mapLore.add(String.valueOf(ChatColor.YELLOW) + "Right click: previous map");
/* 135 */     inv.setItem(13, item(Material.MAP, 0, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.GREEN) + "Map", false, mapLore
/* 136 */           .<String>toArray(new String[0])));
/*     */ 
/*     */     
/* 139 */     Material[] icons = { Material.WOOD_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD };
/* 140 */     Mode[] modes = Mode.values();
/* 141 */     for (int k = 0; k < modes.length; k++) {
/* 142 */       boolean sel = (modes[k] == s.mode);
/* 143 */       inv.setItem(MODE_SLOTS[k], item(icons[k], 0, 
/* 144 */             String.valueOf(sel ? ChatColor.GREEN : ChatColor.GRAY) + String.valueOf(sel ? ChatColor.GREEN : ChatColor.GRAY) + String.valueOf(ChatColor.BOLD), sel, new String[] { String.valueOf(ChatColor.GRAY) + "Players needed in party: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE), 
/* 145 */               "", 
/*     */               
/* 147 */               sel ? (String.valueOf(ChatColor.GREEN) + "✔ Selected") : (String.valueOf(ChatColor.YELLOW) + "Click to select") }));
/*     */     } 
/*     */ 
/*     */     
/* 151 */     MatchOptions o = s.options;
/* 152 */     inv.setItem(38, item(Material.GOLDEN_APPLE, 0, String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.GOLD) + "Respawn delay", false, new String[] { String.valueOf(ChatColor.GRAY) + "Seconds before a player respawns", String.valueOf(ChatColor.GRAY) + "while their bed is alive.", "", String.valueOf(ChatColor.GRAY) + "Current: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE) + "s", "", String.valueOf(ChatColor.YELLOW) + "Left click: next  Right click: previous" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 159 */     inv.setItem(39, toggle(Material.BED, "Beds", o.bedsEnabled, new String[] { "When disabled there are no beds:", "everyone has a single life." }));
/*     */     
/* 161 */     inv.setItem(40, toggle(Material.FEATHER, "Fall damage", o.fallDamage, new String[] { "Take damage when falling." }));
/*     */     
/* 163 */     inv.setItem(41, toggle(Material.EYE_OF_ENDER, "Spectators", o.allowSpectators, new String[] { "Let other players watch the match", "with /bedfight spec <player>." }));
/*     */     
/* 165 */     inv.setItem(42, item(Material.WATCH, 0, String.valueOf(ChatColor.AQUA) + String.valueOf(ChatColor.AQUA) + "Time limit", false, new String[] { String.valueOf(ChatColor.GRAY) + "The match ends in a draw", String.valueOf(ChatColor.GRAY) + "when the time runs out.", "", String.valueOf(ChatColor.GRAY) + "Current: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE), 
/*     */ 
/*     */ 
/*     */             
/* 169 */             "", String.valueOf(ChatColor.YELLOW) + "Left click: next  Right click: previous" }));
/*     */ 
/*     */ 
/*     */     
/* 173 */     inv.setItem(45, item(Material.BARRIER, 0, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED) + "Close", false, new String[0]));
/* 174 */     inv.setItem(53, item(Material.REDSTONE, 0, String.valueOf(ChatColor.RED) + "Reset options", false, new String[] { String.valueOf(ChatColor.GRAY) + "Restore the default rules." }));
/*     */     
/* 176 */     inv.setItem(49, item(Material.EMERALD_BLOCK, 0, 
/* 177 */           String.valueOf(partyOk ? ChatColor.GREEN : ChatColor.RED) + String.valueOf(partyOk ? ChatColor.GREEN : ChatColor.RED) + "START MATCH", partyOk, new String[] { String.valueOf(ChatColor.GRAY) + "Map: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE), String.valueOf(ChatColor.GRAY) + "Mode: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.WHITE), 
/*     */             
/* 179 */             String.valueOf(ChatColor.GRAY) + "Beds: " + String.valueOf(ChatColor.GRAY) + 
/* 180 */             onOff(o.bedsEnabled) + "  Fall damage: " + String.valueOf(ChatColor.GRAY), "", 
/*     */             
/* 182 */             partyOk ? (String.valueOf(ChatColor.YELLOW) + "Click to start!") : (
/* 183 */             String.valueOf(ChatColor.RED) + "Your party needs exactly " + String.valueOf(ChatColor.RED) + " players.") }));
/*     */   }
/*     */   
/*     */   private static String mark(boolean selected) {
/* 187 */     return selected ? (String.valueOf(ChatColor.GREEN) + "▶ ") : (String.valueOf(ChatColor.DARK_GRAY) + "  ");
/*     */   }
/*     */   
/*     */   private static String onOff(boolean b) {
/* 191 */     return b ? (String.valueOf(ChatColor.GREEN) + "On") : (String.valueOf(ChatColor.RED) + "Off");
/*     */   }
/*     */   
/*     */   private static ItemStack toggle(Material m, String name, boolean on, String... description) {
/* 195 */     List<String> lore = new ArrayList<>();
/* 196 */     for (String d : description) {
/* 197 */       lore.add(String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.GRAY));
/*     */     }
/* 199 */     lore.add("");
/* 200 */     lore.add(String.valueOf(ChatColor.GRAY) + "Status: " + String.valueOf(ChatColor.GRAY));
/* 201 */     lore.add("");
/* 202 */     lore.add(String.valueOf(ChatColor.YELLOW) + "Click to toggle");
/* 203 */     return item(m, 0, String.valueOf(on ? ChatColor.GREEN : ChatColor.RED) + String.valueOf(on ? ChatColor.GREEN : ChatColor.RED) + String.valueOf(ChatColor.BOLD), on, lore.<String>toArray(new String[0]));
/*     */   }
/*     */   
/*     */   private static ItemStack pane(int data, String name) {
/* 207 */     return item(Material.STAINED_GLASS_PANE, data, name, false, new String[0]);
/*     */   }
/*     */   
/*     */   private static ItemStack item(Material m, int data, String name, boolean glow, String... lore) {
/* 211 */     ItemStack it = new ItemStack(m, 1, (short)data);
/* 212 */     ItemMeta meta = it.getItemMeta();
/* 213 */     meta.setDisplayName(name);
/* 214 */     if (lore.length > 0) {
/* 215 */       meta.setLore(Arrays.asList(lore));
/*     */     }
/* 217 */     if (glow) {
/* 218 */       meta.addEnchant(Enchantment.DURABILITY, 1, true);
/*     */     }
/* 220 */     meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES });
/* 221 */     it.setItemMeta(meta);
/* 222 */     return it;
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onClick(InventoryClickEvent e) {
/*     */     Holder holder;
/*     */     Player p;
/* 229 */     InventoryHolder inventoryHolder = e.getInventory().getHolder(); if (inventoryHolder instanceof Holder) { holder = (Holder)inventoryHolder; }
/*     */     else
/*     */     { return; }
/* 232 */      e.setCancelled(true);
/* 233 */     HumanEntity humanEntity = e.getWhoClicked(); if (humanEntity instanceof Player) { p = (Player)humanEntity; }
/*     */     else
/*     */     { return; }
/* 236 */      Sounds.play(this.plugin, p, "gui-click");
/* 237 */     int slot = e.getRawSlot();
/* 238 */     if (slot < 0 || slot >= e.getInventory().getSize()) {
/*     */       return;
/*     */     }
/* 241 */     if (!p.hasPermission("bedfight.rank.mvpplusplus")) {
/* 242 */       p.closeInventory();
/* 243 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Only MVP++ can create private matches.");
/*     */       return;
/*     */     } 
/* 246 */     Session s = holder.session;
/* 247 */     boolean backwards = (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT);
/* 248 */     int dir = backwards ? -1 : 1;
/* 249 */     boolean changed = true;
/*     */     
/* 251 */     if (slot == 13)
/*     */     
/* 253 */     { int total = s.maps.size() + 1;
/* 254 */       s.mapIndex = Math.floorMod(s.mapIndex + 1 + dir, total) - 1; }
/* 255 */     else if (slot == MODE_SLOTS[0])
/* 256 */     { s.mode = Mode.SOLO; }
/* 257 */     else if (slot == MODE_SLOTS[1])
/* 258 */     { s.mode = Mode.DUO; }
/* 259 */     else if (slot == MODE_SLOTS[2])
/* 260 */     { s.mode = Mode.TRIO; }
/* 261 */     else if (slot == 38)
/* 262 */     { List<Integer> list = this.plugin.getSettings().getIntList("private-match.respawn-delay-options", List.of(Integer.valueOf(3), Integer.valueOf(5), Integer.valueOf(8), Integer.valueOf(10)));
/* 263 */       s.options.respawnDelaySeconds = cycle(list, s.options.respawnDelaySeconds, dir); }
/* 264 */     else if (slot == 42)
/* 265 */     { List<Integer> list = this.plugin.getSettings().getIntList("private-match.time-limit-options", List.of(Integer.valueOf(0), Integer.valueOf(10), Integer.valueOf(15), Integer.valueOf(20)));
/* 266 */       s.options.timeLimitMinutes = cycle(list, s.options.timeLimitMinutes, dir); }
/* 267 */     else if (slot == 39)
/* 268 */     { s.options.bedsEnabled = !s.options.bedsEnabled; }
/* 269 */     else if (slot == 40)
/* 270 */     { s.options.fallDamage = !s.options.fallDamage; }
/* 271 */     else if (slot == 41)
/* 272 */     { s.options.allowSpectators = !s.options.allowSpectators; }
/* 273 */     else if (slot == 53)
/* 274 */     { s.options = MatchOptions.defaults(this.plugin.getSettings()); }
/* 275 */     else { if (slot == 45) {
/* 276 */         p.closeInventory(); return;
/*     */       } 
/* 278 */       if (slot == 49) {
/* 279 */         start(p, s);
/*     */         return;
/*     */       } 
/* 282 */       changed = false; }
/*     */     
/* 284 */     if (changed) {
/* 285 */       render(p, e.getInventory(), s);
/*     */     }
/*     */   }
/*     */   
/*     */   private void start(Player p, Session s) {
/* 290 */     Arena arena = null;
/* 291 */     if (s.mapIndex >= 0) {
/* 292 */       arena = this.plugin.getArenaManager().get(s.maps.get(s.mapIndex));
/* 293 */       if (arena == null) {
/* 294 */         p.closeInventory();
/* 295 */         this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "That arena no longer exists.");
/*     */         return;
/*     */       } 
/*     */     } 
/* 299 */     if (this.plugin.getGameManager().isBusy(p)) {
/* 300 */       p.closeInventory();
/* 301 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "You are already in a queue or match.");
/*     */       return;
/*     */     } 
/* 304 */     String error = this.plugin.getGameManager().startPrivateMatch(p, arena, s.mode, s.options.copy());
/* 305 */     if (error != null) {
/* 306 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*     */       return;
/*     */     } 
/* 309 */     p.closeInventory();
/*     */   }
/*     */   
/*     */   private static int cycle(List<Integer> values, int current, int dir) {
/* 313 */     int idx = values.indexOf(Integer.valueOf(current));
/* 314 */     if (idx < 0) {
/* 315 */       return ((Integer)values.get(0)).intValue();
/*     */     }
/* 317 */     return ((Integer)values.get(Math.floorMod(idx + dir, values.size()))).intValue();
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onDrag(InventoryDragEvent e) {
/* 322 */     if (e.getInventory().getHolder() instanceof Holder)
/* 323 */       e.setCancelled(true); 
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\gui\PrivateMatchGui.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */