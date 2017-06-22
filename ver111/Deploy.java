//package com.cacheserverdeploy.deploy
package realcode.ver111;
/*
 * 改进之处：
 * 只实现两个大幅度的减少，没有排序操作，都是随机减少，即不排序，也不记录
 * */
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
	private HashMap<Integer,ArrayList> Adj=new HashMap<Integer,ArrayList>();
	private HashMap<Integer,ArrayList> preRoud=new HashMap<Integer,ArrayList>();
	private static boolean stop=false;
	private static Timer timer=new Timer();//触动定时器，如果到85s则将stop改成true用来终止最优化循环
	private ArrayList<Integer> minSP;
	private HashMap<Integer,Integer> CPosi=new HashMap<Integer,Integer>();//为了最后转化正确路径的时候迅速
	private int Sb,Se;//超级源点建立的起始边代号，超级源点建立的终止边代号
	private int minNode,deteNode,minNode2,detIter;//记录删除的时候提供最小流量的节点，和最终删除的节点
	
    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        Deploy d=new Deploy(); 
        timer.schedule(new MyTask(), 89000);//89200
        d.getBasicInformation(graphContent); 
        return d.changeForm(d.getRealRoud(d.dofire(d.getIntaSeverPosition())));
    }
    //定时器
    static class MyTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			stop=true;
			timer.cancel();
		}
    	
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
    			this.Adj.get(CN[0]).add(CN[1]);//加入到邻接表,这是因为该算法不考虑空间复杂性所以才那么做的
    			this.Adj.get(CN[1]).add(CN[0]);//如果算法还考虑空间复杂性，那么可以用NodewithEdges和edge的关系来做
    			continue;
    		}
    		if(layer==3 && !g[i].equals(""))//消费节点编号 消费节点相连节点 需要的最小代价,用于构建超级汇点
    		{
    			this.CPosition.add(new int[]{CN[0],CN[1],CN[2]});//这个初始化方法不错
    			this.CPosi.put(CN[1], CN[0]);
    		}
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
    	int T0=3000,opiter=50000000;//模拟退火的初试温度,模拟退火外循环的迭代次数
    	double T;//模拟退火的当前温度
    	boolean preNeed,nowNeed=true;//记录过去和现在的方案中，所有消费节点的需求是否可以得到满足
    	int preCost,nowCost=Inf,minCost=Inf;//记录过去和现在方案的花费，最小花费
    	HashMap Road=new HashMap();//目前记录的是edge的标示
    	eee=this.deepCloneBaseFor(edge);//因为每次用的都是不同的，这样就不用每次都建了
    	thead=(int[])head.clone();
    	//算法正式开始
    	//针对第一个数据
    	this.buldSuperS(SP1);//根据传入的服务器位置SP1，构建超级源点
    	preCost=this.smallCostBigFunction()+SP1.size()*this.ServerMoney;//得到第初试服务器放置的最大流情况下的花费
    	preNeed=this.checkCNeed();
    	if(preNeed)//如果能满足条件，则先将路径记录下来
    	{
    		minCost=preCost;
    		Road=this.HashMapdeepclone(this.preRoud);//	
    		//Road=t;//
    		minSP=(ArrayList)SP1.clone();
    	}	
    	//针对以后的数据
    	ArrayList preSP=SP1,nowSP;//原来和现在服务器的摆放位置
    	//System.out.println("dofire 开始循环");
    	this.detIter=0;
    	int iter=0;
    	for(;!stop && iter<opiter;iter++)
    	{
    		T=T0/(1+iter);//当前温度
    		
    		//3中改变策略（增加服务器，减少服务器，改变服务器位置）
    		nowSP=this.changeServerMethod(preSP,preNeed,iter);//因为要知道上一次是谁提供的流量最少，所以把这一条放在了重建网络的前面
    		//System.out.println("dofire 成功进行了一次 changeServerMethd   "+iter);//也就是问题出现在没有被替换的时候
    		//错误出现在，如果减少服务器操作没有成功，那么eee也发生了变换，因为我删除的时候已经用到了eee此时，eee肯定不同了，解决方案是在record里面存的不是链路索引，而是要删除的节点号
    		eee=this.deepCloneBaseFor(edge);//重新恢复到还没有确定服务器的状态
    		thead=(int[])head.clone();
    			
    		this.buldSuperS(nowSP);//构建超级汇点
    		nowCost=this.smallCostBigFunction()+nowSP.size()*this.ServerMoney;
    		nowNeed=this.checkCNeed();//检查是否满足了所有服务器的要求
    		if((preNeed&&nowNeed&&preCost>nowCost)||(!preNeed&&nowNeed))//一定发生替换（）
    			//如果现在的方案满足所有消费节点的需求，且现在方案的nowCost小，则一定替换
    		{
    			preCost=nowCost; preSP=nowSP;preNeed=nowNeed;//替换
    			if(nowCost<minCost)//如果是目前最优的，则记录下来
    			{
    				//System.out.println("dofire 替换的路径："+nowSP+"  nowNeed="+nowNeed+"  nowCost="+nowCost);
    				System.out.println("第"+iter+"次迭代"+"  "+nowCost);
    				minCost=nowCost;
    				Road=this.HashMapdeepclone(preRoud);//记录下nowNeed的路径
    				minSP=(ArrayList)nowSP.clone();
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
    	System.out.println("iter="+iter+"  minCost="+minCost+"  stop="+stop);
    	
    	return Road;
    	
    }
    
    //得到真正的Road，即把负边的给去掉
    public HashMap getRealRoud(HashMap old)
    {
    	if(old.isEmpty())
    		return null;
    	
    	eee=this.deepCloneBaseFor(edge);//因为每次用的都是不同的，这样就不用每次都建了
    	this.buldSuperS(minSP);
    	
    	//System.out.println("getRealRoud  oldRoud:"+old.size());
    	
    	int len=old.size();
    	HashMap<Integer,Integer> allE=new HashMap<Integer,Integer>();//用于构建Hash表
    	HashMap<Integer,ArrayList> realRoud=new HashMap<Integer,ArrayList>();//真正路径
    	for(int i=0;i<len;i++)//这个循环得到allE，也就是记录每条路径应该走多少流量
    	{//这个地方不应该是 i<old.size 因为 old有remove操作，会导致它的长度越来越小，导致循环过早结束
    		ArrayList<Integer> sub=(ArrayList<Integer>)old.get(i);//得到第i条路径
    		for(int j=0;j<sub.size()-1;j++)//将所有的边放在Hash表中，计算每条边实际走了多少流量，也就是去除负边的情况下一共有多少流量
    		{//注意，sub的最后一位是目前该条线路的流量,除最后一位的前几位，应该是节点的索引
    			//本来这个地方错了，在allE中，应该是不存储负边的
    			int idex=sub.get(j);
    			if(idex%2!=0)
    				idex=idex^1;
    			if(allE.get(idex)==null)//如果目前还没有存入,则初始化为0
    				allE.put(idex, 0);
    			int flow=allE.get(idex);
    			if((int)sub.get(j)%2==0)//(不是我们构建Ss或St加的边 && 边的标示是偶数)
    				flow=flow+(int)sub.get(sub.size()-1);
    			else//如果是负边
    				flow=flow-(int)sub.get(sub.size()-1); 	
    			allE.remove(idex);//将allE中的flow更新成最新的
    			allE.put(idex, flow);			
    		}
    	}
    	//从allE的数据中得到路径-->构建HashMap-->根据HashMap找路径
    	//构建HashMap
    	HashMap<Integer,LinkedList> find=new HashMap<Integer,LinkedList>();
    	Set s=allE.keySet();
		Iterator it=s.iterator();
		for(;it.hasNext();)
		{
			int edex=(int)it.next();
			int u=this.eee.get(edex).u;//头结点
			int v=this.eee.get(edex).v;//尾节点
			if(find.get(u)==null)//如果暂时无此头结点,则让它有孩子
			{
				LinkedList<int[]> son=new LinkedList<int[]>();
				find.put(u, son);
			}
			LinkedList<int[]> temp=find.get(u);//找到头结点u对应的尾节点表
			if(allE.get(edex)!=0)//因为有个118到70的路径不知道是什么鬼
				temp.add(new int[]{u,v,allE.get(edex)});//在尾节点里分别存入{头结点，尾节点，allFlow}
			else//因为出现有 118 但是 size=0 这就说明118加了个空的，那么应该就是这里删去，这是因为走正走负，导致有些边其实没有走
			{//一直认为是后面错了，想不到。。。。
				if(find.get(u).isEmpty())
					find.remove(u);
			}
		}
		//根据HashMap找路径
		int reallen=0;//做realRoud的key
		while(!find.isEmpty())//当find表不为空时
		{
			ArrayList<Integer> path=new ArrayList<Integer>();
			int hh=Ss;
			int min=Inf;
			int[] shiyan=new int[3];
			while(hh!=St)//当不是超级汇点的时候
			{
				int mq=((LinkedList<int[]>)find.get(hh)).getFirst()[2];
				if(min>mq)
					min=mq;	
				shiyan=((LinkedList<int[]>)find.get(hh)).getFirst();
				hh=shiyan[1];				
				path.add(hh);
			}
			path.remove(path.size()-1);//将超级汇点去掉
			hh=Ss;//head我已经定义为全局变量了
			while(hh!=St)//根据minflow，更改路径上的allFlow，如果allFlow为0则删除，如果被删除的头结点也为空了，则也删除对应find上的一行
			{				
				((LinkedList<int[]>)find.get(hh)).getFirst()[2]-=min;
				int next=((LinkedList<int[]>)find.get(hh)).getFirst()[1];			
				if(((LinkedList<int[]>)find.get(hh)).getFirst()[2]==0)
				{
					((LinkedList<int[]>)find.get(hh)).removeFirst();//如果等于0，则删除
					if(((LinkedList<int[]>)find.get(hh)).isEmpty())
						find.remove(hh);//如果没有了尾节点，则删除该HashMap的head边
				}
				hh=next;
			}
			path.add(this.CPosi.get(path.get(path.size()-1)));//加入消费者的编号
			path.add(min);//在path中增加minflow
			realRoud.put(reallen++, path);//在realRoud中增加path				
		}
		
		System.out.println("-----------测试整体供给和整体需求--------------");
		int sum1=0,sum2=0;
		for(int i=0;i<realRoud.size();i++)
		{
			ArrayList<Integer> pa=realRoud.get(i);
			sum1+=pa.get(pa.size()-1);
		}
		for(int i=0;i<this.CondumerNum;i++)
			sum2+=this.CPosition.get(i)[2];
		System.out.println(" path sum--->"+sum1);
		System.out.println(" real sum--->"+sum2);	
		System.out.println("----------------------------------------");
    	
    	return realRoud;
    }
    
    //模拟退火需要的cost function最小花费最大流
    public int smallCostBigFunction()
    {
    	//前面先进行一些数据的预处理工作
    	//正式开始执行算法
    	this.preRoud.clear();//清除上一次得到的路径
    	//this.smallCost=0;
    	int minflow,minCost=0,len=0;
    	while(spfa(this.nodeNum+2))//这个n还需不要要加2
    	{
    		ArrayList<Integer> subpath=new ArrayList<Integer>();  		
    		minflow=Inf+1;
    		//求线路允许通过的最大流量，也就是这条线路中包含路径的最小cap
    		for(int i=subPath[St];i!=-1;i=subPath[eee.get(i).u])
    		{
    			subpath.add(i);//包含超级源点路径，也包含超级汇点路径,但是是逆序的
    			if(eee.get(i).cap<minflow)
    				minflow=eee.get(i).cap;
    		}
    		subpath.add(minflow);//将线路此时的流量信息放到最后一位
    		preRoud.put(len++, subpath);//记录路径,这时候记录的是  subpath里的所有信息  和 该线路上的流量
    		for(int i=subPath[St];i!=-1;i=subPath[eee.get(i).u])
    		{
    			eee.get(i).cap-=minflow;
    			eee.get(i^1).cap+=minflow;//异或操作，对应了改经过边的所对应的反边
    		}
    		minCost+=Dis[St]*minflow;//Dis中已将包含了Cost的信息，所以不用重新再考虑
    		
    	}
    	return minCost;
    }
    
    
    //最小花费最大流需要的spfa算法,传入的量是超级源点的标号，超级汇点的编号，和所有的节点数目
    public boolean spfa(int allNode)
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
    		for(int i=this.thead[hnode];i!=-1;i=eee.get(i).next)//找到所有以hnode为头结点的边
    		{
    			int tnode=eee.get(i).v;//找到以hnode为头的弧的尾节点
    			//如果可以这个链路可以过，且代价小
    			if(eee.get(i).cap>0 && Dis[tnode]>Dis[hnode]+eee.get(i).cost)
    			{
    				Dis[tnode]=Dis[hnode]+eee.get(i).cost;
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
    
    //最后一步，将得到的结果转化为符合条件的字符串数组
    public String[] changeForm(HashMap Roud)
    {
    	if(Roud==null)//如果没有找到路径，则返回一个空指针
    		return null;
    	//如果找到了路径
    	//System.out.println("changeForm中最终找到的路径数目是："+Roud.size());
    	String[] contents=new String[Roud.size()+2];//
    	contents[0]=Roud.size()+"";//第一行，路径数
    	int i=1;
    	for(;i<Roud.size()+2;i++)
    		contents[i]="";
    	i=2;
		Set s=Roud.keySet();
		Iterator it=s.iterator();
		for(;it.hasNext();)
		{
			int k=(int)it.next();
			ArrayList<Integer> everyRoud=(ArrayList<Integer>)Roud.get(k);
    		for(int j=0;j<everyRoud.size();j++)
    		{
    			if(j!=everyRoud.size()-1)
    				contents[i]+=everyRoud.get(j)+" ";
    			else
    				contents[i++]+=everyRoud.get(j)+"";//最后一个是容量，后面应该没有空格
    		}
		}
    	
    	return contents;
    	
    }
    
    //改变服务器放置的三种策略
    public ArrayList changeServerMethod(ArrayList SP1,boolean need,int iter)
    {
    	ArrayList SP=(ArrayList)SP1.clone();
    	Random rr=new Random();//生成随机量
    	double meths=rr.nextDouble();//确定以下三种方法的随机量
    	double addS,detS;
    	int del;
    	if(this.nodeNum<800)//如果不是高级样例
    	{
    		if(this.nodeNum<300)
    		{
    			addS=0.18;
    			detS=0.5;
    		}else
    		{
    			addS=0.1;//增加服务器的概率
    			detS=0.5;
    			if(iter<=300)
    				detS=0.72;
    		}
    	}else
    	{
    		addS=0.1;//增加服务器的概率
    		detS=0.8;
    		if(iter>200)
    			detS=0.7;
    	}
    	//methed 1:增加服务器数量
    	if(SP1.size()<this.CondumerNum && (need==false || meths<addS))
    	{   
    		//System.out.println("增加服务器操作");
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
    	if(SP.size()>1 && meths>=addS && meths<detS)
    	{
    		//System.out.println("减少服务器操作");
    		if(this.nodeNum<200)
    		{
    			del=rr.nextInt(SP.size());
    			SP.remove(del);
    			return SP;
    		}
    		
    		if(this.nodeNum<700 || this.detIter>9)
    		{
	    		int min=Inf;
	    		int minN=0;
	    		for(int i=Sb+1;i<=Se;i=i+2)
	    		{
	    			//System.out.print(eee.get(i).u+"("+eee.get(i).cap+")   ");
	    			if(eee.get(i).cap<min)
	    			{
	    				min=eee.get(i).cap;
	    				minN=eee.get(i).u;//真实的源点应该是超级节点的尾节点,但是因为这是负边，所以真实的是源点就是边的头结点
	    			}
	    		}
	    		if(minN==minNode)//说明现在出现了删除该最小的就满足不了消费节点的需求的情况
	    			del=rr.nextInt(SP.size());
	    		else
	    		{
	    			this.minNode=minN;	
	    			del=SP.indexOf(this.minNode);
	    			if(del==-1)//当前一步没替换换时，会出现这样的情况
	    				del=rr.nextInt(SP.size());
	    		}
	    		SP.remove(del);
	    		
	    		if(this.nodeNum>700 && iter<25)
	    		{
	    			int del2=rr.nextInt(SP.size());
	    			SP.remove(del2);
	    		}
    		}else
    		{//针对高级用例的前几次，大幅度减少节点的数量
    			
    			int detNum=0;
    			int shu=detIter;
    			if (detIter>5)
    				shu=(this.detIter-4)%6;
    			switch(this.detIter)
    			{
    			case 0://删除50个
    				detNum=50;
    				break;
    			case 1://删除30个
    				detNum=30;
    				break;
    			case 2://删除20个
    				detNum=20;
    				break;
    			case 3://删除10个
    				detNum=10;
    				break;
    			default://删除5个
    				detNum=5;
    				break;
    				
    			}
    			for(int gaod=0;gaod<detNum;gaod++)
    			{
    				int del2=rr.nextInt(SP.size());
    				SP.remove(del2);
    			}
    			
    			this.detIter++;
    		}
    		
    		return SP;
    	}
    	//method 3:改变服务器位置
    	if(meths>=detS)
    	{
    		//System.out.println("改变服务器位置");
    		while(true)//当没有加入成功
    		{
    			int add=rr.nextInt(this.nodeNum);//在除超级源点和超级汇点的网络节点里随机选择一个加入
    			if(SP.indexOf(add)==-1)//说明没有在原来的服务器中
    			{
    				SP.add(add);
    				break;
    			}
    		}
    		//删除一台服务器
    		int min=Inf;
    		int minN=0;
    		for(int i=Sb+1;i<=Se;i=i+2)
    		{
    			//System.out.print(eee.get(i).u+"("+eee.get(i).cap+")   ");
    			if(eee.get(i).cap<min)
    			{
    				min=eee.get(i).cap;
    				minN=eee.get(i).u;//真实的源点应该是超级节点的尾节点,但是因为这是负边，所以真实的是源点就是边的头结点
    			}
    		}
    		del=SP.indexOf(minN);
    		if(del==-1)//当前一步没替换换时，会出现这样的情况
    			del=rr.nextInt(SP.size());		
    		SP.remove(del);
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
    	Edge e=new Edge(u,v,cap,cost,thead[u]);//正向边
    	this.eee.add(e);
    	thead[u]=(eee.size()-1);//以u为头结点的弧所在的边的位置
    	Edge ee=new Edge(v,u,0,-cost,thead[v]);//反向边
    	this.eee.add(ee);
    	thead[v]=(eee.size()-1);//以v为头结点的弧所在的边的位置
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
    	Tb=edge.size();
    	for(int i=0;i<this.CondumerNum;i++)
    	{
    		int c=this.CPosition.get(i)[1];  		
    		this.addedges(c, this.St, CPosition.get(i)[2], 0);
    	}
    	Ts=edge.size()-1;
    	this.minNode=Inf;
    	this.minNode2=Inf;
    }
    //构建超级源点
    public void buldSuperS(ArrayList SP)
    {
    	this.Sb=eee.size();
    	for(int i=0;i<SP.size();i++)
    	{
    		int node=(int)SP.get(i);
    		this.addedgesthead(Ss, node, Inf, 0);
    	}
    	this.Se=eee.size()-1;
    	//this.detIter=0;
    }
    //判断是否所有消费节点的需求都得到了满足
    public boolean checkCNeed()
    {
    	for(int i=Tb;i<=Ts;i=i+2)//因为加1之后是反边
    	{
    		if(eee.get(i).cap>0)
    			return false;
    	}
    	return true;
    }
    //HashMap的克隆方法
	public HashMap<Integer,ArrayList> HashMapdeepclone(HashMap<Integer,ArrayList> Old)
	{
		HashMap<Integer,ArrayList> New=new HashMap<Integer,ArrayList>();
		for(int i=0;i<Old.size();i++)
		{
			ArrayList<Integer> aa=new ArrayList<Integer>();
			ArrayList<Integer> ao=(ArrayList)Old.get(i);
			aa=(ArrayList)ao.clone();//不能成功拷贝的程序
			New.put(i, aa);
		}
		return New;
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
    

}



