package tws.xmod;

/**
 * @author TheWhiteShadow
 */
public class XModXMLParser extends AbstractXModParser
{
	public XModXMLParser() {}
	
	@Override
	protected void read(String namespace) throws XModException
	{
		String openPattern = namespace + ':';
		String closePattern = '/' + openPattern;
		String xModComment = "!--" + openPattern;
		
		char c;
		while ((c = getChar()) != 0)
		{
			if (c == '<')
			{
				if (match(pos + 1, openPattern))
				{
					if (start < pos)
					{
						addText(read(false));
						start = pos;
					}
					pos += openPattern.length()+1;
					parseTag(true);
					start = pos+1;
				}
				else if (match(pos + 1, closePattern))
				{
					if (start < pos)
					{
						addText(read(false));
						start = pos;
					}
					pos += closePattern.length()+1;
					parseTag(false);
					start = pos+1;
				}
				else if (match(pos + 1, xModComment))
				{
					if (start < pos)
					{
						addText(read(false));
						start = pos;
					}
					pos += xModComment.length()+1;
					pos = parseComment(pos);
					start = pos+1;
				}
				else if (match(pos + 1, "!--"))
				{
					start = pos;
					pos += 4;
					pos = parseComment(pos);
				}

			}
			else if (match(pos, "${"))
			{
				if (pos == 0 || getChar(pos - 1) != '\\')
				{
					addText(read(false));
					parseValue();
					start = pos;
				}
			}
			pos++;
		}
		
		if (start < pos-1)
		{
			addText(read(false));
		}

		if (nodeStack != rootNode)
			throwException(currentNode.getSourcePosition(), "Missing closing tag for '" + currentNode.getName() + "'");

		Util.removeEmptyTextNodes(rootNode);
	}
	
	private void parseValue() throws XModException
	{
		int end = indexOf("}", pos);
		if (end == -1) throw new XModException("Missing token '}'");

		String value = read(pos+2, end, false);
		newTag("print");
		addAttribut("exp", value);
		pos = end+1;
	}

	private int parseComment(int pos)
	{
		int end = indexOf("-->", pos);
		// if (end == -1): Fehlendes Kommentar Ende, aber wayne.
		return (end == -1) ? length() : end + 2;
	}

	private boolean match(int pos, String str) throws XModException
	{
		int m = 0;
		char c;
		while (m < str.length())
		{
			c = getChar(pos + m);
			
			if (c != str.charAt(m)) return false;
			m++;
		}
//		if (content.length() < pos + string.length()) return false;
//
//		for (int i = 0; i < string.length(); i++)
//		{
//			if (content.charAt(pos + i) != string.charAt(i)) return false;
//		}
		return str.length() == m;
	}

	private void parseTag(boolean open) throws XModException
	{
		String attName = null;
//		int inString = 0;
		boolean expectAttValue = false;

		char c;
		int nameStart = pos;
		while(isIdentifier(c = expectChar())) { pos++; }
		String tagName = read(nameStart, pos, false);
		if (tagName.isEmpty())
			throwException("XMod-Tag has no name.");
		
		if (open)
		{
			newTag(tagName);	
		}
		else
		{
			closeTag(tagName);
			c = nextSymbol();
			if (c != '>')
				throwException("Trash in close tag.");
			
			return;
		}
		
		start = pos;
		pos--;
		while(true)
		{
			c = getChar();
			
			if (c == 0)
				throwException("Unexpected end of file.");
			
			if (c == '"' || c == '\'')
			{
				String value = parseString(c, false);
				addAttribut(attName, value);
				attName = null;
				expectAttValue = false;
				start = pos + 1;
				continue;
				
//				if (((inString == 1 && c == '\'') || (inString == 2 && c == '\"')) && getChar(pos - 1) != '\\')
//				{
//					inString = 0;
//					addAttribut(attName, read(true));
//					attName = null;
//					expectAttValue = false;
//					start = pos + 1;
//				}
//				pos++;
//				continue;
			}

			if (isIdentifier(c))
			{
				pos++;
				continue;
			}
			
			if (c == '>')
			{
				openTag();
				break;
			}
			
			if (start < pos)
			{
				attName = read(false);
			}
			start = pos + 1;
			
			if (isWhiteSpace(c))
			{
				pos++;
				continue;
			}
			
//			if (c == '\'') inString = 1;
//			else if (c == '\"') inString = 2;
			if (c == '=' && attName != null) { expectAttValue = true; }
			else if (c == '/')
			{
				if (attName != null)
				{
					if (expectAttValue)
						throwException("Missing value for attribut '" + attName + "'");
					
					// Akzeptiere Attribute ohne Wert als "Flag gesetzt".
					addAttribut(attName, "true");
				}
				
				pos++;
				if (getChar() != '>')
						throwException("Missing token '>'");
				
				break;
			}
			else
				throw new XModException("Illegal character: " + c);
			
			pos++;
		}
	}
	
	@Override
	protected void addAttribut(String name, String value) throws XModException
	{
		super.addAttribut(name, resolveEntity(value));
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
 
	@Override
	protected boolean isIdentifier(char c)
	{
		return super.isIdentifier(c) || c == ':';
	}

	private void closeTag(String name) throws XModException
	{
		String currentName = ((TagNode) nodeStack).getName();
		if (!currentName.equals(name))
			throw new XModException("Missing Close-Tag for '" + currentName + "'.");
		
		super.closeTag();
	}
}