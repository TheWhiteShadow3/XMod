package tws.xmod;

//XXX: Geändert
public class TextNode extends Node
{
	private String text;
	
	TextNode(Node parent, int pos, String text)
	{
		super(parent, pos);
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	@Override
	protected void addChild(Node child)
	{
		throw new UnsupportedOperationException("Can not add child to TextNode");
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return "'" + text + "'";
	}
}
