package model.animals.behavior;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityModel;

import java.util.List;

public interface EatStrategy {

    void tick(
            AnimalModel animal,
            List<EntityModel> nearbyEntities,
            BlockModel[][] blocksData
    );
}