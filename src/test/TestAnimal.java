package test;

import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

public class TestAnimal extends AnimalModel {
    private final String name;

    public TestAnimal(String name, EntityCoordinate position, double speed, double directionX, double directionY) {
        super(position, 10, 0, 0, 0, 0, 0, 0.0f, 0.0f, 0.0f, "", speed, directionX, directionY);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void Interact(BlockModel block) {
        // No-op for CLI demo.
    }

    @Override
    public void Interact(EntityModel entity) {
        // No-op for CLI demo.
    }

    @Override
    public void Interact(List<EntityModel> entities) {

    }
}

