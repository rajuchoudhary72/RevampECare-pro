package com.app.ecarepro.di

import com.app.ecarepro.data.AcademicRepoImpl
import com.app.ecarepro.data.LibraryRepoImpl
import com.app.ecarepro.data.SchoolRepositoryImpl
import com.app.ecarepro.data.StaffRepositoryImpl
import com.app.ecarepro.data.ThoughtsRepoImpl
import com.app.ecarepro.data.UserRepositoryImpl
import com.app.ecarepro.data.repository.AcademicRepo
import com.app.ecarepro.data.repository.LibraryRepo
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.StaffRepository
import com.app.ecarepro.data.repository.ThoughtsRepo
import com.app.ecarepro.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class DataBinds {
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindSchoolRepository(
        impl: SchoolRepositoryImpl
    ): SchoolRepository

    @Binds
    abstract fun bindStaffRepository(
        impl: StaffRepositoryImpl
    ): StaffRepository

    @Binds
    abstract fun bindThoughtsRepository(
        impl: ThoughtsRepoImpl
    ): ThoughtsRepo

    @Binds
    abstract fun bindLibraryRepository(
        impl: LibraryRepoImpl
    ): LibraryRepo

    @Binds
    abstract fun bindAcademicRepo(
        impl: AcademicRepoImpl
    ): AcademicRepo

}