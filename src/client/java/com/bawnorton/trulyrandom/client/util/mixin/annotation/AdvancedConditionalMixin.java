package com.bawnorton.trulyrandom.client.util.mixin.annotation;

import com.bawnorton.trulyrandom.client.util.mixin.AdvancedConditionChecker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdvancedConditionalMixin {
    Class<? extends AdvancedConditionChecker> checker();
    boolean invert() default false;
}
