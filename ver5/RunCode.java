package realcode.ver5;

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
		arr[0]="Chu/case5.txt";
		arr[1]="Chu/result51.txt";
//		arr[0]="src/TxtFile/case2.txt";
//		arr[1]="src/TxtFile/result21.txt";//
	}

}
