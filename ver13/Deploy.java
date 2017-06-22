package realcode.ver13;
//package realcode.ver2;
/*
 * 绠�崟鏀硅繘锛岃皟鍙�
 * */
import java.util.*;

public class Deploy
{
	private int nodeNum,edgNum,ServerMoney,CondumerNum;
	//private int[] subPath;//瀛樺偍姣忔浠嶴PFA涓殑鍒扮殑璺緞
	private int Inf=99999999;//瀹氫箟涓�釜鏃犵┓澶х殑鏁�
	private int[] head,thead,Dis,ff,nodeh;
	private ArrayList<Edge> edge=new ArrayList<Edge>();
	private ArrayList<int[]> CPosition=new ArrayList<int[]>();
	private int Ss,St,Tb,Ts;//瓒呯骇婧愮偣鐨勪綅缃紝瓒呯骇姹囩偣鐨勪綅缃紝瓒呯骇姹囩偣寤虹珛鐨勮捣濮嬭竟浠ｅ彿锛岃秴绾ф眹鐐瑰缓绔嬬殑缁堟杈逛唬鍙�
	private ArrayList<Edge> eee;//鐢ㄤ簬鍋歟dge鐨勬瘡娆″彉鍖栧搧
	private HashMap<Integer,ArrayList> Adj=new HashMap<Integer,ArrayList>();
	private HashMap<Integer,ArrayList> preRoud=new HashMap<Integer,ArrayList>();
	private static boolean stop=false;
	private static Timer timer=new Timer();//瑙﹀姩瀹氭椂鍣紝濡傛灉鍒�5s鍒欏皢stop鏀规垚true鐢ㄦ潵缁堟鏈�紭鍖栧惊鐜�
	private ArrayList<Integer> minSP;
	private HashMap<Integer,Integer> CPosi=new HashMap<Integer,Integer>();//涓轰簡鏈�悗杞寲姝ｇ‘璺緞鐨勬椂鍊欒繀閫�
	private int Sb,Se;//瓒呯骇婧愮偣寤虹珛鐨勮捣濮嬭竟浠ｅ彿锛岃秴绾ф簮鐐瑰缓绔嬬殑缁堟杈逛唬鍙�
	private int minNode,deteNode,minNode2,detIter;//璁板綍鍒犻櫎鐨勬椂鍊欐彁渚涙渶灏忔祦閲忕殑鑺傜偣锛屽拰鏈�粓鍒犻櫎鐨勮妭鐐�
   
