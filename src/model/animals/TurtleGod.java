package model.animals;

import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

public class TurtleGod extends AnimalModel {
    public TurtleGod(EntityCoordinate position) {
        super(position, 1000, 1000, 1000, 1000);
        this.entityType = "turtle_god";
        this.currentState = 0;
        this.hitBoxWidth = 10;
        this.hitBoxLength = 10;
        this.healthDepletionMultiplier = 0;
        this.thirstDepletionMultiplier = 0;
        this.hungerDepletionMultiplier = 0;
    }

    @Override
    public void ageUp() {
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        entities.forEach(entityModel -> {
            if(entityModel == this) return;
            entityModel.receiveDamage(1000);
        }); // guarantee death
    }

    @Override
    public void move() {
        roamRandomly(0.1, 1, Math.PI);
    }
}
