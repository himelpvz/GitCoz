package com.hypex.gitcoz.domain.model

data class GitHubUser(
    val login: String,
    val avatarUrl: String,
    val name: String,
    val bio: String,
    val followers: Int,
    val following: Int,
    val publicRepos: Int,
    val location: String = "",
    val company: String = "",
    val blog: String = "",
    val twitterUsername: String = "",
    val createdAt: String = "",
    val publicGists: Int = 0
)

data class GitHubRepository(
    val id: Long,
    val name: String,
    val description: String,
    val stars: Int,
    val language: String,
    val ownerLogin: String,
    val ownerAvatar: String,
    val htmlUrl: String
)

data class GitHubRelease(
    val tagName: String,
    val name: String,
    val publishedAt: String,
    val htmlUrl: String,
    val repoName: String
)
