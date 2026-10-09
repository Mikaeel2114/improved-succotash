/*     */ package com.example.bedfight.spectate;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.game.Match;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.SkullMeta;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SpectatorManager
/*     */ {
/*     */   public static final int TELEPORTER_SLOT = 0;
/*     */   public static final int LEAVE_SLOT = 8;
/*  33 */   public static final String TELEPORTER_TITLE = String.valueOf(ChatColor.DARK_GRAY) + "Teleporter";
/*     */   
/*     */   private final BedFightPlugin plugin;
/*  36 */   private final Map<UUID, Match> states = new HashMap<>();
/*     */   
/*     */   public SpectatorManager(BedFightPlugin plugin) {
/*  39 */     this.plugin = plugin;
/*     */   }
/*     */   
/*  42 */   public boolean is(Player p) { return this.states.containsKey(p.getUniqueId()); } public Match matchOf(Player p) {
/*  43 */     return this.states.get(p.getUniqueId());
/*     */   }
/*     */   
/*     */   public void enable(Player p, Match match, boolean items) {
/*  47 */     this.states.put(p.getUniqueId(), match);
/*     */     
/*  49 */     p.getInventory().clear();
/*  50 */     p.getInventory().setArmorContents(new ItemStack[4]);
/*  51 */     for (PotionEffect pe : new ArrayList(p.getActivePotionEffects())) {
/*  52 */       p.removePotionEffect(pe.getType());
/*     */     }
/*  54 */     p.setGameMode(GameMode.ADVENTURE);
/*  55 */     p.setHealth(p.getMaxHealth());
/*  56 */     p.setFoodLevel(20);
/*  57 */     p.setSaturation(20.0F);
/*  58 */     p.setFireTicks(0);
/*  59 */     p.setFallDistance(0.0F);
/*  60 */     p.setAllowFlight(true);
/*  61 */     p.setFlying(true);
/*  62 */     p.setFlySpeed((float)Math.max(0.01D, Math.min(1.0D, this.plugin.getSettings().getDouble("spectator.fly-speed", 0.1D))));
/*  63 */     setCollides(p, false);
/*     */ 
/*     */     
/*  66 */     for (Player other : match.viewers()) {
/*  67 */       if (other.equals(p)) {
/*     */         continue;
/*     */       }
/*  70 */       if (is(other)) {
/*  71 */         p.showPlayer(other);
/*  72 */         other.showPlayer(p); continue;
/*     */       } 
/*  74 */       other.hidePlayer(p);
/*     */     } 
/*     */ 
/*     */     
/*  78 */     if (items) {
/*  79 */       if (this.plugin.getSettings().getBoolean("spectator.teleporter-item", true)) {
/*  80 */         p.getInventory().setItem(0, 
/*  81 */             named(Material.COMPASS, String.valueOf(ChatColor.GREEN) + "Teleporter", String.valueOf(ChatColor.GRAY) + "Right click to pick a player"));
/*     */       }
/*  83 */       if (this.plugin.getSettings().getBoolean("spectator.leave-item", true)) {
/*  84 */         p.getInventory().setItem(8, 
/*  85 */             named(Material.BED, String.valueOf(ChatColor.RED) + "Leave", String.valueOf(ChatColor.GRAY) + "Right click to leave the match"));
/*     */       }
/*  87 */       p.getInventory().setHeldItemSlot(0);
/*     */     } 
/*  89 */     p.updateInventory();
/*     */   }
/*     */ 
/*     */   
/*     */   public void disable(Player p) {
/*  94 */     if (this.states.remove(p.getUniqueId()) == null) {
/*     */       return;
/*     */     }
/*  97 */     p.setFlying(false);
/*  98 */     p.setAllowFlight(false);
/*  99 */     p.setFlySpeed(0.1F);
/* 100 */     p.setGameMode(GameMode.SURVIVAL);
/* 101 */     setCollides(p, true);
/* 102 */     for (Player other : Bukkit.getOnlinePlayers()) {
/* 103 */       other.showPlayer(p);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isTeleporterItem(ItemStack it) {
/* 108 */     return (it != null && it.getType() == Material.COMPASS && hasName(it, String.valueOf(ChatColor.GREEN) + "Teleporter"));
/*     */   }
/*     */   
/*     */   public boolean isLeaveItem(ItemStack it) {
/* 112 */     return (it != null && it.getType() == Material.BED && hasName(it, String.valueOf(ChatColor.RED) + "Leave"));
/*     */   }
/*     */   
/*     */   public static final class TeleporterHolder
/*     */     implements InventoryHolder
/*     */   {
/* 118 */     public final List<UUID> targets = new ArrayList<>();
/*     */     public Inventory inventory;
/*     */     
/*     */     public Inventory getInventory() {
/* 122 */       return this.inventory;
/*     */     } }
/*     */   
/*     */   public void openTeleporter(Player spectator) {
/* 126 */     Match m = matchOf(spectator);
/* 127 */     if (m == null) {
/*     */       return;
/*     */     }
/* 130 */     List<Player> alive = m.activePlayers();
/* 131 */     if (alive.isEmpty()) {
/* 132 */       this.plugin.msg((CommandSender)spectator, String.valueOf(ChatColor.RED) + "Nobody is alive to teleport to.");
/*     */       return;
/*     */     } 
/* 135 */     TeleporterHolder holder = new TeleporterHolder();
/* 136 */     int size = Math.min(54, Math.max(9, (alive.size() + 8) / 9 * 9));
/* 137 */     Inventory inv = Bukkit.createInventory(holder, size, TELEPORTER_TITLE);
/* 138 */     holder.inventory = inv;
/* 139 */     for (int i = 0; i < alive.size() && i < size; i++) {
/* 140 */       Player t = alive.get(i);
/* 141 */       holder.targets.add(t.getUniqueId());
/* 142 */       ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
/* 143 */       SkullMeta meta = (SkullMeta)head.getItemMeta();
/* 144 */       meta.setOwner(t.getName());
/* 145 */       meta.setDisplayName(String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.YELLOW));
/* 146 */       List<String> lore = new ArrayList<>();
/* 147 */       lore.add(String.valueOf(ChatColor.GRAY) + "Health: " + String.valueOf(ChatColor.GRAY) + String.valueOf(ChatColor.RED) + "❤");
/* 148 */       lore.add("");
/* 149 */       lore.add(String.valueOf(ChatColor.GREEN) + "Click to teleport");
/* 150 */       meta.setLore(lore);
/* 151 */       head.setItemMeta((ItemMeta)meta);
/* 152 */       inv.setItem(i, head);
/*     */     } 
/* 154 */     spectator.openInventory(inv);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ItemStack named(Material m, String name, String lore) {
/* 160 */     ItemStack it = new ItemStack(m);
/* 161 */     ItemMeta meta = it.getItemMeta();
/* 162 */     meta.setDisplayName(name);
/* 163 */     List<String> l = new ArrayList<>();
/* 164 */     l.add(lore);
/* 165 */     meta.setLore(l);
/* 166 */     it.setItemMeta(meta);
/* 167 */     return it;
/*     */   }
/*     */   
/*     */   private static boolean hasName(ItemStack it, String name) {
/* 171 */     ItemMeta meta = it.getItemMeta();
/* 172 */     return (meta != null && name.equals(meta.getDisplayName()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setCollides(Player p, boolean value) {
/*     */     try {
/* 178 */       Object spigot = p.getClass().getMethod("spigot", new Class[0]).invoke(p, new Object[0]);
/* 179 */       for (Object target : new Object[] { p, spigot }) {
/*     */         try {
/* 181 */           target.getClass().getMethod("setCollidesWithEntities", new Class[] { boolean.class }).invoke(target, new Object[] { Boolean.valueOf(value) });
/*     */           return;
/* 183 */         } catch (NoSuchMethodException noSuchMethodException) {}
/*     */       }
/*     */     
/*     */     }
/* 187 */     catch (Exception exception) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\spectate\SpectatorManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */