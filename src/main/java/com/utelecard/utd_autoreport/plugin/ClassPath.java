/**
 * ClassPath.java
 *
 * This class defines methods to localize the reports jars
 * 
 *@author Ronny Z. Suero
 * */
package com.utelecard.utd_autoreport.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPath 
{
	private static final String METODO_ADD_URL = "addURL";
	@SuppressWarnings("rawtypes")
	private static final Class[] PARAMETRO_METODO = new Class[] { URL.class };
	private final URLClassLoader loader;
	private final Method metodoAdd;

	/**
	 * class constructor
	 * @throws NoSuchMethodException 
	 * */
	public ClassPath() throws NoSuchMethodException 
	{
		loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		metodoAdd = URLClassLoader.class.getDeclaredMethod(METODO_ADD_URL,
				PARAMETRO_METODO);
		metodoAdd.setAccessible(true);
	}

	/**
	 * This method add the report jar in the classpath
	 * @throws MalformedURLException
	 * @throws IllegalAccessException, 
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @return void
	 * */
	public void addFile(final File archivo) throws MalformedURLException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		if (archivo != null)
		{
			if (archivo.toURI().toURL() != null) 
				metodoAdd.invoke(loader, new Object[] { archivo.toURI().toURL() });
		}
	}

	/**
	 * This method add the report's URL
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException, 
	 * @throws InvocationTargetException
	 * @return void
	 * */
	public void addURLs(final URL[] urls) throws IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException 
	{
		if (urls != null) 
		{
			for (final URL url : urls)
			{
				if (url != null) 
					metodoAdd.invoke(loader, new Object[] { url });
			}
		}
	}

	/**
	 * This method get the list of URL
	 * @return URL[] Array
	 * */
	public URL[] getURLs() 
	{
		return loader.getURLs();
	}
}