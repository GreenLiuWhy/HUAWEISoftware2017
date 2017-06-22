package realcode.ver13;

public class RunCode {

	String[] arr;
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		
		//RunCode r=new RunCode("chu",3);//�����м�
		RunCode r=new RunCode("gao",4);//���Ը߼�
		Main.main(r.arr);

	}
	
	public RunCode(String dex,int num)
	{
		//�Զ����Գ��и߼�����
		arr=new String[2];
		//���Գ�������
		if(dex.equals("chu"))
		{
			arr[0]="Chu/case"+num+".txt";
			arr[1]="Chu/result"+num+"1.txt";
		}
		//�����м�����
		if(dex.equals("zhong"))
		{
			arr[0]="Zhong/case"+num+".txt";
			arr[1]="Zhong/result"+num+"1.txt";
		}
		//���Ը߼�����
		if(dex.equals("gao"))
		{
			arr[0]="Gao/case"+num+".txt";
			arr[1]="Gao/result"+num+"1.txt";
		}

	}

}
