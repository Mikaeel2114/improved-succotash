/*    */ package com.example.bedfight.party;
/*    */ 
/*    */ import com.example.bedfight.BedFightPlugin;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*    */ import org.bukkit.event.player.PlayerQuitEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public final class PartyListener implements Listener {
/*    */   private final BedFightPlugin plugin;
/*    */   
/*    */   public PartyListener(BedFightPlugin plugin, PartyManager parties) {
/* 18 */     this.plugin = plugin;
/* 19 */     this.parties = parties;
/*    */   }
/*    */   private final PartyManager parties;
/*    */   
/*    */   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
/*    */   public void onChat(AsyncPlayerChatEvent e) {
/* 25 */     Player p = e.getPlayer();
/* 26 */     if (!this.parties.isChatOn(p)) {
/*    */       return;
/*    */     }
/* 29 */     e.setCancelled(true);
/* 30 */     String message = e.getMessage();
/* 31 */     Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
/*    */           String err = this.parties.chat(p, message);
/*    */           if (err != null) {
/*    */             this.parties.tell(p, String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.RED));
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void onQuit(PlayerQuitEvent e) {
/* 41 */     this.parties.handleQuit(e.getPlayer());
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\PartyListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */