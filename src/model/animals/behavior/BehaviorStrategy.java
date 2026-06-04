package model.animals.behavior;

import model.entity.AnimalModel;
import model.entity.EntityModel;

import java.util.List;

/**
 * Dinh nghia mot hanh vi co the chay trong moi lan cap nhat dong vat.
 */
public interface BehaviorStrategy {
    void tick(AnimalModel animal, List<EntityModel> nearbyEntities);
}
