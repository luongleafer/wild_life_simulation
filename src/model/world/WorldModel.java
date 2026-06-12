package model.world;

import model.biome.BiomeModel;
import model.biome.BiomeType;
import model.biome.ForestBiomeModel;
import model.biome.PlainBiomeModel;
import model.biome.WaterBiomeModel;
import model.block.BlockCoordinate;
import model.block.BlockModel;
import model.block.ObstacleBlock;
import model.block.ObstacleBlockModel;
import model.entity.AnimalModel;
import model.entity.AquaticCreature;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import model.generation.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * The core data model and simulation engine for the wild life simulation.
 * This class acts as a central hub that holds all block data, biome data,
 * and entity data. It is responsible for:
 * 1. Procedurally generating the initial terrain using simplex noise.
 * 2. Updating the simulation state (seasons, terrain interactions).
 * 3. Ticking entities and enforcing physical constraints (like drowning and boundaries).
 */
public class WorldModel {
    private static final long DROWNING_TICKS = 100; // 5 seconds at 20 ticks/sec

    // Core Data Grids
    private final Map<BiomeType, BiomeModel> biomeRules;
    private final BiomeType[][] biomeMap; // Assigns a biome to every coordinate
    private final boolean[][] shallowWaterMap; // Tracks which water blocks are shallow vs deep

    // Noise Maps used during generation
    private final float[][] elevationMap;
    private final float[][] moistureMap;
    private final float[][] forestDensityMap;

    // Simulation Timing
    private long tickCount;
    private int tickSpeed;

    // Physical World State
    private BlockModel[][] blocksData; // Base terrain (dirt, water, sand)
    private BlockModel[][] overlayBlocks; // Objects placed on top of terrain (trees, bushes)
    private int width;
    private int length;

    // Entities
    private List<EntityModel> entities =  new ArrayList<>();
    private int entityPadding = 1; // Margin from the world border where entities cannot pass

    Random random = new Random();

    // Seasons Logic
    private Season currentSeason = Season.SPRING;
    private final long seasonLength = 400; // 400 ticks, 20 seconds
    private final int seasonVariance = 100;
    private long ticksSinceLastSeason = 0;
    private long currentSeasonLength = 400;

    // Tracks how long land animals have been standing in deep water
    private final Map<EntityModel, Long> deepWaterExposureTicks = new HashMap<>();

    public WorldModel(int width, int length) {
        this.width = width;
        this.length = length;
        this.blocksData = new BlockModel[width][length];
        this.overlayBlocks = new BlockModel[width][length];
        this.biomeMap = new BiomeType[width][length];
        this.shallowWaterMap = new boolean[width][length];
        this.elevationMap = new float[width][length];
        this.moistureMap = new float[width][length];
        this.forestDensityMap = new float[width][length];
        this.biomeRules = Map.of(BiomeType.WATER, new WaterBiomeModel(),
                                 BiomeType.PLAIN, new PlainBiomeModel(),
                                 BiomeType.FOREST, new ForestBiomeModel());
        this.tickCount = 0;
        this.tickSpeed = 1; // 1 tick per update
    }

    public BlockModel[][] getBlocksData() {
        return blocksData;
    }

    public BlockModel[][] getOverlayBlocks() {
        return overlayBlocks;
    }

    public BlockModel getBlockAt(int xPos, int yPos) {
        if(!isInBounds(xPos, yPos)) return null;
        BlockModel overlayBlock = overlayBlocks[xPos][yPos];
        if(overlayBlock != null){
            return overlayBlock;
        }
        return blocksData[xPos][yPos];
    }

    public BlockModel getBaseBlockAt(int xPos, int yPos){
        if(!isInBounds(xPos, yPos)) return null;
        return blocksData[xPos][yPos];
    }

    public BlockModel getOverlayBlockAt(int xPos, int yPos){
        if(!isInBounds(xPos, yPos)) return null;
        return overlayBlocks[xPos][yPos];
    }

    /**
     * Gets the nearest entity to the specified block coordinate.
     */
    public EntityModel getEntityAt(int xPos, int yPos) {
        if(!isInBounds(xPos, yPos)) return null;
        double centerX = xPos + 0.5;
        double centerY = yPos + 0.5;
        EntityModel nearest = null;
        double nearestDistance = 0.75;
        for(EntityModel entity : entities){
            if(entity == null) continue;
            EntityCoordinate position = entity.getPosition();
            double dx = position.getPosX() - centerX;
            double dy = position.getPosY() - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if(distance <= nearestDistance){
                nearest = entity;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public BiomeType getBiomeAt(int xPos, int yPos){
        if(!isInBounds(xPos, yPos)) return null;
        return biomeMap[xPos][yPos];
    }

    public BiomeType getBiomeAt(EntityCoordinate position){
        if(position == null) return null;
        int xPos = (int)Math.floor(position.getPosX());
        int yPos = (int)Math.floor(position.getPosY());
        return getBiomeAt(xPos, yPos);
    }

    public String getBiomeNameAt(int xPos, int yPos){
        BiomeType biome = getBiomeAt(xPos, yPos);
        return biome == null ? "Unknown" : biome.name();
    }

    public String getBiomeNameAt(EntityCoordinate position){
        BiomeType biome = getBiomeAt(position);
        return biome == null ? "Unknown" : biome.name();
    }

    public boolean isForestSafeZone(EntityCoordinate position){
        return getBiomeAt(position) == BiomeType.FOREST;
    }

    public List<EntityModel> getEntities() {
        return entities;
    }

    public void advanceTickCount(){
        tickCount += tickSpeed;
        ticksSinceLastSeason += tickSpeed;
    }

    /**
     * Manages season transitions, updating global multipliers for entity metabolism
     * and reproduction when a season changes.
     */
    public void updateSeason(){
        if(ticksSinceLastSeason < currentSeasonLength) return;
        ticksSinceLastSeason = 0;
        currentSeason = switch (currentSeason) {
            case SPRING -> Season.SUMMER;
            case SUMMER -> Season.AUTUMN;
            case AUTUMN -> Season.WINTER;
            case WINTER -> Season.SPRING;
        };
        currentSeasonLength = seasonLength + random.nextInt(seasonVariance);
        AnimalModel.hungerDepletionRate = currentSeason.animalHungerDepletionRate;
        AnimalModel.healthDepletionRate = currentSeason.animalHealthDepletionRate;
        AnimalModel.thirstDepletionRate = currentSeason.animalThirstDepletionRate;
        AnimalModel.mateChance = currentSeason.animalMateChance;
    }

    public void setTickSpeed(int tickSpeed) {
        this.tickSpeed = tickSpeed;
    }

    /**
     * Procedurally generates the terrain using OpenSimplex2 noise.
     * The generation happens in three passes:
     * 1. Noise Maps -> Biomes: Generates elevation, moisture, and forest density, then assigns a biome.
     * 2. Context Analysis: Analyzes the biome map to determine which water regions are shallow vs deep.
     * 3. Block Materialization: Asks the assigned BiomeModel to instantiate the actual BlockModel
     *    based on the exact noise values and water depth context for that coordinate.
     */
    public void generateTerrain() {
        NoiseGeneration elevationNoise = new NoiseGeneration();
        elevationNoise.SetNoiseType(NoiseGeneration.NoiseType.OpenSimplex2);
        elevationNoise.SetSeed(random.nextInt());

        NoiseGeneration moistureNoise = new NoiseGeneration();
        moistureNoise.SetNoiseType(NoiseGeneration.NoiseType.OpenSimplex2);
        moistureNoise.SetSeed(random.nextInt());
        moistureNoise.SetFrequency(0.02f);

        NoiseGeneration forestNoise = new NoiseGeneration();
        forestNoise.SetNoiseType( NoiseGeneration.NoiseType.OpenSimplex2);
        forestNoise.SetSeed(random.nextInt());
        forestNoise.SetFrequency(0.03f);

        // Pass 1: assign biome at each coordinate using noise fields.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                float elevation = elevationNoise.GetNoise(x * 5.0f, y * 5.0f);
                float moisture = moistureNoise.GetNoise(x * 5.0f, y * 5.0f);
                float forestDensity = forestNoise.GetNoise(x, y);
                elevationMap[x][y] = elevation;
                moistureMap[x][y] = moisture;
                forestDensityMap[x][y] = forestDensity;
                biomeMap[x][y] = provisionalBiomeFor(elevation);
            }
        }

        // Pass 2: detect shallow/deep water region from the biome map.
        rebuildShallowWaterMapFromBiomes();

        // Pass 3: Final materialization: generate blocks from biome + local noise context.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                BiomeModel rule = biomeRules.get(biomeMap[x][y]);
                blocksData[x][y] =
                            rule.createBlock(
                                x,
                                y,
                                elevationMap[x][y],
                                moistureMap[x][y],
                                forestDensityMap[x][y],
                                shallowWaterMap[x][y],
                                random);
                overlayBlocks[x][y] = null;
            }
        }

