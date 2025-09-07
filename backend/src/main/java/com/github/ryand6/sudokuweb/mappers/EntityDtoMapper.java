package com.github.ryand6.sudokuweb.mappers;

public interface EntityDtoMapper<Entity, Dto> {

    // Method for mapping Entity object to Dto object
    Dto mapToDto(Entity e);

    // Method for mapping Dto object to Entity object
    //Entity mapFromDto(Dto d);

}
