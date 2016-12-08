package tws.xmod.action;

import tws.xmod.TagNode;
import tws.xmod.XModContext;
import tws.xmod.XModException;

/**
 * Definiert eine Aktion in XMod.
 * Aktionen haben Zugriff auf den eingelesenen XMod-Baum und können Text ausgeben oder weitere Aktionen aufrufen.
 * @author TheWhiteShadow
 */
public interface Action
{
	/**
	 * Wird beim auslösen der Aktion aufgerufen.
	 * @param node XMod-Knoten, der die Aktion enthält.
	 * @param context Kontext des Dokuments.
	 * @throws XModException Wird geworfen, wenn bei der Aktion ein Fehler auftritt.
	 */
	public void execute(TagNode node, XModContext context) throws XModException;
}
