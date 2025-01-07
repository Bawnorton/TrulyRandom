package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.RecipeBookScreenExtender;
import com.bawnorton.trulyrandom.client.mixin.accessor.InventoryScreenAccessor;
import com.bawnorton.trulyrandom.client.screen.lootbook.LootBookWidget;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin extends HandledScreenMixin implements RecipeBookScreenExtender {
    protected RecipeBookScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    private boolean narrow;
    @Shadow @Final
    private RecipeBookWidget<?> recipeBook;

    @Unique private TexturedButtonWidget recipeButton;
    @Unique private TexturedButtonWidget lootBookButton;

    @Unique
    private final LootBookWidget lootBook = new LootBookWidget();

    @Unique
    private boolean isShort;

    @Override
    public void trulyrandom$resetY() {
        y = (height - backgroundHeight) / 2 + lootBook.topOffset;
    }

    @Override
    public void trulyrandom$refreshResults() {
        if(lootBook.isOpen()) {
            lootBook.refreshResults();
        }
    }

    @Unique
    private void resetButtonPositions() {
        if(lootBookButton == null) return;

        recipeButton.setX(x + 104);
        lootBookButton.setX(recipeButton.getX() + 22);
        recipeButton.setY(height / 2 - 22 + lootBook.topOffset);
        lootBookButton.setY(recipeButton.getY());
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "addRecipeBook",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/RecipeBookScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    shift = At.Shift.AFTER
            )
    )
    private void initLootBook(CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(!((Object) this instanceof InventoryScreen invScreen)) return;

        isShort = height < 350;
        lootBook.initialize(width, height, client, narrow, isShort);
        lootBookButton = new TexturedButtonWidget(recipeButton.getX() + 22, height / 2 - 22, 20, 18, LootBookWidget.BUTTON_TEXTURES, button -> {
            lootBook.toggleOpen();
            if(lootBook.isOpen() && recipeBook.isOpen()) {
                recipeBook.toggleOpen();
            }
            x = lootBook.findLeftEdge(width, backgroundWidth);
            trulyrandom$resetY();
            resetButtonPositions();
            ((InventoryScreenAccessor) invScreen).setMouseDown(true);
        }) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                if(TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
                    super.renderWidget(context, mouseX, mouseY, delta);
                }
            }
        };

        lootBook.registerGraphListener(graph -> {
            trulyrandom$resetY();
            resetButtonPositions();
        }, graph -> {
            trulyrandom$resetY();
            resetButtonPositions();
        });

        if(recipeBook.isOpen()) {
            x = recipeBook.findLeftEdge(width, backgroundWidth);
            if(lootBook.isOpen()) {
                lootBook.toggleOpen();
            }
        } else if (lootBook.isOpen()) {
            x = lootBook.findLeftEdge(width, backgroundWidth);
            resetButtonPositions();
        } else {
            x = (width - backgroundWidth) / 2;
        }

        trulyrandom$resetY();

        addDrawableChild(lootBookButton);
        addSelectableChild(lootBook);
    }

    @Inject(method = "method_64513", at = @At("TAIL"))
    private void considerLootBook(ButtonWidget button, CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(lootBookButton == null) return;

        if(recipeBook.isOpen() && lootBook.isOpen()) {
            lootBook.toggleOpen();
        }
        trulyrandom$resetY();
        resetButtonPositions();
    }

    @ModifyArg(
            method = "addRecipeBook",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/RecipeBookScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
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
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    private void renderLootBook(RecipeBookScreen<?> instance, DrawContext context, int mouseX, int mouseY, float delta, Operation<Void> original) {
        if (!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
            if(lootBook.isOpen()) {
                lootBook.toggleOpen();
            }
            original.call(instance, context, mouseX, mouseY, delta);
            return;
        }

        if(lootBook.isOpen() && narrow) {
            renderBackground(context, mouseX, mouseY, delta);
            lootBook.render(context, mouseX, mouseY, delta);
        } else if (lootBook.isGraphOpen() && isShort) {
            renderBackground(context, mouseX, mouseY, delta);
            lootBook.renderGraph(context, mouseX, mouseY, delta);
        } else {
            original.call(instance, context, mouseX, mouseY, delta);
            lootBook.render(context, mouseX, mouseY, delta);
        }
    }

    @WrapWithCondition(
            method = "drawSlots",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;drawGhostSlots(Lnet/minecraft/client/gui/DrawContext;Z)V"
            )
    )
    private boolean dontDrawSlotsIfGraphOpen(RecipeBookWidget<?> instance, DrawContext context, boolean resultHasPadding) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return true;
        if(lootBookButton == null) return true;

        return !lootBook.isGraphOpen();
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void renderLootBookTooltip(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(lootBookButton == null) return;

        lootBook.drawTooltip(context, mouseX, mouseY);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;keyPressed(III)Z"
            )
    )
    private boolean keyPressedInLootBook(RecipeBookScreen<?> instance, int keyCode, int scanCode, int modifiers, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES) || lootBookButton == null) {
            return original.call(instance, keyCode, scanCode, modifiers);
        }

        return lootBook.keyPressed(keyCode, scanCode, modifiers) || original.call(instance, keyCode, scanCode, modifiers);
    }

    @WrapOperation(
            method = "charTyped",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;charTyped(CI)Z"
            )
    )
    private boolean charTypedInLootBook(RecipeBookScreen<?> instance, char chr, int modifiers, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES) || lootBookButton == null) {
            return original.call(instance, chr, modifiers);
        }

        return lootBook.charTyped(chr, modifiers) || original.call(instance, chr, modifiers);
    }

    @WrapOperation(
            method = "isPointWithinBounds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointWithinBounds(IIIIDD)Z"
            )
    )
    private boolean isPointWithinLootBookBounds(RecipeBookScreen<?> instance, int x, int y, int width, int height, double pointX, double pointY, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES) || lootBookButton == null) {
            return original.call(instance, x, y, width, height, pointX, pointY);
        }

        boolean closed = !lootBook.isOpen();
        boolean wide = !narrow;
        boolean tall = !isShort;
        if(wide || tall || closed) {
            return original.call(instance, x, y, width, height, pointX, pointY);
        } else {
            return false;
        }
    }

    @WrapOperation(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;mouseClicked(DDI)Z"
            )
    )
    private boolean mouseClickedInLootBook(RecipeBookScreen<?> instance, double mouseX, double mouseY, int button, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES) || lootBookButton == null) {
            return original.call(instance, mouseX, mouseY, button);
        }

        if(lootBook.mouseClicked(mouseX, mouseY, button)) {
            setFocused(lootBook);
            return true;
        }

        boolean closed = !lootBook.isOpen();
        boolean wide = !narrow;
        boolean tall = !isShort;
        if(wide || tall || closed) {
            return original.call(instance, mouseX, mouseY, button);
        } else {
            return false;
        }
    }

    @ModifyReturnValue(
            method = "isClickOutsideBounds",
            at = @At("RETURN")
    )
    private boolean isClickOutsideLootBook(boolean original, double mouseX, double mouseY, int left, int top) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return original;
        if (lootBookButton == null) return original;

        return original && lootBook.isClickOutsideBounds(mouseX, mouseY, x, y, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void mouseDragInInvScreen(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(lootBookButton == null) return;

        if(lootBook.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    protected void mouseScrolledInInvScreen(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(!TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(lootBookButton == null) return;

        if(lootBook.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            cir.setReturnValue(true);
        }
    }
}
