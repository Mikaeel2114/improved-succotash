/*    */ package com.example.bedfight.arena;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.DyeColor;
/*    */ 
/*    */ 
/*    */ public final class Teams
/*    */ {
/*    */   public static DyeColor parse(String input) {
/* 11 */     if (input == null) {
/* 12 */       return null;
/*    */     }
/* 14 */     String s = input.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
/* 15 */     if (s.equals("LIGHTBLUE") || s.equals("AQUA")) {
/* 16 */       s = "LIGHT_BLUE";
/*    */     }
/*    */     try {
/* 19 */       return DyeColor.valueOf(s);
/* 20 */     } catch (IllegalArgumentException ex) {
/* 21 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static ChatColor chat(DyeColor c) {
/* 26 */     switch (c) { case WHITE:
/* 27 */         return ChatColor.WHITE;
/* 28 */       case ORANGE: return ChatColor.GOLD;
/* 29 */       case MAGENTA: return ChatColor.LIGHT_PURPLE;
/* 30 */       case LIGHT_BLUE: return ChatColor.AQUA;
/* 31 */       case YELLOW: return ChatColor.YELLOW;
/* 32 */       case LIME: return ChatColor.GREEN;
/* 33 */       case PINK: return ChatColor.LIGHT_PURPLE;
/* 34 */       case GRAY: return ChatColor.DARK_GRAY;
/* 35 */       case SILVER: return ChatColor.GRAY;
/* 36 */       case CYAN: return ChatColor.DARK_AQUA;
/* 37 */       case PURPLE: return ChatColor.DARK_PURPLE;
/* 38 */       case BLUE: return ChatColor.BLUE;
/* 39 */       case BROWN: return ChatColor.GOLD;
/* 40 */       case GREEN: return ChatColor.DARK_GREEN;
/* 41 */       case RED: return ChatColor.RED;
/* 42 */       case BLACK: return ChatColor.BLACK; }
/* 43 */      return ChatColor.WHITE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static String name(DyeColor c) {
/* 49 */     String n = c.name().toLowerCase(Locale.ROOT).replace('_', ' ');
/* 50 */     return "" + Character.toUpperCase(n.charAt(0)) + Character.toUpperCase(n.charAt(0));
/*    */   }
/*    */ 
/*    */   
/*    */   public static String display(DyeColor c) {
/* 55 */     return String.valueOf(chat(c)) + String.valueOf(chat(c));
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\arena\Teams.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */