package realcode.yichuan.ver1;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length != 2)//也是就没有传入数据路径和结果写入路径时，会提示一条错误信息
        {
            System.err.println("please input args: graphFilePath, resultFilePath");
            return;
        }

        String graphFilePath = args[0];//这个最终让比赛放传入，数据路径，也就是case
        String resultFilePath = args[1];//结果写入路径

        LogUtil.printLog("Begin");

        // 璇诲彇杈撳叆鏂囦欢
        String[] graphContent = FileUtil.read(graphFilePath, null);//返回一个字符串，字符串的格式是：string[0]=第一行，string[1]=空行，string[2]=服务器费用那一行

        // 鍔熻兘瀹炵幇鍏ュ彛
        String[] resultContents = Deploy.deployServer(graphContent);//如果有可到达路径则返回结果字符串，如果没有则返回null
			//如果有解，则应该返回的格式是：i行数据=string[i]

        // 鍐欏叆杈撳嚭鏂囦欢
        if (hasResults(resultContents))
        {
            FileUtil.write(resultFilePath, resultContents, false);//写文件(文件路径、参数会给出)
        }
        else
        {
            FileUtil.write(resultFilePath, new String[] { "NA" }, false);
        }
        LogUtil.printLog("End");//和前面的begin是纪实用的，不用管他
    }
    
    private static boolean hasResults(String[] resultContents)
    {
        if(resultContents==null)//也就是我的deploy如果搜索不到路径则返回null
        {
            return false;
        }
        for (String contents : resultContents)//增强型的for循环，相当于for(int i=0;i<resultContents.length;i++){String contents=resultContents[i]}
        {
            if (contents != null && !contents.trim().isEmpty())
            {
                return true;
            }
        }
        return false;
    }

}

