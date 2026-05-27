package view.entity;

import javafx.scene.image.Image;
import model.entity.EntityModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiEntityView {
    private final Map<EntityModel, EntityView> modelViewMap = new HashMap<>();

    private static GuiEntityView instance;

    public static GuiEntityView getInstance() {
        if (instance == null) {
            instance = new GuiEntityView();
        }
        return instance;
    }

    private GuiEntityView() {}

    public void addView(EntityModel model){
        Image texture = EntityTextureMap.getInstance().getEntityTexture(model);
        EntityView renderer = new EntityView(model, texture, '?', 32, 32);
        modelViewMap.put(model, renderer);
    }

    public void removeView(EntityModel model){
        modelViewMap.remove(model);
    }

    public List<EntityView> getRenderers(){
        return modelViewMap.values().stream().toList();
    }
}
