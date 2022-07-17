package net.kris.gc_caller;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCCaller implements ClientModInitializer {
	public static final String MODID = "gc_caller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	private KeyBinding keyBinding;

	@Override
	public void onInitializeClient() {
		GCCallerThread.getInstance();
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.gc_caller.call_gc",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.gc_caller.name"));
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			GCCallerThread.gc();
		});
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			GCCallerThread.gc();
		});
	}

	private void onEndClientTick(MinecraftClient client) {
		while (keyBinding.wasPressed()) {
			if (client.player == null) {
				GCCallerThread.gc();
			} else if (GCCallerThread.gc()) {
				client.player.sendMessage(Text.translatable("message.gc_caller.gc_called"), true);
			} else {
				client.player.sendMessage(Text.translatable("message.gc_caller.gc_already_called"), true);
			}
		}
	}
}
