package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import java.lang.reflect.Field;

public class HeroOfTheVillagerGraphElement extends GameplayGraphElement implements LivingEntityGuiRenderer {
    private VillagerData villagerData;

    public HeroOfTheVillagerGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
        String giftId = lootTableId.getSegments()[2];
        String villagerId = giftId.substring(0, giftId.lastIndexOf('_'));
        Field[] fields = VillagerProfession.class.getFields();
        villagerData = new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);
        try {
            for (Field field : fields) {
                Object value = field.get(null);
                if (!(value instanceof VillagerProfession vp)) continue;

                if(vp.id().equals(villagerId)) {
                    this.villagerData = new VillagerData(VillagerType.PLAINS, vp, 1);
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

        StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
        Sprite sprite = statusEffectSpriteManager.getSprite(StatusEffects.HERO_OF_THE_VILLAGE);
        context.getMatrices().push();
        context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, x, y, 12, 12);
        context.getMatrices().pop();
    }

    @Override
    protected Text getTooltip() {
        return Text.translatable("trulyrandom.loot_book.hotv.%s".formatted(villagerData.getProfession().id()));
    }
}
