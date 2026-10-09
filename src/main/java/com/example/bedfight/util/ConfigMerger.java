/*     */ package com.example.bedfight.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ConfigMerger
/*     */ {
/*  40 */   private static final Pattern KEY = Pattern.compile("^(\\s*)([^\\s#\\-][^:#]*?):(?:\\s.*)?$");
/*     */ 
/*     */ 
/*     */   
/*     */   public static void merge(JavaPlugin plugin, String name) {
/*     */     
/*  46 */     try { File file = new File(plugin.getDataFolder(), name);
/*  47 */       if (!file.isFile()) {
/*  48 */         plugin.saveResource(name, false);
/*     */         return;
/*     */       } 
/*  51 */       InputStream in = plugin.getResource(name); 
/*  52 */       try { if (in == null)
/*     */         
/*     */         { 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  88 */           if (in != null) in.close();  return; }  String defaultText = new String(in.readAllBytes(), StandardCharsets.UTF_8); YamlConfiguration defaults = new YamlConfiguration(); defaults.loadFromString(defaultText); YamlConfiguration user = YamlFiles.load(file); Map<String, Object> missing = new LinkedHashMap<>(); for (String key : defaults.getKeys(true)) { if (defaults.isConfigurationSection(key) || user.contains(key) || blockedByScalar(user, key)) continue;  missing.put(key, defaults.get(key)); }  if (missing.isEmpty()) { if (in != null) in.close();  return; }  String userText = Files.readString(file.toPath(), StandardCharsets.UTF_8); String merged = mergeText(plugin.getDescription().getVersion(), userText, defaultText, user, missing.keySet()); if (merged != null) { writeFile(file, merged); plugin.getLogger().info(name + ": added " + name + " missing option(s): " + missing.size()); if (in != null) in.close();  return; }  Files.copy(file.toPath(), (new File(plugin.getDataFolder(), name + ".bak")).toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }); for (Map.Entry<String, Object> e : missing.entrySet()) user.set(e.getKey(), e.getValue());  YamlFiles.save(user, file); plugin.getLogger().info(name + ": added " + name + " missing option(s). Old file saved as " + missing.size() + ".bak"); if (in != null) in.close();  } catch (Throwable throwable) { if (in != null)
/*  89 */           try { in.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (Exception ex)
/*  90 */     { plugin.getLogger().log(Level.WARNING, "Could not merge defaults into " + name + ": " + ex.getMessage(), ex); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String mergeText(String version, String userText, String defaultText, YamlConfiguration user, Set<String> missingLeaves) {
/*     */     try {
/* 100 */       String nl = userText.contains("\r\n") ? "\r\n" : "\n";
/* 101 */       List<String> u = lines(userText);
/* 102 */       List<String> d = lines(defaultText);
/* 103 */       List<Node> userNodes = parse(u);
/* 104 */       Map<String, Node> userIndex = index(userNodes);
/* 105 */       Map<String, Node> defIndex = index(parse(d));
/*     */ 
/*     */       
/* 108 */       Set<String> roots = new LinkedHashSet<>();
/* 109 */       for (String leaf : missingLeaves) {
/* 110 */         StringBuilder path = new StringBuilder();
/* 111 */         for (String part : leaf.split("\\.")) {
/* 112 */           if (path.length() > 0) {
/* 113 */             path.append('.');
/*     */           }
/* 115 */           path.append(part);
/* 116 */           if (!user.contains(path.toString())) {
/* 117 */             roots.add(path.toString());
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 123 */       Map<Integer, List<String>> inserts = new TreeMap<>();
/* 124 */       List<String> tail = new ArrayList<>();
/* 125 */       for (String root : roots) {
/* 126 */         Node dn = defIndex.get(root);
/* 127 */         if (dn == null) {
/* 128 */           return null;
/*     */         }
/* 130 */         List<String> block = new ArrayList<>(d.subList(dn.commentFrom, dn.end + 1));
/* 131 */         int dot = root.lastIndexOf('.');
/* 132 */         if (dot < 0) {
/* 133 */           reindent(block, -dn.indent);
/* 134 */           if (!tail.isEmpty()) {
/* 135 */             tail.add("");
/*     */           }
/* 137 */           tail.addAll(block); continue;
/*     */         } 
/* 139 */         String parent = root.substring(0, dot);
/* 140 */         Node pn = userIndex.get(parent);
/* 141 */         if (pn == null) {
/* 142 */           return null;
/*     */         }
/* 144 */         int childIndent = pn.indent + 2;
/* 145 */         for (Node n : userNodes) {
/* 146 */           if (parentOf(n.path).equals(parent)) {
/* 147 */             childIndent = n.indent;
/*     */             break;
/*     */           } 
/*     */         } 
/* 151 */         reindent(block, childIndent - dn.indent);
/* 152 */         ((List<String>)inserts.computeIfAbsent(Integer.valueOf(pn.end + 1), k -> new ArrayList())).addAll(block);
/*     */       } 
/*     */ 
/*     */       
/* 156 */       List<String> out = new ArrayList<>(u);
/* 157 */       while (!out.isEmpty() && ((String)out.get(out.size() - 1)).isBlank()) {
/* 158 */         out.remove(out.size() - 1);
/*     */       }
/* 160 */       List<Integer> positions = new ArrayList<>(inserts.keySet());
/* 161 */       Collections.reverse(positions);
/* 162 */       for (Iterator<Integer> iterator = positions.iterator(); iterator.hasNext(); ) { int pos = ((Integer)iterator.next()).intValue();
/* 163 */         out.addAll(Math.min(pos, out.size()), inserts.get(Integer.valueOf(pos))); }
/*     */       
/* 165 */       if (!tail.isEmpty()) {
/* 166 */         out.add("");
/* 167 */         out.add("# --- Added automatically by BedFight " + version + " ---");
/* 168 */         out.addAll(tail);
/*     */       } 
/* 170 */       String result = String.join(nl, (Iterable)out) + String.join(nl, (Iterable)out);
/*     */ 
/*     */       
/* 173 */       YamlConfiguration check = new YamlConfiguration();
/* 174 */       check.loadFromString(result);
/* 175 */       for (String leaf : missingLeaves) {
/* 176 */         if (!check.contains(leaf)) {
/* 177 */           return null;
/*     */         }
/*     */       } 
/* 180 */       for (String key : user.getKeys(true)) {
/* 181 */         if (!user.isConfigurationSection(key) && !Objects.equals(check.get(key), user.get(key))) {
/* 182 */           return null;
/*     */         }
/*     */       } 
/* 185 */       return result;
/* 186 */     } catch (Exception ex) {
/* 187 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class Node {
/*     */     String path;
/*     */     int indent;
/*     */     int start;
/*     */     int end;
/*     */     int commentFrom;
/*     */   }
/*     */   
/*     */   private static List<Node> parse(List<String> lines) {
/* 200 */     List<Node> nodes = new ArrayList<>();
/* 201 */     List<Node> stack = new ArrayList<>();
/* 202 */     for (int i = 0; i < lines.size(); i++) {
/* 203 */       Matcher m = KEY.matcher(lines.get(i));
/* 204 */       if (m.matches()) {
/*     */ 
/*     */         
/* 207 */         int indent = m.group(1).length();
/* 208 */         String key = m.group(2).trim();
/* 209 */         while (!stack.isEmpty() && ((Node)stack.get(stack.size() - 1)).indent >= indent) {
/* 210 */           stack.remove(stack.size() - 1);
/*     */         }
/* 212 */         Node n = new Node();
/* 213 */         n.path = stack.isEmpty() ? key : (((Node)stack.get(stack.size() - 1)).path + "." + ((Node)stack.get(stack.size() - 1)).path);
/* 214 */         n.indent = indent;
/* 215 */         n.start = i;
/* 216 */         nodes.add(n);
/* 217 */         stack.add(n);
/*     */       } 
/* 219 */     }  for (int k = 0; k < nodes.size(); k++) {
/* 220 */       Node n = nodes.get(k);
/* 221 */       int limit = lines.size();
/* 222 */       for (int j = k + 1; j < nodes.size(); j++) {
/* 223 */         if (((Node)nodes.get(j)).indent <= n.indent) {
/* 224 */           limit = ((Node)nodes.get(j)).start;
/*     */           break;
/*     */         } 
/*     */       } 
/* 228 */       int end = limit - 1;
/* 229 */       while (end > n.start && isBlankOrComment(lines.get(end))) {
/* 230 */         end--;
/*     */       }
/* 232 */       n.end = end;
/* 233 */       int from = n.start;
/* 234 */       while (from > 0 && ((String)lines.get(from - 1)).trim().startsWith("#")) {
/* 235 */         from--;
/*     */       }
/* 237 */       n.commentFrom = from;
/*     */     } 
/* 239 */     return nodes;
/*     */   }
/*     */   
/*     */   private static Map<String, Node> index(List<Node> nodes) {
/* 243 */     Map<String, Node> map = new LinkedHashMap<>();
/* 244 */     for (Node n : nodes) {
/* 245 */       map.putIfAbsent(n.path, n);
/*     */     }
/* 247 */     return map;
/*     */   }
/*     */   
/*     */   private static String parentOf(String path) {
/* 251 */     int dot = path.lastIndexOf('.');
/* 252 */     return (dot < 0) ? "" : path.substring(0, dot);
/*     */   }
/*     */   
/*     */   private static boolean isBlankOrComment(String line) {
/* 256 */     String t = line.trim();
/* 257 */     return (t.isEmpty() || t.startsWith("#"));
/*     */   }
/*     */   
/*     */   private static List<String> lines(String text) {
/* 261 */     return new ArrayList<>(Arrays.asList(text.split("\r?\n", -1)));
/*     */   }
/*     */   
/*     */   private static void reindent(List<String> block, int delta) {
/* 265 */     if (delta == 0) {
/*     */       return;
/*     */     }
/* 268 */     for (int i = 0; i < block.size(); i++) {
/* 269 */       String l = block.get(i);
/* 270 */       if (l.isBlank()) {
/* 271 */         block.set(i, "");
/* 272 */       } else if (delta > 0) {
/* 273 */         block.set(i, " ".repeat(delta) + " ".repeat(delta));
/*     */       } else {
/* 275 */         int strip = 0;
/* 276 */         while (strip < -delta && strip < l.length() && l.charAt(strip) == ' ') {
/* 277 */           strip++;
/*     */         }
/* 279 */         block.set(i, l.substring(strip));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeFile(File file, String text) throws IOException {
/* 285 */     Path tmp = file.toPath().resolveSibling(file.getName() + ".tmp");
/* 286 */     Files.writeString(tmp, text, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
/* 287 */     Files.move(tmp, file.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean blockedByScalar(YamlConfiguration user, String key) {
/* 292 */     int idx = key.indexOf('.');
/* 293 */     while (idx > 0) {
/* 294 */       String parent = key.substring(0, idx);
/* 295 */       if (user.contains(parent) && !user.isConfigurationSection(parent)) {
/* 296 */         return true;
/*     */       }
/* 298 */       idx = key.indexOf('.', idx + 1);
/*     */     } 
/* 300 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\ConfigMerger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */