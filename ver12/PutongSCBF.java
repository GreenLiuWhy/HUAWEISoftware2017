package realcode.ver12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class PutongSCBF {

	private int nodeNum,edgNum,ServerMoney,CondumerNum;
	private int[] subPath;//存储每次从SPFA中的到的路径
	private int Inf=99999999;//定义一个无穷大的数
	private int[] head,Dis;
	private ArrayList<Edge> edge=new ArrayList<Edge>();
	private ArrayList<int[]> CPosition=new ArrayList<int[]>();
	private int Ss,St,Tb,Ts;//超级源点的位置，超级汇点的位置，超级汇点建立的起始边代号，超级汇点建立的终止边代号
	//private ArrayList<Edge> eee;//用于做edge的每次变化品
	private HashMap<Integer,ArrayList> preRoud=new HashMap<Integer,ArrayList>();
	
	public static void main() {
		
		// TODO Auto-generated method stub
		System.out.println("Putong");
	    String[] graphContent = FileUtil.read("Gao/case0.txt", null);
		PutongSCBF putong=new PutongSCBF();
		putong.getBasicInformation(graphContent);
		ArrayList<Integer> SP=putong.getRandomPop();
		putong.buldSuperS(SP);
		long b=System.currentTimeMillis();
		putong.smallCostBigFunction();
		long e=System.currentTimeMillis();
		System.out.println("time="+(e-b));
		putong.outputRoud();
		
		
		
	}
	
	  //初试步骤，通过传进来的数组得到路径信息
    public void getBasicInformation(String[] g)
    {
    	int layer=0;//表示是第几个空行，同时也可以推知是何种顺序,读出来的结果应该是对的
    	for(int i=0;i<g.length;i++)
    	{
    		if(g[i].equals("") || g[i]==null)//有空行
    			layer++;
    		int[] CN=this.changeCharToNum(g[i].toCharArray());//将刚行的文本数据转化为数组数组,CN转化的也是对的
    		if(layer==0)//第一行信息,第一行的数据是对的
    		{
    			this.nodeNum=CN[0];
    			this.edgNum=CN[1];
    			this.CondumerNum=CN[2];
    			this.initNet();//初始化网络
    			continue;
    		}
    		if(layer==1 && !g[i].equals(""))//服务器部署细心
    		{
    			this.ServerMoney=CN[0];//System.out.println(this.ServerMoney+"");正确
    			continue;
    		}
    		if(layer==2 && !g[i].equals(""))//出发点 到达点 最大容量 代价,用于构建edges
    		{
    			this.addedges(CN[0], CN[1], CN[2], CN[3]);
    			this.addedges(CN[1], CN[0], CN[2], CN[3]);
    			continue;
    		}
    		if(layer==3 && !g[i].equals(""))//消费节点编号 消费节点相连节点 需要的最小代价,用于构建超级汇点
    		{
    			this.CPosition.add(new int[]{CN[0],CN[1],CN[2]});//这个初始化方法不错
    		}
    	}
    	//一些基于上面的前期准备工作
    	this.Ss=this.nodeNum;
    	this.St=this.nodeNum+1;
    	this.buildSuperT();
    }
    
    public int smallCostBigFunction()
    {
    	int minflow,minCost=0,len=0;
    	//minflow是一次spfa()中求出的一条最短路径，所可以通过的最大流量
    	//minCost是源点到汇点的总花费（是所有路径的）
    	//len是记录这是多少次路径，也就是spfa执行了多少次
    	//一次spfa()求出一条此时的最短路径
    	 
    	while(spfa())//这个n还需不要要加2
    	{
    		ArrayList<Integer> subpath=new ArrayList<Integer>();//用来记录此次spfa找的路径 
    		minflow=Inf+1;
    		//求线路允许通过的最大流量，也就是这条线路中包含路径的最小cap
    		for(int i=subPath[St];i!=-1;i=subPath[edge.get(i).u])
    		{
    			subpath.add(i);//包含超级源点路径，也包含超级汇点路径,但是是逆序的
    			if(edge.get(i).cap<minflow)
    				minflow=edge.get(i).cap;
    		}
    		subpath.add(minflow);//将线路此时的流量信息放到最后一位
    		preRoud.put(len++, subpath);//记录路径,这时候记录的是  subpath里的所有信息  和 该线路上的流量
    		for(int i=subPath[St];i!=-1;i=subPath[edge.get(i).u])
    		{
    			edge.get(i).cap-=minflow;
    			edge.get(i^1).cap+=minflow;//异或操作，对应了改经过边的所对应的反边
    		}
    		minCost+=Dis[St]*minflow;//Dis中已将包含了Cost的信息，所以不用重新再考虑
    		
    	}
    	return minCost;
    }
    
    public boolean spfa()//这里面传的allnode本来是用来生成Dis和vis用的，但是现在不需要了，而且，
    //此时因为源点一直是固定的，用全局变量来存储为Ss和St了，所以也就不需要再传入什么信息了
    {
    	Queue<Integer> qu=new LinkedList<Integer>();
    	boolean[] vis=new boolean[this.nodeNum+2]; 
    	for(int i=0;i<subPath.length;i++)
    	{
    		subPath[i]=-1;
    		Dis[i]=Inf;
    		vis[i]=false;//没有在队列里
    	}
    	Dis[Ss]=0;
    	qu.add(Ss);
    	vis[Ss]=true;
    	while(!qu.isEmpty())
    	{
    		int hnode=qu.remove();
    		vis[hnode]=false;
    		for(int i=this.head[hnode];i!=-1;i=edge.get(i).next)//找到所有以hnode为头结点的边
    		{
    			int tnode=edge.get(i).v;//找到以hnode为头的弧的尾节点
    			//如果可以这个链路可以过，且代价小
    			if(edge.get(i).cap>0 && Dis[tnode]>Dis[hnode]+edge.get(i).cost)
    			{
    				Dis[tnode]=Dis[hnode]+edge.get(i).cost;
    				subPath[tnode]=i;//记录路径
    				if(vis[tnode]==false)//如果尾节点不再队列里面
    				{
    					qu.add(tnode);
    					vis[tnode]=true;
    				}
    			}
    		}
    	}
    	if(Dis[St]>=Inf)
    		return false;
    	return true;	
    }
    
    ////////////////////////上面主要函数所要调用的一些小函数////////////\
    
    //随机生成含有size个消费节点的初试种群
    public ArrayList<Integer> getRandomPop()
    {
    	ArrayList<Integer> SP=new ArrayList<Integer>();
    	Random r=new Random();
    	while(SP.size()<this.CondumerNum)
    	{
    		int ra=r.nextInt(this.nodeNum);//随机生成一个加入的节点
    		if(SP.indexOf(ra)==-1)//如果原来不在里面
    			SP.add(ra);
    	}
    	return SP;
    }
    
    //将带空格，且最大是千位的书转型
    public int[] changeCharToNum(char[] cc)
    {
    	int cishu=0,len,zi=0;
    	int[] Num=new int[4];
    	for(;zi<cc.length;zi++)
    	{
    		len=0;
    		for(;zi<cc.length && cc[zi]!=' ';zi++)//看看这个数字有多少位
    			len++;
    		//根据len得到数字，并将该数字存入数组
    		int sum=0;
    		for(int i=0;i<len;i++)//将每个char转化为数字，并qui和
    			sum+=((int)(cc[zi-i-1]-48))*((int)Math.pow(10, i));
    		Num[cishu]=sum;
    		cishu++;
    	}
    	return Num;
    }
    //构建网络的初始化网络操作
    public void initNet()
    {
    	this.head=new int[this.nodeNum+2];//因为肯定会有一个超级源点，一个超级汇点
    	this.subPath=new int[this.nodeNum+2];
    	this.Dis=new int[this.nodeNum+2];
    	for(int i=0;i<head.length;i++)
    	{
    		head[i]=-1;
    		subPath[i]=-1;
    	}
    }
    
    
    //构建超级汇点
    public void buildSuperT()
    {
    	Tb=edge.size();
    	for(int i=0;i<this.CondumerNum;i++)
    	{
    		int c=this.CPosition.get(i)[1];  		
    		this.addedges(c, this.St, CPosition.get(i)[2], 0);
    	}
    	Ts=edge.size()-1;
    }
    //构建超级源点
    public void buldSuperS(ArrayList SP)
    {
    	for(int i=0;i<SP.size();i++)
    	{
    		int node=(int)SP.get(i);
    		this.addedges(Ss, node, Inf, 0);
    		//this.addedgesthead(Ss, node, Inf, 0);
    	}
    	//this.detIter=0;
    }
    
    //构建网络时的加边操作
    public void addedges(int u,int v,int cap,int cost)
    {
    	Edge e=new Edge(u,v,cap,cost,head[u]);//正向边
    	this.edge.add(e);
    	head[u]=(edge.size()-1);//以u为头结点的弧所在的边的位置
    	Edge ee=new Edge(v,u,0,-cost,head[v]);//反向边
    	this.edge.add(ee);
    	head[v]=(edge.size()-1);//以v为头结点的弧所在的边的位置
    }
    
    //将preRoad打印出来
    public void outputRoud()
    //public HashMap getRealRoud()
    {
    	System.out.println("preRoad.size()="+preRoud.size());
    	for(int i=0;i<10;i++)
    		System.out.println(preRoud.get(i).toString());
    }

	

}
