package eyeq.crystallizard.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCrystalLizard extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer legLeft0;
    public ModelRenderer legLeft1;
    public ModelRenderer legLeft2;
    public ModelRenderer legLeft3;
    public ModelRenderer legRight0;
    public ModelRenderer legRight1;
    public ModelRenderer legRight2;
    public ModelRenderer legRight3;
    public ModelRenderer tail;

    public ModelCrystalLizard() {
        body = new ModelRenderer(this, 0, 14);
        body.addBox(-4F, -2F, -6F, 8, 4, 12);
        body.setRotationPoint(0F, 22F, 0F);

        head = new ModelRenderer(this, 0, 0);
        head.addBox(0F, -2F, -6F, 6, 4, 6);
        head.setRotationPoint(0F, 22F, -6F);
        tail = new ModelRenderer(this, 18, 4);
        tail.addBox(-2F, -2F, 0F, 4, 4, 6);
        tail.setRotationPoint(0F, 22F, 6F);

        legRight0 = new ModelRenderer(this, 18, 0);
        legRight0.addBox(-4F, 0.0F, 0F, 4, 2, 2);
        legRight0.setRotationPoint(-4F, 22F, -6F);
        legRight1 = new ModelRenderer(this, 18, 0);
        legRight1.addBox(-4F, 0.0F, 0F, 4, 2, 2);
        legRight1.setRotationPoint(-4F, 22F, -3F);
        legRight2 = new ModelRenderer(this, 18, 0);
        legRight2.addBox(-4F, 0.0F, 0F, 4, 2, 2);
        legRight2.setRotationPoint(-4F, 22F, 0F);
        legRight3 = new ModelRenderer(this, 18, 0);
        legRight3.addBox(-4F, 0.0F, 0F, 4, 2, 2);
        legRight3.setRotationPoint(-4F, 22F, 3F);

        legLeft0 = new ModelRenderer(this, 18, 0);
        legLeft0.addBox(0F, 0.0F, 0F, 4, 2, 2);
        legLeft0.setRotationPoint(4F, 22F, -6F);
        legLeft1 = new ModelRenderer(this, 18, 0);
        legLeft1.addBox(0F, 0.0F, 0F, 4, 2, 2);
        legLeft1.setRotationPoint(4F, 22F, -3F);
        legLeft2 = new ModelRenderer(this, 18, 0);
        legLeft2.addBox(0F, 0.0F, 0F, 4, 2, 2);
        legLeft2.setRotationPoint(4F, 22F, 0F);
        legLeft3 = new ModelRenderer(this, 18, 0);
        legLeft3.addBox(0F, 0.0F, 0F, 4, 2, 2);
        legLeft3.setRotationPoint(4F, 22F, 3F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        head.render(scale);
        body.render(scale);
        tail.render(scale);

        legRight0.render(scale);
        legRight1.render(scale);
        legRight2.render(scale);
        legRight3.render(scale);
        
        legLeft0.render(scale);
        legLeft1.render(scale);
        legLeft2.render(scale);
        legLeft3.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks2, float netHeadYaw, float headPitch, float scale, Entity entity) {
        head.rotateAngleX = headPitch / 57.29578F;
        head.rotateAngleY = netHeadYaw / 57.29578F + 45F;
        
        float angleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        tail.rotateAngleY = angleY;

        legRight0.rotateAngleY = angleY;
        legRight1.rotateAngleY = -angleY;
        legRight2.rotateAngleY = angleY;
        legRight3.rotateAngleY = -angleY;
        
        legLeft0.rotateAngleY = angleY;
        legLeft1.rotateAngleY = -angleY;
        legLeft2.rotateAngleY = angleY;
        legLeft3.rotateAngleY = -angleY;
    }
}
