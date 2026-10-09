/*    */ package com.example.bedfight.kit;
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
/*    */ final class Holder
/*    */   implements InventoryHolder
/*    */ {
/*    */   final boolean admin;
/*    */   Inventory inventory;
/*    */   
/*    */   Holder(boolean admin) {
/* 47 */     this.admin = admin;
/*    */   }
/*    */ 
/*    */   
/*    */   public Inventory getInventory() {
/* 52 */     return this.inventory;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\kit\KitEditorGui$Holder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */