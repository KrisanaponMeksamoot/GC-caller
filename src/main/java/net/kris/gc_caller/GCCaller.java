package net.kris.gc_caller;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class GCCaller {

    protected static Logger LOGGER;

    @Instance
    public static GCCaller instance;

    protected static KeyBinding keyBinding;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        keyBinding = new KeyBinding("key.gc_caller.call_gc", 0, "category.gc_caller.name");
        ClientRegistry.registerKeyBinding(keyBinding);
        GCCallerThread.getInstance();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @EventHandler
    public void onAvailable(FMLLoadCompleteEvent event) {
        GCCallerThread.gc();
    }

    @SubscribeEvent
    public void onKeyPressed(InputUpdateEvent event) {
        if (GCCaller.keyBinding.isPressed()) {
            if (event.getEntityPlayer() == null) {
                GCCallerThread.gc();
            } else if (GCCallerThread.gc())
                event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("message.gc_caller.gc_called"),
                        true);
            else
                event.getEntityPlayer()
                        .sendStatusMessage(new TextComponentTranslation("message.gc_caller.gc_already_called"), true);
        }
    }

    @SubscribeEvent
    public void playerLogin(PlayerLoggedInEvent event) {
        GCCallerThread.gc();
    }

    @SubscribeEvent
    public void playerLogout(PlayerLoggedOutEvent event) {
        GCCallerThread.gc();
    }
}
