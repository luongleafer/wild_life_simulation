package view.block;

import controller.WorldController;
import javafx.scene.image.Image;
import model.block.BlockModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage graphical representation of a block.
 * For now, it accepts a blockModel and return an Image to render on screen
 */
public class BlockTextureMap {
    private final Map<String, Image> blockTextureMap = new HashMap<>();

    // This class is implemented as a Singleton
    // This mean only one object of this class can be instantiated.
    // That object can be accessed anywhere in the code by BlockTextureMap.getInstance(),
    // allows each block to register their own textures if needed.


    public BlockTextureMap() {

    }


    /**
     * Register the blockType and associated textures to the globalMap.
     * @param blockType The type of block ("dirt", "grass",...).
     * @param imagePath The Path for texture files.
     */
    public void registerTextures(String blockType, Path imagePath){
        // check if blocks is already registered
        if(blockTextureMap.containsKey(blockType)){
            return;
        }
        // check if path exists
        if(!Files.exists(imagePath)) return;

        // map from path to JavaFX's Image objects to display on the screen.
        // texture is auto-scaled by the world's tile size
        Image image = new Image(imagePath.toUri().toString(),
                                WorldController.WORLD_TILE_SIZE,
                                WorldController.WORLD_TILE_SIZE,
                                true,
                                true
        );

        blockTextureMap.put(blockType, image);


        IO.println("[BlockTextureMap] Block " + blockType + " has been registered");
    }

    /**
     * Return the Image object associate with the blockModel.
     * The object is pull from the list registered in the blockTextureMap.
     * @param blockModel The blockModel to render
     * @return The Image that holds the texture for that blockModel
     * or null if the block type is not registered
     */
    public Image getBlockTexture(BlockModel blockModel){
        return blockTextureMap.getOrDefault(blockModel.getBlockType(), null);
    }
}
