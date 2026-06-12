package model.animals;

import model.entity.EntityCoordinate;
import model.entity.HerbivoreLandAnimal;

import java.util.List;

public class Deer extends HerbivoreLandAnimal {
    public Deer(EntityCoordinate position) {
        super(position, 10, 10, 10, 10);
        this.fleeingSpeed = 10.0 / 20;
        this.threatTypes = List.of("wolf");
        this.currentState = 1;
        this.entityType = "deer";
        this.hitBoxLength = 2;
        this.hitBoxWidth = 2;
    }
    @Override
    public void move() {
        roamRandomly(4.0/20, 8.2/20, Math.PI/3);
    }
}
