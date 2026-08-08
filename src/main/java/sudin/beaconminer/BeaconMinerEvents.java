package sudin.beaconminer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BeaconMinerEvents {

    public static volatile boolean enabled = false;

    private static final Set<net.minecraft.world.level.block.Block> VALID_MINERALS = Set.of(
            Blocks.IRON_BLOCK,
            Blocks.GOLD_BLOCK,
            Blocks.EMERALD_BLOCK,
            Blocks.DIAMOND_BLOCK,
            Blocks.NETHERITE_BLOCK
    );

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClientSide()) return;
            if (!enabled) return;
            if (state.getBlock() != Blocks.BEACON) return;
            if (!player.isShiftKeyDown()) return;

            int levels = getPyramidLevels(world, pos.getX(), pos.getY(), pos.getZ());
            if (levels <= 0) return; // not a valid pyramid

            boolean creative = player.getAbilities().instabuild;
            List<BlockPos> toBreak = new ArrayList<>();

            for (int layer = 1; layer <= levels; layer++) {
                int y = pos.getY() - layer;
                int radius = layer;
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos check = new BlockPos(pos.getX() + dx, y, pos.getZ() + dz);
                        BlockState checkState = world.getBlockState(check);
                        if (VALID_MINERALS.contains(checkState.getBlock())) {
                            toBreak.add(check.immutable());
                        }
                    }
                }
            }

            for (BlockPos bp : toBreak) {
                BlockState bs = world.getBlockState(bp);
                if (creative) {
                    world.removeBlock(bp, false);
                } else {
                    ItemStack drop = new ItemStack(bs.getBlock());
                    world.removeBlock(bp, false);
                    ItemEntity itemEntity = new ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                    world.addFreshEntity(itemEntity);
                }
            }
        });
    }

    // Mirrors vanilla BeaconBlockEntity.updateBase logic
    private static int getPyramidLevels(net.minecraft.world.level.Level world, int x, int y, int z) {
        int levels = 0;

        for (int step = 1; step <= 4; levels = step++) {
            int ly = y - step;
            if (ly < world.getMinY()) break;

            boolean isOk = true;
            for (int lx = x - step; lx <= x + step && isOk; lx++) {
                for (int lz = z - step; lz <= z + step; lz++) {
                    if (!world.getBlockState(new BlockPos(lx, ly, lz)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        isOk = false;
                        break;
                    }
                }
            }
            if (!isOk) break;
        }

        return levels;
    }
}
