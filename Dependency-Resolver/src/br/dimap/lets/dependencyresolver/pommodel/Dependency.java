package br.dimap.lets.dependencyresolver.pommodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement ( name = "dependency" )
@XmlAccessorType (XmlAccessType.FIELD)
public class Dependency
{
	@XmlElement
	private String groupId;
	
	@XmlElement
	private String artifactId;
	
	@XmlElement
	private String version;
	
	public String getGroupId ()
	{
		return this.groupId;
	}
	public void setGroupId ( String groupId )
	{
		this.groupId = groupId;
	}
	
	public String getArtifactId ()
	{
		return this.artifactId;
	}
	public void setArtifactId (String artifactId)
	{
		this.artifactId = artifactId;
	}
	
	public String getVersion ()
	{
		return this.version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
}
