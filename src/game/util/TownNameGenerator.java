package game.util;

import java.util.function.Function;

public class TownNameGenerator extends TownNameTables 
{
	

	private static int lengthof(String[] s) {
		
		return s.length;
	}
	
	/*
	static  int SeedChance(int shift_by, int max, int seed)
	{
		return (BitOps.GB(seed, shift_by, 16) * max) >> 16;
	}

	static  int SeedModChance(int shift_by, int max, int seed) */
	static  int SeedChance(int shift_by, int max, int seed)
	{
		/* This actually gives *MUCH* more even distribution of the values
		 * than SeedChance(), which is absolutely horrible in that. If
		 * you do not believe me, try with i.e. the Czech town names,
		 * compare the words (nicely visible on prefixes) generated by
		 * SeedChance() and SeedModChance(). Do not get dicouraged by the
		 * never-use-modulo myths, which hold true only for the linear
		 * congruential generators (and Random() isn't such a generator).
		 * --pasky */
		// TODO: Perhaps we should use it for all the name generators? --pasky
		return (seed >> shift_by) % max;
	}

	static  int SeedChanceBias(int shift_by, int max, int seed, int bias)
	{
		return SeedChance(shift_by, max + bias, seed) - bias;
	}

	static String RetReplaceWords(final String org, final String rep, String buf)
	{
		//if (strncmp(buf, org, 4) == 0) strncpy(buf, rep, 4);
		if( buf.substring(0, 3).equals(org.substring(0, 3)) )
			return rep + buf.substring(4);
		else 
			return buf;
	}

