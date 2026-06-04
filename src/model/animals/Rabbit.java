package model.animals;

import model.animals.entity.PreyAnimalModel;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

import java.util.List;

/**
 * Thỏ — con mồi nhỏ, nhanh nhẹn, dễ bị cả Wolf lẫn Fox săn.
 *
 * <p>Thỏ là loài chạy nhanh nhất trong nhóm prey nhờ tốc độ cao và
 * hệ số flee lớn. Baby thỏ sẽ bám theo thỏ trưởng thành gần nhất.
 * Thỏ trưởng thành sẽ bỏ chạy ngay khi phát hiện predator trong tầm nhìn.</p>
 *
 * <p>Asset: {@code assets/minecraft_based/bunny.png}</p>
 */
public class Rabbit extends PreyAnimalModel implements Edible {

    /**
     * Cấu hình loài thỏ — dùng chung cho tất cả instance.
     * Tốc độ cao, FOV rộng, flee nhanh hơn tốc độ bình thường 1.6 lần.
     */
    public static final Species SPECIES = new Species(
            "rabbit",
            0.30,               // minSpeed (block/tick) — tốc độ đi lang thang tối thiểu
            0.55,               // maxSpeed — tốc độ đi lang thang tối đa
            10.0,               // viewDistance — tầm nhìn phát hiện nguy hiểm
            Math.toRadians(160), // fovRadians — góc nhìn rộng để cảnh giác tốt
            Math.toRadians(25),  // turnRate — góc xoay tối đa mỗi tick khi lang thang
            1.0,                // huntSpeedMultiplier — không dùng cho prey
            1.6,                // fleeSpeedMultiplier — chạy nhanh hơn 60% khi sợ
            2.5                 // followDistance — baby dừng lại khi cách adult 2.5 block
    );

    /**
     * Spawn thỏ tại vị trí chỉ định với trạng thái mặc định (adult).
     *
     * @param position vị trí xuất hiện trong thế giới
     */
    public Rabbit(EntityCoordinate position) {
        super(position);
        this.entityType  = "rabbit";
        this.health      = 6;
        this.energy      = 8;
        this.hunger      = 5;
        this.thirst      = 5;
        this.age         = 0;
        this.adultAge    = 80;   // trở thành adult sau 80 tick
        this.oldAge      = 400;  // trở thành old sau 400 tick
        this.totalLifespan = 600;
        this.currentState = 1;   // adult mặc định khi spawn
        this.setSpecies(SPECIES);
        this.setSpeed(SPECIES.getMinSpeed());
        this.randomizeDirection();
    }

    // Edible — thỏ có thể bị ăn bởi Wolf và Fox

    @Override
    public float getHungerValue() {
        // Thỏ nhỏ, giá trị thức ăn thấp hơn cừu hay bò
        return 4f;
    }

    @Override
    public float getEnergyValue() {
        return 6f;
    }

    @Override
    public boolean canBeEaten() {
        return true;
    }


    // Interact — delegate lên PreyAnimalModel để chọn strategy


    @Override
    public void Interact(BlockModel block) {
        // Thỏ ăn cỏ/rau — để dành cho food block interaction sau
    }

    @Override
    public void Interact(EntityModel entity) {
        // Xử lý từng entity nếu cần — hiện để PreyAnimalModel xử lý qua List
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        // Giao cho PreyAnimalModel chọn Flee / Follow / Roam
        super.Interact(entities);
    }
}