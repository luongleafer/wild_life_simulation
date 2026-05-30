package view.entity;

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

    public void registerEntity(String entityType, Path file, int index) throws IndexOutOfBoundsException{
        Image image = new Image(file.toUri().toString());
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
    }

    // Get the texture associate with the entity
    public Image getEntityTexture(EntityModel model, int index) {
        return entityTextureMap.get(model.getEntityType()).get(index);
    }
}
