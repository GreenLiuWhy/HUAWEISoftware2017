//package com.cacheserverdeploy.deploy
package realcode.yichuan.ver1;
/*
 * 改进之处：对changeSeverMethod()函数的删除操作进行算法上的优化
 * 找目前提供流量最少的删除 的 优化全部做完，
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
	private int therebegain;
	
    public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        Deploy d=new Deploy(); 
        timer.schedule(new MyTask(), 89200);//89200
        d.getBasicInformation(graphContent); 
        return d.changeForm(d.getRealRoud(d.GA(d.getIntaSeverPosition())));
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
    
    //第二步开始遗传,传如服务器的初试的路径信息
    public HashMap GA(ArrayList<Integer> SP1)
    {
    	//HashMap Road=new HashMap();
    	double Pm=0.9;//变异发生的概率
    	double Pc=0.5;//交叉发生的概率
    	int M=4;//种群的数量
    	int opiter=50000;//终止进化的代数
    	int iter=0;
    	Random r=new Random();
    	
    	int minCost=Inf;
    	
    	int three,four;
    	if(this.nodeNum==800)//高级
    	{
    		three=30;
    		four=50;
    	}else
    	{
    		if(this.nodeNum==300)//中级
    		{
    			three=20;
        		four=30;
    		}else
    		{//初级
    			three=10;
        		four=20;
    		}
    	}
    	//生成第一代种群，其中一个直接与消费节点相连SP1，其余的随机生成与消费节点等多的，或比消费节点数量略少的节点数量   	
    	ArrayList<Integer> SP2=this.getRandomPop(this.CondumerNum-5);
    	ArrayList<Integer> SP3=this.getRandomPop(this.CondumerNum-three);
    	ArrayList<Integer> SP4=this.getRandomPop(this.CondumerNum-four);
    	
    	ArrayList<ArrayList> allSP=new ArrayList<ArrayList>();
    	allSP.add(SP1); allSP.add(SP2); allSP.add(SP3); allSP.add(SP4);
    	
    	ArrayList<int[]> arrSP=new ArrayList<int[]>();
    	
    	//计算原始种群的每一个个体的适应度F(i)
    	int[] F=new int[M];
    	boolean[] Need=new boolean[M];
    	for(int i=0;i<allSP.size();i++)
    	{
    		arrSP.add(this.getArrayFromSP(allSP.get(i)));//建立对应的表，传如的是ArrayList的SP,传出的是int[]
    		eee=this.deepCloneBaseFor(edge);
    		thead=(int[])head.clone();
    		this.buldSuperS(allSP.get(i));//建立超级源点
    		F[i]=this.smallCostBigFunction()+(allSP.get(i)).size()*this.ServerMoney;
    		Need[i]=this.checkCNeed();
    	}
    	
    	int MaxCost=F[0];
    	for(;!stop && iter<opiter;iter++)//执行迭代
    	{   		
    		System.out.print(iter+":");
			for(int i=0;i<M;i++)
			{
				System.out.print("  F:"+F[i]+"   Need:"+Need[i]);
			}
			System.out.println();
    		ArrayList<ArrayList> preSP=new ArrayList<ArrayList>();//初始化新种群
    		//System.out.println("GA:"+iter);
    		
    		int tt=0;
    		for(int i=0;i<Need.length;i++)
    			if(Need[i]==true)
    				tt++;
    		
    		while(preSP.size()<M)
    		{			
    			int f=0,m=0;
    			switch(tt)
    			{
    			case 1://只有一个满足条件
    				int min=Inf;
    				m=0;
    				for(int i=1;i<Need.length;i++)//有一个是满足条件的,另外一个是出满足条件的后代价最小的
    				{
    					if(min>F[i])
    					{
    						min=F[i];
    						m=i;
    					}
    				}
    				break;
    			case 2://如果有两个，则f是这两个中代价小的，m是这两个中代价大的
    				f=0;
    				for(int i=1;i<Need.length;i++)
    					if(Need[i]==true)
    						m=i;
    				break;
    			default://随机选两个
    				f=0;
    				m=r.nextInt(allSP.size());
    				while(f==m)
    				{
    					m=r.nextInt(allSP.size());
    				}
    			}
    			
    			ArrayList<Integer> father=(ArrayList<Integer>)(allSP.get(f)).clone();
    			ArrayList<Integer> mother=(ArrayList<Integer>)(allSP.get(m)).clone();
    			
    			double rc=r.nextDouble();
    			if(rc<Pc)
    			{//对两个个体执行交叉操作，从长的开始，相同的一定要，不同的，随机给对方或者留给自己
    				ArrayList<Integer> son1;
    				ArrayList<Integer> son2;
    				int[] s1,s2;
    				if(father.size()>mother.size())//也就是son1是长的那一个
    				{
    					son1=(ArrayList<Integer>)father.clone(); 
    					s1=(arrSP.get(f)).clone();
    					son2=(ArrayList<Integer>)mother.clone();
    					s2=(arrSP.get(m)).clone();
    				}else
    				{
    					son1=(ArrayList<Integer>)mother.clone();
    					s1=(arrSP.get(m)).clone();
    					son2=(ArrayList<Integer>)father.clone(); 
    					s2=(arrSP.get(f)).clone();
    				}
    				//从大的开始，也就是从son1开始找
    				for(int i=0;i<son1.size();i++)
    				{
    					if(stop)
    						break;
    					int ss=son1.get(i);//也就是ss是节点序列
    					if(s2[ss]==0)//找它含有的元素在son2中有没有
    					{//百分之50留给自己(也就是什么也不用做)，百分之50给son2
    						double rl=r.nextDouble();
    						if(rl<0.5)//给son2
    						{
    							son2.add(ss);//添加给对方
    							s2[ss]=son2.size()-1;
    							son1.remove(i);//将自己的删除
    							s1[ss]=0;
    						}
    					}
    					
    				}
    				preSP.add(son1);
    				preSP.add(son2);
    			}
    			
    			double rm=r.nextDouble();
    			if(rm<Pm)
    			{//变异的情况下，服务器数量就等于原来服务器的数量，对其它两个取与操作，如果都有则加入，而对于非与的
    				//的则随机删除，最终得到与原来数量相同的服务器布置
    				ArrayList<Integer> son3=father;//对两个个体进行变异操作，编译操作是仿照DE的方法执行的
    				//先简单的，随机选1个点发生变化
    				int rnc=r.nextInt(son3.size());
    				son3.remove(rnc);//其实就是随机删除一个
    				preSP.add(son3);//将产生的新个体加入到prePop中 
    			}
    			  			
    		}
    		
    		//计算子代的F[i]和Need[i]
    		int[] sonF=new int[preSP.size()];
        	boolean[] sonNeed=new boolean[preSP.size()];
        	for(int i=0;i<preSP.size() && !stop;i++)
        	{
        		if(stop)
        			break;
        		eee=this.deepCloneBaseFor(edge);
        		thead=(int[])head.clone();
        		this.buldSuperS(preSP.get(i));//建立超级源点
        		sonF[i]=this.smallCostBigFunction()+(preSP.get(i)).size()*this.ServerMoney;
        		sonNeed[i]=this.checkCNeed();
        	}
        	
        	if(stop)
        	{
        		//选取最好的出来,最好的其实就在allSP中的第一个
        		eee=this.deepCloneBaseFor(edge);
        		thead=(int[])head.clone();
        		this.buldSuperS(allSP.get(0));//建立超级源点
        		minCost=this.smallCostBigFunction()+allSP.get(0).size()*this.ServerMoney;
        		System.out.println("  iter="+iter+"  minCost="+minCost+"  stop="+stop);
            	minSP=allSP.get(0);
            	return preRoud;
        	}else{
        		//在子代和父带中选取比较好的M个去更新新种群，排序
        		//排序的时候分组，满足条件的在前，不满足条件的在后，然后再不同分组里从小到大排序，选择前4个
        		ArrayList<Integer> fsor=this.orderFatherSon(F, Need, sonF, sonNeed);
        		ArrayList<ArrayList> tempSP=new ArrayList<ArrayList>();//初始化新种群
        		int[] tempF=new int[M];
        		//fsor[父0子1][cost]...
        		int ffl=0;
        		for(int i=0;i<2*M;i=i+2)
        		{
        			tempF[ffl++]=fsor.get(i+1);
        			if(fsor.get(i)==0)//说明来自父亲
        			{
        				int fp=0;
        				for(int m=0;m<F.length;m++)//在父亲中找到
        					if(F[m]==fsor.get(i+1))
        					{
        						fp=m;
        						break;
        					}
        				tempSP.add(allSP.get(fp));//将对应的放置方式加入到tempSP中
        			}else
        			{
        				int sp=0;
        				for(int m=0;m<sonF.length;m++)//在父亲中找到
        					if(sonF[m]==fsor.get(i+1))
        					{
        						sp=m;
        						break;
        					}
        				tempSP.add(preSP.get(sp));//将对应的放置方式加入到tempSP中
        			}
        		}
            	allSP.clear();//更新allSP
            	allSP=tempSP;
            	arrSP.clear();
            	for(int i=0;i<allSP.size();i++)
            		arrSP.add(this.getArrayFromSP(allSP.get(i)));//更新arrSP
            	F=tempF;//更新F[]
            	int ii=0;
            	for(;ii<M && ii<this.therebegain/2;ii++)//更新Need
            		Need[ii]=true;
            	for(;ii<M;ii++)
            		Need[ii]=false;
            	
        	}       	
    	}    	
    	
    	System.out.println("iter="+iter+"  minCost="+minCost+"  stop="+stop);
    	
    	return null;
    	
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
    
    //随机生成含有size个消费节点的初试种群
    public ArrayList<Integer> getRandomPop(int size)
    {
    	ArrayList<Integer> SP=new ArrayList<Integer>();
    	Random r=new Random();
    	while(SP.size()<size)
    	{
    		int ra=r.nextInt(this.nodeNum);//随机生成一个加入的节点
    		if(SP.indexOf(ra)==-1)//如果原来不在里面
    			SP.add(ra);
    	}
    	return SP;
    }
    
    //在初试allSP中选出1个
    public int RWS(double[] P,double r)
    {
    	int m=0;
    	for(int i=0;i<P.length;i++)
    	{
    		m+=P[i];
    		if(r<=m)
    			return i;
    	}
    	return 5;
    }
    
    //得到SP对应的仿2进制数组
    public int[] getArrayFromSP(ArrayList<Integer> SP)
    {
    	int[] arr=new int[this.nodeNum];//因为int数组是初试话为0的，而我的节点从0开始编号，
    	//这样会带来混淆，而每次将数组初试为-1太麻烦，所以，就把有数据的0改变，改成this.node
    	for(int i=0;i<SP.size();i++)
    	{
    		arr[SP.get(i)]=i;
    		if(i==0)
    			arr[SP.get(i)]=this.nodeNum;
    	}
    	return arr;
    }
    
    //得到父代和子代的排序
    public ArrayList<Integer> orderFatherSon(int[] F,boolean Need[],int[] sonF,boolean sonNeed[])
    {
    	//首先根据是否满足条件进行分组
    	ArrayList<Integer> notNeed=new ArrayList<Integer>();
    	ArrayList<Integer> haveNeed=new ArrayList<Integer>();
    	for(int i=0;i<F.length;i++)//将父亲的分类
    	{
    		if(Need[i]==true)
    		{
    			haveNeed.add(0);//偶数的列0表示来自父亲，
    			haveNeed.add(F[i]);
    		}else{
    			notNeed.add(0);
    			notNeed.add(F[i]);
    		}
    	}
    	for(int i=0;i<sonF.length;i++)//将孩子的分类
    	{
    		if(sonNeed[i]==true)
    		{
    			haveNeed.add(1);//偶数的列1表示来自孩子，
    			haveNeed.add(sonF[i]);
    		}else{
    			notNeed.add(1);
    			notNeed.add(sonF[i]);
    		}
    	}
    	
    	ArrayList<Integer> ok=new ArrayList<Integer>();//将两个分组和起来
    	//对每个分组的分别进行排序
    	while(!haveNeed.isEmpty())//对满足条件的进行排序
    	{
    		int minC=Inf;
    		int min=0;
    		for(int i=1;i<haveNeed.size();i=i+2)
    		{
    			if(minC>haveNeed.get(i))
    			{
    				min=i;
    				minC=haveNeed.get(i);
    			}
    		}
    		ok.add(haveNeed.remove(min-1));
    		ok.add(haveNeed.remove(min-1));
    	}
    	this.therebegain=ok.size();
    	while(!notNeed.isEmpty())//对不满足条件的进行排序
    	{
    		int minC=Inf;
    		int min=0;
    		for(int i=1;i<notNeed.size();i=i+2)
    		{
    			if(minC>notNeed.get(i))
    			{
    				min=i;
    				minC=notNeed.get(i);
    			}
    		}
    		ok.add(notNeed.remove(min-1));
    		ok.add(notNeed.remove(min-1));
    	}
    	return ok;
    	
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
    
    //按服务器节点所供给的容量，对服务器节点进行排序，返回一个数组，数组里面存储的是边的代号
    //采用归并排序
    public int[] calServerGood(int len)
    {
    	//System.out.println("SP.size()="+len+"   Sb="+Sb+"  Se="+Se);
    	int[][] record=new int[2][len];
    	int jt=0;
		for(int i=Sb+1;i<=Se;i=i+2)
		{
			record[0][jt]=eee.get(i).u;//链路的索引
			record[1][jt++]=eee.get(i).cap;//要排的数数据
		}
		int[][] temp=new int[2][len];
		this.mergeSort(record, 0, len-1, temp);
		int[] result=new int[len];
		for(int i=0;i<len;i++)
			result[i]=record[0][i];
    	return result;
    }
    

    
	public void mergeSort(int[][] a,int f,int l,int temp[][])//排序的主算法
	{
		if(f<l)
		{
			int mid=(f+l)/2;
			mergeSort(a,f,mid,temp);//左边有序
			mergeSort(a,mid+1,l,temp);//右边有序
			this.addMerge(a,f,mid,l,temp);//合并有序数组a[f..mid]和a[mid+1...l]到temp
		}
	}
	
	public void addMerge(int[][] a,int f,int mid,int l,int[][] temp)//排序用的合并算法
	{
		int i=f,j=mid+1,m=mid,n=l,k=0;
		while(i<=m && j<=n)
		{
			if(a[1][i]<=a[1][j])
			{
				temp[0][k]=a[0][i];
				temp[1][k++]=a[1][i++];
			}
			else
			{
				temp[0][k]=a[0][j];
				temp[1][k++]=a[1][j++];
			}
		}
		while(i<=m)//当前半部分多
		{
			temp[0][k]=a[0][i];
			temp[1][k++]=a[1][i++];
		}
		while(j<=n)//当后半部分多
		{
			temp[0][k]=a[0][j];
			temp[1][k++]=a[1][j++];
		}
		for(i=0;i<k;i++)//会写到a中，可以进行不写回的优化
		{
			a[0][f+i]=temp[0][i];
			a[1][f+i]=temp[1][i];
		}
	}
}



