package model.entity;

import model.block.BlockModel;
import model.generation.WaterBlock;

public abstract class LandAnimal extends AnimalModel{

    public LandAnimal(EntityCoordinate position) {
        super(position);
    }

    @Override
    public void Interact(BlockModel block) {
        super.Interact(block);
        if(block instanceof WaterBlock waterBlock){avoidWater(waterBlock);}
    }

    private void avoidWater(WaterBlock water) {
        if(isThirsty()) {
            setDirection(0, 0); // stop to have a sip
            drink(water);
        }
        else{
            headAwayFrom(water.getPosition(),1); // avoid water
        }

    }
}
