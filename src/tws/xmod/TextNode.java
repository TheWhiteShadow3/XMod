package tws.xmod;

public class TextNode extends Node
{
	private CharSequence text;
	
	TextNode(Node parent, int pos, CharSequence text)
	{
		super(parent, pos);
		this.text = text;
	}

	public String getText()
	{
		return text.toString();
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
