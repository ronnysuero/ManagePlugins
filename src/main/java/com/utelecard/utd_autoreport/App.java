/**
 * App.java
 *
 * This class defines the main method of the apliccation 
 * 
 *@author Ronny Z. Suero
 * */
package com.utelecard.utd_autoreport;

public class App 
{
	public static void main(final String[] args) 
	{
		final Environment env = new Environment(args);

		Runtime.getRuntime().addShutdownHook(new Thread() 
		{
			@Override
			public void run() {
				env.shutdown();
			}
		});

		env.start();
	}
}