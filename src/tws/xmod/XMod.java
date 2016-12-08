package tws.xmod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * XMod Hauptklasse
 * @author TheWhiteShadow
 */
public class XMod
{	
	private final XModSystem system;

	/**
	 * Erstellt ein neues XMod Objekt.
	 */
	public XMod()
	{
		this.system = new XModSystem();
	}
	
	/**
	 * Gibt die Konfiguration zu diesem XMod-Objekt zurück.
	 * @return Die XMod-Konfiguration.
	 */
	public XModConfig getConfig()
	{
		return system;
	}

	public void parse(InputSource inputSource, OutputStream out) throws IOException
	{
		XModParser parser = XModSystem.findParser(inputSource);
		
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
