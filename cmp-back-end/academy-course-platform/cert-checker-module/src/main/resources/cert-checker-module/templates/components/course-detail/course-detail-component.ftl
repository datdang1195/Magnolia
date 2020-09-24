[#assign courseCompulsory = model.getCurrentCourseCompulsory()!"Course not found"]
[#assign currentUser = model.getCurrentUser()!"User is anonymous!"]
[#assign userProfile = model.getUserProfile()!""]
[#assign commentList = model.getComments()!""]
[#assign allComment = model.getCommentList()!""]
[#assign totalCommentList = model.getTotalComment()!""]
[#assign courseId = ctx.uuid!""]
<script>
    window.EKINO_ACADEMY['currentPage'] = "courseDetail"
    var courseId = "${courseId}";
    var userId = "${currentUser.getIdentifier()}";
    var commentList = JSON.parse('${allComment}');
    var contextPath = "${ctx.contextPath}";
    window.userProfile = {
        photoUrl: "${userProfile.headerThumbnail}",
        userId: "${userProfile.uuid}",
        fullName: "${userProfile.name}"

    };
    window.EKINO_ACADEMY.page['courseDetail'] = {
        commentList:commentList,
        courseId:courseId,
        contextPath:contextPath
    }
</script>

[#if currentUser?has_content && currentUser.getName() != 'anonymous']
    <div class="app-wrapper__courseDetail">
    <div id="loading" class="loading">
        <div class="loading__content">
            <i></i>
        </div>
    </div>

    [#if courseCompulsory != "Course not found" ]
        <div class="container-courseDetail">
            <div class="courseDetail">
                <p class="page__title">${courseCompulsory.getCourseDetail().getNodeName()!"Course not found"}</p>
                [#if model.getCourseTitleDetails()?size == 2]
                    <div class="course">
                        <p class="course__level">${model.getCourseTitleDetails()[0]!"None"}
                        <p class="line">&nbsp;&dash;&nbsp;</p>
                        <p class="course__subject">${model.getCourseTitleDetails()[1]!"None"}</p>

                    </div>
                [/#if]
                <div class="duration status-${courseCompulsory.getCourseStatus().getNodeName()?lower_case}">
                    <div class="duration__lable">
                        [#if courseCompulsory.getCourseDetail().getGroup()??]
                            [#if courseCompulsory.getCourseDetail().getGroup().getIcon()??]
                                <img src="${courseCompulsory.getCourseDetail().getGroup().getIcon().getLink()!"Link not found"}">
                                ${courseCompulsory.getCourseDetail().getGroup().getDisplayName()!"Group not found"}
                            [#else ]
                                <img src=""> ${courseCompulsory.getCourseDetail().getGroup().getDisplayName()!"Group not found"}
                            [/#if]
                        [#else ]
                            <img src=""> No Group
                        [/#if]

                    </div>
                    <img class="line"
                         src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/line.png"
                         alt="line">
                    <div class="wrapper-progress">
                        <div class="progress-item">
                            <div class="progress progress__open progress--borderLeft">
                                <div class="progress-status"></div>
                            </div>
                            <p class="progress__lable">open</p>
                        </div>
                        <div class="progress-item">
                            <div class="progress progress__class">
                                <div class="progress-status"></div>
                            </div>
                            <p class="progress__lable">class</p>
                        </div>
                        <div class="progress-item">
                            <div class="progress progress__quiz">
                                <div class="progress-status"></div>
                            </div>
                            <p class="progress__lable">quiz</p>
                        </div>
                        <div class="progress-item">
                            <div class="progress progress__homework">
                                <div class="progress-status"></div>
                            </div>
                            <p class="progress__lable">h-work</p>
                        </div>
                        <div class="progress-item">
                            <div class="progress progress--borderRight progress__finished">
                                <div class="progress-status"></div>
                            </div>
                            <p class="progress__lable">finished</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="contentPage">
                <div class="category">Category:
                    [#if courseCompulsory.getCourseDetail()?? && courseCompulsory.getCourseDetail().getCategory()??]
                        <span>${courseCompulsory.getCourseDetail().getCategory().getNodeName()!"Category not found"}</span>
                    [/#if]
                </div>

                <div class="category">
                    [#if courseCompulsory.getCourseDetail().getOnline()??]
                        [#if courseCompulsory.getCourseDetail().getOnline() == true]
                            <span>Formality: Online</span>
                            [#else ]
                                <span>Formality: Offline</span>
                        [/#if]
                    [/#if]
                </div>

                <p class="category">${courseCompulsory.getSemester()}</p>
                <div class="main-course">
                    [#assign sessions = courseCompulsory.getSessions()]

                    <div class="course__supervisor">Course supervisor:
                        [#if sessions?has_content && sessions?size > 0]
                            <span>${sessions[0].getSupervisor().getFullName()!"Supervisor's name not found"}</span>
                        [#else ]
                            <span>None</span>
                        [/#if]
                    </div>

                    <div class="course__trainer">Course trainer:
                        [#if sessions?has_content && sessions?size > 0]
                            <span>${sessions[0].getTrainer().getFullName()!"Trainer's name not found"}</span>
                        [#else ]
                            <span>None</span>
                        [/#if]
                    </div>
                </div>
                <div class="classes">Prerequisite classes:
                    [#assign prerequisitesCourses = model.getListPrerequisiteClassesOfCourseCompulsory()!""]
                    [#if prerequisitesCourses?has_content && prerequisitesCourses?size > 0]
                        [#list prerequisitesCourses as course]
                            <a href="${course.getReadMoreLink()!"uuid not found"}"
                               target="_blank">${course.getCourseDetail().getNodeName()!"Course not found"}</a>

                            [#if course?index  < (prerequisitesCourses?size -1) ]
                                ,&nbsp;
                            [/#if]

                        [/#list]
                    [/#if]
                </div>
                <div class="section-info">
                    <div class="section__lable">DESCRIPTION</div>
                    <div class="section__description">${courseCompulsory.getCourseDetail().getDescription()!"Course description is empty"}</div>
                </div>
                <div class="section-info">
                    <div class="section__lable">COURSE OUTLINE</div>
                    <div class="section__listoutline">
                        ${courseCompulsory.getCourseDetail().getOutline()!"Outline is empty"}
                    </div>
                </div>
                [#if courseCompulsory.getCourseDetail().getMaterialTitle()??
                || courseCompulsory.getCourseDetail().getMaterialDescription()??
                || courseCompulsory.getCourseDetail().getMaterialLinks()?size > 0]

                    <div class="section-info">
                        <div class="section__lable">MATERIAL</div>
                        [#assign materials = courseCompulsory.getCourseDetail().getMaterialLinks()!]

                        <div class="homework__content">
                            <strong>${courseCompulsory.getCourseDetail().getMaterialTitle()!}</strong></div>

                        <div class="homework__content">${courseCompulsory.getCourseDetail().getMaterialDescription()!}</div>

                        [#if materials?has_content && materials?size > 0]
                            <div class="section__listmaterial">
                                [#list materials as material]
                                    <div class="skill-item">
                                        <a href="${material.getUrl()!'URL not found'}"
                                           target="_blank"><span>&dash;</span>${material.getName()!"Name of link not found"}
                                        </a>
                                    </div>
                                [/#list]
                            </div>
                        [/#if]
                    </div>
                [/#if]

                [#if courseCompulsory.getCourseDetail().getHomeworkTitle()??
                || courseCompulsory.getCourseDetail().getHomeworkDescription()??
                || courseCompulsory.getCourseDetail().getHomeworkLinks()?size > 0]

                    <div class="section-info">
                        <div class="section__lable">Homework</div>
                        [#assign homeworks = courseCompulsory.getCourseDetail().getHomeworkLinks()]
                        <div class="homework__content">
                            <strong>${courseCompulsory.getCourseDetail().getHomeworkTitle()!}</strong></div>

                        <div class="homework__content">${courseCompulsory.getCourseDetail().getHomeworkDescription()!}</div>
                        [#if homeworks?has_content && homeworks?size > 0]
                            [#list homeworks as homework]
                                <div class="download">
                                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/download.png"
                                         alt="download">
                                    <a href="${homework.getLink().getLink()!"Link not found"}"
                                       target="_blank">${homework.getFileName()!"Homework title not found"}</a>
                                </div>
                            [/#list]
                        [/#if]
                    </div>

                [/#if]
                <div class="comment">
                    <div class="comment__count evn-row justify-content-center">
                        <div class="comment__count__item evn-row align-items-center">
                            <div class="comment__count__figure">
                                <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-black-comment.png" alt="" class="comment__icon">
                            </div>
                            <div class="comment__count__number" id = "js-comment-count-number">${totalCommentList}</div>
                        </div>
                    </div>
                    <hr class="comment__divide">
                    <div class="comment__flow">

                        <div class="comment__main-thread js-comment-main-thread">
                            <div class="comment__main-input evn-row js-comment-main-input">
                                [#if userProfile.headerThumbnail?has_content]
                                <div class="comment__photo"><img src="${userProfile.headerThumbnail}" alt="" class="comment__big-photo"></div>
                                [#else]
                                <div class="comment__photo"><img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/avatar.png" alt="" class="comment__big-photo"></div>
                                [/#if]
                                <textarea class="comment__text-area js-comment-text-area" placeholder="Write a comment…" contenteditable type="comment__text-area"></textarea>
                            </div>
                            <div class="comment__main-btn js-comment-main-btn">
                                <div class="comment__btn-group evn-row"><button class="evn-btn evn-btn--comment js-btn-main-cancel">CANCEL</button><button class="evn-btn evn-btn--comment js-btn-main-send">SEND</button></div>
                            </div>
                        </div>

                        <div class="comment__post js-root-comment-list">
                            [#list commentList as comment]
                                <div class="comment__post__item js-comment-post-item">
                                    <input style="display: none" class="js-id-root-comment" value="${comment.uuid}">
                                    <div class="evn-row">
                                        [#if comment.user.headerThumbnail??]
                                        <div class="comment__photo"><img src="${(comment.user.headerThumbnail.link)}" alt="" class="comment__big-photo"></div>
                                        [#else]
                                        <div class="comment__photo"><img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/avatar.png" alt="" class="comment__big-photo"></div>
                                        [/#if]
                                        <div class="comment__data">
                                            <div class="comment__name">${comment.user.fullName!""}</div>
                                            <div class="comment__time">${comment.commentTime!""}</div>
                                        </div>
                                    </div>
                                    <div class="comment__text">
                                        <div class="comment__text__data">${comment.comment!""}</div>
                                        <div class="comment__data__group evn-row js-comment-data-group">
                                            <div class="comment__count__group evn-row js-comment-count">
                                                <div class="comment__count__figure">
                                                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-comment.png" alt="" class="comment__icon">
                                                </div>
                                                <div class="comment__text__count comment__text__count--is-positive js-comment-reply-text-area">${comment.getCommentList() ? size}</div>
                                            </div>
                                            <div class="btn-comment-reply js-btn-comment-reply">REPLY</div>
                                        </div>
                                        <div class="js-list-reply-comment">
                                            [#list comment.getCommentList() as itm]
                                                <div class="comment__post__list js-comment-post-list">
                                                    <div class="comment__post__item js-sub-comment-post-item">
                                                        <div class="evn-row">
                                                            [#if itm.user.headerThumbnail??]
                                                                <div class="comment__photo"><img src="${itm.user.headerThumbnail.link}" alt="" class="comment__big-photo"></div>
                                                            [#else]
                                                                <div class="comment__photo"><img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/avatar.png" alt="" class="comment__big-photo"></div>
                                                            [/#if]
                                                            <div class="comment__data">
                                                                <div class="comment__name">${itm.user.fullName!""}</div>
                                                                <div class="comment__time">${itm.commentTime!""}</div>
                                                            </div>
                                                        </div>
                                                        <div class="comment__text js-comment-text">
                                                            <div class="comment__text__data">${itm.comment!""}</div>
                                                        </div>
                                                        [#--                                                        <div class="comment__data__group evn-row js-comment-data-group">--]
                                                        [#--                                                            <div class="comment__count__group evn-row">--]
                                                        [#--                                                                <div class="comment__count__figure">--]
                                                        [#--                                                                    <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/icon-comment.png" alt="" class="comment__icon">--]
                                                        [#--                                                                </div>--]
                                                        [#--                                                                <div class="comment__text__count">${itm.getCommentList() ? size}</div>--]
                                                        [#--                                                            </div>--]
                                                        [#--                                                            --][#-- <div class="btn-comment-reply js-btn-comment-reply">REPLY</div>--]
                                                        [#--                                                        </div>--]
                                                    </div>
                                                </div>

                                            [/#list]
                                        </div>
                                    </div>

                                </div>
                            [/#list]
                        </div>
                    </div>

                </div>
            </div>
        </div>
        </div>
    [#else ]
        <h2>Course Not Found!</h2>
    [/#if]
[#else ]
    ${ctx.response.sendRedirect("/login")!}
[/#if]
