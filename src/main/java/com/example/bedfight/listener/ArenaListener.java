/*     */ package com.example.bedfight.listener;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.arena.ArenaManager;
/*     */ import com.example.bedfight.game.Match;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockBurnEvent;
/*     */ import org.bukkit.event.block.BlockIgniteEvent;
/*     */ import org.bukkit.event.block.BlockPistonExtendEvent;
/*     */ import org.bukkit.event.block.BlockPistonRetractEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.block.LeavesDecayEvent;
/*     */ import org.bukkit.event.entity.EntityChangeBlockEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityExplodeEvent;
/*     */ import org.bukkit.event.entity.FoodLevelChangeEvent;
/*     */ import org.bukkit.event.entity.ItemSpawnEvent;
/*     */ import org.bukkit.event.hanging.HangingBreakEvent;
/*     */ import org.bukkit.event.player.PlayerBucketEmptyEvent;
/*     */ import org.bukkit.event.player.PlayerBucketFillEvent;
/*     */ import org.bukkit.event.player.PlayerDropItemEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */ import org.bukkit.event.player.PlayerPickupItemEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArenaListener
/*     */   implements Listener
/*     */ {
/*     */   private static final String PROTECTED_MSG = "This arena is protected. Use /bedfight buildmode to edit it.";
/*     */   private final BedFightPlugin plugin;
/*     */   
/*     */   public ArenaListener(BedFightPlugin plugin) {
/*  47 */     this.plugin = plugin;
/*     */   }
/*     */   
/*     */   private Match match(World w) {
/*  51 */     return this.plugin.getGameManager().getMatchByWorld(w);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onBreak(BlockBreakEvent e) {
/*  58 */     Match m = match(e.getBlock().getWorld());
/*  59 */     if (m != null) {
/*  60 */       m.onBreak(e);
/*     */       return;
/*     */     } 
/*  63 */     ArenaManager am = this.plugin.getArenaManager();
/*  64 */     if (am.isTemplateWorld(e.getBlock().getWorld()) && !am.isBuildMode(e.getPlayer())) {
/*  65 */       e.setCancelled(true);
/*  66 */       this.plugin.msg((CommandSender)e.getPlayer(), "This arena is protected. Use /bedfight buildmode to edit it.");
/*     */     } 
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onPlace(BlockPlaceEvent e) {
/*  72 */     Match m = match(e.getBlock().getWorld());
/*  73 */     if (m != null) {
/*  74 */       m.onPlace(e);
/*     */       return;
/*     */     } 
/*  77 */     ArenaManager am = this.plugin.getArenaManager();
/*  78 */     if (am.isTemplateWorld(e.getBlock().getWorld()) && !am.isBuildMode(e.getPlayer())) {
/*  79 */       e.setCancelled(true);
/*  80 */       this.plugin.msg((CommandSender)e.getPlayer(), "This arena is protected. Use /bedfight buildmode to edit it.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onBucketEmpty(PlayerBucketEmptyEvent e) {
/*  87 */     if (match(e.getPlayer().getWorld()) != null) {
/*  88 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onBucketFill(PlayerBucketFillEvent e) {
/*  94 */     if (match(e.getPlayer().getWorld()) != null) {
/*  95 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onPistonExtend(BlockPistonExtendEvent e) {
/* 102 */     if (match(e.getBlock().getWorld()) != null) {
/* 103 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onPistonRetract(BlockPistonRetractEvent e) {
/* 109 */     if (match(e.getBlock().getWorld()) != null) {
/* 110 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onBurn(BlockBurnEvent e) {
/* 116 */     if (match(e.getBlock().getWorld()) != null) {
/* 117 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onIgnite(BlockIgniteEvent e) {
/* 123 */     if (match(e.getBlock().getWorld()) != null) {
/* 124 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onLeavesDecay(LeavesDecayEvent e) {
/* 130 */     if (match(e.getBlock().getWorld()) != null) {
/* 131 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onHangingBreak(HangingBreakEvent e) {
/* 137 */     if (match(e.getEntity().getWorld()) != null) {
/* 138 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onEntityChangeBlock(EntityChangeBlockEvent e) {
/* 145 */     Match m = match(e.getBlock().getWorld());
/* 146 */     if (m == null) {
/*     */       return;
/*     */     }
/* 149 */     if (e.getEntity() instanceof org.bukkit.entity.FallingBlock) {
/* 150 */       if (e.getTo() == Material.AIR) {
/*     */         
/* 152 */         if (m.isPlacedBlock(e.getBlock())) {
/* 153 */           m.unmarkPlaced(e.getBlock());
/*     */         } else {
/* 155 */           e.setCancelled(true);
/*     */         } 
/*     */       } else {
/*     */         
/* 159 */         m.markPlaced(e.getBlock());
/*     */       } 
/*     */     } else {
/* 162 */       e.setCancelled(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
/*     */   public void onExplode(EntityExplodeEvent e) {
/* 168 */     World w = e.getEntity().getWorld();
/* 169 */     Match m = match(w);
/* 170 */     if (m != null) {
/* 171 */       m.onExplode(e);
/* 172 */     } else if (this.plugin.getArenaManager().isTemplateWorld(w)) {
/* 173 */       e.blockList().clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onPhysicalInteract(PlayerInteractEvent e) {
/* 180 */     if (e.getAction() == Action.PHYSICAL && match(e.getPlayer().getWorld()) != null) {
/* 181 */       e.setCancelled(true);
/* 182 */       e.setUseInteractedBlock(Event.Result.DENY);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onDamage(EntityDamageEvent e) {
/* 190 */     Match m = match(e.getEntity().getWorld());
/* 191 */     if (m != null) {
/* 192 */       m.onDamage(e);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(ignoreCancelled = true)
/*     */   public void onMove(PlayerMoveEvent e) {
/* 198 */     Match m = match(e.getPlayer().getWorld());
/* 199 */     if (m != null) {
/* 200 */       m.onMove(e);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onDrop(PlayerDropItemEvent e) {
/* 206 */     if (match(e.getPlayer().getWorld()) != null) {
/* 207 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onPickup(PlayerPickupItemEvent e) {
/* 213 */     Match m = match(e.getPlayer().getWorld());
/* 214 */     if (m != null && !m.canPickup(e.getPlayer(), e.getItem())) {
/* 215 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGHEST)
/*     */   public void onItemSpawn(ItemSpawnEvent e) {
/* 221 */     Match m = match(e.getLocation().getWorld());
/* 222 */     if (m != null && !m.allowItemSpawn(e.getEntity())) {
/* 223 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onFood(FoodLevelChangeEvent e) {
/* 229 */     if (match(e.getEntity().getWorld()) != null) {
/* 230 */       e.setCancelled(true);
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onJoin(PlayerJoinEvent e) {
/* 236 */     Player p = e.getPlayer();
/*     */     
/* 238 */     if (p.getWorld().getName().startsWith("bf_") && match(p.getWorld()) == null) {
/* 239 */       p.teleport(this.plugin.getLobby());
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onQuit(PlayerQuitEvent e) {
/* 245 */     this.plugin.getArenaManager().onQuit(e.getPlayer());
/* 246 */     this.plugin.getGameManager().handleQuit(e.getPlayer());
/* 247 */     this.plugin.getScoreboards().remove(e.getPlayer());
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\listener\ArenaListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */