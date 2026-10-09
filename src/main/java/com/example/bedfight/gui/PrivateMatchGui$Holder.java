/*    */ package com.example.bedfight.gui;
/*    */ 
/*    */ import org.bukkit.inventory.Inventory;
/*    */ import org.bukkit.inventory.InventoryHolder;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class Holder
/*    */   implements InventoryHolder
/*    */ {
/*    */   final PrivateMatchGui.Session session;
/*    */   Inventory inventory;
/*    */   
/*    */   Holder(PrivateMatchGui.Session session) {
/* 66 */     this.session = session;
/*    */   }
/*    */ 
/*    */   
/*    */   public Inventory getInventory() {
/* 71 */     return this.inventory;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\gui\PrivateMatchGui$Holder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */