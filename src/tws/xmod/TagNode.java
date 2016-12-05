package tws.xmod;

import java.util.ArrayList;
import java.util.List;


public class TagNode extends Node
{
	private String name;
	private final List<Attribut> attributes = new ArrayList<Attribut>(4);
	
	TagNode(Node parent, int pos, String name)
	{
		super(parent, pos);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public List<Attribut> getAttributes()
	{
		return attributes;
	}
	
	public String getAttribut(String name, String def)
	{
		String result = getAttribut(name);
		if (result == null) return def;
		return result;
	}
	
	public String getAttribut(String name)
	{
		if (name == null) throw new NullPointerException("name is null");
		
		for(int i = 0; i < attributes.size(); i++)
		{
			Attribut att = attributes.get(i);
			if ( name.equals(att.getName()) ) return att.getValue();
		}
		return null;
	}

	public void addAttribut(Attribut att)
	{
		if (att == null) throw new NullPointerException("att is null");
		
		attributes.add(att);
	}

	@Override
	public String toString()
	{
		if (hasChildren())
		{
			StringBuilder builder = new StringBuilder(128);
			builder.append('<').append(name).append(' ').append(attributes).append(">\n");
			for(Node child : getChildren())
			{
				builder.append('\t').append(child).append('\n');
			}
			builder.append("</").append(name).append(' ').append('>');
			return builder.toString();
		}
		else
			return "<" + name + " " + attributes + " />";
	}
}
