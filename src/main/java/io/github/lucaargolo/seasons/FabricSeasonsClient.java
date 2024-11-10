package io.github.lucaargolo.seasons;

import io.github.lucaargolo.seasons.payload.ConfigSyncPacket;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import io.github.lucaargolo.seasons.utils.CompatWarnState;
import io.github.lucaargolo.seasons.utils.ModConfig;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static io.github.lucaargolo.seasons.FabricSeasons.CONFIG;
import static io.github.lucaargolo.seasons.FabricSeasons.MOD_ID;
import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class FabricSeasonsClient implements ClientModInitializer {

    private static boolean isServerConfig = false;
    private static ModConfig clientConfig = null;
    private static final Map<RegistryKey<World>, Season> lastRenderedSeasonMap = new HashMap<>();

    public static final Map<BakedModel, Map<Season, BakedModel>> originalToSeasonModelMap = new HashMap<>();

    @Override
    public void onInitializeClient() {
        clientConfig = FabricSeasons.CONFIG;
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new GrassSeasonColors());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FoliageSeasonColors());
        
        ClientTickEvents.END_WORLD_TICK.register((clientWorld) -> {
            if (FabricSeasons.getCurrentSeason(clientWorld) != lastRenderedSeasonMap.get(clientWorld.getRegistryKey())) {
                lastRenderedSeasonMap.put(clientWorld.getRegistryKey(), FabricSeasons.getCurrentSeason(clientWorld));
                MinecraftClient.getInstance().worldRenderer.reload();
            }
        });
        
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, (payload, handler) -> {
            String configJson = payload.config();
            handler.client().execute(() -> {
                FabricSeasons.CONFIG = FabricSeasons.GSON.fromJson(configJson, ModConfig.class);
                isServerConfig = true;
                FabricSeasons.LOGGER.info("[" + MOD_NAME + "] Received dedicated server config.");
            });
        });
        
        
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (CONFIG.shouldNotifyCompat()) {
                CompatWarnState.join(client);
            }
            if (!client.isIntegratedServerRunning()) {
                FabricSeasons.LOGGER.info("[" + MOD_NAME + "] Joined dedicated server, asking for config.");
                ClientPlayNetworking.send(new ConfigSyncPacket("request"));
            }
        });
        
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent((container) -> {
            ResourceManagerHelper.registerBuiltinResourcePack(FabricSeasons.identifier("seasonal_lush_caves"), container, Text.literal("Seasonal Lush Caves"), ResourcePackActivationType.DEFAULT_ENABLED);
        });
    }
}
