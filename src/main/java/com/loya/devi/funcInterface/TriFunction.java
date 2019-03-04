package com.loya.devi.funcInterface;

/**
 * Its implementation of the functional interface
 *
 * @author DEVIAPHAN on 16.01.2019
 * @project university
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
