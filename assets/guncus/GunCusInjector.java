/*    */ package assets.guncus;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.lang.reflect.Method;
/*    */ import java.net.URI;
/*    */ 
/*    */ public class GunCusInjector
/*    */ {
/*    */   public ClassLoader classloader;
/*    */   public Method method;
/*    */ 
/*    */   public GunCusInjector(ClassLoader classloader, Method method)
/*    */   {
/* 21 */     this.classloader = classloader;
/* 22 */     this.method = method;
/*    */   }
/*    */ 
/*    */   public void addToClassPath(File file)
/*    */   {
/*    */     try
/*    */     {
/* 29 */       this.method.invoke(this.classloader, new Object[] { file.toURI().toURL() });
/*    */     }
/*    */     catch (NullPointerException e) {
/*    */     }
/*    */     catch (Exception e) {
/* 34 */       GunCus.log("Failed to add some textures to class path");
/* 35 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusInjector
 * JD-Core Version:    0.6.2
 */