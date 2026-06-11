package model.entity;

public class Grass extends PlantModel implements Edible{
    public Grass(EntityCoordinate position) {
        super(position);
        this.entityType = "grass";
        this.adultAge = 20;
        this.health = 10;
        this.hitBoxWidth = 1;
        this.hitBoxLength = 1;
    }

    @Override
    public float getHungerValue() {
        return 3;
    }

    @Override
    public float getEnergyValue() {
        return 1;
    }

    @Override
    public boolean canBeEaten() {
        return true;
    }
}
