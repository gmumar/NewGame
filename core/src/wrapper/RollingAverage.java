package wrapper;

public class RollingAverage {

    private int size;
    private float total = 0f;
    private int index = 0;
    private float samples[];
    
    private float max = -1000;
    private int maxIndex;

    
    private float min = 1000;
    private int minIndex;
    
    public RollingAverage(int size) {
        this.size = size;
        samples = new float[size];
        for (int i = 0; i < size; i++) samples[i] = 0f;
    }

    public void add(float x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        
       /* --maxIndex;

        if(maxIndex<=0){
        	max = -1000;
        }
        
        if(x > max){
        	max = x;
        	maxIndex = index;
        }
        
        --minIndex;
        
        if(minIndex<=0){
        	min = 1000;
        }
        
        if(x < min){
        	min = x;
        	minIndex = index;
        }*/
        
        if (++index == size) index = 0; // cheaper than modulus
    }
    
    public float getDeltaAverage() {
        return (total-max-min) / (size-2);
    }
    

    public float getAverage() {
        return total / size;
    }   
}