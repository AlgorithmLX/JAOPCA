package thelm.jaopca.compat.crossroads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "crossroads")
public class CrossroadsCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "gold", "iron", "tin"));
	private static final Set<String> MOLTEN_BLACKLIST = new TreeSet<>(Arrays.asList(
			"copper", "copshowium", "gold", "iron", "tin"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configMaterialToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configDustToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configNuggetToMoltenBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "crossroads_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have mill recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have material crucible recipes added."),
				configMaterialToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have dust crucible recipes added."),
				configDustToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.nuggetToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have nugget crucible recipes added."),
				configNuggetToMoltenBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have fluid cooling recipes added."),
				configToMaterialBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CrossroadsHelper helper = CrossroadsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerMillRecipe(
							new ResourceLocation("jaopca", "crossroads.material_to_dust."+material.getName()),
							materialLocation, dustLocation, 1);
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!MOLTEN_BLACKLIST.contains(name) && !configMaterialToMoltenBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				if(api.getFluidTags().contains(moltenLocation)) {
					helper.registerCrucibleRecipe(
							new ResourceLocation("jaopca", "crossroads.material_to_molten."+material.getName()),
							materialLocation, moltenLocation, 144);
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!MOLTEN_BLACKLIST.contains(name) && !configDustToMoltenBlacklist.contains(name)) {
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				if(api.getItemTags().contains(dustLocation) && api.getFluidTags().contains(moltenLocation)) {
					helper.registerCrucibleRecipe(
							new ResourceLocation("jaopca", "crossroads.dust_to_molten."+material.getName()),
							dustLocation, moltenLocation, 144);
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!MOLTEN_BLACKLIST.contains(name) && !configNuggetToMoltenBlacklist.contains(name)) {
				ResourceLocation nuggetLocation = miscHelper.getTagLocation("nuggets", material.getName());
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				if(api.getItemTags().contains(nuggetLocation) && api.getFluidTags().contains(moltenLocation)) {
					helper.registerCrucibleRecipe(
							new ResourceLocation("jaopca", "crossroads.nugget_to_molten."+material.getName()),
							nuggetLocation, moltenLocation, 16);
				}
			}
			if(!ArrayUtils.contains(MaterialType.DUSTS, type) &&
					!MOLTEN_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
				ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				if(api.getFluidTags().contains(moltenLocation)) {
					helper.registerFluidCoolingRecipe(
							new ResourceLocation("jaopca", "crossroads.molten_to_material."+material.getName()),
							moltenLocation, 144, materialLocation, 1, 1500, 100);
				}
			}
		}
	}
}
