package tws.xmod;

@Deprecated
public class Attribut
{
	private String name;
	private String value;
	
	public Attribut(String name, String value) throws XModException
	{
		this.name = name;
		this.value = value;
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
