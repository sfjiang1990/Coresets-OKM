package okm;

import java.util.ArrayList;

public class OKMSolver
{
	private ArrayList<WeightedPoint> data;
	
	public OKMSolver(ArrayList<WeightedPoint> data)
	{
		this.data = data;
	}
	
	public static int[] sample(int n, int k)
	{
		int[] res = new int[k];
		for (int i = 0; i < k; i++)
		{
			res[i] = Rand.getRand().nextInt(n);
		}
		return res;
	}
	
	private ArrayList<Point> getPoint(int[] index)
	{
		ArrayList<Point> res = new ArrayList<Point>();
		for (int i = 0; i < index.length; i++)
		{
			res.add(data.get(index[i]).data);
		}
		return res;
	}
	
	public ArrayList<Double> samplingForEvaluation(WeightedDouble[] w, int k, int ite)
	{
		double minObj = -1;
		ArrayList<Double> solution = new ArrayList<Double>();
		for (int t = 0; t < ite; t++)
		{
			ArrayList<Point> sample = getPoint(sample(data.size(), k));
			double obj = WeightedPoint.cost(data, sample, w);
			if (minObj == -1 || minObj > obj)
			{
				minObj = obj;
			}
			solution.add(minObj);
		}
		return solution;
	}
	
	public ArrayList<Point> sampling(WeightedDouble[] w, int k)
	{
		double minObj = -1;
		ArrayList<Point> solution = new ArrayList<Point>();
		for (int t = 0; t < 50; t++)
		{
			ArrayList<Point> sample = getPoint(sample(data.size(), k));
			double obj = WeightedPoint.cost(data, sample, w);
			if (minObj == -1 || minObj > obj)
			{
				minObj = obj;
				solution = sample;
			}
		}
		return solution;
	}
	
	public ArrayList<Point> bruteForce(WeightedDouble[] w, int k)
	{
		double minObj = -1;
		ArrayList<Point> sol = new ArrayList<Point>();
		ArrayList<Point> can = new ArrayList<Point>();
		for (WeightedPoint p : data)
		{
			can.add(p.data);
		}
		for (Point p : can)
		{
			ArrayList<Point> c = new ArrayList<Point>();
			c.add(p);
			double obj = WeightedPoint.cost(data, c, w);
			if (minObj == -1 || minObj > obj)
			{
				minObj = obj;
				sol = c;
			}
		}
		System.out.println(minObj);
		return sol;
	}
}
