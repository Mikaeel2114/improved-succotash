/*     */ package com.example.bedfight.arena;
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import com.example.bedfight.util.YamlFiles;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.DyeColor;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.WorldCreator;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public final class ArenaManager {
/*     */   private final BedFightPlugin plugin;
/*  25 */   private final Map<String, Arena> arenas = new LinkedHashMap<>(); private final File dir;
/*  26 */   private final Map<UUID, Arena> sessions = new HashMap<>();
/*  27 */   private final Map<UUID, DyeColor> selectedTeam = new HashMap<>();
/*  28 */   private final Set<UUID> buildMode = new HashSet<>();
/*     */   
/*     */   public ArenaManager(BedFightPlugin plugin) {
/*  31 */     this.plugin = plugin;
/*  32 */     this.dir = new File(plugin.getDataFolder(), "arenas");
/*     */   }
/*     */   
/*     */   public void loadAll() {
/*  36 */     this.arenas.clear();
/*  37 */     this.dir.mkdirs();
/*  38 */     File[] files = this.dir.listFiles((d, n) -> n.toLowerCase().endsWith(".yml"));
/*  39 */     if (files == null) {
/*     */       return;
/*     */     }
/*  42 */     for (File f : files) {
/*  43 */       String name = f.getName().substring(0, f.getName().length() - 4);
/*  44 */       Arena a = Arena.read(name, YamlConfiguration.loadConfiguration(f));
/*  45 */       String problem = a.validate();
/*  46 */       if (problem == null) {
/*  47 */         String geo = a.checkGeometry(minAboveBed());
/*  48 */         if (geo != null) {
/*  49 */           this.plugin.getLogger().warning("Arena '" + name + "' may be misconfigured: " + geo);
/*     */         }
/*  51 */         this.arenas.put(name.toLowerCase(), a);
/*     */       } else {
/*  53 */         this.plugin.getLogger().warning("Skipping incomplete arena '" + name + "': " + problem);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int minAboveBed() {
/*  60 */     return Math.max(0, this.plugin.getSettings().getInt("arena.min-build-height-above-bed", 3));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> savedNames() {
/*  65 */     List<String> out = new ArrayList<>();
/*  66 */     File[] files = this.dir.listFiles((d, n) -> n.toLowerCase().endsWith(".yml"));
/*  67 */     if (files != null) {
/*  68 */       for (File f : files) {
/*  69 */         out.add(f.getName().substring(0, f.getName().length() - 4));
/*     */       }
/*     */     }
/*  72 */     Collections.sort(out);
/*  73 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public String editArena(Player p, String name) {
/*  78 */     String real = null;
/*  79 */     for (String n : savedNames()) {
/*  80 */       if (n.equalsIgnoreCase(name)) {
/*  81 */         real = n;
/*     */       }
/*     */     } 
/*  84 */     if (real == null) {
/*  85 */       return "No saved arena called '" + name + "'. Saved arenas: " + (
/*  86 */         savedNames().isEmpty() ? "(none)" : String.join(", ", (Iterable)savedNames()));
/*     */     }
/*  88 */     String err = setupArena(p, real);
/*  89 */     if (err == null) {
/*  90 */       this.buildMode.add(p.getUniqueId());
/*  91 */       Arena a = this.sessions.get(p.getUniqueId());
/*  92 */       if (a != null && a.getSpawn() != null && a.hasBounds()) {
/*  93 */         World w = Bukkit.getWorld(a.getWorldName());
/*  94 */         if (w != null) {
/*  95 */           p.teleport(a.getSpawn().level().toLocation(w));
/*     */         }
/*     */       } 
/*     */     } 
/*  99 */     return err;
/*     */   }
/*     */   public Collection<Arena> all() {
/* 102 */     return this.arenas.values();
/*     */   } public Arena get(String name) {
/* 104 */     return (name == null) ? null : this.arenas.get(name.toLowerCase());
/*     */   }
/*     */   public String save(Player p) {
/* 107 */     Arena a = this.sessions.get(p.getUniqueId());
/* 108 */     if (a == null) {
/* 109 */       return "No arena setup in progress. Use /bedfight setuparena <world_name>.";
/*     */     }
/* 111 */     String err = a.validate();
/* 112 */     if (err == null) {
/* 113 */       err = a.checkGeometry(minAboveBed());
/*     */     }
/* 115 */     if (err != null) {
/* 116 */       return err;
/*     */     }
/* 118 */     YamlConfiguration y = new YamlConfiguration();
/* 119 */     a.write(y);
/* 120 */     File f = new File(this.dir, a.getName() + ".yml");
/*     */     try {
/* 122 */       this.dir.mkdirs();
/* 123 */       YamlFiles.save(y, f);
/* 124 */     } catch (IOException ex) {
/* 125 */       this.plugin.getLogger().log(Level.SEVERE, "Could not save arena " + a.getName(), ex);
/* 126 */       return "Could not write " + f.getName() + ": " + ex.getMessage();
/*     */     } 
/* 128 */     this.arenas.put(a.getName().toLowerCase(), Arena.read(a.getName(), y));
/* 129 */     World w = Bukkit.getWorld(a.getWorldName());
/* 130 */     if (w != null) {
/* 131 */       w.save();
/*     */     }
/* 133 */     return null;
/*     */   }
/*     */   
/*     */   public String setupArena(Player p, String worldName) {
/* 137 */     if (!worldName.matches("[A-Za-z0-9_.\\-]+") || worldName.contains("..")) {
/* 138 */       return "Invalid world name.";
/*     */     }
/* 140 */     File container = Bukkit.getWorldContainer();
/* 141 */     File folder = new File(container, worldName);
/* 142 */     if (!folder.isDirectory() || !(new File(folder, "level.dat")).isFile()) {
/* 143 */       return "World folder '" + worldName + "' (with level.dat) not found in " + container.getAbsolutePath();
/*     */     }
/* 145 */     World world = Bukkit.getWorld(worldName);
/* 146 */     if (world == null) {
/* 147 */       world = Bukkit.createWorld(new WorldCreator(worldName));
/*     */     }
/* 149 */     if (world == null) {
/* 150 */       return "Failed to load world '" + worldName + "'.";
/*     */     }
/* 152 */     File existing = new File(this.dir, worldName + ".yml");
/*     */ 
/*     */     
/* 155 */     Arena arena = existing.isFile() ? Arena.read(worldName, YamlConfiguration.loadConfiguration(existing)) : new Arena(worldName, worldName);
/* 156 */     this.sessions.put(p.getUniqueId(), arena);
/* 157 */     this.selectedTeam.remove(p.getUniqueId());
/* 158 */     p.teleport(world.getSpawnLocation());
/* 159 */     return null;
/*     */   }
/*     */   
/* 162 */   public Arena session(Player p) { return this.sessions.get(p.getUniqueId()); }
/* 163 */   public void select(Player p, DyeColor c) { this.selectedTeam.put(p.getUniqueId(), c); } public DyeColor selected(Player p) {
/* 164 */     return this.selectedTeam.get(p.getUniqueId());
/*     */   }
/*     */   public boolean toggleBuildMode(Player p) {
/* 167 */     if (!this.buildMode.remove(p.getUniqueId())) {
/* 168 */       this.buildMode.add(p.getUniqueId());
/* 169 */       return true;
/*     */     } 
/* 171 */     return false;
/*     */   }
/*     */   public boolean isBuildMode(Player p) {
/* 174 */     return this.buildMode.contains(p.getUniqueId());
/*     */   }
/*     */   public void onQuit(Player p) {
/* 177 */     this.buildMode.remove(p.getUniqueId());
/*     */   }
/*     */   
/*     */   public boolean isTemplateWorld(World w) {
/* 181 */     String n = w.getName();
/* 182 */     for (Arena a : this.arenas.values()) {
/* 183 */       if (a.getWorldName().equalsIgnoreCase(n)) {
/* 184 */         return true;
/*     */       }
/*     */     } 
/* 187 */     for (Arena a : this.sessions.values()) {
/* 188 */       if (a.getWorldName().equalsIgnoreCase(n)) {
/* 189 */         return true;
/*     */       }
/*     */     } 
/* 192 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\arena\ArenaManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */