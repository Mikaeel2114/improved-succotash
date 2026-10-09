/*    */ package com.example.bedfight.game;
/*    */ 
/*    */ import com.example.bedfight.util.Settings;
/*    */ 
/*    */ 
/*    */ public final class MatchOptions
/*    */ {
/*    */   public int respawnDelaySeconds;
/*    */   public boolean fallDamage;
/*    */   public boolean bedsEnabled = true;
/*    */   public boolean allowSpectators = true;
/*    */   public int timeLimitMinutes;
/*    */   
/*    */   public static MatchOptions defaults(Settings s) {
/* 15 */     MatchOptions o = new MatchOptions();
/* 16 */     o.respawnDelaySeconds = Math.max(1, s.getInt("game.respawn-delay-seconds", 5));
/* 17 */     o.fallDamage = s.getBoolean("game.fall-damage", false);
/* 18 */     o.allowSpectators = s.getBoolean("spectator.enabled", true);
/* 19 */     o.timeLimitMinutes = Math.max(0, s.getInt("game.time-limit-minutes", 0));
/* 20 */     return o;
/*    */   }
/*    */   
/*    */   public MatchOptions copy() {
/* 24 */     MatchOptions o = new MatchOptions();
/* 25 */     o.respawnDelaySeconds = this.respawnDelaySeconds;
/* 26 */     o.fallDamage = this.fallDamage;
/* 27 */     o.bedsEnabled = this.bedsEnabled;
/* 28 */     o.allowSpectators = this.allowSpectators;
/* 29 */     o.timeLimitMinutes = this.timeLimitMinutes;
/* 30 */     return o;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\game\MatchOptions.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */