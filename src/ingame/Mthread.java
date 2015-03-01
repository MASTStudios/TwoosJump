package ingame;

public abstract class Mthread extends Thread{
	
	protected Object callback;

	public Mthread(Object callback){
		this.callback=callback;
	}
}
