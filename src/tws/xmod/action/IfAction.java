package tws.xmod.action;

import tws.xmod.Node;
import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class IfAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String exp = Util.getRequiredDefaultAttribute(node, "exp");
		
		boolean result = context.resolveExpression(exp).asBoolean();
		
		boolean elseBlock = false;
		for (Node child : node.getChildren())
		{
			if (child instanceof TagNode && ((TagNode) child).getName().equals("else"))
			{
				elseBlock = true;
			}
			else if (result != elseBlock) context.write(child);
		}
	}
}
