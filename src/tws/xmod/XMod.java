package tws.xmod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class XMod
{
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
		system.setInputSource(inputSource);
		XModParser parser = new XModParser(system);
		try (XModWriter writer = new XModWriter(system, out))
		{
			system.setVariable(XModContext.SOURCE, inputSource.getName());
			system.setWriter(writer);
			writer.write(parser.read(inputSource));
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
