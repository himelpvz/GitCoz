package com.hypex.gitcoz.domain.repository

import com.hypex.gitcoz.domain.model.*
import kotlinx.coroutines.flow.Flow

interface GitHubRepositoryContract {
    fun getAuthenticatedUser(): Flow<GitHubUser>
    fun getUserProfile(username: String): Flow<GitHubUser>
    fun getUserRepos(username: String): Flow<List<GitHubRepository>>
    fun getTrendingRepos(date: String): Flow<List<GitHubRepository>>
    fun getTopRepos(language: String? = null): Flow<List<GitHubRepository>>
    fun searchRepos(query: String): Flow<List<GitHubRepository>>
    fun searchUsers(query: String): Flow<List<GitHubUser>>
    fun getTopReleases(repos: List<Pair<String, String>>): Flow<List<GitHubRelease>>
    fun getRepoReleases(owner: String, repo: String): Flow<List<GitHubRelease>>
    fun getReadme(owner: String, repo: String): Flow<String>
    fun getUserReadme(username: String): Flow<String>
}
