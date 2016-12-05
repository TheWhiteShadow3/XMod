package tws.xmod;

import tws.expression.Config;

public interface XModConfig
{
	/**
	 * Gibt den Namensraum von XMod zurück.
	 * @return den Namensraum.
	 */
	public String getNamespace();
	
	/**
	 * Setzt den Namensraum von XMod.
	 * Der Default-Namensraum ist <code>xmod</code>
	 * @param namespace neuer Namensraum.
	 */
	public void setNamespace(String namespace);
	
	/**
	 * Gibt die Konfiguration für {@link tws.expression.Expression Ausdrücke} zurück.
	 * @return Die Expression Konfiguration.
	 */
	public Config getExpressionConfig();
	
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
	 * Setzt einen Alias für einen Bezeichner, der alternativ beim Auflösen von Variablen oder Funktionen angegeben werden kann.
	 * @param name Originalname
	 * @param alias Neuer Alias.
	 */
	public void setAlias(String name, String alias);
	
	/**
	 * Löst einen Alias zu seinem Originalnamen auf.
	 * @param alias Alias.
	 * @return den Originalnamen zum Alias.
	 */
	public String resolveAlias(String alias);
}
