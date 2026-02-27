package com.hypex.gitcoz.data.repository

import com.hypex.gitcoz.data.api.GitHubApi
import com.hypex.gitcoz.domain.model.*
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.util.Base64

class GitHubRepositoryImpl(
    private val api: GitHubApi
) : GitHubRepositoryContract {

    override fun getAuthenticatedUser(): Flow<GitHubUser> = flow {
        val userDto = api.getAuthenticatedUser()
        emit(userDto.toDomainUser())
    }

    override fun getUserProfile(username: String): Flow<GitHubUser> = flow {
        val userDto = api.getUserProfile(username)
        emit(userDto.toDomainUser())
    }

    override fun getUserRepos(username: String): Flow<List<GitHubRepository>> = flow {
        val reposDto = api.getUserRepos(username)
        emit(reposDto.map { it.toDomain() })
    }

    override fun getTrendingRepos(date: String): Flow<List<GitHubRepository>> = flow {
        val response = api.searchRepositories("created:>$date", sort = "stars", order = "desc")
        emit(response.items.map { it.toDomain() })
    }

    override fun getTopRepos(language: String?): Flow<List<GitHubRepository>> = flow {
        val query = if (language.isNullOrBlank() || language == "All") {
            "stars:>1000"
        } else {
            "language:$language stars:>100"
        }
        val response = api.getTopRepositories(query)
        emit(response.items.map { it.toDomain() })
    }

    override fun searchRepos(query: String): Flow<List<GitHubRepository>> = flow {
        val response = api.searchRepositories(query)
        emit(response.items.map { it.toDomain() })
    }

    override fun searchUsers(query: String): Flow<List<GitHubUser>> = flow {
        val response = api.searchUsers(query)
        val users = response.items.map { searchUser ->
            try {
                val userDto = api.getUserProfile(searchUser.login)
                userDto.toDomainUser()
            } catch (e: Exception) {
                GitHubUser(
                    login = searchUser.login,
                    avatarUrl = searchUser.avatarUrl,
                    name = searchUser.login,
                    bio = "No bio available",
                    followers = 0,
                    following = 0,
                    publicRepos = 0
                )
            }
        }
        emit(users)
    }

    override fun getTopReleases(repos: List<Pair<String, String>>): Flow<List<GitHubRelease>> = flow {
        val releases = repos.mapNotNull { (owner, repo) ->
            try {
                val releaseDto = api.getLatestRelease(owner, repo)
                releaseDto.toDomainRelease(repo)
            } catch (e: Exception) {
                null
            }
        }
        emit(releases)
    }

    override fun getRepoReleases(owner: String, repo: String): Flow<List<GitHubRelease>> = flow {
        val releases = api.getRepoReleases(owner, repo)
            .map { it.toDomainRelease(repo) }
        emit(releases)
    }

    override fun getReadme(owner: String, repo: String): Flow<String> = flow {
        try {
            val readmeDto = api.getReadme(owner, repo)
            val decodedBytes = Base64.decode(readmeDto.content.replace("\n", ""), Base64.DEFAULT)
            emit(String(decodedBytes))
        } catch (e: Exception) {
            emit("README not found or could not be loaded.")
        }
    }

    override fun getUserReadme(username: String): Flow<String> = flow {
        try {
            val readmeDto = api.getReadme(username, username)
            val decodedBytes = Base64.decode(readmeDto.content.replace("\n", ""), Base64.DEFAULT)
            emit(String(decodedBytes))
        } catch (e: Exception) {
            emit("User profile README not found.")
        }
    }

    private fun com.hypex.gitcoz.data.model.RepositoryDto.toDomain() = GitHubRepository(
        id = id,
        name = name,
        description = description ?: "No description",
        stars = stars,
        language = language ?: "Unknown",
        ownerLogin = owner.login,
        ownerAvatar = owner.avatarUrl,
        htmlUrl = htmlUrl
    )

    private fun com.hypex.gitcoz.data.model.UserDto.toDomainUser() = GitHubUser(
        login = login,
        avatarUrl = avatarUrl,
        name = name ?: login,
        bio = bio ?: "No bio available",
        followers = followers,
        following = following,
        publicRepos = publicRepos,
        location = location ?: "",
        company = company ?: "",
        blog = blog ?: "",
        twitterUsername = twitterUsername ?: "",
        createdAt = createdAt ?: "",
        publicGists = publicGists ?: 0
    )

    private fun com.hypex.gitcoz.data.model.ReleaseDto.toDomainRelease(repoName: String) = GitHubRelease(
        tagName = tagName,
        name = name ?: tagName,
        publishedAt = publishedAt,
        htmlUrl = htmlUrl,
        repoName = repoName
    )
}
