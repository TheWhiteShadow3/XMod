package tws.xmod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node
{
	private Node parent;
	private int sourcePos;
	private Node root;
	private List<Node> children;
	
	Node(Node parent, int sourcePos)
	{
		this.parent = parent;
		this.sourcePos = sourcePos;
		if (parent != null)
		{
			parent.addChild(this);
			this.root = (parent.parent == null) ? parent : parent.root;
		}
	}
	
	protected void addChild(Node child)
	{
		if (children == null) children = new ArrayList<Node>(8);
		children.add(child);
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public int getSourcePosition()
	{
		return sourcePos;
	}

	public Node getRoot()
	{
		return root;
	}
	
	protected List<Node> children()
	{
		return children;
	}
	
	public List<Node> getChildren()
	{
		if (children == null) return Collections.EMPTY_LIST;
		
		return Collections.unmodifiableList(children);
	}
	
	public boolean hasChildren()
	{
		return children != null && children.size() > 0;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + children;
	}
}
