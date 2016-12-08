package tws.xmod;

import java.io.IOException;
import java.util.Arrays;

public class XModException extends IOException
{
	private static final long serialVersionUID = 6643670630959371422L;

	public XModException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public XModException(String message)
	{
		super(message);
	}

	public XModException(Throwable cause)
	{
		super(cause);
	}

	XModException(String line, int pos, String message)
	{
		super(createMessage(line, pos, message));
	}
	
	private static String createMessage(String line, int pos, String message)
	{
		StringBuilder builder = new StringBuilder(256);
		builder.append(message);
		builder.append('\n').append(line.replace('\t', ' '));
		
		char[] chars = new char[pos+1];
		Arrays.fill(chars, ' ');
		chars[pos] = '^';
		
		builder.append('\n').append(chars);
		return builder.toString();
	}
}
