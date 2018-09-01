package net.perfectdreams.dreamspicyboot.utils

import java.util.*

class VersionComparator : Comparator<VersionTokenizer> {
	fun equals(o1: VersionTokenizer, o2: VersionTokenizer): Boolean {
		return compare(o1, o2) == 0
	}

	override fun compare(tokenizer1: VersionTokenizer, tokenizer2: VersionTokenizer): Int {
		var number1 = 0
		var number2 = 0
		var suffix1 = ""
		var suffix2 = ""

		while (tokenizer1.moveNext()) {
			if (!tokenizer2.moveNext()) {
				do {
					number1 = tokenizer1.number
					suffix1 = tokenizer1.suffix ?: ""
					if (number1 != 0 || suffix1.length != 0) {
						// Version one is longer than number two, and non-zero
						return 1
					}
				} while (tokenizer1.moveNext())

				// Version one is longer than version two, but zero
				return 0
			}

			number1 = tokenizer1.number
			suffix1 = tokenizer1.suffix ?: ""
			number2 = tokenizer2.number
			suffix2 = tokenizer2.suffix ?: ""

			if (number1 < number2) {
				// Number one is less than number two
				return -1
			}
			if (number1 > number2) {
				// Number one is greater than number two
				return 1
			}

			val empty1 = suffix1.length == 0
			val empty2 = suffix2.length == 0

			if (empty1 && empty2) continue // No suffixes
			if (empty1) return 1 // First suffix is empty (1.2 > 1.2b)
			if (empty2) return -1 // Second suffix is empty (1.2a < 1.2)

			// Lexical comparison of suffixes
			val result = suffix1.compareTo(suffix2)
			if (result != 0) return result

		}
		if (tokenizer2.moveNext()) {
			do {
				number2 = tokenizer2.number
				suffix2 = tokenizer2.suffix ?: ""
				if (number2 != 0 || suffix2.length != 0) {
					// Version one is longer than version two, and non-zero
					return -1
				}
			} while (tokenizer2.moveNext())

			// Version two is longer than version one, but zero
			return 0
		}
		return 0
	}
}