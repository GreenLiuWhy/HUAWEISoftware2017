package realcode.ver4;

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
		arr[0]="Chu/case4.txt";
		arr[1]="Chu/result42.txt";
//		arr[0]="src/TxtFile/case2.txt";
//		arr[1]="src/TxtFile/result21.txt";//
	}

}
