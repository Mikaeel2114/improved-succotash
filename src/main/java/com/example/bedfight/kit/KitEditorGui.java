/*     */ package com.example.bedfight.kit;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import java.util.Arrays;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.inventory.InventoryAction;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryCloseEvent;
/*     */ import org.bukkit.event.inventory.InventoryDragEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ 
/*     */ public final class KitEditorGui
/*     */   implements Listener
/*     */ {
/*  31 */   private static final String TITLE = String.valueOf(ChatColor.DARK_GRAY) + "BedFight kit editor";
/*  32 */   private static final String PLAYER_TITLE = String.valueOf(ChatColor.DARK_GRAY) + "Your BedFight kit";
/*     */   
/*     */   private static final int LOCKED_FROM = 40;
/*     */   private static final int RESET_SLOT = 53;
/*  36 */   private static final Set<InventoryAction> PLAYER_ACTIONS = EnumSet.of(InventoryAction.PICKUP_ALL, new InventoryAction[] { InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_ONE, InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE, InventoryAction.SWAP_WITH_CURSOR });
/*     */   
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   private static final class Holder
/*     */     implements InventoryHolder
/*     */   {
/*     */     final boolean admin;
/*     */     Inventory inventory;
/*     */     
/*     */     Holder(boolean admin) {
/*  47 */       this.admin = admin;
/*     */     }
/*     */ 
/*     */     
/*     */     public Inventory getInventory() {
/*  52 */       return this.inventory;
/*     */     }
/*     */   }
/*     */   
/*     */   public KitEditorGui(BedFightPlugin plugin) {
/*  57 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public void open(Player p) {
/*  62 */     Holder holder = new Holder(true);
/*  63 */     Inventory inv = Bukkit.createInventory(holder, 54, TITLE);
/*  64 */     holder.inventory = inv;
/*  65 */     KitManager kit = this.plugin.getKitManager();
/*  66 */     fill(inv, kit.getItems());
/*  67 */     ItemStack[] armor = kit.getArmor();
/*  68 */     for (int i = 0; i < 4; i++) {
/*  69 */       if (armor[i] != null) {
/*  70 */         inv.setItem(36 + i, armor[i].clone());
/*     */       }
/*     */     } 
/*  73 */     ItemStack filler = named(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7), " ", new String[0]);
/*  74 */     for (int j = 40; j < 54; j++) {
/*  75 */       inv.setItem(j, filler);
/*     */     }
/*  77 */     inv.setItem(49, named(new ItemStack(Material.PAPER), String.valueOf(ChatColor.YELLOW) + "How it works", new String[] { String.valueOf(ChatColor.GRAY) + "Rows 1-3: inventory, row 4: hotbar", String.valueOf(ChatColor.GRAY) + "Row 5, first 4 slots: armor", String.valueOf(ChatColor.GRAY) + "Close the window to save the kit.", String.valueOf(ChatColor.GRAY) + "Wool, stained clay/glass and leather", String.valueOf(ChatColor.GRAY) + "armor take the team color." }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     p.openInventory(inv);
/*  84 */     this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GRAY) + "Arrange the kit and close the window to save it.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void openPlayer(Player p) {
/*  89 */     Holder holder = new Holder(false);
/*  90 */     Inventory inv = Bukkit.createInventory(holder, 54, PLAYER_TITLE);
/*  91 */     holder.inventory = inv;
/*  92 */     KitManager kit = this.plugin.getKitManager();
/*  93 */     Map<Integer, ItemStack> own = kit.personalLayout(p.getUniqueId());
/*  94 */     fill(inv, (own != null) ? own : kit.getItems());
/*  95 */     ItemStack[] armor = kit.getArmor();
/*  96 */     for (int i = 0; i < 4; i++) {
/*  97 */       if (armor[i] != null) {
/*  98 */         inv.setItem(36 + i, armor[i].clone());
/*     */       }
/*     */     } 
/* 101 */     ItemStack filler = named(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7), " ", new String[0]);
/* 102 */     for (int j = 36; j < 54; j++) {
/* 103 */       if (inv.getItem(j) == null) {
/* 104 */         inv.setItem(j, filler);
/*     */       }
/*     */     } 
/* 107 */     inv.setItem(49, named(new ItemStack(Material.PAPER), String.valueOf(ChatColor.YELLOW) + "How it works", new String[] { String.valueOf(ChatColor.GRAY) + "Move your items to the slots you like.", String.valueOf(ChatColor.GRAY) + "Rows 1-3: inventory, row 4: hotbar", String.valueOf(ChatColor.GRAY) + "Close the window to save." }));
/*     */ 
/*     */ 
/*     */     
/* 111 */     inv.setItem(53, named(new ItemStack(Material.BARRIER), String.valueOf(ChatColor.RED) + "Reset to default", new String[] { String.valueOf(ChatColor.GRAY) + "Click to restore the default layout." }));
/*     */     
/* 113 */     p.openInventory(inv);
/*     */   }
/*     */   
/*     */   private static void fill(Inventory inv, Map<Integer, ItemStack> layout) {
/* 117 */     for (int i = 0; i < 36; i++) {
/* 118 */       inv.setItem(i, null);
/*     */     }
/* 120 */     for (Map.Entry<Integer, ItemStack> e : layout.entrySet()) {
/* 121 */       int slot = ((Integer)e.getKey()).intValue();
/* 122 */       inv.setItem((slot >= 9) ? (slot - 9) : (27 + slot), ((ItemStack)e.getValue()).clone());
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Map<Integer, ItemStack> read(Inventory inv) {
/* 127 */     Map<Integer, ItemStack> items = new TreeMap<>();
/* 128 */     for (int i = 0; i < 36; i++) {
/* 129 */       ItemStack it = inv.getItem(i);
/* 130 */       if (it != null && it.getType() != Material.AIR) {
/* 131 */         items.put(Integer.valueOf((i < 27) ? (i + 9) : (i - 27)), it.clone());
/*     */       }
/*     */     } 
/* 134 */     return items;
/*     */   }
/*     */   @EventHandler
/*     */   public void onClick(InventoryClickEvent e) {
/*     */     Holder h;
/* 139 */     InventoryHolder inventoryHolder = e.getInventory().getHolder(); if (inventoryHolder instanceof Holder) { h = (Holder)inventoryHolder; }
/*     */     else
/*     */     { return; }
/* 142 */      int raw = e.getRawSlot();
/* 143 */     if (h.admin) {
/* 144 */       if (raw >= 40 && raw < 54 && (raw < 36 || raw >= 40)) {
/* 145 */         e.setCancelled(true);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 150 */     if (raw == 53) {
/* 151 */       e.setCancelled(true);
/* 152 */       fill(e.getInventory(), this.plugin.getKitManager().getItems());
/* 153 */       e.getView().setCursor(null);
/*     */       return;
/*     */     } 
/* 156 */     if (raw < 0 || raw >= 36 || !PLAYER_ACTIONS.contains(e.getAction()))
/* 157 */       e.setCancelled(true); 
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onDrag(InventoryDragEvent e) {
/*     */     Holder h;
/* 163 */     InventoryHolder inventoryHolder = e.getInventory().getHolder(); if (inventoryHolder instanceof Holder) { h = (Holder)inventoryHolder; }
/*     */     else
/*     */     { return; }
/* 166 */      int limit = h.admin ? 40 : 36;
/* 167 */     for (Iterator<Integer> iterator = e.getRawSlots().iterator(); iterator.hasNext(); ) { int raw = ((Integer)iterator.next()).intValue();
/* 168 */       if (raw >= limit) {
/* 169 */         e.setCancelled(true);
/*     */         return;
/*     */       }  }
/*     */   
/*     */   } @EventHandler
/*     */   public void onClose(InventoryCloseEvent e) {
/*     */     Holder h;
/*     */     Player p;
/* 177 */     InventoryHolder inventoryHolder = e.getInventory().getHolder(); if (inventoryHolder instanceof Holder) { h = (Holder)inventoryHolder; HumanEntity humanEntity = e.getPlayer(); if (humanEntity instanceof Player) { p = (Player)humanEntity; } else { return; }
/*     */        }
/*     */     else { return; }
/* 180 */      Inventory inv = e.getInventory();
/* 181 */     Map<Integer, ItemStack> items = read(inv);
/* 182 */     if (h.admin) {
/* 183 */       ItemStack[] armor = new ItemStack[4];
/* 184 */       for (int j = 0; j < 4; j++) {
/* 185 */         ItemStack it = inv.getItem(36 + j);
/* 186 */         armor[j] = (it == null || it.getType() == Material.AIR) ? null : it.clone();
/*     */       } 
/* 188 */       this.plugin.getKitManager().set(items, armor);
/* 189 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Kit saved (" + String.valueOf(ChatColor.GREEN) + " item stacks). It is given at match start and on respawn.");
/*     */       
/*     */       return;
/*     */     } 
/* 193 */     ItemStack cursor = e.getView().getCursor();
/* 194 */     if (cursor != null && cursor.getType() != Material.AIR) {
/*     */       
/* 196 */       e.getView().setCursor(null);
/* 197 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Your kit was not saved: put every item down inside the window before closing it.");
/*     */       return;
/*     */     } 
/* 200 */     if (this.plugin.getKitManager().savePersonal(p.getUniqueId(), items)) {
/* 201 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.GREEN) + "Your kit layout was saved. It is used at match start and on respawn.");
/*     */     } else {
/* 203 */       this.plugin.msg((CommandSender)p, String.valueOf(ChatColor.RED) + "Your kit was not saved: you can only move the kit's items around.");
/*     */     } 
/*     */   }
/*     */   
/*     */   private static ItemStack named(ItemStack it, String name, String... lore) {
/* 208 */     ItemMeta meta = it.getItemMeta();
/* 209 */     meta.setDisplayName(name);
/* 210 */     if (lore.length > 0) {
/* 211 */       meta.setLore(Arrays.asList(lore));
/*     */     }
/* 213 */     it.setItemMeta(meta);
/* 214 */     return it;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\kit\KitEditorGui.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */