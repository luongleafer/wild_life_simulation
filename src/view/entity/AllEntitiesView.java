package view.entity;

import controller.WorldController;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import model.entity.EntityModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AllEntitiesView {
    // map an Entity to a View on screen
    private final Map<EntityModel, EntityView> modelViewMap = new ConcurrentHashMap<>();
    EntityTextureMap entityTextureMap;
    Pane allEntitiesPane;
    List<EntityModel> waitToRender = new ArrayList<>();

    public AllEntitiesView(EntityTextureMap entityTextureMap, Pane allEntitiesPane) {
       this.entityTextureMap = entityTextureMap;
       this.allEntitiesPane = allEntitiesPane;
    }

    // Add new view when an entity is spawned
    public void addView(EntityModel model){
        Image texture = entityTextureMap.getEntityTexture(model, model.getCurrentState());
        EntityView renderer = new EntityView(model, texture, '?', 32, 32);
        modelViewMap.put(model, renderer);
        allEntitiesPane.getChildren().add(renderer.getSprite());
    }

    public void requestRender(EntityModel model){
        waitToRender.add(model);
    }

    // Remove view when an entity die
    public void removeView(EntityModel model){
        modelViewMap.remove(model);
    }
    public void setEntityTextureMap(EntityTextureMap entityTextureMap) {
        this.entityTextureMap = entityTextureMap;
    }

    public void refresh(){
        allEntitiesPane.getChildren().clear();

        if(entityTextureMap == null){
            IO.println("[EntityRenderer] Texture map not found");
            return;
        }

        modelViewMap.forEach((model, entityView) -> {
            // update view for entities base on current state
            entityView.setSprite(entityTextureMap.getEntityTexture(model, model.getCurrentState()));
            // update screen position for movement
            entityView.updateScreenPosition(WorldController.WORLD_TILE_SIZE, 0, 0);
            // add the ImageView to pane
            allEntitiesPane.getChildren().add(entityView.getSprite());
        });

        if(!waitToRender.isEmpty()){
            waitToRender.forEach(this::addView);
            waitToRender.clear();
        }
    }
}
