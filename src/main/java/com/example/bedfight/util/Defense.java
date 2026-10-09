/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import com.example.bedfight.BedFightPlugin;
/*    */ import java.util.List;
/*    */ import java.util.Locale;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.block.Block;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Defense
/*    */ {
/* 20 */   private static final List<String> DEFAULTS = List.of("ENDER_STONE", "WOOD", "WOOL", "STAINED_CLAY", "HARD_CLAY", "GLASS", "STAINED_GLASS", "OBSIDIAN", "LADDER");
/*    */ 
/*    */   
/*    */   public static boolean isBreakable(BedFightPlugin plugin, Block b) {
/* 24 */     Material t = b.getType();
/* 25 */     if (t == Material.AIR || t == Material.BED_BLOCK || t == Material.BEDROCK || t == Material.BARRIER) {
/* 26 */       return false;
/*    */     }
/* 28 */     Settings st = plugin.getSettings();
/* 29 */     List<String> list = st.contains("breakable-blocks") ? st.getStringList("breakable-blocks") : DEFAULTS;
/* 30 */     for (String m : list) {
/* 31 */       if (matches(m, t)) {
/* 32 */         return true;
/*    */       }
/*    */     } 
/* 35 */     return false;
/*    */   }
/*    */ 
/*    */   
/* 39 */   private static final List<String> DROP_DEFAULTS = List.of("WOOL", "WOOD", "ENDER_STONE", "STAINED_CLAY", "HARD_CLAY");
/*    */ 
/*    */   
/*    */   public static boolean dropsItems(BedFightPlugin plugin, Block b) {
/* 43 */     Material t = b.getType();
/* 44 */     if (t == Material.AIR || t == Material.BED_BLOCK) {
/* 45 */       return false;
/*    */     }
/* 47 */     Settings st = plugin.getSettings();
/* 48 */     List<String> list = st.contains("drop-blocks") ? st.getStringList("drop-blocks") : DROP_DEFAULTS;
/* 49 */     for (String m : list) {
/* 50 */       if (matches(m, t)) {
/* 51 */         return true;
/*    */       }
/*    */     } 
/* 54 */     return false;
/*    */   }
/*    */   
/*    */   private static boolean matches(String configured, Material t) {
/* 58 */     String c = configured.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
/* 59 */     String n = t.name();
/* 60 */     if (c.equals(n)) {
/* 61 */       return true;
/*    */     }
/* 63 */     switch (c) {
/*    */       case "END_STONE":
/*    */       case "ENDSTONE":
/* 66 */         return n.equals("ENDER_STONE");
/*    */       case "PLANKS":
/*    */       case "WOODEN_PLANKS":
/*    */       case "WOOD_PLANKS":
/*    */       case "OAK_PLANKS":
/* 71 */         return n.equals("WOOD");
/*    */       case "TERRACOTTA":
/*    */       case "CLAY":
/* 74 */         return (n.equals("STAINED_CLAY") || n.equals("HARD_CLAY"));
/*    */       case "GLASS_PANE":
/* 76 */         return (n.equals("THIN_GLASS") || n.equals("STAINED_GLASS_PANE"));
/*    */     } 
/* 78 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Defense.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */