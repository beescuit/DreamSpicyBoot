package net.perfectdreams.dreamspicyboot.utils

class VersionTokenizer(private val _versionString: String?) {
	private val _length: Int

	private var _position: Int = 0
	var number: Int = 0
		private set
	var suffix: String? = null
		private set
	private var _hasValue: Boolean = false

	fun hasValue(): Boolean {
		return _hasValue
	}

	init {
		if (_versionString == null)
			throw IllegalArgumentException("versionString is null")
		_length = _versionString.length
	}

	fun moveNext(): Boolean {
		number = 0
		suffix = ""
		_hasValue = false

		// No more characters
		if (_position >= _length)
			return false

		_hasValue = true

		while (_position < _length) {
			val c = _versionString!!.get(_position)
			if (c < '0' || c > '9') break
			number = number * 10 + (c - '0')
			_position++
		}

		val suffixStart = _position

		while (_position < _length) {
			val c = _versionString!!.get(_position)
			if (c == '.') break
			_position++
		}

		suffix = _versionString!!.substring(suffixStart, _position)

		if (_position < _length) _position++

		return true
	}
}