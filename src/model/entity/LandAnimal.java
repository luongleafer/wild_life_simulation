package model.entity;

import model.block.BlockModel;
import model.generation.WaterBlock;

import java.util.List;

public abstract class LandAnimal extends AnimalModel{

    public LandAnimal(EntityCoordinate position) {
        super(position);
    }

    @Override
    public void Interact(BlockModel block) {
        if(block instanceof WaterBlock waterBlock){avoidWater(waterBlock);}
    }

    private void avoidWater(WaterBlock water) {
        if(thirst <= maxThirst) {
            setDirection(0, 0); // stop to have a sip
            thirst += 1;
        }
        else{
            headAwayFrom(water.getPosition()); // avoid water
        }

    }
}
