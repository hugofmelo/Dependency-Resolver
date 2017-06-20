package br.dimap.lets.dependencyresolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Main
{
	private static final String projectRoot = "./projects/processing-processing";
	
	public static void main (String args[])
	{
		// Auxiliar
		File parent;
		Stack<File> filesStack = new Stack<File>();
        
		//Output
		Set<File> sourceDirs = new HashSet<File>();
		Set<File> testDirs = new HashSet<File>();
        
		
		parent = new File(projectRoot);
		
		if ( parent.exists() == false )
		{
			throw new IllegalArgumentException ("Caminho não existe: " + projectRoot);
		}
		else if ( parent.isDirectory() == false )
        {
			throw new IllegalArgumentException ("Caminho não representa uma pasta: " + projectRoot);
        }
		else
		{
			// Busca em profundidade
			filesStack.add(parent);
			while (filesStack.isEmpty() == false)
			{
				parent = filesStack.pop();
				
				for (File child : parent.listFiles())
				{
					if ( child.isDirectory() )
					{
						filesStack.add(child);
					}
					else if ( child.isFile() && child.getName().endsWith(".java") )
					{
						try
						{
							String path = getSourceDir(child);
							
							// Existe 'test' no caminho. É considerado um test dir.
							if ( path.indexOf("\\test\\") != -1 )
							{
								testDirs.add(new File(path));
							}
							else
							{
								sourceDirs.add(new File (path));
							}
						}
						catch (FileNotFoundException e)
						{
							// Não deve acontecer.
							throw new RuntimeException (e);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
			System.out.println("Source directories: ");
			for ( File f : sourceDirs )
			{
				System.out.println(f.getAbsolutePath());
			}
			
			System.out.println();
			
			System.out.println("Test directories: ");
			for ( File f : testDirs )
			{
				System.out.println(f.getAbsolutePath());
			}
				
		}
         

	}

	private static String getSourceDir(File javaFile) throws FileNotFoundException, IOException
	{
		String dirPath = javaFile.getParent();
		String packageDeclaration = getPackageDeclaration(javaFile);
		String path;
		
		if ( packageDeclaration.equals("") )
		{
			path = dirPath + "\\";
		}
		else
		{
			path = dirPath.substring(0, dirPath.lastIndexOf(packageDeclaration) );
		}
		
		return path;
	}

	private static String getPackageDeclaration(File javaFile) throws FileNotFoundException, IOException
	{
		boolean existsPackageDeclaration = false;
		
		// output
		String packageDeclaration = null;
		
		try(BufferedReader br = new BufferedReader(new FileReader(javaFile)))
		{
		    String line = br.readLine();
		    
		    while (line != null)
		    {	
		    	if ( line.startsWith("package") )
    			{
		    		packageDeclaration = line.substring(line.indexOf(' ') + 1, line.indexOf(';'));
		    		packageDeclaration = packageDeclaration.replace('.', '\\');
		    		
		    		existsPackageDeclaration = true;
    			}
		    	
		        line = br.readLine();
		    }
		}
		
		if ( !existsPackageDeclaration )
		{
			packageDeclaration = "";
		}
		
		
		return packageDeclaration;	
	}
}
