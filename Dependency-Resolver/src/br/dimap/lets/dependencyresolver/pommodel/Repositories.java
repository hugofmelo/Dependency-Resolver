package br.dimap.lets.dependencyresolver.pommodel;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "repositories")
@XmlAccessorType (XmlAccessType.FIELD)
public class Repositories
{
	@XmlElement (name = "repository")
	private List<Repository> repositories = null;

	public List<Repository> getRepositories()
	{
		return repositories;
	}
	public void setRepositories(List<Repository> repositories)
	{
		this.repositories = repositories;
	}
}
