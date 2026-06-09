package model.entity;

import model.block.BlockModel;

import java.util.List;

public class PlantModel extends EntityModel{
    public PlantModel(EntityCoordinate position) {
        super(position);
        this.health = 10;
        this.age = 0;
        this.adultAge = 10;
        this.oldAge = 30;
        this.totalLifespan = 50;
        this.currentState = 0;
        this.entityType = "plant";
    }

    @Override
    public void ageUp() {
        super.ageUp();
        if (age >= 10 && currentState == 0) {
            currentState = 1;
        } else if (age >= 30 && currentState == 1) {
            currentState = 2;
        }
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