/*    */ package assets.guncus;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import net.minecraft.client.audio.SoundManager;
/*    */ import net.minecraftforge.client.event.sound.SoundLoadEvent;
/*    */ import net.minecraftforge.event.ForgeSubscribe;
/*    */ 
/*    */ public class GunCusSound
/*    */ {
/* 10 */   public static ArrayList<String> soundFiles = new ArrayList();
/*    */ 
/*    */   @ForgeSubscribe
/*    */   public void forgeSubscribe(SoundLoadEvent event)
/*    */   {
/* 15 */     String[] sounds = { "shoot_normal.ogg", "inground.ogg", "knife.ogg", "shoot_silenced.ogg", "shoot_sniper.ogg", "reload.ogg", "click.ogg", "reload_tube.ogg", "reload_rpg.ogg" };
/* 16 */     for (String s : soundFiles)
/*    */     {
/* 18 */       GunCus.log("Found custom sound file: \"" + s + "\"");
/* 19 */       event.manager.addSound("minecraft:" + s);
/*    */     }
/* 21 */     for (String s : sounds)
/*    */     {
/* 23 */       event.manager.addSound("guncus:" + s);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void addSound(String s)
/*    */   {
/* 29 */     soundFiles.add(s);
/*    */   }
/*    */ }

/* Location:           C:\Users\Nate\Desktop\Mod\GunCusClass.zip
 * Qualified Name:     assets.guncus.GunCusSound
 * JD-Core Version:    0.6.2
 */