	static String MakeEnglishOriginalTownName(int seed)
	{
		int i;
		String buf = "";


		// optional first segment
		i = SeedChanceBias(0, lengthof(name_original_english_1), seed, 50);
		if (i >= 0)
			buf += (name_original_english_1[i]);

		//mandatory middle segments
		buf += ( name_original_english_2[SeedChance(4,  lengthof(name_original_english_2), seed)]);
		buf += ( name_original_english_3[SeedChance(7,  lengthof(name_original_english_3), seed)]);
		buf += ( name_original_english_4[SeedChance(10, lengthof(name_original_english_4), seed)]);
		buf += ( name_original_english_5[SeedChance(13, lengthof(name_original_english_5), seed)]);

		//optional last segment
		i = SeedChanceBias(15, lengthof(name_original_english_6), seed, 60);
		if (i >= 0)
			buf += ( name_original_english_6[i]);

		char c = buf.charAt(0);
		if (c == 'C' && (c == 'e' || c == 'i'))
			buf = "K" + buf.substring(1);

		buf = RetReplaceWords("Cunt", "East", buf);
		buf = RetReplaceWords("Slag", "Pits", buf);
		buf = RetReplaceWords("Slut", "Edin", buf);
		//buf = RetReplaceWords("Fart", "Boot", buf);
		buf = RetReplaceWords("Drar", "Quar", buf);
		buf = RetReplaceWords("Dreh", "Bash", buf);
		buf = RetReplaceWords("Frar", "Shor", buf);
		buf = RetReplaceWords("Grar", "Aber", buf);
		buf = RetReplaceWords("Brar", "Over", buf);
		buf = RetReplaceWords("Wrar", "Inve", buf);

		return buf;
	}


/*
	static byte MakeEnglishAdditionalTownName(String buf, int seed)
	{
		int i;

		//null terminates the string for strcat
		strcpy(buf, "");

		// optional first segment
		i = SeedChanceBias(0, lengthof(name_additional_english_prefix), seed, 50);
		if (i >= 0)
			buf += (name_additional_english_prefix[i]);

		if (SeedChance(3, 20, seed) >= 14) {
			buf += ( name_additional_english_1a[SeedChance(6, lengthof(name_additional_english_1a), seed)]);
		} else {
			buf += ( name_additional_english_1b1[SeedChance(6, lengthof(name_additional_english_1b1), seed)]);
			buf += ( name_additional_english_1b2[SeedChance(9, lengthof(name_additional_english_1b2), seed)]);
			if (SeedChance(11, 20, seed) >= 4) {
				buf += ( name_additional_english_1b3a[SeedChance(12, lengthof(name_additional_english_1b3a), seed)]);
			} else {
				buf += ( name_additional_english_1b3b[SeedChance(12, lengthof(name_additional_english_1b3b), seed)]);
			}
		}

		buf += ( name_additional_english_2[SeedChance(14, lengthof(name_additional_english_2), seed)]);

		//optional last segment
		i = SeedChanceBias(15, lengthof(name_additional_english_3), seed, 60);
		if (i >= 0)
			buf += ( name_additional_english_3[i]);

		ReplaceWords("Cunt", "East", buf);
		ReplaceWords("Slag", "Pits", buf);
		ReplaceWords("Slut", "Edin", buf);
		ReplaceWords("Fart", "Boot", buf);
		ReplaceWords("Drar", "Quar", buf);
		ReplaceWords("Dreh", "Bash", buf);
		ReplaceWords("Frar", "Shor", buf);
		ReplaceWords("Grar", "Aber", buf);
		ReplaceWords("Brar", "Over", buf);
		ReplaceWords("Wrar", "Stan", buf);

		return 0;
	}

	static byte MakeAustrianTownName(String buf, int seed)
	{
		int i, j = 0;
		strcpy(buf, "");

		// Bad, Maria, Gross, ...
		i = SeedChanceBias(0, lengthof(name_austrian_a1), seed, 15);
		if (i >= 0) buf += ( name_austrian_a1[i]);

		i = SeedChance(4, 6, seed);
		if (i >= 4) {
			// Kaisers-kirchen
			buf += ( name_austrian_a2[SeedChance( 7, lengthof(name_austrian_a2), seed)]);
			buf += ( name_austrian_a3[SeedChance(13, lengthof(name_austrian_a3), seed)]);
		} else if (i >= 2) {
			// St. Johann
			buf += ( name_austrian_a5[SeedChance( 7, lengthof(name_austrian_a5), seed)]);
			buf += ( name_austrian_a6[SeedChance( 9, lengthof(name_austrian_a6), seed)]);
			j = 1; // More likely to have a " an der " or " am "
		} else {
			// Zell
			buf += ( name_austrian_a4[SeedChance( 7, lengthof(name_austrian_a4), seed)]);
		}

		i = SeedChance(1, 6, seed);
		if (i >= 4 - j) {
			// an der Donau (rivers)
			buf += ( name_austrian_f1[SeedChance(4, lengthof(name_austrian_f1), seed)]);
			buf += ( name_austrian_f2[SeedChance(5, lengthof(name_austrian_f2), seed)]);
		} else if (i >= 2 - j) {
			// am Dachstein (mountains)
			buf += ( name_austrian_b1[SeedChance(4, lengthof(name_austrian_b1), seed)]);
			buf += ( name_austrian_b2[SeedChance(5, lengthof(name_austrian_b2), seed)]);
		}

		return 0;
	}

	static byte MakeGermanTownName(String buf, int seed)
	{
		uint i;
		uint seed_derivative;

		//null terminates the string for strcat
		strcpy(buf, "");

		seed_derivative = SeedChance(7, 28, seed);

		//optional prefix
		if (seed_derivative == 12 || seed_derivative == 19) {
			i = SeedChance(2, lengthof(name_german_pre), seed);
			buf += (name_german_pre[i]);
		}

		// mandatory middle segments including option of hardcoded name
		i = SeedChance(3, lengthof(name_german_real) + lengthof(name_german_1), seed);
		if (i < lengthof(name_german_real)) {
			buf += (name_german_real[i]);
		} else {
			buf += ( name_german_1[i - lengthof(name_german_real)]);

			i = SeedChance(5, lengthof(name_german_2), seed);
			buf += ( name_german_2[i]);
		}

		// optional suffix
		if (seed_derivative == 24) {
			i = SeedChance(9,
				lengthof(name_german_4_an_der) + lengthof(name_german_4_am), seed);
			if (i < lengthof(name_german_4_an_der)) {
				buf += ( name_german_3_an_der[0]);
				buf += ( name_german_4_an_der[i]);
			} else {
				buf += ( name_german_3_am[0]);
				buf += ( name_german_4_am[i - lengthof(name_german_4_an_der)]);
			}
		}
		return 0;
	}

	static byte MakeSpanishTownName(String buf, int seed)
	{
		strcpy(buf, name_spanish_real[SeedChance(0, lengthof(name_spanish_real), seed)]);
		return 0;
	}

	static byte MakeFrenchTownName(String buf, int seed)
	{
		strcpy(buf, name_french_real[SeedChance(0, lengthof(name_french_real), seed)]);
		return 0;
	}

	static byte MakeSillyTownName(String buf, int seed)
	{
		strcpy(buf, name_silly_1[SeedChance( 0, lengthof(name_silly_1), seed)]);
		buf += ( name_silly_2[SeedChance(16, lengthof(name_silly_2), seed)]);
		return 0;
	}

	static byte MakeSwedishTownName(String buf, int seed)
	{
		int i;

		//null terminates the string for strcat
		strcpy(buf, "");

		// optional first segment
		i = SeedChanceBias(0, lengthof(name_swedish_1), seed, 50);
		if (i >= 0)
			buf += ( name_swedish_1[i]);

		// mandatory middle segments including option of hardcoded name
		if (SeedChance(4, 5, seed) >= 3) {
			buf += ( name_swedish_2[SeedChance( 7, lengthof(name_swedish_2), seed)]);
		} else {
			buf += ( name_swedish_2a[SeedChance( 7, lengthof(name_swedish_2a), seed)]);
			buf += ( name_swedish_2b[SeedChance(10, lengthof(name_swedish_2b), seed)]);
			buf += ( name_swedish_2c[SeedChance(13, lengthof(name_swedish_2c), seed)]);
		}

		buf += ( name_swedish_3[SeedChance(16, lengthof(name_swedish_3), seed)]);

		return 0;
	}

	static byte MakeDutchTownName(String buf, int seed)
	{
		int i;

		//null terminates the string for strcat
		strcpy(buf, "");

		// optional first segment
		i = SeedChanceBias(0, lengthof(name_dutch_1), seed, 50);
		if (i >= 0)
			buf += ( name_dutch_1[i]);

		// mandatory middle segments including option of hardcoded name
		if (SeedChance(6, 9, seed) > 4) {
			buf += ( name_dutch_2[SeedChance( 9, lengthof(name_dutch_2), seed)]);
		} else {
			buf += ( name_dutch_3[SeedChance( 9, lengthof(name_dutch_3), seed)]);
			buf += ( name_dutch_4[SeedChance(12, lengthof(name_dutch_4), seed)]);
		}
		buf += ( name_dutch_5[SeedChance(15, lengthof(name_dutch_5), seed)]);

		return 0;
	}

	static byte MakeFinnishTownName(String buf, int seed)
	{
		//null terminates the string for strcat
		strcpy(buf, "");

		// Select randomly if town name should consists of one or two parts.
		if (SeedChance(0, 15, seed) >= 10) {
			buf += ( name_finnish_real[SeedChance( 2, lengthof(name_finnish_real), seed)]);
		} else {
			buf += ( name_finnish_1[SeedChance( 2, lengthof(name_finnish_1), seed)]);
			buf += ( name_finnish_2[SeedChance(10, lengthof(name_finnish_2), seed)]);
		}

		return 0;
	}

	static byte MakePolishTownName(String buf, int seed)
	{
		uint i;
		uint j;

		//null terminates the string for strcat
		strcpy(buf, "");

		// optional first segment
		i = SeedChance(0,
			lengthof(name_polish_2_o) + lengthof(name_polish_2_m) +
			lengthof(name_polish_2_f) + lengthof(name_polish_2_n),
			seed);
		j = SeedChance(2, 20, seed);


		if (i < lengthof(name_polish_2_o)) {
			buf += ( name_polish_2_o[SeedChance(3, lengthof(name_polish_2_o), seed)]);
		} else if (i < lengthof(name_polish_2_m) + lengthof(name_polish_2_o)) {
			if (j < 4)
				buf += ( name_polish_1_m[SeedChance(5, lengthof(name_polish_1_m), seed)]);

			buf += ( name_polish_2_m[SeedChance(7, lengthof(name_polish_2_m), seed)]);

			if (j >= 4 && j < 16)
				buf += ( name_polish_3_m[SeedChance(10, lengthof(name_polish_3_m), seed)]);
		} else if (i < lengthof(name_polish_2_f) + lengthof(name_polish_2_m) + lengthof(name_polish_2_o)) {
			if (j < 4)
				buf += ( name_polish_1_f[SeedChance(5, lengthof(name_polish_1_f), seed)]);

			buf += ( name_polish_2_f[SeedChance(7, lengthof(name_polish_2_f), seed)]);

			if (j >= 4 && j < 16)
				buf += ( name_polish_3_f[SeedChance(10, lengthof(name_polish_3_f), seed)]);
		} else {
			if (j < 4)
				buf += ( name_polish_1_n[SeedChance(5, lengthof(name_polish_1_n), seed)]);

			buf += ( name_polish_2_n[SeedChance(7, lengthof(name_polish_2_n), seed)]);

			if (j >= 4 && j < 16)
				buf += ( name_polish_3_n[SeedChance(10, lengthof(name_polish_3_n), seed)]);
		}
		return 0;
	}

	static byte MakeCzechTownName(String buf, int seed)
	{
		// Probability of prefixes/suffixes 
		// 0..11 prefix, 12..13 prefix+suffix, 14..17 suffix, 18..31 nothing 
		int prob_tails;
		bool do_prefix, do_suffix, dynamic_subst;
		// IDs of the respective parts 
		int prefix = 0, ending = 0, suffix = 0;
		uint postfix = 0;
		uint stem;
		// The select criteria. 
		CzechGender gender;
		CzechChoose choose;
		CzechAllow allow;

		// 1:3 chance to use a real name.
		if (SeedChance(0, 4, seed) == 0) {
			strcpy(buf, name_czech_real[SeedChance(4, lengthof(name_czech_real), seed)]);
			return 0;
		}

		// NUL terminates the string for strcat()
		strcpy(buf, "");

		prob_tails = SeedModChance(2, 32, seed);
		do_prefix = prob_tails < 12;
		do_suffix = prob_tails > 11 && prob_tails < 17;

		if (do_prefix) prefix = SeedChance(5, lengthof(name_czech_adj) * 12, seed) / 12;
		if (do_suffix) suffix = SeedChance(7, lengthof(name_czech_suffix), seed);
		// 3:1 chance 3:1 to use dynamic substantive
		stem = SeedChance(9,
			lengthof(name_czech_subst_full) + 3 * lengthof(name_czech_subst_stem),
			seed);
		if (stem < lengthof(name_czech_subst_full)) {
			// That was easy!
			dynamic_subst = false;
			gender = name_czech_subst_full[stem].gender;
			choose = name_czech_subst_full[stem].choose;
			allow = name_czech_subst_full[stem].allow;
		} else {
			unsigned int map[lengthof(name_czech_subst_ending)];
			int ending_start = -1, ending_stop = -1;
			int i;

			// Load the substantive
			dynamic_subst = true;
			stem -= lengthof(name_czech_subst_full);
			stem %= lengthof(name_czech_subst_stem);
			gender = name_czech_subst_stem[stem].gender;
			choose = name_czech_subst_stem[stem].choose;
			allow = name_czech_subst_stem[stem].allow;

			// Load the postfix (1:1 chance that a postfix will be inserted)
			postfix = SeedModChance(14, lengthof(name_czech_subst_postfix) * 2, seed);

			if (choose & CZC_POSTFIX) {
				// Always get a real postfix.
				postfix %= lengthof(name_czech_subst_postfix);
			}
			if (choose & CZC_NOPOSTFIX) {
				// Always drop a postfix.
				postfix += lengthof(name_czech_subst_postfix);
			}
			if (postfix < lengthof(name_czech_subst_postfix))
				choose |= CZC_POSTFIX;
			else
				choose |= CZC_NOPOSTFIX;

			// Localize the array segment containing a good gender
			for (ending = 0; ending < (int) lengthof(name_czech_subst_ending); ending++) {
				final CzechNameSubst *e = &name_czech_subst_ending[ending];

				if (gender == CZG_FREE ||
						(gender == CZG_NFREE && e->gender != CZG_SNEUT && e->gender != CZG_PNEUT) ||
						gender == e->gender) {
					if (ending_start < 0)
						ending_start = ending;

				} else if (ending_start >= 0) {
					ending_stop = ending - 1;
					break;
				}
			}
			if (ending_stop < 0) {
				// Whoa. All the endings matched.
				ending_stop = ending - 1;
			}

			// Make a sequential map of the items with good mask
			i = 0;
			for (ending = ending_start; ending <= ending_stop; ending++) {
				final CzechNameSubst *e = &name_czech_subst_ending[ending];

				if ((e->choose & choose) == choose && (e->allow & allow) != 0)
					map[i++] = ending;
			}
			assert(i > 0);

			// Load the ending
			ending = map[SeedModChance(16, i, seed)];
			// Override possible CZG_*FREE; this must be a real gender,
			// otherwise we get overflow when modifying the adjectivum.
			gender = name_czech_subst_ending[ending].gender;
			assert(gender != CZG_FREE && gender != CZG_NFREE);
		}

		if (do_prefix && (name_czech_adj[prefix].choose & choose) != choose) {
			// Throw away non-matching prefix.
			do_prefix = false;
		}

		// Now finally finalruct the name

		if (do_prefix) {
			CzechPattern pattern = name_czech_adj[prefix].pattern;
			int endpos;

			buf += ( name_czech_adj[prefix].name);
			endpos = strlen(buf) - 1;
			if (gender == CZG_SMASC && pattern == CZP_PRIVL) {
				// -ovX -> -uv 
				buf[endpos - 2] = 'u';
				assert(buf[endpos - 1] == 'v');
				buf[endpos] = '\0';
			} else {
				buf[endpos] = name_czech_patmod[gender][pattern];
			}

			buf += ( " ");
		}

		if (dynamic_subst) {
			buf += ( name_czech_subst_stem[stem].name);
			if (postfix < lengthof(name_czech_subst_postfix)) {
				final String poststr = name_czech_subst_postfix[postfix];
				final String endstr = name_czech_subst_ending[ending].name;
				int postlen, endlen;

				postlen = strlen(poststr);
				endlen = strlen(endstr);
				assert(postlen > 0 && endlen > 0);

				// Kill the "avava" and "Jananna"-like cases
				if (postlen < 2 || postlen > endlen || (
							(poststr[1] != 'v' || poststr[1] != endstr[1]) &&
							poststr[2] != endstr[1])
						) {
					uint buflen;
					buf += ( poststr);
					buflen = strlen(buf);

					// k-i -> c-i, h-i -> z-i
					if (endstr[0] == 'i') {
						switch (buf[buflen - 1]) {
							case 'k': buf[buflen - 1] = 'c'; break;
							case 'h': buf[buflen - 1] = 'z'; break;
							default: break;
						}
					}
				}
			}
			buf += ( name_czech_subst_ending[ending].name);
		} else {
			buf += ( name_czech_subst_full[stem].name);
		}

		if (do_suffix) {
			buf += ( " ");
			buf += ( name_czech_suffix[suffix]);
		}

		return 0;
	}

	static byte MakeRomanianTownName(String buf, int seed)
	{
		strcpy(buf, name_romanian_real[SeedChance(0, lengthof(name_romanian_real), seed)]);
		return 0;
	}

	static byte MakeSlovakTownName(String buf, int seed)
	{
		strcpy(buf, name_slovak_real[SeedChance(0, lengthof(name_slovak_real), seed)]);
		return 0;
	}

	static byte MakeNorwegianTownName(String buf, int seed)
	{
		strcpy(buf, "");

		// Use first 4 bit from seed to decide whether or not this town should
		// have a real name 3/16 chance.  Bit 0-3
		if (SeedChance(0, 15, seed) < 3) {
			// Use 7bit for the realname table index.  Bit 4-10
			buf += ( name_norwegian_real[SeedChance(4, lengthof(name_norwegian_real), seed)]);
		} else {
			// Use 7bit for the first fake part.  Bit 4-10
			buf += ( name_norwegian_1[SeedChance(4, lengthof(name_norwegian_1), seed)]);
			// Use 7bit for the last fake part.  Bit 11-17
			buf += ( name_norwegian_2[SeedChance(11, lengthof(name_norwegian_2), seed)]);
		}

		return 0;
	}

	static byte MakeHungarianTownName(String buf, int seed)
	{
		uint i;

		//null terminates the string for strcat
		strcpy(buf, "");

		if (SeedChance(12, 15, seed) < 3) {
			buf += ( name_hungarian_real[SeedChance(0, lengthof(name_hungarian_real), seed)]);
		} else {
			// optional first segment
			i = SeedChance(3, lengthof(name_hungarian_1) * 3, seed);
			if (i < lengthof(name_hungarian_1))
				buf += ( name_hungarian_1[i]);

			// mandatory middle segments
			buf += ( name_hungarian_2[SeedChance(3, lengthof(name_hungarian_2), seed)]);
			buf += ( name_hungarian_3[SeedChance(6, lengthof(name_hungarian_3), seed)]);

			// optional last segment
			i = SeedChance(10, lengthof(name_hungarian_4) * 3, seed);
			if (i < lengthof(name_hungarian_4)) {
				buf += ( name_hungarian_4[i]);
			}
		}

		return 0;
	}

	static byte MakeSwissTownName(String buf, int seed)
	{
		strcpy(buf, name_swiss_real[SeedChance(0, lengthof(name_swiss_real), seed)]);
		return 0;
	}

	static byte MakeDanishTownName(String buf, int seed)
	{
		int i;

		// null terminates the string for strcat
		strcpy(buf, "");

		// optional first segment
		i = SeedChanceBias(0, lengthof(name_danish_1), seed, 50);
		if (i >= 0)
			buf += ( name_danish_1[i]);

		// middle segments removed as this algorithm seems to create much more realistic names
		buf += ( name_danish_2[SeedChance( 7, lengthof(name_danish_2), seed)]);
		buf += ( name_danish_3[SeedChance(16, lengthof(name_danish_3), seed)]);

		return 0;
	}
*/
	static String MakeRussianTownName(int seed)
	{
		return name_russian_real[SeedChance(0, lengthof(name_russian_real), seed)];
	}


