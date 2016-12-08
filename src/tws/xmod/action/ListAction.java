package tws.xmod.action;

import java.util.List;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class ListAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String var = Util.getRequiredDefaultAttribute(node, "var");

		List list = context.resolveExpression(var).asList();
		writeList(node, list, context);
		
//		else throw new IOException("Attribut 'var' must point to an Array or List. Name: " + var);
	}
	
	private void writeList(TagNode node, List list, XModContext context) throws XModException
	{
		String seperator = node.getAttribut("seperator", " ");
		String start = node.getAttribut("start", "");
		String end = node.getAttribut("end", "");
		
		context.write(Util.listToString(list, start, seperator, end));
	}
}
