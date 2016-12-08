package tws.xmod;

public class RootNode extends Node
{
	private String filename;

	RootNode(String filename)
	{
		super(null, -1);
		this.filename = filename;
	}

	public String getFilename()
	{
		return filename;
	}

	@Override
	public Node getRoot()
	{
		return this;
	}
}
