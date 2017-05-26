package eyeq.crystallizard.entity.ai;

import eyeq.crystallizard.entity.passive.EntityCrystalLizard;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIEatStone extends EntityAIBase {
    private final EntityCrystalLizard entity;
    private final World world;
    int eatingTimer;

    public EntityAIEatStone(EntityCrystalLizard entity) {
        this.entity = entity;
        this.world = entity.world;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if(entity.getRNG().nextInt(entity.isChild() ? 50 : 1000) != 0) {
            return false;
        }
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        Block block = world.getBlockState(pos.down()).getBlock();
        return block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.MOSSY_COBBLESTONE;
    }

    @Override
    public void startExecuting() {
        this.eatingTimer = 40;
        this.world.setEntityState(this.entity, (byte) 10);
        this.entity.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        this.eatingTimer = 0;
    }

    @Override
    public boolean continueExecuting() {
        return this.eatingTimer > 0;
    }

    public int getEatingTimer() {
        return this.eatingTimer;
    }

    @Override
    public void updateTask() {
        this.eatingTimer = Math.max(0, this.eatingTimer - 1);

        if(this.eatingTimer == 4) {
            BlockPos pos = new BlockPos(this.entity.posX, this.entity.posY, this.entity.posZ).down();
            Block block = this.world.getBlockState(pos).getBlock();
            if(block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.MOSSY_COBBLESTONE) {
                if(this.world.getGameRules().getBoolean("mobGriefing")) {
                    this.world.playEvent(2001, pos, Block.getIdFromBlock(block));
                    this.world.setBlockState(pos, Blocks.GRAVEL.getDefaultState(), 2);
                }
                this.entity.eatStoneBonus(block == Blocks.MOSSY_COBBLESTONE);
            }
        }
    }
}
