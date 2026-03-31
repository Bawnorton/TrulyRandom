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
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinEnvironment("client")
@Mixin(AbstractRecipeBookScreen.class)
abstract class AbstractRecipeBookScreenMixin extends AbstractContainerScreenMixin implements RecipeBookScreenExtender {
    protected AbstractRecipeBookScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    private boolean widthTooNarrow;
    @Shadow @Final
    private RecipeBookComponent<?> recipeBookComponent;

    @Unique private ImageButton recipeButton;
    @Unique private ImageButton lootBookButton;

    @Unique
    private final LootBookWidget lootBook = new LootBookWidget();

    @Unique
    private boolean isShort;

    @Override
    public void trulyrandom$resetY() {
        topPos = (height - imageHeight) / 2 + lootBook.topOffset;
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

        recipeButton.setX(leftPos + 104);
        lootBookButton.setX(recipeButton.getX() + 22);
        recipeButton.setY(height / 2 - 22 + lootBook.topOffset);
        lootBookButton.setY(recipeButton.getY());
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "initButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;addWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
                    shift = At.Shift.AFTER
            )
    )
    private void initLootBook(CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return;
        if(!((Object) this instanceof InventoryScreen invScreen)) return;

        isShort = height < 350;
        lootBook.initialize(width, height, minecraft, widthTooNarrow, isShort);
        lootBookButton = new ImageButton(recipeButton.getX() + 22, height / 2 - 22, 20, 18, LootBookWidget.BUTTON_TEXTURES, button -> {
            lootBook.toggleOpen();
            if(lootBook.isOpen() && recipeBookComponent.isVisible()) {
                recipeBookComponent.toggleVisibility();
            }
            leftPos = lootBook.findLeftEdge(width, imageWidth);
            trulyrandom$resetY();
            resetButtonPositions();
            ((InventoryScreenAccessor) invScreen).trulyrandom$buttonClicked(true);
        }) {
            @Override
            public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                if(TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) {
                    super.extractContents(graphics, mouseX, mouseY, a);
                }
            }

            @Override
            protected void handleCursor(GuiGraphicsExtractor graphics) {
                if(TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) {
                    super.handleCursor(graphics);
                }
            }
        };

        lootBook.registerGraphListener(_ -> {
            trulyrandom$resetY();
            resetButtonPositions();
        }, _ -> {
            trulyrandom$resetY();
            resetButtonPositions();
        });

        if(recipeBookComponent.isVisible()) {
            leftPos = recipeBookComponent.updateScreenPosition(width, imageWidth);
            if(lootBook.isOpen()) {
                lootBook.toggleOpen();
            }
        } else if (lootBook.isOpen()) {
            leftPos = lootBook.findLeftEdge(width, imageWidth);
            resetButtonPositions();
        } else {
            leftPos = (width - imageWidth) / 2;
        }

        trulyrandom$resetY();

        addRenderableWidget(lootBookButton);
        addWidget(lootBook);
    }

    @Inject(method = "lambda$initButton$0", at = @At("TAIL"))
    private void considerLootBook(Button button, CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return;
        if(lootBookButton == null) return;

        if(recipeBookComponent.isVisible() && lootBook.isOpen()) {
            lootBook.toggleOpen();
        }
        trulyrandom$resetY();
        resetButtonPositions();
    }

    @ModifyArg(
            method = "initButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"
            )
    )
    private GuiEventListener captureRecipeButton(GuiEventListener button) {
        recipeButton = (ImageButton) button;
        return button;
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
            )
    )
    private void extractLootBook(AbstractRecipeBookScreen<?> instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        if (!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) {
            if(lootBook.isOpen()) {
                lootBook.toggleOpen();
            }
            original.call(instance, graphics, mouseX, mouseY, a);
            return;
        }

        if(lootBook.isOpen() && widthTooNarrow) {
            extractBackground(graphics, mouseX, mouseY, a);
            lootBook.extractRenderState(graphics, mouseX, mouseY, a);
        } else if (lootBook.isGraphOpen() && isShort) {
            extractBackground(graphics, mouseX, mouseY, a);
            lootBook.extractRenderState(graphics, mouseX, mouseY, a);
        } else {
            original.call(instance, graphics, mouseX, mouseY, a);
            lootBook.extractRenderState(graphics, mouseX, mouseY, a);
        }
    }

    @WrapWithCondition(
            method = "extractSlots",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;extractGhostRecipe(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Z)V"
            )
    )
    private boolean dontDrawSlotsIfGraphOpen(RecipeBookComponent<?> instance, GuiGraphicsExtractor graphics, boolean isResultSlotBig) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return true;
        if(lootBookButton == null) return true;

        return !lootBook.isGraphOpen();
    }

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void renderLootBookTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return;
        if(lootBookButton == null) return;

        lootBook.extractTooltip(graphics, mouseX, mouseY);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    private boolean keyPressedInLootBook(AbstractRecipeBookScreen<?> instance, KeyEvent event, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker() || lootBookButton == null) {
            return original.call(instance, event);
        }

        return lootBook.keyPressed(event) || original.call(instance, event);
    }

    @WrapOperation(
            method = "charTyped",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z"
            )
    )
    private boolean charTypedInLootBook(AbstractRecipeBookScreen<?> instance, CharacterEvent characterEvent, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker() || lootBookButton == null) {
            return original.call(instance, characterEvent);
        }

        return lootBook.charTyped(characterEvent) || original.call(instance, characterEvent);
    }

    @WrapOperation(
            method = "hasClickedOutside",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;hasClickedOutside(DDIIII)Z"
            )
    )
    private boolean hasClickedOutsideLootBookBounds(RecipeBookComponent<?> instance, double mx, double my, int leftPos, int topPos, int imageWidth, int imageHeight, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker() || lootBookButton == null) {
            return original.call(instance, mx, my, leftPos, topPos, imageWidth, imageHeight);
        }

        boolean closed = !lootBook.isOpen();
        boolean wide = !widthTooNarrow;
        boolean tall = !isShort;
        if(wide || tall || closed) {
            return original.call(instance, mx, my, leftPos, topPos, imageWidth, imageHeight);
        } else {
            return false;
        }
    }

    @WrapOperation(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"
            )
    )
    private boolean mouseClickedInLootBook(AbstractRecipeBookScreen<?> instance, MouseButtonEvent containerInput, boolean xo, Operation<Boolean> original) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker() || lootBookButton == null) {
            return original.call(instance, containerInput, xo);
        }

        if(lootBook.mouseClicked(containerInput, xo)) {
            setFocused(lootBook);
            return true;
        }
        boolean closed = !lootBook.isOpen();
        boolean wide = !widthTooNarrow;
        boolean tall = !isShort;
        if(wide || tall || closed) {
            return original.call(instance, containerInput, xo);
        } else {
            return false;
        }
    }

    @ModifyReturnValue(
            method = "hasClickedOutside",
            at = @At("RETURN")
    )
    private boolean hasClickedOutsideLootBookBounds(boolean original, double mouseX, double mouseY, int left, int top) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return original;
        if (lootBookButton == null) return original;

        return original && lootBook.isClickOutsideBounds(mouseX, mouseY, left, top, imageWidth, imageHeight);
    }

    @Override
    protected void mouseDragInInvScreen(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return;
        if(lootBookButton == null) return;

        if(lootBook.mouseDragged(event, dx, dy)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    protected void mouseScrolledInInvScreen(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(!TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) return;
        if(lootBookButton == null) return;

        if(lootBook.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            cir.setReturnValue(true);
        }
    }
}
