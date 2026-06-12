package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;
import model.block.BlockFactory;
import model.block.BlockModel;
import model.entity.AnimalModel;
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
    private Label interactionStatusLabel;
    @FXML
    private ComboBox<String> interactionModeBox;
    @FXML
    private ComboBox<String> assetModeBox;
    @FXML
    private ComboBox<String> spawnKindBox;
    @FXML
    private ComboBox<String> spawnTypeBox;
    @FXML
    private ComboBox<String> selectKindBox;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label selectedTypeValueLabel;
    @FXML
    private Label selectedNameValueLabel;
    @FXML
    private Label selectedBiomeValueLabel;
    @FXML
    private Label selectedHealthValueLabel;
    @FXML
    private Label selectedSpeedValueLabel;
    @FXML
    private Label selectedHungerValueLabel;
    @FXML
    private Label selectedThirstValueLabel;
    @FXML
    private Label selectedEnergyValueLabel;
    @FXML
    private Label selectedSinkabilityValueLabel;

    private static final String MODE_SPAWN = "Spawn";
    private static final String MODE_SELECT = "Select";
    private static final String NONE_VALUE = "-";

    private WorldModel worldModel;
    private WorldController worldController;
    private int simulationTps = 20;
    private Timeline dashboardTimer;

    @FXML
    private void initialize() {
        interactionModeBox.setItems(FXCollections.observableArrayList(MODE_SPAWN, MODE_SELECT));
        interactionModeBox.getSelectionModel().select(MODE_SPAWN);
        interactionModeBox.setOnAction(event -> refreshInteractionStatus());

        spawnKindBox.setItems(FXCollections.observableArrayList("Entity", "Block"));
        spawnKindBox.getSelectionModel().select("Entity");
        spawnKindBox.setOnAction(event -> refreshSpawnTypeOptions());

        spawnTypeBox.setOnAction(event -> refreshInteractionStatus());

        selectKindBox.setItems(FXCollections.observableArrayList("Entity", "Block"));
        selectKindBox.getSelectionModel().select("Entity");
        selectKindBox.setOnAction(event -> refreshInteractionStatus());

        assetModeBox.setOnAction(event -> {
            if(worldController == null) return;
            String selectedMode = assetModeBox.getValue();
            if(selectedMode != null){
                worldController.setTexturePath(selectedMode);
                refreshDashboard();
            }
        });

        refreshSpawnTypeOptions();
        clearSelectionInfo();
        refreshInteractionStatus();
    }

    public void bind(WorldModel worldModel, WorldController worldController, int simulationTps) {
        this.worldModel = worldModel;
        this.worldController = worldController;
        this.simulationTps = Math.max(1, simulationTps);

        assetModeBox.setItems(FXCollections.observableArrayList(worldController.getTextureModes()));
        assetModeBox.getSelectionModel().select(worldController.getCurrentTextureMode());

        refreshSpawnTypeOptions();
        refreshInteractionStatus();
        refreshDashboard();
        startDashboardTimer();
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double zoom = newVal.doubleValue();
            worldController.setZoomFactor(zoom);
            worldController.getWorldView().setZoom(zoom);
        });
    }

    public void handleWorldClick(double mouseX, double mouseY) {
        if(worldModel == null || worldController == null) return;

        double tileSize = WorldController.WORLD_TILE_SIZE * worldController.getZoomFactor();
        int x = (int)Math.floor(mouseX / tileSize);
        int y = (int)Math.floor(mouseY / tileSize);
        if(x < 0 || y < 0 || x >= worldModel.getWidth() || y >= worldModel.getLength()){
            return;
        }

        String mode = interactionModeBox.getValue();
        if(MODE_SELECT.equals(mode)){
            selectAt(x, y, selectKindBox.getValue());
            refreshInteractionStatus();
            return;
        }
        spawnAtCurrentSelection(x, y);
        refreshDashboard();
        refreshInteractionStatus();
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
        refreshInteractionStatus();
    }

    private void spawnAtCurrentSelection(int xPos, int yPos) {
        String spawnKind = spawnKindBox.getValue();
        String spawnType = spawnTypeBox.getValue();
        if(spawnKind == null || spawnType == null) return;

        if("Entity".equals(spawnKind)){
            EntityModel entity = EntityFactory.create(spawnType, new EntityCoordinate(xPos + 0.5, yPos + 0.5));
            worldController.requestSpawnEntity(entity);
            return;
        }
        BlockModel block = BlockFactory.create(spawnType, xPos, yPos);
        worldController.placeBlock(block, xPos, yPos);
    }

    private void selectAt(int xPos, int yPos, String selectType) {
        if("Block".equals(selectType)){
            BlockModel block = worldModel.getBlockAt(xPos, yPos);
            if(block != null){
                updateBlockSelectionInfo(block, xPos, yPos);
                return;
            }
            clearSelectionInfo();
            return;
        }

        EntityModel entity = worldModel.getEntityAt(xPos, yPos);
        if(entity != null){
            updateEntitySelectionInfo(entity);
            return;
        }
        clearSelectionInfo();
    }

    private void updateEntitySelectionInfo(EntityModel entity) {
        selectedTypeValueLabel.setText("Entity");
        selectedNameValueLabel.setText(entity.getEntityType());
        selectedBiomeValueLabel.setText(worldModel.getBiomeNameAt(entity.getPosition()));
        selectedHealthValueLabel.setText(formatValue(entity.getHealth()));

        if(entity instanceof AnimalModel animal){
            selectedSpeedValueLabel.setText(formatValue(animal.getSpeed()));
            selectedHungerValueLabel.setText(formatValue(animal.getHunger()));
            selectedThirstValueLabel.setText(formatValue(animal.getThirst()));
            selectedEnergyValueLabel.setText(formatValue(animal.getEnergy()));
        } else {
            selectedSpeedValueLabel.setText(NONE_VALUE);
            selectedHungerValueLabel.setText(NONE_VALUE);
            selectedThirstValueLabel.setText(NONE_VALUE);
            selectedEnergyValueLabel.setText(NONE_VALUE);
        }
        selectedSinkabilityValueLabel.setText(NONE_VALUE);
    }

    private void updateBlockSelectionInfo(BlockModel block, int xPos, int yPos) {
        selectedTypeValueLabel.setText("Block");
        selectedNameValueLabel.setText(block.getBlockType());
        selectedBiomeValueLabel.setText(worldModel.getBiomeNameAt(xPos, yPos));
        selectedHealthValueLabel.setText(NONE_VALUE);
        selectedSpeedValueLabel.setText(NONE_VALUE);
        selectedHungerValueLabel.setText(NONE_VALUE);
        selectedThirstValueLabel.setText(NONE_VALUE);
        selectedEnergyValueLabel.setText(NONE_VALUE);
        selectedSinkabilityValueLabel.setText(String.valueOf(block.getSinkability()));
    }

    private void clearSelectionInfo() {
        selectedTypeValueLabel.setText(NONE_VALUE);
        selectedNameValueLabel.setText(NONE_VALUE);
        selectedBiomeValueLabel.setText(NONE_VALUE);
        selectedHealthValueLabel.setText(NONE_VALUE);
        selectedSpeedValueLabel.setText(NONE_VALUE);
        selectedHungerValueLabel.setText(NONE_VALUE);
        selectedThirstValueLabel.setText(NONE_VALUE);
        selectedEnergyValueLabel.setText(NONE_VALUE);
        selectedSinkabilityValueLabel.setText(NONE_VALUE);
    }

    private void refreshInteractionStatus() {
        String mode = interactionModeBox.getValue();
        boolean spawnMode = MODE_SPAWN.equals(mode);
        spawnKindBox.setDisable(!spawnMode);
        spawnTypeBox.setDisable(!spawnMode);
        selectKindBox.setDisable(spawnMode);
        if(!spawnMode){
            String selectType = selectKindBox.getValue();
            interactionStatusLabel.setText("Select mode: Click map to inspect " +
                    (selectType == null ? "object" : selectType.toLowerCase(Locale.US)));
            return;
        }

        String kind = spawnKindBox.getValue();
        String type = spawnTypeBox.getValue();
        if(kind == null || type == null){
            interactionStatusLabel.setText("Spawn mode: select kind/type");
            return;
        }
        interactionStatusLabel.setText("Spawn mode: Click map to place " + kind + " [" + type + "]");
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

    private String formatValue(double value){
        return String.format(Locale.US, "%.2f", value);
    }
}