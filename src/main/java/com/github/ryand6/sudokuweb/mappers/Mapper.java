package com.github.ryand6.sudokuweb.mappers;

public interface Mapper<A, B> {

    // Method for mapping object A to object B
    B mapToDto(A a);

    // Method for mapping object B to object A
    A mapFromDto(B b);

}
