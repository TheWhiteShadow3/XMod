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
		String var = Util.getRequiredAttribute(node, "var");
		String value = Util.getRequiredAttribute(node, "value");
		
		context.resolveExpression(var + ":=" + value);
	}
}