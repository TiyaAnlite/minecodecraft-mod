package cn.focot.codelab.minecodecraft.handlers;

import cn.focot.codelab.minecodecraft.helpers.PlayerData;
import cn.focot.codelab.minecodecraft.helpers.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHandler extends AbstractHandler{
    public static void onBlockBreak(Block block, World world, BlockPos pos, BlockState state, ServerPlayerEntity player) {
        PlayerData playerData = PlayerHelper.checkedPlayerData(player);
        playerData.breakBlock();
    }
}
