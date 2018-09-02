package net.perfectdreams.dreamspicyboot

import com.fasterxml.jackson.annotation.JsonProperty

class SpicyConfig(
		@JsonProperty("plugins-folder")
		val pluginsFolder: String,
		@JsonProperty("extra-flags")
		val extraFlags: String?,
		@JsonProperty("java-path")
		val javaPath: String?,
		@JsonProperty("classpath-jars")
		val classpathJars: List<String>,
		@JsonProperty("jrebel-flags")
		val jrebelFlags: String?
)