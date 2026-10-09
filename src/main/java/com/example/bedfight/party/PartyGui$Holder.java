/*    */ package com.example.bedfight.party;
/*    */ 
/*    */ import java.util.UUID;
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
/*    */ final class Holder
/*    */   implements InventoryHolder
/*    */ {
/*    */   Inventory inventory;
/* 40 */   final UUID[] memberAt = new UUID[54];
/*    */ 
/*    */   
/*    */   public Inventory getInventory() {
/* 44 */     return this.inventory;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\party\PartyGui$Holder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */