/*    */ package com.example.bedfight.game;
/*    */ 
/*    */ import com.example.bedfight.arena.Pos;
/*    */ import java.util.HashSet;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.DyeColor;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class TeamState
/*    */ {
/*    */   final DyeColor color;
/*    */   final Pos spawn;
/*    */   final Pos bed;
/* 63 */   final Set<UUID> members = new LinkedHashSet<>();
/* 64 */   final Set<UUID> alive = new HashSet<>();
/*    */   boolean bedAlive = true;
/*    */   
/*    */   TeamState(DyeColor color, Pos spawn, Pos bed) {
/* 68 */     this.color = color;
/* 69 */     this.spawn = spawn;
/* 70 */     this.bed = bed;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\game\Match$TeamState.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */