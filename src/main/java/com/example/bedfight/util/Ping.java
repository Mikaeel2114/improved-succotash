/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ public final class Ping
/*    */ {
/*    */   public static int get(Player p) {
/*    */     try {
/* 10 */       Object handle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
/* 11 */       return Math.max(0, handle.getClass().getField("ping").getInt(handle));
/* 12 */     } catch (Exception ex) {
/* 13 */       return 0;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Ping.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */