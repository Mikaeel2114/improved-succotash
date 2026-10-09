/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.CopyOption;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.StandardCopyOption;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class null
/*    */   extends SimpleFileVisitor<Path>
/*    */ {
/*    */   public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/* 21 */     Files.createDirectories(dst.resolve(src.relativize(dir).toString()), (FileAttribute<?>[])new FileAttribute[0]);
/* 22 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 27 */     if (!WorldFiles.SKIP.contains(file.getFileName().toString())) {
/* 28 */       Files.copy(file, dst.resolve(src.relativize(file).toString()), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*    */     }
/* 30 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\WorldFiles$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */