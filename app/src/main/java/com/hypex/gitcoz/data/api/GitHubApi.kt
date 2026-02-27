package com.hypex.gitcoz.data.api

import com.hypex.gitcoz.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("user")
    suspend fun getAuthenticatedUser(): UserDto

    @GET("users/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): UserDto

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String
    ): List<RepositoryDto>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): SearchResponseDto

    @GET("search/repositories")
    suspend fun getTopRepositories(
        @Query("q") query: String = "stars:>1",
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30
    ): SearchResponseDto

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 20
    ): SearchUsersResponseDto

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReleaseDto

    @GET("repos/{owner}/{repo}/releases")
    suspend fun getRepoReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 100
    ): List<ReleaseDto>

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReadmeDto
}
