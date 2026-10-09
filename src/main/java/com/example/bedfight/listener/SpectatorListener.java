/*     */ package com.example.bedfight.listener;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.game.Match;
/*     */ import com.example.bedfight.spectate.SpectatorManager;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.entity.EntityTargetEvent;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryDragEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.inventory.InventoryHolder;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public final class SpectatorListener
/*     */   implements Listener {
/*     */   public SpectatorListener(BedFightPlugin plugin) {
/*  27 */     this.plugin = plugin;
/*     */   }
/*     */   private final BedFightPlugin plugin;
/*     */   private SpectatorManager specs() {
/*  31 */     return this.plugin.getSpectators();
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onInteract(PlayerInteractEvent e) {
/*  36 */     Player p = e.getPlayer();
/*  37 */     if (!specs().is(p)) {
/*     */       return;
/*     */     }
/*  40 */     e.setCancelled(true);
/*  41 */     if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
/*     */       return;
/*     */     }
/*  44 */     ItemStack hand = p.getItemInHand();
/*  45 */     if (specs().isTeleporterItem(hand)) {
/*  46 */       specs().openTeleporter(p);
/*  47 */     } else if (specs().isLeaveItem(hand)) {
/*  48 */       this.plugin.getGameManager().leaveAny(p);
/*     */     } 
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onInteractEntity(PlayerInteractEntityEvent e) {
/*  54 */     if (specs().is(e.getPlayer()))
/*  55 */       e.setCancelled(true); 
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onClick(InventoryClickEvent e) {
/*     */     Player p;
/*  61 */     HumanEntity who = e.getWhoClicked();
/*  62 */     if (who instanceof Player) { p = (Player)who; }
/*     */     else
/*     */     { return; }
/*  65 */      InventoryHolder holder = e.getInventory().getHolder();
/*  66 */     if (holder instanceof SpectatorManager.TeleporterHolder) { SpectatorManager.TeleporterHolder th = (SpectatorManager.TeleporterHolder)holder;
/*  67 */       e.setCancelled(true);
/*  68 */       int slot = e.getRawSlot();
/*  69 */       if (slot < 0 || slot >= th.targets.size()) {
/*     */         return;
/*     */       }
/*  72 */       UUID id = th.targets.get(slot);
/*  73 */       Player target = Bukkit.getPlayer(id);
/*  74 */       Match m = specs().matchOf(p);
/*  75 */       p.closeInventory();
/*  76 */       if (target == null || m == null || !target.getWorld().equals(p.getWorld())) {
/*  77 */         this.plugin.msg((CommandSender)p, "That player is no longer available.");
/*     */         return;
/*     */       } 
/*  80 */       p.teleport(target.getLocation().clone().add(0.0D, 2.0D, 0.0D));
/*     */       return; }
/*     */     
/*  83 */     if (specs().is(p))
/*     */     {
/*  85 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onDrag(InventoryDragEvent e) {
/*  91 */     HumanEntity humanEntity = e.getWhoClicked(); if (humanEntity instanceof Player) { Player p = (Player)humanEntity;
/*  92 */       if (specs().is(p) || e.getInventory().getHolder() instanceof SpectatorManager.TeleporterHolder) {
/*  93 */         e.setCancelled(true);
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onTarget(EntityTargetEvent e) {
/* 100 */     Entity entity = e.getTarget(); if (entity instanceof Player) { Player p = (Player)entity; if (specs().is(p))
/* 101 */         e.setCancelled(true);  }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\listener\SpectatorListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */