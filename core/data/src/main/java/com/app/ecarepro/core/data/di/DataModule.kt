package com.app.ecarepro.core.data.di

import com.app.ecarepro.core.data.repository.AcademicRepositoryImpl
import com.app.ecarepro.core.data.repository.SyllabusRepositoryImpl
import com.app.ecarepro.core.data.repository.SchoolRepositoryImpl
import com.app.ecarepro.core.data.repository.UserRepositoryImpl
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsSchoolRepository(
        schoolRepositoryImpl: SchoolRepositoryImpl,
    ): SchoolRepository

    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl,
    ): UserRepository

    @Binds
    internal abstract fun bindsAcademicRepository(
        academicRepositoryImpl: AcademicRepositoryImpl,
    ): AcademicRepository

    @Binds
    internal abstract fun bindsAdminRepository(
        academicRepositoryImpl: SyllabusRepositoryImpl,
    ): SyllabusRepository

}