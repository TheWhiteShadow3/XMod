# Java Text Transformation Language by TheWhiteShadow
Copyright (C) 2017 by TheWhiteShadow

Mit XMod kann man aus Templates aus Java heraus Dokumente erstellen.
XMod arbeitet ausschließlich mit XML-Syntax. Das Ausgabedokument kann allerdings eine beliebige Textdatei sein.

### Beispiel (Bubblsort):
```XML
<bubblesort>
	<xmod:set var="input" value="[5, 2, 6, 4, 1, 3]" /><input>${input}</input>
	<xmod:set var="n" value="input.size" />
	<xmod:while exp="n > 1">
		<xmod:set var="new_n" value="1" />
		<xmod:set var="i" value="0" />
		<xmod:while exp="i &lt; n - 1">
			<xmod:if exp="input[i] > input[i+1]">
				<xmod:set var="temp" value="input[i]" />
				<xmod:set var="input[i]" value="input[i+1]" />
				<xmod:set var="input[i+1]" value="temp" />
				<xmod:set var="new_n" value="i+1" />
			</xmod:if>
			<xmod:set var="i" value="i+1" />
		</xmod:while>
		<xmod:set var="n" value="new_n" />
	</xmod:while>
	<output><xmod:list var="input" seperator=", "/></output>
</bubblesort>
```

### Die Ausgabe dazu sieht dann so aus:
```XML
<bubblesort>
	<input>[5, 2, 6, 4, 1, 3]</input>
	
	<output>1, 2, 3, 4, 5, 6</output>
</bubblesort>
```

### Der hierzu benötigte Java-Code sieht wie folgt aus:
```Java
InputSource source = new InputSource(new File("bubblesort.xml"));
System.out.println(new XMod().parse(source));
```

XMod benötigt meine [Expression Library](https://github.com/TheWhiteShadow3/Expression).