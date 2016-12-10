package tws.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import tws.xmod.InputSource;
import tws.xmod.TagNode;
import tws.xmod.XMod;
import tws.xmod.XModContext;
import tws.xmod.XModException;
import tws.xmod.action.Action;
import tws.xmod.action.ActionProvider;

public class XModTest
{
	private static final Action DUMMY_ACTION = new Action()
	{
		@Override
		public void execute(TagNode node, XModContext context) throws XModException
		{}
	};
	
	@Test
	public void tagSyntax() throws IOException
	{
		XMod xmod = new XMod();
		// Nur ein Textknoten
		xmod.parse(new InputSource("<<xmod>/xmod>"));
		
		xmod.parse(input("<xmod-valid "));
		
		try
		{
			xmod.parse(input("<xmod: />"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:invalid "));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:name '/>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:tag ></other>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:nope /x"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:tag></xmod:tag!>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:tag att=/>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:tag =/>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("<xmod:tag att='a' att='b'/>"));
			fail();
		}
		catch(XModException e) { handleException(e); }
		
		try
		{
			xmod.parse(input("${5"));
			fail();
		}
		catch(XModException e) { handleException(e); }
	}
	
	@Test
	public void tagValidation() throws IOException
	{
		XMod xmod = new XMod();
		xmod.getConfig().setVariable("blub", "pantsu");
		String result;
		
		result = xmod.parse(input("${2}"));
		assertEquals("2", result);
		
		result = xmod.parse(input("<xmod:print value='blub' />"));
		assertEquals("blub", result);
		
		result = xmod.parse(input("<xmod:print exp='blub' />"));
		assertEquals("pantsu", result);
		
		result = xmod.parse(input("<xmod:print value='/>' />"));
		assertEquals("/>", result);
		
		result = xmod.parse(input("<tag att=\"<xmod:print value='string' />\" />"));
		assertEquals("<tag att=\"string\" />", result);
		
		ActionProvider.getInstance().addAction("t4g:name", DUMMY_ACTION);
		
		xmod.parse(new InputSource("<xmod:t4g:name />"));
		
		xmod.parse(input("<xmod:set var='bool' value/>"));
		assertEquals(Boolean.TRUE, xmod.getConfig().getVariable("bool"));
		
		URL url = new File(System.getProperty("user.dir") + "/test/include.xml").toURI().toURL();
		result = xmod.parse(input("<xmod:include url='" + url + "' />"));
		assertTrue(!result.isEmpty());
	}
	
	@Test
	public void cascadeTest() throws IOException
	{
		XMod xmod = new XMod();
		xmod.getConfig().setNamespace("x1");
		
		String result = xmod.parse(input("<x2:print exp=\"2 * <x1:print value='5' />\" />"));
		xmod.getConfig().setNamespace("x2");
		
		result = xmod.parse(input(result));
		assertEquals("10", result);
	}
	
	private InputSource input(String str)
	{
		System.out.println(str);
		return new InputSource(str);
	}
	
	private void handleException(XModException e)
	{
		System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		if (e.getCause() != null)
			System.out.println(e.getCause());
		
		System.out.println();
	}
}
