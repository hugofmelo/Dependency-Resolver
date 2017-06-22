package br.dimap.lets.dependencyresolver;

import java.io.File;

public class Main
{
	//private static final String projectRoot = "./projects/processing-processing";
	private static final String rootDir = "./projects";
	
	public static void main (String args[])
	{
		File projectsRoot = new File (rootDir);
		
		// Para cada documento na pasta
		for (File project : projectsRoot.listFiles())
		{
			ProjectDirectoriesResolver dirResolver = new ProjectDirectoriesResolver(project);
			
			System.out.println("Projeto: " + project.getName());
			
			System.out.println("Test dirs: ");
			for ( String s : dirResolver.getTestRoots() )
			{
				System.out.println(s);
			}
			
			System.out.println("Source dirs: ");
			for ( String s : dirResolver.getSourcesRoots() )
			{
				System.out.println(s);
			}
			
			System.out.println("Jars: ");
			for ( String s : dirResolver.getJarsFiles() )
			{
				System.out.println(s);
			}
			
			System.out.println("pom files: ");
			for ( String s : dirResolver.pomFiles )
			{
				System.out.println(s);
			}
		}
	}
}
