package model.animals;

import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.CarnivoreLandAnimal;
import model.entity.CarnivorePredatorModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

public class Fox extends CarnivoreLandAnimal {


    public Fox(EntityCoordinate position) {
        super(position, 20, 20, 20, 20);
        this.species = new Species("fox", 0.1, 0.5, 5, Math.PI, 0.5, 1,1,5);
        this.currentState = 1;
        this.entityType = "fox";
        this.preyTypes = List.of("pig", "cow");
        this.attackStrength = 5;
    }



    @Override
    public void move() {
        roamRandomly(0.1, 6.5/20, Math.PI / 3);
        super.move();
    }
}
