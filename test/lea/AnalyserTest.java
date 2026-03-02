package lea;

import org.junit.jupiter.api.Test;

import lea.Reporter.Phase;

/**
 * JUnit tests for the Analyser class.
 */
public final class AnalyserTest {

	/* =========================
	 * === INITIALISATION ======
	 * ========================= */

	@Test
	void initializedVariable_isOk() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 0;
				écrire(x);
			fin
			""").assertHasNoError();
	}

	@Test
	void useBeforeInitialization_isReported_straightline() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- x + 1;
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Variable non initialisée");
	}

	/* =========================
	 * === IF / ELSE ===========
	 * ========================= */

	@Test
	void ifWithoutElse_thenAssign_doesNotGuaranteeInitialization() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				si vrai alors
					x <- 0;
				fin si
				écrire(x);
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Variable non initialisée");
	}

	@Test
	void ifElse_bothBranchesAssign_thenOk() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				si vrai alors
					x <- 0;
				sinon
					x <- 1;
				fin si
				écrire(x);
			fin
			""").assertHasNoError();
	}

	@Test
	void ifElse_assignDifferentVars_thenNeitherIsCertainlyInitialized() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
				y : entier;
			début
				si vrai alors
					x <- 0;
				sinon
					y <- 1;
				fin si
				écrire(x);
				écrire(y);
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Variable non initialisée");
	}

	/* =========================
	 * === WHILE ===============
	 * ========================= */

	@Test
	void whileMayNotIterate_assignmentInsideDoesNotGuaranteeInitialization() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				tant que faux faire
					x <- 1;
				fin tant que
				écrire(x);
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Variable non initialisée");
	}

	@Test
	void while_assignmentAfterInitialization_isOk() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 0;
				tant que faux faire
					x <- x + 1;
				fin tant que
				écrire(x);
			fin
			""").assertHasNoError();
	}

	/* =========================
	 * === FOR =================
	 * ========================= */

	@Test
	void forLoop_variableIsConsideredInitializedInBody() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de 1 à 3 faire
					écrire(i);
				fin pour
			fin
			""").assertHasNoError();
	}

	@Test
	void forLoop_usesUndeclaredIterator_isReported() {
		new LeaAsserts("""
			algorithme
			variables
			début
				pour i de 1 à 3 faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Variable non déclarée");
	}

	/* =========================
	 * === CODE MORT ===========
	 * ========================= */

	@Test
	void deadCode_afterBreakInSequence_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 0;
				interrompre;
				écrire(x);
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Code mort");
	}

	@Test
	void deadCode_afterBreakInIfBothBranches_isReported() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 0;
				si vrai alors
					interrompre;
				sinon
					interrompre;
				fin si
				écrire(x);
			fin
			""").assertHasErrorContaining(Phase.STATIC, "Code mort");
	}

	@Test
	void noDeadCode_ifBreakOnlyInOneBranch() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 0;
				si vrai alors
					interrompre;
				sinon
					écrire(x);
				fin si
				écrire(x);
			fin
			""").assertHasNoErrorUntil(Phase.STATIC);
	}
	
}
