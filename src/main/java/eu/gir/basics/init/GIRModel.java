package eu.gir.basics.init;

import java.util.regex.Pattern;

import eu.gir.basics.GIRMain;
import net.minecraft.client.renderer.block.model.BuiltInModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class GIRModel implements ICustomModelLoader {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	private static final Pattern PATTERN = Pattern.compile("\\d", Pattern.MULTILINE);
	
	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.getResourceDomain().equals(GIRMain.MODID)
				&& PATTERN.matcher(modelLocation.getResourcePath()).find();
	}

	private static IModel modelCache = null;

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		if (modelLocation instanceof ModelResourceLocation
				&& ((ModelResourceLocation) modelLocation).getVariant().equals("inventory")) {
			if (modelCache != null) {
				return modelCache;
			} else {
				modelCache = ModelLoaderRegistry
						.getModel(new ResourceLocation(GIRMain.MODID, "item/invisiblelights"));
				return modelCache;
			}
		}
		return (state, format, getter) -> new BuiltInModel(ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE);
	}

}
