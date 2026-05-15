package view;

import javafx.scene.image.Image;
import model.block.BlockModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Return the visual representation of a block.
 * For now, it accepts a blockModel and return an Image to render on screen
 */
public class GuiBlockView {
    private final Map<String, List<Image>> blockTextureMap = new HashMap<>();


    public void registerTextures(String blockType, List<Path> texturesPath){
        if(!texturesPath.stream().allMatch(Files::exists)) return; // temporary, should throw exception

        List<Image> imageList = texturesPath.stream()
                .map(path -> new Image(path.toUri().toString()))
                .toList();
        blockTextureMap.put(blockType, imageList);

        IO.println("Block " + blockType + " has been registered");
    }

    public Image getBlockTexture(BlockModel blockModel){
        if(blockTextureMap.containsKey(blockModel.getBlockType())){
            return blockTextureMap.get(blockModel.getBlockType()).get(blockModel.getCurrentState());
        }
        else{
            return null;
        }
    }
}
