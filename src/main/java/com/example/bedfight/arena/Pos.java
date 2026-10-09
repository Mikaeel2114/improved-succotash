/*    */ package com.example.bedfight.arena;
/*    */ public final class Pos extends Record { private final double x;
/*    */   private final double y;
/*    */   private final double z;
/*    */   private final float yaw;
/*    */   private final float pitch;
/*    */   
/*  8 */   public Pos(double x, double y, double z, float yaw, float pitch) { this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lcom/example/bedfight/arena/Pos;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #8	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  8 */     //   0	7	0	this	Lcom/example/bedfight/arena/Pos; } public double x() { return this.x; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lcom/example/bedfight/arena/Pos;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #8	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lcom/example/bedfight/arena/Pos; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lcom/example/bedfight/arena/Pos;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #8	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lcom/example/bedfight/arena/Pos;
/*  8 */     //   0	8	1	o	Ljava/lang/Object; } public double y() { return this.y; } public double z() { return this.z; } public float yaw() { return this.yaw; } public float pitch() { return this.pitch; }
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