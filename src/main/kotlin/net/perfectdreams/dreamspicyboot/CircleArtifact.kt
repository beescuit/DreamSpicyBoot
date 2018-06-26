package net.perfectdreams.dreamspicyboot

import com.google.gson.annotations.SerializedName

class CircleArtifact(
		val path: String,
		@SerializedName("pretty_path")
		val prettyPath: String,
		@SerializedName("node_index")
		val nodeIndex: Int,
		val url: String
)