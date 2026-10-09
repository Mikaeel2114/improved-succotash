/*    */ package com.example.bedfight.party;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public final class Party {
/*    */   public enum Role {
/* 12 */     LEADER, MOD, MEMBER;
/*    */   }
/* 14 */   private final Map<UUID, Role> members = new LinkedHashMap<>();
/*    */   
/* 16 */   private final Map<UUID, Long> invites = new HashMap<>();
/*    */   
/*    */   private int limit;
/*    */   
/*    */   private boolean open;
/*    */   
/*    */   private boolean allInvite;
/*    */   private boolean muted;
/*    */   
/*    */   Party(UUID leader, int limit, boolean open, boolean allInvite) {
/* 26 */     this.members.put(leader, Role.LEADER);
/* 27 */     this.limit = limit;
/* 28 */     this.open = open;
/* 29 */     this.allInvite = allInvite;
/*    */   }
/*    */   
/* 32 */   public Map<UUID, Role> getMembers() { return this.members; }
/* 33 */   public Map<UUID, Long> getInvites() { return this.invites; }
/* 34 */   public int size() { return this.members.size(); }
/* 35 */   public boolean has(UUID id) { return this.members.containsKey(id); } public Role roleOf(UUID id) {
/* 36 */     return this.members.get(id);
/*    */   }
/*    */   public UUID getLeader() {
/* 39 */     for (Map.Entry<UUID, Role> e : this.members.entrySet()) {
/* 40 */       if (e.getValue() == Role.LEADER) {
/* 41 */         return e.getKey();
/*    */       }
/*    */     } 
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isStaff(UUID id) {
/* 49 */     Role r = this.members.get(id);
/* 50 */     return (r == Role.LEADER || r == Role.MOD);
/*    */   }
/*    */   public List<UUID> ids() {
/* 53 */     return new ArrayList<>(this.members.keySet());
/*    */   }
/* 55 */   public int getLimit() { return this.limit; }
/* 56 */   public void setLimit(int limit) { this.limit = limit; }
/* 57 */   public boolean isOpen() { return this.open; }
/* 58 */   public void setOpen(boolean open) { this.open = open; }
/* 59 */   public boolean isAllInvite() { return this.allInvite; }
/* 60 */   public void setAllInvite(boolean allInvite) { this.allInvite = allInvite; }
/* 61 */   public boolean isMuted() { return this.muted; } public void setMuted(boolean muted) {
/* 62 */     this.muted = muted;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\Party.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */