package tws.xmod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TagNode extends Node
{
	static final String DEFAULT_ATTRIBUT_NAME = "-default";
	
	private String name;
	private final Map<String, String> attributes = new HashMap<String, String>(4);
	
	TagNode(Node parent, int pos, String name)
	{
		super(parent, pos);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, String> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}
	
	public int getAttributCount()
	{
		return attributes.size();
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
		
		return attributes.get(name);
	}
	
	public String getDefaultAttribut()
	{
		return attributes.get(DEFAULT_ATTRIBUT_NAME);
	}

	public boolean addAttribut(String name, String value)
	{
		if (name == null) throw new NullPointerException("Attribute name can not be null");
		if (value == null) throw new NullPointerException("Attribute value can not be null");
		
		if (attributes.containsKey(name)) return false;
		
		attributes.put(name, value);
		return true;
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
