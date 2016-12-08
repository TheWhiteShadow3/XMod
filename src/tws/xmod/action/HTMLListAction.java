package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class HTMLListAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String var = Util.getRequiredDefaultAttribute(node, "var");

		Object list = context.resolveExpression(var).asObject();
		if (!(list instanceof Iterable))
			throw new XModException("Attribut 'var' must point to a Iterable. Name: " + var);
		
		String listtag = node.getAttribut("listtag", "ul");
		String itemtag = node.getAttribut("itemtag", "li");
		String clazz = node.getAttribut("class");
		
		context.write("<");
		context.write(listtag);
		
		if (clazz != null)
		{
			context.write(" class=\"");
			context.write(clazz);
			context.write("\"");
		}
		context.write(">\n");
		for(Object e : (Iterable) list)
		{
			context.write("\t<");
			context.write(itemtag);
			context.write(">");
			context.write(e);
			context.write("</");
			context.write(itemtag);
			context.write(">\n");
		}
		
		context.write("</");
		context.write(listtag);
		context.write(">");
	}
}
