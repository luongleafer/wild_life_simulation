package model.entity;

import model.animals.behavior.BehaviorStrategy;
import model.animals.species.Species;
import controller.WorldController;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.block.ObstacleBlockModel;
import model.world.WorldModel;


import java.util.Random;
import java.util.function.Predicate;

public abstract class AnimalModel extends EntityModel {
    public static double mateChance = 0.001;
    public static final double mateDistance = 1;
    public static final long maxTicksBetweenBirth = 400; // animals can only mate each 20 seconds
    // health reduce when hungry/thirsty, can be affected by, for example, season
    static public double healthDepletionRate = 1;
    // hunger reduce when moving, per distance in moveByDistance()
    static public double hungerDepletionRate = 0.1;
    static public double thirstDepletionRate = 0.1;

    // since we are following the Minecraft model here...
    // the values will be floats
    private double hunger;
    private double thirst;
    private double energy;

    protected double maxThirst;
    protected double maxHunger;
    protected double maxEnergy;

    protected double healthDepletionMultiplier = 1;
    protected double hungerDepletionMultiplier = 1;
    protected double thirstDepletionMultiplier = 1;

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
    private static final double DEFAULT_HITBOX_RADIUS = 0.35;
    private static final double COLLISION_STEP_SIZE = 0.12;
    private static final double NEEDS_THRESHOLD_RATIO = 0.4;
    private static final double[] AVOIDANCE_ANGLE_OFFSETS_DEG = {
            20, -20, 35, -35, 50, -50, 70, -70, 90, -90, 120, -120, 150, -150, 180
    };

    protected long birthCooldown = 0;

    // Main methods
    // eat(food) food can be other Entity or Block, depend on the specific implementation of the animal
    public void eat(Edible food) {
        if (food == null || !food.canBeEaten()) {
            return;
        }
        this.hunger += food.getHungerValue();// công them vao thanh hunger
        this.energy += food.getEnergyValue();
    }

