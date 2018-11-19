package SysbenchTest;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Admin on 2018/11/12.
 */
public class SysTest {

    public static void main(String[] args) {
        String commandCpu = "sysbench --cpu-max-prime=10000 --threads=2 cpu run  ";
        String commandFileIoPrepare = "sysbench --file-total-size=2G fileio prepare";
        String commandFileIoRun = "sysbench --file-total-size=2G --file-test-mode=rndrw --time=120 --max-requests=0 fileio run ";
        String commandFileIoClean = "sysbench --file-total-size=2G fileio cleanup";
        String commandMemory = "sysbench --threads=4  --memory-block-size=8k --memory-total-size=4G memory run ";
        String commandThreads = "sysbench --thread-yields=2000 --thread-locks=8 threads run ";
        String commandMutex = "sysbench mutex run ";
        String OLTPPrepare = "sysbench ./tests/include/oltp_legacy/oltp.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password=123 --oltp-test-mode=complex --oltp-tables-count=10 --oltp-table-size=100000 --threads=10 --time=120 --report-interval=10 prepare";
        String OLTPRun = "sysbench ./tests/include/oltp_legacy/oltp.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password=123 --oltp-test-mode=complex --oltp-tables-count=10 --oltp-table-size=100000 --threads=10 --time=120 --report-interval=10 run ";
        String OLTPClean = "sysbench ./tests/include/oltp_legacy/oltp.lua --mysql-host=localhost --mysql-port=3306 --mysql-user=root --mysql-password=123 cleanup";
        FileControl fileControl = new FileControl();
        File my = new File("/etc/my.cnf");
        File mysqlNum = new File("/usr/local/sysbench/mysqlNum.csv");
        File mysqlResult = new File("/usr/local/sysbench/mysqlResult.csv");
        File result = new File("/usr/local/sysbench/mysysbench.log");
        ArrayList<String> arrayList = fileControl.readNum(mysqlNum);   //读取所有测试数据
        int size = arrayList.size();               //测试数据组数
        String[] array = arrayList.toArray(new String[size]);
        String[] cpu = new String[241];
        String[] memory = new String[241];
        String[] threads = new String[241];
        String[] mutex = new String[241];
        String[] oltpTrans = new String[241];
        String[] oltpQue = new String[241];
        String[] oltp = new String[241];
        for (int i=0;i<size;i++){       //循环体  基于测试数据组数
            fileControl.cleanFile(my);      //清空mysql配置文件
            String[] a = array[i].split(",");      //将单组测试数据存入一个长为11得字符数组
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(my, true));   //向mysql配置文件中写入配置数据
                System.out.println("开始覆盖配置文件...");
                bw.write("[mysqld]");
                bw.newLine();
                bw.write("basedir=/usr/local/sysbench");
                bw.newLine();
                bw.write("datadir=/usr/local/sysbench/data");
                bw.newLine();
                bw.write("key_buffer_size"+"="+a[0]+"MB");
                bw.newLine();
                bw.write("record_buffer_size"+"="+a[1]+"KB");
                bw.newLine();
                bw.write("read_rnd_buffer_size"+"="+a[2]+"MB");
                bw.newLine();
                bw.write("sort_buffer_size"+"="+a[3]+"MB");
                bw.newLine();
                bw.write("join_buffer_size"+"="+a[4]+"MB");
                bw.newLine();
                bw.write("table_cache"+"="+a[5]+"MB");
                bw.newLine();
                bw.write("thread_cache_size"+"="+a[6]);
                bw.newLine();
                bw.write("innodb_buffer_pool_size"+"="+a[7]+"GB");
                bw.newLine();
                bw.write("innodb_log_buffer_size"+"="+a[8]+"MB");
                bw.newLine();
                bw.write("tmp_table_size"+"="+a[9]+"MB");
                bw.newLine();
                bw.write("back_log"+"="+a[10]);
                bw.newLine();
                bw.close();
                System.out.println("配置文件覆盖完毕...");

                System.out.println("开始测试...");

                //cpu
                Process process1 = Runtime.getRuntime().exec(commandCpu);   //执行相关测试命令并读取相应结果
                fileControl.readAndWriteResult(process1,result,30);
                cpu[i] = fileControl.readResult(result,25);
                System.out.println("cpu数据读取完成...");
                fileControl.cleanFile(result);
//                Process process2 = Runtime.getRuntime().exec(commandFileIoPrepare);
//                process2.waitFor();
//                Process process3 = Runtime.getRuntime().exec(commandFileIoRun);
//                fileControl.readAndWriteResult(process3,result,41);
//                String fileIo = fileControl.readResult(result,40);
//                System.out.println("FileIo数据读取完成...");
//                Process process4 = Runtime.getRuntime().exec(commandFileIoClean);
//                process4.waitFor();
//                fileControl.cleanFile(result);
                //memory
                Process process2 = Runtime.getRuntime().exec(commandMemory);
                fileControl.readAndWriteResult(process2,result,31);
                memory[i] = fileControl.readResult(result,31);
                System.out.println("memory数据读取完成...");
                fileControl.cleanFile(result);
                //threads
                Process process3 = Runtime.getRuntime().exec(commandThreads);
                fileControl.readAndWriteResult(process3,result,26);
                threads[i] = fileControl.readResult(result,21);
                System.out.println("threads数据读取完成...");
                fileControl.cleanFile(result);
                //mutex
                Process process4 = Runtime.getRuntime().exec(commandMutex);
                fileControl.readAndWriteResult(process4,result,26);
                mutex[i] = fileControl.readResult(result,21);
                System.out.println("mutex数据读取完成...");
                fileControl.cleanFile(result);
                //OLTP
                Process process5 = Runtime.getRuntime().exec(OLTPPrepare);
                process5.waitFor();
                Process process6 = Runtime.getRuntime().exec(OLTPRun);
                fileControl.readAndWriteResult(process6,result,49);
                oltpTrans[i] = fileControl.readResult(result,31);
                oltpQue[i] = fileControl.readResult(result,32);
                oltp[i] = fileControl.readResult(result,44);
                System.out.println("oltp数据读取完成...");
                Process process7 = Runtime.getRuntime().exec(OLTPClean);
                process7.waitFor();
                fileControl.cleanFile(result);

//                OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(mysqlResult),"utf-8");
//                BufferedWriter bufferedWriter = new BufferedWriter(writerStream); //将结果写入到结果文件 完成一轮
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mysqlResult, true));
                bufferedWriter.write(cpu[i]+","+memory[i]+","+threads[i]+","+mutex[i]+","+oltpTrans[i]+","+oltpQue[i]+","+oltp[i]);
                bufferedWriter.flush();
                bufferedWriter.newLine();
                bufferedWriter.close();
