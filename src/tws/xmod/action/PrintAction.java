package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XModContext;
import tws.xmod.XModException;

//XXX: Geändert
public class PrintAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String exp = node.getAttribut("exp");
		if (exp == null)
			exp = node.getDefaultAttribut();
		String value = node.getAttribut("value");
		
		Util.oneOfTheArguments(node, "exp, value",  exp, value); 
		
		if (exp != null)
		{
			context.write( format(node, context.resolveExpression(exp).asObject()) );
		}
		else
		{
			context.write(format(node, value));
		}
	}
	
	private String format(TagNode node, Object value)
	{
		String format = node.getAttribut("format");
		if (format != null)
			return String.format(format, value);
		else if (value != null)
			return value.toString();
		else
			return "";
	}
}
