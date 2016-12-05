package tws.xmod;


public class RootNode extends Node
{
	private InputSource inputSource;
	
	RootNode(InputSource inputSource)
	{
		super(null, -1);
		this.inputSource = inputSource;
	}

	public InputSource getInputSource()
	{
		return inputSource;
	}

	@Override
	public Node getRoot()
	{
		return this;
	}
}