	final static TownGenFunction [] _town_name_generators =
	{
		TownNameGenerator::MakeEnglishOriginalTownName,
		TownNameGenerator::MakeRussianTownName,
	};
	
	
	/*
	final Function<Integer,String> _town_name_generators[] =
	{
		TownNameGenerator::MakeEnglishOriginalTownName,
		TownNameGenerator::MakeRussianTownName,
		/*
		MakeFrenchTownName,
		MakeGermanTownName,
		MakeEnglishAdditionalTownName,
		MakeSpanishTownName,
		MakeSillyTownName,
		MakeSwedishTownName,
		MakeDutchTownName,
		MakeFinnishTownName,
		MakePolishTownName,
		MakeSlovakTownName,
		MakeNorwegianTownName,
		MakeHungarianTownName,
		MakeAustrianTownName,
		MakeRomanianTownName,
		MakeCzechTownName,
		MakeSwissTownName,
		MakeDanishTownName,
	};
		*/

	/*
	// DO WE NEED THIS ANY MORE?
	#define FIXNUM(x, y, z) (((((x) << 16) / (y)) + 1) << z)

	int GetOldTownName(int townnameparts, byte old_town_name_type)
	{
		switch (old_town_name_type) {
			case 0: case 3: // English, American 
				//	Already OK 
				return townnameparts;

			case 1: // French 
				//	For some reason 86 needs to be subtracted from townnameparts
				//	0000 0000 0000 0000 0000 0000 1111 1111 
				return FIXNUM(townnameparts - 86, lengthof(name_french_real), 0);

			case 2: // German 
				DEBUG(misc, 0) ("German Townnames are buggy... (%d)", townnameparts);
				return townnameparts;

			case 4: // Latin-American 
				//	0000 0000 0000 0000 0000 0000 1111 1111 
				return FIXNUM(townnameparts, lengthof(name_spanish_real), 0);

			case 5: // Silly 
				//	NUM_SILLY_1	-	lower 16 bits
				//	NUM_SILLY_2	-	upper 16 bits without leading 1 (first 8 bytes)
				//  1000 0000 2222 2222 0000 0000 1111 1111 
				return FIXNUM(townnameparts, lengthof(name_silly_1), 0) | FIXNUM(GB(townnameparts, 16, 8), lengthof(name_silly_2), 16);
		}
		return 0;
	}
	*/
}

@FunctionalInterface
interface TownGenFunction extends Function<Integer,String> {}