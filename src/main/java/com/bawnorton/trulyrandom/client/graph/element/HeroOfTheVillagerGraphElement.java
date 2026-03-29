package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

import java.lang.reflect.Field;

public class HeroOfTheVillagerGraphElement extends GameplayGraphElement implements LivingEntityGuiRenderer {
    private VillagerData villagerData;

    public HeroOfTheVillagerGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
        villagerData = Villager.createDefaultVillagerData();
        if(!lootTableId.isHeroOfTheVillage()) return;

        String giftId = lootTableId.getSegments()[2];
        String villagerId = giftId.substring(0, giftId.lastIndexOf('_'));
        Field[] fields = VillagerProfession.class.getFields();
        try {
            for (Field field : fields) {
                Object value = field.get(null);
                if (!(value instanceof ResourceKey<?> vp)) continue;

                if(vp.identifier().toString().equals(villagerId)) {
                    RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
                    //noinspection unchecked
                    villagerData = villagerData.withProfession(registryAccess, (ResourceKey<VillagerProfession>) vp);
                    break;
                }
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        Villager villagerEntity = EntityType.VILLAGER.create(minecraft.level, EntitySpawnReason.COMMAND);
        if(villagerEntity == null) return;

        villagerEntity.setVillagerData(villagerData);
        extractRenderState(graphics, mouseX, mouseY, villagerEntity, x - 4, y, scale);

        Identifier sprite = Gui.getMobEffectSprite(MobEffects.HERO_OF_THE_VILLAGE);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 12, 12);
    }

    @Override
    protected Component getTooltip() {
        return Component.translatable("trulyrandom.loot_book.hotv.%s".formatted(villagerData.profession().getRegisteredName()));
    }
}
