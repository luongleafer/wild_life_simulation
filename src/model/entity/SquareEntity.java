package model.entity;

import model.block.BlockModel;

import java.util.List;

public class SquareEntity extends EntityModel{
    public SquareEntity(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState);
    }

    @Override
    public void Interact(BlockModel block) {

    }

    @Override
    public void Interact(EntityModel entity) {

    }

    @Override
    public void Interact(List<EntityModel> entities) {

    }
}
