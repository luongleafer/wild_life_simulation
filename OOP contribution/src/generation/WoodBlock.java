package generation;
import model.block.BlockModel;
public class WoodBlock extends BlockModel {
	public WoodBlock(int x, int y, int initial State, int sinkability) {
		super (x, y, initialState );
		this.blockType='wood';
		this.sinkability=0;
		this.totalState=1;
	}
}
