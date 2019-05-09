package okm;

import java.util.Random;

public class Rand
{
	private Rand() {}
	private static Random rand = new Random();
	
	public static Random getRand()
	{
		return rand;
	}
}
