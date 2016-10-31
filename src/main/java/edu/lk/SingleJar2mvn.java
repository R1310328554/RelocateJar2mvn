package edu.lk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


/**
 * 
 * TODO  
 * 1 获取jar
 * 2 生成 .sha文件
 * 3 生成_remote.repositories
 * 4 ..
 * 
 * @author lk
 *
 */
public class SingleJar2mvn implements Callable<String> {

	private static final String LOCAL_MVN_REPO = "D:\\mvn\\repos\\";
	
	private static final String META_INF = "META-INF/";
	private static final String POM_XML_FILE = "pom.xml";
	private static final String POM_PROPERTY_FILE = "pom.properties";
	private static final String MANIFEST_FILE = "MANIFEST.MF";
	
	private static final String _SHA1 = ".sha1";
	private static final String _REMOTE_REPOSITORIES = "_remote.repositories";
	
    static final int BUFFER = 4096;

	private String jarPath;

	private String msg = "";
	
	
	public SingleJar2mvn(String jarPath) {
		this.jarPath = jarPath;
	}

	/**
	 * 将本地的jar， 放置到 本地maven仓库中去
	 * @return 
	 * 
	 * @throws IOException
	 */
	protected String relocateJar2mvn() {
		JarElement jar;
		try {
			jar = parserJar(jarPath);
			if (jar == null) {
				return "ERROR";
			} else {
//				downloadPomXml2mvn(name, name);
			}
			String mvnJarPath = locateJar(jar);
			if (mvnJarPath == null) {
				return "ERROR";
			}
			cpJar2mvn(jarPath, mvnJarPath);
			String mvnPomPath = mvnJarPath.replace(".jar", ".pom");
			cpPomXml2mvn(mvnPomPath, jar.getPomXml());
		} catch (IOException e) {
			e.printStackTrace();
			setMsg(e.getMessage());
			return "ERROR";
		}
		return "SUCCESS";
	}

	private  void cpPomXml2mvn(String mvnPomPath, String pomXmlStr) {
		File mvnPom = new File(mvnPomPath);
		try {
			OutputStream output = new FileOutputStream(mvnPom);
			IOUtils.write(pomXmlStr, output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	/**
	 * 从 mvn 远程 仓库下载 TODO
	 * 
	 * @param mvnPomPath
	 * @param pomXmlStr
	 */
	private  void downloadPomXml2mvn(String mvnPomPath, String jarInfo) {
		
	}

	private  void cpJar2mvn(String srcJarPath, String mvnJarPath) {
		File srcJar = new File(srcJarPath);
		File mvnJar = new File(mvnJarPath);
		if (mvnJar.exists() && mvnJar.isFile()) {
			setMsg("对应的jar 已经存在 ！");
			return;
		}
		try {
			OutputStream output = new FileOutputStream(mvnJar);
			InputStream input = new FileInputStream(srcJar);
			IOUtils.copy(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	private  String locateJar(JarElement jar) {
		String mvnPath = jar.getMvnPath();
		if (mvnPath == null) {
			return null;
		}
		String locateJarPath = LOCAL_MVN_REPO + "\\" + mvnPath;
		File f = new File(locateJarPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return locateJarPath + "\\" +  jar.getJarName();
	}

	private JarElement parserJar(String path) throws IOException {
		JarElement jarInfo = null;
		
		File jarFile = new File(path);
		if (jarFile.isDirectory()) {
			setMsg(" 不能是一个 目录 ！ ");
//			System.out.println( msg);
			return null;
		}
		if (!jarFile.exists()) {
			setMsg(" 不存在  ！ ");
//			System.out.println( msg);
			return null;
		}
		
		ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile));
		ZipEntry entry;
		
        while ((entry = zis.getNextEntry()) != null) {
//            System.out.println("Extracting: " + entry);
            String name = entry.getName();
//            System.out.println(name);
            
            if (name.startsWith(META_INF)) {
                if (name.endsWith(POM_PROPERTY_FILE)) {
                	// META-INF/maven/com.fasterxml.jackson.core/jackson-annotations/pom.properties
                	jarInfo = getJarInfo(zis, entry);
    				if (jarInfo != null) {
    					jarInfo.setName(jarFile.getName());
    					//System.out.println( pomInfo);
					} else {
						setMsg("不存在文件， 或解析失败： " + POM_PROPERTY_FILE);
					}
        		} else if (name.endsWith(POM_XML_FILE)) {
        			String pomXmlInfo = getPomXmlInfo(zis, entry);
    				if (jarInfo != null) {
    					jarInfo.setPomXml(pomXmlInfo);
    					break;
					} else {
						setMsg("不存在文件， 或解析失败： " + POM_XML_FILE);
					}
        		} else if (name.endsWith(MANIFEST_FILE)) {
        			
        		} else {
        			
        		}
			} else {
				continue;
			}
        }

		if (jarInfo == null && msg.equals("") && jarFile.length() <= 100) {
			setMsg("jar 文件格式不正确， ： ");
		}
		return jarInfo;
	}

	private  String getPomXmlInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
		byte data[] = new byte[BUFFER];
		int read;
		StringBuffer sb = new StringBuffer();
		while ((read = zis.read(data)) != -1) {
			String pomXmlInfo = new String(data, 0, read);
			sb.append(pomXmlInfo);
		}
		return sb.toString();
	}
	
	private  JarElement getJarInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
		JarElement je = null;
		byte data[] = new byte[BUFFER];
		String artifactId = null;
		String groupId = null;
		String version = null;
		
		int read;
		StringBuffer sb = new StringBuffer();
		while ((read = zis.read(data)) != -1) {
			String pomInfo = new String(data, 0, read);
			sb.append(pomInfo);
		}
		StringTokenizer st = new StringTokenizer(sb.toString(), "\n");
		while (st.hasMoreElements()) {
			String str = ((String) st.nextElement()).trim();
			//System.out.println(str);
			if (!str.startsWith("#") && str.indexOf('=') != -1) {
				String[] arr = str.split("=");
				if (arr.length == 2) {
					String le = arr[0];
					if (le.equals("version")) {
						version = arr[1];
					}
					if (le.equals("groupId")) {
						groupId = arr[1];
					}
					if (le.equals("artifactId")) {
						artifactId = arr[1];
					}
				}
			}
		}
		je = new JarElement(version, groupId, artifactId);
//		Properties prop = new Properties();
//		prop.
//        String string = name.split(META_INF)[1];
        return je;
	}
	

	private  JarElement getPomInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
		JarElement je = null;
        String name = entry.getName();
        if (name.endsWith(POM_PROPERTY_FILE)) {
        	
		} else if (name.endsWith(POM_XML_FILE)) {
			
		} else if (name.endsWith(MANIFEST_FILE)) {
			
		} else {
			
		}
        return null;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
		public String call() throws Exception {
			String relocateJar2mvn = relocateJar2mvn();
	//		System.out.println( relocateJar2mvn);
			return jarPath + " ==>  " + relocateJar2mvn + " : " + getMsg();
		}
}