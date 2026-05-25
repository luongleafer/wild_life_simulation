package view;

import java.util.HashMap;
import java.util.Map;

import model.block.BlockModel;

public class BlockView {
    private Map<String, String[]> blockRenderMap;

    public BlockView() {
        blockRenderMap = new HashMap<>();
        // can be expanded to use file paths for textures in GUI
        blockRenderMap.put("water", new String[]{"~"});
        blockRenderMap.put("dirt", new String[]{"."});
        blockRenderMap.put("grass", new String[]{"g"});
        blockRenderMap.put("wood", new String[]{"T"});
        blockRenderMap.put("mud", new String[]{"m"});
        blockRenderMap.put("sand", new String[]{"s"});
        // obstacle/food block symbols
        blockRenderMap.put("rock", new String[]{"R"});
        blockRenderMap.put("bush", new String[]{"b"});
        blockRenderMap.put("berry", new String[]{"*"});
        blockRenderMap.put("default", new String[]{"?"});
    }

    public String getBlockDisplay(BlockModel block) {
        if (block == null) {
            return "?";
        }
        String[] renders = blockRenderMap.get(block.getBlockType());
        if (renders == null) {
            renders = blockRenderMap.get("default");
        }
        // We only have one state for now
        // Can use block.getCurrentState() later to get the correct display string
        return renders[0];
    }
}
