package view.entity;

import javafx.scene.image.Image;
import model.entity.EntityModel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EntityTextureMap {
    Map<Class<? extends EntityModel>, Image> entityTextureMap = new HashMap<>();
    private EntityTextureMap() {

    }

    private static EntityTextureMap instance;
    public static EntityTextureMap getInstance() {
        if (instance == null) {
            instance = new EntityTextureMap();
        }
        return instance;
    }

    public void registerEntity(EntityModel model, Path file) {
        entityTextureMap.put(model.getClass(), new Image(file.toUri().toString()));
    }

    public Image getEntityTexture(EntityModel model) {
        return entityTextureMap.get(model.getClass());
    }
}
