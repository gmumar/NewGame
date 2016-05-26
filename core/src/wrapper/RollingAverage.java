package wrapper;

public class RollingAverage {

    private int size;
    private float total = 0f;
    private int index = 0;
    private float samples[];

    public RollingAverage(int size) {
        this.size = size;
        samples = new float[size];
        for (int i = 0; i < size; i++) samples[i] = 0f;
    }

    public void add(float x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) index = 0; // cheaper than modulus
    }

    public float getAverage() {
        return total / size;
    }   
}