package model.animals;

import model.animals.entity.PredatorAnimalModel;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

import java.util.List;

/**
 * Chó sói — kẻ săn mồi chính, săn được tất cả các loài prey.
 *
 * <p>Wolf có tầm nhìn xa nhất trong các predator, tốc độ sprint cao và
 * hunt speed multiplier mạnh. Không loài prey nào miễn nhiễm với Wolf —
 * kể cả Cow và Turtle đều có thể bị Wolf hạ nếu đủ thời gian.
 * Fox không thể bị Wolf ăn (canBeEaten = false giữa predator).</p>
 *
 * <p>Wolf không thể bị ăn bởi bất kỳ loài nào trong simulation hiện tại.</p>
 *
 * <p>Asset: {@code assets/minecraft_based/wolf.png}</p>
 */
public class Wolf extends PredatorAnimalModel implements Edible {

    /**
     * Cấu hình loài chó sói — tầm nhìn xa, sprint nhanh, FOV rộng.
     */
    public static final Species SPECIES = new Species(
            "wolf",
            0.20,               // minSpeed — tốc độ lang thang
            0.45,               // maxSpeed — tốc độ chạy bình thường
            14.0,               // viewDistance — tầm nhìn xa nhất trong predator
            Math.toRadians(150), // fovRadians — góc nhìn rộng
            Math.toRadians(12),  // turnRate — xoay chậm, đường thẳng khi săn
            1.8,                // huntSpeedMultiplier — sprint 80% nhanh hơn max
            1.0,                // fleeSpeedMultiplier — wolf không cần flee
            0.0                 // followDistance — không dùng cho predator
    );

    /**
     * Spawn chó sói tại vị trí chỉ định với trạng thái adult mặc định.
     *
     * @param position vị trí xuất hiện trong thế giới
     */
    public Wolf(EntityCoordinate position) {
        super(position);
        this.entityType   = "wolf";
        this.health       = 20;  // khỏe nhất trong simulation
        this.energy       = 15;
        this.hunger       = 5;
        this.thirst       = 5;
        this.age          = 0;
        this.adultAge     = 100;
        this.oldAge       = 500;
        this.totalLifespan = 750;
        this.currentState = 1;
        this.setSpecies(SPECIES);
        this.setSpeed(SPECIES.getMinSpeed());
        this.randomizeDirection();
    }

    // =====================================================================
    // Edible — wolf không thể bị ăn
    // =====================================================================

    @Override
    public float getHungerValue() {
        // Wolf không được ăn bởi loài nào
        return 0f;
    }

    @Override
    public float getEnergyValue() {
        return 0f;
    }

    @Override
    public boolean canBeEaten() {
        return false;
    }

    // =====================================================================
    // Interact
    // =====================================================================

    @Override
    public void Interact(BlockModel block) {
        // Wolf không tương tác với block
    }

    @Override
    public void Interact(EntityModel entity) {
        // Tương tác đơn lẻ — xử lý qua List để dùng PredatorAnimalModel logic
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        // Giao cho PredatorAnimalModel chọn Hunt / Roam
        super.Interact(entities);
    }
}