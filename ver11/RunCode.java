package realcode.ver11;

public class RunCode {

	String[] arr;
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		
		//RunCode r=new RunCode("zhong",6);//测试中级
		RunCode r=new RunCode("gao",7);//测试高级
		Main.main(r.arr);

	}
	
	public RunCode(String dex,int num)
	{
		//自动测试初中高级例子
		arr=new String[2];
		//测试初级例子
		if(dex.equals("chu"))
		{
			arr[0]="Chu/case"+num+".txt";
			arr[1]="Chu/result"+num+"1.txt";
		}
		//测试中级样例
		if(dex.equals("zhong"))
		{
			arr[0]="Zhong/case"+num+".txt";
			arr[1]="Zhong/result"+num+"1.txt";
		}
		//测试高级样例
		if(dex.equals("gao"))
		{
			arr[0]="Gao/case"+num+".txt";
			arr[1]="Gao/result"+num+"1.txt";
		}

	}

}
