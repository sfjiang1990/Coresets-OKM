package okm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WeightedPoint
{
	public Point data;
	public int weight;
	
	public WeightedPoint(Point d, int w)
	{
		this.data = d;
		this.weight = w;
	}
	
	public static void sortByDist(ArrayList<WeightedPoint> X, ArrayList<Point> C)
	{
		Collections.sort(X, new Comparator<WeightedPoint>(){
			@Override
			public int compare(WeightedPoint o1, WeightedPoint o2)
			{
				return Double.compare(Point.dist(o1.data, C), Point.dist(o2.data, C));
			}
		});
	}
	
	public static double cost(ArrayList<WeightedPoint> X, ArrayList<Point> C, WeightedDouble[] w)
	{
		WeightedPoint.sortByDist(X, C);
		WeightedDouble[] D = new WeightedDouble[X.size()];
		for (int i = 0; i < X.size(); i++)
		{
			D[i] = new WeightedDouble(Point.dist(X.get(i).data, C), X.get(i).weight);
		}
		return WeightedDouble.dotProduct(D, w);
	}
	
	public static int totalWeight(ArrayList<WeightedPoint> X)
	{
		int sum = 0;
		for (WeightedPoint p : X)
		{
			sum += p.weight;
		}
		return sum;
	}
	
	@Override
	public int hashCode()
	{
		return this.data.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		WeightedPoint p = (WeightedPoint)o;
		return p.data.equals(this.data);
	}
}
