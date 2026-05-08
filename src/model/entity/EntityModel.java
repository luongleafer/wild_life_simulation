package model.entity;

import model.block.*;

public abstract class EntityModel {
    // work in progress, shoddy implementation

    // its coordinates
    protected EntityCoordinate position;
    // its health
    protected int health;
    // its age and grow speed
    protected int age;
    // DUMBASS TEMPORARY METHOD: use 2 more values to dictate when switch to next grow stage
    protected int adult_age;
    protected int old_age;
    // total lifespan
    protected int total_lifespan;
    // its current state as 0, 1, 2 (baby -> adult -> old)
    protected int current_state;
    // i think we also should have a variable that time it takes to switch to a new state


    public EntityModel(EntityCoordinate position, int health, int age, int adult_age, int old_age, int total_lifespan, int current_state) {
        this.position = position;
        this.health = health;
        this.age = age;
        this.adult_age = adult_age;
        this.old_age = old_age;
        this.total_lifespan = total_lifespan;
        this.current_state = current_state;
    }

    public void ageUp() {
        this.age += 1;
    }

    public void advanceLifeStage() {
        // currently I see no factor that allows something to grow so
        // Either the statement that switches the stage as soon as adult_age or old_age is reached
        // can be implemented elsewhere, or here
        this.current_state += 1;
    }

    // unsure
    public abstract void Interact(BlockModel block);
    public abstract void Interact(EntityModel entity);
}