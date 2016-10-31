package edu.lk;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 
 * TODO  
 * 1 获取jar
 * 2 生成 .sha文件
 * 3 生成_remote.repositories
 * 4  占内存太多的情况 ，
 * 5 处理比较慢， 电脑卡
 * 
 * @author lk
 *
 */
public class RelocateJar2mvn {

	private static final String LOCAL_MVN_REPO = "D:\\mvn\\repos\\";
	private static final String _m2 = ".m2";
	private static final String _gradle = ".gradle";
	private static final String _ivy = ".ivy";
	private static final String _ = "\\.";
	private static final String _ECLIPSE = "\\Program Files";
	private static final String _nexus = "\\nexus";
	private static final String _JAVA = "\\JAVA";

	static ExecutorService es = Executors.newCachedThreadPool();
	static CompletionService<String> cs = new ExecutorCompletionService<String>(es);
    
	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws IOException, Exception {
		
		String[] excludes = new String[]{"$RECYCLE.BIN" .toLowerCase(), LOCAL_MVN_REPO .toLowerCase(), _ECLIPSE.toLowerCase()
				, _nexus.toLowerCase(), _JAVA.toLowerCase(),  _, "aaaaaddddxxx"};
		EfusParser parser = new EfusParser(excludes);
		String efuPath = "C:\\Users\\Administrator\\Desktop\\ff22.efu";
		Set<JarLine> jarPaths = parser.getJarPaths(efuPath);
		for (Iterator iterator = jarPaths.iterator(); iterator.hasNext();) {
			JarLine jarLine = (JarLine) iterator.next();
//			System.out.println( jarLine);
		}
		System.out.println(" TOTAL SIZE : "  +  jarPaths .size() );
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println( " ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  ");
		System.out.println();
		System.out.println();
		System.out.println();
		
		try {
			Iterator<JarLine> iterator = jarPaths.iterator();
			while (iterator.hasNext()) {
				JarLine jarLine = (JarLine) iterator.next();
				SingleJar2mvn single = new SingleJar2mvn(jarLine.getPath());
				cs.submit(single);
			}
			int tasksSize = jarPaths.size();
			for (int i = 0; i < tasksSize; i++) {
				Future<String> take = cs.take();
				String string = take.get();
				System.out.println( " ret " + string );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	    	es.shutdownNow();
	    }
		
		
	}
}