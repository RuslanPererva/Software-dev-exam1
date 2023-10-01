//https://www.baeldung.com/java-varargs

import java.io.*;
import java.util.*;


public class Flush implements Serializable{
	private int threshold;
	private static final long serialVersionUID =1L;
	private transient List<Integer> numbers;
	
	public Flush (int... numbs) {
		this.threshold = 0;
		this.numbers = new ArrayList<>();
		for (int x : numbs) {
			numbers.add(x);
		}
	}
	
	
	public void setThreshold(int thresh) {
		threshold = thresh;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public List<Integer> getNumbers() {
		return numbers;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(threshold);
		List <Integer> temp = new ArrayList<>();
		for (Integer x : numbers) {
			if (x>threshold) {
				temp.add(x);
			}
		}
		out.writeObject(temp);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject (ObjectInputStream in) throws ClassNotFoundException, IOException {
		threshold =  in.readInt();
		numbers = (List<Integer>) in.readObject();
	}
	
	
}