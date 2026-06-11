package model.entity;

public class HerbivoreLandAnimal extends LandAnimal{

    public HerbivoreLandAnimal(EntityCoordinate position) {
        super(position);
    }

    public HerbivoreLandAnimal(EntityCoordinate position, double maxHealth, double maxHunger, double maxThirst, double maxEnergy) {
        super(position, maxHealth, maxHunger, maxThirst, maxEnergy);
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
    }
}
