package generation;
import model.block.BlockModel;
public class WaterBlock extends BlockModel{
	public WaterBlock(int x, int y, int initialState, int sinkability) {
		super(x,y,initialState);
		this.blockType='water';
		this.sinkability=5;
		this.totalState=1;
	}
    public void animate() {
        this.currentState = (this.currentState + 1) % this.totalStates;
    }

}