package tws.xmod;

import java.io.IOException;
import java.io.InputStreamReader;

//XXX: Neu!
public abstract class AbstractXModParser implements XModParser
{
	protected Node nodeStack;
	protected TagNode currentNode;
	protected StringBuilder content;
	protected int start;
	protected int pos;
	
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
			this.content = builder;
			this.pos = 0;
			this.start = pos;
			this.nodeStack = new RootNode(inputSource.getName());
			return read(inputSource.getName(), context.getNamespace());
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

	abstract protected RootNode read(String name, String namespace) throws XModException;
	
	protected char nextSymbol() throws XModException
	{
		char c = 0;
		while (pos < content.length())
		{
			c = content.charAt(pos);
			if (!isWhiteSpace(c)) break;
			pos++;
		}
			
		if (pos >= content.length()) return 0;
//			throwException("Unexpected end of file.", pos);
		
		return c;
	}
	
	protected boolean isWhiteSpace(char c)
	{
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
 
	protected boolean isIdentifier(char c)
	{
		return (c > 47 && c < 58)       // Zahl
				|| (c > 64 && c < 91)   // Groß A-Z
				|| (c > 96 && c < 123)  // Klein a-z
				|| (c == '_') || (c == '.') || (c == '-'); // Sonderzeichen
	}
	
	protected String checkForIdentifier() throws XModException
	{
		char c = content.charAt(pos);
		if (c == '"' || c == '\'')
		{
			return parseString(c, false);
		}
		
		while (isWhiteSpace(c))
		{
			pos++;
			if (pos < content.length()) return null;
			c = content.charAt(pos);
		}
		
		if (isIdentifier(c))
		{
			start = pos;
			while(content.length() > ++pos && isIdentifier(content.charAt(pos)));
			
			if (content.length() <= pos);
				return content.substring(start, pos);
		}
		return null;
	}
	
	protected void addAttribut(String name, String value) throws XModException
	{
		if (!currentNode.addAttribut(name, value))
			throwException("Dublicate attribute '" + name + "' for node '" + currentNode.getName() + "'");
	}
	
	protected boolean match(String string)
	{
		if (content.length() < pos + string.length()) return false;

		for (int i = 0; i < string.length(); i++)
		{
			if (content.charAt(pos + i) != string.charAt(i)) return false;
		}
		return true;
	}
	
	protected String parseString(char endChar, boolean include) throws XModException
	{
		int pos = this.pos;
		do
		{
			pos = content.indexOf(String.valueOf(endChar), pos+1);
			
			if (pos == -1)
				throw new XModException("Missing string terminator '" + endChar + "'");
		}
		while (content.charAt(pos-1) == '\\');

		String name;
		if (include)
			name = content.substring(this.pos, pos+1);
		else
			name = content.substring(this.pos+1, pos);
		this.pos = pos+1;
		return name;
	}
	
//	protected String parseValueUntil(char... stops) throws XModException
//	{
//		int start = pos;
//		
//		char c;
//		while ((c = nextSymbol()) != 0)
//		{
//			for(int i = 0; i < stops.length; i++)
//			{
//				if (c == stops[i]) break;
//			}
//			
//			if (c == '"' || c == '\'')
//			{
//				parseString(c, true);
//			}
//			if (c == '[')
//			{
//				parseString(']', true);
//			}
//			if (c == '(')
//			{
//				parseString(')', true);
//			}
//			pos++;
//		}
//		
//		// Für den Zugriff auf den Terminierer.
//		pos--;
//		return content.substring(start, pos).trim();
//	}
	
	protected void newTag(String name)
	{
		currentNode = new TagNode(nodeStack, pos, name);
	}
	
	protected void openTag()
	{
//		System.out.println("open");
		
		nodeStack = currentNode;
		currentNode = null;
	}
	
	protected void closeTag() throws XModException
	{
//		System.out.println("close");
		
		Node node = nodeStack.getParent();
		if (node == null)
			throwException("Parent node is null.");
			
		nodeStack = node;
	}
	
	protected void addText(String text)
	{
		new TextNode(nodeStack, pos, text);
	}
	
	protected char getEndChar(char startChar)
	{
		switch(startChar)
		{
			case '"': return '"';
			case '\'': return '\'';
			case '(': return ')';
			case '[': return ']';
			case '{': return '}';
			default:
				throw new IllegalArgumentException("illegal Sequence character.");
		}
	}
	
	protected void throwException(String message) throws XModException
	{
		int lStart = content.lastIndexOf("\n", pos);
		int lEnd = content.indexOf("\n", pos+1);
		if (lStart == -1) lStart = 0;
		if (lEnd == -1) lEnd = content.length();
		String line = content.substring(lStart, lEnd);
		
		throw new XModException(line, pos - lStart, message);
	}
}
