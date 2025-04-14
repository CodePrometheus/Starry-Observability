package org.example;

import java.io.File;

/**
 * 将Demo项目中和ByteBuddy关系不大的常用代码以工具类形式维护
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/11/27 12:36 AM
 */
public class DemoTools {
    private DemoTools() {
    }

    /**
     * 删除当前类路径下所有文件和子目录
     */
    public static void deleteCurrentClassPathFiles() {
        deletePathFiles(currentClassPathFile());
    }

    /**
     * 删除指定路径下所有文件
     */
    public static void deletePathFiles(File path) {
        if (path.isDirectory()) {
            // 遍历当前目录下所有文件和子目录
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是子目录，递归调用删除文件方法
                        deletePathFiles(file);
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 返回一个当前类路径对应的File对象
     */
    public static File currentClassPathFile() {
        return new File(currentClassPath());
    }

    /**
     * 获取当前类所在的绝对路径
     */
    public static String currentClassPath() {
        return Thread.currentThread().getContextClassLoader().getResource("").getFile();
    }
}
