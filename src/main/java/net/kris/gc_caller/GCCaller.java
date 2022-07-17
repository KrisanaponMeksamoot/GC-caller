package net.kris.gc_caller;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("gc_caller")
public class GCCaller {
    // Directly reference a log4j logger.
    protected static final Logger LOGGER = LogManager.getLogger();

    private static KeyBinding keyBinding;

    public GCCaller() {
        GCCallerThread.getInstance();
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        keyBinding = new KeyBinding("key.gc_caller.call_gc", GLFW.GLFW_KEY_UNKNOWN, "category.gc_caller.name");
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public void onKeyPressed(InputUpdateEvent event) {
        if (keyBinding.isDown()) {
            if (event.getPlayer() == null) {
                GCCallerThread.gc();
            } else if (GCCallerThread.gc()) {
                event.getPlayer().displayClientMessage(new TranslationTextComponent("message.gc_caller.gc_called"),
                        true);
            } else {
                event.getPlayer().displayClientMessage(
                        new TranslationTextComponent("message.gc_caller.gc_already_called"), true);
            }
        }
    }

    public void onAvailable(FMLLoadCompleteEvent event) {
        GCCallerThread.gc();
    }
}
