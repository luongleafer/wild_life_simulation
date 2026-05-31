package view.entity;

import controller.WorldController;
import javafx.scene.image.Image;
import model.entity.EntityModel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map an Entity with its texture, represent as Image
 * Model as Singleton for program-wide access
 */
public class EntityTextureMap {
    Map<String, List<Image>> entityTextureMap = new HashMap<>();
    public EntityTextureMap() {

    }

    // Register an image with an Entity
    public void registerEntity(String entityType, List<Path> files) {
        for(int i = 0; i < files.size(); ++i) {
            registerEntity(entityType, files.get(i),i);
        }
    }

    /**
     * Register texture for an Entity specific state with size = 1x1 square of the world.
     * Calls {@link #registerEntity(String, Path, int, double, double)} with widthScale and heightScale = 1
     */
    public void registerEntity(String entityType, Path file, int index) throws IndexOutOfBoundsException{
        registerEntity(entityType, file, index, 1, 1);
    }

    /**
     * Register texture for an Entity specific state with different scaling in each axis
     * @param entityType Type of Entity
     * @param file Path to texture, in form of "assets/..."
     * @param index State of Entity
     * @param widthScale Width scale of entity
     * @param lengthScale Length scale of entity
     * @throws IndexOutOfBoundsException When index is larger than the current entity's total number of state registered
     */
    public void registerEntity(String entityType, Path file, int index, double widthScale, double lengthScale) throws IndexOutOfBoundsException{
        Image image = new Image(file.toUri().toString(),
                                WorldController.WORLD_TILE_SIZE * widthScale,
                                WorldController.WORLD_TILE_SIZE * lengthScale,
                                false,
                                true
                                );
        if(entityTextureMap.containsKey(entityType)) {
            List<Image> entityTextures =  entityTextureMap.get(entityType);
            if(index < 0 || index > entityTextures.size()) {
                throw new IndexOutOfBoundsException();
            }
            else if (index == entityTextures.size()) {
                entityTextures.add(image);
            }
            else{
                entityTextures.set(index, image);
            }
        }
        else{
            List<Image> imageList = new ArrayList<>();
            for(int i = 0; i< index;i++){
                imageList.add(null);
            }
            imageList.add(image);
            entityTextureMap.put(entityType, imageList);
        }
        IO.println("[EntityTextureMap] Entity of type " + entityType + " at state " + index + " has been registered");
    }

    // Get the texture associate with the entity
    public Image getEntityTexture(EntityModel model, int index) {
        return entityTextureMap.get(model.getEntityType()).get(index);
    }
}
