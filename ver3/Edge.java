package realcode.ver3;
//����һ�����ڱߵ��ⲿ��

public class Edge implements Cloneable{
	int u;
	int v;
	int cap;
	int cost;
	int next;
	
	//���캯��
	public Edge(int u,int v,int cap,int cost,int next)
	{
		this.u=u;
		this.v=v;
		this.cap=cap;
		this.cost=cost;
		this.next=next;
	}
	
	//clone����
	public Object clone()
	{
		Edge e=null;
		try {
			e=(Edge)super.clone();
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return e;
	}
}
