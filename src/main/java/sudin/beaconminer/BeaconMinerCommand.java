package sudin.beaconminer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BeaconMinerCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("beaconminer")
                    .then(Commands.literal("enable")
                            .requires(BeaconMinerCommand::isOp)
                            .executes(ctx -> {
                                BeaconMinerEvents.enabled = true;
                                ctx.getSource().sendSuccess(() -> Component.literal("BeaconMiner enabled"), true);
                                return 1;
                            }))
                    .then(Commands.literal("disable")
                            .requires(BeaconMinerCommand::isOp)
                            .executes(ctx -> {
                                BeaconMinerEvents.enabled = false;
                                ctx.getSource().sendSuccess(() -> Component.literal("BeaconMiner disabled"), true);
                                return 1;
                            })));
        });
    }

    private static boolean isOp(net.minecraft.commands.CommandSourceStack src) {
        ServerPlayer player = src.getPlayer();
        return player == null || src.getServer().getPlayerList().isOp(player.nameAndId());
    }
}
