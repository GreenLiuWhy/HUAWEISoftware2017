package realcode.ver12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class ZKW {

	private int nodeNum,edgNum,ServerMoney,CondumerNum;
	private int[] subPath;//�洢ÿ�δ�SPFA�еĵ���·��
	private int Inf=99999999;//����һ����������
	private int[] head,Dis;
	private ArrayList<Edge> edge=new ArrayList<Edge>();
	private ArrayList<int[]> CPosition=new ArrayList<int[]>();
	private int Ss,St,Tb,Ts;//����Դ���λ�ã���������λ�ã�������㽨������ʼ�ߴ��ţ�������㽨������ֹ�ߴ���
	//private ArrayList<Edge> eee;//������edge��ÿ�α仯Ʒ
	private HashMap<Integer,ArrayList> preRoud=new HashMap<Integer,ArrayList>();
	private boolean[] vis,mark;
	private int pi1,minCost;
	public static void main() {
		
		// TODO Auto-generated method stub
		System.out.println("ZKW");
	    String[] graphContent = FileUtil.read("Gao/case0.txt", null);
		ZKW zkw=new ZKW();
		zkw.getBasicInformation(graphContent);
		ArrayList<Integer> SP=zkw.getRandomPop();
		zkw.buldSuperS(SP);
		long b=System.currentTimeMillis();
		//System.out.println(zkw.theZKW()+"<-zkwֵ");
		zkw.theZKWver2();
		System.out.println(zkw.minCost);
		long e=System.currentTimeMillis();
		System.out.println("time="+(e-b));
		//putong.outputRoud();
		
		
		
	}
	
	
	//-------------------zkw ver2---------------------------------
	public void theZKWver2()
	{
		System.out.println("theZKWver2()");
		this.minCost=0;
		int temp=0;
		while(this.spfa(23))
		{
			mark[St]=true;
			while(this.mark[St])
			{
				for(int i=0;i<this.mark.length;i++)
					mark[i]=false;
				temp+=dfs(Ss,Inf);
			}
		}
	}
	
	public int dfs(int loc,int low)
	{
		System.out.println("dfs");
	    mark[loc]=true;
	    if (loc==St) return low;
	    int w,used=0;
	   // int i=this.head[hnode];i!=-1;i=edge.get(i).next
	    for (int i=this.head[loc];i!=-1;i=edge.get(i).next)
	        if (edge.get(i).cap>0 && !mark[edge.get(i).v] && Dis[edge.get(i).v]==Dis[loc]-edge.get(i).cost)
	        {
        	
                w=dfs(edge.get(i).v,min(low-used,edge.get(i).cap));
                this.minCost+=w*edge.get(i).cost;
                used+=w;
                edge.get(i).cap-=w;edge.get(i^1).cap+=w;//����ط��仯��cap�����Կ��ܻᵼ��spfa��ִ�б���
                if (used==low) return low;
	         }
	    return used;
	}
	
	public int min(int a,int b)
	{
		if(a<b)
			return a;
		else
			return b;
	}
	  public int spfa2()//�����洫��allnode��������������Dis��vis�õģ��������ڲ���Ҫ�ˣ����ң�
	    //��ʱ��ΪԴ��һֱ�ǹ̶��ģ���ȫ�ֱ������洢ΪSs��St�ˣ�����Ҳ�Ͳ���Ҫ�ٴ���ʲô��Ϣ��
	    {
		   System.out.println("spf2");
	    	Queue<Integer> qu=new LinkedList<Integer>();
	    	boolean[] vis2=new boolean[this.nodeNum+2]; 
	    	for(int i=0;i<subPath.length;i++)
	    	{
	    		Dis[i]=Inf;
	    		vis[i]=false;//û���ڶ�����
	    	}
	    	Dis[Ss]=0;
	    	qu.add(Ss);
	    	vis2[Ss]=true;
	    	while(!qu.isEmpty())
	    	{
	    		int hnode=qu.remove();
	    		vis2[hnode]=false;
	    		for(int i=this.head[hnode];i!=-1;i=edge.get(i).next)//�ҵ�������hnodeΪͷ���ı�
	    		{
	    			int tnode=edge.get(i).v;//�ҵ���hnodeΪͷ�Ļ���β�ڵ�
	    			//������������·���Թ����Ҵ���С
	    			if(edge.get(i^1).cap>0 && Dis[tnode]>Dis[hnode]-edge.get(i).cost)//edge.get(i).cap>0 && 
	    			{
	    				Dis[tnode]=Dis[hnode]+edge.get(i).cost;
	    				if(vis2[tnode]==false)//���β�ڵ㲻�ٶ�������
	    				{
	    					qu.add(tnode);
	    					vis2[tnode]=true;
	    				}
	    			}
	    		}
	    	}
	    	return Dis[St];
	    }
	  
	   public boolean spfa(int allNode)
	    {
	    	Queue<Integer> qu=new LinkedList<Integer>();
	    	boolean[] vis=new boolean[this.nodeNum+2]; 
	    	for(int i=0;i<subPath.length;i++)
	    	{
	    		subPath[i]=-1;
	    		Dis[i]=Inf;
	    		vis[i]=false;//û���ڶ�����
	    	}
	    	Dis[Ss]=0;
	    	qu.add(Ss);
	    	vis[Ss]=true;
	    	while(!qu.isEmpty())
	    	{
	    		int hnode=qu.remove();
	    		vis[hnode]=false;
	    		for(int i=this.head[hnode];i!=-1;i=edge.get(i).next)//�ҵ�������hnodeΪͷ���ı�
	    		{
	    			int tnode=edge.get(i).v;//�ҵ���hnodeΪͷ�Ļ���β�ڵ�
	    			//������������·���Թ����Ҵ���С
	    			if(edge.get(i).cap>0 && Dis[tnode]>Dis[hnode]+edge.get(i).cost)
	    			{
	    				Dis[tnode]=Dis[hnode]+edge.get(i).cost;
	    				subPath[tnode]=i;//��¼·��
	    				if(vis[tnode]==false)//���β�ڵ㲻�ٶ�������
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
//----------------------zkw ver1 ---------------------------------
	public int theZKW()
	{
		minCost=0;
		pi1=0;
		//���Ƚ���һ��spfa�����������һ���ظ�Ȩ����
		System.out.println("spfa��ʼ");
		this.spfa();
		System.out.println("spfa����");
		for(int node=0;node<this.nodeNum+2;node++)
		{
			for(int j=this.head[node];j!=-1;j=edge.get(j).next)
			{
				edge.get(j).cost+=(Dis[j]-Dis[node]);
			}
		}
		System.out.println("the ZKW---spfaִ�����");
		
		while(modlabel())
		{
			while(this.aug(Ss, Inf)!=0);
		}
		//while(this.aug(Ss, Inf)!=0);
		//while(modlabel());
		return minCost;
		
	}
	
	public boolean modlabel()
	{
		System.out.println("modlabel");
		int d=Inf;
		for(int node=0;node<this.nodeNum+2;node++)
		{
			if(vis[node])
			{
				for(int j=this.head[node];j!=-1;j=edge.get(j).next)
				{
					Edge e=edge.get(j);
					if(e.cap>0 && !vis[e.v] && e.cost<d)
						d=e.cost;
				}
			}
		}
		if(d>=Inf)
			return false;
		for(int node=0;node<this.nodeNum+2;node++)
		{
			if(vis[node])
			{
				for(int j=this.head[node];j!=-1;j=edge.get(j).next)		
				{
					edge.get(j).cost-=d;
					edge.get(j^1).cost+=d;
				}
			}
		}
		pi1+=d;
		return true;
	}
	
	public int aug(int no,int m)
	{
		System.out.println("aug");
		if(no==St)
		{
			minCost+=pi1*m;
			return m;
		}
		vis[no]=true;
		int l=m;
		for(int i=this.head[no];i!=-1;i=edge.get(i).next)
		{
			Edge e=edge.get(i);
			if(e.cap>0 && e.cost==0 && vis[e.v])
			{
				int td;
				if(l<e.cap)
					td=l;
				else
					td=e.cap;
				int d=aug(e.v,td);
				edge.get(i).cap-=d;
				edge.get(i^1).cap+=d;
				l-=d;
				if(l==0)
					return m;
			}
		}
		return m-l;
	}
	
	   public void spfa()//�����洫��allnode��������������Dis��vis�õģ��������ڲ���Ҫ�ˣ����ң�
	    //��ʱ��ΪԴ��һֱ�ǹ̶��ģ���ȫ�ֱ������洢ΪSs��St�ˣ�����Ҳ�Ͳ���Ҫ�ٴ���ʲô��Ϣ��
	    {
	    	Queue<Integer> qu=new LinkedList<Integer>();
	    	boolean[] vis2=new boolean[this.nodeNum+2]; 
	    	for(int i=0;i<subPath.length;i++)
	    	{
	    		Dis[i]=Inf;
	    		vis[i]=false;//û���ڶ�����
	    	}
	    	Dis[Ss]=0;
	    	qu.add(Ss);
	    	vis2[Ss]=true;
	    	while(!qu.isEmpty())
	    	{
	    		int hnode=qu.remove();
	    		vis2[hnode]=false;
	    		for(int i=this.head[hnode];i!=-1;i=edge.get(i).next)//�ҵ�������hnodeΪͷ���ı�
	    		{
	    			int tnode=edge.get(i).v;//�ҵ���hnodeΪͷ�Ļ���β�ڵ�
	    			//������������·���Թ����Ҵ���С
	    			if(Dis[tnode]>Dis[hnode]+edge.get(i).cost)//edge.get(i).cap>0 && 
	    			{
	    				Dis[tnode]=Dis[hnode]+edge.get(i).cost;
	    				if(vis2[tnode]==false)//���β�ڵ㲻�ٶ�������
	    				{
	    					qu.add(tnode);
	    					vis2[tnode]=true;
	    				}
	    			}
	    		}
	    	}	
	    }
	    
	  //���Բ��裬ͨ��������������õ�·����Ϣ
    public void getBasicInformation(String[] g)
    {
    	int layer=0;//��ʾ�ǵڼ������У�ͬʱҲ������֪�Ǻ���˳��,�������Ľ��Ӧ���ǶԵ�
    	for(int i=0;i<g.length;i++)
    	{
    		if(g[i].equals("") || g[i]==null)//�п���
    			layer++;
    		int[] CN=this.changeCharToNum(g[i].toCharArray());//�����е��ı�����ת��Ϊ��������,CNת����Ҳ�ǶԵ�
    		if(layer==0)//��һ����Ϣ,��һ�е������ǶԵ�
    		{
    			this.nodeNum=CN[0];
    			this.edgNum=CN[1];
    			this.CondumerNum=CN[2];
    			this.initNet();//��ʼ������
    			continue;
    		}
    		if(layer==1 && !g[i].equals(""))//����������ϸ��
    		{
    			this.ServerMoney=CN[0];//System.out.println(this.ServerMoney+"");��ȷ
    			continue;
    		}
    		if(layer==2 && !g[i].equals(""))//������ ����� ������� ����,���ڹ���edges
    		{
    			this.addedges(CN[0], CN[1], CN[2], CN[3]);
    			this.addedges(CN[1], CN[0], CN[2], CN[3]);
    			continue;
    		}
    		if(layer==3 && !g[i].equals(""))//���ѽڵ��� ���ѽڵ������ڵ� ��Ҫ����С����,���ڹ����������
    		{
    			this.CPosition.add(new int[]{CN[0],CN[1],CN[2]});//�����ʼ����������
    		}
    	}
    	//һЩ���������ǰ��׼������
    	this.Ss=this.nodeNum;
    	this.St=this.nodeNum+1;
    	this.buildSuperT();
    }
    
    ////////////////////////������Ҫ������Ҫ���õ�һЩС����////////////\
    
    //������ɺ���size�����ѽڵ�ĳ�����Ⱥ
    public ArrayList<Integer> getRandomPop()
    {
    	ArrayList<Integer> SP=new ArrayList<Integer>();
    	Random r=new Random();
    	while(SP.size()<this.CondumerNum)
    	{
    		int ra=r.nextInt(this.nodeNum);//�������һ������Ľڵ�
    		if(SP.indexOf(ra)==-1)//���ԭ����������
    			SP.add(ra);
    	}
    	return SP;
    }
    
    //�����ո��������ǧλ����ת��
    public int[] changeCharToNum(char[] cc)
    {
    	int cishu=0,len,zi=0;
    	int[] Num=new int[4];
    	for(;zi<cc.length;zi++)
    	{
    		len=0;
    		for(;zi<cc.length && cc[zi]!=' ';zi++)//������������ж���λ
    			len++;
    		//����len�õ����֣����������ִ�������
    		int sum=0;
    		for(int i=0;i<len;i++)//��ÿ��charת��Ϊ���֣���qui��
    			sum+=((int)(cc[zi-i-1]-48))*((int)Math.pow(10, i));
    		Num[cishu]=sum;
    		cishu++;
    	}
    	return Num;
    }
    //��������ĳ�ʼ���������
    public void initNet()
    {
    	this.head=new int[this.nodeNum+2];//��Ϊ�϶�����һ������Դ�㣬һ���������
    	this.subPath=new int[this.nodeNum+2];
    	this.Dis=new int[this.nodeNum+2];
    	this.vis=new boolean[this.nodeNum+2];
    	this.mark=new boolean[this.nodeNum+2];
    	for(int i=0;i<head.length;i++)
    	{
    		head[i]=-1;
    		subPath[i]=-1;
    	}
    }
    
    
    //�����������
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
    //��������Դ��
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
    
    //��������ʱ�ļӱ߲���
    public void addedges(int u,int v,int cap,int cost)
    {
    	Edge e=new Edge(u,v,cap,cost,head[u]);//�����
    	this.edge.add(e);
    	head[u]=(edge.size()-1);//��uΪͷ���Ļ����ڵıߵ�λ��
    	Edge ee=new Edge(v,u,0,-cost,head[v]);//�����
    	this.edge.add(ee);
    	head[v]=(edge.size()-1);//��vΪͷ���Ļ����ڵıߵ�λ��
    }
    
    //��preRoad��ӡ����
    public void outputRoud()
    //public HashMap getRealRoud()
    {
    	System.out.println("preRoad.size()="+preRoud.size());
    	for(int i=0;i<10;i++)
    		System.out.println(preRoud.get(i).toString());
    }

	

}

