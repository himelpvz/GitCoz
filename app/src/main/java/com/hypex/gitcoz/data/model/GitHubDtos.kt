package com.hypex.gitcoz.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("name") val name: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int,
    @SerializedName("public_repos") val publicRepos: Int,
    @SerializedName("location") val location: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("blog") val blog: String?,
    @SerializedName("twitter_username") val twitterUsername: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("public_gists") val publicGists: Int?
)

data class SearchUserDto(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String
)

data class SearchUsersResponseDto(
    @SerializedName("items") val items: List<SearchUserDto>
)

data class RepositoryDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("owner") val owner: OwnerDto,
    @SerializedName("html_url") val htmlUrl: String
)

data class OwnerDto(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class SearchResponseDto(
    @SerializedName("items") val items: List<RepositoryDto>
)

data class ReleaseDto(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String?,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("html_url") val htmlUrl: String
)

data class ReadmeDto(
    @SerializedName("content") val content: String,
    @SerializedName("encoding") val encoding: String
)
