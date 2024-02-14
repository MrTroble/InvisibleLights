package com.troblecodings.invisiblelights.init;

import com.troblecodings.invisiblelights.InvisibleLightsMain;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ILModel implements ICustomModelLoader {

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(InvisibleLightsMain.MODID)
                && !modelLocation.getResourcePath().endsWith("invisiblelights")
                && !modelLocation.getResourcePath().endsWith("ghostglowstone");
    }

    private static IModel modelCache = null;

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if (modelCache == null) {
            modelCache = new ItemLayerModel(ModelLoaderRegistry
                    .getModel(new ResourceLocation(InvisibleLightsMain.MODID, "item/invisiblelights"))
                    .asVanillaModel().get());
        }
        return modelCache;
    }

}
