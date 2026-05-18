package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.world.WorldModel;
import view.GuiBlockView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestGuiBlockView extends Application {
    GuiBlockView guiBlockView = GuiBlockView.getInstance();
    static void main() {
    }

    private void registerBlockTextures(){
        guiBlockView.registerTextures("dirt", List.of(
                Path.of("assets/dirt.png")
        ));
        guiBlockView.registerTextures("grass", List.of(
                Path.of("assets/grass_block_top.png")
        ));
        guiBlockView.registerTextures("sand", List.of(
                Path.of("assets/sand.png")
        ));
        guiBlockView.registerTextures("water", List.of(
                Path.of("assets/water_still_oneblock.png")
        ));
        guiBlockView.registerTextures("wood", List.of(
                Path.of("assets/oak_log.png")
        ));
        guiBlockView.registerTextures("mud", List.of(
                Path.of("assets/mud.png")
        ));
    }

    @Override
    public void start(Stage stage) throws Exception {
        registerBlockTextures();

    }
}
