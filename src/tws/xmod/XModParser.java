package tws.xmod;

/**
 * XMod-Parser Schnittstelle.
 * @author TheWhiteShadow
 */
public interface XModParser
{
	public RootNode read(InputSource inputSource, XModContext context) throws XModException;
}
