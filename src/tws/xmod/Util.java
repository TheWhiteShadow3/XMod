package tws.xmod;

import java.util.List;
import java.util.Set;

//XXX: Geändert
/**
 * Werkzeugklasse für Tags.
 * @author TheWhiteShadow
 */
public class Util
{
	private Util() {}
	
	/**
	 * Gibt den Wert eines Attributs des angegebenen Knotens zurück.
	 * Wirft eine fehlermeldung, wenn das Attribut nicht existiert.
	 * @param node Knoten, der das Attribut enthält.
	 * @param name Name des Attributs.
	 * @return Wert des Attributs.
	 * @throws XModException Wenn das Attribut nicht existiert.
	 */
	public static String getRequiredNamedAttribute(TagNode node, String name) throws XModException
	{
		String att = node.getAttribut(name);
		if (att == null)
			throw new XModException("Missing attribute '" + name + "' for Tag " + node.getName() + ".");

		return att;
	}
	
	/**
	 * Gibt den Wert eines Attributs des angegebenen Knotens zurück.
	 * Wirft eine fehlermeldung, wenn das Attribut nicht existiert.
	 * @param node Knoten, der das Attribut enthält.
	 * @param name Name des Attributs.
	 * @return Wert des Attributs.
	 * @throws XModException Wenn das Attribut nicht existiert.
	 */
	public static String getRequiredDefaultAttribute(TagNode node, String name) throws XModException
	{
		String att = node.getAttribut(name);
		if (att == null)
			att = node.getAttribut("-default");
		if (att == null)
			throw new XModException("Missing attribute '" + name + "' for Tag " + node.getName() + ".");

		return att;
	}

	/**
	 * Gibt den Namen eines einzigen Attributs des angegebenen Knotens zurück.
	 * Wirft eine fehlermeldung, wenn kein oder mehrere Attribute existieren.
	 * @param node Knoten, der das Attribut enthält.
	 * @return Name des Attributs.
	 * @throws XModException Wenn der Knoten kein oder merh als ein Attribut hat.
	 */
	public static String getExpectedSingleAttributeName(TagNode node) throws XModException
	{
		Set<String> set = node.getAttributes().keySet();
		if (set.size() != 1)
			throw new XModException("Tag " + node.getName() + " expect only one attribute.");
		
		return set.iterator().next();
	}
	
	
	/**
	 * Überprüft, ob ein Knoten genau ein Attribut aus der angegebenen Liste besitzt.
	 * Wirft eine fehlermeldung, wenn der Knoten weniger oder mehr Attribute aus der Liste gesetzt hat.
	 * @param node Knoten, der das Attribut enthält.
	 * @param names Namen der Attribute.
	 * @param values Liste der Attributwerten.
	 * @throws XModException Wenn der Knoten weniger oder mehr als ein Attribut aus der Liste gesetzt hat.
	 */
	public static void oneOfTheArguments(TagNode node, String names, String... values) throws XModException
	{
		if (values.length == 0)
			throw new IllegalArgumentException("Invalid argument count.");
		
		String tag = null;
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] != null)
			{
				if (tag != null)
					onlyOneArgument(node, names);
				tag = values[i];
			}
		}
		if (tag == null) onlyOneArgument(node, names);
	}

	private static void onlyOneArgument(TagNode node, String names) throws XModException
	{
		throw new XModException("Only one of the Attributes " + names + " can used for Tag " + node.getName() + ".");
	}
	
	/**
	 * Entfernt leere Text-Knoten unterhalb des übergebenene Knotens rekursiv.
	 * @param node Wurzel-Knoten, ab dem gesucht werden soll.
	 * @return true, wenn der übergebene Knoten selbst ein leerer Textknoten ist.
	 */
	public static boolean removeEmptyTextNodes(Node node)
	{
		if (node instanceof TextNode)
		{
			String str = ((TextNode) node).getText().trim();
			if (str.isEmpty()) return true;
		}
		else
		{
			List<Node> children = node.children();
			if (children != null) for (int i = children.size() -1; i >= 0; i--)
			{
				if ( removeEmptyTextNodes(children.get(i)) ) children.remove(i);
			}
		}
		return false;
	}

	/**
	 * Verarbeitet alle Kindknoten.
	 * @param parent Der Knoten.
	 * @param context Der Kontext.
	 * @throws XModException Wenn Fehler beim Schreiben auftreten.
	 */
	public static void writeChildNodes(Node parent, XModContext context) throws XModException
	{
		List<Node> children = parent.getChildren();
		for(int i = 0; i < children.size(); i++)
		{
			context.write(children.get(i));
		}
	}
	
	/**
	 * 
	 * @param list
	 * @param seperator
	 */
	public static String listToString(List<?> list, String seperator)
	{
		return listToString(list, "", seperator, "");
	}
	
	public static String listToString(List<?> list, String start, String seperator, String end)
	{
		StringBuilder builder = new StringBuilder(list.size() * 8);
		builder.append(start);
		for (int i = 0; i < list.size(); i++)
		{
			if (i > 0) builder.append(seperator);
			builder.append(list.get(i).toString());
		}
		builder.append(end);
		return builder.toString();
	}
}
