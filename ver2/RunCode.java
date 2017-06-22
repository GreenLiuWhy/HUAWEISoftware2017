package realcode.ver2;

public class RunCode {

	String[] arr;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RunCode r=new RunCode();
		Main.main(r.arr);
	}
	
	public RunCode()
	{
		arr=new String[2];
		//arr[0]="Chu/case0.txt";
		//arr[1]="src/TxtFile/result0.txt";
		arr[0]="src/TxtFile/case0.txt";
		arr[1]="src/TxtFile/thisresult0.txt";//
	}

}
