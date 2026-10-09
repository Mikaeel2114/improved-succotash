/*    */ package com.example.bedfight.game;
/*    */ 
/*    */ import java.util.Locale;
/*    */ 
/*    */ public enum Mode {
/*  6 */   SOLO(1, "1v1"),
/*  7 */   DUO(2, "2v2"),
/*  8 */   TRIO(3, "3v3");
/*    */   
/*    */   private final int teamSize;
/*    */   private final String label;
/*    */   
/*    */   Mode(int teamSize, String label) {
/* 14 */     this.teamSize = teamSize;
/* 15 */     this.label = label;
/*    */   }
/*    */   
/* 18 */   public int teamSize() { return this.teamSize; }
/* 19 */   public String label() { return this.label; } public int totalPlayers() {
/* 20 */     return this.teamSize * 2;
/*    */   }
/*    */   public static Mode parse(String s) {
/* 23 */     if (s == null) {
/* 24 */       return null;
/*    */     }
/* 26 */     String l = s.toLowerCase(Locale.ROOT);
/* 27 */     for (Mode m : values()) {
/* 28 */       if (m.label.equals(l)) {
/* 29 */         return m;
/*    */       }
/*    */     } 
/* 32 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\game\Mode.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */