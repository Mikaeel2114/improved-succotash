/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.Reader;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.nio.file.Files;
/*    */ import org.bukkit.configuration.file.YamlConfiguration;
/*    */ 
/*    */ 
/*    */ public final class YamlFiles
/*    */ {
/*    */   public static YamlConfiguration load(File file) {
/* 16 */     YamlConfiguration y = new YamlConfiguration(); 
/* 17 */     try { Reader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8); 
/* 18 */       try { y.load(r);
/* 19 */         r.close(); } catch (Throwable throwable) { try { r.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Exception ex)
/* 20 */     { throw new IllegalStateException("Could not read " + file.getName() + ": " + ex.getMessage(), ex); }
/*    */     
/* 22 */     return y;
/*    */   }
/*    */   
/*    */   public static void save(YamlConfiguration y, File file) throws IOException {
/* 26 */     file.getParentFile().mkdirs();
/* 27 */     Files.write(file.toPath(), y.saveToString().getBytes(StandardCharsets.UTF_8), new java.nio.file.OpenOption[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\YamlFiles.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */