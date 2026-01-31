package miterenewed;

import miterenewed.datagen.MITERenewedRecipeProvider;
import miterenewed.datagen.MITERenewedChestLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MITERenewedDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(MITERenewedRecipeProvider::new);
        pack.addProvider(MITERenewedChestLootTableProvider::new);
    }
}
