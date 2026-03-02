package lea;


import org.junit.jupiter.api.Test;

import lea.Node.*;
import lea.Reporter.Phase;

/**
 * JUnit tests for the Interpreter class.
 */
public final class InterpreterTest {


	/* =========================
	 * === NORMAL CASES =========
	 * ========================= */

	@Test
	void simpleAssignmentsAndExpressions() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				  y : entier;
				début
				  x <- 1;
				  y <- x + 1;
				  écrire("Résultat=", y * 3);
				fin
				""").assertOutputs(new Str("Résultat="), new Int(6));
	}

	@Test
	void ifThenElseExecution() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  si x = 0 alors
				    x <- 1;
				  sinon
				    x <- 2;
				  fin si
				  écrire(x);
				fin
				""").assertOutputs(new Int(1));
	}

	@Test
	void nestedIf_executesCorrectBranch() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 2;
				  si x < 3 alors
				    si x = 2 alors
				      écrire(10);
				    sinon
				      écrire(11);
				    fin si
				  sinon
				    écrire(12);
				  fin si
				fin
				""").assertOutputs(new Int(10));
	}

	@Test
	void whileLoopExecution() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 3 faire
				    x <- x + 1;
				  fin tant que
				  écrire(x);
				fin
				""").assertOutputs(new Int(3));
	}

	@Test
	void whileLoop_zeroIterations_doesNotExecuteBody() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 0 faire
				    x <- 1;
				  fin tant que
				  écrire(x);
				fin
				""").assertOutputs(new Int(0));
	}

	@Test
	void breakInsideLoop_exitsLoop() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 10 faire
				    x <- x + 1;
				    si x = 3 alors
				      interrompre;
				    fin si
				  fin tant que
				  écrire(x);
				fin
				""").assertOutputs(new Int(3));
	}

	@Test
	void forLoopIncreasing() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 1 à 5 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""").assertOutputs(new Int(15));
	}

	@Test
	void forLoopWithStep() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 1 à 6 pas 2 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""").assertOutputs(new Int(9));
	}

	@Test
	void forLoopDecreasing() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 5 à 1 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""").assertOutputs(new Int(15));
	}

	@Test
	void forLoop_singleIteration_whenStartEqualsEnd() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				début
				  pour i de 3 à 3 faire
				    écrire(i);
				  fin pour
				fin
				""").assertOutputs(new Int(3));
	}


	@Test
	void arraysAndIndexing() {
		new LeaAsserts("""
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- [1, 2, 3];
				  a[2] <- 5;
				  écrire(a[2]);
				fin
				""").assertOutputs(new Int(5));
	}

	@Test
	void stringsAndLength() {
		new LeaAsserts("""
				algorithme
				variables
				  s : chaîne;
				début
				  s <- "abc";
				  écrire(longueur(s));
				  écrire(s[2]);
				fin
				""").assertOutputs(new Int(3), new Char('b'));
	}

	@Test
	void write_multipleArguments_preservesOrder_andTypes() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 4;
				  écrire("x=", x, ", ok=", x = 4);
				fin
				""").assertOutputs(new Str("x="), new Int(4), new Str(", ok="), new Bool(true));
	}

	@Test
	void precedenceAndAssociativity_areCorrect() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 1 + 2 * 3;
				  écrire(x);
				  x <- (1 + 2) * 3;
				  écrire(x);
				  x <- 10 - 3 - 2;
				  écrire(x);
				fin
				""").assertOutputs(new Int(7), new Int(9), new Int(5));
	}

	@Test
	void booleanOperators_andComparison_work() {
		new LeaAsserts("""
				algorithme
				variables
				  x : entier;
				début
				  x <- 3;
				  écrire(x < 4);
				  écrire(x = 3);
				  écrire(x = 2);
				fin
				""").assertOutputs(new Bool(true), new Bool(true), new Bool(false));
	}

	/* =========================
	 * ==== RUNTIME ERRORS ======
	 * ========================= */

	@Test
	void breakOutsideLoop_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				début
				  interrompre;
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Interrompre ne peut pas être en dehors d'une boucle");
	}

	@Test
	void arrayIndexOutOfBounds_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- [1, 2];
				  écrire(a[3]);
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Indice hors limites");
	}

	@Test
	void stringIndexOutOfBounds_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				  s : chaîne;
				début
				  s <- "ab";
				  écrire(s[3]);
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Indice hors limites");
	}

	@Test
	void invalidArraySize_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- tableau(-1, 0);
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Taille invalide");
	}

	@Test
	void infiniteForLoop_isReported_stepIsZero() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				début
				  pour i de 1 à 5 pas 0 faire
				    écrire(i);
				  fin pour
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Boucle pour infinie");
	}

	@Test
	void infiniteForLoop_decreasingStepIsNonNegative_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				début
				  pour i de 5 à 1 pas 1 faire
				    écrire(i);
				  fin pour
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Boucle pour infinie");
	}

	@Test
	void infiniteForLoop_decreasingStepIsZero_isReported() {
		new LeaAsserts("""
				algorithme
				variables
				  i : entier;
				début
				  pour i de 5 à 1 pas 0 faire
				    écrire(i);
				  fin pour
				fin
				""").assertHasErrorContaining(Phase.RUNTIME, "Boucle pour infinie");
	}
}
