package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.XModContext;
import tws.xmod.XModException;

public interface Action
{
	/**
	 * execute-methode
	 * @param node
	 * @param context
	 * @throws XModException
	 */
	public void execute(TagNode node, XModContext context) throws XModException;
}
