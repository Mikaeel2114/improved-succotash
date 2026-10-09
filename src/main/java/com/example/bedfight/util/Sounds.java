/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import com.example.bedfight.BedFightPlugin;
/*    */ import java.util.List;
/*    */ import java.util.Locale;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public final class Sounds {
/*    */   public static void play(BedFightPlugin plugin, Player p, String settingsKey) {
/* 13 */     if (p == null || !p.isOnline()) {
/*    */       return;
/*    */     }
/* 16 */     Voices voices = plugin.getVoices();
/* 17 */     if (voices != null && !voices.enabled()) {
/*    */       return;
/*    */     }
/* 20 */     List<String> entries = (voices == null) ? null : voices.list(settingsKey);
/* 21 */     if (entries == null)
/*    */     {
/* 23 */       entries = plugin.getSettings().getStringList("sounds." + settingsKey);
/*    */     }
/* 25 */     float mult = (voices == null) ? 1.0F : (float)voices.volumeMultiplier();
/* 26 */     for (String entry : entries) {
/* 27 */       String[] parts = entry.trim().split(":");
/* 28 */       Sound sound = parse(parts[0]);
/* 29 */       if (sound == null) {
/*    */         continue;
/*    */       }
/* 32 */       float volume = number(parts, 1, 1.0F) * mult;
/* 33 */       float pitch = number(parts, 2, 1.0F);
/* 34 */       long delay = (long)number(parts, 3, 0.0F);
/* 35 */       if (delay <= 0L) {
/* 36 */         p.playSound(p.getLocation(), sound, volume, pitch); continue;
/*    */       } 
/* 38 */       Bukkit.getScheduler().runTaskLater((Plugin)plugin, () -> { if (p.isOnline()) p.playSound(p.getLocation(), sound, volume, pitch);  }, delay);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static Sound parse(String name) {
/*    */     try {
/* 49 */       return Sound.valueOf(name.trim().toUpperCase(Locale.ROOT));
/* 50 */     } catch (IllegalArgumentException ex) {
/* 51 */       Bukkit.getLogger().warning("[BedFight] Unknown sound in voices.yml: " + name);
/* 52 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static float number(String[] parts, int idx, float def) {
/* 57 */     if (parts.length <= idx) {
/* 58 */       return def;
/*    */     }
/*    */     try {
/* 61 */       return Float.parseFloat(parts[idx].trim());
/* 62 */     } catch (NumberFormatException ex) {
/* 63 */       return def;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Sounds.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */