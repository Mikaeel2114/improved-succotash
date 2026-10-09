/*     */ package com.example.bedfight;
/*     */ import com.example.bedfight.arena.ArenaManager;
/*     */ import com.example.bedfight.command.BedFightCommand;
/*     */ import com.example.bedfight.game.GameManager;
/*     */ import com.example.bedfight.gui.PrivateMatchGui;
/*     */ import com.example.bedfight.kit.KitEditorGui;
/*     */ import com.example.bedfight.kit.KitManager;
/*     */ import com.example.bedfight.listener.ArenaListener;
/*     */ import com.example.bedfight.listener.SpectatorListener;
/*     */ import com.example.bedfight.party.PartyCommand;
/*     */ import com.example.bedfight.party.PartyGui;
/*     */ import com.example.bedfight.party.PartyListener;
/*     */ import com.example.bedfight.party.PartyManager;
/*     */ import com.example.bedfight.scoreboard.ScoreboardManager;
/*     */ import com.example.bedfight.spectate.SpectatorManager;
/*     */ import com.example.bedfight.util.ConfigMerger;
/*     */ import com.example.bedfight.util.Settings;
/*     */ import com.example.bedfight.util.Voices;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.command.TabCompleter;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public final class BedFightPlugin extends JavaPlugin {
/*  39 */   public static final String PREFIX = String.valueOf(ChatColor.RED) + "BedFight " + String.valueOf(ChatColor.RED) + "» " + String.valueOf(ChatColor.BOLD) + String.valueOf(ChatColor.RESET);
/*     */   
/*     */   private Settings settings;
/*     */   
/*     */   private Voices voices;
/*     */   
/*     */   private PartyManager parties;
/*     */   private ArenaManager arenaManager;
/*     */   private GameManager gameManager;
/*     */   private PrivateMatchGui privateGui;
/*     */   private KitManager kitManager;
/*     */   private KitEditorGui kitEditor;
/*     */   private SpectatorManager spectators;
/*     */   private ScoreboardManager scoreboards;
/*     */   
/*     */   public void onEnable() {
/*  55 */     mergeDefaults();
/*  56 */     reloadConfig();
/*  57 */     this.settings = new Settings(this);
/*  58 */     this.voices = new Voices(this);
/*     */     
/*  60 */     this.parties = new PartyManager(this);
/*  61 */     this.arenaManager = new ArenaManager(this);
/*  62 */     this.arenaManager.loadAll();
/*  63 */     this.gameManager = new GameManager(this);
/*  64 */     this.spectators = new SpectatorManager(this);
/*  65 */     this.privateGui = new PrivateMatchGui(this);
/*  66 */     this.kitManager = new KitManager(this);
/*  67 */     this.kitEditor = new KitEditorGui(this);
/*  68 */     this.scoreboards = new ScoreboardManager(this);
/*     */     
/*  70 */     getServer().getPluginManager().registerEvents((Listener)new ArenaListener(this), (Plugin)this);
/*  71 */     getServer().getPluginManager().registerEvents((Listener)new SpectatorListener(this), (Plugin)this);
/*  72 */     getServer().getPluginManager().registerEvents((Listener)this.privateGui, (Plugin)this);
/*  73 */     getServer().getPluginManager().registerEvents((Listener)this.kitEditor, (Plugin)this);
/*  74 */     PartyGui partyGui = new PartyGui(this, this.parties);
/*  75 */     getServer().getPluginManager().registerEvents((Listener)partyGui, (Plugin)this);
/*  76 */     getServer().getPluginManager().registerEvents((Listener)new PartyListener(this, this.parties), (Plugin)this);
/*     */     
/*  78 */     BedFightCommand command = new BedFightCommand(this);
/*  79 */     register("bedfight", command);
/*  80 */     register("leave", command);
/*  81 */     register("adminkiteditor", command);
/*     */     
/*  83 */     PartyCommand partyCommand = new PartyCommand(this, this.parties, partyGui);
/*  84 */     for (String name : new String[] { "party", "pc" }) {
/*  85 */       PluginCommand pc = getCommand(name);
/*  86 */       pc.setExecutor((CommandExecutor)partyCommand);
/*  87 */       pc.setTabCompleter((TabCompleter)partyCommand);
/*     */     } 
/*     */     
/*  90 */     getLogger().info("Loaded " + this.arenaManager.all().size() + " arena(s).");
/*     */   }
/*     */   
/*     */   private void register(String name, BedFightCommand executor) {
/*  94 */     PluginCommand pc = getCommand(name);
/*  95 */     pc.setExecutor((CommandExecutor)executor);
/*  96 */     pc.setTabCompleter((TabCompleter)executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 101 */     if (this.scoreboards != null) {
/* 102 */       this.scoreboards.stop();
/*     */     }
/* 104 */     if (this.gameManager != null) {
/* 105 */       this.gameManager.shutdown();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void reloadAll() {
/* 111 */     mergeDefaults();
/* 112 */     reloadConfig();
/* 113 */     this.settings.reload();
/* 114 */     this.voices.reload();
/* 115 */     this.arenaManager.loadAll();
/* 116 */     this.kitManager.load();
/* 117 */     this.scoreboards.reload();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void mergeDefaults() {
/* 127 */     getDataFolder().mkdirs();
/* 128 */     List<String> names = new ArrayList<>(); 
/* 129 */     try { JarFile jar = new JarFile(getFile()); 
/* 130 */       try { Enumeration<JarEntry> entries = jar.entries();
/* 131 */         while (entries.hasMoreElements()) {
/* 132 */           JarEntry e = entries.nextElement();
/* 133 */           String n = e.getName();
/* 134 */           if (e.isDirectory() || n.endsWith(".class") || n.startsWith("META-INF/") || n.equals("plugin.yml")) {
/*     */             continue;
/*     */           }
/* 137 */           names.add(n);
/*     */         } 
/* 139 */         jar.close(); } catch (Throwable throwable) { try { jar.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Exception ex)
/* 140 */     { names.clear();
/* 141 */       names.addAll(Arrays.asList(new String[] { "config.yml", "settings.yml", "voices.yml", "scoreboard.yml" })); }
/*     */     
/* 143 */     Collections.sort(names);
/* 144 */     for (String n : names) {
/* 145 */       if (n.endsWith(".yml") || n.endsWith(".yaml")) {
/* 146 */         ConfigMerger.merge(this, n); continue;
/* 147 */       }  if (!(new File(getDataFolder(), n)).exists()) {
/* 148 */         saveResource(n, false);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void msg(CommandSender to, String message) {
/* 155 */     to.sendMessage(PREFIX + PREFIX);
/*     */   }
/*     */   
/*     */   public Location getLobby() {
/*     */     org.bukkit.configuration.ConfigurationSection sec = getConfig().getConfigurationSection("lobby");
/*     */     if (sec != null && sec.contains("x")) {
/*     */       World lw = Bukkit.getWorld(sec.getString("world", ""));
/*     */       if (lw != null) {
/*     */         return new Location(lw, sec.getDouble("x"), sec.getDouble("y"), sec.getDouble("z"), (float)sec.getDouble("yaw"), (float)sec.getDouble("pitch"));
/*     */       }
/*     */     }
/*     */     World w = Bukkit.getWorld(getConfig().getString("lobby-world", ""));
/*     */     if (w == null) {
/*     */       w = Bukkit.getWorlds().get(0);
/*     */     }
/*     */     return w.getSpawnLocation();
/*     */   }
/*     */   
/*     */   public void setLobby(Location l) {
/*     */     getConfig().set("lobby.world", l.getWorld().getName());
/*     */     getConfig().set("lobby.x", Double.valueOf(l.getX()));
/*     */     getConfig().set("lobby.y", Double.valueOf(l.getY()));
/*     */     getConfig().set("lobby.z", Double.valueOf(l.getZ()));
/*     */     getConfig().set("lobby.yaw", Float.valueOf(l.getYaw()));
/*     */     getConfig().set("lobby.pitch", Float.valueOf(l.getPitch()));
/*     */     saveConfig();
/*     */   }
/*     */   
/* 166 */   public Settings getSettings() { return this.settings; }
/* 167 */   public Voices getVoices() { return this.voices; }
/* 168 */   public PartyManager getParties() { return this.parties; }
/* 169 */   public ArenaManager getArenaManager() { return this.arenaManager; }
/* 170 */   public GameManager getGameManager() { return this.gameManager; }
/* 171 */   public PrivateMatchGui getPrivateGui() { return this.privateGui; }
/* 172 */   public KitManager getKitManager() { return this.kitManager; }
/* 173 */   public KitEditorGui getKitEditor() { return this.kitEditor; }
/* 174 */   public SpectatorManager getSpectators() { return this.spectators; } public ScoreboardManager getScoreboards() {
/* 175 */     return this.scoreboards;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\BedFightPlugin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */