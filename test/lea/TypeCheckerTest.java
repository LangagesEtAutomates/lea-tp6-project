package lea;

import org.junit.jupiter.api.Test;

import lea.Reporter.Phase;

/**
 * JUnit tests for the TypeChecker class.
 */
public final class TypeCheckerTest {

	/* =========================
	 * === PROGRAMMES VALIDES ==
	 * ========================= */

	@Test
	void ok_basic_arith_and_write() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
				y : entier;
			début
				x <- 1;
				y <- x * 3 - 2;
				écrire("y=", y);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_plus_is_coercive() {
		new LeaAsserts("""
			algorithme
			variables
				s : chaîne;
			début
				s <- 1 + vrai;
				écrire(s);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_equal_is_total() {
		new LeaAsserts("""
			algorithme
			variables
				b : booléen;
			début
				b <- (1 = vrai);
				écrire(b);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_string_length_and_index() {
		new LeaAsserts("""
			algorithme
			variables
				n : entier;
				c : caractère;
			début
				n <- longueur("abc");
				c <- "abc"[2];
				écrire(n, c);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_arrays_init_and_index() {
		new LeaAsserts("""
			algorithme
			variables
				a : tableau de entier;
				x : entier;
			début
				a <- [1, 2, 3];
				x <- a[1] + a[3];
				écrire(x);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_tableau_constructor() {
		new LeaAsserts("""
			algorithme
			variables
				a : tableau de booléen;
				b : booléen;
			début
				a <- tableau(3, vrai);
				b <- a[2] et faux;
				écrire(b);
			fin
			""").assertHasNoError();
	}

	@Test
	void ok_if_while_for_types() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
				x : entier;
			début
				x <- 0;
				si x < 1 alors
					x <- 1;
				fin si

				tant que x < 3 faire
					x <- x + 1;
				fin tant que

				pour i de 1 à 3 pas 1 faire
					écrire(i);
				fin pour
			fin
			""").assertHasNoError();
	}

	/* =========================
	 * === ERREURS : CONDITIONS
	 * ========================= */

	@Test
	void error_if_condition_must_be_bool() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 1;
				si x alors
					écrire(1);
				fin si
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_while_condition_must_be_bool() {
		new LeaAsserts("""
			algorithme
			début
				tant que 1 faire
					écrire(0);
				fin tant que
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	/* =========================
	 * === ERREURS : OPERATEURS
	 * ========================= */

	@Test
	void error_difference_requires_int() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- vrai - 1;
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_product_requires_int() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- "a" * 3;
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_lower_requires_ints() {
		new LeaAsserts("""
			algorithme
			variables
				b : booléen;
			début
				b <- "a" < "b";
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_and_requires_bool() {
		new LeaAsserts("""
			algorithme
			variables
				b : booléen;
			début
				b <- vrai et 1;
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_or_requires_bool() {
		new LeaAsserts("""
			algorithme
			variables
				b : booléen;
			début
				b <- 0 ou faux;
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_unary_minus_requires_int() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- -"abc";
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	/* =========================
	 * === ERREURS : LONGUEUR / INDEX
	 * ========================= */

	@Test
	void error_length_requires_string_or_array() {
		new LeaAsserts("""
			algorithme
			variables
				n : entier;
			début
				n <- longueur(1);
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Chaîne ou tableau attendu");
	}

	@Test
	void error_index_requires_string_or_array() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- 1[1];
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Chaîne ou tableau attendu");
	}

	@Test
	void error_index_position_requires_int() {
		new LeaAsserts("""
			algorithme
			variables
				c : caractère;
			début
				c <- "abc"[vrai];
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	/* =========================
	 * === ERREURS : TABLEAUX
	 * ========================= */

	@Test
	void error_tableau_length_requires_int() {
		new LeaAsserts("""
			algorithme
			variables
				a : tableau de entier;
			début
				a <- tableau(vrai, 0);
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_list_must_be_homogeneous() {
		new LeaAsserts("""
			algorithme
			variables
				a : tableau de entier;
			début
				a <- [1, vrai, 3];
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_assign_incompatible_rhs() {
		new LeaAsserts("""
			algorithme
			variables
				x : entier;
			début
				x <- vrai;
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_array_index_result_type_must_match_assignment() {
		new LeaAsserts("""
			algorithme
			variables
				a : tableau de booléen;
				x : entier;
			début
				a <- tableau(3, vrai);
				x <- a[1];
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	/* =========================
	 * === ERREURS : POUR
	 * ========================= */

	@Test
	void error_for_bounds_must_be_int() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de vrai à 3 faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_for_step_must_be_int() {
		new LeaAsserts("""
			algorithme
			variables
				i : entier;
			début
				pour i de 1 à 3 pas faux faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}

	@Test
	void error_for_id_must_be_int() {
		new LeaAsserts("""
			algorithme
			variables
				i : booléen;
			début
				pour i de 1 à 3 faire
					écrire(i);
				fin pour
			fin
			""").assertHasErrorContaining(Phase.TYPE, "Type incompatible");
	}
}
