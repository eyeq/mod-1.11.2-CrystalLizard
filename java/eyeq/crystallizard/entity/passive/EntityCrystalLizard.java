package eyeq.crystallizard.entity.passive;

import com.google.common.collect.Sets;
import eyeq.crystallizard.CrystalLizard;
import eyeq.crystallizard.entity.ai.EntityAIEatStone;
import eyeq.util.entity.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;
import java.util.Set;

public class EntityCrystalLizard extends EntityAnimal {
    public enum Crystal {
        COAL(0, Blocks.COAL_BLOCK, Items.COAL),
        IRON(1, Blocks.IRON_BLOCK, Items.IRON_INGOT),
        GOLD(2, Blocks.GOLD_BLOCK, Items.GOLD_INGOT),
        REDSTONE(3, Blocks.REDSTONE_BLOCK, Items.REDSTONE, 5, 0),
        LAPIS(4, Blocks.LAPIS_BLOCK, Items.DYE, 8, EnumDyeColor.BLUE.getDyeDamage()),
        GLOWSTONE(5, Blocks.GLOWSTONE, Items.GLOWSTONE_DUST, 4, 0),
        DIAMOND(6, Blocks.DIAMOND_BLOCK, Items.DIAMOND),
        EMERALD(7, Blocks.EMERALD_BLOCK, Items.EMERALD),
        QUARTZ(8, Blocks.QUARTZ_BLOCK, Items.QUARTZ);

        private static final Crystal[] DOMINANT_TO_ENUM = new Crystal[values().length];

        private final int dominant;
        private final IBlockState render;
        private final Item drop;
        private final int dropAmount;
        private final int dropMeta;

        static {
            for(Crystal crystal : values()) {
                DOMINANT_TO_ENUM[crystal.dominant] = crystal;
            }
        }

        public static Crystal dominantOf(int dominant) {
            return DOMINANT_TO_ENUM[dominant];
        }

        public static void entityDropItem(Entity entity, Crystal a, Crystal b) {
            if(b.isDominant(a)) {
                Crystal temp = a;
                a = b;
                b = temp;
            }
            if(a == COAL && b == IRON) {
                IRON.entityDropItem(entity, 2);
                return;
            }
            if(a == COAL && b == DIAMOND) {
                DIAMOND.entityDropItem(entity, 6);
                return;
            }

            if(a == COAL && b == GLOWSTONE) {
                GLOWSTONE.entityDropItem(entity, 1);
                return;
            }

            if(a == REDSTONE && b == GLOWSTONE) {
                REDSTONE.entityDropItem(entity, REDSTONE.dropAmount);
                GLOWSTONE.entityDropItem(entity, GLOWSTONE.dropAmount);
                return;
            }
            if(a == LAPIS && b == EMERALD) {
                entity.entityDropItem(new ItemStack(Items.NETHER_STAR), 1.0F);
                return;
            }

            if(a != REDSTONE && b == REDSTONE) {
                a.entityDropItem(entity, 2);
                return;
            }
            a.entityDropItem(entity, a.dropAmount);
        }

        Crystal(int dominant, Block render, Item drop) {
            this(dominant, render, drop, 1, 0);
        }

        Crystal(int dominant, Block render, Item drop, int dropAmount, int dropMeta) {
            this.dominant = dominant;
            this.render = render.getDefaultState();
            this.drop = drop;
            this.dropAmount = dropAmount;
            this.dropMeta = dropMeta;
        }

        public IBlockState getRender() {
            return render;
        }

        public boolean isDominant(Crystal crystal) {
            return this.dominant <= crystal.dominant;
        }

        public ItemStack getDrop() {
            return new ItemStack(drop, 1, dropMeta);
        }

