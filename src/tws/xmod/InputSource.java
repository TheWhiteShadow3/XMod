package tws.xmod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class InputSource
{
	private File file;
	private URL url;
	private String name;
	private InputStream inputStream;
	
	public InputSource(File file)
	{
		this.file = file;
		this.name = file.getName();
		try
		{
			this.url = file.toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public InputSource(URL url)
	{
		this.url = url;
		this.name = url.getPath();
	}
	
	public InputSource(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	public InputSource(String str)
	{
		this.inputStream = new ByteArrayInputStream(str.getBytes());
	}
	
	public InputSource(InputSource parent, String relPath)
	{
		try
		{
			this.url = new URL(parent.url, relPath);
			this.name = url.getPath();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream() throws IOException
	{
		if (inputStream != null)
			return inputStream;
		else if (url != null)
			return url.openStream();
		else
			throw new IOException("Invalid InputSource.");
	}

	public String getName()
	{
		if (file != null) return file.getName();
		
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public URL getURL()
	{
		return url;
	}

	public File getFile()
	{
		return file;
	}

	@Override
	public String toString()
	{
		return "InputSource [name=" + name + "]";
	}
}
