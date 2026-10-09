/*    */ package com.example.bedfight.util;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Titles
/*    */ {
/*    */   private static boolean initialised;
/*    */   private static boolean working;
/*    */   private static Constructor<?> timesCtor;
/*    */   private static Constructor<?> textCtor;
/*    */   private static Object titleAction;
/*    */   private static Object subtitleAction;
/*    */   private static Method serializer;
/*    */   private static Method getHandle;
/*    */   private static Field connection;
/*    */   private static Method sendPacket;
/*    */   
/*    */   private static void init() {
/* 25 */     if (initialised) {
/*    */       return;
/*    */     }
/* 28 */     initialised = true;
/*    */     try {
/* 30 */       String pkg = Bukkit.getServer().getClass().getPackage().getName();
/* 31 */       String ver = pkg.substring(pkg.lastIndexOf('.') + 1);
/* 32 */       String nms = "net.minecraft.server." + ver + ".";
/* 33 */       Class<?> packet = Class.forName(nms + "PacketPlayOutTitle");
/* 34 */       Class<?> action = Class.forName(nms + "PacketPlayOutTitle$EnumTitleAction");
/* 35 */       Class<?> chat = Class.forName(nms + "IChatBaseComponent");
/* 36 */       Class<?> chatSerializer = Class.forName(nms + "IChatBaseComponent$ChatSerializer");
/* 37 */       Class<?> basePacket = Class.forName(nms + "Packet");
/* 38 */       timesCtor = packet.getConstructor(new Class[] { int.class, int.class, int.class });
/* 39 */       textCtor = packet.getConstructor(new Class[] { action, chat });
/* 40 */       titleAction = constant(action, "TITLE");
/* 41 */       subtitleAction = constant(action, "SUBTITLE");
/* 42 */       serializer = chatSerializer.getMethod("a", new Class[] { String.class });
/* 43 */       getHandle = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer").getMethod("getHandle", new Class[0]);
/* 44 */       connection = Class.forName(nms + "EntityPlayer").getField("playerConnection");
/* 45 */       sendPacket = Class.forName(nms + "PlayerConnection").getMethod("sendPacket", new Class[] { basePacket });
/* 46 */       working = (titleAction != null && subtitleAction != null);
/* 47 */     } catch (Exception ex) {
/* 48 */       Bukkit.getLogger().warning("[BedFight] Title packets unavailable, using chat fallback: " + String.valueOf(ex));
/* 49 */       working = false;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static Object constant(Class<?> enumClass, String name) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: invokevirtual getEnumConstants : ()[Ljava/lang/Object;
/*    */     //   4: astore_2
/*    */     //   5: aload_2
/*    */     //   6: arraylength
/*    */     //   7: istore_3
/*    */     //   8: iconst_0
/*    */     //   9: istore #4
/*    */     //   11: iload #4
/*    */     //   13: iload_3
/*    */     //   14: if_icmpge -> 47
/*    */     //   17: aload_2
/*    */     //   18: iload #4
/*    */     //   20: aaload
/*    */     //   21: astore #5
/*    */     //   23: aload #5
/*    */     //   25: checkcast java/lang/Enum
/*    */     //   28: invokevirtual name : ()Ljava/lang/String;
/*    */     //   31: aload_1
/*    */     //   32: invokevirtual equals : (Ljava/lang/Object;)Z
/*    */     //   35: ifeq -> 41
/*    */     //   38: aload #5
/*    */     //   40: areturn
/*    */     //   41: iinc #4, 1
/*    */     //   44: goto -> 11
/*    */     //   47: aconst_null
/*    */     //   48: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #54	-> 0
/*    */     //   #55	-> 23
/*    */     //   #56	-> 38
/*    */     //   #54	-> 41
/*    */     //   #59	-> 47
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   23	18	5	o	Ljava/lang/Object;
/*    */     //   0	49	0	enumClass	Ljava/lang/Class;
/*    */     //   0	49	1	name	Ljava/lang/String;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	49	0	enumClass	Ljava/lang/Class<*>;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static Object component(String text) throws Exception {
/* 63 */     String json = "{\"text\":\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
/* 64 */     return serializer.invoke(null, new Object[] { json });
/*    */   }
/*    */   
/*    */   public static void send(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
/* 68 */     init();
/* 69 */     if (working) {
/*    */       try {
/* 71 */         Object conn = connection.get(getHandle.invoke(p, new Object[0]));
/* 72 */         sendPacket.invoke(conn, new Object[] { timesCtor.newInstance(new Object[] { Integer.valueOf(fadeIn), Integer.valueOf(stay), Integer.valueOf(fadeOut) }) });
/* 73 */         sendPacket.invoke(conn, new Object[] { textCtor.newInstance(new Object[] { subtitleAction, component((subtitle == null) ? "" : subtitle) }) });
/* 74 */         sendPacket.invoke(conn, new Object[] { textCtor.newInstance(new Object[] { titleAction, component((title == null) ? "" : title) }) });
/*    */         return;
/* 76 */       } catch (Exception ex) {
/* 77 */         working = false;
/* 78 */         Bukkit.getLogger().warning("[BedFight] Title sending failed, using chat fallback: " + String.valueOf(ex));
/*    */       } 
/*    */     }
/* 81 */     p.sendMessage(title + title);
/*    */   }
/*    */ }


/* Location:              C:\Users\nasle javan\Downloads\BedFight-1.2.1.jar!\com\example\bedfigh\\util\Titles.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */