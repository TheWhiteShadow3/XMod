package tws.xmod;

import tws.expression.Argument;

/**
 * XMod Kontext.
 * Enthält Methoden zu Informationen und zur Manipulation des XMod-Dokuments.
 * @author TheWhiteShadow
 */
public interface XModContext
{
	/**
	 * Name der Variablen, die den aktuellen Dateinamen enthält.
	 */
	public static final String VAR_FILE = "_FILE_";
	
	/**
	 * Name der Variablen, die die InputSource enthält.
	 * @see #getInputSource()
	 */
	public static final String SOURCE = "_SOURCE_";
	
	/**
	 * Gibt den Namensraum zurück, aus dem die XMod-Anweisungen gelesen werden.
	 * @return Den Namensraum.
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
	
	/**
	 * Schreibt den angegebenen Text in das Dokument.
	 * @param text Der Text.
	 * @throws XModException Wenn beim Schreiben ein Fehler auftritt.
	 */
	public void write(String text) throws XModException;
	
	/**
	 * Schreibt das angegebene Objekt in das Dokument.
	 * Wenn das Objekt <code>null</code> ist, wird "null" ausgegeben.
	 * @param obj Das Objekt.
	 * @throws XModException Wenn beim Schreiben ein Fehler auftritt.
	 */
	public void write(Object obj) throws XModException;
	
	/**
	 * Schreibt den angegebenen XMod Knoten in das Dokument.
	 * Wenn der Knoten eine Aktion enthält, wird diese aufgerufen.
	 * @param node Der Knoten.
	 * @throws XModException Wenn beim Schreiben ein Fehler auftritt.
	 */
	public void write(Node node) throws XModException;
	
	/**
	 * Löst einen Ausdruck aus.
	 * @param exp Der Ausdruck.
	 * @return Das Ergebnis des Ausdrucks.
	 * @throws XModException Wenn der Ausdruck nicht aufgelöst werden kann.
	 */
	public Argument resolveExpression(String exp) throws XModException;

	/**
	 * Gibt die Quelldatei zurück, in der die XMod-Anweisungen stehen.
	 * @return Quelldatei.
	 */
	public InputSource getInputSource();
	
	/**
	 * Löst eine Variable oder einen Ausdruck auf.
	 * Wenn der übergebene String in <code>${...}</code> eingeschlossen ist, wird dieser als Ausdruck gewertet ansonsten wird er als Variable interpretiert.
	 * @param name Der Name oder der Ausdruck.
	 * @return Das aufgelöste Objekt.
	 * @throws XModException
	 * @see #resolveValue(String)
	 * @see #resolveExpression(String)
	 * @see #getVariable(String)
	 */
	public Object resolveName(String name) throws XModException;

	/**
	 * Löst einen Wert oder einen Ausdruck auf.
	 * Wenn der übergebene String in <code>${...}</code> eingeschlossen ist, wird dieser als Ausdruck gewertet ansonsten wird er als fester Wert interpretiert.
	 * @param value Der Wert oder der Ausdruck.
	 * @return Das aufgelöste Objekt.
	 * @throws XModException
	 * @see #resolveName(String)
	 * @see #resolveExpression(String)
	 */
	public Object resolveValue(String value) throws XModException;
}
