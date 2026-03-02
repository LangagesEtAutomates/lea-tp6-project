package lea;

import org.junit.jupiter.api.Test;

import lea.Reporter.Phase;

/**
 * JUnit tests for the Parser class.
 */
public final class ParserTest {

	/* =========================
	 * === PROGRAMMES VALIDES ==
	 * ========================= */

	@Test
	void minimalProgram_emptyBody() {
		new LeaAsserts("""
			algorithme
			début
			fin
			""").assertHasNoError();
	}

	@Test
	void declarations_types_and_arrayType() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
				s : chaîne;
				a : tableau de entier;
			début
				écrire(x, s, a);
			fin
			""").assertHasNoErrorAt(Phase.PARSER);
	}

	@Test
	void commands_and_expressions_smoke() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
				a : tableau de entier;
			début
				x <- 1 + 2 * 3;
				a <- [1, 2, 3];
				a[2] <- -x;
				écrire(longueur("abc"), ":", "ab"[2]);
				écrire(tableau(3, 0));
				écrire(x = 7 ou faux et vrai);
			fin
			""").assertHasNoError();
	}

	@Test
	void structuredStatements_if_while_for() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
				i : entier;
			début
				si x = 0 alors
					écrire("zero");
				sinon
					écrire("nonzero");
				fin si

				tant que x < 3 faire
					x <- x + 1;
				fin tant que

				pour i de 1 à 3 faire
					écrire(i);
				fin pour

				pour i de 5 à 1 pas -1 faire
					écrire(i);
				fin pour
			fin
			""").assertHasNoErrorAt(Phase.PARSER);
	}

	/* =========================
	 * === ERREURS STRUCTURELLES
	 * ========================= */

	@Test
	void programStructureError_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Erreur dans le programme");
	}

	/* =========================
	 * === catch_expr : MANQUANT
	 * ========================= */

	@Test
	void missingExpression_inIfCondition_isReported() {
		new LeaAsserts("""
			algorithme
			variables
			début
				si alors
					écrire(1);
				fin si
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Expression manquante");
	}

	@Test
	void missingExpression_inWhileCondition_isReported() {
		new LeaAsserts("""
			algorithme
			variables
			début
				tant que faire
					écrire(1);
				fin tant que
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Expression manquante");
	}

	@Test
	void missingExpression_inForStart_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de à 5 faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Expression manquante");
	}

	@Test
	void missingExpression_inForEnd_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de 1 à faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Expression manquante");
	}

	@Test
	void missingExpression_inForStep_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de 1 à 5 pas faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Expression manquante");
	}

	/* =========================
	 * === catch_expr : CASSEE
	 * ========================= */

	@Test
	void invalidExpression_inIfCondition_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				si x < alors
					écrire(1);
				fin si
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Erreur dans l'expression");
	}

	@Test
	void invalidExpression_inForEnd_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de 1 à 1 + faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Erreur dans l'expression");
	}

	/* =========================
	 * === RECOVERY : CONTINUER
	 * ========================= */

	@Test
	void recovery_afterBadCommand_continuesUntilEnd() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				écrire(1;
				x <- 2;
				écrire(x);
			fin
			""").assertHasErrorContaining(Phase.PARSER, "Erreur dans la commande");
	}
	
}
