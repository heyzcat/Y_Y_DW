//daisy class is used to simulate daisy, this class will extend 
//white daisy and black daisy
enum Type {
	WHITE, BLACK
}
public class Daisy {
	private Type type;
	private int currentAge;
	
	//get the type and set 
	public Daisy(Type t) {
		type = t;
		currentAge = 0;
	}
	
	//judge whether this daisy is alive, when its current age is greater than
	//its life expanse, it is dead, or it is alive
	public boolean isAlive() {
		return Params.MAX_AGE >= currentAge;
	}
	
	/*if it is a seed daisy, which means age = 0, return true
	 */
	public boolean isSeed() {
		return currentAge > 1 ? false : true;
	}
	
	//increase the age of this daisy by one
	public void increaseAge() {
		this.currentAge++;
	}
	
	//set the current age of daisy
	public void setAge(int age) {
		this.currentAge = age;
	}
	//get the type the daisy
	public Type getType() {
		return this.type;
	}
	
}
