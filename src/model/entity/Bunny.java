package model.entity;

import model.block.BlockModel;

public class Bunny extends AnimalModel{
    public Bunny(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, Direction direction) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, survivalStrategy, direction);
        directionChangeChance = 0.3;
    }

    public Bunny(EntityCoordinate position){
        this(position,
             100,
             0,
             30,
             80,
             100,
             0,
             100,
             100,
             100,
             "Friendly",
             new Direction(Direction.SOUTH));
    }

    @Override
    public void Interact(BlockModel block) {

    }

    @Override
    public void Interact(EntityModel entity) {

    }
}
