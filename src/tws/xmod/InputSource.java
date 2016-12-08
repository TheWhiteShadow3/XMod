package tws.xmod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Die XMod Quelldatei.
 * @author TheWhiteShadow
 */
public class InputSource
{
	private File file;
	private URL url;
	private String name;
	private InputStream inputStream;
	
	/**
	 * Erstellt eine neue Quelle aus einer Datei.
	 * @param file Die Datei.
	 */
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
	
	/**
	 * Erstellt eine neue Quelle aus einer URL.
	 * @param url Die URL.
	 */
	public InputSource(URL url)
	{
		this.url = url;
		this.name = url.getPath();
	}
	
	/**
	 * Erstellt eine neue Quelle aus einem InputStream.
	 * @param inputStream Die Eingabedaten.
	 */
	public InputSource(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	/**
	 * Erstellt eine neue Quelle aus einem String.
	 * @param str Der String.
	 */
	public InputSource(String str)
	{
		this.inputStream = new ByteArrayInputStream(str.getBytes());
	}
	
	/**
	 * Erstellt eine neue Quelle aus einer URL, die relativ zur angegebenen Quelle liegt.
	 * @param parent Die Eltern-Quelle
	 * @param relPath Der relative Pfad von der Eltern-Quelle.
	 */
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
	
	/**
	 * Öffnet einen InputStream zur Quelle und gibt diesen zurück.
	 * @return InputStream aus der Quelle.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public InputStream getInputStream() throws IOException
	{
		if (inputStream != null)
			return inputStream;
		else if (url != null)
			return url.openStream();
		else
			throw new IOException("Invalid InputSource.");
	}

	/**
	 * Gibt den Name der Eingabequelle zurück.
	 * @return Name der Eingabequelle oder <code>null</code>, wenn die Quelle keinen Namen hat.
	 */
	public String getName()
	{
		if (file != null) return file.getName();
		
		return name;
	}

	/**
	 * Setzt den Namen der Qelle.
	 * @param name Neuer Name der Qelle.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gibt die URL zur Quelle zurück.
	 * @return URL zur Quelle oder <code>null</code>, wenn die Quelle keine URL hat.
	 */
	public URL getURL()
	{
		return url;
	}

	/**
	 * Gibt die Datei zur Quelle zurück.
	 * @return Datei zur Quelle oder <code>null</code>, wenn die Quelle keine Datei ist.
	 */
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
