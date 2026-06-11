package view.entity;

import controller.WorldController;
import javafx.scene.image.Image;
import model.entity.EntityCoordinate;
import model.entity.EntityFactory;
import model.entity.EntityModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
     * Register texture for an Entity specific state with different scaling in each axis
     * @param entityType Type of Entity
     * @param file Path to texture, in form of "assets/..."
     * @param index State of Entity
     * @throws IndexOutOfBoundsException When index is larger than the current entity's total number of state registered
     */
    public void registerEntity(String entityType, Path file, int index) throws IndexOutOfBoundsException{
        Image image = new Image(file.toUri().toString(),
                                WorldController.WORLD_TILE_SIZE,
                                WorldController.WORLD_TILE_SIZE,
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
        List<Image> textures = entityTextureMap.get(model.getEntityType());
        if(textures == null || textures.isEmpty()) return null;
        if(index >= 0 && index < textures.size() && textures.get(index) != null){
            return textures.get(index);
        }
        return textures.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static EntityTextureMap loadFrom(Path path){
        int maxState = 8;
        EntityTextureMap entityTextureMap = new EntityTextureMap();
        Set<String> allEntityTypes = EntityFactory.allEntityString();
        if(allEntityTypes.isEmpty()) return null;
        for(String entityType : allEntityTypes){
            for(int i = 0; i<maxState; i++){
                Path thisStatePath = Paths.get(path.toString(), "entity", entityType + "_" + i + ".png");
                if(Files.exists(thisStatePath)){
                    double scale = 1.0;
                    if ("elephant".equalsIgnoreCase(entityType)) {
                        scale = 2.5; // Kích thước lớn cho voi
                    }
                    entityTextureMap.registerEntity(entityType, thisStatePath, i, scale, scale);
                }
                else{
                    IO.println("Try to load file at " + thisStatePath + " but doesn't exist");
                }
            }
        }
        return entityTextureMap;
    }
}
