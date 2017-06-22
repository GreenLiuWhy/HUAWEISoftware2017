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
	private int[] subPath;//�洢ÿ�δ�SPFA�еĵ���·��
	private int Inf=99999999;//����һ����������
	private int[] head,Dis;
	private ArrayList<Edge> edge=new ArrayList<Edge>();
	private ArrayList<int[]> CPosition=new ArrayList<int[]>();
	private int Ss,St,Tb,Ts;//����Դ���λ�ã���������λ�ã�������㽨������ʼ�ߴ��ţ�������㽨������ֹ�ߴ���
	//private ArrayList<Edge> eee;//������edge��ÿ�α仯Ʒ
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
    
    public int smallCostBigFunction()
    {
    	int minflow,minCost=0,len=0;
    	//minflow��һ��spfa()�������һ�����·����������ͨ�����������
    	//minCost��Դ�㵽�����ܻ��ѣ�������·���ģ�
    	//len�Ǽ�¼���Ƕ��ٴ�·����Ҳ����spfaִ���˶��ٴ�
    	//һ��spfa()���һ����ʱ�����·��
    	 
    	while(spfa())//���n���費ҪҪ��2
    	{
    		ArrayList<Integer> subpath=new ArrayList<Integer>();//������¼�˴�spfa�ҵ�·�� 
    		minflow=Inf+1;
    		//����·����ͨ�������������Ҳ����������·�а���·������Сcap
    		for(int i=subPath[St];i!=-1;i=subPath[edge.get(i).u])
    		{
    			subpath.add(i);//��������Դ��·����Ҳ�����������·��,�����������
    			if(edge.get(i).cap<minflow)
    				minflow=edge.get(i).cap;
    		}
    		subpath.add(minflow);//����·��ʱ��������Ϣ�ŵ����һλ
    		preRoud.put(len++, subpath);//��¼·��,��ʱ���¼����  subpath���������Ϣ  �� ����·�ϵ�����
    		for(int i=subPath[St];i!=-1;i=subPath[edge.get(i).u])
    		{
    			edge.get(i).cap-=minflow;
    			edge.get(i^1).cap+=minflow;//����������Ӧ�˸ľ����ߵ�����Ӧ�ķ���
    		}
    		minCost+=Dis[St]*minflow;//Dis���ѽ�������Cost����Ϣ�����Բ��������ٿ���
    		
    	}
    	return minCost;
    }
    
    public boolean spfa()//�����洫��allnode��������������Dis��vis�õģ��������ڲ���Ҫ�ˣ����ң�
    //��ʱ��ΪԴ��һֱ�ǹ̶��ģ���ȫ�ֱ������洢ΪSs��St�ˣ�����Ҳ�Ͳ���Ҫ�ٴ���ʲô��Ϣ��
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
