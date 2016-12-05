package tws.test;

import java.io.File;
import java.io.IOException;

import tws.xmod.InputSource;
import tws.xmod.XMod;

public class XModBubblesortExample
{
	public static void main(String[] args)
	{
		XMod xmod = new XMod();
		
		try
		{
			InputSource source = new InputSource(new File("test/bubblesort.xml"));
			System.out.println(xmod.parse(source));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
