import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//simulate the ground patch in environment
//it has albedo, and one daisy living on one patch
public class Patch {
	//id of the patch
	private int id;
	//represents whether the patch is open
	private boolean isOpen;
	//temperature of the patch
	private float temperature;
	//daisy instance growing on that patch 
	private Daisy daisy;
	//the probability of growing a new daisy
	private float probablilityRepro;	
	//the list of neighbor patches
	private List<Patch> neighbors;
	//the temperature got from neighbors
	private float tempFromNei;
	//a random variable to generate random number
	public static Random rand = new Random();
	
	//constructor
	public Patch(int id) {
		this.id = id;
		//initial state should be true
		this.isOpen = true;
		//set initial temperature
		this.temperature = Params.START_TEMP;
	}
	
	//set neighbor
	public void setNeighbors(List<Patch> neiList) {
		this.neighbors = neiList;
	}
	
	//set the open state
	public void setState(boolean open) {
		this.isOpen = open;
	}
	//get the state
	public boolean getState() {
		return this.isOpen;
	}
	//set the daisy on this patch
	//only when patch state is open
	public void setDaisy(Daisy d) {
		if(isOpen == false)
			return;
		this.daisy = d;
		this.isOpen = false;
	}
	//get the daisy on this patch
	public Daisy getDaisy() {
		return this.daisy;
	}
	
	//get the current local temperature in this patch
	public float getTemperature() {
		return this.temperature;
	}
	//gain temperature from neighbor
	public void gainTempFromNei(float temp) {
		this.tempFromNei += temp;
	}
/*	calculate the current temprature of patch
  the percentage of absorbed energy is calculated (1 - albedo-of-surface) and 
	then multiplied by the solar-luminosity to give a scaled absorbed-luminosity.*/
	public void calcuTemperature() {
		//the real albedo on this patch
		//equals to albedo of daisy if there is daisy here
		//or is albedo of ground
		float realAlbedo;
		if(isOpen || daisy.isSeed()) 
			realAlbedo = Params.ALBEDO_GROUND;
		else if(this.daisy.getType() == Type.WHITE )
			realAlbedo = Params.ALBEDO_WHITE ;
		else if (this.daisy.getType() == Type.GRAY)
			realAlbedo = Params.ALBEDO_GRAY;
		else 
			realAlbedo = Params.ALBEDO_BLACK;
		
		//the actual absorbed luminosity
		float absorbedLumino = (1 - realAlbedo) * (Params.SOLAR_LUMI );
//	 local-heating is calculated as logarithmic function of solar-luminosity
		//value to store absorbed heat
		float absorbedHeat;
		if(absorbedLumino > 0) {
			absorbedHeat = (float) (72 * Math.log(absorbedLumino) + 80);
		} else 
			absorbedHeat = 80;
		//set the temperature at this patch to be the average of the current 
		//temperature and the local-heating effect
		temperature = (temperature + absorbedHeat)/2;
		
	}
	
	//set the final temperature
	public void calcFinalTemperature() {
		this.temperature += this.tempFromNei;
		this.tempFromNei = 0;
	}
	

/*	diffuse temperature 0.5
	 each patch diffuses 50% of its variable
	 chemical to its neighboring 8 patches. Thus,
	 each patch gets 1/8 of 50% of the temperature
	 from each neighboring patch.)
	(If a patch has fewer than eight neighbors, each neighbor still 
	gets an eighth share; the patch keeps any leftover shares.)
	important: diffuse should happen when all patches calculated temperature*/
	public void diffuse() {
		for(Patch nei : neighbors)
			nei.gainTempFromNei((float) (temperature * 0.5 / 8));
		temperature -= (temperature * 0.5 /8) * neighbors.size();

	}
	
	
	
/*	calculate the probability of reproducing a new daisy
  it is a parabola.
  This parabola has a peak value of 1 -- the maximum growth factor possible at an optimum
  temperature of 22.5 degrees C
  -- and drops to zero at local temperatures of 5 degrees C and 40 degrees C. 
  Thus, growth of new daisies can only occur within this temperature range,
  with decreasing probability of growth new daisies closer to the x-intercepts of the parabolas
  this probability calculation is based on the local temperature.*/
	public void calcuProbRepro() {
		if(isOpen || daisy.isSeed()) 
			probablilityRepro = 0;
		else
			probablilityRepro = (float) ((0.1457 * temperature) - (0.0032 * Math.pow(temperature, 2)) - 0.6443);
	}
	
	//a random algorithm to choose one neighbor patch without daisy on it to sprout seed
	public void sproutDaisy() {
		//then calculate the probability to reproduce
		calcuProbRepro();
		//sprout seed if possible, else return
		if(rand.nextFloat() < probablilityRepro) {
			//a list to record the index of open patch
			List<Integer> openNeighbors = new ArrayList<>();
			for(int i =0; i < neighbors.size(); i++)
				if(neighbors.get(i).getState())
					openNeighbors.add(i);
			//if no neighbor is open, do nothing
			// else choose one open neighbor randomly and seed
			if(openNeighbors.size() == 0)
				return;
			else {			
				int index = rand.nextInt(openNeighbors.size());
				//generate a new daisy with the same type as daisy on this patch
				Daisy seedDaisy = new Daisy(this.daisy.getType());
				//set the seed daisy onto the chosen neighbor
				neighbors.get(openNeighbors.get(index)).setDaisy(seedDaisy);
			}
		}		
	}
	
	//simulate one time tick happen on this ground patch 
	public void tick() {
		//increase the age of the daisy on this patch if there is 
		if(!isOpen && daisy != null)
			daisy.increaseAge();
		//remove the daisy if it is dead and set patch state to open
		if(daisy != null && !daisy.isAlive()) {
			daisy = null;
			isOpen = true;
		}
		//calculate local temperature in every tick
		calcuTemperature();
		//System.out.println("patch temperature: " + temperature);
		//diffuse temperature to neighbors
		diffuse();
	}
}
