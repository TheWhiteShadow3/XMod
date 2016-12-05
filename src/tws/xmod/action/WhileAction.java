package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class WhileAction implements Action
{
	private static final int MAX_LOOPS = 500;
	
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String exp = Util.getRequiredAttribute(node, "exp");
		
		int loops = 0;
		while (context.resolveExpression(exp).asBoolean())
		{
			if (loops == MAX_LOOPS) throw new XModException("Infinite loop detected.");
			
			Util.writeChildNodes(node, context);
			loops++;
		}
	}
}
