package model.entity;

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
    private final String survivalStrategy;

    // Movement is defined by a speed (blocks per tick) and a unit direction vector.
    // The direction vector is normalized so speed alone controls distance per tick.
    private double speed; // blocks per tick
    private double directionX;
    private double directionY;

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
        move();
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

    // Constructor for position ONLY
    public AnimalModel(EntityCoordinate position) {
        super(position);
    }
}
