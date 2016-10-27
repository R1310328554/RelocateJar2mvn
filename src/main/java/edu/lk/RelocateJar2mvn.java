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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class RelocateJar2mvn {

	private static final String LOCAL_MVN_REPO = "D:\\mvn\\repos\\";
	
	private static final String META_INF = "META-INF/";
	private static final String POM_XML_FILE = "pom.xml";
	private static final String POM_PROPERTY_FILE = "pom.properties";
	private static final String MANIFEST_FILE = "MANIFEST.MF";
    static final int BUFFER = 4096;
	static String xx = "E:\\downloads\\ff\\jackson-annotations-2.6.0.jar";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		relocateJar2mvn(xx);
	}

	/**
	 * 将本地的jar， 放置到 本地maven仓库中去
	 * @return 
	 * 
	 * @throws IOException
	 */
	private static String relocateJar2mvn(String localJar) {
		JarElement jar;
		try {
			jar = parserJar(localJar);
			if (jar == null) {
				return "ERROR";
			} else {
//				downloadPomXml2mvn(name, name);
			}
			String mvnJarPath = locateJar(jar);
			if (mvnJarPath == null) {
				return "ERROR";
			}
			cpJar2mvn(xx, mvnJarPath);
			String mvnPomPath = mvnJarPath.replace(".jar", ".pom");
			cpPomXml2mvn(mvnPomPath, jar.getPomXml());
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR" + e.getMessage();
		}
		return "SUCCESS";
	}

	private static void cpPomXml2mvn(String mvnPomPath, String pomXmlStr) {
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
	private static void downloadPomXml2mvn(String mvnPomPath, String jarInfo) {
		
	}

	private static void cpJar2mvn(String srcJarPath, String mvnJarPath) {
		File srcJar = new File(srcJarPath);
		try {
			OutputStream output = new FileOutputStream(mvnJarPath);
			InputStream input = new FileInputStream(srcJar);
			IOUtils.copy(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}

	private static String locateJar(JarElement jar) {
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

	static JarElement parserJar(String path) throws IOException {
		JarElement jarInfo = null;
		
		File jarFile = new File(path);
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
					}
        		} else if (name.endsWith(POM_XML_FILE)) {
        			String pomXmlInfo = getPomXmlInfo(zis, entry);
    				if (jarInfo != null) {
    					jarInfo.setPomXml(pomXmlInfo);
    					break;
    				}
        		} else if (name.endsWith(MANIFEST_FILE)) {
        			
        		} else {
        			
        		}
			} else {
				continue;
			}
        }
		return jarInfo;
	}

	private static String getPomXmlInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
		byte data[] = new byte[BUFFER];
		int read;
		StringBuffer sb = new StringBuffer();
		while ((read = zis.read(data)) != -1) {
			String pomXmlInfo = new String(data, 0, read);
			sb.append(pomXmlInfo);
		}
		return sb.toString();
	}
	
	private static JarElement getJarInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
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
			System.out.println(str);
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
	

	private static JarElement getPomInfo(ZipInputStream zis, ZipEntry entry) throws IOException {
		JarElement je = null;
        String name = entry.getName();
        if (name.endsWith(POM_PROPERTY_FILE)) {
        	
		} else if (name.endsWith(POM_XML_FILE)) {
			
		} else if (name.endsWith(MANIFEST_FILE)) {
			
		} else {
			
		}
        return null;
	}
}