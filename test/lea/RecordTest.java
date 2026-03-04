package lea;

import org.junit.jupiter.api.Test;
import lea.Node.*;
import lea.Reporter.Phase;

/**
 * JUnit tests for the Record extension.
 */
public final class RecordTest {

	/* =========================
	 * === ANALYSE SYNTAXIQUE ==
	 * ========================= */

	@Test
	void recordDeclaration_isParsed() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin
				algorithme début fin
				""").assertHasNoErrorAt(Phase.PARSER);
	}

	/* =========================
	 * === ANALYSE STATIQUE ====
	 * ========================= */

	@Test
	void error_unknownRecordType_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				    p : Inconnu;
				début
				fin
				""").assertHasErrorContaining(Phase.STATIC, "Type");
	}

	@Test
	void error_newRecord_wrongArgCount_isReported() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin
				algorithme
				variables
				    p : Point;
				début
				    p <- Point(1);
				fin
				""").assertHasErrorContaining(Phase.TYPE, "");
	}

	@Test
	void error_accessInexistentField_isReported() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				fin

				algorithme
				variables
				    p : Point;
				début
				    p <- Point(1);
				    écrire(p.z);
				fin
				""").assertHasErrorContaining(Phase.TYPE, "Champ inexistant");
	}

	/* =========================
	 * === INTERPRÉTATION ======
	 * ========================= */

	@Test
	void simpleRecord_execution() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin

				algorithme
				variables
				    p : Point;
				début
				    p <- Point(10, 20);
				    écrire(p.x, p.y);
				fin
				""").assertOutputs(new Int(10), new Int(20));
	}

	@Test
	void nestedRecords_and_assignment() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin

				enregistrement Cercle
				début
				    centre : Point;
				    rayon : entier;
				fin

				algorithme
				variables
				    c : Cercle;
				début
				    c <- Cercle(Point(1, 1), 5);
				    c.centre.x <- 3;
				    écrire(c.centre.x);
				fin
				""").assertOutputs(new Int(3));
	}

	@Test
	void recordToString_format() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin

				algorithme
				variables
				    p : Point;
				début
				    p <- Point(6, 8);
				    écrire("" + p);
				fin
				""").assertOutputs(new Str("Point(6, 8)"));
	}

	@Test
	void complexExample_fromSubject() {
		new LeaAsserts("""
				enregistrement Point
				début
				    x : entier;
				    y : entier;
				fin

				enregistrement Cercle
				début
				    centre : Point;
				    rayon : entier;
				fin

				algorithme
				variables
				    p : Point;
				    c : Cercle;
				    d : Point;
				début
				    p <- Point(6, 8);
				    c <- Cercle(Point(3, 4), 5);
				    d <- Point(c.centre.x - p.x, c.centre.y - p.y);

				    si d.x * d.x + d.y * d.y = c.rayon * c.rayon alors
				        écrire(p + " est sur le cercle " + c);
				    fin si
				fin
				""").assertOutputs(new Str("Point(6, 8) est sur le cercle Cercle(Point(3, 4), 5)"));
	}
}