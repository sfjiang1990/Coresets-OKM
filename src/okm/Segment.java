package okm;

import java.util.ArrayList;
import java.util.Collections;

public class Segment
{
	public Point center;
	public Point direction;
	
	public ArrayList<WeightedDouble> T = new ArrayList<WeightedDouble>();
	
	public Segment(ArrayList<WeightedPoint> X, Point center, Point direction)
	{
		this.center = center;
		this.direction = direction;
		
		for (WeightedPoint p : X)
		{
			double t = p.data.minus(center).dot(direction);
			T.add(new WeightedDouble(t, p.weight));
		}
		Collections.sort(T);
	}
	
	public Segment(Point center, Point direction)
	{
		this.center = center;
		this.direction = direction;
	}
	
	public void sort()
	{
		Collections.sort(T);
	}
	
	public Point getPoint(double t)
	{
		return center.plus(direction.multiply(t));
	}
	
	public WeightedPoint getPointByIndex(int i)
	{
		return new WeightedPoint(getPoint(T.get(i).data), T.get(i).weight);
	}
	
	public WeightedDouble mean()
	{
		double sum = 0;
		int sumw = 0;
		for (WeightedDouble t : T)
		{
			sum += t.data * t.weight;
			sumw += t.weight;
		}
		return new WeightedDouble(sum / sumw, sumw);
	}
	
	public WeightedPoint meanPoint()
	{
		WeightedDouble mean = mean();
		return new WeightedPoint(this.getPoint(mean.data), mean.weight);
	}
	
	public ArrayList<WeightedPoint> toPoints()
	{
		ArrayList<WeightedPoint> res = new ArrayList<WeightedPoint>();
		for (WeightedDouble t : T)
		{
			res.add(new WeightedPoint(getPoint(t.data), t.weight));
		}
		return res;
	}
	
	public static ArrayList<WeightedPoint> meanPoints(ArrayList<Segment> segs)
	{
		ArrayList<WeightedPoint> res = new ArrayList<WeightedPoint>();
		for (Segment s : segs)
		{
			res.add(s.meanPoint());
		}
		return res;
	}
	
	public int totalWeight()
	{
		int sum = 0;
		for (WeightedDouble t : T)
		{
			sum += t.weight;
		}
		return sum;
	}
	
	public static ArrayList<Segment> combine(ArrayList<Segment> A, ArrayList<Segment> B)
	{
		return null;
	}
}
