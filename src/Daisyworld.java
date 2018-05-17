//system class to simulate the whole system
//including all ground patches and solar, and daisy
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Daisyworld {
	//list of ground patch
	private Patch[] patchArray;
	//number of white daisy
	private int numberWhite;
	//number of black daisy
	private int numberBlack;
	//global temperature
	private float globalTemperature;
	//generator of random number
	public static Random rand = new Random();
	
	//constructor, initial all ground patches
	public Daisyworld() {
		patchArray = new Patch[Params.NUMBER_PATCH];
		//initial all patches
		for(int i = 0; i < Params.NUMBER_PATCH; i++) {
			Patch p = new Patch(i);
			patchArray[i] = p;
			

					
		}
		
		initNeighbors();
		initialDaisy();
		globalTemperature = Params.START_TEMP;
	}
	
	//initial neighbor patches of each patch
	public void initNeighbors() {
		for(int i = 0; i < Params.NUMBER_PATCH; i++) {
			//the total system is a A x A square
			int[] neighborNum = {i - Params.WIDTH_GROUND -1,
								  i - Params.WIDTH_GROUND,
								  i - Params.WIDTH_GROUND + 1,
								  i - 1,
								  i + 1,
								  i + Params.WIDTH_GROUND - 1,
								  i + Params.WIDTH_GROUND,
								  i + Params.WIDTH_GROUND + 1
			};
			//list of neighbnor patches
			List<Patch> neiList = new ArrayList<>();
			//here are some rules to validate a neighbor
			//the number should be within the range
			//they should be neighbor/same row
			//they should be neighbor/same column
			for(int j = 0; j < neighborNum.length; j++)
				if(0 <= neighborNum[j] && 
						neighborNum[j] < Params.NUMBER_PATCH &&
						Math.abs(neighborNum[j] % Params.WIDTH_GROUND - i % Params.WIDTH_GROUND) <= 1 &&
						Math.abs(neighborNum[j] / Params.WIDTH_GROUND - i / Params.WIDTH_GROUND	) <= 1				
						)
					neiList.add(patchArray[neighborNum[j]]);
			
			//set the neighbor for each [atch
			patchArray[i].setNeighbors(neiList);
		}
	}
	//get the number of white daisy
	public int getNumWhite() {
		return this.numberWhite;
	}
	
	//get the number of black daisy
	public int getNumBlack() {
		return this.numberBlack;
	}
	
	//get the global temperature of system
	public float getGlobalTemp() {
		return this.globalTemperature;
	}
	
	//initial daisy
	//randomly choose certain percentage of patches to seed daisy
	//daisy has random age
	public void initialDaisy() {
		//set white daisy
		for(int i = 0; i < Params.NUMBER_PATCH * Params.START_PERC_WHITE; ) {
			//index of the chosen patch to set
			int index = rand.nextInt(Params.NUMBER_PATCH);
			
			//set daisy into patch
			if(patchArray[index].getState()) {
				//the daisy to set, random age
				Daisy daisy = new Daisy(Type.WHITE);
				daisy.setAge(rand.nextInt(Params.MAX_AGE));
				patchArray[index].setDaisy(daisy);
				i++;
			}						
		}
		//set black daisy
		for(int i = 0; i < Params.NUMBER_PATCH * Params.START_PERC_BLACK; ) {
			//index of the chosen patch to set
			int index = rand.nextInt(Params.NUMBER_PATCH);
			
			//set daisy into patch
			if(patchArray[index].getState()) {
				//the daisy to set, random age
				Daisy daisy = new Daisy(Type.BLACK);
				daisy.setAge(rand.nextInt(Params.MAX_AGE));
				patchArray[index].setDaisy(daisy);
				i++;
			}						
		}
	
	}
	//calculate the global temperature of the system
	//which is mean of local temperature of all patches
	public void calcuGlobalTemp() {
		float sumTemp = 0;
		for(Patch p : patchArray)
			sumTemp += p.getTemperature();
		this.globalTemperature = sumTemp / Params.NUMBER_PATCH;
	}
	
	//update number of white/black daisy
	public void updateDaisyNumber() {
		//check state first, then check which kind of daisy
		numberBlack = 0;
		numberWhite = 0;
		
		for( Patch p : patchArray) 
			//if it is not open
			if(!p.getState() && !p.getDaisy().isSeed()) {
				//if daisy is black. increase black number
				if(p.getDaisy().getType() == Type.WHITE)
					this.numberWhite++;
				else
					this.numberBlack++;
			}
					
	}
	
	//simulate what happens in one time tick in the system
	public void tick() {
		//update all patches with thier own tick
		for(Patch p : patchArray)
			p.tick();
		//diffuse the temperature
		for(Patch p : patchArray)
			p.diffuse();
		//time to sprout
		for(Patch p : patchArray)
			p.sproutDaisy();
		//calculate global tmeperature
		calcuGlobalTemp();
		//update the number of daisy
		updateDaisyNumber();
		
		
		System.out.println("global temperature: " + globalTemperature);
		System.out.println("white number: " +  numberWhite );
		System.out.println("black number: " + numberBlack);
	}
}
