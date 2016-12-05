package tws.xmod;

public class Attribut
{
	private String name;
	private String value;
	
	public Attribut(String name, String value) throws XModException
	{
		this.name = name;
		this.value = resolveEntities(value);
	}
	
	private String resolveEntities(String str) throws XModException
	{
		StringBuilder builder = new StringBuilder(str);
		
		char c;
		for (int i = 0; i < builder.length(); i++)
		{
			c = builder.charAt(i);
			if (c == '&')
			{
				int p = builder.indexOf(";", i);
				if (p == -1) throw new XModException("Unterminated Entity");
				String entity = builder.substring(i+1, p);
				switch(entity)
				{
					case "amp": builder.replace(i, p+1, "&"); break;
					case "lt": builder.replace(i, p+1, "<"); break;
					case "gt": builder.replace(i, p+1, ">"); break;
				}
			}
		}
		return builder.toString();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return name + "=" + value;
	}
}
