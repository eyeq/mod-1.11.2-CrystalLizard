package eyeq.crystallizard;

import eyeq.crystallizard.client.renderer.entity.RenderCrystalLizard;
import eyeq.crystallizard.entity.passive.EntityCrystalLizard;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.USoundCreator;
import eyeq.util.client.resource.gson.SoundResourceManager;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.common.registry.UEntityRegistry;
import eyeq.util.common.registry.USoundEventRegistry;
import eyeq.util.world.biome.BiomeUtils;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

import static eyeq.crystallizard.CrystalLizard.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
public class CrystalLizard {
    public static final String MOD_ID = "eyeq_crystallizard";

    @Mod.Instance(MOD_ID)
    public static CrystalLizard instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static SoundEvent entityCrystalLizardAmbient;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        registerEntities();
        registerSoundEvents();
        if(event.getSide().isServer()) {
            return;
        }
        registerEntityRenderings();
        createFiles();
    }

    public static void registerEntities() {
        UEntityRegistry.registerModEntity(resource, EntityCrystalLizard.class, "CrystalLizard", 0, instance, 0x516769, 0x28C5F8);

        EntityRegistry.addSpawn(EntityCrystalLizard.class, 5, 1, 3, EnumCreatureType.MONSTER, BiomeUtils.getBiomes().toArray(new Biome[0]));
    }

    public static void registerSoundEvents() {
        entityCrystalLizardAmbient = new SoundEvent(resource.createResourceLocation("crystal_lizard"));

        USoundEventRegistry.registry(entityCrystalLizardAmbient);
    }

	@SideOnly(Side.CLIENT)
    public static void registerEntityRenderings() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCrystalLizard.class, RenderCrystalLizard::new);
    }
	
    public static void createFiles() {
    	File project = new File("../1.11.2-CrystalLizard");
    	
        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, EntityCrystalLizard.class, "Crystal Lizard");
        language.register(LanguageResourceManager.JA_JP, EntityCrystalLizard.class, "結晶トカゲ");

        ULanguageCreator.createLanguage(project, MOD_ID, language);

        SoundResourceManager sound = new SoundResourceManager();

        sound.register(entityCrystalLizardAmbient, SoundCategory.AMBIENT.getName());

        USoundCreator.createSoundJson(project, MOD_ID, sound);
    }
}
