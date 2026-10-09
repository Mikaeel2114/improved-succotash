/*    */ package com.example.bedfight.arena;
/*    */ public record Pos(double x, double y, double z, float yaw, float pitch) {
/*    */   
/*    */   public static Pos of(Location l) {
/* 11 */     return new Pos(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
/*    */   }
/*    */   
/*    */   public static Pos ofBlock(Block b) {
/* 15 */     return new Pos(b.getX(), b.getY(), b.getZ(), 0.0F, 0.0F);
/*    */   }
/*    */   
/*    */   public Location toLocation(World w) {
/* 19 */     return new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
/*    */   }
/*    */ 
/*    */   
/*    */   public Pos level() {
/* 24 */     return new Pos(this.x, this.y, this.z, this.yaw, 0.0F);
/*    */   }
/*    */   
/* 27 */   public int blockX() { return (int)Math.floor(this.x); }
/* 28 */   public int blockY() { return (int)Math.floor(this.y); } public int blockZ() {
/* 29 */     return (int)Math.floor(this.z);
/*    */   }
/*    */   public void write(ConfigurationSection s) {
/* 32 */     s.set("x", Double.valueOf(this.x));
/* 33 */     s.set("y", Double.valueOf(this.y));
/* 34 */     s.set("z", Double.valueOf(this.z));
/* 35 */     s.set("yaw", Double.valueOf(this.yaw));
/* 36 */     s.set("pitch", Double.valueOf(this.pitch));
/*    */   }
/*    */   
/*    */   public static Pos read(ConfigurationSection s) {
/* 40 */     if (s == null || !s.contains("x") || !s.contains("y") || !s.contains("z")) {
/* 41 */       return null;
/*    */     }
/* 43 */     return new Pos(s.getDouble("x"), s.getDouble("y"), s.getDouble("z"), 
/* 44 */         (float)s.getDouble("yaw"), (float)s.getDouble("pitch"));
/*    */   } }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\arena\Pos.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */