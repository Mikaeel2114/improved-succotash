/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import com.example.bedfight.BedFightPlugin;
/*    */ import java.io.File;
/*    */ import java.util.List;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public final class Voices {
/*    */   private final BedFightPlugin plugin;
/*    */   private final File file;
/*    */   private YamlConfiguration cfg;
/*    */   
/*    */   public Voices(BedFightPlugin plugin) {
/* 15 */     this.plugin = plugin;
/* 16 */     this.file = new File(plugin.getDataFolder(), "voices.yml");
/* 17 */     reload();
/*    */   }
/*    */   
/*    */   public void reload() {
/* 21 */     ConfigMerger.merge((JavaPlugin)this.plugin, "voices.yml");
/* 22 */     this.cfg = YamlFiles.load(this.file);
/*    */   }
/*    */   public boolean enabled() {
/* 25 */     return this.cfg.getBoolean("enabled", true);
/*    */   } public double volumeMultiplier() {
/* 27 */     return Math.max(0.0D, this.cfg.getDouble("volume-multiplier", 1.0D));
/*    */   }
/*    */   
/*    */   public List<String> list(String key) {
/* 31 */     String path = "sounds." + key;
/* 32 */     return this.cfg.contains(path) ? this.cfg.getStringList(path) : null;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Voices.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */