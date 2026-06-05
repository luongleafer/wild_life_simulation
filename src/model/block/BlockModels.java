package model.block;

import java.lang.reflect.InvocationTargetException;

public class BlockModels {

    /**
     * Create new block with the same type as another block at coordinate (0,0) and state 0
     * @param block The block to create
     * @return The new block
     * @param <T> Type of block, which derives from BlockModel
     */
    public static <T extends BlockModel> BlockModel from(BlockModel block) {
        return from(block, 0, 0);
    }

    /**
     * Create a new block from another block with the specified parameter
     * This requires the blocks to have a constructor with 3 int parameter
     * @param block The block to create
     * @param x The x position in world
     * @param y The y position in world
     * @return A new block of the same type as `block`
     * @param <T> The type of block, which derives from BlockModel
     */
    public static <T extends BlockModel> BlockModel from(BlockModel block, int x, int y) {
        BlockModel newModel = null;
        try {
            newModel = block.getClass()
                    .getConstructor(Integer.TYPE, Integer.TYPE)
                    .newInstance(x, y);
        } catch (Exception e){
            IO.println("Exception when create new block of type " + block.getClass().getName() + " : " + e.getMessage());
        }
        return newModel;
    }
}
