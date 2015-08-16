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
        addTimeline(schema);
        addAriticle(schema);
        addAttendance(schema);
        addFestival(schema);
        addSchedule(schema);
        addLetter(schema);
        addReport(schema);

        new DaoGenerator().generateAll(schema, "src-gen");
    }

    private static void addConfig(Schema schema) {
        Entity note = schema.addEntity("ConfigEntity");
        note.addIdProperty();
        note.addStringProperty("sid");
        note.addStringProperty("token");
        note.addStringProperty("mobile");
        note.addStringProperty("userid");
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

        ToMany classToStudents = classEntity.addToMany(student, classIdStudent);
        ToMany classToTeachers = classEntity.addToMany(teacher, classIdTeacher);
        ToMany studentToClasses = student.addToMany(classEntity, classid);

        Entity school = schema.addEntity("SchoolEntity");
        school.addStringProperty("id").notNull().primaryKey().getProperty();
        school.addStringProperty("name");
        school.addStringProperty("address");
        ToMany schoolToClass = school.addToMany(classEntity, schoolId);
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

        ToMany report2ReporItem = report.addToMany(reportItem,reportId);
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
}
