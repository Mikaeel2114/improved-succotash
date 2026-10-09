/*    */ package com.example.bedfight.util;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.CopyOption;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.StandardCopyOption;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ import java.util.Set;
/*    */ 
/*    */ public final class WorldFiles {
/* 13 */   private static final Set<String> SKIP = Set.of("uid.dat", "session.lock");
/*    */ 
/*    */ 
/*    */   
/*    */   public static void copy(final Path src, final Path dst) throws IOException {
/* 18 */     Files.walkFileTree(src, new SimpleFileVisitor<Path>()
/*    */         {
/*    */           public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/* 21 */             Files.createDirectories(dst.resolve(src.relativize(dir).toString()), (FileAttribute<?>[])new FileAttribute[0]);
/* 22 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */ 
/*    */           
/*    */           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 27 */             if (!WorldFiles.SKIP.contains(file.getFileName().toString())) {
/* 28 */               Files.copy(file, dst.resolve(src.relativize(file).toString()), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*    */             }
/* 30 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   public static void delete(Path dir) {
/* 36 */     if (!Files.exists(dir, new java.nio.file.LinkOption[0])) {
/*    */       return;
/*    */     }
/*    */     try {
/* 40 */       Files.walkFileTree(dir, new SimpleFileVisitor<Path>()
/*    */           {
/*    */             public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 43 */               Files.deleteIfExists(file);
/* 44 */               return FileVisitResult.CONTINUE;
/*    */             }
/*    */ 
/*    */             
/*    */             public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
/* 49 */               Files.deleteIfExists(d);
/* 50 */               return FileVisitResult.CONTINUE;
/*    */             }
/*    */           });
/* 53 */     } catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\WorldFiles.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */