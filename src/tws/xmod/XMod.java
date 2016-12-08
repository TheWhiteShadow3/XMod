package tws.xmod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

//XXX: Geändert
public class XMod
{
	private static Map<String, Class<? extends XModParser>> parserMap;
	
	static
	{
		parserMap = new HashMap<>();
		addParser(XModXMLParser.class, false, null, "xml", "xhtml");
		addParser(XModJSONParser.class, false, "json", "js");
		addParser(XModNativeParser.class, false, "xmod");
	}
	
	/**
	 * Fügt einen Parser hinzu.
	 * @param clazz
	 * @param overrite
	 * @param extensions
	 */
	public static void addParser(Class<? extends XModParser> clazz, boolean overrite, String... extensions)
	{
		for(String ext : extensions)
		{
			if (!overrite && parserMap.containsKey(ext)) continue;
			parserMap.put(ext, clazz);
		}
	}
	
	public static XModParser findParser(InputSource inputSource) throws XModException
	{
		String ext =  getExtension(inputSource.getName());
		try
		{
			Class<? extends XModParser> parserClass = parserMap.get(ext);
			if (parserClass == null)	
				throw new XModException("could not find parser for file extension '" + ext + "'");
			
			return parserClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new XModException(e);
		}
	}
	
	private static String getExtension(String filename)
	{
		if (filename == null) return null;
		int dot = filename.lastIndexOf('.');
		if (dot == -1) return null;
		
		return filename.substring(dot+1);
	}
	
	private final XModSystem system;

	public XMod()
	{
		this.system = new XModSystem();
	}
	
	public XModConfig getConfig()
	{
		return system;
	}

	public void parse(InputSource inputSource, OutputStream out) throws IOException
	{
		XModParser parser = findParser(inputSource);
		
		XModWriter writer = new XModWriter(system, out);
		system.setInputSource(inputSource);
		system.setWriter(writer);
		system.setVariable(XModContext.SOURCE, inputSource.getName());
		
		try
		{
			writer.write(parser.read(inputSource, system));
			writer.flush();
		}
		catch (XModException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new XModException(e);
		}
	}
	
	public String parse(InputSource inputSource) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(1 << 10);
		parse(inputSource, out);
		
		return out.toString();
	}

	public void parse(InputSource inputSource, File dstFile) throws IOException
	{
		parse(inputSource, new FileOutputStream(dstFile));
	}
}
