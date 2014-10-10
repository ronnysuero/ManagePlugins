/**
 * Enviroment.java
 *
 * This class defines methods to manage the environment of the reports
 *
 *@author Ronny Z. Suero
 * */
package com.utelecard.utd_autoreport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;

import com.utelecard.autoreport.plugin.ReportInterface;
import com.utelecard.utd_autoreport.plugin.PluginLoader;

public class Environment
{
	private static Logger logger = Logger.getLogger(Environment.class.getName());
	private String eventName;
	private List<ReportInterface> runningReports = new ArrayList<ReportInterface>();

	/**
	 * class constructor
	 * @param args the args parameter is the same parameter of the method main
	 * */
	public Environment(String[] args)
	{
		new File("./reports").mkdir();

		// Search for event name
		Set<String> c = new HashSet<String>(Arrays.asList(args));
	    logger.setLevel(Level.ALL);

	    if(c.contains("-run"))
	    {
			int eventIndex = Arrays.asList(args).indexOf("-run") + 1;

			if(args.length > 1)
				this.eventName = args[eventIndex];
			else
				this.eventName = "";
		}
		else
		{
			logger.log(Level.SEVERE, "Please, write the argument \"-run [report-name]\" to execute an report.\n");
			System.exit(1);
		}
	}

	/**
	 * This method start the search of the reports
	 * @return void
	 * */
	public void start()
	{
		logger.info("[UTD AutoReport Monitor]\n");

		if(this.eventName.equals(""))
		{
			logger.log(Level.SEVERE, "Report not specified.\n");
			System.exit(1);
		}

		// Load plug-in's reports.
		try
		{
			if(PluginLoader.loadJars())
			{
				List<ReportInterface> reports = PluginLoader.getPlugins();
				boolean flag = true;

				for(ReportInterface report : reports)
				{
					if(this.eventName.equalsIgnoreCase(report.getReportNameToExecute()))
					{
						new File("./reports/" + report.getReportNameToExecute()).mkdir();

						// Generate required folders
						new File(String.format("./reports/%s/generated_files", report.getReportNameToExecute())).mkdir();
						new File(String.format("./reports/%s/config", report.getReportNameToExecute())).mkdir();
						new File(String.format("./reports/%s/logs", report.getReportNameToExecute())).mkdir();

						// Save log
						FileHandler fh = new FileHandler(String.format("./reports/%s/logs/%s.log", this.eventName, LocalDate.now()));
						fh.setFormatter(new SimpleFormatter());
						logger.addHandler(fh);
						report.load("./reports/" + report.getReportNameToExecute(), Environment.logger);
						logger.log(Level.FINE, String.format("[Running report: %s\n", this.eventName));

						// Run report
						Runnable event = (Runnable) report;
						FutureTask<Object> taskReport = new FutureTask<Object>(event, null);
						taskReport.run();

						// Add report close to shutdown application
						this.runningReports.add(report);
						flag = false;
						break;
					}
				}
				if(flag)
					logger.log(Level.SEVERE, "Don't found this report.\n");
			}
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, String.format("Error in the report system: %s\n", e.getMessage()));
		}
	}

	/**
	 * This method shutdown the application
	 * @return void
	 * */
	public void shutdown()
	{
		for(ReportInterface report : this.runningReports)
			logger.info(String.format("[Closing %s ]\n", report.getReportNameToExecute()));

		logger.info("[Application end]");
	}
}