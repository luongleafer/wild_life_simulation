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

public class WorldModel {
    private static final long DROWNING_TICKS = 100; // 5 seconds at 20 ticks/sec
    private final Map<BiomeType, BiomeModel> biomeRules;
    private final BiomeType[][] biomeMap;
    private final boolean[][] shallowWaterMap;
    private final float[][] elevationMap;
    private final float[][] moistureMap;
    private long tickCount;
    private int tickSpeed;
    private BlockModel[][] blocksData;
    private BlockModel[][] overlayBlocks;
    private int width;
    private int length;
    private List<EntityModel> entities =  new ArrayList<>();
    private int entityPadding = 1;
    Random random = new Random();
    private Season currentSeason = Season.SPRING;
    private final long seasonLength = 400; // 400 ticks, 20 seconds
    private final int seasonVariance = 100;
    private long ticksSinceLastSeason = 0;
    private long currentSeasonLength = 400;
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

    public List<EntityModel> getEntities() {
        return entities;
    }

    public void advanceTickCount(){
        tickCount += tickSpeed;
        ticksSinceLastSeason += tickSpeed;
    }

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

    public void generateTerrain() {
        NoiseGeneration elevationNoise = new NoiseGeneration();
        elevationNoise.SetNoiseType(NoiseGeneration.NoiseType.OpenSimplex2);
        elevationNoise.SetSeed(random.nextInt());

        NoiseGeneration moistureNoise = new NoiseGeneration();
        moistureNoise.SetNoiseType(NoiseGeneration.NoiseType.Cellular);
        moistureNoise.SetSeed(random.nextInt());

        // Pass 1: assign biome at each coordinate using noise fields.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                float elevation = elevationNoise.GetNoise(x * 5.0f, y * 5.0f);
                float moisture = moistureNoise.GetNoise(x * 5.0f, y * 5.0f);
                elevationMap[x][y] = elevation;
                moistureMap[x][y] = moisture;
                biomeMap[x][y] = provisionalBiomeFor(elevation);
            }
        }

        // Pass 2: detect shallow/deep water region from the biome map.
        rebuildShallowWaterMapFromBiomes();

        // Final materialization: generate blocks from biome + local noise context.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                BiomeModel rule = biomeRules.get(biomeMap[x][y]);
                blocksData[x][y] = rule.createBlock(x,
                                                    y,
                                                    elevationMap[x][y],
                                                    moistureMap[x][y],
                                                    shallowWaterMap[x][y],
                                                    random);
                overlayBlocks[x][y] = null;
            }
        }

        // Keep shallow/deep classification aligned with actual water tiles after
        // final block materialization.
        rebuildShallowWaterMapFromBlocks();
//        placeObstacle();
    }

    public void placeObstacle(){
        Random rand = new Random();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                if(blocksData[x][y] instanceof DirtBlock && rand.nextDouble() < 0.02) {
                    overlayBlocks[x][y] = new CobbleStoneBlock(x,y,0);
                }
                if ((blocksData[x][y] instanceof GrassBlock || blocksData[x][y] instanceof DirtBlock) && rand.nextDouble() < 0.03) {
                    overlayBlocks[x][y] = new SeedBlock(x, y);
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
                entity.setHealth(0);
            }
        }
    }

    private boolean isAquaticCreature(EntityModel entity) {
        return entity instanceof AquaticCreature;
    }

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

    private boolean isWaterAt(int x, int y){
        BlockModel block = blocksData[x][y];
        return block != null && "water".equals(block.getBlockType());
    }

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
