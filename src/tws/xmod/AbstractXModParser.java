package tws.xmod;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author TheWhiteShadow
 */
public abstract class AbstractXModParser implements XModParser
{
	protected Node nodeStack;
	protected TagNode currentNode;
	protected StringBuilder content;
	protected RootNode rootNode;
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
			this.rootNode = new RootNode(inputSource.getName());
			this.nodeStack = rootNode;
			read(context.getNamespace());
			return rootNode;
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
	
	public RootNode getRoot()
	{
		return rootNode;
	}

	abstract protected void read(String namespace) throws XModException;
	
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
		return c <= ' ';
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
//		System.out.print("Att:  ");
//		debugPositionInfo(start);
		
		if (!currentNode.addAttribut(name, value))
			throwException(currentNode.getSourcePosition(), "Dublicate attribute '" + name + "' for tag '" + currentNode.getName() + "'");
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
	
	protected void newTag(String name)
	{
//		System.out.print("nTag: ");
//		debugPositionInfo(start);
		currentNode = new TagNode(nodeStack, start, name);
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
			throwException("Parent tag is null.");
			
		nodeStack = node;
	}
	
	protected void addText(String text)
	{
//		System.out.print("Text: ");
//		debugPositionInfo(start);
		new TextNode(nodeStack, start, text);
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
		throwException(this.pos, message);
	}
	
	protected void throwException(int pos, String message) throws XModException
	{
//		System.out.print("Error: ");
//		debugPositionInfo(pos);
		
		int lStart = content.lastIndexOf("\n", pos);
		int lEnd = content.indexOf("\n", pos+1);
		if (lStart == -1) lStart = 0;
		if (lEnd == -1) lEnd = content.length();
		String line = content.substring(lStart, lEnd);
		
		throw new XModException(line, pos - lStart, message);
	}
	
	///DEBUG: Debugausgaben zur Nodeposition (Zeilenorientiert)
	@SuppressWarnings("unused")
	private void debugPositionInfo(int p)
	{
		int colStart = 0;
		int line = 1;
		char c;
		for (int i = 0; i < p; i++)
		{
			c = content.charAt(i);
			
			if (c == '\n')
			{
				colStart = i+1;
				line++;
			}
		}
		int column = p - colStart + 1;
		System.out.print("Zeile: " + String.format("%3d", line) + "; Spalte " + String.format("%3d", column) + ":\t");
		
		int start = p;//Math.max(p-5, 0);
		int ende = Math.min(p+10, content.length()-1);
		String str = content.substring(start, ende).replaceAll("\r", "").replaceAll("\n", "\\\\n");
		
		System.out.println(str);
//		System.out.println();
	}
}
