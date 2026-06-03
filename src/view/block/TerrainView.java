package view.block;

import controller.WorldController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.block.BlockModel;
import model.block.BlockModels;

/**
 * Render the world's terrain in a grid
 */
public class TerrainView {
    private final GridPane baseBlockGridPane;
    private final GridPane overlayBlockGridPane;
    private final StackPane rootPane;

    private final ImageView[][] imageViews;
    private final ImageView[][] overlayImageViews;
    int width;
    int length;
    BlockTextureMap textureMap;

    /**
     * Construct a rectangle grid view of the world's terrain
     * @param width The width of the world
     * @param length The length of the world
     * @param root The StackPane to render to
     */
    public TerrainView(int width, int length, StackPane root, BlockTextureMap textureMap) {
        this.width = width;
        this.length = length;
        this.rootPane = root;
        baseBlockGridPane = new GridPane();
        overlayBlockGridPane = new GridPane();
        rootPane.getChildren().clear();
        rootPane.getChildren().add(baseBlockGridPane);
        rootPane.getChildren().add(overlayBlockGridPane);
        imageViews = new ImageView[width][length];
        overlayImageViews = new ImageView[width][length];
        baseBlockGridPane.getChildren().clear();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                imageViews[x][y] = new ImageView();
                imageViews[x][y].setFitWidth(WorldController.WORLD_TILE_SIZE);
                imageViews[x][y].setFitHeight(WorldController.WORLD_TILE_SIZE);
                overlayImageViews[x][y] = new ImageView();
                overlayImageViews[x][y].setFitWidth(WorldController.WORLD_TILE_SIZE);
                overlayImageViews[x][y].setFitHeight(WorldController.WORLD_TILE_SIZE);
                baseBlockGridPane.add(imageViews[x][y], x, y);
                overlayBlockGridPane.add(overlayImageViews[x][y], x, y);
            }
        }
        this.textureMap = textureMap;
    }

    public void refresh(BlockModel[][] blocksData, BlockModel[][] overlayBlocksData){
        if(baseBlockGridPane == null) return;
        for (int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
               if(blocksData[x][y] == null){
                   IO.println("[TerrainView] NULL blockModel at " + x + "; " + y);
//                   continue;
               }
               else{
                   imageViews[x][y].setImage(textureMap.getBlockTexture(blocksData[x][y]));
               }
               if(overlayBlocksData[x][y] != null){
                   overlayImageViews[x][y].setImage(textureMap.getBlockTexture(overlayBlocksData[x][y]));
               }

            }
        }
    }

    public void setTextureMap(BlockTextureMap textureMap){
        this.textureMap = textureMap;
    }
}
