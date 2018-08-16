package net.perfectdreams.dreamspicyboot

import com.fasterxml.jackson.annotation.JsonProperty

enum class ServerVersion {
	@JsonProperty("Minecraft 1.13")
	MC_1_13,
	@JsonProperty("Minecraft 1.12.2")
	MC_1_12_2;

	fun getPretty(): String {
		return when (this) {
			MC_1_13 -> "1.13"
			MC_1_12_2 -> "1.12.2"
		}
	}
}