    public void drink(Drinkable block) {
        if (block == null || !block.canBeDrank()) {
            return;
        }
        this.thirst += block.getThirstValue();
        this.energy += block.getEnergyValue();
    }
    public void updateMetabolism() {} // ham cap nhat qua trinh trao doi chat

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
    public void updateAndMove(BlockModel[][] blocksData) {
        updateMetabolism(); // cap nhat cac thanh nang luong, mau,...
        move();
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

    protected void moveByDistance(double distance) {
        // Internal move helper that uses the current direction vector.
        if (distance == 0.0 || (directionX == 0.0 && directionY == 0.0)) {
            return;
        }
        double effectiveDistance = distance * getTerrainSpeedMultiplier();
        if(effectiveDistance <= 0) return;
        double movedDistance = moveByDistanceWithCollision(effectiveDistance);
        if(movedDistance <= 0) return;
        hunger -= movedDistance * hungerDepletionRate * hungerDepletionMultiplier;
        thirst -= movedDistance * thirstDepletionRate * thirstDepletionMultiplier;
    }

    private double getTerrainSpeedMultiplier() {
        WorldModel worldModel = getWorldModel();
        if(worldModel == null || getPosition() == null) return 1.0;
        int x = (int)Math.floor(getPosition().getPosX());
        int y = (int)Math.floor(getPosition().getPosY());
        BlockModel standingBlock = worldModel.getBaseBlockAt(x, y);
        if(standingBlock != null && "mud".equals(standingBlock.getBlockType())){
            return 0.55;
        }
        return 1.0;
    }

    private WorldModel getWorldModel() {
        WorldController controller = WorldController.getController();
        if(controller == null) return null;
        return controller.getWorldModel();
    }

    private double moveByDistanceWithCollision(double requestedDistance) {
        double startX = getPosition().getPosX();
        double startY = getPosition().getPosY();
        WorldModel worldModel = getWorldModel();
        if(worldModel == null){
            move(startX + directionX * requestedDistance, startY + directionY * requestedDistance);
            return requestedDistance;
        }

        int steps = Math.max(1, (int)Math.ceil(requestedDistance / COLLISION_STEP_SIZE));
        double stepDistance = requestedDistance / steps;
        double currentX = startX;
        double currentY = startY;

        for(int i = 0; i < steps; i++){
            double nextX = currentX + directionX * stepDistance;
            double nextY = currentY + directionY * stepDistance;
            if(isPositionBlocked(nextX, nextY, worldModel)){
                double[] avoidanceStep = findAvoidanceStep(currentX, currentY, stepDistance, worldModel);
                if(avoidanceStep == null){
                    break;
                }
                currentX = avoidanceStep[0];
                currentY = avoidanceStep[1];
                setDirection(avoidanceStep[2], avoidanceStep[3]);
                continue;
            }
            currentX = nextX;
            currentY = nextY;
        }

        if(currentX == startX && currentY == startY){
            return 0;
        }
        move(currentX, currentY);
        return Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY));
    }

    private double[] findAvoidanceStep(double currentX, double currentY, double stepDistance, WorldModel worldModel) {
        double currentAngle = Math.atan2(directionY, directionX);
        for(double offsetDeg : AVOIDANCE_ANGLE_OFFSETS_DEG){
            double candidateAngle = currentAngle + Math.toRadians(offsetDeg);
            double candidateDirX = Math.cos(candidateAngle);
            double candidateDirY = Math.sin(candidateAngle);
            double nextX = currentX + candidateDirX * stepDistance;
            double nextY = currentY + candidateDirY * stepDistance;
            if(!isPositionBlocked(nextX, nextY, worldModel)){
                return new double[]{nextX, nextY, candidateDirX, candidateDirY};
            }
        }
        return null;
    }

    private boolean isPositionBlocked(double xPos, double yPos, WorldModel worldModel) {
        return collidesWithObstacles(xPos, yPos, worldModel) || collidesWithOtherAnimals(xPos, yPos, worldModel);
    }

    private boolean collidesWithObstacles(double xPos, double yPos, WorldModel worldModel) {
        double radius = getHitboxRadius();
        int minX = Math.max(0, (int)Math.floor(xPos - radius));
        int maxX = Math.min(worldModel.getWidth() - 1, (int)Math.floor(xPos + radius));
        int minY = Math.max(0, (int)Math.floor(yPos - radius));
        int maxY = Math.min(worldModel.getLength() - 1, (int)Math.floor(yPos + radius));

        for(int x = minX; x <= maxX; x++){
            for(int y = minY; y <= maxY; y++){
                BlockModel baseBlock = worldModel.getBaseBlockAt(x, y);
                BlockModel overlayBlock = worldModel.getOverlayBlockAt(x, y);
                if(isBlockingObstacle(baseBlock) && circleIntersectsTile(xPos, yPos, radius, x, y)){
                    return true;
                }
                if(isBlockingObstacle(overlayBlock) && circleIntersectsTile(xPos, yPos, radius, x, y)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlockingObstacle(BlockModel block) {
        return block instanceof ObstacleBlockModel obstacle && obstacle.blocksMovement();
    }

    private boolean circleIntersectsTile(double centerX, double centerY, double radius, int tileX, int tileY) {
        double nearestX = clamp(centerX, tileX, tileX + 1.0);
        double nearestY = clamp(centerY, tileY, tileY + 1.0);
        double dx = centerX - nearestX;
        double dy = centerY - nearestY;
        return dx * dx + dy * dy < radius * radius;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean collidesWithOtherAnimals(double xPos, double yPos, WorldModel worldModel) {
        double myRadius = getHitboxRadius();
        double currentX = getPosition().getPosX();
        double currentY = getPosition().getPosY();
        for(EntityModel entity : worldModel.getEntities()){
            if(!(entity instanceof AnimalModel other) || other == this || other.getHealth() <= 0){
                continue;
            }
            double combinedRadius = myRadius + other.getHitboxRadius();
            double currentDx = currentX - other.getPosition().getPosX();
            double currentDy = currentY - other.getPosition().getPosY();
            double currentDistanceSquared = currentDx * currentDx + currentDy * currentDy;
            double dx = xPos - other.getPosition().getPosX();
            double dy = yPos - other.getPosition().getPosY();
            double nextDistanceSquared = dx * dx + dy * dy;
            if(nextDistanceSquared < combinedRadius * combinedRadius){
                if(currentDistanceSquared < combinedRadius * combinedRadius && nextDistanceSquared > currentDistanceSquared){
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    protected double getHitboxRadius() {
        return DEFAULT_HITBOX_RADIUS;
    }

    protected boolean shouldSeekFood() {
        return hunger <= maxHunger * NEEDS_THRESHOLD_RATIO;
    }

    protected boolean shouldSeekWater() {
        return thirst <= maxThirst * NEEDS_THRESHOLD_RATIO;
    }

    protected boolean moveTowardNearestWater(double trackingRadius, double speedMultiplier, double stopDistance) {
        EntityCoordinate target = findNearestWaterTarget(trackingRadius);
        if(target == null){
            return false;
        }
        moveToward(target, speedMultiplier, stopDistance);
        return true;
    }

    protected boolean moveTowardNearestFood(double trackingRadius, double speedMultiplier, double stopDistance) {
        EntityCoordinate target = findNearestFoodTarget(trackingRadius);
        if(target == null){
            return false;
        }
        moveToward(target, speedMultiplier, stopDistance);
        return true;
    }

    protected EntityCoordinate findNearestWaterTarget(double radius) {
        return findNearestBlockCenterInRadius(radius, block -> block != null && "water".equals(block.getBlockType()));
    }

    protected EntityCoordinate findNearestFoodTarget(double radius) {
        return null;
    }

    protected EntityCoordinate findNearestBlockCenterInRadius(double radius, Predicate<BlockModel> blockPredicate) {
        if(blockPredicate == null) return null;
        WorldModel worldModel = getWorldModel();
        if(worldModel == null) return null;

        EntityCoordinate myPos = getPosition();
        int minX = Math.max(0, (int)Math.floor(myPos.getPosX() - radius));
        int maxX = Math.min(worldModel.getWidth() - 1, (int)Math.floor(myPos.getPosX() + radius));
        int minY = Math.max(0, (int)Math.floor(myPos.getPosY() - radius));
        int maxY = Math.min(worldModel.getLength() - 1, (int)Math.floor(myPos.getPosY() + radius));

        double bestDistance = Double.MAX_VALUE;
        EntityCoordinate bestTarget = null;

        for(int x = minX; x <= maxX; x++){
            for(int y = minY; y <= maxY; y++){
                bestTarget = selectNearestBlockTarget(worldModel.getBaseBlockAt(x, y), x, y, myPos, radius, blockPredicate, bestDistance, bestTarget);
                if(bestTarget != null){
                    bestDistance = myPos.distance(bestTarget);
                }
                bestTarget = selectNearestBlockTarget(worldModel.getOverlayBlockAt(x, y), x, y, myPos, radius, blockPredicate, bestDistance, bestTarget);
                if(bestTarget != null){
                    bestDistance = myPos.distance(bestTarget);
                }
            }
        }
        return bestTarget;
    }

    private EntityCoordinate selectNearestBlockTarget(BlockModel block,
                                                      int xPos,
                                                      int yPos,
                                                      EntityCoordinate origin,
                                                      double radius,
                                                      Predicate<BlockModel> blockPredicate,
                                                      double bestDistance,
                                                      EntityCoordinate currentBest) {
        if(block == null || !blockPredicate.test(block)){
            return currentBest;
        }
        EntityCoordinate center = new EntityCoordinate(xPos + 0.5, yPos + 0.5);
        double distance = origin.distance(center);
        if(distance <= radius && distance < bestDistance){
            return center;
        }
        return currentBest;
    }

    protected EntityModel findNearestEntityInRadius(double radius, Predicate<EntityModel> entityPredicate) {
        if(entityPredicate == null) return null;
        WorldModel worldModel = getWorldModel();
        if(worldModel == null) return null;

        EntityCoordinate myPos = getPosition();
        double radiusSquared = radius * radius;
        EntityModel best = null;
        double bestDistanceSquared = Double.MAX_VALUE;

        for(EntityModel entity : worldModel.getEntities()){
            if(entity == null || entity == this || !entityPredicate.test(entity)){
                continue;
            }
            double dx = entity.getPosition().getPosX() - myPos.getPosX();
            double dy = entity.getPosition().getPosY() - myPos.getPosY();
            double distanceSquared = dx * dx + dy * dy;
            if(distanceSquared <= radiusSquared && distanceSquared < bestDistanceSquared){
                best = entity;
                bestDistanceSquared = distanceSquared;
            }
        }
        return best;
    }

    protected void headTowards(BlockCoordinate targetBlock){
        double newDirectionX = targetBlock.x -  getPosition().getPosX();
        double newDirectionY = targetBlock.y -  getPosition().getPosY();
//        setDirection(newDirectionX, newDirectionY);
        alterDirection(newDirectionX, newDirectionY, 1.0);
    }

    protected void headAwayFrom(BlockCoordinate targetBlock, double priority){
        double newDirectionX = getPosition().getPosX() - targetBlock.x;
        double newDirectionY = getPosition().getPosY() - targetBlock.y;
//        setDirection(newDirectionX, newDirectionY);
        alterDirection(newDirectionX, newDirectionY, priority);
    }

    protected void headRandomly(){
        double alterX = new Random().nextDouble() * 2 - 1;
        double alterY = new Random().nextDouble() * 2 - 1;
        alterDirection(alterX, alterY, 1);
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

    protected AnimalModel(EntityCoordinate position,
                          double maxHealth,
                          double maxHunger,
                          double maxThirst,
                          double maxEnergy
                          ){
        super(position);
        this.maxHealth = maxHealth;
        this.maxHunger = maxHunger;
        this.maxThirst = maxThirst;
        this.maxEnergy = maxEnergy;
        setupStats();
    }

    public AnimalModel(EntityCoordinate position){
        this(position, 10, 10, 10, 10);
        this.survivalStrategy = "survival";
        this.direction = Direction.STAY();
    }
    public double getHunger() {
        return hunger;
    }
    public void setHunger(double hunger) {
        this.hunger = Math.max(0f, Math.min(100f, hunger));
    }
    public double getThirst() {
        return thirst;
    }
    public void setThirst(double thirst) {
        this.thirst = Math.max(0f, Math.min(100f, thirst));
    }

    public double getEnergy() {
        return energy;
    }
    public void setEnergy(double energy) {
        this.energy = Math.max(0f, Math.min(100f, energy));
    }

    @Override
    protected void setupStats(){
        super.setupStats();
        this.hunger = maxHunger / 2;
        this.thirst = maxThirst / 2;
        this.energy = maxEnergy / 2;
    }

    // check if animal is hungry, can be overridden
    protected boolean isHungry(){
        return hunger <= maxHunger / 2;
    }

    protected boolean isThirsty(){
        return thirst <= maxThirst / 2;
    }

    @Override
    public void ageUp() {
        super.ageUp();
        // animals slowly dying of hunger and thirst
        if(hunger <= 0){
            hunger = 0;
            health -= healthDepletionRate * healthDepletionMultiplier;
        }
        if(thirst <= 0){
            thirst = 0;
            health -= healthDepletionRate * healthDepletionMultiplier;
        }
        birthCooldown++;
    }

    @Override
    public void Interact(BlockModel block) {
        if(block instanceof ObstacleBlockModel obstacle){
            BlockCoordinate obstaclePos = obstacle.getPosition();
            headAwayFrom(obstaclePos, 1.0);
        }
    }

    @Override
    public void Interact(EntityModel entity) {
        if(entity.getEntityType().equals(entityType)){
            Interact((AnimalModel) entity);
        }
    }

    private void Interact(AnimalModel other){
        if(readyToMate() && other.readyToMate()
                && position.distance(other.position) <= mateDistance
                && new Random().nextDouble() < mateChance
        ){
            birthCooldown = 0;
            other.birthCooldown = 0;
            WorldController.getController().requestSpawnEntity(EntityFactory.createFrom(this));
        }
    }

    protected void alterDirection(double deltaX, double deltaY, double priority){
        setDirection(directionX + deltaX * priority, directionY + deltaY * priority);
    }

    public boolean readyToMate(){
        int maxTicksBetweenBirth = 400;
        return birthCooldown >= maxTicksBetweenBirth;
    }
}
