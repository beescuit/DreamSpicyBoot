package net.perfectdreams.dreamspicyboot.tags

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Tagging @JsonCreator constructor(
		@JsonProperty("tags")
		val tags: MutableList<Tag>
)