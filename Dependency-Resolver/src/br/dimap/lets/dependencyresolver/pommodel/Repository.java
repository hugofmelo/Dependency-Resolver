package br.dimap.lets.dependencyresolver.pommodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement ( name = "repository" )
@XmlAccessorType (XmlAccessType.FIELD)
public class Repository
{
	@XmlElement
	private String url;
	
	public String getUrl ()
	{
		return this.url;
	}
	public void setUrl (String url)
	{
		this.url = url;
	}
}
