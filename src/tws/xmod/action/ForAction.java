package tws.xmod.action;

import java.util.Map;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

//XXX: Geändert
public class ForAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String each = Util.getRequiredDefaultAttribute(node, "var");
		
		Object obj = context.resolveExpression(each).asObject();
		
		if (obj instanceof Map)
		{
			iterateMap((Map) obj, node, context);
		}
		else if (obj instanceof Iterable)
		{
			iterateList((Iterable) obj, node, context);
		}
		else
		{
			String t = (obj != null) ? obj.getClass().getName() : "null";
			throw new XModException("Can not iterate over an Object of Type " + t);
		}
	}

	private void iterateMap(Map map, TagNode node, XModContext context) throws XModException
	{
		String as = node.getAttribut("as", "$1,$2");
		String[] vars = as.split(",");
		if (vars.length != 2) throw new XModException("Invalid format for Tag 'as': " + as);
		
		for (Object key : map.keySet())
		{
			context.setVariable(vars[0], key);
			context.setVariable(vars[1], map.get(key));

			Util.writeChildNodes(node, context);
		}
	}
	
	private void iterateList(Iterable itr, TagNode node, XModContext context) throws XModException
	{
		String as = node.getAttribut("as", "$1");
		for (Object e : itr)
		{
			context.setVariable(as, e);

			Util.writeChildNodes(node, context);
		}
	}
}
