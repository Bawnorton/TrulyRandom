package com.bawnorton.trulyrandom.client.util.mixin.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VersionPredicate {
    String min() default "";

    String max() default "";
}
