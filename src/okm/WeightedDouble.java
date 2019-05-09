package okm;

import java.util.Arrays;

public class WeightedDouble implements Comparable<WeightedDouble>
{
	public double data;
	public int weight;
	
	public WeightedDouble(double d, int w)
	{
		this.data = d;
		this.weight = w;
	}

	@Override
	public int compareTo(WeightedDouble o)
	{
		return Double.compare(this.data, o.data);
	}
	
	public static double dotProduct(WeightedDouble[] A, WeightedDouble[] B)
	{
		double res = 0;
		int wA = 0, wB = 0;
		double vA = 0, vB = 0;
		for (int i = 0, j = 0; i < A.length || j < B.length;)
		{
			if (wA <= wB)
			{
				vA = A[i].data;
				res += Math.min(A[i].weight, wB - wA) * vA * vB;
				wA += A[i].weight;
				i++;
			}
			else
			{
				vB = B[j].data;
				res += Math.min(B[j].weight, wA - wB) * vA * vB;
				wB += B[j].weight;
				j++;
			}
		}
		return res;
	}
	
	public static WeightedDouble[] getWeightVector(int p, int tot)
	{
		WeightedDouble[] w = new WeightedDouble[] {new WeightedDouble(1.0, p), new WeightedDouble(0.0, tot - p)};
		Arrays.sort(w);
		return w;
	}
	
	public static WeightedDouble[] getPowerLawVector(double alpha, int tot)
	{
		WeightedDouble[] w = new WeightedDouble[tot];
		for (int i = 0; i < tot; i++)
		{
			w[i] = new WeightedDouble(1.0 / Math.pow(i + 1, alpha), 1);
		}
		Arrays.sort(w);
		return w;
	}
	
/*	public static void main(String[] args)
	{
		double[] A = new double[]{1.0, 1.0};
		int[] wA = new int[]{5, 3};
		
		double[] B = {1.0, 1.0, 1.0, 1.0};
		int[] wB = new int[] {1, 3, 2, 2};
		
		WeightedDouble[] tA = new WeightedDouble[A.length];
		WeightedDouble[] tB = new WeightedDouble[B.length];
		
		for (int i = 0; i < A.length; i++)
		{
			tA[i] = new WeightedDouble(A[i], wA[i]);
		}
		for (int i = 0; i < B.length; i++)
		{
			tB[i] = new WeightedDouble(B[i], wB[i]);
		}
		
		
		System.out.println(dotProduct(tA, tB));
	}*/
}