//                bufferedWriter.write(cpu[i]+","+memory[i]+","+threads[i]+","+mutex[i]+","+oltpTrans[i]+","+oltpQue[i]+","+oltp[i]);
//                bufferedWriter.newLine();
//                bufferedWriter.close();
                System.out.println("第"+(i+60)+"次结果写入完成...");
            }catch (FileNotFoundException e){
            e.printStackTrace();
            }catch (IOException e){
            e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
//            try {
//                process = Runtime.getRuntime().exec(commandCpu);   //执行相关测试命令并读取相应结果
//                process.waitFor();
//                String cpu = fileControl.readResult(result,25);
//                System.out.println("cpu 95perc d"+cpu);
//                fileControl.cleanFile(result);
//                process = Runtime.getRuntime().exec(commandFileIoPrepare);
//                process.waitFor();
//                process = Runtime.getRuntime().exec(commandFileIoRun);
//                process.waitFor();
//                String fileIo = fileControl.readResult(result,40);
//                process = Runtime.getRuntime().exec(commandFileIoClean);
//                process.waitFor();
//                fileControl.cleanFile(result);
//                process = Runtime.getRuntime().exec(commandMemory);
//                process.waitFor();
//                String memory = fileControl.readResult(result,31);
//                fileControl.cleanFile(result);
//                process = Runtime.getRuntime().exec(commandThreads);
//                process.waitFor();
//                String threads = fileControl.readResult(result,21);
//                fileControl.cleanFile(result);
//                process = Runtime.getRuntime().exec(commandMutex);
//                process.waitFor();
//                String mutex = fileControl.readResult(result,21);
//                fileControl.cleanFile(result);
//                process = Runtime.getRuntime().exec(OLTPPrepare);
//                process.waitFor();
//                process = Runtime.getRuntime().exec(OLTPRun);
//                process.waitFor();
//                String oltpTran = fileControl.readResult(result,31);
//                String oltpQue = fileControl.readResult(result,32);
//                String oltp = fileControl.readResult(result,44);
//                System.out.println("oltp 95perc d"+oltp);
//                process = Runtime.getRuntime().exec(OLTPClean);
//                process.waitFor();
//                OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(mysqlResult),"utf-8");
//                BufferedWriter bufferedWriter = new BufferedWriter(writerStream); //将结果写入到结果文件 完成一轮
//                bufferedWriter.write(cpu+","+fileIo+","+memory+","+threads+","+mutex+","+oltpTran+","+oltpQue+","+oltp);
//                bufferedWriter.newLine();
//                bufferedWriter.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
        }
    }

}
