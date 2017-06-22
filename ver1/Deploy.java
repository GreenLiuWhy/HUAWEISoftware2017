package realcode.ver1;

import java.util.*;

public class Deploy
{
	private int nodeNum,edgNum,ServerMoney,CondumerNum;
	private int[] subPath;//存储每次从SPFA中的到的路径
	private int Inf=99999999;//定义一个无穷大的数
	private int[] head,thead,Dis;
	private ArrayList<Edge> edge=new ArrayList<Edge>();
	private ArrayList<int[]> CPosition=new ArrayList<int[]>();
	private int Ss,St,Tb,Ts;//超级源点的位置，超级汇点的位置，超级汇点建立的起始边代号，超级汇点建立的终止边代号
	private ArrayList<Edge> eee;//用于做edge的每次变化品
	//private int[] NodewithEdges;//通过记录每个节点的放置位置，来变相推知节点的度数和对应在edge中的位置
	private HashMap<Integer,ArrayList> Adj=new HashMap<Integer,ArrayList>();
	private HashMap<Integer,ArrayList> preRoud=new HashMap<Integer,ArrayList>();
    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        Deploy d=new Deploy();
        d.getBasicInformation(graphContent);
        return d.changeForm(d.dofire(d.getIntaSeverPosition()));
       // return null;
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
    			//构建edges  int u,int v,int cap,int cost，无向图加2此
    			this.addedges(CN[0], CN[1], CN[2], CN[3]);
    			this.addedges(CN[1], CN[0], CN[2], CN[3]);
    			this.Adj.get(CN[0]).add(CN[1]);//加入到邻接表,这是因为该算法不考虑空间复杂性所以才那么做的
    			this.Adj.get(CN[1]).add(CN[0]);//如果算法还考虑空间复杂性，那么可以用NodewithEdges和edge的关系来做
    			continue;
    		}
    		if(layer==3 && !g[i].equals(""))//消费节点编号 消费节点相连节点 需要的最小代价,用于构建超级汇点
    			this.CPosition.add(new int[]{CN[0],CN[1],CN[2]});//这个初始化方法不错
    	}
    	//一些基于上面的前期准备工作
    	this.Ss=this.nodeNum;
    	this.St=this.nodeNum+1;
    	this.buildSuperT();
    }
    
    //第一步选择初试服务器摆放位置,需要路径信息(度和链接信息)
    public ArrayList getIntaSeverPosition()
    {//先实现简单的，也就是在消费节点相连的节点上放一个
    	ArrayList<Integer> SP=new ArrayList<Integer>();//用于标示哪些位置用来摆放服务器
    	for(int i=0;i<this.CondumerNum;i++)
    		SP.add(this.CPosition.get(i)[1]);
    	return SP;
    }
    
    //第二步开始改进的模拟退火,传如服务器的初试的路径信息
    public HashMap dofire(ArrayList SP1)
    {
    	//要用的变量什么的一个声明
    	Random rand=new Random();//用于生成随机数
    	int T0=1000,opiter=500;//模拟退火的初试温度,模拟退火外循环的迭代次数
    	double T;//模拟退火的当前温度
    	boolean preNeed,nowNeed=true;//记录过去和现在的方案中，所有消费节点的需求是否可以得到满足
    	int preCost,nowCost=Inf,minCost=Inf;//记录过去和现在方案的花费，最小花费
    	HashMap Road=new HashMap();//目前记录的是edge的标示
    	eee=this.deepCloneBaseFor(edge);//因为每次用的都是不同的，这样就不用每次都建了
    	thead=(int[])head.clone();
    	//算法正式开始
    	//针对第一个数据
    	this.buldSuperS(SP1);//根据传入的服务器位置SP1，构建超级源点
//    	System.out.println("dofire first head");////这个head也一定会在S变后发生更改的
//    	for(int ii=0;ii<this.thead.length;ii++)
//    		System.out.print(ii+":"+thead[ii]+"  ");
//    	System.out.println();
    	//System.out.println("dofire中第一个 现在的边的数目为："+eee.size()+"   过去的边的数目为："+edge.size());
    	preCost=this.smallCostBigFunction()+SP1.size()*this.ServerMoney;//得到第初试服务器放置的最大流情况下的花费
    	preNeed=this.checkCNeed();
    	System.out.println("dofire中第一个preNeed是"+preNeed+"  最初的最短路径个数是:"+preRoud.size());
    	if(preNeed)//如果能满足条件，则先将路径记录下来
    	{
    		minCost=preCost;
    		Road=(HashMap)this.preRoud.clone();//	
    	}
    	
    	//针对以后的数据
    	ArrayList preSP=SP1,nowSP;//原来和现在服务器的摆放位置
    	System.out.println("dofire 开始循环");
    	for(int iter=0;iter<opiter;iter++)
    	{
    		T=T0/(1+iter);//当前温度
    		eee=this.deepCloneBaseFor(edge);//重新恢复到还没有确定服务器的状态
    		thead=(int[])head.clone();
    		
    		//3中改变策略（增加服务器，减少服务器，改变服务器位置）
    		nowSP=this.changeServerMethod(preSP,preNeed);
    		//System.out.println("dofire nowSP:"+nowSP.toString());///
    		this.buldSuperS(nowSP);//构建超级汇点
//    		System.out.println("dofire show eee");///////////
//        	for(int i=Tb;i<=Ts;i=i+2)//因为加1之后是反边
//        	{
//        		System.out.print(eee.get(i).u+"-->"+eee.get(i).v+"="+eee.get(i).cap+"  ");//
//        	}
//        	System.out.println();///
        	
    		nowCost=this.smallCostBigFunction()+nowSP.size()*this.ServerMoney;
    		//System.out.println("dofire nowCost="+nowCost);
    		nowNeed=this.checkCNeed();//检查是否满足了所有服务器的要求
    		if((preNeed&&nowNeed&&preCost>nowCost)||(!preNeed&&nowNeed))//一定发生替换（）
    			//如果现在的方案满足所有消费节点的需求，且现在方案的nowCost小，则一定替换
    		{
    			preCost=nowCost; preSP=nowSP;preNeed=nowNeed;//替换
    			if(nowCost<minCost)//如果是目前最优的，则记录下来
    			{
    				System.out.println("dofire 替换的路径："+nowSP+"  nowNeed="+nowNeed+"  nowCost="+nowCost);
    				minCost=nowCost;
    				Road=(HashMap)this.preRoud.clone();//记录下nowNeed的路径
    				System.out.println("dofire 替换后的Road:"+Road.size()+"   此时的preRoud:"+preRoud.size());
    			} 
    			continue;
    		}else{
    			if(preNeed&&!nowNeed)//一定不发生替换
    			{
    				continue;//不替换
    			}else{
    				//按照一定的概率进行替换
    				int erro=nowCost-preCost;
    				double r=rand.nextDouble();//随机生成一个双精度型
    				if(r<Math.exp(-(nowCost-preCost)/T))//如果在这一范围内则替换
    				{
    					preCost=nowCost; preSP=nowSP;preNeed=nowNeed;//替换
    					continue;
    				}else
    				{
    					continue;//不替换
    				}
    			}
    		}
    	}
    	return Road;
    	
    }
    
