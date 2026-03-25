package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.ResourceKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import java.lang.reflect.Field;

public class HeroOfTheVillagerGraphElement extends GameplayGraphElement implements LivingEntityGuiRenderer {
    private VillagerData villagerData;

    public HeroOfTheVillagerGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
        String giftId = lootTableId.getSegments()[2];
        String villagerId = giftId.substring(0, giftId.lastIndexOf('_'));
        Field[] fields = VillagerProfession.class.getFields();
        villagerData = VillagerEntity.createVillagerData();
        try {
            for (Field field : fields) {
                Object value = field.get(null);
                if (!(value instanceof ResourceKey vp)) continue;

                if(vp.getValue().toString().equals(villagerId)) {
                    DynamicRegistryManager registryManager = MinecraftClient.getInstance().world.getRegistryManager();
                    villagerData = villagerData.withProfession(registryManager, vp);
                    break;
                }
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        VillagerEntity villagerEntity = EntityType.VILLAGER.create(client.world, SpawnReason.COMMAND);
        if(villagerEntity == null) return;

        villagerEntity.setVillagerData(villagerData);
        render(context, mouseX, mouseY, villagerEntity, x - 4, y, scale);

        Identifier sprite = InGameHud.getEffectTexture(StatusEffects.HERO_OF_THE_VILLAGE);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, x, y, 12, 12);
    }

    @Override
    protected Text getTooltip() {
        return Text.translatable("trulyrandom.loot_book.hotv.%s".formatted(villagerData.profession().getIdAsString()));
    }
}
