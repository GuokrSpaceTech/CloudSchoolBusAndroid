package com.guokrspace;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;
import de.greenrobot.daogenerator.ToOne;

public class DAODBGenerator {


    /**
     * Generates entities and DAOs for the example project DaoExample.
     * <p/>
     * Run it as a Java application (not Android).
     *
     * @author Markus
     */

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "com.guokrspace");

        addConfig(schema);
        addBaseInfo(schema);
        addBaseInfoTeacher(schema);
        addTimeline(schema);
        addAriticle(schema);
        addAttendance(schema);
        addFestival(schema);
        addSchedule(schema);
        addLetter(schema);
        addReport(schema);
        addUploadingPhotos(schema);

        new DaoGenerator().generateAll(schema, "src-gen");
    }

    private static void addConfig(Schema schema) {
        Entity note = schema.addEntity("ConfigEntity");
        note.addIdProperty();
        note.addStringProperty("sid");
        note.addStringProperty("token");
        note.addStringProperty("mobile");
        note.addStringProperty("userid");
        note.addStringProperty("imToken");
        note.addIntProperty("currentChild");
    }

    private static void addBaseInfo(Schema schema) {
        Entity student = schema.addEntity("StudentEntity");
        student.addStringProperty("cnname");
        student.addStringProperty("birthday");
        student.addStringProperty("sex");
        student.addStringProperty("avatar");
        student.addStringProperty("nikename");
        student.addStringProperty("studentid").notNull().primaryKey();
        Property classIdStudent = student.addStringProperty("classid").notNull().getProperty();

        Entity teacher = schema.addEntity("TeacherEntity");
        teacher.addStringProperty("id").notNull().primaryKey();
        teacher.addStringProperty("duty");
        teacher.addStringProperty("avatar");
        teacher.addStringProperty("name");
        Property classIdTeacher = teacher.addStringProperty("classid").notNull().getProperty();

        Entity classEntity = schema.addEntity("ClassEntity");
        Property classid = classEntity.addStringProperty("classid").notNull().primaryKey().getProperty();
        classEntity.addStringProperty("name");
        Property schoolId = classEntity.addStringProperty("schoolid").notNull().getProperty();

        Entity lastIMMessageEntity = schema.addEntity("LastIMMessageEntity");
        Property teacherid = lastIMMessageEntity.addStringProperty("teacherid").notNull().primaryKey().getProperty();
        lastIMMessageEntity.addStringProperty("timestamp");
        lastIMMessageEntity.addStringProperty("hasUnread");

        ToMany teacherToMessages = teacher.addToMany(lastIMMessageEntity,teacherid);
        ToMany classToStudents = classEntity.addToMany(student, classIdStudent);
        ToMany classToTeachers = classEntity.addToMany(teacher, classIdTeacher);
        ToMany studentToClasses = student.addToMany(classEntity, classid);

        Entity school = schema.addEntity("SchoolEntity");
        school.addStringProperty("id").notNull().primaryKey().getProperty();
        school.addStringProperty("name");
        school.addStringProperty("address");
        ToMany schoolToClass = school.addToMany(classEntity, schoolId);
    }

    private static void addBaseInfoTeacher(Schema schema) {

        Entity school = schema.addEntity("SchoolEntityT");
        Property schoolid = school.addStringProperty("id").notNull().primaryKey().getProperty();
        school.addStringProperty("groupid");
        school.addStringProperty("name");
        school.addStringProperty("remark");
        school.addStringProperty("address");

        Entity classEntity = schema.addEntity("ClassEntityT");
        Property classId = classEntity.addStringProperty("classid").notNull().primaryKey().getProperty();
        classEntity.addStringProperty("classname");
        classEntity.addStringProperty("remark");
        classEntity.addStringProperty("dutyid");
        Property schoolidclass = classEntity.addStringProperty("schoolid").notNull().getProperty();

        Entity student = schema.addEntity("StudentEntityT");
        Property studentId = student.addStringProperty("studentid").notNull().primaryKey().getProperty();
        student.addStringProperty("nikename");
        student.addStringProperty("cnname");
        student.addStringProperty("sex");
        student.addStringProperty("birthday");
        student.addStringProperty("avatar");

        Entity parent = schema.addEntity("ParentEntityT");
        Property parentId = parent.addStringProperty("parentid").notNull().primaryKey().getProperty();
        parent.addStringProperty("nikename");
        parent.addStringProperty("relationship");
        parent.addStringProperty("mobile");
        parent.addStringProperty("avatar");

        Entity teacher = schema.addEntity("TeacherEntityT");
        Property teacherId = teacher.addStringProperty("teacherid").notNull().primaryKey().getProperty();
        teacher.addStringProperty("duty");
        teacher.addStringProperty("avatar");
        teacher.addStringProperty("realname");
        teacher.addStringProperty("nickname");
        teacher.addStringProperty("sex");
        teacher.addStringProperty("mobile");
        Property schoolidteacher = teacher.addStringProperty("schoolid").notNull().getProperty();

        //Settings related tables
        Entity tags = schema.addEntity("TagsEntityT");
        Property tagsId = tags.addStringProperty("tagid").primaryKey().getProperty();
        tags.addStringProperty("tagname");
        tags.addStringProperty("tagname_en");
        tags.addStringProperty("tagnamedesc");
        tags.addStringProperty("tagnamedesc_en");
        Property schoolidtags = tags.addStringProperty("schoolid").notNull().getProperty();

        Entity messagetype = schema.addEntity("MessageTypeEntity");
        Property messgetypeid = messagetype.addStringProperty("id").notNull().primaryKey().getProperty();
        messagetype.addStringProperty("type");
        Property schoolidmessagetype = messagetype.addStringProperty("schoolid").notNull().getProperty();

        Entity classmodule = schema.addEntity("ClassModuleEntity");
        classmodule.addStringProperty("id").notNull().primaryKey();
        classmodule.addStringProperty("icon");
        classmodule.addStringProperty("url");
        classmodule.addStringProperty("title");
        Property schoolidclassmodule = classmodule.addStringProperty("schoolid").notNull().getProperty();

        Entity teacherDuty = schema.addEntity("TeacherDutyEntity");
        teacherDuty.addStringProperty("id").primaryKey().notNull();
        teacherDuty.addStringProperty("duty");
        Property schoolidteacherduty = teacherDuty.addStringProperty("schoolid").notNull().getProperty();

        //Many2many Relationship Tables
        Entity teacherdutyclass = schema.addEntity("TeacherDutyClassRelationEntity");
        teacherdutyclass.addStringProperty("classid");
        teacherdutyclass.addStringProperty("dutyid");
        teacherdutyclass.addStringProperty("teacherid");

        Entity studentClassEntity = schema.addEntity("StudentClassRelationEntity");
        studentClassEntity.addStringProperty("studentid");
        studentClassEntity.addStringProperty("classid");

        Entity studentParentEntity = schema.addEntity("StudentParentRelationEntity");
        studentParentEntity.addStringProperty("studentid");
        studentParentEntity.addStringProperty("parentid");

        ToMany schoolToClass = school.addToMany(classEntity, schoolidclass);
        ToMany schoolToTags = school.addToMany(tags, schoolidtags);
        ToMany schoolTomessagetype = school.addToMany(messagetype, schoolidmessagetype);
        ToMany schoolToclassmodule = school.addToMany(classmodule, schoolidclassmodule);
        ToMany schoolToduty = school.addToMany(teacherDuty, schoolidteacherduty);
        ToMany schoolToTeacher = school.addToMany(teacher,schoolidteacher);
    }

    private static void addTimeline(Schema schema)
    {
        Entity sender = schema.addEntity("SenderEntity");
        sender.addStringProperty("id").notNull().primaryKey();
        sender.addStringProperty("role");
        sender.addStringProperty("avatar");
        sender.addStringProperty("classname");
        sender.addStringProperty("name");

        Entity tag = schema.addEntity("TagEntity");
        tag.addStringProperty("tagid").notNull().primaryKey();
        tag.addStringProperty("tagName");
        tag.addStringProperty("tagnamedesc");
        tag.addStringProperty("tagname_en");
        tag.addStringProperty("tagnamedesc_en");
        Property messageIdTag = tag.addStringProperty("messageid").notNull().getProperty();

        Entity message = schema.addEntity("MessageEntity");
        message.addStringProperty("messageid").notNull().primaryKey().getProperty();
        message.addStringProperty("title");
        message.addStringProperty("description");
        message.addStringProperty("isconfirm");
        message.addStringProperty("sendtime");
        message.addStringProperty("apptype");
        message.addStringProperty("studentid");
        message.addStringProperty("ismass");
        message.addStringProperty("isreaded");
        message.addStringProperty("body");
        Property senderIdMessage = message.addStringProperty("senderid").notNull().getProperty();

        ToMany messageToTag    = message.addToMany(tag,      messageIdTag);
        ToOne  messageToSender = message.addToOne(sender, senderIdMessage);
    }

    private static void addReport(Schema schema) {
        Entity report = schema.addEntity("ReportEntity");
        report.addStringProperty("id").notNull().primaryKey();
        report.addStringProperty("title");
        report.addStringProperty("cnname");
        report.addStringProperty("reportname");
        report.addStringProperty("studentlist");
        report.addStringProperty("reporttime");
        report.addStringProperty("createtime");
        report.addStringProperty("type");
        report.addStringProperty("adduserid");
        report.addStringProperty("teachername");
        report.addStringProperty("studentlistid");
        report.addStringProperty("studentname");

        Entity reportItem = schema.addEntity("ReportItemEntity");
        reportItem.addStringProperty("title");
        reportItem.addStringProperty("answer");
        Property reportId = reportItem.addStringProperty("reportId").notNull().getProperty();

        ToMany report2ReporItem = report.addToMany(reportItem, reportId);
    }

    private static void addNotice(Schema schema)
    {
        Entity notice = schema.addEntity("NoticeEntity");
        notice.addStringProperty("noticekey").notNull().primaryKey();
        notice.addStringProperty("noticeid");
        notice.addStringProperty("noticetitle");
        notice.addStringProperty("noticecontent");
        notice.addStringProperty("addtime");
        notice.addStringProperty("isteacher");
        notice.addStringProperty("isconfirm");
        notice.addStringProperty("haveisconfirm");

        Entity noticeImage =  schema.addEntity("NoticeImageEntity");
        noticeImage.addStringProperty("source");
        noticeImage.addStringProperty("filename");
        noticeImage.addStringProperty("iscloud");
        Property notice_key_image = noticeImage.addStringProperty("noticekey").notNull().getProperty();

        noticeImage.addToOne(notice,notice_key_image);
        notice.addToMany(noticeImage,notice_key_image).setName("noticeImages");
    }

    private static void addAttendance(Schema schema)
    {
        Entity attendance = schema.addEntity("AttendanceEntity");
        attendance.addStringProperty("month");
        attendance.addStringProperty("day");
        attendance.addStringProperty("timestamp").notNull().primaryKey();
        attendance.addStringProperty("imageUrl");
    }

    private static void addFestival(Schema schema)
    {
        Entity festival = schema.addEntity("FestivalEntity");
        festival.addStringProperty("date"); // 2015-04-17
        festival.addStringProperty("festivalName");
    }

    private static void addSchedule(Schema schema)
    {
        Entity schedule = schema.addEntity("ScheduleEntity");
        schedule.addIntProperty("starthour");
        schedule.addIntProperty("startmin");
        schedule.addIntProperty("endhour");
        schedule.addIntProperty("endmin");
        schedule.addStringProperty("cnname");
        schedule.addStringProperty("enname");
        schedule.addIntProperty("week");
        schedule.addIntProperty("year");
    }

    private static void addAriticle(Schema schema)
    {
        Entity article = schema.addEntity("ArticleEntity");
        article.addStringProperty("articlekey").notNull().primaryKey();
        article.addStringProperty("tag");
        article.addStringProperty("articleid");
        article.addStringProperty("title");
        article.addStringProperty("content");
        article.addStringProperty("publishtime");
        article.addStringProperty("addtime");
        article.addStringProperty("upnum");
        article.addStringProperty("commentnum");
        article.addStringProperty("havezan");

        Entity image = schema.addEntity("ImageEntity");
        image.addStringProperty("filename").notNull().primaryKey();
        image.addStringProperty("source");
        image.addStringProperty("fext");
        image.addStringProperty("size");
        image.addStringProperty("isCloud");
        Property ariticle_id_image = image.addStringProperty("articleId").notNull().getProperty();

        image.addToOne(article,ariticle_id_image);
        ToMany ariticleToImages = article.addToMany(image, ariticle_id_image);
        ariticleToImages.setName("images");
    }

    private static void addLetter(Schema schema)
    {
        Entity LastLetter = schema.addEntity("LastLetterEntity");
        LastLetter.addStringProperty("teacherid").primaryKey().notNull();
        LastLetter.addStringProperty("lastchat");
        LastLetter.addStringProperty("picture");

        Entity letters = schema.addEntity("LetterEntity");
        letters.addStringProperty("letterid");
        letters.addStringProperty("letter_type");
        letters.addStringProperty("from_role");
        letters.addStringProperty("from_id");
        letters.addStringProperty("to_role");
        letters.addStringProperty("to_id");
        letters.addStringProperty("addtime");
        letters.addStringProperty("content");
        letters.addBooleanProperty("isShowDate");
    }

    private static void addUploadingPhotos(Schema schema)
    {
        Entity uploading = schema.addEntity("UploadingPhotoEntity");
        uploading.addStringProperty("key").primaryKey().notNull();
        uploading.addStringProperty("picPathString");
        uploading.addStringProperty("picFileString");
        uploading.addStringProperty("picSizeString");
        uploading.addStringProperty("studentId");
        uploading.addStringProperty("classuid");
        uploading.addStringProperty("intro");
        uploading.addStringProperty("photoTag");
        uploading.addStringProperty("teacherid");
    }
}
