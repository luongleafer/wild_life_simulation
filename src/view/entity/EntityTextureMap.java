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
    Map<Class<? extends EntityModel>, List<Image>> entityTextureMap = new HashMap<>();
    private EntityTextureMap() {

    }

    private static EntityTextureMap instance;
    public static EntityTextureMap getInstance() {
        if (instance == null) {
            instance = new EntityTextureMap();
        }
        return instance;
    }

    // Register an image with an Entity
    public void registerEntity(EntityModel model, List<Path> files) {
        for(int i = 0; i < files.size(); ++i) {
            registerEntity(model, files.get(i),i);
        }
    }

    public void registerEntity(EntityModel model, Path file, int index) throws IndexOutOfBoundsException{
        Class<? extends EntityModel> entityClass = model.getClass();
        Image image = new Image(file.toUri().toString());
        if(entityTextureMap.containsKey(entityClass)) {
            List<Image> entityTextures =  entityTextureMap.get(entityClass);
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
            entityTextureMap.put(entityClass, imageList);
        }
    }

    // Get the texture associate with the entity
    public Image getEntityTexture(EntityModel model, int index) {
        return entityTextureMap.get(model.getClass()).get(index);
    }
}
