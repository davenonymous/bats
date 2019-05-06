package org.dave.bats.util.serialization;

public @interface SerializationHandler {
    Class readClass() default void.class;
    Class writeClass() default void.class;
}
