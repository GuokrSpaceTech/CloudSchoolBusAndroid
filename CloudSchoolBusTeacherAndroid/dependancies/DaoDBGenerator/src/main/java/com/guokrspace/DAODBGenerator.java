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
        note.addStringProperty("imToken");
        note.addIntProperty("currentuser");
    }

    private static void addBaseInfo(Schema schema) {
        Entity schoolT = schema.addEntity("SchoolEntityT");
        Property schoolid = schoolT.addStringProperty("id").notNull().primaryKey().getProperty();
        schoolT.addStringProperty("groupid");
        schoolT.addStringProperty("name");
        schoolT.addStringProperty("remark");
        schoolT.addStringProperty("address");
        schoolT.addStringProperty("logo");
        schoolT.addStringProperty("cover");

        Entity classEntityT = schema.addEntity("ClassEntityT");
        Property classId = classEntityT.addStringProperty("classid").notNull().primaryKey().getProperty();
        classEntityT.addStringProperty("classname");
        classEntityT.addStringProperty("remark");
        classEntityT.addStringProperty("dutyid");
        Property schoolidclass = classEntityT.addStringProperty("schoolid").notNull().getProperty();

        Entity studentT = schema.addEntity("StudentEntityT");
        Property studentId = studentT.addStringProperty("studentid").notNull().primaryKey().getProperty();
        studentT.addStringProperty("nikename");
        studentT.addStringProperty("cnname");
        studentT.addStringProperty("sex");
        studentT.addStringProperty("birthday");
        studentT.addStringProperty("avatar");
        Property uploadArticleIdStudent = studentT.addStringProperty("pickey").getProperty();

        Entity parentT = schema.addEntity("ParentEntityT");
        Property parentId = parentT.addStringProperty("parentid").notNull().primaryKey().getProperty();
        parentT.addStringProperty("nikename");
        parentT.addStringProperty("relationship");
        parentT.addStringProperty("mobile");
        parentT.addStringProperty("avatar");

        Entity teacherT = schema.addEntity("TeacherEntityT");
        Property teacherId = teacherT.addStringProperty("teacherid").notNull().primaryKey().getProperty();
        teacherT.addStringProperty("duty");
        teacherT.addStringProperty("avatar");
        teacherT.addStringProperty("realname");
        teacherT.addStringProperty("nickname");
        teacherT.addStringProperty("sex");
        teacherT.addStringProperty("mobile");
        Property schoolidteacher = teacherT.addStringProperty("schoolid").notNull().getProperty();

        //Settings related tables
        Entity tags = schema.addEntity("TagsEntityT");
        Property tagsId = tags.addStringProperty("tagid").primaryKey().getProperty();
        tags.addStringProperty("tagname");
        tags.addStringProperty("tagname_en");
        tags.addStringProperty("tagnamedesc");
        tags.addStringProperty("tagnamedesc_en");
        Property schoolidtags = tags.addStringProperty("schoolid").notNull().getProperty();
        Property uploadArticleIdTags = tags.addStringProperty("pickey").getProperty();

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
        teacherDuty.addStringProperty("id").notNull();
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

        ToMany schoolToClassT = schoolT.addToMany(classEntityT, schoolidclass);
        ToMany schoolToTagsT = schoolT.addToMany(tags, schoolidtags);
        ToMany schoolTomessagetypeT = schoolT.addToMany(messagetype, schoolidmessagetype);
        ToMany schoolToclassmoduleT = schoolT.addToMany(classmodule, schoolidclassmodule);
        ToMany schoolTodutyT = schoolT.addToMany(teacherDuty, schoolidteacherduty);
        ToMany schoolToTeacherT = schoolT.addToMany(teacherT,schoolidteacher);

        Entity article = schema.addEntity("UploadArticleEntity");
        article.addStringProperty("pickey").primaryKey().notNull();
        article.addStringProperty("pictype");
        article.addStringProperty("classid");
        article.addStringProperty("teacherid");
        article.addStringProperty("content");
        article.addStringProperty("sendtime");
        article.addStringProperty("studentids");
        article.addStringProperty("tagids");

        Entity singlefile = schema.addEntity("UploadArticleFileEntity");
        singlefile.addIdProperty().primaryKey().autoincrement();
        singlefile.addStringProperty("fbody");
        singlefile.addStringProperty("thumb");
        singlefile.addStringProperty("compress");
        singlefile.addStringProperty("fname");
        singlefile.addStringProperty("ftime");
        singlefile.addStringProperty("pictype");
        singlefile.addBooleanProperty("isSuccess");
        Property pickey = singlefile.addStringProperty("pickey").notNull().getProperty();

        Entity lastIMMessageEntity = schema.addEntity("LastIMMessageEntity");
        lastIMMessageEntity.addIdProperty().primaryKey().autoincrement();
        lastIMMessageEntity.addStringProperty("timestamp");
        lastIMMessageEntity.addStringProperty("hasUnread");
        Property userid = lastIMMessageEntity.addStringProperty("userid").notNull().getProperty();

        ToMany parentToLastIm = parentT.addToMany(lastIMMessageEntity, userid);
        ToMany teacherToLastIM = teacherT.addToMany(lastIMMessageEntity,userid);
        ToMany articleToFiles = article.addToMany(singlefile, pickey);
    }

    private static void addTimeline(Schema schema)
    {
        Entity sender = schema.addEntity("SenderEntity");
        sender.addStringProperty("id").notNull().primaryKey();
        sender.addStringProperty("role");
        sender.addStringProperty("avatar");
        sender.addStringProperty("classname");
        sender.addStringProperty("name");

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
        message.addStringProperty("tagids");
        Property senderIdMessage = message.addStringProperty("senderid").getProperty();

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
}
