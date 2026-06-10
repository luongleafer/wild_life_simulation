package model.entity;

import model.animals.behavior.FleeStrategy;
import model.animals.behavior.FollowStrategy;
import model.animals.behavior.RoamStrategy;
import model.animals.behavior.SimpleEatStrategy;
import model.animals.behavior.SimpleWaterSeekStrategy;
import model.animals.species.Species;
import model.block.BlockModel;

import java.util.List;

/**
 * Lớp trừu tượng cho các thực thể là con mồi (Prey). Quản lý chạy trốn, bám mẹ, ăn và uống.
 */
public abstract class PreyAnimalModel extends AnimalModel {

    private EntityModel currentThreat;     // Kẻ săn mồi đe dọa hiện tại
    private AnimalModel currentLeader;     // Con trưởng thành dẫn đầu

    // Các chiến thuật sinh tồn
    private final RoamStrategy roamStrategy = new RoamStrategy();
    private final FleeStrategy fleeStrategy = new FleeStrategy(null);
    private final FollowStrategy followStrategy = new FollowStrategy(null);
    private final SimpleEatStrategy eatStrategy = new SimpleEatStrategy();
    private final SimpleWaterSeekStrategy waterSeekStrategy = new SimpleWaterSeekStrategy();

    protected float hungerRate = 0.02f;   // Tỷ lệ đói mỗi tick
    protected float thirstRate = 0.01f;   // Tỷ lệ khát mỗi tick

    private List<EntityModel> lastNearbyEntities = List.of();
    private static final double FLEE_TRIGGER_DISTANCE = 8.0; // Khoảng cách bắt đầu chạy trốn

    public PreyAnimalModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, double speed, double directionX, double directionY) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState, hunger, thirst, energy, "prey", speed, directionX, directionY);
    }

    public PreyAnimalModel(EntityCoordinate position) {
        super(position);
    }

    @Override
    public void Interact(List<EntityModel> nearbyEntities) {
        this.lastNearbyEntities = nearbyEntities;
        Species species = getSpecies();

        currentThreat = findNearestThreat(nearbyEntities);

        if (currentThreat != null) {
            fleeStrategy.setThreat(currentThreat);
            activeStrategy = fleeStrategy;
        } else if (isBaby() && species != null) {
            currentLeader = findNearestAdultSameSpecies(nearbyEntities);
            if (currentLeader != null) {
                followStrategy.setLeader(currentLeader);
                activeStrategy = followStrategy;
            } else {
                activeStrategy = roamStrategy;
            }
        } else {
            activeStrategy = roamStrategy;
        }
    }

    @Override
    public void updateAndMove(BlockModel[][] blocksData) {
        // 1. Chạy trốn khỏi kẻ săn mồi
        if (activeStrategy == fleeStrategy) {
            fleeStrategy.tick(this, lastNearbyEntities);
            return;
        }

        // 2. Tìm nước khi khát (thanh nước < 40f)
        if (getThirst() < 40f) {
            waterSeekStrategy.tick(this, blocksData);
            return;
        }

        // 3. Tìm đồ ăn khi đói (thanh đói < 40f)
        if (getHunger() < 40f) {
            eatStrategy.tick(this, lastNearbyEntities, blocksData);
            return;
        }

        // 4. Lang thang hoặc đi theo con mẹ
        if (activeStrategy != null) {
            activeStrategy.tick(this, lastNearbyEntities);
        }
    }

    @Override
    public void updateMetabolism() {
        float speedFactor = (float) getSpeed();
        // Tiêu hao năng lượng và nước uống theo thời gian
        setHunger(this.getHunger() - hungerRate * (1.0f + speedFactor));
        setThirst(this.getThirst() - thirstRate * (1.0f + speedFactor));

        if (getHunger() < 20f) {
            this.health -= 1;
        }

        if (getThirst() < 20f) {
            this.health -= 1;
        }
    }

    private EntityModel findNearestThreat(List<EntityModel> nearbyEntities) {
        EntityModel nearest = null;
        double minDist = FLEE_TRIGGER_DISTANCE;

        for (EntityModel entity : nearbyEntities) {
            if (!(entity instanceof PredatorAnimalModel)) continue;
            if (entity == this) continue;

            double dist = distanceTo(entity.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    private AnimalModel findNearestAdultSameSpecies(List<EntityModel> nearbyEntities) {
        AnimalModel nearest = null;
        double minDist = Double.MAX_VALUE;

        for (EntityModel entity : nearbyEntities) {
            if (entity == this) continue;
            if (!(entity instanceof PreyAnimalModel prey)) continue;

            if (!matchesEntityType(prey)) continue;
            if (!prey.isAdult() && !prey.isOld()) continue;

            double dist = distanceTo(prey.getPosition());
            if (dist < minDist) {
                minDist = dist;
                nearest = prey;
            }
        }
        return nearest;
    }

    /**
     * Kiểm tra xem entity khác có cùng entityType với con vật này không.
     *
     * @param other entity cần so sánh
     * @return true nếu cùng loài
     */
    private boolean matchesEntityType(EntityModel other) {
        String myType = getEntityType();
        String otherType = other.getEntityType();
        return myType != null && myType.equals(otherType);
    }
}