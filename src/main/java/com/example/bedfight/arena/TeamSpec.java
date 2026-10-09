/*    */ package com.example.bedfight.arena;
/*    */ 
/*    */ import org.bukkit.DyeColor;
/*    */ 
/*    */ public final class TeamSpec {
/*    */   private final DyeColor color;
/*    */   private Pos spawn;
/*    */   private Pos bed;
/*    */   private Pos waitSpawn;
/*    */   
/*    */   public TeamSpec(DyeColor color) {
/* 12 */     this.color = color;
/*    */   }
/*    */   
/* 15 */   public DyeColor getColor() { return this.color; }
/* 16 */   public Pos getSpawn() { return this.spawn; }
/* 17 */   public void setSpawn(Pos spawn) { this.spawn = spawn; }
/* 18 */   public Pos getBed() { return this.bed; } public void setBed(Pos bed) {
/* 19 */     this.bed = bed;
/*    */   }
/* 21 */   public Pos getWaitSpawn() { return this.waitSpawn; } public void setWaitSpawn(Pos waitSpawn) {
/* 22 */     this.waitSpawn = waitSpawn;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\arena\TeamSpec.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */