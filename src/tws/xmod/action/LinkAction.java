package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public class LinkAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String href = Util.getRequiredAttribute(node, "href");
		String value = node.getAttribut("text", href);
		String htmlClass = node.getAttribut("class");
		
		StringBuilder builder = new StringBuilder();
		builder.append("<a ");
		if (htmlClass != null)
			builder.append("class=\"").append(htmlClass).append("\" ");
		
		builder.append("href=\"").append(href).append("\" ");
		builder.append(">").append(value).append("</a>");
		
		context.write(builder.toString());
	}
}
