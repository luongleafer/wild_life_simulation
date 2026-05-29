package view.entity;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import model.entity.EntityModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiEntityView {
    // map an Entity to a View on screen
    private final Map<EntityModel, EntityView> modelViewMap = new HashMap<>();
    EntityTextureMap entityTextureMap;
    Pane allEntitiesPane;

    public GuiEntityView(EntityTextureMap entityTextureMap, Pane allEntitiesPane) {
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

    // Remove view when an entity die
    public void removeView(EntityModel model){
        modelViewMap.remove(model);
    }

    public List<EntityView> getRenderers(){
        // update view
        modelViewMap.forEach((model, entityView) -> {
            entityView.setSprite(entityTextureMap.getEntityTexture(model, model.getCurrentState()));
        });
        return modelViewMap.values().stream().toList();
    }

    public void setEntityTextureMap(EntityTextureMap entityTextureMap) {
        this.entityTextureMap = entityTextureMap;
    }

    public void refresh(){
        allEntitiesPane.getChildren().clear();
        getRenderers().forEach(entityView -> {
            entityView.updateScreenPosition(16, 0,0);
            allEntitiesPane.getChildren().add(entityView.getSprite());
        });
    }
}
