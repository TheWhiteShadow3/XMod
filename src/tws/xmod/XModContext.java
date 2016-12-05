package tws.xmod;

import tws.expression.Argument;


public interface XModContext
{
	public static final String VAR_FILE = "_FILE_";
	public static final String SOURCE = "_SOURCE_";
	
	/**
	 * Gibt den Namensraum von XMod zurück.
	 * @return den Namensraum.
	 */
	public String getNamespace();
	
	/**
	 * Gibt eine zuvor gespeicherte Variable zurück.
	 * @param name Name der Variable.
	 * @return Den Wert der Variable.
	 */
	public Object getVariable(String name);
	
	/**
	 * Setzt eine Variable.
	 * @param name Name der Variable.
	 * @param value Wert der Variable.
	 */
	public void setVariable(String name, Object value);
	
	/**
	 * Löscht eine Variable.
	 * @param name Name der Variable.
	 */
	public void unsetVariable(String name);
	
	public void write(String text) throws XModException;
	public void write(Object obj) throws XModException;
	public void write(Node node) throws XModException;
	
	public Argument resolveExpression(String exp) throws XModException;

	public InputSource getInputSource();

	public Object resolveName(String name) throws XModException;

	public Object resolveValue(String value) throws XModException;
}
