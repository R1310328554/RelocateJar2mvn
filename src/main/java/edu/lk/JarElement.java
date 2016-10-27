package edu.lk;

class JarElement {
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	private String name;
	String version;
	String groupId;
	String artifactId;
	private String pomXml;

	public JarElement() {
		
	}

	public JarElement(String version, String groupId, String artifactId) {
		this.version = version;
		this.groupId = groupId;
		this.artifactId = artifactId;
	}
	
	@Override
	public String toString() {
		return "JarElement [version=" + version + ", groupId=" + groupId
				+ ", artifactId=" + artifactId + "]";
	}

	public String getJarName() {
		return name;
	}

	public String getMvnPath() {
		if (groupId == null || artifactId == null || version == null) {
			return null;
		}
		return (groupId).replace(".", "\\") + "\\" + 
//		(artifactId).replace(".", "\\") + 
		artifactId + "\\" + 
		version;
	}
	public String getMvnPath2() {
		return groupId+artifactId+version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPomXml() {
		return pomXml;
	}
	public void setPomXml(String pomXml) {
		this.pomXml = pomXml;
	}
	
}
