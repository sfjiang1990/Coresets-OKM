package okm;

import java.util.ArrayList;

public class Point implements Comparable<Point>
{
	public double x, y;
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point plus(Point p)
	{
		return new Point(x + p.x, y + p.y);
	}
	
	public Point minus(Point p)
	{
		return new Point(x - p.x, y - p.y);
	}
	
	public Point multiply(double t)
	{
		return new Point(x * t, y * t);
	}
	
	public double norm()
	{
		return Math.sqrt(x * x + y * y);
	}
	
	public Point normalize()
	{
		double d = norm();
		return new Point(x / d, y / d);
	}
	
	public static double dist(Point p, Point q)
	{
		return p.minus(q).norm();
	}
	
	public static double dist(Point p, ArrayList<Point> S)
	{
		double mn = -1;
		for (Point x : S)
		{
			double d = dist(x, p);
			if (mn == -1 || mn > d)
				mn = d;
		}
		return mn;
	}
	
	public double dot(Point p)
	{
		return x * p.x + y * p.y;
	}
	
	public double atan2(Point center)
	{
		double dy = y - center.y;
		double dx = x - center.x;
		return Math.atan2(dy, dx);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(" + x + ", " + y + "), ");
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
		return Double.toHexString(x * y).hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		Point p = (Point)o;
		return this.x == p.x && this.y == p.y;
	}

	@Override
	public int compareTo(Point o)
	{
		if (x != o.x)
			return Double.compare(x, o.x);
		return Double.compare(y, o.y);
	}
}
