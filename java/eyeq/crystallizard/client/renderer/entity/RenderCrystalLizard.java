package eyeq.crystallizard.client.renderer.entity;

import eyeq.crystallizard.client.model.ModelCrystalLizard;
import eyeq.crystallizard.client.renderer.entity.layers.LayerCarryBlock;
import eyeq.util.client.renderer.EntityRenderResourceLocation;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static eyeq.crystallizard.CrystalLizard.MOD_ID;

@SideOnly(Side.CLIENT)
public class RenderCrystalLizard extends RenderLiving {
    protected static final ResourceLocation textures = new EntityRenderResourceLocation(MOD_ID, "crystal_lizard");

    public RenderCrystalLizard(RenderManager renderManager) {
        super(renderManager, new ModelCrystalLizard(), 0.5F);
        this.addLayer(new LayerCarryBlock(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return textures;
    }
}
