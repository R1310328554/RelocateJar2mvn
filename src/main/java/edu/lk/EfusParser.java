package edu.lk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 * 解析everything 导出的 efu 文件，
 * 
 * @author lk
 *
 */
public class EfusParser {

	private String[] excludes;

	//$RECYCLE.BIN
	public EfusParser(String[] excludes) {
		this.excludes = excludes;
	}
	
	/**
	 * C:\Users\Administrator\Desktop\ff22.efu
	 * 
	 * @param efuPath
	 * @return
	 */
	public Set<JarLine> getJarPaths(String efuPath) {
		Set<JarLine> set = new HashSet<>();
		File file = new File(efuPath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			outer:
			while ((line = br.readLine()) != null) {
//				StringTokenizer st = new StringTokenizer(line, ",");
//				while (st.hasMoreElements()) {
//					Object str = (Object) st.nextElement();
//				}
				/**
				 * the line is sth like : "C:\svn\xx\WEB-INF\lib\commons-configuration-1.3.jar",,,,0
				 */
				String item = line.split(",")[0].toLowerCase();
				if (item.startsWith("\"")) {
					item = item.substring(1);
				}
				if (item.endsWith("\"")) {
					item = item.substring(0, item.length() - 1);
				}
				if (!item.endsWith(".jar")) {
					continue;
				}
				for (int i = 0; i < excludes.length; i++) {
					String string = excludes[i];
					if (item.contains(string)) {
						continue outer;
					}
				}
				int lastIndexOf = item.lastIndexOf("\\");
				String jarName = "";
				if (lastIndexOf > 0) {
					jarName = item.substring(lastIndexOf+1);
					if (jarName.startsWith("$")) {
						continue;
					}
					if (jarName.startsWith(".")) {
						continue;
					}
					if (jarName.startsWith("!")) {
						continue;
					}
					if (jarName.startsWith("~")) {
						continue;
					}
				}
				JarLine jl = new JarLine();
				jl.setName(jarName);
				jl.setPath(item);
				set.add(jl);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return set;
	}

	/**
		 * C:\Users\Administrator\Desktop\ff22.efu
		 * 
		 * @param efuPath
		 * @return
		 */
		public List getJarPathList(String efuPath) {
			List list = new ArrayList<>();
			File file = new File(efuPath);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
	//				StringTokenizer st = new StringTokenizer(line, ",");
	//				while (st.hasMoreElements()) {
	//					Object str = (Object) st.nextElement();
	//				}
					/**
					 * the line is sth like : "C:\svn\xx\WEB-INF\lib\commons-configuration-1.3.jar",,,,0
					 */
					String item = line.split(",")[0].toLowerCase();
					if (!item.endsWith(".jar")) {
						continue;
					}
					for (int i = 0; i < excludes.length; i++) {
						String string = excludes[i];
						if (item.contains(string)) {
							continue;
						}
					}
					int lastIndexOf = item.lastIndexOf("\\");
					if (lastIndexOf > 0) {
						String substring = item.substring(lastIndexOf+1);
						if (substring.startsWith("$")) {
							continue;
						}
					}
					list.add(item);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return list;
		}
}

class JarLine {
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	String path;
	long size;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JarLine) {
			JarLine jl = (JarLine)obj;
			if (jl.getName().equals(getName())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "JarLine [name=" + name + ", path=" + path + ", size=" + size
				+ "]";
	}
	
}