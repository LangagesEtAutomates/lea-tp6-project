package lea;

import org.junit.jupiter.api.Test;

import lea.Reporter.Phase;

/**
 * JUnit tests for the Lexer class.
 */
public final class LexerTest {

	/* =========================
	 * === IDENTIFIANTS / MOTS-CLES
	 * ========================= */

	@Test
	void identifier_basic() {
		new LeaAsserts("x")
		.assertMatches(Terminal.ID);
	}

	@Test
	void identifier_with_underscores_and_digits() {
		new LeaAsserts("a_1_b2")
		.assertMatches(Terminal.ID);
	}

	@Test
	void keyword_vs_identifier_prefix() {
		new LeaAsserts("si sinon simon")
		.assertMatches(Terminal.SI, Terminal.SINON, Terminal.ID);
	}

	@Test
	void keywords_allCore() {
		new LeaAsserts("""
				fin algorithme variables début
				si alors sinon
				tant que faire
				pour de à pas interrompre
				écrire longueur tableau
				""")
		.assertMatches(
				Terminal.FIN, Terminal.ALGORITHME, Terminal.VARIABLES, Terminal.DEBUT, 
				Terminal.SI, Terminal.ALORS, Terminal.SINON,
				Terminal.TANT, Terminal.QUE, Terminal.FAIRE,
				Terminal.POUR, Terminal.DE, Terminal.A, Terminal.PAS, Terminal.INTERROMPRE,
				Terminal.ECRIRE, Terminal.LONGUEUR, Terminal.TABLEAU
				);
	}

	
	/* =========================
	 * === SYMBOLES / OPERATEURS
	 * ========================= */

	@Test
	void punctuation_and_assignment() {
		new LeaAsserts("<- : ; , ( ) [ ]")
		.assertMatches(
				Terminal.AFFECTATION, Terminal.DEUX_PT, Terminal.PT_VIRG, Terminal.VIRG,
				Terminal.PAR_G, Terminal.PAR_D, Terminal.CROCHET_G, Terminal.CROCHET_D);
	}

	@Test
	void operators_and_logicals() {
		new LeaAsserts("+ - * = < et ou")
		.assertMatches(
				Terminal.PLUS, Terminal.MOINS, Terminal.MULTIPLIE,
				Terminal.EGAL, Terminal.INFERIEUR,
				Terminal.ET, Terminal.OU);
	}

	/* =========================
	 * === LITTERAUX
	 * ========================= */

	@Test
	void boolean_literals() {
		new LeaAsserts("vrai faux")
		.assertMatches(Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void integer_literals() {
		new LeaAsserts("0 7 42 123456")
		.assertMatches(Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void char_literals_simple_and_escaped() {
		new LeaAsserts("'a' '\\n' '\\'' '\\\\'")
		.assertMatches(Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void string_literals_simple_and_escaped() {
		new LeaAsserts("\"abc\" \"a\\\\nb\" \"\\\"\" \"\\\\\"")
		.assertMatches(Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void types_are_typebase_tokens() {
		new LeaAsserts("booléen entier caractère chaîne")
		.assertMatches(Terminal.TYPEBASE, Terminal.TYPEBASE, Terminal.TYPEBASE, Terminal.TYPEBASE);
	}

	/* =========================
	 * === ESPACES / COMMENTAIRES
	 * ========================= */

	@Test
	void whitespace_is_ignored() {
		new LeaAsserts(" \n\t  x \r\f y ")
		.assertMatches(Terminal.ID, Terminal.ID);
	}

	@Test
	void line_comment_is_ignored() {
		new LeaAsserts("""
				x // comment
				y
				""")
		.assertMatches(Terminal.ID, Terminal.ID);
	}

	@Test
	void block_comment_is_ignored() {
		new LeaAsserts("x /* comment */ y")
		.assertMatches(Terminal.ID, Terminal.ID);
	}

	@Test
	void block_comment_with_stars_is_ignored() {
		new LeaAsserts("x /* ** */ y")
		.assertMatches(Terminal.ID, Terminal.ID);
	}

	/* =========================
	 * === ERREURS LEXICALES
	 * ========================= */

	@Test
	void illegal_character_is_reported() {
		new LeaAsserts("@")
		.assertHasErrorContaining(Phase.LEXER, "Illegal character");
	}

	@Test
	void illegal_character_does_not_prevent_other_tokens() {
		new LeaAsserts("x @ y")
		.assertMatches(Terminal.ID, Terminal.ID);
	}

	@Test
	void accentuated_keyword_is_recognized() {
		new LeaAsserts("début à fin")
		.assertMatches(Terminal.DEBUT, Terminal.A, Terminal.FIN);
	}

}