	public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
        Deploy d=new Deploy(); 
        timer.schedule(new MyTask(), 89000);//89200
        d.getBasicInformation(graphContent); 
        return d.changeForm(d.getRealRoud(d.dofire(d.getIntaSeverPosition())));
    }
    //瀹氭椂鍣�
    static class MyTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			stop=true;
			timer.cancel();
		}
    	
    }
    
    //鍒濊瘯姝ラ锛岄�杩囦紶杩涙潵鐨勬暟缁勫緱鍒拌矾寰勪俊鎭�
    public void getBasicInformation(String[] g)
    {
    	int layer=0;//琛ㄧず鏄鍑犱釜绌鸿锛屽悓鏃朵篃鍙互鎺ㄧ煡鏄綍绉嶉『搴�璇诲嚭鏉ョ殑缁撴灉搴旇鏄鐨�
    	for(int i=0;i<g.length;i++)
    	{
    		if(g[i].equals("") || g[i]==null)//鏈夌┖琛�
    			layer++;
    		int[] CN=this.changeCharToNum(g[i].toCharArray());//灏嗗垰琛岀殑鏂囨湰鏁版嵁杞寲涓烘暟缁勬暟缁�CN杞寲鐨勪篃鏄鐨�
    		if(layer==0)//绗竴琛屼俊鎭�绗竴琛岀殑鏁版嵁鏄鐨�
    		{
    			this.nodeNum=CN[0];
    			this.edgNum=CN[1];
    			this.CondumerNum=CN[2];
    			this.initNet();//鍒濆鍖栫綉缁�
    			continue;
    		}
    		if(layer==1 && !g[i].equals(""))//鏈嶅姟鍣ㄩ儴缃茬粏蹇�
    		{
    			this.ServerMoney=CN[0];//System.out.println(this.ServerMoney+"");姝ｇ‘
    			continue;
    		}
    		if(layer==2 && !g[i].equals(""))//鍑哄彂鐐�鍒拌揪鐐�鏈�ぇ瀹归噺 浠ｄ环,鐢ㄤ簬鏋勫缓edges
    		{
    			this.addedges(CN[0], CN[1], CN[2], CN[3]);
    			this.addedges(CN[1], CN[0], CN[2], CN[3]);
    			this.Adj.get(CN[0]).add(CN[1]);//鍔犲叆鍒伴偦鎺ヨ〃,杩欐槸鍥犱负璇ョ畻娉曚笉鑰冭檻绌洪棿澶嶆潅鎬ф墍浠ユ墠閭ｄ箞鍋氱殑
    			this.Adj.get(CN[1]).add(CN[0]);//濡傛灉绠楁硶杩樿�铏戠┖闂村鏉傛�锛岄偅涔堝彲浠ョ敤NodewithEdges鍜宔dge鐨勫叧绯绘潵鍋�
        		//---------灏嗚矾寰刢ost淇℃伅閭绘帴鐭╅樀---------//
    	    	//this.mMatrix[CN[0]][CN[1]]=this.mMatrix[CN[1]][CN[0]]=CN[3];
    			//---------鍒粰鏅撶幃鏀规瘉浜�---------------//
    			continue;
    		}
    		if(layer==3 && !g[i].equals(""))//娑堣垂鑺傜偣缂栧彿 娑堣垂鑺傜偣鐩歌繛鑺傜偣 闇�鐨勬渶灏忎唬浠�鐢ㄤ簬鏋勫缓瓒呯骇姹囩偣
    		{
    			this.CPosition.add(new int[]{CN[0],CN[1],CN[2]});//杩欎釜鍒濆鍖栨柟娉曚笉閿�
    			this.CPosi.put(CN[1], CN[0]);
    		}
    	}
    	//涓�簺鍩轰簬涓婇潰鐨勫墠鏈熷噯澶囧伐浣�
    	this.Ss=this.nodeNum;
    	this.St=this.nodeNum+1;
    	this.buildSuperT();
    }
    
    //绗竴姝ラ�鎷╁垵璇曟湇鍔″櫒鎽嗘斁浣嶇疆,闇�璺緞淇℃伅(搴﹀拰閾炬帴淇℃伅)
    public ArrayList getIntaSeverPosition()
    {//鍏堝疄鐜扮畝鍗曠殑锛屼篃灏辨槸鍦ㄦ秷璐硅妭鐐圭浉杩炵殑鑺傜偣涓婃斁涓�釜
    	ArrayList<Integer> SP=new ArrayList<Integer>();//鐢ㄤ簬鏍囩ず鍝簺浣嶇疆鐢ㄦ潵鎽嗘斁鏈嶅姟鍣�   	
    	
    	for(int i=0;i<this.CondumerNum;i++)
    		//SP.add(this.cost_dis[i]);
    		SP.add(this.CPosition.get(i)[1]);
    	return SP;
    }
  
    //绗簩姝ュ紑濮嬫敼杩涚殑妯℃嫙閫�伀,浼犲鏈嶅姟鍣ㄧ殑鍒濊瘯鐨勮矾寰勪俊鎭�
   public HashMap dofire(ArrayList SP1)
    //public void dofire(ArrayList SP1)
    {
    	//瑕佺敤鐨勫彉閲忎粈涔堢殑涓�釜澹版槑
    	Random rand=new Random();//鐢ㄤ簬鐢熸垚闅忔満鏁�
    	int T0=3000,opiter=50000000;//妯℃嫙閫�伀鐨勫垵璇曟俯搴�妯℃嫙閫�伀澶栧惊鐜殑杩唬娆℃暟
    	double T;//妯℃嫙閫�伀鐨勫綋鍓嶆俯搴�
    	boolean preNeed,nowNeed=true;//璁板綍杩囧幓鍜岀幇鍦ㄧ殑鏂规涓紝鎵�湁娑堣垂鑺傜偣鐨勯渶姹傛槸鍚﹀彲浠ュ緱鍒版弧瓒�
    	int preCost,nowCost=Inf,minCost=Inf;//璁板綍杩囧幓鍜岀幇鍦ㄦ柟妗堢殑鑺辫垂锛屾渶灏忚姳璐�
    	HashMap Road=new HashMap();//鐩墠璁板綍鐨勬槸edge鐨勬爣绀�
    	eee=this.deepCloneBaseFor(edge);//鍥犱负姣忔鐢ㄧ殑閮芥槸涓嶅悓鐨勶紝杩欐牱灏变笉鐢ㄦ瘡娆￠兘寤轰簡
    	thead=(int[])head.clone();

    	this.buldSuperS(SP1);//鏍规嵁浼犲叆鐨勬湇鍔″櫒浣嶇疆SP1锛屾瀯寤鸿秴绾ф簮鐐�
    	preCost=this.makeable()+SP1.size()*this.ServerMoney;//寰楀埌绗垵璇曟湇鍔″櫒鏀剧疆鐨勬渶澶ф祦鎯呭喌涓嬬殑鑺辫垂
    	preNeed=this.checkCNeed();
    	if(preNeed)//濡傛灉鑳芥弧瓒虫潯浠讹紝鍒欏厛灏嗚矾寰勮褰曚笅鏉�
    	{
    		minCost=preCost;
    		Road=this.HashMapdeepclone(this.preRoud);//	
    		//Road=t;//
    		minSP=(ArrayList)SP1.clone();
    	}	
    	//閽堝浠ュ悗鐨勬暟鎹�
    	ArrayList preSP=SP1,nowSP;//鍘熸潵鍜岀幇鍦ㄦ湇鍔″櫒鐨勬憜鏀句綅缃�
    	System.out.println("dofire 开始了");
    	this.detIter=0;
    	int[] order=null;
    	if(this.nodeNum>700)
    		order=this.calServerGood(SP1.size());
    	int iter=0;
    	for(;!stop && iter<opiter;iter++)
    	{
    		//T=T0/(1+iter);//褰撳墠娓╁害
    		T=T0*(Math.pow(0.5,iter));//褰撳墠娓╁害
    		//3涓敼鍙樼瓥鐣ワ紙澧炲姞鏈嶅姟鍣紝鍑忓皯鏈嶅姟鍣紝鏀瑰彉鏈嶅姟鍣ㄤ綅缃級
    		nowSP=this.changeServerMethod(preSP,preNeed,iter,order);//鍥犱负瑕佺煡閬撲笂涓�鏄皝鎻愪緵鐨勬祦閲忔渶灏戯紝鎵�互鎶婅繖涓�潯鏀惧湪浜嗛噸寤虹綉缁滅殑鍓嶉潰
    		//System.out.println("dofire 鎴愬姛杩涜浜嗕竴娆�changeServerMethd   "+iter);//涔熷氨鏄棶棰樺嚭鐜板湪娌℃湁琚浛鎹㈢殑鏃跺�
    		//閿欒鍑虹幇鍦紝濡傛灉鍑忓皯鏈嶅姟鍣ㄦ搷浣滄病鏈夋垚鍔燂紝閭ｄ箞eee涔熷彂鐢熶簡鍙樻崲锛屽洜涓烘垜鍒犻櫎鐨勬椂鍊欏凡缁忕敤鍒颁簡eee姝ゆ椂锛宔ee鑲畾涓嶅悓浜嗭紝瑙ｅ喅鏂规鏄湪record閲岄潰瀛樼殑涓嶆槸閾捐矾绱㈠紩锛岃�鏄鍒犻櫎鐨勮妭鐐瑰彿
    		eee=this.deepCloneBaseFor(edge);//閲嶆柊鎭㈠鍒拌繕娌℃湁纭畾鏈嶅姟鍣ㄧ殑鐘舵�
    		thead=(int[])head.clone();
    			
    		this.buldSuperS(nowSP);//鏋勫缓瓒呯骇姹囩偣
    		nowCost=this.makeable()+nowSP.size()*this.ServerMoney;
    		nowNeed=this.checkCNeed();//妫�煡鏄惁婊¤冻浜嗘墍鏈夋湇鍔″櫒鐨勮姹�
    		//System.out.println(nowNeed+"    "+nowCost+"   "+minCost);
    		if((preNeed&&nowNeed&&preCost>nowCost)||(!preNeed&&nowNeed))//涓�畾鍙戠敓鏇挎崲锛堬級
    			//濡傛灉鐜板湪鐨勬柟妗堟弧瓒虫墍鏈夋秷璐硅妭鐐圭殑闇�眰锛屼笖鐜板湪鏂规鐨刵owCost灏忥紝鍒欎竴瀹氭浛鎹�
    		{
    			preCost=nowCost; preSP=nowSP;preNeed=nowNeed;//鏇挎崲
    			if(nowCost<minCost)//濡傛灉鏄洰鍓嶆渶浼樼殑锛屽垯璁板綍涓嬫潵
    			{
    				//System.out.println("dofire 鏇挎崲鐨勮矾寰勶細"+nowSP+"  nowNeed="+nowNeed+"  nowCost="+nowCost);
    				System.out.println("第"+iter+"次迭代替换为：  "+nowCost);
    				minCost=nowCost;
    				Road=this.HashMapdeepclone(preRoud);//璁板綍涓媙owNeed鐨勮矾寰�
    				minSP=(ArrayList)nowSP.clone();
    			} 
    			if(this.detIter<=4 && this.nodeNum>700)
					order=this.calServerGood(nowSP.size());//寰楀埌鎺掑簭鍚庣殑鏁扮粍锛宱rder閲岄潰瀛樺偍鐨勬槸杈圭殑浠ｅ彿
				else
					order=null;
    			continue;
    		}else{
    			if(preNeed&&!nowNeed)//涓�畾涓嶅彂鐢熸浛鎹�
    			{
    				continue;//涓嶆浛鎹�
    			}else{
    				//鎸夌収涓�畾鐨勬鐜囪繘琛屾浛鎹�
    				int erro=nowCost-preCost;
    				double r=rand.nextDouble();//闅忔満鐢熸垚涓�釜鍙岀簿搴﹀瀷
    				double p =Math.exp(-(nowCost-preCost)/T);
    				//if(r<Math.exp(-(nowCost-preCost)/T))//濡傛灉鍦ㄨ繖涓�寖鍥村唴鍒欐浛鎹�
    				if(r<p)//濡傛灉鍦ㄨ繖涓�寖鍥村唴鍒欐浛鎹�
    				{
    					preCost=nowCost; preSP=nowSP;preNeed=nowNeed;//鏇挎崲
    					if(this.detIter<=4 && this.nodeNum>700)
        					order=this.calServerGood(nowSP.size());//寰楀埌鎺掑簭鍚庣殑鏁扮粍锛宱rder閲岄潰瀛樺偍鐨勬槸杈圭殑浠ｅ彿
        				else
        					order=null;
    					continue;
    				}else
    				{
    					continue;//涓嶆浛鎹�
    				}
    			}
    		}
    	}
    	//System.out.println("iter="+iter+"  minCost="+minCost+"  stop="+stop);
    	
    	return Road;
    	
    }
    
    //寰楀埌鐪熸鐨凴oad锛屽嵆鎶婅礋杈圭殑缁欏幓鎺�
    public HashMap getRealRoud(HashMap old)
    //public HashMap getRealRoud()
    {
    	if(old.isEmpty())
    		return null;
    	
    	eee=this.deepCloneBaseFor(edge);//鍥犱负姣忔鐢ㄧ殑閮芥槸涓嶅悓鐨勶紝杩欐牱灏变笉鐢ㄦ瘡娆￠兘寤轰簡
    	this.buldSuperS(minSP);
    	
    	//System.out.println("getRealRoud  oldRoud:"+old.size());
    	
    	int len=old.size();
    	HashMap<Integer,Integer> allE=new HashMap<Integer,Integer>();//鐢ㄤ簬鏋勫缓Hash琛�
    	HashMap<Integer,ArrayList> realRoud=new HashMap<Integer,ArrayList>();//鐪熸璺緞
    	for(int i=0;i<len;i++)//杩欎釜寰幆寰楀埌allE锛屼篃灏辨槸璁板綍姣忔潯璺緞搴旇璧板灏戞祦閲�
    	{//杩欎釜鍦版柟涓嶅簲璇ユ槸 i<old.size 鍥犱负 old鏈塺emove鎿嶄綔锛屼細瀵艰嚧瀹冪殑闀垮害瓒婃潵瓒婂皬锛屽鑷村惊鐜繃鏃╃粨鏉�
    		ArrayList<Integer> sub=(ArrayList<Integer>)old.get(i);//寰楀埌绗琲鏉¤矾寰�
    		for(int j=0;j<sub.size()-1;j++)//灏嗘墍鏈夌殑杈规斁鍦℉ash琛ㄤ腑锛岃绠楁瘡鏉¤竟瀹為檯璧颁簡澶氬皯娴侀噺锛屼篃灏辨槸鍘婚櫎璐熻竟鐨勬儏鍐典笅涓�叡鏈夊灏戞祦閲�
    		{//娉ㄦ剰锛宻ub鐨勬渶鍚庝竴浣嶆槸鐩墠璇ユ潯绾胯矾鐨勬祦閲�闄ゆ渶鍚庝竴浣嶇殑鍓嶅嚑浣嶏紝搴旇鏄妭鐐圭殑绱㈠紩
    			//鏈潵杩欎釜鍦版柟閿欎簡锛屽湪allE涓紝搴旇鏄笉瀛樺偍璐熻竟鐨�
    			int idex=sub.get(j);
    			if(idex%2!=0)
    				idex=idex^1;
    			if(allE.get(idex)==null)//濡傛灉鐩墠杩樻病鏈夊瓨鍏�鍒欏垵濮嬪寲涓�
    				allE.put(idex, 0);
    			int flow=allE.get(idex);
    			if((int)sub.get(j)%2==0)//(涓嶆槸鎴戜滑鏋勫缓Ss鎴朣t鍔犵殑杈�&& 杈圭殑鏍囩ず鏄伓鏁�
    				flow=flow+(int)sub.get(sub.size()-1);
    			else//濡傛灉鏄礋杈�
    				flow=flow-(int)sub.get(sub.size()-1); 	
    			allE.remove(idex);//灏哸llE涓殑flow鏇存柊鎴愭渶鏂扮殑
    			allE.put(idex, flow);			
    		}
    	}
    	//浠巃llE鐨勬暟鎹腑寰楀埌璺緞-->鏋勫缓HashMap-->鏍规嵁HashMap鎵捐矾寰�
    	//鏋勫缓HashMap
    	HashMap<Integer,LinkedList> find=new HashMap<Integer,LinkedList>();
    	Set s=allE.keySet();
		Iterator it=s.iterator();
		for(;it.hasNext();)
		{
			int edex=(int)it.next();
			int u=this.eee.get(edex).u;//澶寸粨鐐�
			int v=this.eee.get(edex).v;//灏捐妭鐐�
			if(find.get(u)==null)//濡傛灉鏆傛椂鏃犳澶寸粨鐐�鍒欒瀹冩湁瀛╁瓙
			{
				LinkedList<int[]> son=new LinkedList<int[]>();
				find.put(u, son);
			}
			LinkedList<int[]> temp=find.get(u);//鎵惧埌澶寸粨鐐箄瀵瑰簲鐨勫熬鑺傜偣琛�
			if(allE.get(edex)!=0)//鍥犱负鏈変釜118鍒�0鐨勮矾寰勪笉鐭ラ亾鏄粈涔堥
				temp.add(new int[]{u,v,allE.get(edex)});//鍦ㄥ熬鑺傜偣閲屽垎鍒瓨鍏澶寸粨鐐癸紝灏捐妭鐐癸紝allFlow}
			else//鍥犱负鍑虹幇鏈�118 浣嗘槸 size=0 杩欏氨璇存槑118鍔犱簡涓┖鐨勶紝閭ｄ箞搴旇灏辨槸杩欓噷鍒犲幓锛岃繖鏄洜涓鸿蛋姝ｈ蛋璐燂紝瀵艰嚧鏈変簺杈瑰叾瀹炴病鏈夎蛋
			{//涓�洿璁や负鏄悗闈㈤敊浜嗭紝鎯充笉鍒般�銆傘�銆�
				if(find.get(u).isEmpty())
					find.remove(u);
			}
		}
		//鏍规嵁HashMap鎵捐矾寰�
		int reallen=0;//鍋歳ealRoud鐨刱ey
		while(!find.isEmpty())//褰揻ind琛ㄤ笉涓虹┖鏃�
		{
			ArrayList<Integer> path=new ArrayList<Integer>();
			int hh=Ss;
			int min=Inf;
			int[] shiyan=new int[3];
			while(hh!=St)//褰撲笉鏄秴绾ф眹鐐圭殑鏃跺�
			{
				int mq=((LinkedList<int[]>)find.get(hh)).getFirst()[2];
				if(min>mq)
					min=mq;	
				shiyan=((LinkedList<int[]>)find.get(hh)).getFirst();
				hh=shiyan[1];				
				path.add(hh);
			}
			path.remove(path.size()-1);//灏嗚秴绾ф眹鐐瑰幓鎺�
			hh=Ss;//head鎴戝凡缁忓畾涔変负鍏ㄥ眬鍙橀噺浜�
			while(hh!=St)//鏍规嵁minflow锛屾洿鏀硅矾寰勪笂鐨刟llFlow锛屽鏋渁llFlow涓�鍒欏垹闄わ紝濡傛灉琚垹闄ょ殑澶寸粨鐐逛篃涓虹┖浜嗭紝鍒欎篃鍒犻櫎瀵瑰簲find涓婄殑涓�
			{				
				((LinkedList<int[]>)find.get(hh)).getFirst()[2]-=min;
				int next=((LinkedList<int[]>)find.get(hh)).getFirst()[1];			
				if(((LinkedList<int[]>)find.get(hh)).getFirst()[2]==0)
				{
					((LinkedList<int[]>)find.get(hh)).removeFirst();//濡傛灉绛変簬0锛屽垯鍒犻櫎
					if(((LinkedList<int[]>)find.get(hh)).isEmpty())
						find.remove(hh);//濡傛灉娌℃湁浜嗗熬鑺傜偣锛屽垯鍒犻櫎璇ashMap鐨刪ead杈�
				}
				hh=next;
			}
			path.add(this.CPosi.get(path.get(path.size()-1)));//鍔犲叆娑堣垂鑰呯殑缂栧彿
			path.add(min);//鍦╬ath涓鍔爉inflow
			realRoud.put(reallen++, path);//鍦╮ealRoud涓鍔爌ath				
		}
		
		//System.out.println("-----------娴嬭瘯鏁翠綋渚涚粰鍜屾暣浣撻渶姹�-------------");
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
    
    
   
    //鏈�皬鑺辫垂鏈�ぇ娴侀渶瑕佺殑spfa绠楁硶,浼犲叆鐨勯噺鏄秴绾ф簮鐐圭殑鏍囧彿锛岃秴绾ф眹鐐圭殑缂栧彿锛屽拰鎵�湁鐨勮妭鐐规暟鐩�
    public boolean yicimake()
    {
    	LinkedList<Integer> qu=new LinkedList<Integer>();//涓轰簡鏂逛究瀹炵幇锛岀洿鎺ヤ笉鐢ㄩ槦鍒楋紝鐩存帴鐢ㄩ摼琛�
    
    	boolean[] vis=new boolean[this.nodeNum+2]; 
    	for(int i=0;i<Dis.length;i++)
    	{
    		nodeh[i]=-1;
    		Dis[i]=Inf;
    		vis[i]=false;//娌℃湁鍦ㄩ槦鍒楅噷
    	}	
    	
    	qu.add(Ss);//id of itself,from,flow,cost
    	Dis[Ss]=0;
    	//this.nodeh[Ss]=0;//nodeh里面应该是个边，所以这个应该删除,其实也可以放一个-1之类的根本不存在的索引作为终止条件，但是因为有了索引了，所以就可以直接不用
    	ff[Ss]=Inf;
    	vis[Ss]=true;
    	int iterr=0;
    	boolean have=false;
    	while(!qu.isEmpty())
    	{
    		//chose the small cost remove
    		//这个地方每次执行，导致算法的时间复杂度过高
//    		int minDis=Inf;
//    		int minh=0,h=0;
//    		Iterator iter=qu.iterator();//qu里面放的事节点的标示
//    		while(iter.hasNext())
//    		{
//    			int tt=(int)iter.next();//得到节点的表示
//    			if(Dis[tt]<minDis)
//    			{
//    				minDis=Dis[tt];
//    				minh=h;
//    			}
//    			h++;
//    		}
//    		int hnode=qu.remove(minh);
//    		if(hnode==St)
//    			return true;
    		
    		iterr++;
    		if(iterr%(this.edgNum)==0)
    		{
    			Iterator iter=qu.iterator();
    			while(iter.hasNext())
    			{
    				int tt=(int)iter.next();//得到节点的表示
    				if(Dis[tt]<Dis[St])
    				{
    					have=true;
    					break;
    				}
    			}
    			
    			if(have==false)
        			return true;
    		}
    		
    		
    		
    		int hnode=qu.removeFirst();//remove the small
    		vis[hnode]=false;
    		for(int is=this.thead[hnode];is!=-1;is=eee.get(is).next)//得到以hnode为头结点的所有的边
    		{
    			Edge ee=eee.get(is);
    			int tnode=ee.v;//以hnode为头结点的对应的尾节点
    			if(ee.cap>0 && Dis[tnode]>Dis[hnode]+ee.cost)
    			{
    				Dis[tnode]=Dis[hnode]+ee.cost;
    				int fff=this.returnmin(ee.cap, this.ff[hnode]);
    		    	this.nodeh[tnode]=is;// is the idex of edges, and  the hnode is eee.get(is).u
    		    	ff[tnode]=fff;    	
    				if(vis[tnode]==false)//濡傛灉灏捐妭鐐逛笉鍐嶉槦鍒楅噷闈�
    				{	
    					if(!qu.isEmpty() && Dis[tnode]<Dis[qu.getFirst()])
    						qu.addFirst(tnode);
    					else
    						qu.add(tnode);//id of itself,from,flow,cost
    					vis[tnode]=true;
    				}
    			}
  		
    	
    		}
    	}
    	//System.out.println(Dis[St]);
    	if(Dis[St]>=Inf)
    		return false;
    	return true;	
    }
    
    public int makeable()//recode roud
    {
    	//鍓嶉潰鍏堣繘琛屼竴浜涙暟鎹殑棰勫鐞嗗伐浣�
    	//姝ｅ紡寮�鎵ц绠楁硶
    	this.preRoud.clear();//娓呴櫎涓婁竴娆″緱鍒扮殑璺緞
    	//this.smallCost=0;
    	int minflow,minCost=0,len=0;
    	 
    	while(this.yicimake())//杩欎釜n杩橀渶涓嶈瑕佸姞2
    	{
    		ArrayList<Integer> subpath=new ArrayList<Integer>(); 
    		minflow=Inf+1;
    		//姹傜嚎璺厑璁搁�杩囩殑鏈�ぇ娴侀噺锛屼篃灏辨槸杩欐潯绾胯矾涓寘鍚矾寰勭殑鏈�皬cap
    		int ii=this.nodeh[St];//ii是以St为v的边的代号
    		int tn=0;
    		for(;ii!=-1 && eee.get(ii).u!=Ss;ii=this.nodeh[tn])//根据它的头结点,其实这个ii!=-1应该没有用
    		{
    			tn=eee.get(ii).u;//找到它的头结点
    			subpath.add(ii);//存储路径，ii应该是边的代号，而不是节点
    			if(eee.get(ii).cap<minflow)
    				minflow=eee.get(ii).cap;
    		}
    		//System.out.println(minflow);//minflow=0;
    		subpath.add(ii);//也就是subpath里加入的第一个区v是St,最后一个取u时Ss！！一会可能用到这个
    		subpath.add(minflow);//将可以通过的流量加进去
    		preRoud.put(len++, subpath);//将子路径加入到总的路径里面取
    		//修改路径
    		ii=this.nodeh[St];
    		for(;ii!=-1 && eee.get(ii).u!=Ss;ii=this.nodeh[tn])//这个其实超级源点到真实源点的路径的cap并没有发生变化，但因为其本身就是无穷的，所以这样正合我意
    		{
    			tn=eee.get(ii).u;
    			eee.get(ii).cap-=minflow;
    			eee.get(ii^1).cap+=minflow;//寮傛垨鎿嶄綔锛屽搴斾簡鏀圭粡杩囪竟鐨勬墍瀵瑰簲鐨勫弽杈�
    		}
    		minCost+=Dis[St]*minflow;//Dis涓凡灏嗗寘鍚簡Cost鐨勪俊鎭紝鎵�互涓嶇敤閲嶆柊鍐嶈�铏�
    		
    	}
    	//System.out.println("makeable");
    	return minCost;
    }
    
    public int returnmin(int a,int b)
    {
    	if(a<b)
    		return a;
    	else
    		return b;
    }
    //鏈�悗涓�锛屽皢寰楀埌鐨勭粨鏋滆浆鍖栦负绗﹀悎鏉′欢鐨勫瓧绗︿覆鏁扮粍
    public String[] changeForm(HashMap Roud)
    {
    	if(Roud==null)//濡傛灉娌℃湁鎵惧埌璺緞锛屽垯杩斿洖涓�釜绌烘寚閽�
    		return null;
    	//濡傛灉鎵惧埌浜嗚矾寰�
    	//System.out.println("changeForm涓渶缁堟壘鍒扮殑璺緞鏁扮洰鏄細"+Roud.size());
    	String[] contents=new String[Roud.size()+2];//
    	contents[0]=Roud.size()+"";//绗竴琛岋紝璺緞鏁�
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
    				contents[i++]+=everyRoud.get(j)+"";//鏈�悗涓�釜鏄閲忥紝鍚庨潰搴旇娌℃湁绌烘牸
    		}
		}
    	
    	return contents;
    	
    }
    
    //鏀瑰彉鏈嶅姟鍣ㄦ斁缃殑涓夌绛栫暐
    public ArrayList changeServerMethod(ArrayList SP1,boolean need,int iter,int[] order)
    {
    	ArrayList SP=(ArrayList)SP1.clone();
    	Random rr=new Random();//鐢熸垚闅忔満閲�
    	double meths=rr.nextDouble();//纭畾浠ヤ笅涓夌鏂规硶鐨勯殢鏈洪噺
    	double addS,detS;
    	if(this.nodeNum<800)//濡傛灉涓嶆槸楂樼骇鏍蜂緥
    	{
    		if(this.nodeNum<300)
    		{
    			addS=0.18;
    			detS=0.5;
    		}else
    		{
    			addS=0.1;//澧炲姞鏈嶅姟鍣ㄧ殑姒傜巼
    			detS=0.5;
    			if(iter<=300)
    				detS=0.72;
    		}
    	}else
    	{
    		addS=0.1;//澧炲姞鏈嶅姟鍣ㄧ殑姒傜巼
    		detS=0.8;
    		if(iter>200)
    			detS=0.7;
    	}
    	//methed 1:澧炲姞鏈嶅姟鍣ㄦ暟閲�
    	if(SP1.size()<this.CondumerNum && (need==false || meths<addS))
    	{   
    		
    		while(true)//褰撴病鏈夊姞鍏ユ垚鍔�
    		{
    			int add=rr.nextInt(this.nodeNum);//鍦ㄩ櫎瓒呯骇婧愮偣鍜岃秴绾ф眹鐐圭殑缃戠粶鑺傜偣閲岄殢鏈洪�鎷╀竴涓姞鍏�
    			if(SP.indexOf(add)==-1)//璇存槑娌℃湁鍦ㄥ師鏉ョ殑鏈嶅姟鍣ㄤ腑
    			{
    				SP.add(add);
    				break;
    			}
    		}
    		return SP;
    	}
    	//method 2:鍑忓皯鏈嶅姟鍣ㄥ櫒鏁伴噺
    	if(SP.size()>1 && meths>=addS && meths<detS)
    	{
    		//System.out.println("鍑忓皯鏈嶅姟鍣ㄦ搷浣�);
    		if(this.nodeNum<700 || this.detIter>4)
    		{
    			if(this.nodeNum<200)
    			{
    				int del=rr.nextInt(SP.size());
    				SP.remove(del);
    				return SP;
    			}else
    			{
		    		int min=Inf;
		    		int minN=0;
		    		for(int i=Sb+1;i<=Se;i=i+2)
		    		{
		    			//System.out.print(eee.get(i).u+"("+eee.get(i).cap+")   ");
		    			if(eee.get(i).cap<min)
		    			{
		    				min=eee.get(i).cap;
		    				minN=eee.get(i).u;//鐪熷疄鐨勬簮鐐瑰簲璇ユ槸瓒呯骇鑺傜偣鐨勫熬鑺傜偣,浣嗘槸鍥犱负杩欐槸璐熻竟锛屾墍浠ョ湡瀹炵殑鏄簮鐐瑰氨鏄竟鐨勫ご缁撶偣
		    			}
		    		}
		    		int del;
		    		if(minN==minNode)//璇存槑鐜板湪鍑虹幇浜嗗垹闄よ鏈�皬鐨勫氨婊¤冻涓嶄簡娑堣垂鑺傜偣鐨勯渶姹傜殑鎯呭喌
		    			del=rr.nextInt(SP.size());
		    		else
		    		{
		    			this.minNode=minN;	
		    			del=SP.indexOf(this.minNode);
		    			if(del==-1)//褰撳墠涓�娌℃浛鎹㈡崲鏃讹紝浼氬嚭鐜拌繖鏍风殑鎯呭喌
		    				del=rr.nextInt(SP.size());
		    		}
		    		SP.remove(del);
		    		
		    		if(this.nodeNum>700 && iter<25)//濡傛灉鏄珮绾х敤渚嬶紝鍒欏啀鍑忎竴涓�
		    		{
		    			int del2=rr.nextInt(SP.size());
		    			SP.remove(del2);
		    		}else
		    			return SP;
    			}
    		}else
    		{//閽堝楂樼骇鐢ㄤ緥鐨勫墠鍑犳锛屽ぇ骞呭害鍑忓皯鑺傜偣鐨勬暟閲�
    			
    			int detNum=0;
    			switch(this.detIter)
    			{
    			case 0://鍒犻櫎50涓�
    				detNum=50;
    				break;
    			//case 1:
    				//detNum=40;
    			case 1://鍒犻櫎30涓�
    				detNum=30;
    				break;
    			case 2://鍒犻櫎20涓�
    				detNum=20;
    				break;
    			case 3://鍒犻櫎10涓�
    				detNum=10;
    				break;
    			//case 5:
    				//detNum=10;
    				//break;
    			default://鍒犻櫎5涓�
    				detNum=5;
    				break;
    				
    			}
    			for(int gaod=0;gaod<detNum;gaod++)
    			{
    				//int dell=eee.get(order[gaod]).u;
    				int del2=SP.indexOf(order[gaod]);
    				if(del2==-1)
    					del2=rr.nextInt(SP.size());
    				SP.remove(del2);
    			}
    			
    			this.detIter++;
    		}
    		
    		return SP;
    	}
    	//method 3:鏀瑰彉鏈嶅姟鍣ㄤ綅缃�
    	if(meths>=detS)
    	{
    		//System.out.println("鏀瑰彉鏈嶅姟鍣ㄤ綅缃�);
//    		while(true)//褰撴病鏈夊姞鍏ユ垚鍔�
//    		{
//    			int add=rr.nextInt(this.nodeNum);//鍦ㄩ櫎瓒呯骇婧愮偣鍜岃秴绾ф眹鐐圭殑缃戠粶鑺傜偣閲岄殢鏈洪�鎷╀竴涓姞鍏�
//    			if(SP.indexOf(add)==-1)//璇存槑娌℃湁鍦ㄥ師鏉ョ殑鏈嶅姟鍣ㄤ腑
//    			{
//    				SP.add(add);
//    				break;
//    			}
//    		}
    		//闅忔満澧炲姞涓�彴涓庢煇鏈嶅姟鍣ㄧ洿鎺ョ浉杩炵殑鐐�
    		while(true)//褰撴病鏈夊姞鍏ユ垚鍔�
    		{
    			int addbor=rr.nextInt(SP.size());//鍦ㄩ櫎瓒呯骇婧愮偣鍜岃秴绾ф眹鐐圭殑缃戠粶鑺傜偣閲岄殢鏈洪�鎷╀竴涓姞鍏�
    			int aa=rr.nextInt(Adj.get(SP.get(addbor)).size());
    			int add=(int)Adj.get(SP.get(addbor)).get(aa);
    			if(SP.indexOf(add)==-1)//璇存槑娌℃湁鍦ㄥ師鏉ョ殑鏈嶅姟鍣ㄤ腑
    			{
    				SP.add(add);
    				break;
    			}
    		}
    		//鍒犻櫎涓�彴鏈嶅姟鍣�
    		//闅忔満鍒犻櫎
    		int del=rr.nextInt(SP.size()-1);
    		SP.remove(del);
//    		int min=Inf;
//    		int minN=0;
//    		for(int i=Sb+1;i<=Se;i=i+2)
//    		{
//    			//System.out.print(eee.get(i).u+"("+eee.get(i).cap+")   ");
//    			if(eee.get(i).cap<min)
//    			{
//    				min=eee.get(i).cap;
//    				minN=eee.get(i).u;//鐪熷疄鐨勬簮鐐瑰簲璇ユ槸瓒呯骇鑺傜偣鐨勫熬鑺傜偣,浣嗘槸鍥犱负杩欐槸璐熻竟锛屾墍浠ョ湡瀹炵殑鏄簮鐐瑰氨鏄竟鐨勫ご缁撶偣
//    			}
//    		}
//    		int del=SP.indexOf(minN);
//    		if(del==-1)//褰撳墠涓�娌℃浛鎹㈡崲鏃讹紝浼氬嚭鐜拌繖鏍风殑鎯呭喌
//    			del=rr.nextInt(SP.size());		
//    		SP.remove(del);
    	}
    	return SP;
    }
    ////////////////////////涓婇潰涓昏鍑芥暟鎵�璋冪敤鐨勪竴浜涘皬鍑芥暟////////////\
    //灏嗗甫绌烘牸锛屼笖鏈�ぇ鏄崈浣嶇殑涔﹁浆鍨�
    public int[] changeCharToNum(char[] cc)
    {
    	int cishu=0,len,zi=0;
    	int[] Num=new int[4];
    	for(;zi<cc.length;zi++)
    	{
    		len=0;
    		for(;zi<cc.length && cc[zi]!=' ';zi++)//鐪嬬湅杩欎釜鏁板瓧鏈夊灏戜綅
    			len++;
    		//鏍规嵁len寰楀埌鏁板瓧锛屽苟灏嗚鏁板瓧瀛樺叆鏁扮粍
    		int sum=0;
    		for(int i=0;i<len;i++)//灏嗘瘡涓猚har杞寲涓烘暟瀛楋紝骞秖ui鍜�
    			sum+=((int)(cc[zi-i-1]-48))*((int)Math.pow(10, i));
    		Num[cishu]=sum;
    		cishu++;
    	}
    	return Num;
    }
    //鏋勫缓缃戠粶鏃剁殑鍔犺竟鎿嶄綔
    public void addedges(int u,int v,int cap,int cost)
    {
    	Edge e=new Edge(u,v,cap,cost,head[u]);//姝ｅ悜杈�
    	this.edge.add(e);
    	head[u]=(edge.size()-1);//浠涓哄ご缁撶偣鐨勫姬鎵�湪鐨勮竟鐨勪綅缃�
    	Edge ee=new Edge(v,u,0,-cost,head[v]);//鍙嶅悜杈�
    	this.edge.add(ee);
    	head[v]=(edge.size()-1);//浠涓哄ご缁撶偣鐨勫姬鎵�湪鐨勮竟鐨勪綅缃�
    }
    //鏋勫缓缃戠粶鏃剁殑鍔犺竟鎿嶄綔
    public void addedgesthead(int u,int v,int cap,int cost)
    {
    	Edge e=new Edge(u,v,cap,cost,thead[u]);//姝ｅ悜杈�
    	this.eee.add(e);
    	thead[u]=(eee.size()-1);//浠涓哄ご缁撶偣鐨勫姬鎵�湪鐨勮竟鐨勪綅缃�
    	Edge ee=new Edge(v,u,0,-cost,thead[v]);//鍙嶅悜杈�
    	this.eee.add(ee);
    	thead[v]=(eee.size()-1);//浠涓哄ご缁撶偣鐨勫姬鎵�湪鐨勮竟鐨勪綅缃�
    }
    //鏋勫缓缃戠粶鐨勫垵濮嬪寲缃戠粶鎿嶄綔
    public void initNet()
    {
    	this.head=new int[this.nodeNum+2];//鍥犱负鑲畾浼氭湁涓�釜瓒呯骇婧愮偣锛屼竴涓秴绾ф眹鐐�
    
    	this.Dis=new int[this.nodeNum+2];
    	this.ff=new int[this.nodeNum+2];
    	this.nodeh=new int[this.nodeNum+2];
    	for(int i=0;i<head.length;i++)
    	{
    		head[i]=-1;
    		
    	}
    	for(int j=0;j<this.nodeNum;j++)
    	{
    		ArrayList<Integer> aa=new ArrayList<Integer>();
    		this.Adj.put(j, aa);
    	}
    	/*for(int x=0;x<this.nodeNum;x++)
			for(int y=0;y<this.nodeNum;y++){
				this.mMatrix[x][y] = Inf;
			}*/
    }
    //鏋勫缓瓒呯骇姹囩偣
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
    //鏋勫缓瓒呯骇婧愮偣
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
    //鍒ゆ柇鏄惁鎵�湁娑堣垂鑺傜偣鐨勯渶姹傞兘寰楀埌浜嗘弧瓒�
    public boolean checkCNeed()
    {
    	for(int i=Tb;i<=Ts;i=i+2)//鍥犱负鍔�涔嬪悗鏄弽杈�
    	{
    		if(eee.get(i).cap>0)
    			return false;
    	}
    	return true;
    }
    
    
    //HashMap鐨勫厠闅嗘柟娉�
	public HashMap<Integer,ArrayList> HashMapdeepclone(HashMap<Integer,ArrayList> Old)
	{
		HashMap<Integer,ArrayList> New=new HashMap<Integer,ArrayList>();
		for(int i=0;i<Old.size();i++)
		{
			ArrayList<Integer> aa=new ArrayList<Integer>();
			ArrayList<Integer> ao=(ArrayList)Old.get(i);
			aa=(ArrayList)ao.clone();//涓嶈兘鎴愬姛鎷疯礉鐨勭▼搴�
			New.put(i, aa);
		}
		return New;
	}
	
    //娴嬭瘯鐢╟lone鏂规硶1锛氶噸鍐檆lone鏂规硶锛屽皢ArrayList閬嶅巻锛宑lone鍒板簳
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
    
    //鎸夋湇鍔″櫒鑺傜偣鎵�緵缁欑殑瀹归噺锛屽鏈嶅姟鍣ㄨ妭鐐硅繘琛屾帓搴忥紝杩斿洖涓�釜鏁扮粍锛屾暟缁勯噷闈㈠瓨鍌ㄧ殑鏄竟鐨勪唬鍙�
    //閲囩敤褰掑苟鎺掑簭
    public int[] calServerGood(int len)
    {
    	//System.out.println("SP.size()="+len+"   Sb="+Sb+"  Se="+Se);
    	int[][] record=new int[2][len];
    	int jt=0;
		for(int i=Sb+1;i<=Se;i=i+2)
		{
			record[0][jt]=eee.get(i).u;//閾捐矾鐨勭储寮�
			record[1][jt++]=eee.get(i).cap;//瑕佹帓鐨勬暟鏁版嵁
		}
		int[][] temp=new int[2][len];
		this.mergeSort(record, 0, len-1, temp);
		int[] result=new int[len];
		for(int i=0;i<len;i++)
			result[i]=record[0][i];
    	return result;
    }
    
	public void mergeSort(int[][] a,int f,int l,int temp[][])//鎺掑簭鐨勪富绠楁硶
	{
		if(f<l)
		{
			int mid=(f+l)/2;
			mergeSort(a,f,mid,temp);//宸﹁竟鏈夊簭
			mergeSort(a,mid+1,l,temp);//鍙宠竟鏈夊簭
			this.addMerge(a,f,mid,l,temp);//鍚堝苟鏈夊簭鏁扮粍a[f..mid]鍜宎[mid+1...l]鍒皌emp
		}
	}
	 public int[] calServerGood1(int[] cost)
	    {
	    	int len=cost.length;
	    	int[][] record=new int[2][len];
	    	int jt=0;
			for(int i=0;i<len;i++)
			{
				record[0][jt]=i;//閾捐矾鐨勭储寮�
				record[1][jt++]=cost[i];//瑕佹帓鐨勬暟鏁版嵁
			}
			int[][] temp=new int[2][len];
			this.mergeSort(record, 0, len-1, temp);
			int[] result=new int[len];
			for(int i=0;i<len;i++)
				result[i]=record[0][i];
	    	return result;
	    }
	
	public void addMerge(int[][] a,int f,int mid,int l,int[][] temp)//鎺掑簭鐢ㄧ殑鍚堝苟绠楁硶
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
		while(i<=m)//褰撳墠鍗婇儴鍒嗗
		{
			temp[0][k]=a[0][i];
			temp[1][k++]=a[1][i++];
		}
		while(j<=n)//褰撳悗鍗婇儴鍒嗗
		{
			temp[0][k]=a[0][j];
			temp[1][k++]=a[1][j++];
		}
		for(i=0;i<k;i++)//浼氬啓鍒癮涓紝鍙互杩涜涓嶅啓鍥炵殑浼樺寲
		{
			a[0][f+i]=temp[0][i];
			a[1][f+i]=temp[1][i];
		}
	}
}



