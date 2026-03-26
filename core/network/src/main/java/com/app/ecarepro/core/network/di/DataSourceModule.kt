package com.app.ecarepro.core.network.di

import com.app.ecarepro.core.network.ActivityCalendarRemoteDataSource
import com.app.ecarepro.core.network.ConversationRemoteDataSource
import com.app.ecarepro.core.network.datasource.ConversationRemoteDataSourceImpl
import com.app.ecarepro.core.network.GlobalSearchRemoteDataSource
import com.app.ecarepro.core.network.datasource.GlobalSearchRemoteDataSourceImpl
import com.app.ecarepro.core.network.SettingsRemoteDataSource
import com.app.ecarepro.core.network.datasource.SettingsRemoteDataSourceImpl
import com.app.ecarepro.core.network.MyProfileRemoteDataSource
import com.app.ecarepro.core.network.datasource.MyProfileRemoteDataSourceImpl
import com.app.ecarepro.core.network.NotificationRemoteDataSource
import com.app.ecarepro.core.network.datasource.NotificationRemoteDataSourceImpl
import com.app.ecarepro.core.network.SurveyRemoteDataSource
import com.app.ecarepro.core.network.EBookRemoteDataSource
import com.app.ecarepro.core.network.FeeRemoteDataSource
import com.app.ecarepro.core.network.datasource.FeeRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.EBookRemoteDataSourceImpl
import com.app.ecarepro.core.network.BirthdayRemoteDataSource
import com.app.ecarepro.core.network.ClassTeacherRemoteDataSource
import com.app.ecarepro.core.network.datasource.ClassTeacherRemoteDataSourceImpl
import com.app.ecarepro.core.network.SMSReportRemoteDataSource
import com.app.ecarepro.core.network.datasource.BirthdayRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.SMSReportRemoteDataSourceImpl
import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.LibraryRemoteDataSource
import com.app.ecarepro.core.network.datasource.LibraryRemoteDataSourceImpl
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.MessageRemoteDataSource
import com.app.ecarepro.core.network.AnnouncementRemoteDataSource
import com.app.ecarepro.core.network.MenuRemoteDataSource
import com.app.ecarepro.core.network.datasource.MenuRemoteDataSourceImpl
import com.app.ecarepro.core.network.GalleryRemoteDataSource
import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.SmsRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.TaskManagerRemoteDataSource
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.datasource.ActivityCalendarRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.SurveyRemoteDataSourceImpl
import com.app.ecarepro.core.network.DashboardRemoteDataSource
import com.app.ecarepro.core.network.SmsConsumptionRemoteDataSource
import com.app.ecarepro.core.network.datasource.DashboardRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.SmsConsumptionRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.AcademicRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.AdminRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.MessageRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.AnnouncementRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.GalleryRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.SchoolRemoteDataSourceImpl
import com.app.ecarepro.core.network.DisciplineRemoteDataSource
import com.app.ecarepro.core.network.TransportRemoteDataSource
import com.app.ecarepro.core.network.datasource.DisciplineRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.TransportRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.SmsRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.StaffRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.TaskManagerRemoteDataSourceImpl
import com.app.ecarepro.core.network.datasource.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindsSchoolRemoteDataSource(
        dataSource: SchoolRemoteDataSourceImpl,
    ): SchoolRemoteDataSource

    @Binds
    abstract fun bindsUserRemoteDataSource(
        dataSource: UserRemoteDataSourceImpl,
    ): UserRemoteDataSource

    @Binds
    internal abstract fun bindsAcademicRemoteDataSource(
        dataSource: AcademicRemoteDataSourceImpl,
    ): AcademicRemoteDataSource

    @Binds
    internal abstract fun bindsAdminRemoteDataSource(
        dataSource: AdminRemoteDataSourceImpl,
    ): AdminRemoteDataSource

    @Binds
    internal abstract fun bindsStaffRemoteDataSource(
        dataSource: StaffRemoteDataSourceImpl,
    ): StaffRemoteDataSource
    @Binds
    internal abstract fun bindsSmsRemoteDataSource(
        dataSource: SmsRemoteDataSourceImpl,
    ): SmsRemoteDataSource

    @Binds
    internal abstract fun bindsTaskManagerRemoteDataSource(
        dataSource: TaskManagerRemoteDataSourceImpl,
    ): TaskManagerRemoteDataSource

    @Binds
    internal abstract fun bindsDisciplineRemoteDataSource(
        dataSource: DisciplineRemoteDataSourceImpl,
    ): DisciplineRemoteDataSource

    @Binds
    internal abstract fun bindsTransportRemoteDataSource(
        dataSource: TransportRemoteDataSourceImpl,
    ): TransportRemoteDataSource

    @Binds
    internal abstract fun bindsAnnouncementRemoteDataSource(
        dataSource: AnnouncementRemoteDataSourceImpl,
    ): AnnouncementRemoteDataSource

    @Binds

    internal abstract fun bindsGalleryRemoteDataSource(
        dataSource: GalleryRemoteDataSourceImpl,
    ): GalleryRemoteDataSource


    @Binds
    internal abstract fun bindsMenuRemoteDataSource(
        dataSource: MenuRemoteDataSourceImpl,
    ): MenuRemoteDataSource
    @Binds
    internal abstract fun bindsMessageRemoteDataSource(
        dataSource: MessageRemoteDataSourceImpl,
    ): MessageRemoteDataSource

    @Binds
    internal abstract fun bindsActivityCalendarRemoteDataSource(
        dataSource: ActivityCalendarRemoteDataSourceImpl,
    ): ActivityCalendarRemoteDataSource

    @Binds
    internal abstract fun bindsLibraryRemoteDataSource(
        dataSource: LibraryRemoteDataSourceImpl,
    ): LibraryRemoteDataSource

    @Binds
    internal abstract fun bindsEBookRemoteDataSource(
        dataSource: EBookRemoteDataSourceImpl,
    ): EBookRemoteDataSource

    @Binds

    internal abstract fun bindsFeeRemoteDataSource(
        dataSource: FeeRemoteDataSourceImpl,
    ): FeeRemoteDataSource

    @Binds
    internal abstract fun bindsSMSReportRemoteDataSource(
        dataSource: SMSReportRemoteDataSourceImpl,
    ): SMSReportRemoteDataSource

    @Binds
    internal abstract fun bindsBirthdayRemoteDataSource(
        impl: BirthdayRemoteDataSourceImpl,
    ): BirthdayRemoteDataSource

    @Binds
    internal abstract fun bindsSurveyRemoteDataSource(
        dataSource: SurveyRemoteDataSourceImpl,
    ): SurveyRemoteDataSource

    @Binds
    internal abstract fun bindsDashboardRemoteDataSource(
        dataSource: DashboardRemoteDataSourceImpl,
    ): DashboardRemoteDataSource

    @Binds
    internal abstract fun bindsSmsConsumptionRemoteDataSource(
        dataSource: SmsConsumptionRemoteDataSourceImpl,
    ): SmsConsumptionRemoteDataSource

    @Binds
    internal abstract fun bindsConversationRemoteDataSource(
        impl: ConversationRemoteDataSourceImpl,
    ): ConversationRemoteDataSource

    @Binds
    internal abstract fun bindsGlobalSearchRemoteDataSource(
        impl: GlobalSearchRemoteDataSourceImpl,
    ): GlobalSearchRemoteDataSource

    @Binds
    internal abstract fun bindsSettingsRemoteDataSource(
        impl: SettingsRemoteDataSourceImpl,
    ): SettingsRemoteDataSource

    @Binds
    internal abstract fun bindsClassTeacherRemoteDataSource(
        impl: ClassTeacherRemoteDataSourceImpl,
    ): ClassTeacherRemoteDataSource

    @Binds
    internal abstract fun bindsMyProfileRemoteDataSource(
        impl: MyProfileRemoteDataSourceImpl,
    ): MyProfileRemoteDataSource

    @Binds
    internal abstract fun bindsNotificationRemoteDataSource(
        impl: NotificationRemoteDataSourceImpl,
    ): NotificationRemoteDataSource

}