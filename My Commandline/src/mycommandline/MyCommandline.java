package mycommandline;

import com.nomagic.magicdraw.commandline.CommandLine;

public class MyCommandline extends CommandLine
{

	@Override
	protected byte execute()
	{
		System.out.println("Executing my command line...");
		return 0;
	}

}
