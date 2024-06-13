package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.screen.lootbook.LootBookWidget;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreenMixin<PlayerScreenHandler> {
    @Shadow private boolean narrow;
    @Shadow private boolean mouseDown;
    @Shadow @Final private RecipeBookWidget recipeBook;

    @Unique private TexturedButtonWidget recipeButton;
    @Unique private TexturedButtonWidget lootBookButton;

    @Unique
    private final LootBookWidget lootBook = new LootBookWidget();

    protected InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Unique
    private void resetButtonPositions() {
        recipeButton.setX(x + 104);
        lootBookButton.setX(recipeButton.getX() + 22);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    shift = At.Shift.AFTER
            )
    )
    private void initLootBook(CallbackInfo ci) {
        lootBook.initialize(width, height, client, narrow, handler);
        lootBookButton = new TexturedButtonWidget(recipeButton.getX() + 22, height / 2 - 22, 20, 18, LootBookWidget.BUTTON_TEXTURES, button -> {
            lootBook.toggleOpen();
            if(lootBook.isOpen() && recipeBook.isOpen()) {
                recipeBook.toggleOpen();
            }
            x = lootBook.findLeftEdge(width, backgroundWidth);
            resetButtonPositions();
            mouseDown = true;
        }) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                if(TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
                    super.renderWidget(context, mouseX, mouseY, delta);
                }
            }
        };

        if(recipeBook.isOpen()) {
            x = recipeBook.findLeftEdge(width, backgroundWidth);
        } else if (lootBook.isOpen()) {
            x = lootBook.findLeftEdge(width, backgroundWidth);
            resetButtonPositions();
        } else {
            x = (width - backgroundWidth) / 2;
        }

        addDrawableChild(lootBookButton);
        addSelectableChild(lootBook);
    }

    @Inject(method = "method_19891", at = @At("TAIL"))
    private void considerLootBook(ButtonWidget button, CallbackInfo ci) {
        if(recipeBook.isOpen() && lootBook.isOpen()) {
            lootBook.toggleOpen();
        }
        resetButtonPositions();
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
            )
    )
    private Element captureRecipeButton(Element button) {
        recipeButton = (TexturedButtonWidget) button;
        return button;
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    private void renderLootBook(InventoryScreen instance, DrawContext context, int mouseX, int mouseY, float delta, Operation<Void> original) {
        if(lootBook.isOpen() && narrow) {
            renderBackground(context, mouseX, mouseY, delta);
        } else {
            original.call(instance, context, mouseX, mouseY, delta);
        }
        if (TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
            lootBook.render(context, mouseX, mouseY, delta);
        } else if(lootBook.isOpen()) {
            lootBook.toggleOpen();
        }
    }

    @Override
    protected int changeEffectX(int x) {
        if (lootBook.isOpen()) {
            return x + 149;
        }
        return super.changeEffectX(x);
    }
}
