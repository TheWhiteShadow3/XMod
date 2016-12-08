package tws.xmod;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author TheWhiteShadow
 */
public class XModXMLParser implements XModParser
{
	private StringBuilder content;
	private RootNode root;
	private Node currentNode;

	public XModXMLParser() {}

	public RootNode getRoot()
	{
		return root;
	}
	
	@Override
	public RootNode read(InputSource inputSource, XModContext context) throws XModException
	{
		try(InputStreamReader reader = new InputStreamReader(inputSource.getInputStream()))
		{
			StringBuilder builder = new StringBuilder(1 << 12); // 4k
			
			char[] buffer = new char[builder.capacity()];
			int count;
			while ((count = reader.read(buffer)) > 0)
			{
				builder.append(buffer, 0, count);
			}
			return read(builder, inputSource.getName(), context.getNamespace());
		}
		catch(XModException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw new XModException(e);
		}
	}
	
	private RootNode read(StringBuilder content, String filename, String namespace) throws XModException
	{
		this.content = content;
		this.root = new RootNode(filename);
		this.currentNode = root;
		String pattern = namespace + ':';
		
		int pos = 0;
		int start = pos;
		char c;
		while (pos < content.length())
		{
			c = content.charAt(pos);
			
			if (c == '<')
			{
				if (match(pos + 1, "!--"))
				{
					start = pos;
					pos += 4;
					pos = parseComment(pos);
				}
				else if (match(pos + 1, pattern))
				{
					if (start < pos)
					{
						addText(start, content.substring(start, pos));
					}
					pos = parseTag(pos + pattern.length()+1, true);
					start = pos;
				}
				else if (match(pos + 1, '/' + pattern))
				{
					if (start < pos)
					{
						addText(start, content.substring(start, pos));
					}
					pos = parseTag(pos + pattern.length()+2, false);
					start = pos;
				}
				
			}
			else if (match(pos, "${"))
			{
				if (pos > 0 && content.charAt(pos-1) == '\\')
				{
					addText(start, content.substring(start, pos-1));
				}
				else
				{
					addText(start, content.substring(start, pos));
					pos = parseValue(pos);
				}
				start = pos;
			}
			pos++;
		}
		
		if (start < pos-1)
		{
			addText(start, content.substring(start, pos-1));
		}

		if (currentNode != root)
			throw new XModException("Missing closing tag for '" + ((TagNode) currentNode).getName() + "'");

		Util.removeEmptyTextNodes(root);

		return root;
	}
	
	private int parseValue(int pos) throws XModException
	{
		int end = content.indexOf("}", pos);
		if (end == -1) throw new XModException("Missing token '}'");

		String value = content.substring(pos+2, end);
		TagNode node = new TagNode(currentNode, pos, "print");
		addAttribut(node, pos, "exp", value);
		
		return end+1;
	}

	private int parseComment(int pos)
	{
		int end = content.indexOf("-->", pos);
		// if (end == -1): Fehlendes Kommentar Ende, aber wayne.
		return (end == -1) ? content.length() : end + 2;
	}

	private boolean match(int pos, String string)
	{
		if (content.length() < pos + string.length()) return false;

		for (int i = 0; i < string.length(); i++)
		{
			if (content.charAt(pos + i) != string.charAt(i)) return false;
		}
		return true;
	}

