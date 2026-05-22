package model.block;

import java.lang.reflect.InvocationTargetException;

public class BlockModels {
    public static <T extends BlockModel> BlockModel from(BlockModel block) {
        return from(block, 0, 0, 0);
    }

    public static <T extends BlockModel> BlockModel from(BlockModel block, int x, int y, int initialState) {
        BlockModel newModel = null;
        try {
            newModel = block.getClass()
                    .getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE)
                    .newInstance(x, y, initialState);
        } catch (Exception e){
            IO.println("Exception when create new block of type " + block.getClass().getName() + " : " + e.getMessage());
        }
        return newModel;
    }
}
