package tws.xmod.action;

import tws.xmod.InputSource;
import tws.xmod.TagNode;
import tws.xmod.Util;
import tws.xmod.XMod;
import tws.xmod.XModContext;
import tws.xmod.XModException;

//XXX: Geändert
public class IncludeAction implements Action
{
	@Override
	public void execute(TagNode node, XModContext context) throws XModException
	{
		String urlName = Util.getRequiredDefaultAttribute(node, "url");
		
		InputSource inputSource = new InputSource(context.getInputSource(), urlName);
		context.write(XMod.findParser(inputSource).read(inputSource, context));
	}
}
