package net.perfectdreams.dreamspicyboot.tags

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Tag @JsonCreator constructor(
		@JsonProperty("name")
		val name: String,
		@JsonProperty("jar-name")
		val jarName: String
)