	private int parseTag(int start, boolean open) throws XModException
	{
		String attName = null;
		int inString = 0;

		char c;
		int pos = start;
		while(isIdentifier(c = nextChar(pos))) { pos++; }
		String tagName = content.substring(start, pos);
		if (tagName.isEmpty())
			throwException("XMod-Tag has no name.", pos);
		
		if (open)
		{
			openTag(start, tagName);
//			node = (TagNode) this.currentNode;
		}
		else
		{
//			node = (TagNode) this.currentNode;
			closeTag(tagName);
		}
		
		start = pos;
		pos--;
		while((c = nextChar(++pos)) != '>' || inString != 0)
		{
			if (inString > 0)
			{
				if (((inString == 1 && c == '\'') || (inString == 2 && c == '\"')) && content.charAt(pos - 1) != '\\')
				{
					inString = 0;
					addAttribut(currentNode, pos, attName, content.substring(start, pos));
					attName = null;
					start = pos + 1;
				}
				continue;
			}

			if (isIdentifier(c)) continue;
			
			if (start < pos)
			{
				attName = content.substring(start, pos);
			}
			start = pos + 1;
			
			if (isWhiteSpace(c)) continue;

//			if (c == '&')
//			{
//				int p = content.indexOf(";", pos);
//				if (p == -1) throw new XModException("Unterminated Entity after '" + token + "'");
//				String entity = content.substring(pos+1, p);
//				switch(entity)
//				{
//					case "amp": 
//				}
//			}
			
			if (!open) throwException("Unexpected character '" + c + "'", pos);

			if (c == '\'') inString = 1;
			else if (c == '\"') inString = 2;
			else if (c == '=' && attName != null) continue;
			else if (c == '/')
			{
				if (attName != null)
				{
					if (content.charAt(pos - 1) == '=')
						throwException("Missing value for attribut '" + attName + "'", pos);
					
					// Akzeptiere Attribute ohne Wert als "Flag gesetzt".
					addAttribut(currentNode, pos, attName, "true");
				}
				
				if (nextChar(++pos) != '>') throwException("Missing token '>'", pos);

				closeTag(tagName);
				break;
			}
			else
				throw new XModException("Illegal character: " + c);
		}
		return pos+1;
	}
	
	private void addAttribut(Node node, int pos, String name, String value) throws XModException
	{
		if (!((TagNode) node).addAttribut(name, resolveEntity(value)))
			throwException("Dublicate attribute '" + name + "'", pos);
	}
	
	private String resolveEntity(String rawString) throws XModException
	{
		StringBuilder builder = new StringBuilder(rawString.length());
		
		int start = 0;
		char c;
		for (int pos = 0; pos < rawString.length(); pos++)
		{
			c = rawString.charAt(pos);
			if (c == '&')
			{
				builder.append(rawString.substring(start, pos));
				
				start = pos+1;
				pos = rawString.indexOf(";", pos);
				if (pos == -1) throw new XModException("Unterminated Entity");
				String entity = rawString.substring(start, pos);
				switch(entity)
				{
					case "amp": builder.append('&'); break;
					case "lt": builder.append('<'); break;
					case "gt": builder.append('>'); break;
					case "quot": builder.append('"'); break;
					case "apos": builder.append('\''); break;
					default:
						builder.append(rawString.substring(start, pos));
				}
				start = pos+1;
			}
		}
		builder.append(rawString.substring(start, rawString.length()));
		return builder.toString();
	}

	private boolean isWhiteSpace(char c)
	{
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
 
	private boolean isIdentifier(char c)
	{
		return (c > 47 && c < 58)       // Zahl
				|| (c > 64 && c < 91)   // Groß A-Z
				|| (c > 96 && c < 123)  // Klein a-z
				|| (c == '_') || (c == ':') || (c == '.') || (c == '-'); // Sonderzeichen
	}
	
	protected void openTag(int pos, String name)
	{
		// System.out.println("Neuer Knoten: " + name);
		currentNode = new TagNode(currentNode, pos, name);
	}

	protected void addText(int pos, String text)
	{
		// System.out.println("Füge Text hinzu [[" + text + "]]");
		new TextNode(currentNode, pos, text);
	}

//	protected void addAttribut(Attribut att)
//	{
//		// System.out.println("Füge Attribut hinzu: " + att);
//		((TagNode) currentNode).getAttributes().add(att);
//	}

	protected void closeTag(String name) throws XModException
	{
		String currentName = ((TagNode) currentNode).getName();

		if (!currentName.equals(name)) throw new XModException("Missing Close-Tag for '" + currentName + "'.");

		// System.out.println("Schließe Knoten: " + name);
		currentNode = currentNode.getParent();
	}
	
	private char nextChar(int pos) throws XModException
	{
		if (pos >= content.length()) throwException("Unexpected end of file.", pos);
		return content.charAt(pos);
	}
	
	private void throwException(String message, int pos) throws XModException
	{
		int lStart = content.lastIndexOf("\n", pos);
		int lEnd = content.indexOf("\n", pos);
		if (lStart == -1) lStart = 0;
		if (lEnd == -1) lEnd = content.length();
		String line = content.substring(lStart, lEnd);
		
		throw new XModException(line, pos - lStart, message);
	}
}