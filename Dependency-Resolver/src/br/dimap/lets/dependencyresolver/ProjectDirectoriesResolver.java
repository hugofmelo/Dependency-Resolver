package br.dimap.lets.dependencyresolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import br.dimap.lets.dependencyresolver.pommodel.Dependency;
import br.dimap.lets.dependencyresolver.pommodel.Project;
import br.dimap.lets.dependencyresolver.pommodel.Repository;

public class ProjectDirectoriesResolver
{
	private File projectRoot;
	private Set <String> sourcesRoots;
	private Set <String> testRoots;
	private Set <String> jarsFiles;
	private int pomFileCounter;
	// TODO remover esse atributo
	public Set <String> pomFiles;

	public ProjectDirectoriesResolver (File projectRoot)
	{
		this.projectRoot = projectRoot;
		sourcesRoots = new HashSet<String>();
		testRoots = new HashSet<String>();
		jarsFiles = new HashSet<String>();
		pomFileCounter = 1;
		pomFiles = new HashSet<String>();
		
		resolve();
	}

	private void resolve()
	{
		File parent;
		Stack<File> filesStack = new Stack<File>();

		parent = projectRoot;

		// Verificar se o caminho existe e é uma pasta
		if ( parent.exists() == false )
		{
			throw new IllegalArgumentException ("Caminho não existe: " + projectRoot);
		}
		else if ( parent.isDirectory() == false )
		{
			// TODO descomentar
			//throw new IllegalArgumentException ("Caminho não representa uma pasta: " + projectRoot);
		}
		// não sendo... 
		else
		{
			// Percorre os arquivos do projeto (varredura em profundidade). Os tipos procurados são *.java, pom.xml, ???

			filesStack.add(parent);
			while (filesStack.isEmpty() == false)
			{
				parent = filesStack.pop();

				// Para cada documento na pasta
				for (File child : parent.listFiles())
				{
					// Se for uma pasta, empilha para ser processado depois 
					if ( child.isDirectory() )
					{
						filesStack.add(child);
					}
					// É um arquivo...
					else
					{
						// Arquivo *.java
						if ( child.getName().endsWith(".java") )
						{
							try
							{
								String path = getSourceDir(child);

								// Verifica se é um dir de classes para teste.
								if ( isTestDir(path) )
								{
									this.testRoots.add(path);
								}
								else
								{
									this.sourcesRoots.add(path);
								}
							}
							catch (FileNotFoundException e)
							{
								// TODO Logger.getInstance().writeErrorLog ("O arquivo '" + child.getAbsolutePath() + "' não foi encontrado. Ele foi ignorado para a análise.")
								e.printStackTrace();
							}
							catch (IOException e)
							{
								// Falhou ao ler um arquivo. Salvar no relatório e ignorá-lo
								// TODO	Logger.getInstance().writeErrorLog ("O arquivo '" + child.getAbsolutePath() + "' não pôde ser lido. Ele foi ignorado para a análise.");
								e.printStackTrace();
							}
						}
						// Arquivo pom.xml.
						else if ( child.getName().equals("pom.xml") )
						{
							this.pomFiles.add(child.getAbsolutePath());
							
							try
							{
								resolveMavenDependencies (child);
							}
							catch (MavenInvocationException e)
							{
								e.printStackTrace();
								//TODO Logger.getInstance().writeErrorLog("Erro ao resolver dependencias de ' + child.getAbsolutePath() + "'. Stacktrace:" + e.printStackTrace());
							}
							catch (IOException e)
							{
								e.printStackTrace();
								// TODO Logger.getInstance().writeErrorLog("Erro ao resolver dependencias de ' + child.getAbsolutePath() + "'. Stacktrace:" + e.printStackTrace());
							}
							catch (IllegalStateException e)
							{
								//System.out.println ( "Erro ao resolver dependencias de '" + child.getAbsolutePath() + "'. A build falhou.\nStacktrace:");
								e.printStackTrace();
								// TODO Logger.getInstance().writeErrorLog("Erro ao resolver dependencias de ' + child.getAbsolutePath() + "'. A build falhou.\nStacktrace:" + e.printStackTrace());
							}
							
							pomFileCounter++;
						}
						// Arquivo *.jar. É considerado como sendo uma dependencia do projeto
						else if ( child.getName().endsWith((".jar") ) )
						{
							this.jarsFiles.add(child.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	// Verifica, de maneira bem fraca, se o source dir é de classes de teste. No caso considera-se que é source de teste se houver uma pasta chamada "test" no caminho.
	private boolean isTestDir(String path)
	{
		return path.indexOf("\\test\\") != -1;
	}

	// Esse método executa o maven para baixar e localizar as dependencias do arquivo pom.xml.
	// Ele cria um arquivo temporário e depois o apaga
	// É necessário ter um maven instalado e o home setado.
	// TODO remover os caminhos hardcoded. Colocar em um arquivo properties ou no PATH do sistema.
	// Podem haver vários arquivos pom.xml em um mesmo projeto.
	private void resolveMavenDependencies(File pomPath) throws MavenInvocationException, IOException
	{
		File output = new File ( this.projectRoot.getParent()+"/"+this.projectRoot.getName()+"-depedencies" + pomFileCounter + ".txt" );
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( pomPath );
		request.setGoals(Arrays.asList("dependency:list"));
		
		Properties properties = new Properties();
		properties.setProperty("outputFile", output.getAbsolutePath()); // redirect output to a file
		properties.setProperty("outputAbsoluteArtifactFilename", "true"); // with paths
		properties.setProperty("includeScope", "runtime"); // only runtime (scope compile + runtime)
		// if only interested in scope runtime, you may replace with excludeScope = compile
		
		request.setProperties(properties);

		Invoker invoker = new DefaultInvoker();
		// the Maven home can be omitted if the "maven.home" system property is set
		invoker.setMavenHome(new File("C:/Users/hugofm/apache-maven-3.5.0"));
		invoker.setOutputHandler(null); // not interested in Maven output itself
		InvocationResult result;
		
		
		result = invoker.execute(request);

		if (result.getExitCode() != 0)
		{
			// TODO descomentar
			// Files.deleteIfExists(output.toPath());
			throw new IllegalStateException("Resultado da build do pom.xml " + this.pomFileCounter + " falhou.\nPath: " + pomPath.getAbsolutePath());
		}

		Pattern pattern = Pattern.compile("(?:compile|runtime):(.*)");
		try (BufferedReader reader = Files.newBufferedReader(output.toPath()))
		{
			// Pular outras linhas
			while (!"The following files have been resolved:".equals(reader.readLine()));
			
			// Salvar os endereços dos jars
			String line;
			while ((line = reader.readLine()) != null && !line.isEmpty())
			{
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
				{
					// group 1 contains the path to the file
					this.jarsFiles.add(matcher.group(1));
				}
			}
		}
	}

	// Método auxiliar. Dado um arquivo java, retorna o caminho para o source root dele.
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

	// Extrai o package declaration do arquivo java. "package org.com.net;"
	private static String getPackageDeclaration(File javaFile) throws FileNotFoundException, IOException
	{
		boolean existsPackageDeclaration = false;

		// output
		String packageDeclaration = null;

		try(BufferedReader br = new BufferedReader(new FileReader(javaFile)))
		{
			String line = br.readLine();

			while (line != null && existsPackageDeclaration == false)
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
	
	
	public Set<String> getSourcesRoots() {
		return sourcesRoots;
	}

	public Set<String> getTestRoots() {
		return testRoots;
	}

	public Set<String> getJarsFiles() {
		return jarsFiles;
	}

	private static void parsePomXML(File pomPath)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance( Project.class );
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Project project = (Project)jaxbUnmarshaller.unmarshal( pomPath );

			System.out.println("Parsing pom.xml from " + pomPath.getParent());
			System.out.println("Repositories: ");
			for ( Repository r : project.getRepositories().getRepositories() )
			{
				System.out.println(r.getUrl());
			}

			System.out.println("Dependencies: ");
			for ( Dependency d : project.getDependencies().getDependencies() )
			{
				System.out.println(d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion());
			}
		}
		catch ( JAXBException e )
		{
			e.printStackTrace();
		}
	}
}
