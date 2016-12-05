package tws.xmod.action;

import java.io.IOException;

@Deprecated
public class TagException extends IOException
{
	private static final long serialVersionUID = 7804372009816997223L;

	public TagException()
	{
		super();
	}

	public TagException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TagException(String message)
	{
		super(message);
	}

	public TagException(Throwable cause)
	{
		super(cause);
	}
}
