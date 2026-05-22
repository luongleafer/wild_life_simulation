package model.entity;

public interface Edible {
    float getHungerValue();
    float getEnergyValue();
    boolean canBeEaten();
}
