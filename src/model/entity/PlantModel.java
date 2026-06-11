package model.entity;

import model.block.BlockModel;

import java.util.List;

public abstract class PlantModel extends EntityModel implements Edible{
    public PlantModel(EntityCoordinate position) {
        super(position);
        this.health = 10;
        this.age = 0;
        this.adultAge = 10;
        this.oldAge = 30;
        this.totalLifespan = 50;
        this.currentState = 0;
    }

    @Override
    public void ageUp() {
        super.ageUp();
        if (age >= adultAge && currentState == 0) {
            currentState = 1;
        }
    }

    public void resetAge(){
        age = 0;
        currentState = 0;
    }

    @Override
    public void Interact(BlockModel block) {
        // Plants are not sentient
    }

    @Override
    public void Interact(EntityModel entity) {
        // Plants are not sentient
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        // Plants are not sentient
    }
}