package view.block;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.block.BlockModel;
import model.block.BlockModels;

/**
 * Render the world's terrain in a grid
 */
public class TerrainView {
    private final GridPane gridPane;
    private final ImageView[][] imageViews;
    int width;
    int length;
    BlockTextureMap textureMap;

    /**
     * Construct a rectangle grid view of the world's terrain
     * @param width The width of the world
     * @param length The length of the world
     * @param pane The GridPane to render to
     */
    public TerrainView(int width, int length, GridPane pane, BlockTextureMap textureMap) {
        this.width = width;
        this.length = length;
        gridPane = pane;
        imageViews = new ImageView[width][length];
        gridPane.getChildren().clear();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
                imageViews[x][y] = new ImageView();
                gridPane.add(imageViews[x][y], x, y);
            }
        }
        this.textureMap = textureMap;
    }

    public void refresh(BlockModel[][] blocksData){
        if(gridPane == null) return;
        for (int x = 0; x < width; x++){
            for(int y = 0; y < length; y++){
               if(blocksData[x][y] == null){
                   IO.println("[TerrainView] NULL blockModel at " + x + "; " + y);
                   continue;
               }
               imageViews[x][y].setImage(textureMap.getBlockTexture(blocksData[x][y]));
            }
        }
    }
}
