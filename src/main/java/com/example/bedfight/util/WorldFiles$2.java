/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class null
/*    */   extends SimpleFileVisitor<Path>
/*    */ {
/*    */   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 43 */     Files.deleteIfExists(file);
/* 44 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
/* 49 */     Files.deleteIfExists(d);
/* 50 */     return FileVisitResult.CONTINUE;
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\WorldFiles$2.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */