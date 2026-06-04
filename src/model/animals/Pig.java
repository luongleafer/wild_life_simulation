package model.animals;

import model.animals.entity.PreyAnimalModel;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

import java.util.List;

/**
 * Lợn — con mồi tầm trung, có thể bị cả Wolf lẫn Fox săn.
 *
 * <p>Lợn có tốc độ và health ở mức trung bình trong nhóm prey. Di chuyển
 * khá linh hoạt (turnRate cao) nhưng không đủ nhanh để thoát khỏi predator
 * đang sprint. Flee multiplier ở mức trung bình.</p>
 *
 * <p>Asset: {@code assets/minecraft_based/pig.png}</p>
 */
public class Pig extends PreyAnimalModel implements Edible {

    /**
     * Cấu hình loài lợn — tốc độ trung bình, tầm nhìn khá tốt.
     */
    public static final Species SPECIES = new Species(
            "pig",
            0.22,               // minSpeed
            0.42,               // maxSpeed
            9.0,                // viewDistance
            Math.toRadians(140), // fovRadians
            Math.toRadians(30),  // turnRate — linh hoạt hơn cừu
            1.0,                // huntSpeedMultiplier — không dùng
            1.45,               // fleeSpeedMultiplier
            2.5                 // followDistance
    );

    /**
     * Spawn lợn tại vị trí chỉ định với trạng thái mặc định (adult).
     *
     * @param position vị trí xuất hiện trong thế giới
     */
    public Pig(EntityCoordinate position) {
        super(position);
        this.entityType   = "pig";
        this.health       = 10;
        this.energy       = 8;
        this.hunger       = 5;
        this.thirst       = 5;
        this.age          = 0;
        this.adultAge     = 90;
        this.oldAge       = 450;
        this.totalLifespan = 650;
        this.currentState = 1;
        this.setSpecies(SPECIES);
        this.setSpeed(SPECIES.getMinSpeed());
        this.randomizeDirection();
    }

    // =====================================================================
    // Edible
    // =====================================================================

    @Override
    public float getHungerValue() {
        return 6f;
    }

    @Override
    public float getEnergyValue() {
        return 8f;
    }

    @Override
    public boolean canBeEaten() {
        return true;
    }

    // =====================================================================
    // Interact
    // =====================================================================

    @Override
    public void Interact(BlockModel block) {
        // Lợn là omnivore nhẹ, ăn cả cỏ lẫn rễ cây
    }

    @Override
    public void Interact(EntityModel entity) {
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}