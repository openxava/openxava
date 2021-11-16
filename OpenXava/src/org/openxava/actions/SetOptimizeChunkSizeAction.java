package org.openxava.actions;

/**
 * tmr
 * @since 6.6.1
 * @author Javier Paniza
 */
public class SetOptimizeChunkSizeAction extends TabBaseAction {
	
	private boolean optimizeChunkSize;
	
	public void execute() throws Exception {
		System.out.println("[SetOptimizeChunkSizeAction.execute] optimizeChunkSize=" + optimizeChunkSize); // tmr
		getTab().setOptimizeChunkSize(optimizeChunkSize);
	}


	public boolean isOptimizeChunkSize() {
		return optimizeChunkSize;
	}


	public void setOptimizeChunkSize(boolean optimizeChunkSize) {
		this.optimizeChunkSize = optimizeChunkSize;
	}

}