        public void entityDropItem(Entity entity, int size) {
            for(int i = 0; i < size; i++) {
                entity.entityDropItem(this.getDrop(), 1.0F);
            }
        }
    }

    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(EntityCrystalLizard.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> CRYSTAL_0 = EntityDataManager.createKey(EntityCrystalLizard.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> CRYSTAL_1 = EntityDataManager.createKey(EntityCrystalLizard.class, DataSerializers.BYTE);

    private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(Item.getItemFromBlock(Blocks.STONE), Item.getItemFromBlock(Blocks.COBBLESTONE), Item.getItemFromBlock(Blocks.MOSSY_COBBLESTONE));

    private int growTimer;
    private EntityAIEatStone entityAIEatStone;

    public static Crystal getRandomCrystal(Random rand) {
        int n = rand.nextInt(100);
        if(n < 50) {
            return Crystal.COAL;
        }
        if(n < 65) {
            return Crystal.IRON;
        }
        if(n < 75) {
            return Crystal.GOLD;
        }
        if(n < 85) {
            return Crystal.REDSTONE;
        }
        if(n < 95) {
            return Crystal.LAPIS;
        }
        if(n < 99) {
            return Crystal.DIAMOND;
        }
        return Crystal.EMERALD;
    }

    public static Crystal getRandomCrystalNether(Random rand) {
        int n = rand.nextInt(100);
        if(n < 15) {
            return Crystal.QUARTZ;
        }
        if(n < 20) {
            return Crystal.GLOWSTONE;
        }
        return null;
    }

    public EntityCrystalLizard(World world) {
        super(world);
        this.setSize(0.7F, 0.5F);

        Crystal a = getRandomCrystal(rand);
        Crystal b = getRandomCrystal(rand);
        if(dimension == -1) {
            Crystal temp = getRandomCrystalNether(rand);
            if(temp != null) {
                if(rand.nextBoolean()) {
                    a = temp;
                } else {
                    b = temp;
                }
            }
        }
        this.setCrystal(a, b);
    }

    @Override
    public void setScaleForAge(boolean isChild) {
        this.setScale(1.0F);
    }

    @Override
    protected void initEntityAI() {
        this.entityAIEatStone = new EntityAIEatStone(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1, false, TEMPTATION_ITEMS));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
        this.tasks.addTask(5, this.entityAIEatStone);
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected void updateAITasks() {
        this.growTimer = this.entityAIEatStone.getEatingTimer();
        super.updateAITasks();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHEARED, false);
        this.dataManager.register(CRYSTAL_0, (byte) 0);
        this.dataManager.register(CRYSTAL_1, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.isSheared());
        compound.setByte("Crystal_0", (byte) this.getDominantCrystal().dominant);
        compound.setByte("Crystal_1", (byte) this.getRecessiveCrystal().dominant);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setSheared(compound.getBoolean("Sheared"));
        int a = compound.getByte("Crystal_0");
        int b = compound.getByte("Crystal_1");
        this.setCrystal(Crystal.dominantOf(a), Crystal.dominantOf(b));
    }

    @Override
    public void onLivingUpdate() {
        if(world.isRemote) {
            this.growTimer = Math.max(0, this.growTimer - 1);
        } else {
            if(world.isDaytime()) {
                float brightness = this.getBrightness(1.0F);
                if(brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && world.canSeeSky(new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ))) {
                    this.setFire(8);
                }
            }
        }
        super.onLivingUpdate();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if(id == 10) {
            this.growTimer = 40;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected void jump() {
        if(growTimer == 0) {
            super.jump();
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return TEMPTATION_ITEMS.contains(itemStack.getItem());
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(itemStack.getItem() instanceof ItemPickaxe) {
            if(!this.isSheared() && !this.isChild()) {
                if(!this.world.isRemote) {
                    this.setSheared(true);
                    Crystal crystal = getDominantCrystal();
                    int n = crystal.dropAmount + rand.nextInt(crystal.dropAmount + 1);
                    for(int i = 0; i < n; i++) {
                        EntityItem drop = this.entityDropItem(crystal.getDrop(), 1.0F);
                        drop.motionY += this.rand.nextFloat() * 0.05F;
                        drop.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                        drop.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    }
                }
                itemStack.damageItem(1, player);
                this.playSound(SoundEvents.ITEM_SHOVEL_FLATTEN, 1.0F, 1.0F);
                return true;
            }
        }
        if(this.isBreedingItem(itemStack)) {
            Block block = ((ItemBlock) itemStack.getItem()).getBlock();
            this.world.playEvent(2001, new BlockPos(posX, posY, posZ), Block.getIdFromBlock(block));
            this.consumeItemFromStack(player, itemStack);
            if(!world.isRemote) {
                this.entityDropItem(new ItemStack(Blocks.GRAVEL), 0.1F);
            }
            if(!this.isChild() && !this.isInLove()) {
                this.setInLove(player);
            }
            this.eatStoneBonus(block == Blocks.MOSSY_COBBLESTONE);
            return true;
        }
        return super.processInteract(player, hand);
    }

    public void eatStoneBonus(boolean isMossy) {
        if(!world.isRemote) {
            if(getDominantCrystal() != Crystal.EMERALD || getRecessiveCrystal() != Crystal.EMERALD) {
                this.setSheared(false);
            }
            if(isMossy && getDominantCrystal() == Crystal.COAL) {
                this.setCrystal(getRecessiveCrystal(), Crystal.DIAMOND);
            }
        }
        if(this.isChild()) {
            this.ageUp((int) ((-this.getGrowingAge() / 20) * 0.1F), true);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CrystalLizard.entityCrystalLizardAmbient;
    }

    @Override
    protected SoundEvent getHurtSound() {
        return CrystalLizard.entityCrystalLizardAmbient;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CrystalLizard.entityCrystalLizardAmbient;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if(isSheared()) {
            return;
        }
        Crystal.entityDropItem(this, getDominantCrystal(), getRecessiveCrystal());
    }

    @Override
    public boolean getCanSpawnHere() {
        IBlockState state = this.world.getBlockState((new BlockPos(this)).down());
        return state.canEntitySpawn(this) && EntityUtils.isValidLightLevel(this, rand);
    }

    @Override
    public EntityCrystalLizard createChild(EntityAgeable ageable) {
        if(!(ageable instanceof EntityCrystalLizard)) {
            return null;
        }
        EntityCrystalLizard man = ((EntityCrystalLizard) ageable);
        Crystal a = rand.nextBoolean() ? this.getDominantCrystal() : this.getRecessiveCrystal();
        Crystal b = rand.nextBoolean() ? man.getDominantCrystal() : man.getRecessiveCrystal();
        if(dimension == -1) {
            Crystal temp = getRandomCrystalNether(rand);
            if(temp != null) {
                if(rand.nextBoolean()) {
                    a = temp;
                } else {
                    b = temp;
                }
            }
        }

        EntityCrystalLizard entity = new EntityCrystalLizard(world);
        entity.setCrystal(a, b);
        return entity;
    }

    public boolean isSheared() {
        return this.dataManager.get(SHEARED);
    }

    public void setSheared(boolean isSheared) {
        this.dataManager.set(SHEARED, isSheared);
    }

    public Crystal getDominantCrystal() {
        return Crystal.dominantOf(this.dataManager.get(CRYSTAL_0));
    }

    public Crystal getRecessiveCrystal() {
        return Crystal.dominantOf(this.dataManager.get(CRYSTAL_1));
    }

    public void setCrystal(Crystal a, Crystal b) {
        if(b.isDominant(a)) {
            Crystal temp = a;
            a = b;
            b = temp;
        }
        this.dataManager.set(CRYSTAL_0, (byte) a.dominant);
        this.dataManager.set(CRYSTAL_1, (byte) b.dominant);
    }

    public IBlockState getRender() {
        if(this.isSheared()) {
            return Blocks.AIR.getDefaultState();
        }
        return this.getDominantCrystal().getRender();
    }
}
