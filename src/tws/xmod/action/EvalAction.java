package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class EvalAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String exp = Util.getRequiredDefaultAttribute(node, "exp");
		
		context.setVariable("?", context.resolveExpression(exp).asObject()); 
	}
}