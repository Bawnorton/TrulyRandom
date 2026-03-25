package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.util.mixin.AdvancedConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class TrulyRandomMixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName).visibleAnnotations;
            if(annotationNodes == null) return true;

            for(AnnotationNode node: annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(AdvancedConditionalMixin.class))) {
                    Type checkerType = Annotations.getValue(node, "checker");
                    boolean invert = Annotations.getValue(node, "invert", Boolean.FALSE);
                    AnnotationNode version = Annotations.getValue(node, "version", VersionPredicate.class);
                    AdvancedConditionChecker checker = AdvancedConditionChecker.create(checkerType, version);
                    boolean shouldApply = checker.shouldApply();
                    if (invert) shouldApply = !shouldApply;
                    TrulyRandom.LOGGER.debug("TrulyRandomMixinPlugin: " + mixinClassName + " is" + (shouldApply ? " " : " not ") + "being applied because " + checkerType.getClassName() + " returned " + shouldApply);
                    return shouldApply;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
