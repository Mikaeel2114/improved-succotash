/*    */ package com.example.bedfight.scoreboard;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.scoreboard.DisplaySlot;
/*    */ import org.bukkit.scoreboard.Objective;
/*    */ import org.bukkit.scoreboard.Scoreboard;
/*    */ import org.bukkit.scoreboard.Team;
/*    */ 
/*    */ final class PlayerBoard
/*    */ {
/*    */   private static final char SECTION = '§';
/*    */   private final Scoreboard board;
/*    */   private final Objective objective;
/*    */   private int shown;
/*    */   
/*    */   PlayerBoard(Player p) {
/* 20 */     this.board = Bukkit.getScoreboardManager().getNewScoreboard();
/* 21 */     this.objective = this.board.registerNewObjective("bedfight", "dummy");
/* 22 */     this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
/* 23 */     p.setScoreboard(this.board);
/*    */   }
/*    */   
/*    */   void render(Player p, String title, List<String> lines) {
/* 27 */     if (p.getScoreboard() != this.board) {
/* 28 */       p.setScoreboard(this.board);
/*    */     }
/* 30 */     this.objective.setDisplayName(cut(title, 32));
/* 31 */     int n = Math.min(lines.size(), 15); int i;
/* 32 */     for (i = 0; i < n; i++) {
/* 33 */       String entry = entry(i);
/* 34 */       Team team = this.board.getTeam("l" + i);
/* 35 */       if (team == null) {
/* 36 */         team = this.board.registerNewTeam("l" + i);
/*    */       }
/* 38 */       if (!team.hasEntry(entry)) {
/* 39 */         team.addEntry(entry);
/*    */       }
/* 41 */       String[] parts = split(lines.get(i));
/* 42 */       if (!parts[0].equals(team.getPrefix())) {
/* 43 */         team.setPrefix(parts[0]);
/*    */       }
/* 45 */       if (!parts[1].equals(team.getSuffix())) {
/* 46 */         team.setSuffix(parts[1]);
/*    */       }
/* 48 */       this.objective.getScore(entry).setScore(n - i);
/*    */     } 
/*    */     
/* 51 */     for (i = n; i < this.shown; i++) {
/* 52 */       this.board.resetScores(entry(i));
/*    */     }
/* 54 */     this.shown = n;
/*    */   }
/*    */   
/*    */   private static String entry(int i) {
/* 58 */     return "§" + Integer.toHexString(i) + "§r";
/*    */   }
/*    */   
/*    */   private static String cut(String s, int max) {
/* 62 */     return (s.length() <= max) ? s : s.substring(0, max);
/*    */   }
/*    */   
/*    */   private static String[] split(String text) {
/* 66 */     text = cut(text, 32);
/* 67 */     if (text.length() <= 16) {
/* 68 */       return new String[] { text, "" };
/*    */     }
/* 70 */     String prefix = text.substring(0, 16);
/* 71 */     String rest = text.substring(16);
/* 72 */     if (prefix.charAt(15) == '§') {
/*    */       
/* 74 */       prefix = prefix.substring(0, 15);
/* 75 */       rest = "§" + rest;
/*    */     } 
/*    */     
/* 78 */     String suffix = cut(ChatColor.getLastColors(prefix) + ChatColor.getLastColors(prefix), 16);
/* 79 */     return new String[] { prefix, suffix };
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\scoreboard\PlayerBoard.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */