package model.entity;

import model.animals.behavior.BehaviorStrategy;
import model.animals.species.Species;

import java.util.Random;

public abstract class AnimalModel extends EntityModel {

    // since we are following the Minecraft model here...
    // the values will be floats
    protected float hunger;
    protected float thirst;
    protected float energy;
    // As seen in issue #8, this will be temporarily implemented using String.
    // though I don't know a better way to do this yet.
    // Possible acceptable keywords: predator, camouflage, defensive, etc...
    // but that is for later, when those behaviors are defined better
    protected String survivalStrategy;
    // Cau hinh loai vat, gom toc do, tam nhin va khoang cach follow.
    protected Species species;
    // Hanh vi dang duoc uu tien trong tick hien tai.
    protected BehaviorStrategy activeStrategy;
    // direction may mean that this animal is chasing/fleeing from other entities.
    // new method: use enums for direction
    protected double directionChangeChance = 0;

    // Movement is defined by a speed (blocks per tick) and a unit direction vector.
    // The direction vector is normalized so speed alone controls distance per tick.
    private double speed; // blocks per tick
    private double directionX;
    private double directionY;

    protected Direction direction;
    // Shared RNG for wandering/turning behaviors.
    private static final Random MOVE_RANDOM = new Random();

    // Main methods
    // eat(food) food can be other Entity or Block, depend on the specific implementation of the animal
    public void eat(Edible food) {
        if (food == null || !food.canBeEaten()) {
            return;
        }
        this.hunger += food.getHungerValue();
        this.energy += food.getEnergyValue();
    }

    public void drink(Drinkable block) {
        if (block == null || !block.canBeDrank()) {
            return;
        }
        this.thirst += block.getThirstValue();
        this.energy += block.getEnergyValue();
    }

    public double getSpeed() {
        return speed;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public BehaviorStrategy getActiveStrategy() {
        return activeStrategy;
    }

    public void setActiveStrategy(BehaviorStrategy activeStrategy) {
        this.activeStrategy = activeStrategy;
    }

    /**
     * Kiểm tra con vật có đang ở giai đoạn baby không (currentState == 0).
     * 0 = Baby, 1 = Adult, 2 = Old
     */
    public boolean isBaby() {
        return currentState == 0;
    }

    /**
     * Kiểm tra con vật có đang ở giai đoạn trưởng thành không (currentState == 1).
     */
    public boolean isAdult() {
        return currentState == 1;
    }

    /**
     * Kiểm tra con vật có đang ở giai đoạn già không (currentState == 2).
     */
    public boolean isOld() {
        return currentState == 2;
    }

    /**
     * Cập nhật life stage dựa trên tuổi hiện tại so với adultAge và oldAge.
     * Được gọi trong ageUp() hoặc override ở class con.
     * Chỉ tăng stage, không bao giờ giảm (một chiều theo thời gian).
     */
    public void updateLifeStage() {
        if (adultAge > 0 && age >= adultAge && currentState < 1) {
            currentState = 1; // Baby → Adult
        }
        if (oldAge > 0 && age >= oldAge && currentState < 2) {
            currentState = 2; // Adult → Old
        }
    }
    public void setSpeed(double speed) {
        // Keep speed non-negative so callers don't accidentally reverse movement.
        this.speed = Math.max(0.0, speed);
    }

    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirection(double directionX, double directionY) {
        // Normalize the input vector so direction has unit length.
        // A zero-length vector stops movement entirely.
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        if (magnitude == 0.0) {
            this.directionX = 0.0;
            this.directionY = 0.0;
            return;
        }
        this.directionX = directionX / magnitude;
        this.directionY = directionY / magnitude;
    }

    public void randomizeDirection() {
        // Pick a random unit vector by choosing a random angle in [0, 2π).
        double angle = MOVE_RANDOM.nextDouble() * Math.PI * 2.0;
        setDirection(Math.cos(angle), Math.sin(angle));
    }

    // Move to specified coordinate, basic implementation
    public void move(double x, double y) {
        // Direct position update; no collision or boundary checks here.
        getPosition().setPosX(x);
        getPosition().setPosY(y);
    }

    // Move in the current specified direction for one tick
    public void move() {
        // Advance by speed along the unit direction vector.
        moveByDistance(speed);
    }

    public void roamRandomly(double minSpeed, double maxSpeed, double maxTurnRadians) {
        // Randomly turn a little, then move at a randomized speed range.
        // This supports "normal" animals that wander around the map.
        if (maxSpeed < minSpeed) {
            double swap = minSpeed;
            minSpeed = maxSpeed;
            maxSpeed = swap;
        }
        if (directionX == 0.0 && directionY == 0.0) {
            randomizeDirection();
        } else {
            double angle = Math.atan2(directionY, directionX);
            double turn = (MOVE_RANDOM.nextDouble() * 2.0 - 1.0) * Math.max(0.0, maxTurnRadians);
            double newAngle = angle + turn;
            setDirection(Math.cos(newAngle), Math.sin(newAngle));
        }
        setSpeed(minSpeed + MOVE_RANDOM.nextDouble() * (maxSpeed - minSpeed));
//        move();
        moveByDistance(speed);
    }


    public void moveToward(EntityCoordinate target, double speedMultiplier) {
        // Convenience overload: no stop distance.
        moveToward(target, speedMultiplier, 0.0);
    }

    public void moveToward(EntityCoordinate target, double speedMultiplier, double stopDistance) {
        // For predators/babies: face target and advance, optionally stopping near it.
        if (target == null) {
            return;
        }
        double distance = distanceTo(target);
        if (distance <= stopDistance) {
            return;
        }
        setDirection(target.getPosX() - getPosition().getPosX(), target.getPosY() - getPosition().getPosY());
        moveByDistance(speed * Math.max(0.0, speedMultiplier));
    }
    /**
     * Di chuyển theo hướng ngược lại với vị trí nguồn nguy hiểm.
     * Dùng cho FleeStrategy khi con mồi né kẻ săn mồi.
     *
     * @param threat         vị trí của mối đe dọa cần tránh xa
     * @param speedMultiplier hệ số nhân tốc độ, thường là fleeSpeedMultiplier từ Species
     */
    public void moveAwayFrom(EntityCoordinate threat, double speedMultiplier) {
        if (threat == null) return;

        double dx = getPosition().getPosX() - threat.getPosX();
        double dy = getPosition().getPosY() - threat.getPosY();

        if (dx == 0.0 && dy == 0.0) {
            randomizeDirection(); // trùng vị trí, chạy ngẫu nhiên
        } else {
            setDirection(dx, dy); // hướng ngược lại kẻ săn mồi
        }
        moveByDistance(speed * Math.max(0.0, speedMultiplier));
    }

    public boolean isInFieldOfView(EntityCoordinate target, double fovRadians, double maxDistance) {
        // Check if the target is within the cone defined by direction and FOV.
        if (target == null || (directionX == 0.0 && directionY == 0.0)) {
            return false;
        }
        double dx = target.getPosX() - getPosition().getPosX();
        double dy = target.getPosY() - getPosition().getPosY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (maxDistance > 0.0 && distance > maxDistance) {
            return false;
        }
        if (distance == 0.0) {
            return true;
        }
        double targetX = dx / distance;
        double targetY = dy / distance;
        double dot = directionX * targetX + directionY * targetY;
        double halfFov = Math.max(0.0, fovRadians) / 2.0;
        return dot >= Math.cos(halfFov);
    }

    public double distanceTo(EntityCoordinate target) {
        // distance is Euclidean
        if (target == null) {
            return Double.POSITIVE_INFINITY;
        }
        double dx = target.getPosX() - getPosition().getPosX();
        double dy = target.getPosY() - getPosition().getPosY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void moveByDistance(double distance) {
        // Internal move helper that uses the current direction vector.
        if (distance == 0.0 || (directionX == 0.0 && directionY == 0.0)) {
            return;
        }
        double newX = getPosition().getPosX() + directionX * distance;
        double newY = getPosition().getPosY() + directionY * distance;
        move(newX, newY);
    }

    public AnimalModel(EntityCoordinate position, int health, int age, int adultAge, int oldAge, int totalLifespan, int currentState, float hunger, float thirst, float energy, String survivalStrategy, double speed, double directionX, double directionY) {
        super(position, health, age, adultAge, oldAge, totalLifespan, currentState);
        this.hunger = hunger;
        this.thirst = thirst;
        this.energy = energy;
        this.survivalStrategy = survivalStrategy;
        // Normalize direction and clamp speed on construction to keep movement consistent.
        this.speed = Math.max(0.0, speed);
        setDirection(directionX, directionY);
    }

    public AnimalModel(EntityCoordinate position){
        super(position);
        this.survivalStrategy = "survival";
        this.direction = Direction.STAY();
    }


}
