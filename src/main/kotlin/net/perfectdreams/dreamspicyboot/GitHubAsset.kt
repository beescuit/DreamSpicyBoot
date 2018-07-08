package net.perfectdreams.dreamspicyboot

import com.google.gson.annotations.SerializedName

class GitHubAsset(
		val url: String,
		val id: Int,
		@SerializedName("node_id")
		val nodeId: String,
		val name: String,
		val label: String?,
		@SerializedName("content_type")
		val contentType: String,
		val state: String,
		val size: Int,
		@SerializedName("download_count")
		val downloadCount: Int,
		@SerializedName("created_at")
		val createdAt: String,
		@SerializedName("updated_at")
		val updatedAt: String,
		@SerializedName("browser_download_url")
		val browserDownloadUrl: String
)