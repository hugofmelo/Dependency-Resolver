package br.dimap.lets.dependencyresolver.pommodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "dependencies")
@XmlAccessorType (XmlAccessType.FIELD)
public class Dependencies
{
	@XmlElement (name = "dependency")
	private List<Dependency> dependencies = null;

	public List<Dependency> getDependencies()
	{
		return dependencies;
	}
	public void setDependencies(List<Dependency> dependencies)
	{
		this.dependencies = dependencies;
	}
}
