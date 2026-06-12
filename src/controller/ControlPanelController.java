package controller;

import javafx.animation.KeyFrame;
import javafx.scene.control.Slider;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.Duration;
import model.block.BlockFactory;
import model.block.BlockModel;
import model.block.ObstacleBlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityFactory;
import model.entity.EntityModel;
import model.world.WorldModel;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ControlPanelController {
    @FXML
    private Label elapsedTimeLabel;
    @FXML
    private Label seasonLabel;
    @FXML
    private Label entityStatsLabel;
    @FXML
    private Label activeAssetModeLabel;
    @FXML
    private Label spawnModeStatusLabel;
    @FXML
    private CheckBox spawnModeCheckBox;
    @FXML
    private ComboBox<String> assetModeBox;
    @FXML
    private ComboBox<String> spawnKindBox;
    @FXML
    private ComboBox<String> spawnTypeBox;
    @FXML
    private Slider zoomSlider;

    private WorldModel worldModel;
    private WorldController worldController;
    private int simulationTps = 20;
    private Timeline dashboardTimer;

    @FXML
    private void initialize() {
        spawnModeCheckBox.setSelected(false);
        spawnModeCheckBox.setOnAction(event -> refreshSpawnModeStatus());

        spawnKindBox.setItems(FXCollections.observableArrayList("Entity", "Block"));
        spawnKindBox.getSelectionModel().select("Entity");
        spawnKindBox.setOnAction(event -> refreshSpawnTypeOptions());

        spawnTypeBox.setOnAction(event -> refreshSpawnModeStatus());

        assetModeBox.setOnAction(event -> {
            if(worldController == null) return;
            String selectedMode = assetModeBox.getValue();
            if(selectedMode != null){
                worldController.setTexturePath(selectedMode);
                refreshDashboard();
            }
        });

        refreshSpawnTypeOptions();
        refreshSpawnModeStatus();
    }

    public void bind(WorldModel worldModel, WorldController worldController, int simulationTps) {
        this.worldModel = worldModel;
        this.worldController = worldController;
        this.simulationTps = Math.max(1, simulationTps);

        assetModeBox.setItems(FXCollections.observableArrayList(worldController.getTextureModes()));
        assetModeBox.getSelectionModel().select(worldController.getCurrentTextureMode());

        refreshSpawnTypeOptions();
        refreshSpawnModeStatus();
        refreshDashboard();
        startDashboardTimer();
        zoomSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> {

                    double zoom = newVal.doubleValue();

                    worldController.setZoomFactor(zoom);

                    worldController.getWorldView().setZoom(zoom);
                }
        );
    }

    public void handleWorldClick(double mouseX, double mouseY) {
        if(worldModel == null || worldController == null) return;
        if(!spawnModeCheckBox.isSelected()) return;

        String spawnKind = spawnKindBox.getValue();
        String spawnType = spawnTypeBox.getValue();
        if(spawnKind == null || spawnType == null){
            refreshSpawnModeStatus();
            return;
        }

        double tileSize = WorldController.WORLD_TILE_SIZE * worldController.getZoomFactor();
        int x = (int)Math.floor(mouseX / tileSize);
        int y = (int)Math.floor(mouseY / tileSize);
        if(x < 0 || y < 0 || x >= worldModel.getWidth() || y >= worldModel.getLength()){
            return;
        }

        spawnAt(spawnKind, spawnType, x, y);
        refreshDashboard();
        refreshSpawnModeStatus();
    }

    public void stop() {
        if(dashboardTimer != null){
            dashboardTimer.stop();
        }
    }

    private void startDashboardTimer() {
        stop();
        dashboardTimer = new Timeline(new KeyFrame(Duration.millis(200), event -> refreshDashboard()));
        dashboardTimer.setCycleCount(Timeline.INDEFINITE);
        dashboardTimer.play();
    }

    private void refreshSpawnTypeOptions() {
        List<String> options;
        if("Block".equals(spawnKindBox.getValue())){
            options = BlockFactory.allBlockType().stream()
                    .sorted(Comparator.naturalOrder())
                    .toList();
        } else {
            options = EntityFactory.allEntityString().stream()
                    .sorted(Comparator.naturalOrder())
                    .toList();
        }
        spawnTypeBox.setItems(FXCollections.observableArrayList(options));
        if(!options.isEmpty()){
            spawnTypeBox.getSelectionModel().select(0);
        }
        refreshSpawnModeStatus();
    }

    private void spawnAt(String spawnKind, String spawnType, int x, int y) {
        if("Entity".equals(spawnKind)){
            EntityModel entity = EntityFactory.create(spawnType, new EntityCoordinate(x + 0.5, y + 0.5));
            worldController.requestSpawnEntity(entity);
            return;
        }
        BlockModel block = BlockFactory.create(spawnType, x, y);
        if(block instanceof ObstacleBlockModel){

        }
        worldController.placeBlock(block, x, y);
    }

    private void refreshSpawnModeStatus() {
        String kind = spawnKindBox.getValue();
        String type = spawnTypeBox.getValue();
        if(!spawnModeCheckBox.isSelected()){
            spawnModeStatusLabel.setText("Spawn mode: OFF");
            return;
        }
        if(kind == null || type == null){
            spawnModeStatusLabel.setText("Spawn mode: ON (select kind/type)");
            return;
        }
        spawnModeStatusLabel.setText("Spawn mode: ON -> Click map to place " + kind + " [" + type + "]");
    }

    private void refreshDashboard() {
        if(worldModel == null || worldController == null) return;
        long ticks = worldModel.getTickCount();
        double seconds = ticks / (double) simulationTps;
        elapsedTimeLabel.setText(String.format(Locale.US, "Elapsed: %.1f s (%d ticks)", seconds, ticks));
        seasonLabel.setText("Season: " + worldModel.getCurrentSeason());
        activeAssetModeLabel.setText("Current mode: " + worldController.getCurrentTextureMode());

        Map<String, Long> groupedByType = worldModel.getEntities().stream()
                .collect(Collectors.groupingBy(EntityModel::getEntityType, Collectors.counting()));
        String detail = groupedByType.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
        entityStatsLabel.setText("Entities: " + worldModel.getEntities().size() +
                                 (detail.isEmpty() ? "" : " (" + detail + ")"));
    }
}
