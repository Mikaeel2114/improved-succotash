/*     */ package com.example.bedfight.arena;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.DyeColor;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ 
/*     */ 
/*     */ public final class Arena
/*     */ {
/*     */   private final String name;
/*     */   private final String worldName;
/*     */   private Pos pos1;
/*     */   private Pos pos2;
/*     */   private Pos spawn;
/*     */   private int minX;
/*     */   private int maxX;
/*     */   private int minY;
/*     */   private int maxY;
/*     */   private int minZ;
/*     */   private int maxZ;
/*  23 */   private final Map<DyeColor, TeamSpec> teams = new LinkedHashMap<>();
/*     */   
/*     */   public Arena(String name, String worldName) {
/*  26 */     this.name = name;
/*  27 */     this.worldName = worldName;
/*     */   }
/*     */   
/*  30 */   public String getName() { return this.name; }
/*  31 */   public String getWorldName() { return this.worldName; }
/*  32 */   public Pos getPos1() { return this.pos1; }
/*  33 */   public Pos getPos2() { return this.pos2; }
/*  34 */   public Pos getSpawn() { return this.spawn; }
/*  35 */   public void setSpawn(Pos spawn) { this.spawn = spawn; }
/*  36 */   public Map<DyeColor, TeamSpec> getTeams() { return this.teams; }
/*  37 */   public int getMinX() { return this.minX; }
/*  38 */   public int getMaxX() { return this.maxX; }
/*  39 */   public int getMinY() { return this.minY; }
/*  40 */   public int getMaxY() { return this.maxY; }
/*  41 */   public int getMinZ() { return this.minZ; } public int getMaxZ() {
/*  42 */     return this.maxZ;
/*     */   }
/*  44 */   public void setPos1(Pos p) { this.pos1 = p; recalc(); }
/*  45 */   public void setPos2(Pos p) { this.pos2 = p; recalc(); } public boolean hasBounds() {
/*  46 */     return (this.pos1 != null && this.pos2 != null);
/*     */   }
/*     */   private void recalc() {
/*  49 */     if (!hasBounds()) {
/*     */       return;
/*     */     }
/*  52 */     this.minX = Math.min(this.pos1.blockX(), this.pos2.blockX());
/*  53 */     this.maxX = Math.max(this.pos1.blockX(), this.pos2.blockX());
/*  54 */     this.minY = Math.min(this.pos1.blockY(), this.pos2.blockY());
/*  55 */     this.maxY = Math.max(this.pos1.blockY(), this.pos2.blockY());
/*  56 */     this.minZ = Math.min(this.pos1.blockZ(), this.pos2.blockZ());
/*  57 */     this.maxZ = Math.max(this.pos1.blockZ(), this.pos2.blockZ());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inBuildArea(int x, int y, int z) {
/*  62 */     return (x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ && y >= this.minY && y <= this.maxY);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inHorizontalRange(double x, double z, int padding) {
/*  67 */     return (x >= (this.minX - padding) && x <= (this.maxX + 1 + padding) && z >= (this.minZ - padding) && z <= (this.maxZ + 1 + padding));
/*     */   }
/*     */   
/*     */   public boolean removeTeam(DyeColor color) {
/*  71 */     return (this.teams.remove(color) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Pos waitSpawn(DyeColor color) {
/*  76 */     TeamSpec t = this.teams.get(color);
/*  77 */     Pos p = (t != null && t.getWaitSpawn() != null) ? t.getWaitSpawn() : this.spawn;
/*  78 */     return p.level();
/*     */   }
/*     */   
/*     */   public TeamSpec team(DyeColor color, boolean create) {
/*  82 */     TeamSpec t = this.teams.get(color);
/*  83 */     if (t == null && create) {
/*  84 */       t = new TeamSpec(color);
/*  85 */       this.teams.put(color, t);
/*     */     } 
/*  87 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public String validate() {
/*  92 */     if (!hasBounds()) return "Set both boundaries first (/bedfight po1 and /bedfight po2)."; 
/*  93 */     if (this.spawn == null) return "Set the game spawn first (/bedfight setspawn)."; 
/*  94 */     if (this.teams.size() < 2) return "At least 2 teams are required (/bedfight createteam <color>)."; 
/*  95 */     for (TeamSpec t : this.teams.values()) {
/*  96 */       if (t.getSpawn() == null) return "Team " + String.valueOf(t.getColor()) + " has no spawn (/bedfight setteam)."; 
/*  97 */       if (t.getBed() == null) return "Team " + String.valueOf(t.getColor()) + " has no bed (/bedfight setbed)."; 
/*     */     } 
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String checkGeometry(int minAboveBed) {
/* 104 */     if (!hasBounds()) return null; 
/* 105 */     if (this.spawn != null && this.spawn.blockY() < this.minY) {
/* 106 */       return "The game spawn is below the void level (min Y = " + this.minY + ").";
/*     */     }
/* 108 */     for (TeamSpec t : this.teams.values()) {
/* 109 */       if (t.getSpawn() != null && t.getSpawn().blockY() < this.minY) {
/* 110 */         return "Team " + String.valueOf(t.getColor()) + " spawn (Y " + t.getSpawn().blockY() + ") is below the void level (min Y = " + this.minY + "): players would die instantly.";
/*     */       }
/*     */       
/* 113 */       if (t.getBed() != null) {
/* 114 */         Pos b = t.getBed();
/* 115 */         if (b.blockX() < this.minX || b.blockX() > this.maxX || b.blockZ() < this.minZ || b.blockZ() > this.maxZ || b.blockY() < this.minY) {
/* 116 */           return "The bed of team " + String.valueOf(t.getColor()) + " (X " + b.blockX() + ", Y " + b.blockY() + ", Z " + b.blockZ() + ") is outside the po1/po2 box (X " + this.minX + " to " + this.maxX + ", Y " + this.minY + " to " + this.maxY + ", Z " + this.minZ + " to " + this.maxZ + "). Re-run /bedfight po1 and po2 at two opposite corners so the box covers BOTH islands.";
/*     */         }
/*     */ 
/*     */         
/* 120 */         if (this.maxY < b.blockY() + minAboveBed) {
/* 121 */           return "Max Y (" + this.maxY + ") is too low: it must be at least " + minAboveBed + " blocks above the bed of team " + 
/* 122 */             String.valueOf(t.getColor()) + " (Y " + b.blockY() + ") so bed defenses can be built. Set po2 (or po1) higher up.";
/*     */         }
/*     */       } 
/*     */     } 
/* 126 */     return null;
/*     */   }
/*     */   
/*     */   public void write(YamlConfiguration y) {
/* 130 */     y.set("name", this.name);
/* 131 */     y.set("world", this.worldName);
/* 132 */     this.pos1.write(y.createSection("pos1"));
/* 133 */     this.pos2.write(y.createSection("pos2"));
/* 134 */     y.set("bounds.min-x", Integer.valueOf(this.minX));
/* 135 */     y.set("bounds.max-x", Integer.valueOf(this.maxX));
/* 136 */     y.set("bounds.min-y", Integer.valueOf(this.minY));
/* 137 */     y.set("bounds.max-y", Integer.valueOf(this.maxY));
/* 138 */     y.set("bounds.min-z", Integer.valueOf(this.minZ));
/* 139 */     y.set("bounds.max-z", Integer.valueOf(this.maxZ));
/* 140 */     this.spawn.write(y.createSection("spawn"));
/* 141 */     for (TeamSpec t : this.teams.values()) {
/* 142 */       String base = "teams." + t.getColor().name();
/* 143 */       t.getSpawn().write(y.createSection(base + ".spawn"));
/* 144 */       t.getBed().write(y.createSection(base + ".bed"));
/* 145 */       if (t.getWaitSpawn() != null) {
/* 146 */         t.getWaitSpawn().write(y.createSection(base + ".wait-spawn"));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Arena read(String name, YamlConfiguration y) {
/* 153 */     Arena a = new Arena(name, y.getString("world", name));
/* 154 */     a.pos1 = Pos.read(y.getConfigurationSection("pos1"));
/* 155 */     a.pos2 = Pos.read(y.getConfigurationSection("pos2"));
/* 156 */     a.recalc();
/* 157 */     a.spawn = Pos.read(y.getConfigurationSection("spawn"));
/* 158 */     ConfigurationSection ts = y.getConfigurationSection("teams");
/* 159 */     if (ts != null) {
/* 160 */       for (String key : ts.getKeys(false)) {
/* 161 */         DyeColor c = Teams.parse(key);
/* 162 */         if (c == null) {
/*     */           continue;
/*     */         }
/* 165 */         TeamSpec t = a.team(c, true);
/* 166 */         t.setSpawn(Pos.read(ts.getConfigurationSection(key + ".spawn")));
/* 167 */         t.setBed(Pos.read(ts.getConfigurationSection(key + ".bed")));
/* 168 */         t.setWaitSpawn(Pos.read(ts.getConfigurationSection(key + ".wait-spawn")));
/*     */       } 
/*     */     }
/* 171 */     return a;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\arena\Arena.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */