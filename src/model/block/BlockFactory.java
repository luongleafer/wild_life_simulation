package model.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockFactory {
    private static final Map<String, BlockCreation> blockCreationMap = new HashMap<>();

    public static void register(String blockType, BlockCreation blockCreation) {
        blockCreationMap.put(blockType, blockCreation);
    }

    public static BlockModel create(String blockType, int x, int y) {
        BlockCreation blockCreation = blockCreationMap.get(blockType);
        if (blockCreation == null) {
            return null;
        }
        return blockCreation.create(x, y);
    }

    public static BlockModel createFrom(BlockModel block){
        return create(block.getBlockType(), block.position.x, block.position.y);
    }

    public static Set<String> allBlockType(){
        return blockCreationMap.keySet();
    }

}
