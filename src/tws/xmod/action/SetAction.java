package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class SetAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String var;
		String value;
		
		if (node.getAttributCount() == 1)
		{
			var = Util.getExpectedSingleAttributeName(node);
			value = node.getAttribut(var);
		}
		else
		{
			var = Util.getRequiredNamedAttribute(node, "var");
			value = Util.getRequiredNamedAttribute(node, "value");
		}
		
		context.resolveExpression(var + ":=" + value);
	}
}
