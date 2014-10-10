/**
 * PluginLoader.java
 *
 * This class defines methods to load the report jars
 *
 *@author Ronny Z. Suero
 * */
package com.utelecard.utd_autoreport.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import com.utelecard.autoreport.plugin.ReportInterface;

public class PluginLoader
{
	private static final String FILE_EXTENSION = ".jar";
	private static final String PLUGINS_DIR = "reports";

	/**
	 *  This method return the list with the report's interfase
	 *
	 * @param host the host parameter defines the host of Connect to Oracle
	 * @param port the port parameter defines the port of Connect to Oracle
	 * @param database the database parameter defines the name of the Oracle's Database
	 * @param user the user parameter defines the username to Connect to Oracle
	 * @param password the password parameter defines the password to Connect to Oracle
	 * @return List<ReportInterface>
	 *
	 * */
	public static List<ReportInterface> getPlugins()
	{
		final ServiceLoader<ReportInterface> serviceLoader = ServiceLoader
				.load(ReportInterface.class);
		serviceLoader.reload();

		final List<ReportInterface> reports = new ArrayList<ReportInterface>();
		serviceLoader.forEach(report -> reports.add(report));

		return reports;
	}

	/**
	 *  This method load the reports jars
	 *
	 *@throws NoSuchMethodException
	 *@throws MalformedURLException
	 *@throws IllegalAccessException
	 *@throws IllegalArgumentException,
	 *@throws InvocationTargetException
	 * @return boolean
	 *
	 * */
	public static boolean loadJars() throws NoSuchMethodException,
	MalformedURLException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException
	{
		final List<File> jars = searchPlugins();

		if (jars.size() > 0)
		{
			final ClassPath cp = new ClassPath();

			for (final File jar : jars)
				cp.addFile(jar);

			return true;
		}
		return false;
	}

	/**
	 *  This method search the reports jars
	 *
	 * @return List<File>
	 *
	 * */
	private static List<File> searchPlugins()
	{
		final List<File> f = new ArrayList<File>();
		final File pluginDir = new File(PLUGINS_DIR);

		if (pluginDir.exists() && pluginDir.isDirectory()) {
			final File[] jars = pluginDir.listFiles(new FilenameFilter() {
				public boolean accept(final File dir, final String fileName) {
					return fileName.endsWith(FILE_EXTENSION);
				}
			});

			Arrays.asList(jars).stream().forEach(jar -> f.add(jar));

			return f;
		}
		return null;
	}
}