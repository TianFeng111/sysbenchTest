package SysbenchTest;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Admin on 2018/11/12.
 */
public class FileControl {
    public void cleanFile(File file){      //清空文件内容
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public ArrayList<String> readNum(File file) {   //读取测试数据
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                arrayList.add(str);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public String readResult(File file,int lineNum)throws IOException{  //将每次测试运行结果读出 由于不同测试的应得结果所在行数不同，添加行数变量（int）
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
       String line = null;
        for (int lineCounter = 1;;lineCounter++){
            line = input.readLine();
            if (lineCounter==lineNum && line != null) break;
        }
        return line;
    }

    public void readAndWriteResult(Process process,File file,int lines){
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            String line = null;
//            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
//            BufferedWriter bufferedWriter = new BufferedWriter(writerStream); //将结果写入到结果文件 完成一轮
                for (int i=0;i<lines;i++){
                    line = br.readLine();
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try{
                bufferedWriter.close();
                fileWriter.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
