package model.animals;

import model.animals.entity.PreyAnimalModel;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.entity.Edible;

import java.util.List;

/**
 * Bò — con mồi lớn nhất, chậm nhất, health cao, chỉ Wolf mới đủ sức săn.
 *
 * <p>Bò có health cao nhất trong nhóm prey thông thường (không tính rùa)
 * và tốc độ thấp. Fox không thể săn bò hiệu quả do bò cần nhiều đòn tấn
 * công mới chết. Giá trị thức ăn cao nhất — Wolf ưu tiên bò nếu gặp.</p>
 *
 * <p>Texture có 2 state: state 0 = calf ({@code calf.png}), state 1 = adult
 * ({@code cow.png}) — khớp với texture đã đăng ký trong WorldController.</p>
 *
 * <p>Asset: {@code assets/minecraft_based/calf.png} (baby),
 * {@code assets/minecraft_based/cow.png} (adult)</p>
 */
public class Cow extends PreyAnimalModel implements Edible {

    /**
     * Cấu hình loài bò — chậm, tầm nhìn trung bình, flee yếu.
     */
    public static final Species SPECIES = new Species(
            "cow",
            0.15,               // minSpeed — chậm chạp khi lang thang
            0.30,               // maxSpeed
            8.0,                // viewDistance
            Math.toRadians(120), // fovRadians
            Math.toRadians(18),  // turnRate — xoay chậm vì thân to
            1.0,                // huntSpeedMultiplier — không dùng
            1.3,                // fleeSpeedMultiplier — chạy nhanh hơn 30% khi sợ
            3.5                 // followDistance — bê con giữ khoảng cách xa hơn
    );

    /**
     * Spawn bò tại vị trí chỉ định.
     * Mặc định spawn là bê con (state 0) để texture calf.png hiển thị đúng.
     *
     * @param position vị trí xuất hiện trong thế giới
     */
    public Cow(EntityCoordinate position) {
        super(position);
        this.entityType   = "cow";
        this.health       = 15;
        this.energy       = 12;
        this.hunger       = 5;
        this.thirst       = 5;
        this.age          = 0;
        this.adultAge     = 120;  // bê → bò sau 120 tick (khớp với texture state 1)
        this.oldAge       = 600;
        this.totalLifespan = 900;
        this.currentState = 0;   // bắt đầu là bê con (calf.png)
        this.setSpecies(SPECIES);
        this.setSpeed(SPECIES.getMinSpeed());
        this.randomizeDirection();
    }

    // =====================================================================
    // Edible — bò có giá trị thức ăn cao nhất
    // =====================================================================

    @Override
    public float getHungerValue() {
        return 10f;
    }

    @Override
    public float getEnergyValue() {
        return 14f;
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
        // Bò ăn cỏ
    }

    @Override
    public void Interact(EntityModel entity) {
    }

    @Override
    public void Interact(List<EntityModel> entities) {
        super.Interact(entities);
    }
}