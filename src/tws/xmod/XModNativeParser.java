package tws.xmod;

import tws.xmod.action.Action;
import tws.xmod.action.ActionProvider;

/**
 * @author TheWhiteShadow
 */
public class XModNativeParser extends AbstractXModParser
{
	@Override
	protected void read(String namespace) throws XModException
	{
		char c;
		while ((c = nextSymbol()) != 0)
		{
			if (c == '\r' || c == '\n')
			{
				pos++;
				continue;
			}
			if (c == '{')
			{
				pos++;
				openTag();
			}
			else if (c == '}')
			{
				pos++;
				closeTag();
			}
			else
			{
				String name = checkForIdentifier();
				if (name != null)
				{
					parseFunction(name);
				}
				else
				{
					throwException("Invalid statement.");
				}
			}
		}
	}

	private void parseFunction(String name) throws XModException
	{
		Action action = ActionProvider.getInstance().getAction(name);
		if (action != null)
		{
			newTag(name);
			parseArgument();
		}
		else
		{
			newTag("eval");
			String exp = parseExpression();
			addAttribut("exp", exp);
		}
	}

	private String parseExpression() throws XModException
	{
		char c;
		while ((c = nextSymbol()) != 0)
		{
			if (c == '"')
			{
				content.replace(pos, pos+1, "'");
				c = '\'';
			}
			
			if (c == '\'')
			{
				parseString('\'', true);
			}
			else if (c == '\r' || c == '\n')
			{
				break;
			}
			pos++;
		}
		
//		int start = start;content.lastIndexOf("\n", pos);
//		int end = content.indexOf("\n", pos);
//		if (end == -1) end = content.length() - 1;
//		
//		pos = end+1;
		return content.substring(start, pos);
	}

	private void parseArgument() throws XModException
	{
		boolean inBrace = false;
		String name = null;
		String value = null;
		
		char c;
		c = nextSymbol();
		
		if (c == '(')
		{
			pos++;
			inBrace = true;
		}
		
		start = pos;
		while ((c = nextSymbol()) != 0)
		{
//			name = checkForIdentifier();
//			if (name != null)
//			{
//				c = nextSymbol();
//				char tc =  inBrace ? ')' : '\n';
//				if (c == '=')
//				{
//					String value = parseValueUntil(',', tc);
//					if (value != null)
//					{
//						addAttribut(name, value);
//					}
//				}
//				else
//				{
//					addAttribut("-default", name + parseValueUntil(tc));
//				}
//				continue;
//			}
			
			if (c == '(' || c == '[' || c == '\'')
			{
				parseValueUntil(getEndChar(c));
				continue;
			}
			
			if (c == ',' || c == ')' || c == '\n' || c == '"')
			{
				if (name == null)
				{
					if (currentNode.getAttribut("-default") != null)
						throwException("Tag '" + currentNode.getName() + "' have already a default attribute.");
						
					name = "-default";
				}
				
				if (c == '"')
				{
					value = parseString(c, false);
					c = nextSymbol();
				}
				else
					value = content.substring(start, pos).trim();
				addAttribut(name, value);
				
				if (c == ',')
				{
					pos++;
					start = pos;
					continue;
				}
				else if (c == '=')
				{
					pos++;
					continue;
				}
				else if (c == ')')
				{
					if (!inBrace)
						throwException("Illegal character ')'");
					
					pos++;
					break;
				}
				else if (c == '\n')
				{
					if (inBrace)
						throwException("Missing ')'");
					
					pos++;
					break;
				}
			}
			if (c == '=')
			{
				name = content.substring(start, pos);
				pos++;
				start = pos;
				continue;
			}
//			if (c == ')')
//			{
//				if (!inBrace)
//					throwException("Illegal character ')'");
//					
//				inBrace = false;
//				pos++;
//				break;
//			}
//			if (c == '\n' && !inBrace)
//			{
//				pos++;
//				break;
//			}
			pos++;
		}
	}

	protected void parseValueUntil(char endChar) throws XModException
	{
		pos++;
		
		char c;
		while ((c = nextSymbol()) != 0)
		{
			if (c == endChar) break;
			
			if (c == '"' || c == '\'')
			{
				parseString(c, true);
			}
			if (c == '[')
			{
				parseValueUntil(']');
			}
			if (c == '(')
			{
				parseValueUntil(')');
			}
			pos++;
		}
		pos++;
	}
	
	@Override
	protected boolean isWhiteSpace(char c)
	{
		return c == ' ' || c == '\t';
	}
}
