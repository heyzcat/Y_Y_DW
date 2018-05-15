import java.util.Random;

//run the system in this class
public class Main {

	public static void main(String[] args) {
		 Daisyworld daisyworld = new Daisyworld();
		 
		 
		 for(int i = 0; i < 1000; i++) {
			 daisyworld.tick();
			 
		 }
	}

}
