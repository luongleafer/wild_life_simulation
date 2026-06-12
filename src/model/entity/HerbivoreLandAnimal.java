package model.entity;

import model.block.BlockCoordinate;

import java.util.ArrayList;
import java.util.List;

public class HerbivoreLandAnimal extends LandAnimal{
    protected List<String> threatTypes;
    protected double fleeingSpeed;

    public HerbivoreLandAnimal(EntityCoordinate position) {
        super(position);
    }

    public HerbivoreLandAnimal(EntityCoordinate position, double maxHealth, double maxHunger, double maxThirst, double maxEnergy) {
        super(position, maxHealth, maxHunger, maxThirst, maxEnergy);
        threatTypes = new ArrayList<>();
    }

    @Override
    public void Interact(EntityModel entity) {
        if(entity instanceof PlantModel plant && isHungry()){
            eat(plant);
            plant.resetAge();
        }
        else{
            super.Interact(entity);
        }
        if(threatTypes.contains(entity.getEntityType())){
            setSpeed(fleeingSpeed);
            headAwayFrom(new BlockCoordinate(
                    (int) entity.position.posX,
                    (int) entity.position.posY
            ), 1.0);
        }
    }
}
