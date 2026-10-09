/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import com.example.bedfight.BedFightPlugin;
/*    */ import java.io.File;
/*    */ import java.util.List;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public final class Settings {
/*    */   private final BedFightPlugin plugin;
/*    */   private final File file;
/*    */   private YamlConfiguration cfg;
/*    */   
/*    */   public Settings(BedFightPlugin plugin) {
/* 15 */     this.plugin = plugin;
/* 16 */     this.file = new File(plugin.getDataFolder(), "settings.yml");
/* 17 */     reload();
/*    */   }
/*    */   
/*    */   public void reload() {
/* 21 */     ConfigMerger.merge((JavaPlugin)this.plugin, "settings.yml");
/* 22 */     this.cfg = YamlFiles.load(this.file);
/*    */   }
/*    */   
/* 25 */   public int getInt(String path, int def) { return this.cfg.getInt(path, def); }
/* 26 */   public long getLong(String path, long def) { return this.cfg.getLong(path, def); }
/* 27 */   public double getDouble(String path, double def) { return this.cfg.getDouble(path, def); }
/* 28 */   public boolean getBoolean(String path, boolean def) { return this.cfg.getBoolean(path, def); }
/* 29 */   public String getString(String path, String def) { return this.cfg.getString(path, def); }
/* 30 */   public boolean contains(String path) { return this.cfg.contains(path); } public List<String> getStringList(String path) {
/* 31 */     return this.cfg.getStringList(path);
/*    */   } public List<Integer> getIntList(String path, List<Integer> def) {
/* 33 */     List<Integer> l = this.cfg.getIntegerList(path);
/* 34 */     return l.isEmpty() ? def : l;
/*    */   } public String message(String key, String def) {
/* 36 */     return Text.color(this.cfg.getString("messages." + key, def));
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Settings.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */