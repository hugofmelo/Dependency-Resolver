package br.dimap.lets.dependencyresolver.pommodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement ( name = "project", namespace="http://maven.apache.org/POM/4.0.0" )
@XmlAccessorType (XmlAccessType.FIELD)
public class Project
{
	@XmlElement ( name = "repositories" )
	private Repositories repositories;
	
	@XmlElement ( name = "dependencies" )
	private Dependencies dependencies;

	public Repositories getRepositories ()
	{
		return this.repositories;
	}
	public void setRepositories (Repositories repositories)
	{
		this.repositories = repositories;
	}
	
	public Dependencies getDependencies ()
	{
		return this.dependencies;
	}
	public void setDependencies (Dependencies dependencies)
	{
		this.dependencies = dependencies;
	}
}
