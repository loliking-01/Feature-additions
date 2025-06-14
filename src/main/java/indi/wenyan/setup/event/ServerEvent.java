package indi.wenyan.setup.event;

import indi.wenyan.interpreter.utils.FileLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static indi.wenyan.WenyanNature.LOGGER;
import static indi.wenyan.WenyanNature.MODID;

@EventBusSubscriber(modid = MODID)
public class ServerEvent {
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("道曰：「「问天地好在」」");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(FileLoader.FILE_COMMAND);
    }
}
