package net.kris.gc_caller;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.TextComponentTranslation;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class GCCaller {

    protected static Logger logger;

    @Instance
    public static GCCaller instance;

    private static KeyBinding keyBinding;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        keyBinding = new KeyBinding("category.gc_caller.name", 0, "key.gc_caller.call_gc");
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public void onAvailable(FMLLoadCompleteEvent event) {
        GCCallerThread.gc();
    }

    @SubscribeEvent
    public void onKeyPressed(InputUpdateEvent event) {
        if (keyBinding.isPressed()) {
            if (event.getEntityPlayer() == null) {
                GCCallerThread.gc();
            } else if (GCCallerThread.gc())
                event.getEntityPlayer().sendMessage(new TextComponentTranslation("message.gc_caller.gc_called"));
            else
                event.getEntityPlayer()
                        .sendMessage(new TextComponentTranslation("message.gc_caller.gc_already_called"));
        }
    }
}
