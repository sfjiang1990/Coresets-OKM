package coreset;

import java.util.ArrayList;
import java.util.HashSet;

import okm.Point;
import okm.Segment;
import okm.WeightedPoint;

public class Coreset1D
{
	private Segment data;
	public Coreset1D(ArrayList<WeightedPoint> pointSet, Point center, Point direction)
	{
		data = new Segment(pointSet, center, direction);
	}

	private ArrayList<Segment> doLight(int s, int t, double eps, int k, int p, double opt)
	{
		double threshold = eps * opt / p / 5; // opt_i?
		ArrayList<Segment> res = new ArrayList<Segment>();
		for (int i = s; i <= t;)
		{
			int start = i;
			int end = start;
			for (; end <= t && Point.dist(data.getPointByIndex(end).data, data.getPointByIndex(start).data) <= threshold;
					end++ )
			{
			}
			Segment current = new Segment(data.center, data.direction);
			for (int j = start; j < end; j++)
			{
				current.T.add(data.T.get(j));
			}
			res.add(current);
			i = end;
		}
		return res;
	}
	
	private double evaluateError(double start, double end)
	{
		int startCeil = (int)Math.ceil(start);
		int startFloor = (int)Math.floor(start);
		int endFloor = (int)Math.floor(end);
		
		double sum = 0;
		double sumw = 0;
		
		for (int i = startCeil; i <= endFloor - 1; i++)
		{
			sum += data.T.get(i).data * data.T.get(i).weight;
			sumw += data.T.get(i).weight;
		}
		
		sum += data.T.get(startFloor).data * (startCeil - start) * data.T.get(startFloor).weight
				+ data.T.get(endFloor).data * (end - endFloor) * data.T.get(endFloor).weight;
		sumw += data.T.get(startFloor).weight * (startCeil - start)
				+ data.T.get(endFloor).weight * (end - endFloor);
		
		double mean = sum / sumw;
		Point meanPoint = data.getPoint(mean);
		
		double totalErr = (startCeil - start) * data.T.get(startFloor).weight
				* Point.dist(data.getPointByIndex(startFloor).data, meanPoint)
				+ (end - endFloor) * data.T.get(endFloor).weight
				* Point.dist(data.getPointByIndex(endFloor).data, meanPoint);
		
		for (int i = startCeil; i <= endFloor - 1; i++)
		{
			totalErr += data.T.get(i).weight * Point.dist(data.getPointByIndex(i).data, meanPoint);
		}
		return totalErr;
	}
	
	private ArrayList<Segment> heavySegment(double start, double end)
	{
		Segment main = new Segment(data.center, data.direction);
		int a = (int)Math.ceil(start);
		int b = (int)Math.floor(end) - 1;
		for (int i = a; i <= b; i++)
		{
			main.T.add(data.T.get(i));
		}
		
		Segment tail = new Segment(data.center, data.direction);
		tail.T.add(data.T.get(b + 1));

		ArrayList<Segment> res = new ArrayList<Segment>();
		if (!main.T.isEmpty())
			res.add(main);
		res.add(tail);
		return res;
	}

	private ArrayList<Segment> doHeavy(int s, int t, double eps, int k, int p, double opt)
	{
		double threshold = eps * opt / 15 / k;
		ArrayList<Segment> res = new ArrayList<Segment>();
		double start = s;
		while (t - start >= 1)
		{
			double a = start;
			double b = t;
			double sol = a;
			while (b - a > 1e-6)
			{
				double mid = (a + b) / 2.0;
				if (evaluateError(start, mid) <= threshold)
				{
					sol = mid;
					a = mid;
				}
				else
				{
					b = mid;
				}
			}
			res.addAll(heavySegment(start, sol));
			start = sol;
		}
		Segment last = new Segment(data.center, data.direction);
		last.T.add(data.T.get(t));
		res.add(last);
		return res;
	}
	
	public Segment getSegment()
	{
		return this.data;
	}

	public ArrayList<Segment> getCoreset(double eps, int k, int p, HashSet<WeightedPoint> light, double opt, ArrayList<Point> centers)
	{
		double hopt = 0;
		ArrayList<WeightedPoint> pnt = data.toPoints();
		for (WeightedPoint P : pnt)
		{
			if (!light.contains(P))
			{
				hopt += P.weight * Point.dist(P.data, centers);
			}
		}
		System.out.println(hopt + " " + opt);
		ArrayList<Segment> res = new ArrayList<Segment>();
		for (int i = 0; i < data.T.size();)
		{
			int start = i;
			int end = start;
			for (; end < data.T.size() && light.contains(data.getPointByIndex(end)) == light.contains(data.getPointByIndex(start)); end++)
			{
			}
			if (light.contains(data.getPointByIndex(start)))
			{
				ArrayList<Segment> tmp = doLight(start, end - 1, eps, k, p, opt);
				int sum = 0;
				for (Segment s : tmp)
				{
					sum += s.totalWeight();
				}
				System.out.println("light " + sum + " " + (end - start));
				res.addAll(tmp);
			}
			else
			{
				ArrayList<Segment> tmp = doHeavy(start, end - 1, eps, k, p, hopt);
				int sum = 0;
				for (Segment s : tmp)
				{
					sum += s.totalWeight();
				}
				System.out.println("heavy " + sum + " " + (end - start));
				res.addAll(tmp);
			}
			i = end;
		}
		return res;
	}
}