//    //得到真正的Road，即把负边的给去掉
//    public HashMap getRealRoud(HashMap old)
//    {
//    	if(old.isEmpty())
//    		return null;
//    	for(int i=0;i<old.size();i++)
//    	{
//    		ArrayList sub=(ArrayList)old.get(i);//edge中0,2,4..是正向边。1,3,5..,是反向边
//    		for(int j=0;j<sub.size();j++)//找寻含负边的最短路径
//    		{
//    			sss//有多个负边的时候怎么办？
//    		}
//    	}
//    	return null;
//    }
    
    //模拟退火需要的cost function最小花费最大流
    public int smallCostBigFunction()
    {
    	//前面先进行一些数据的预处理工作
    	//正式开始执行算法
    	this.preRoud.clear();//清除上一次得到的路径
    	int minflow,mincost=0,len=0;
    	while(spfa(this.nodeNum+2))//这个n还需不要要加2
    	{
    		ArrayList<Integer> subpath=new ArrayList<Integer>();  		
    		minflow=Inf+1;
    		//求线路允许通过的最大流量，也就是这条线路中包含路径的最小cap
    		for(int i=subPath[St];i!=-1;i=subPath[eee.get(i).u])
    		{
    			subpath.add(i);
    			if(eee.get(i).cap<minflow)
    				minflow=eee.get(i).cap;
    		}
    		this.preRoud.put(len++, subpath);//记录路径,这时候记录的是  subpath里的所有信息  和 该线路上的流量
    		for(int i=subPath[St];i!=-1;i=subPath[eee.get(i).u])
    		{
    			eee.get(i).cap-=minflow;
    			eee.get(i^1).cap+=minflow;//异或操作，对应了改经过边的所对应的反边
    		}
    		//System.out.println("smallCost.. mincost+=Dis[St]*minflow"+Dis[St]+"*"+minflow);
    		mincost+=Dis[St]*minflow;//Dis中已将包含了Cost的信息，所以不用重新再考虑
    		
    	}
    	//System.out.println("smallCostBigFunction mincost="+mincost);
    	return mincost;
    }
    
    
    //最小花费最大流需要的spfa算法,传入的量是超级源点的标号，超级汇点的编号，和所有的节点数目
    public boolean spfa(int allNode)
    {
    	Queue<Integer> qu=new LinkedList<Integer>();
    	for(int i=0;i<subPath.length;i++)
    		subPath[i]=-1;
    	for(int i=0;i<Dis.length;i++)
    		Dis[i]=Inf;
    	Dis[Ss]=0;
    	qu.add(Ss);
    	while(!qu.isEmpty())
    	{
    		int hnode=qu.remove();
    		//System.out.println("spfa hnode="+hnode+"   i="+this.thead[hnode]);//无论出队列的是几，thead[hnode]都是410，这肯定是由问题的
    		for(int i=this.thead[hnode];i!=-1;i=eee.get(i).next)//找到所有以hnode为头结点的边
    		{
    			//System.out.println("spfa out i="+i+"  next"+eee.get(i).next);
//    			if(hnode==37)////////////////////////
//    				System.out.print(i+"   ");
    			int tnode=eee.get(i).v;//找到以hnode为头的弧的尾节点
    			//如果可以这个链路可以过，且代价小
    			if(eee.get(i).cap>0 && Dis[tnode]>Dis[hnode]+eee.get(i).cost)
    			{
    				//System.out.println("spfa inner i="+i);
    				Dis[tnode]=Dis[hnode]+eee.get(i).cost;
    				subPath[tnode]=i;//记录路径
    				if(qu.contains(tnode)==false)//如果尾节点不再队列里面
    					qu.add(tnode);
    			}
    		}
    	}
//    	System.out.println("spfa subpath");//这个subpath包含所有节点到超级源点的信息
//    	for(int i=0;i<this.subPath.length;i++)
//    		if(subPath[i]!=-1)
//    			System.out.print(i+":"+subPath[i]+"  ");
//    	System.out.println();
//    	System.out.println("spfa Dis");
//    	for(int i=0;i<Dis.length;i++)
//    		System.out.print(i+":"+Dis[i]+"  ");
//    	System.out.println();
    	if(Dis[St]>=Inf)
    		return false;
    	return true;	
    }
    
    //最后一步，将得到的结果转化为符合条件的字符串数组
    public String[] changeForm(HashMap Roud)
    {
    	if(Roud==null)//如果没有找到路径，则返回一个空指针
    		return null;
    	//如果找到了路径
    	System.out.println("changeForm中最终找到的路径数目是："+Roud.size());
    	String[] contents=new String[Roud.size()+2];//
    	contents[0]=Roud.size()+"";//
    	for(int i=1;i<Roud.size()+2;i++)
    		contents[i]="";
    	for(int i=0;i<Roud.size();i++)
    	{
    		ArrayList everyRoud=(ArrayList)Roud.get(i);
    		for(int j=0;j<everyRoud.size();j++)
    		{
    			if(j!=everyRoud.size()-1)
    				contents[i+2]=everyRoud.get(j)+" ";
    			else
    				contents[i+2]=everyRoud.get(j)+"";//最后一个是容量，后面应该没有空格
    		}
    	}
    	return contents;
    	
    }
    
    //改变服务器放置的三种策略
    public ArrayList changeServerMethod(ArrayList SP1,boolean need)
    {
    	ArrayList SP=(ArrayList)SP1.clone();
    	Random rr=new Random();//生成随机量
    	double meths=rr.nextDouble();//确定以下三种方法的随机量
    	//methed 1:增加服务器数量
    	//System.out.println("changeServerMethod need="+need);
    	if(SP1.size()<this.CondumerNum && (need==false || meths<0.2))
    	{   
    		//System.out.println("changeServerMethod 增加了一台");
    		while(true)//当没有加入成功
    		{
    			int add=rr.nextInt(this.nodeNum);//在除超级源点和超级汇点的网络节点里随机选择一个加入
    			if(SP.indexOf(add)==-1)//说明没有在原来的服务器中
    			{
    				SP.add(add);
    				break;
    			}
    		}
    		return SP;
    	}
    	//method 2:减少服务器器数量
    	if(SP.size()>1 && meths>=0.2 && meths<0.5)
    	{
    		//System.out.println("changeServerMethod 减少了一台");
    		int del=rr.nextInt(SP.size());
    		SP.remove(del);
    		return SP;
    	}
    	//method 3:改变服务器位置
    	if(meths>=0.5)
    	{
    		//System.out.println("changeServerMethod 改变服务器位置");
    		while(true)//当没有加入成功
    		{
    			int add=rr.nextInt(this.nodeNum);//在除超级源点和超级汇点的网络节点里随机选择一个加入
    			if(SP.indexOf(add)==-1)//说明没有在原来的服务器中
    			{
    				SP.add(add);
    				break;
    			}
    		}
    		int del=rr.nextInt(SP.size()-1);
    		SP.remove(del);
    		return SP;
    	}
    	return SP;
    }
    ////////////////////////上面主要函数所要调用的一些小函数////////////\
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
    //构建网络时的加边操作
    public void addedges(int u,int v,int cap,int cost)
    {
    	//构造函数需要：int u,int v,int cap,int cost,int next
    	Edge e=new Edge(u,v,cap,cost,head[u]);//正向边
    	this.edge.add(e);
    	head[u]=(edge.size()-1);//以u为头结点的弧所在的边的位置
    	Edge ee=new Edge(v,u,0,-cost,head[v]);//反向边
    	this.edge.add(ee);
    	head[v]=(edge.size()-1);//以v为头结点的弧所在的边的位置
    }
    //构建网络时的加边操作
    public void addedgesthead(int u,int v,int cap,int cost)
    {
    	//构造函数需要：int u,int v,int cap,int cost,int next	
    	Edge e=new Edge(u,v,cap,cost,thead[u]);//正向边
    	this.eee.add(e);
    	thead[u]=(eee.size()-1);//以u为头结点的弧所在的边的位置
    	Edge ee=new Edge(v,u,0,-cost,thead[v]);//反向边
    	//System.out.println("addedgesthead before thead[v]"+thead[v]+"  v="+v);
    	this.eee.add(ee);
    	thead[v]=(eee.size()-1);//以v为头结点的弧所在的边的位置
    	//System.out.println("addedgesthead after thead[v]"+thead[v]+"   v="+v);
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
    	for(int j=0;j<this.nodeNum;j++)
    	{
    		ArrayList<Integer> aa=new ArrayList<Integer>();
    		this.Adj.put(j, aa);
    	}
    }
    //构建超级汇点
    public void buildSuperT()
    {
    	Tb=edge.size();//为啥有减号我也不知道
    	//System.out.println("buildSuperT head[c]");
    	for(int i=0;i<this.CondumerNum;i++)
    	{
    		int c=this.CPosition.get(i)[1];
    		
    		this.addedges(c, this.St, CPosition.get(i)[2], 0);
    		//System.out.print(c+":"+head[c]+"  ");
    		//Edge e=new Edge(c,this.St,CPosition.get(i)[2],0,head[c]);
    		//edge.add(e);
    	}
    	//System.out.println();
    	Ts=edge.size()-1;
    }
    //构建超级源点
    public void buldSuperS(ArrayList SP)
    {
    	for(int i=0;i<SP.size();i++)
    	{
    		int node=(int)SP.get(i);
    		this.addedgesthead(Ss, node, Inf, 0);
    		//Edge e=new Edge(Ss,node,Inf,0,thead[Ss]);
    		//eee.add(e);
    	}
//    	System.out.println("buldSuperS's check");///
//    	for(int i=Tb;i<=Ts;i=i+2)//因为加1之后是反边
//    		System.out.print(eee.get(i).u+"-->"+eee.get(i).v+"="+eee.get(i).cap+"  ");//
//    	System.out.println();///
//    	
//    	System.out.println("buldSuperS's check edge");///
//    	for(int i=Tb;i<=Ts;i=i+2)//因为加1之后是反边
//    		System.out.print(edge.get(i).u+"-->"+edge.get(i).v+"="+edge.get(i).cap+"  ");//
//    	System.out.println();///
    }
    //判断是否所有消费节点的需求都得到了满足
    public boolean checkCNeed()
    {
    	//System.out.println("checkCNeed's Tb="+Tb+"  Ts="+this.Ts);///
    	for(int i=Tb;i<=Ts;i=i+2)//因为加1之后是反边
    	{
    		//System.out.print(eee.get(i).u+"-->"+eee.get(i).v+"="+eee.get(i).cap+"  ");//
    		if(eee.get(i).cap>0)
    			return false;
    	}
    	//System.out.println();///
    	return true;
    }
    
    //测试用clone方法1：重写clone方法，将ArrayList遍历，clone到底
    public ArrayList deepCloneBaseFor(ArrayList n)
    {
    	ArrayList<Edge> r=new ArrayList<Edge>();
    	for(int i=0;i<n.size();i++)
    	{
    		Edge e=(Edge)((Edge)n.get(i)).clone();
    		r.add(e);
    	}
    	return r;
    }
    //测试用clone方法2：用序列化进行克隆

}



