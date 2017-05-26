package eyeq.crystallizard.client.renderer.entity.layers;

import eyeq.crystallizard.client.renderer.entity.RenderCrystalLizard;
import eyeq.crystallizard.entity.passive.EntityCrystalLizard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCarryBlock implements LayerRenderer<EntityCrystalLizard> {
    private final RenderCrystalLizard render;

    public LayerCarryBlock(RenderCrystalLizard render) {
        this.render = render;
    }

    @Override
    public void doRenderLayer(EntityCrystalLizard entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IBlockState state = entity.getRender();
        if(state == null) {
            return;
        }
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.2F, 1.1F, 0.2F);
        if(entity.isChild()) {
            float f = 1.2F;
            GlStateManager.scale(0.4F / f, 0.15F/ f, 0.4F/ f);
        } else {
            GlStateManager.scale(0.4F, 0.15F, 0.4F);
        }
        int brightness = entity.getBrightnessForRender(partialTicks);
        int s = brightness % 65536;
        int t = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, s, t);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