        // Keep shallow/deep classification aligned with actual water tiles after
        // final block materialization.
        rebuildShallowWaterMapFromBlocks();
        placeObstacle();
    }

    /**
     * Extracts block types that act as obstacles (trees, saplings, seeds) from the 
     * base blocksData grid and moves them to the overlayBlocks grid.
     */
    public void placeObstacle(){
        Random rand = new Random();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                BlockModel blockModel = blocksData[x][y];
                if(blockModel instanceof SeedBlock || blockModel instanceof SaplingBlock || blockModel instanceof TreeBlock){
                    overlayBlocks[x][y] = blockModel;
                    blocksData[x][y] = new DirtBlock(x,y);
                }
            }
        }
    }

    public void placeBlock(BlockModel newBlock) {
        if(newBlock == null) return;
        int x = newBlock.getPosition().x;
        int y = newBlock.getPosition().y;

        // check world boundaries before placing
        if (x >= 0 && x < width && y >= 0 && y < length) {
            blocksData[x][y] = newBlock;
            rebuildShallowWaterMapFromBlocks();
        }
    }

    public void placeObstacle(ObstacleBlockModel newObstacle){
        int x = newObstacle.getPosition().x;
        int y = newObstacle.getPosition().y;
        overlayBlocks[x][y] = newObstacle;
    }

    /**
     * The main simulation loop entry point. Advances time, updates seasons,
     * triggers slow terrain updates, and processes all entity logic.
     */
    public void update(){
        advanceTickCount();
        updateSeason();

        // Slow down block updates so it's visible to the human eye.
        // Terrain updates once every 50 ticks (approx. 1 second if tick is 20ms)
        if (tickCount % 50 == 0) {
            updateTerrain();
            rebuildShallowWaterMapFromBlocks();
        }

        updateEntities();
        updateDeepWaterDrowning();
    }

    public List<EntityModel> getDeadEntities(){
        return entities.stream()
                .filter(entityModel -> entityModel.getHealth() <= 0).toList();
    }

    private void removeDeadEntities(){
        entities.removeAll(getDeadEntities());
    }

    /**
     * Prompts all animals to move and enforces world boundary constraints.
     * Prevents entities from walking off the edge of the generated map by reversing
     * their direction if they hit the padded border.
     */
    private void entitiesMovement(){
        entities.stream()
                .filter(entity -> entity instanceof AnimalModel)
                .map(entity -> (AnimalModel) entity)
                .forEach(AnimalModel::move);

        entities.forEach(entityModel -> {
            EntityCoordinate position = entityModel.getPosition();
            if(position.getPosX() > width - entityPadding){
                position.setPosX(width - entityPadding - 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(-1, 0);
                }
            }
            if(position.getPosX() < entityPadding){
                position.setPosX(entityPadding + 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(1, 0);
                }
            }
            if(position.getPosY() > length - entityPadding){
                position.setPosY(length - entityPadding - 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(0, -1);
                }
            }
            if(position.getPosY() < entityPadding){
                position.setPosY(entityPadding + 1);
                if(entityModel instanceof AnimalModel animalModel){
                    animalModel.setDirection(0, 1);
                }
            }
        });
    }

    /**
     * List all Entities in a circle area
     * @param origin The center of the area
     * @param radius The radius of the area
     * @return All Entities in the area
     */
    List<EntityModel> getEntitiesInAnArea(EntityCoordinate origin, double radius){
        return entities.stream().filter(
                entityModel -> entityModel.getPosition().distance(origin) <= radius
        ).sorted(Comparator.comparing(entityModel -> entityModel.getPosition().distance(origin))).toList();
    }

    List<BlockModel> getBlocksInAnArea(EntityCoordinate origin, double reachRadius){
        List<BlockModel> blocks = new ArrayList<>();
        for(int x = 0 ; x < width ; x++){
            for(int y = 0 ; y < length ; y++){
                if(origin.distance(new EntityCoordinate(x + 0.5, y + 0.5)) <= reachRadius){
                    blocks.add(blocksData[x][y]);
                    if(overlayBlocks[x][y] != null){
                        blocks.add(overlayBlocks[x][y]);
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Processes interactions between entities, and between entities and nearby blocks.
     */
    private void entitiesInteraction(){
        entities.forEach(
                entityModel -> {
                    List<EntityModel> surrounding = getEntitiesInAnArea(entityModel.getPosition(), 5);
                    entityModel.Interact(surrounding);
                    List<BlockModel> surroundingBlocks = getBlocksInAnArea(entityModel.getPosition(), 2);
                    surroundingBlocks.forEach(entityModel::Interact);
                }
        );
    }

    private void updateEntities(){
        removeDeadEntities();
        entities.forEach(EntityModel::ageUp);
        entitiesMovement();
        entitiesInteraction();
    }

    /**
     * Kills non-aquatic entities if they remain in deep water for too many consecutive ticks.
     */
    private void updateDeepWaterDrowning() {
        Set<EntityModel> aliveEntities = new HashSet<>(entities);
        deepWaterExposureTicks.keySet().removeIf(entity ->
                                                         !aliveEntities.contains(entity) || entity.getHealth() <= 0);

        for(EntityModel entity : entities){
            if(entity == null || entity.getHealth() <= 0){
                continue;
            }
            if(isAquaticCreature(entity) || !isStandingOnDeepWater(entity)){
                deepWaterExposureTicks.remove(entity);
                continue;
            }
            long exposure = deepWaterExposureTicks.getOrDefault(entity, 0L) + tickSpeed;
            deepWaterExposureTicks.put(entity, exposure);
            if(exposure >= DROWNING_TICKS){
                entity.setHealth(0); // Drown
            }
        }
    }

    private boolean isAquaticCreature(EntityModel entity) {
        return entity instanceof AquaticCreature;
    }

    /**
     * Checks if the entity is currently standing on a tile identified as deep water.
     */
    private boolean isStandingOnDeepWater(EntityModel entity) {
        int x = (int) Math.floor(entity.getPosition().getPosX());
        int y = (int) Math.floor(entity.getPosition().getPosY());
        if(!isInBounds(x, y)){
            return false;
        }
        if(!isWaterAt(x, y)){
            return false;
        }
        return !shallowWaterMap[x][y];
    }

    /**
     * Defines the hard elevation thresholds for biomes during generation.
     */
    private BiomeType provisionalBiomeFor(float elevation) {
        if(elevation < -0.25f){
            return BiomeType.WATER;
        }
        if(elevation < 0.4f){
            return BiomeType.PLAIN;
        }
        return BiomeType.FOREST;
    }

    private void rebuildShallowWaterMapFromBiomes() {
        rebuildShallowWaterMap(true);
    }

    private void rebuildShallowWaterMapFromBlocks() {
        rebuildShallowWaterMap(false);
    }

    /**
     * Iterates through the world to find contiguous regions of water and delegates 
     * the depth calculation to a BFS algorithm.
     */
    private void rebuildShallowWaterMap(boolean fromBiomeMap) {
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                shallowWaterMap[x][y] = false;
            }
        }
        Set<Integer> visited = new HashSet<>();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                if(visited.contains(toCellId(x, y))) continue;
                if(!isWaterTileForClassification(x, y, fromBiomeMap)) continue;
                markWaterRegionDepth(x, y, fromBiomeMap, visited);
            }
        }
    }

    /**
     * Breadth-First Search (BFS) to find contiguous bodies of water. 
     * If a water region is extremely small (bounding box <= 3x3 AND total cells <= 9), 
     * it is marked as "shallow water" (e.g. a small pond). Otherwise, it is deep water (e.g. an ocean).
     */
    private void markWaterRegionDepth(int startX, int startY, boolean fromBiomeMap, Set<Integer> visitedGlobal) {
        Queue<int[]> queue = new ArrayDeque<>();
        List<int[]> regionCells = new ArrayList<>();

        queue.add(new int[]{startX, startY});
        visitedGlobal.add(toCellId(startX, startY));

        int minX = startX;
        int minY = startY;
        int maxX = startX;
        int maxY = startY;
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

        while(!queue.isEmpty()){
            int[] cell = queue.poll();
            int x = cell[0];
            int y = cell[1];
            regionCells.add(cell);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);

            for(int[] direction : directions){
                int nx = x + direction[0];
                int ny = y + direction[1];
                if(!isInBounds(nx, ny)) continue;
                if(!isWaterTileForClassification(nx, ny, fromBiomeMap)) continue;
                int id = toCellId(nx, ny);
                if(visitedGlobal.contains(id)) continue;
                visitedGlobal.add(id);
                queue.add(new int[]{nx, ny});
            }
        }

        // Depth heuristic: Small isolated ponds are shallow, large bodies are deep
        boolean isShallow = (maxX - minX + 1) <= 3 && (maxY - minY + 1) <= 3 && regionCells.size() <= 9;
        for(int[] cell : regionCells){
            shallowWaterMap[cell[0]][cell[1]] = isShallow;
        }
    }

    private boolean isWaterTileForClassification(int x, int y, boolean fromBiomeMap){
        if(fromBiomeMap){
            return biomeMap[x][y] == BiomeType.WATER;
        }
        return isWaterAt(x, y);
    }

    private boolean isInBounds(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < length;
    }

    public boolean isDeepWater(int x, int y) {
        if (!isInBounds(x, y)) {
            return false;
        }
        if (!isWaterAt(x, y)) {
            return false;
        }
        return !shallowWaterMap[x][y];
    }

    private boolean isWaterAt(int x, int y){
        BlockModel block = blocksData[x][y];
        return block != null && "water".equals(block.getBlockType());
    }

    /**
     * Converts a 2D coordinate to a 1D ID for efficient HashSet storage during BFS.
     */
    private int toCellId(int x, int y){
        return x * length + y;
    }

    private List<BlockModel> getSurroundingBlocks(BlockCoordinate origin){
        List<BlockModel> surroundingBlocks = new ArrayList<>();
        int x = origin.x;
        int y = origin.y;
        if(x > 0) surroundingBlocks.add(blocksData[x-1][y]);
        if(y > 0) surroundingBlocks.add(blocksData[x][y-1]);
        if(x < width - 1) surroundingBlocks.add(blocksData[x+1][y]);
        if(y < length - 1) surroundingBlocks.add(blocksData[x][y+1]);
        return surroundingBlocks;
    }

    /**
     * Triggers the "interact" method on all blocks, allowing blocks to change
     * state based on their neighbors (e.g. mud drying out, grass spreading).
     */
    private void updateTerrain(){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                blocksData[x][y] = blocksData[x][y].interact(getSurroundingBlocks(blocksData[x][y].getPosition()));
                if (overlayBlocks[x][y] != null) {
                    overlayBlocks[x][y] = overlayBlocks[x][y].interact(getSurroundingBlocks(overlayBlocks[x][y].getPosition()));
                }
            }
        }
    }

    public long getTickCount() {
        return tickCount;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public String getCurrentSeason(){
        return currentSeason.getName();

    }

}
