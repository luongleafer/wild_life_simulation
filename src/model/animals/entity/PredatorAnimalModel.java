package model.animals.entity;

import model.animals.behavior.HuntStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.behavior.SimpleWaterSeekStrategy;
import model.animals.species.Species;
import model.block.BlockModel;
import model.entity.AnimalModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;

import java.util.List;

/**
 * Lớp trừu tượng cho tất cả thú săn mồi (Predator). Quản lý săn mồi, lang thang, và uống nước.
 */
public abstract class PredatorAnimalModel extends AnimalModel {

    private EntityModel currentTarget;     // Con mồi mục tiêu hiện tại

    // Các chiến thuật di chuyển/sinh tồn
    private final RoamStrategy roamStrategy = new RoamStrategy();
    private final HuntStrategy huntStrategy = new HuntStrategy(null);
    private final SimpleWaterSeekStrategy waterSeekStrategy = new SimpleWaterSeekStrategy();

    protected float hungerDecay = 0.08f;   // Tốc độ giảm chỉ số đói mỗi tick
    protected float energyDrain = 0.05f;   // Tốc độ tiêu hao năng lượng mỗi tick

    public PredatorAnimalModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, double speed, double directionX, double directionY) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, "predator", speed, directionX, directionY);
    }

    public PredatorAnimalModel(EntityCoordinate position) {
        super(position);
    }

    @Override
    public void Interact(List<EntityModel> nearbyEntities) {
        Species species = getSpecies();

        // Kiểm tra xem target cũ còn dùng được không
        if (currentTarget != null) {
            boolean targetDead = currentTarget.getHealth() <= 0;
            boolean huntExpired = !huntStrategy.isStillTracking();

            if (targetDead || huntExpired) {
                currentTarget = null;
            }
        }

        // Nếu chưa có target, quét tầm nhìn tìm con mồi mới
        if (currentTarget == null && species != null) {
            currentTarget = findPreyInFOV(nearbyEntities, species);
        }

        if (currentTarget != null) {
            huntStrategy.setTarget(currentTarget);
            activeStrategy = huntStrategy;
        } else {
            activeStrategy = roamStrategy;
        }
    }
    @Override
    public void updateAndMove(BlockModel[][] blocksData) {
        // 1. Ưu tiên đi uống nước khi khát (thanh nước < 40f)
        if (getThirst() < 40f) {
            waterSeekStrategy.tick(this, blocksData);
            return;
        }

        // 2. Mặc định chạy chiến thuật săn mồi hoặc lang thang
        if (activeStrategy != null) {
            activeStrategy.tick(this, List.of());
        }
    }

    @Override
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        // Tiêu hao đói và năng lượng
        setHunger(this.hunger - hungerDecay * (1.0f + speedFactor));
        setEnergy(this.energy - energyDrain * (1.0f + speedFactor));

        if (hunger < 15f) {
            this.health -= 2; // Thú ăn thịt mất máu nhanh khi đói
        }
        if (energy <= 0f) {
            this.health -= 1; // Mất máu do kiệt sức
        }
    }

    public EntityModel getCurrentTarget() {
        return currentTarget;
    }

    private EntityModel findPreyInFOV(List<EntityModel> nearbyEntities, Species species) {
        EntityModel nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityModel entity : nearbyEntities) {
            if (entity == this) continue;
            if (!(entity instanceof PreyAnimalModel)) continue;
            if (entity.getHealth() <= 0) continue;

            boolean inFov = isInFieldOfView(
                    entity.getPosition(),
                    species.getFovRadians(),
                    species.getViewDistance()
            );
            if (!inFov) continue;

            double dist = distanceTo(entity.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }
}