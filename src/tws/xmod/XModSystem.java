package tws.xmod;

import java.util.HashMap;
import java.util.Map;

import tws.expression.Argument;
import tws.expression.Config;
import tws.expression.DefaultInvoker;
import tws.expression.EvaluationException;
import tws.expression.Expression;
import tws.expression.Operation;
import tws.expression.Resolver;

//XXX: Geändert
public class XModSystem implements XModContext, XModConfig
{
	private String namespace = "xmod";

	private InputSource inputSource;
	private XModWriter writer;
	private ExpressionHandler expressionHandler;
	
	XModSystem()
	{
		this.expressionHandler = new ExpressionHandler();
	}
	
	void setWriter(XModWriter writer)
	{
		this.writer = writer;
	}

	@Override
	public String getNamespace()
	{
		return namespace;
	}

	@Override
	public void setNamespace(String namespace)
	{
		if (namespace == null || namespace.isEmpty()) throw new IllegalArgumentException();
		this.namespace = namespace;
	}
	
	@Override
	public Config getExpressionConfig()
	{
		return expressionHandler.expConfig;
	}

	@Override
	public InputSource getInputSource()
	{
		return inputSource;
	}

	public void setInputSource(InputSource inputSource)
	{
		this.inputSource = inputSource;
	}
	
	@Override
	public Object getVariable(String name) throws EvaluationException
	{
		return expressionHandler.getVariable(name);
	}

	@Override
	public void setVariable(String name, Object value)
	{
		expressionHandler.setVariable(name, value);
	}
	
	@Override
	public void unsetVariable(String name)
	{
		expressionHandler.unsetVariable(name);
	}
	
	@Override
	public void setAlias(String name, String alias)
	{
		expressionHandler.setAlias(name, alias);
	}
	
	@Override
	public String resolveAlias(String alias)
	{
		return expressionHandler.resolveAlias(alias);
	}

	@Override
	public void write(String text) throws XModException
	{
		writer.write(text);
	}

	@Override
	public void write(Object obj) throws XModException
	{
		writer.write(obj);
	}

	@Override
	public void write(Node node) throws XModException
	{
		writer.write(node);
	}

	@Override
	public Object resolveValue(String value) throws XModException
	{
		if (isWrappedExpression(value))
		{
			return resolveExpression(value.substring(1, value.length()-1)).asObject();
		}
		return value;
	}
	
	@Override
	public Object resolveName(String name) throws XModException
	{
		if (isWrappedExpression(name))
		{
			return resolveExpression(name.substring(1, name.length()-1)).asObject();
		}
		return expressionHandler.resolve(name, null);
	}

	@Override
	public Argument resolveExpression(String exp) throws XModException
	{
		if (isWrappedExpression(exp))
			exp = exp.substring(1, exp.length()-1);
		
		return expressionHandler.resolveExpression(exp);
	}
	
	private boolean isWrappedExpression(String str)
	{
		return (str.length() > 2 && str.charAt(0) == '{' && str.charAt(str.length()-1) == '}');
	}

	private static class ExpressionHandler extends DefaultInvoker implements Resolver
	{
		public static final Map<String, Operation> EXPRESSION_CACHE = new HashMap<String, Operation>();
		
		private final Map<String, Object> variables = new HashMap<String, Object>();
		private final Map<String, String> aliases = new HashMap<String, String>();
		
		private final Config expConfig = new Config();
		
		private ExpressionHandler()
		{
			this.expConfig.nullCast = Config.NullCast.TO_EMPTY_STRING;
			this.expConfig.resolver = this;
			this.expConfig.invoker = this;
		}
		
		public Argument resolveExpression(String exp) throws XModException
		{

			Operation op = EXPRESSION_CACHE.get(exp);
			if (op == null)
			{
				op = new Expression(exp, expConfig).compile();
				EXPRESSION_CACHE.put(exp, op);
			}
			return op.resolve();
		}

		@Override
		public Object invoke(Argument reciever, String name, Argument[] args) throws Exception
		{
			// Ersetze Feldaufrufe durch Methodenaufrufe ohne Argumente.
			if (args == null) args = new Argument[0];
			
			String alias = aliases.get(name);
			if (alias != null) try
			{
				return invoke(reciever, alias, args);
			}
			catch(EvaluationException e) {}
			
			return super.invoke(reciever, name, args);
		}

		@Override
		public Object resolve(String name, Argument[] args) throws EvaluationException
		{
			return getVariable(name);
		}

		@Override
		public void assign(String name, Argument arg) throws EvaluationException
		{
			setVariable(name, arg.asObject());
		}
		
		public Object getVariable(String name) throws EvaluationException
		{
			Object result = variables.get(name);
			if (result == null && !variables.containsKey(name))
				throw new EvaluationException("Variable '" + name + "' does not exists.");
			return result;
		}

		public void setVariable(String name, Object value)
		{
			variables.put(name, value);
		}
		
		public void unsetVariable(String name)
		{
			variables.remove(name);
		}
		
		public void setAlias(String name, String alias)
		{
			if (alias == null)
				aliases.remove(name);
			else
				aliases.put(alias, name);
		}
		
		public String resolveAlias(String alias)
		{
			return aliases.get(alias);
		}
	}

//	@Override
//	public Action getActionForName(String name) throws XModException
//	{
//		Action action = actionProvider.getAction(name);
//		if (action == null)
//			throw new XModException("No Action for tag '" + name + "' was found.");
//		
//		return action;
//	}
}
