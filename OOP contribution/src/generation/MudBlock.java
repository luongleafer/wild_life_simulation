package generation;
import model.block.BlockModel;
public class MudBlock extends BlockModel{
	public MudBlock(int x, int y, int initialState, int sinkability) {
		super(x,y,initialState);
		this.blockType='mud';
		this.sinkability=4;
		this.totalState=1;
	}

}
