package view.entity;

import javafx.scene.image.Image;
import model.entity.EntityModel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiEntityView {
    private Map<EntityModel, EntityRenderer> modelViewMap = new HashMap<>();

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
        EntityRenderer renderer = new EntityRenderer(model, texture, '?',16, 16);
        modelViewMap.put(model, renderer);
    }

    public List<EntityRenderer> getRenderers(){
        return modelViewMap.values().stream().toList();
    }
}
