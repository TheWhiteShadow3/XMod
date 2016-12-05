package tws.xmod.action;

import java.util.HashMap;

public class ActionProvider
{
	private static final HashMap<String, Action> actions = new HashMap<String, Action>();
	
	static
	{
		actions.put("print", new PrintAction());
		actions.put("if", new IfAction());
		actions.put("for", new ForAction());
		actions.put("while", new WhileAction());
		actions.put("set", new SetAction());
		actions.put("list", new ListAction());
		actions.put("htmllist", new HTMLListAction());
		actions.put("include", new IncludeAction());
	}

	private static final ActionProvider instance = new ActionProvider();
	
	public static ActionProvider getInstance()
	{
		return instance;
	}
	
	private ActionProvider() {}
	
	public boolean addAction(String name, Action action)
	{
		if (action == null) throw new NullPointerException();
		if (actions.containsKey(name)) return false;
		
		actions.put(name, action);
		return true;
	}
	
	public boolean removeAction(String name)
	{
		return actions.remove(name) != null;
	}

	public Action getAction(String name)
	{
		return actions.get(name);
	}
}
