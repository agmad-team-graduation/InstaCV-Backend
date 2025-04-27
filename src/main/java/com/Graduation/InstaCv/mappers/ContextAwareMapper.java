package com.Graduation.InstaCv.mappers;

public interface ContextAwareMapper<A, B, C> {
    B mapTo(A a);
    A mapFrom(B b, C c);
}
