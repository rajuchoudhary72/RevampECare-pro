package com.app.ecarepro.core.data.di

import com.app.ecarepro.core.data.repository.ActivityCalendarRepositoryImpl
import com.app.ecarepro.core.data.repository.SurveyRepositoryImpl
import com.app.ecarepro.core.data.repository.AcademicRepositoryImpl
import com.app.ecarepro.core.data.repository.ClassPromotionRepositoryImpl
import com.app.ecarepro.core.data.repository.ManageRollNumberRepositoryImpl
import com.app.ecarepro.core.data.repository.UpdateHouseRepositoryImpl
import com.app.ecarepro.core.data.repository.UpdateProfilePictureRepositoryImpl
import com.app.ecarepro.core.data.repository.AnnouncementRepositoryImpl
import com.app.ecarepro.core.data.repository.FeedRepositoryImpl
import com.app.ecarepro.core.data.repository.GalleryRepositoryImpl
import com.app.ecarepro.core.data.repository.AppliedLeavesRepositoryImpl
import com.app.ecarepro.core.data.repository.DisciplineRepositoryImpl
import com.app.ecarepro.core.data.repository.MessageRepositoryImpl
import com.app.ecarepro.core.data.repository.SyllabusRepositoryImpl
import com.app.ecarepro.core.data.repository.SchoolRepositoryImpl
import com.app.ecarepro.core.data.repository.StaffProfileRepositoryImpl
import com.app.ecarepro.core.data.repository.StudentProfileRepositoryImpl
import com.app.ecarepro.core.data.repository.TaskManagerRepositoryImpl
import com.app.ecarepro.core.data.repository.TransportRepositoryImpl
import com.app.ecarepro.core.data.repository.UserRepositoryImpl
import com.app.ecarepro.core.domain.repository.ActivityCalendarRepository
import com.app.ecarepro.core.domain.repository.SurveyRepository
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.domain.repository.ClassPromotionRepository
import com.app.ecarepro.core.domain.repository.ManageRollNumberRepository
import com.app.ecarepro.core.domain.repository.UpdateHouseRepository
import com.app.ecarepro.core.domain.repository.UpdateProfilePictureRepository
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.domain.repository.FeedRepository
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.domain.repository.AppliedLeavesRepository
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.StaffProfileRepository
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.domain.repository.TaskManagerRepository
import com.app.ecarepro.core.domain.repository.TransportRepository
import com.app.ecarepro.core.data.repository.EBookRepositoryImpl
import com.app.ecarepro.core.data.repository.FeeRepositoryImpl
import com.app.ecarepro.core.data.repository.LibraryRepositoryImpl
import com.app.ecarepro.core.data.repository.MenuRepositoryImpl
import com.app.ecarepro.core.data.repository.BirthdayRepositoryImpl
import com.app.ecarepro.core.data.repository.SMSReportRepositoryImpl
import com.app.ecarepro.core.domain.repository.BirthdayRepository
import com.app.ecarepro.core.domain.repository.EBookRepository
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.domain.repository.LibraryRepository
import com.app.ecarepro.core.domain.repository.MenuRepository
import com.app.ecarepro.core.domain.repository.SMSReportRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.data.repository.ConversationRepositoryImpl
import com.app.ecarepro.core.data.repository.DashboardRepositoryImpl
import com.app.ecarepro.core.data.repository.SmsConsumptionRepositoryImpl
import com.app.ecarepro.core.domain.repository.ConversationRepository
import com.app.ecarepro.core.domain.repository.DashboardRepository
import com.app.ecarepro.core.domain.repository.SmsConsumptionRepository
import com.app.ecarepro.core.data.repository.GlobalSearchRepositoryImpl
import com.app.ecarepro.core.domain.repository.GlobalSearchRepository
import com.app.ecarepro.core.data.repository.ClassTeacherRepositoryImpl
import com.app.ecarepro.core.data.repository.KnowYourTeacherRepositoryImpl
import com.app.ecarepro.core.data.repository.MarkManagerRepositoryImpl
import com.app.ecarepro.core.domain.repository.ClassTeacherRepository
import com.app.ecarepro.core.domain.repository.KnowYourTeacherRepository
import com.app.ecarepro.core.domain.repository.MarkManagerRepository
import com.app.ecarepro.core.data.repository.SettingsRepositoryImpl
import com.app.ecarepro.core.domain.repository.SettingsRepository
import com.app.ecarepro.core.data.repository.MyProfileRepositoryImpl
import com.app.ecarepro.core.domain.repository.MyProfileRepository
import com.app.ecarepro.core.data.repository.NotificationRepositoryImpl
import com.app.ecarepro.core.domain.repository.NotificationRepository
import com.app.ecarepro.core.data.repository.QuestionPaperRepositoryImpl
import com.app.ecarepro.core.domain.repository.QuestionPaperRepository
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

    @Binds
    internal abstract fun bindsStudentProfileRepository(
        studentProfileRepositoryImpl: StudentProfileRepositoryImpl,
    ): StudentProfileRepository

    @Binds
    internal abstract fun bindsStaffProfileRepository(
        staffProfileRepositoryImpl: StaffProfileRepositoryImpl,
    ): StaffProfileRepository

    @Binds
    internal abstract fun bindsAppliedLeavesRepository(
        appliedLeavesRepositoryImpl: AppliedLeavesRepositoryImpl,
    ): AppliedLeavesRepository

    @Binds
    internal abstract fun bindsDisciplineRepository(
        disciplineRepositoryImpl: DisciplineRepositoryImpl,
    ): DisciplineRepository

    @Binds
    internal abstract fun bindsTaskManagerRepository(
        taskManagerRepositoryImpl: TaskManagerRepositoryImpl,
    ): TaskManagerRepository

    @Binds
    internal abstract fun bindsTransportRepository(
        transportRepositoryImpl: TransportRepositoryImpl,
    ): TransportRepository

    @Binds
    internal abstract fun bindsMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl,
    ): MessageRepository

    @Binds
    internal abstract fun bindsAnnouncementRepository(
        announcementRepositoryImpl: AnnouncementRepositoryImpl,
    ): AnnouncementRepository

    @Binds
    internal abstract fun bindsGalleryRepository(
        galleryRepositoryImpl: GalleryRepositoryImpl,
    ): GalleryRepository

    @Binds
    internal abstract fun bindsFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl,
    ): FeedRepository

    @Binds
    internal abstract fun bindsClassPromotionRepository(
        classPromotionRepositoryImpl: ClassPromotionRepositoryImpl,
    ): ClassPromotionRepository

    @Binds
    internal abstract fun bindsManageRollNumberRepository(
        manageRollNumberRepositoryImpl: ManageRollNumberRepositoryImpl,
    ): ManageRollNumberRepository

    @Binds
    internal abstract fun bindsUpdateHouseRepository(
        updateHouseRepositoryImpl: UpdateHouseRepositoryImpl,
    ): UpdateHouseRepository

    @Binds
    internal abstract fun bindsUpdateProfilePictureRepository(
        updateProfilePictureRepositoryImpl: UpdateProfilePictureRepositoryImpl,
    ): UpdateProfilePictureRepository

    @Binds
    internal abstract fun bindsMenuRepository(
        menuRepositoryImpl: MenuRepositoryImpl,
    ): MenuRepository

    @Binds
    internal abstract fun bindsActivityCalendarRepository(
        activityCalendarRepositoryImpl: ActivityCalendarRepositoryImpl,
    ): ActivityCalendarRepository

    @Binds
    internal abstract fun bindsLibraryRepository(
        libraryRepositoryImpl: LibraryRepositoryImpl,
    ): LibraryRepository

    @Binds
    internal abstract fun bindsEBookRepository(
        eBookRepositoryImpl: EBookRepositoryImpl,
    ): EBookRepository

    @Binds

    internal abstract fun bindsFeeRepository(
        feeRepositoryImpl: FeeRepositoryImpl,
    ): FeeRepository

    @Binds
    internal abstract fun bindsSMSReportRepository(
        impl: SMSReportRepositoryImpl,
    ): SMSReportRepository

    @Binds
    internal abstract fun bindsBirthdayRepository(
        impl: BirthdayRepositoryImpl,
    ): BirthdayRepository

    @Binds
    internal abstract fun bindsSurveyRepository(
        surveyRepositoryImpl: SurveyRepositoryImpl,
    ): SurveyRepository

    @Binds
    internal abstract fun bindsDashboardRepository(
        impl: DashboardRepositoryImpl,
    ): DashboardRepository

    @Binds
    internal abstract fun bindsSmsConsumptionRepository(
        impl: SmsConsumptionRepositoryImpl,
    ): SmsConsumptionRepository

    @Binds
    internal abstract fun bindsConversationRepository(
        impl: ConversationRepositoryImpl,
    ): ConversationRepository

    @Binds
    internal abstract fun bindsGlobalSearchRepository(
        impl: GlobalSearchRepositoryImpl,
    ): GlobalSearchRepository

    @Binds
    internal abstract fun bindsSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    internal abstract fun bindsMyProfileRepository(
        impl: MyProfileRepositoryImpl,
    ): MyProfileRepository

    @Binds
    internal abstract fun bindsNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository

    @Binds
    internal abstract fun bindsMarkManagerRepository(
        impl: MarkManagerRepositoryImpl,
    ): MarkManagerRepository

    @Binds
    internal abstract fun bindsKnowYourTeacherRepository(
        impl: KnowYourTeacherRepositoryImpl,
    ): KnowYourTeacherRepository

    @Binds
    internal abstract fun bindsClassTeacherRepository(
        impl: ClassTeacherRepositoryImpl,
    ): ClassTeacherRepository

    @Binds
    internal abstract fun bindsQuestionPaperRepository(
        impl: QuestionPaperRepositoryImpl,
    ): QuestionPaperRepository

}