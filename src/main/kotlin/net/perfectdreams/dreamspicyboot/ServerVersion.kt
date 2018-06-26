package net.perfectdreams.dreamspicyboot

import com.google.gson.annotations.SerializedName

enum class ServerVersion {
	@SerializedName("1.12.2")
	MC_1_12_2;

	fun getPretty(): String {
		return when (this) {
			MC_1_12_2 -> "1.12.2"
		}
	}
}