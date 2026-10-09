/*     */ package com.example.bedfight.kit;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.util.YamlFiles;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.DyeColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.enchantments.Enchantment;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemFlag;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.PlayerInventory;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.inventory.meta.LeatherArmorMeta;
/*     */ 
/*     */ public final class KitManager
/*     */ {
/*     */   private static final int KIT_VERSION = 2;
/*  28 */   private static final String[] ARMOR_KEYS = new String[] { "helmet", "chestplate", "leggings", "boots" };
/*     */   private final BedFightPlugin plugin;
/*     */   private final File file;
/*  31 */   private final Map<Integer, ItemStack> items = new TreeMap<>();
/*  32 */   private final ItemStack[] armor = new ItemStack[4];
/*     */   
/*     */   private final File playerDir;
/*  35 */   private final Map<UUID, Map<Integer, ItemStack>> personal = new HashMap<>();
/*     */   
/*     */   public KitManager(BedFightPlugin plugin) {
/*  38 */     this.plugin = plugin;
/*  39 */     this.file = new File(plugin.getDataFolder(), "kit.yml");
/*  40 */     this.playerDir = new File(plugin.getDataFolder(), "playerkits");
/*  41 */     load();
/*     */   }
/*     */   
/*     */   public void load() {
/*  45 */     this.personal.clear();
/*  46 */     if (!this.file.isFile()) {
/*  47 */       resetToDefault();
/*     */       return;
/*     */     } 
/*  50 */     this.items.clear();
/*  51 */     for (int i = 0; i < 4; i++) {
/*  52 */       this.armor[i] = null;
/*     */     }
/*  54 */     YamlConfiguration y = YamlFiles.load(this.file);
/*  55 */     boolean outdated = (y.getInt("version", 1) < 2);
/*  56 */     ConfigurationSection slots = y.getConfigurationSection("slots");
/*  57 */     if (slots != null) {
/*  58 */       for (String key : slots.getKeys(false)) {
/*     */         try {
/*  60 */           int slot = Integer.parseInt(key);
/*  61 */           ItemStack it = slots.getItemStack(key);
/*  62 */           if (it != null && slot >= 0 && slot < 36) {
/*  63 */             this.items.put(Integer.valueOf(slot), it);
/*     */           }
/*  65 */         } catch (NumberFormatException numberFormatException) {}
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  70 */     for (int j = 0; j < 4; j++) {
/*  71 */       this.armor[j] = y.getItemStack("armor." + ARMOR_KEYS[j]);
/*     */     }
/*  73 */     if (outdated) {
/*     */       
/*  75 */       this.plugin.getLogger().info("kit.yml is from an older version: your kit was kept. Use /adminkiteditor reset to get the new default kit.");
/*     */       
/*  77 */       save();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resetToDefault() {
/*  82 */     this.personal.clear();
/*  83 */     this.items.clear();
/*  84 */     this.items.put(Integer.valueOf(0), tool(Material.WOOD_SWORD, 0));
/*  85 */     this.items.put(Integer.valueOf(1), new ItemStack(Material.WOOL, 64));
/*  86 */     this.items.put(Integer.valueOf(2), tool(Material.SHEARS, 0));
/*  87 */     this.items.put(Integer.valueOf(3), tool(Material.WOOD_PICKAXE, 1));
/*  88 */     this.items.put(Integer.valueOf(4), tool(Material.WOOD_AXE, 1));
/*  89 */     this.armor[0] = new ItemStack(Material.LEATHER_HELMET);
/*  90 */     this.armor[1] = new ItemStack(Material.LEATHER_CHESTPLATE);
/*  91 */     this.armor[2] = new ItemStack(Material.LEATHER_LEGGINGS);
/*  92 */     this.armor[3] = new ItemStack(Material.LEATHER_BOOTS);
/*  93 */     save();
/*     */   }
/*     */ 
/*     */   
/*     */   private static ItemStack tool(Material m, int efficiency) {
/*  98 */     ItemStack it = new ItemStack(m);
/*  99 */     ItemMeta meta = it.getItemMeta();
/* 100 */     meta.spigot().setUnbreakable(true);
/* 101 */     meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS });
/* 102 */     if (efficiency > 0) {
/* 103 */       meta.addEnchant(Enchantment.DIG_SPEED, efficiency, true);
/*     */     }
/* 105 */     it.setItemMeta(meta);
/* 106 */     return it;
/*     */   }
/*     */   
/* 109 */   public Map<Integer, ItemStack> getItems() { return this.items; } public ItemStack[] getArmor() {
/* 110 */     return this.armor;
/*     */   }
/*     */   public void set(Map<Integer, ItemStack> newItems, ItemStack[] newArmor) {
/* 113 */     this.personal.clear();
/* 114 */     this.items.clear();
/* 115 */     for (Map.Entry<Integer, ItemStack> e : newItems.entrySet()) {
/* 116 */       this.items.put(e.getKey(), ((ItemStack)e.getValue()).clone());
/*     */     }
/* 118 */     for (int i = 0; i < 4; i++) {
/* 119 */       this.armor[i] = (newArmor[i] == null) ? null : newArmor[i].clone();
/*     */     }
/* 121 */     save();
/*     */   }
/*     */   
/*     */   private void save() {
/* 125 */     YamlConfiguration y = new YamlConfiguration();
/* 126 */     y.set("version", Integer.valueOf(2));
/* 127 */     for (Map.Entry<Integer, ItemStack> e : this.items.entrySet()) {
/* 128 */       y.set("slots." + String.valueOf(e.getKey()), e.getValue());
/*     */     }
/* 130 */     for (int i = 0; i < 4; i++) {
/* 131 */       if (this.armor[i] != null) {
/* 132 */         y.set("armor." + ARMOR_KEYS[i], this.armor[i]);
/*     */       }
/*     */     } 
/*     */     try {
/* 136 */       YamlFiles.save(y, this.file);
/* 137 */     } catch (IOException ex) {
/* 138 */       this.plugin.getLogger().log(Level.SEVERE, "Could not save kit.yml", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<ItemStack, Integer> totals(Collection<ItemStack> list) {
/* 146 */     Map<ItemStack, Integer> m = new HashMap<>();
/* 147 */     for (ItemStack it : list) {
/* 148 */       if (it == null || it.getType() == Material.AIR) {
/*     */         continue;
/*     */       }
/* 151 */       ItemStack key = it.clone();
/* 152 */       key.setAmount(1);
/* 153 */       m.merge(key, Integer.valueOf(it.getAmount()), Integer::sum);
/*     */     } 
/* 155 */     return m;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValidLayout(Map<Integer, ItemStack> layout) {
/* 160 */     for (Map.Entry<Integer, ItemStack> e : layout.entrySet()) {
/* 161 */       if (((Integer)e.getKey()).intValue() < 0 || ((Integer)e.getKey()).intValue() > 35) {
/* 162 */         return false;
/*     */       }
/*     */     } 
/* 165 */     return totals(layout.values()).equals(totals(this.items.values()));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<Integer, ItemStack> personalLayout(UUID id) {
/* 170 */     if (this.personal.containsKey(id)) {
/* 171 */       return this.personal.get(id);
/*     */     }
/* 173 */     Map<Integer, ItemStack> layout = null;
/* 174 */     File f = new File(this.playerDir, String.valueOf(id) + ".yml");
/* 175 */     if (f.isFile()) {
/*     */       try {
/* 177 */         YamlConfiguration y = YamlFiles.load(f);
/* 178 */         ConfigurationSection slots = y.getConfigurationSection("slots");
/* 179 */         Map<Integer, ItemStack> loaded = new TreeMap<>();
/* 180 */         if (slots != null) {
/* 181 */           for (String key : slots.getKeys(false)) {
/* 182 */             ItemStack it = slots.getItemStack(key);
/* 183 */             if (it != null) {
/* 184 */               loaded.put(Integer.valueOf(Integer.parseInt(key)), it);
/*     */             }
/*     */           } 
/*     */         }
/*     */         
/* 189 */         layout = isValidLayout(loaded) ? loaded : null;
/* 190 */       } catch (RuntimeException ex) {
/* 191 */         layout = null;
/*     */       } 
/*     */     }
/* 194 */     this.personal.put(id, layout);
/* 195 */     return layout;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean savePersonal(UUID id, Map<Integer, ItemStack> layout) {
/* 200 */     if (!isValidLayout(layout)) {
/* 201 */       return false;
/*     */     }
/* 203 */     YamlConfiguration y = new YamlConfiguration();
/* 204 */     for (Map.Entry<Integer, ItemStack> e : layout.entrySet()) {
/* 205 */       y.set("slots." + String.valueOf(e.getKey()), e.getValue());
/*     */     }
/*     */     try {
/* 208 */       YamlFiles.save(y, new File(this.playerDir, String.valueOf(id) + ".yml"));
/* 209 */     } catch (IOException ex) {
/* 210 */       this.plugin.getLogger().log(Level.SEVERE, "Could not save personal kit of " + String.valueOf(id), ex);
/* 211 */       return false;
/*     */     } 
/* 213 */     Map<Integer, ItemStack> copy = new TreeMap<>();
/* 214 */     for (Map.Entry<Integer, ItemStack> e : layout.entrySet()) {
/* 215 */       copy.put(e.getKey(), ((ItemStack)e.getValue()).clone());
/*     */     }
/* 217 */     this.personal.put(id, copy);
/* 218 */     return true;
/*     */   }
/*     */   
/*     */   public void resetPersonal(UUID id) {
/* 222 */     this.personal.remove(id);
/* 223 */     File f = new File(this.playerDir, String.valueOf(id) + ".yml");
/* 224 */     if (f.isFile() && !f.delete()) {
/* 225 */       this.plugin.getLogger().warning("Could not delete " + f.getName());
/*     */     }
/* 227 */     this.personal.put(id, null);
/*     */   }
/*     */   
/*     */   public void apply(Player p, DyeColor color) {
/* 231 */     PlayerInventory inv = p.getInventory();
/* 232 */     inv.clear();
/* 233 */     inv.setArmorContents(new ItemStack[4]);
/* 234 */     Map<Integer, ItemStack> own = personalLayout(p.getUniqueId());
/* 235 */     for (Map.Entry<Integer, ItemStack> e : ((own != null) ? own : this.items).entrySet()) {
/* 236 */       inv.setItem(((Integer)e.getKey()).intValue(), tint(e.getValue(), color));
/*     */     }
/* 238 */     inv.setHelmet((this.armor[0] == null) ? null : tint(this.armor[0], color));
/* 239 */     inv.setChestplate((this.armor[1] == null) ? null : tint(this.armor[1], color));
/* 240 */     inv.setLeggings((this.armor[2] == null) ? null : tint(this.armor[2], color));
/* 241 */     inv.setBoots((this.armor[3] == null) ? null : tint(this.armor[3], color));
/* 242 */     inv.setHeldItemSlot(0);
/*     */   }
/*     */   
/*     */   private static ItemStack tint(ItemStack src, DyeColor color) {
/* 246 */     ItemStack it = src.clone();
/* 247 */     Material m = it.getType();
/* 248 */     if (m == Material.WOOL || m == Material.STAINED_CLAY || m == Material.STAINED_GLASS || m == Material.STAINED_GLASS_PANE || m == Material.CARPET) {
/*     */       
/* 250 */       it.setDurability((short)color.getWoolData());
/* 251 */     } else if (m.name().startsWith("LEATHER_")) {
/* 252 */       ItemMeta meta = it.getItemMeta();
/* 253 */       if (meta instanceof LeatherArmorMeta) { LeatherArmorMeta lm = (LeatherArmorMeta)meta;
/* 254 */         lm.setColor(color.getColor());
/* 255 */         it.setItemMeta((ItemMeta)lm); }
/*     */     
/*     */     } 
/* 258 */     return it;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\kit\KitManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */