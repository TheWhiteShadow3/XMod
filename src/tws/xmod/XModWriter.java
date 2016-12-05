package tws.xmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import tws.xmod.action.Action;
import tws.xmod.action.ActionProvider;


public class XModWriter implements AutoCloseable
{
	private OutputStreamWriter writer;
	private XModContext context;
	private RootNode lastRoot;
	
	public XModWriter(XModContext context, File file) throws FileNotFoundException
	{
		this(context, new FileOutputStream(file));
	}
	
	public XModWriter(XModContext context, OutputStream output)
	{
		this.context = context;
		this.writer = new OutputStreamWriter(output);
	}

	public void write(String text) throws XModException
	{
		try
		{
			writer.append(text);
		}
		catch (IOException e)
		{
			throw new XModException(e);
		}
	}

	public void write(Object obj) throws XModException
	{
		if (obj == null)
			write("null");
		else
			write(obj.toString());
	}
	
	public void write(Node node) throws XModException
	{
		if (node instanceof TextNode)
		{
			write(((TextNode) node).getText());
		}
		else if (node instanceof TagNode)
		{
			writeTag((TagNode) node);
		}
		else if (node instanceof RootNode)
		{
			RootNode parentRoot = lastRoot;
			try
			{
				this.lastRoot = (RootNode) node;
				context.setVariable(XModContext.VAR_FILE, lastRoot.getInputSource().getName());
				
				for (Node child : node.getChildren())
				{
					write(child);
				}
			}
			finally
			{
				this.lastRoot = parentRoot;
			}
		}
		else
		{
			throw new IllegalArgumentException("unexpected kind of Node: " + node.getClass().getName());
		}
	}
	
	private void writeTag(TagNode tagNode) throws XModException
	{
		String tagName = tagNode.getName();
		Action action = ActionProvider.getInstance().getAction(tagName);
		if (action == null)
			throw new XModException("No action for tag '" + tagName + "' was found.");
		
		try
		{
			action.execute(tagNode, context);
		}
		catch(XModException e)
		{
			System.err.println("\nDebug-Information:" +
					"\nSource:   " + lastRoot.getInputSource().getName() + 
					"\nNode:     " + tagName +
					"\nPosition: " + tagNode.getSourcePosition());
			throw e;
		}
		flush();
	}
	
	private void flush() throws XModException
	{
		try
		{
			writer.flush();
		}
		catch (IOException e)
		{
			throw new XModException(e);
		}
	}
	
	@Override
	public void close() throws IOException
	{
		writer.close();
	}
}
