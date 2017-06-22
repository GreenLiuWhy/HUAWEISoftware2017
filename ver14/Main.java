package realcode.ver14;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length != 2)//Ҳ�Ǿ�û�д������·���ͽ��д��·��ʱ������ʾһ��������Ϣ
        {
            System.err.println("please input args: graphFilePath, resultFilePath");
            return;
        }

        String graphFilePath = args[0];//��������ñ���Ŵ��룬���·����Ҳ����case
        String resultFilePath = args[1];//���д��·��

        LogUtil.printLog("Begin");

        // 读取输入文件
        String[] graphContent = FileUtil.read(graphFilePath, null);//����һ���ַ��ַ�ĸ�ʽ�ǣ�string[0]=��һ�У�string[1]=���У�string[2]=������������һ��

        // 功能实现入口
        String[] resultContents = Deploy.deployServer(graphContent);//����пɵ���·���򷵻ؽ���ַ����û���򷵻�null
			//����н⣬��Ӧ�÷��صĸ�ʽ�ǣ�i�����=string[i]

        // 写入输出文件
        if (hasResults(resultContents))
        {
            FileUtil.write(resultFilePath, resultContents, false);//д�ļ�(�ļ�·�����������)
        }
        else
        {
            FileUtil.write(resultFilePath, new String[] { "NA" }, false);
        }
        LogUtil.printLog("End");//��ǰ���begin�Ǽ�ʵ�õģ����ù���
    }
    
    private static boolean hasResults(String[] resultContents)
    {
        if(resultContents==null)//Ҳ�����ҵ�deploy�����������·���򷵻�null
        {
            return false;
        }
        for (String contents : resultContents)//��ǿ�͵�forѭ�����൱��for(int i=0;i<resultContents.length;i++){String contents=resultContents[i]}
        {
            if (contents != null && !contents.trim().isEmpty())
            {
                return true;
            }
        }
        return false;
    }

}

