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
        } catch (InvocationTargetException e) {
            IO.println("Invocation Target Exception");
            IO.println(e.getMessage());
        } catch (InstantiationException e) {

            IO.println("Instantiation Exception");
            IO.println(e.getMessage());
        } catch (IllegalAccessException e) {

            IO.println("Illegal Access Exception");
            IO.println(e.getMessage());
        } catch (NoSuchMethodException e) {
            IO.println("No such constructor");
            IO.println(e.getMessage());
        }
        return newModel;
    }
}
