package generation;
import model.block.BlockModel;
public class GrassBlock extends BlockModel{
	public GrassBlock(int x, int y, int initialState, int sinkability) {
		super(x,y,initialState);
		this.blockType='grass';
		this.sinkability=3;
		this.totalState=1;
	}

}