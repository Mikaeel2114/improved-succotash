/*     */ package com.example.bedfight.hook;
/*     */ 
/*     */ import com.example.bedfight.BedFightPlugin;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public final class PartyHook
/*     */ {
/*  19 */   private static final String[] API_ACCESSORS = new String[] { "getPartyManager", "getPartyAPI", "getAPI", "getPartyService" };
/*  20 */   private static final String[] GET_PARTY = new String[] { "getParty", "getPartyOf", "getPlayerParty" };
/*  21 */   private static final String[] PARTY_MEMBERS = new String[] { "getMembers", "getPlayers", "getOnlineMembers" };
/*  22 */   private static final String[] PARTY_LEADER = new String[] { "getLeader", "getOwner" };
/*  23 */   private static final String[] SPLIT = new String[] { "splitIntoTeams", "splitTeams", "randomTeams", "createTeams" };
/*     */   
/*     */   private final BedFightPlugin plugin;
/*     */   private Object api;
/*     */   private boolean available;
/*     */   
/*     */   public PartyHook(BedFightPlugin plugin) {
/*  30 */     this.plugin = plugin;
/*  31 */     Plugin party = Bukkit.getPluginManager().getPlugin("minestormparty");
/*  32 */     if (party == null || !party.isEnabled()) {
/*  33 */       plugin.getLogger().warning("minestormparty not found - team modes (2v2/3v3) are limited.");
/*     */       return;
/*     */     } 
/*  36 */     Object resolved = null;
/*  37 */     for (String accessor : API_ACCESSORS) {
/*  38 */       Object o = call(party, new String[] { accessor }, new Object[0]);
/*  39 */       if (o != null) {
/*  40 */         resolved = o;
/*     */         break;
/*     */       } 
/*     */     } 
/*  44 */     this.api = (resolved != null) ? resolved : party;
/*  45 */     this.available = true;
/*  46 */     plugin.getLogger().info("Hooked into minestormparty (" + this.api.getClass().getName() + ").");
/*     */   }
/*     */   public boolean isAvailable() {
/*  49 */     return this.available;
/*     */   }
/*     */   
/*     */   public List<Player> getPartyMembers(Player player) {
/*  53 */     List<Player> out = new ArrayList<>();
/*  54 */     if (this.available) {
/*  55 */       Object party = call(this.api, GET_PARTY, new Object[] { player });
/*  56 */       if (party != null) {
/*  57 */         out.addAll(toPlayers(call(party, PARTY_MEMBERS, new Object[0])));
/*     */       }
/*     */     } 
/*  60 */     out.remove(player);
/*  61 */     out.add(0, player);
/*  62 */     return out;
/*     */   }
/*     */   
/*     */   public boolean isLeader(Player player) {
/*  66 */     if (!this.available) {
/*  67 */       return true;
/*     */     }
/*  69 */     Object party = call(this.api, GET_PARTY, new Object[] { player });
/*  70 */     if (party == null) {
/*  71 */       return true;
/*     */     }
/*  73 */     Object leader = call(party, PARTY_LEADER, new Object[0]);
/*  74 */     if (leader instanceof Player) { Player p = (Player)leader;
/*  75 */       return p.getUniqueId().equals(player.getUniqueId()); }
/*     */     
/*  77 */     if (leader instanceof UUID) { UUID id = (UUID)leader;
/*  78 */       return id.equals(player.getUniqueId()); }
/*     */     
/*  80 */     return true;
/*     */   }
/*     */   
/*     */   public List<List<Player>> splitIntoTeams(List<Player> players, int teamSize, int teamCount) {
/*  84 */     if (this.available) {
/*  85 */       Object raw = call(this.api, SPLIT, new Object[] { new ArrayList<>(players), Integer.valueOf(teamSize) });
/*  86 */       List<List<Player>> teams = toTeams(raw);
/*  87 */       if (isValidSplit(teams, players, teamSize, teamCount)) {
/*  88 */         return teams;
/*     */       }
/*     */     } 
/*  91 */     List<Player> shuffled = new ArrayList<>(players);
/*  92 */     Collections.shuffle(shuffled);
/*  93 */     List<List<Player>> result = new ArrayList<>();
/*  94 */     for (int i = 0; i < teamCount; i++) {
/*  95 */       int from = i * teamSize;
/*  96 */       int to = Math.min(from + teamSize, shuffled.size());
/*  97 */       result.add(new ArrayList<>(shuffled.subList(from, to)));
/*     */     } 
/*  99 */     return result;
/*     */   }
/*     */   
/*     */   private static boolean isValidSplit(List<List<Player>> teams, List<Player> players, int size, int count) {
/* 103 */     if (teams.size() != count) {
/* 104 */       return false;
/*     */     }
/* 106 */     Set<UUID> seen = new HashSet<>();
/* 107 */     for (List<Player> t : teams) {
/* 108 */       if (t.size() != size) {
/* 109 */         return false;
/*     */       }
/* 111 */       for (Player p : t) {
/* 112 */         seen.add(p.getUniqueId());
/*     */       }
/*     */     } 
/* 115 */     for (Player p : players) {
/* 116 */       if (!seen.contains(p.getUniqueId())) {
/* 117 */         return false;
/*     */       }
/*     */     } 
/* 120 */     return true;
/*     */   }
/*     */   
/*     */   private static List<Player> toPlayers(Object raw) {
/* 124 */     List<Player> out = new ArrayList<>();
/* 125 */     if (raw instanceof Collection) { Collection<?> c = (Collection)raw;
/* 126 */       for (Object o : c) {
/* 127 */         Player p = null;
/* 128 */         if (o instanceof Player) { Player pl = (Player)o;
/* 129 */           p = pl; }
/* 130 */         else if (o instanceof UUID) { UUID id = (UUID)o;
/* 131 */           p = Bukkit.getPlayer(id); }
/* 132 */         else if (o instanceof String) { String name = (String)o;
/* 133 */           p = Bukkit.getPlayerExact(name); }
/*     */         
/* 135 */         if (p != null && p.isOnline() && !out.contains(p)) {
/* 136 */           out.add(p);
/*     */         }
/*     */       }  }
/*     */     
/* 140 */     return out;
/*     */   }
/*     */   
/*     */   private static List<List<Player>> toTeams(Object raw) {
/* 144 */     List<List<Player>> teams = new ArrayList<>();
/* 145 */     Collection<?> outer = null;
/* 146 */     if (raw instanceof Map) { Map<?, ?> m = (Map<?, ?>)raw;
/* 147 */       outer = m.values(); }
/* 148 */     else if (raw instanceof Collection) { Collection<?> c = (Collection)raw;
/* 149 */       outer = c; }
/*     */     
/* 151 */     if (outer != null) {
/* 152 */       for (Object o : outer) {
/* 153 */         teams.add(toPlayers(o));
/*     */       }
/*     */     }
/* 156 */     return teams;
/*     */   }
/*     */   
/*     */   private static Object call(Object target, String[] names, Object... args) {
/* 160 */     if (target == null) {
/* 161 */       return null;
/*     */     }
/* 163 */     for (String name : names) {
/* 164 */       for (Method m : target.getClass().getMethods()) {
/* 165 */         if (m.getName().equals(name) && m.getParameterCount() == args.length && 
/* 166 */           accepts(m.getParameterTypes(), args)) {
/*     */           try {
/* 168 */             m.setAccessible(true);
/* 169 */             return m.invoke(target, args);
/* 170 */           } catch (ReflectiveOperationException|RuntimeException reflectiveOperationException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 176 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean accepts(Class<?>[] types, Object[] args) {
/* 180 */     for (int i = 0; i < types.length; i++) {
/* 181 */       Class<?> t = types[i];
/* 182 */       if (t == int.class) t = Integer.class; 
/* 183 */       if (t == long.class) t = Long.class; 
/* 184 */       if (t == boolean.class) t = Boolean.class; 
/* 185 */       if (args[i] == null || !t.isAssignableFrom(args[i].getClass())) {
/* 186 */         return false;
/*     */       }
/*     */     } 
/* 189 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfight\hook\PartyHook.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */